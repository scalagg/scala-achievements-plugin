package gg.scala.achievements.plugin.profile

import gg.scala.achievements.plugin.model.Achievement
import gg.scala.achievements.plugin.model.goals.AchievementGoalDataCollector
import gg.scala.achievements.plugin.model.goals.ttl.TTLRespectingGoalDataCollector
import gg.scala.achievements.plugin.service.AchievementService
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.store.storage.type.DataStoreStorageType
import java.util.*

/**
 * @author GrowlyX
 * @since 8/21/2022
 */
data class AchievementProfile(
    override val identifier: UUID,
) : IDataStoreObject
{
    val dataCollectors = mutableMapOf<String, AchievementGoalDataCollector<*>>()
    private val collectedStages = mutableListOf<String>()

    fun ttlDataCollectors() = dataCollectors.values
        .filterIsInstance<TTLRespectingGoalDataCollector<*>>()

    inline fun <reified T : AchievementGoalDataCollector<*>> dataCollector(id: String) =
        this.dataCollectors[id] as T

    fun hasCollectedStage(achievement: Achievement, stage: Int) =
        this.collectedStages.contains("${achievement.id}-$stage")

    fun setCollectedStage(achievement: Achievement, stage: Int) =
        this.collectedStages.add("${achievement.id}-$stage")

    fun save() = DataStoreObjectControllerCache
        .findNotNull<AchievementProfile>()
        .save(this, DataStoreStorageType.MONGO)

    fun ensureTtlValidities(): Boolean
    {
        var profileSaveRequired = false

        ttlDataCollectors()
            .forEach { ttlDataCollector ->
                val result = ttlDataCollector
                    .ttlRefresh(
                        profile = this, bulk = true
                    )

                if (!profileSaveRequired && result)
                {
                    profileSaveRequired = true
                }
            }

        return profileSaveRequired
    }

    fun populateDataCollectors()
    {
        AchievementService.achievements()
            .forEach {
                val goals = it.stages
                    .flatMap { stage -> stage.goals }

                for (goal in goals)
                {
                    val collector = this
                        .dataCollectors[goal.id]

                    if (collector == null)
                    {
                        this.dataCollectors[goal.id] =
                            goal.createDataCollector(this)
                    }
                }
            }
    }
}
