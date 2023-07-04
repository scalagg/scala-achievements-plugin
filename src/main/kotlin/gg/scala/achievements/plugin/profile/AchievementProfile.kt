package gg.scala.achievements.plugin.profile

import gg.scala.achievements.plugin.model.Achievement
import gg.scala.achievements.plugin.model.goals.AchievementGoalDataCollector
import gg.scala.achievements.plugin.model.goals.ReferenceDataCollector
import gg.scala.achievements.plugin.model.goals.ttl.TTLRespectingGoalDataCollector
import gg.scala.achievements.plugin.service.AchievementService
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.store.storage.type.DataStoreStorageType
import net.evilblock.cubed.serializers.Serializers
import java.util.*

/**
 * @author GrowlyX
 * @since 8/21/2022
 */
data class AchievementProfile(
    override val identifier: UUID,
) : IDataStoreObject
{
    val dataCollectors = mutableMapOf<String, ReferenceDataCollector>()
    private val collectedStages = mutableListOf<String>()

    fun dataCollectors() = dataCollectors
        .mapValues {
            it.value.unwrapped<AchievementGoalDataCollector<*>>()
        }

    fun ttlDataCollectors() = dataCollectors()
        .filterValues {
            it is TTLRespectingGoalDataCollector<*>
        }

    inline fun <reified T : AchievementGoalDataCollector<*>> dataCollector(id: String) =
        this.dataCollectors[id]!!.unwrapped<T>()

    fun hasCollectedStage(achievement: Achievement, stage: Int) =
        this.collectedStages.contains("${achievement.id}-$stage")

    fun setCollectedStage(achievement: Achievement, stage: Int) =
        this.collectedStages.add("${achievement.id}-$stage")

    fun removeCollectedStage(key: String) =
        this.collectedStages.remove(key)

    fun save() = DataStoreObjectControllerCache
        .findNotNull<AchievementProfile>()
        .save(this, DataStoreStorageType.MONGO)

    fun ensureDataCollectorsAreHealthy(): Boolean
    {
        var profileSaveRequired = false

        ttlDataCollectors()
            .forEach { dataCollector ->
                val ttlDataCollector =
                    dataCollector.value as TTLRespectingGoalDataCollector<*>
                val result = ttlDataCollector
                    .ttlRefresh(
                        profile = this, bulk = true,
                        key = dataCollector.key
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
                        val collector = goal.createDataCollector(this)

                        this.dataCollectors[goal.id] =
                            ReferenceDataCollector(
                                properties = Serializers.gson
                                    .toJsonTree(collector)
                                    .asJsonObject,
                                clazz = (collector.getAbstractType() as Class<*>).name
                            )
                    }
                }
            }
    }
}
