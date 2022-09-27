package gg.minequest.sync.profile

import gg.minequest.sync.model.Achievement
import gg.minequest.sync.model.AchievementStage
import gg.minequest.sync.model.goals.AchievementGoalDataCollector
import gg.minequest.sync.service.AchievementService
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
    val collectedStages = mutableListOf<String>()

    inline fun <reified T : AchievementGoalDataCollector<*>> dataCollector(id: String) =
        this.dataCollectors[id] as T

    fun hasCollectedStage(achievement: Achievement, stage: Int) =
        this.collectedStages.contains("${achievement.id}-$stage")

    fun save() = DataStoreObjectControllerCache
        .findNotNull<AchievementProfile>()
        .save(this, DataStoreStorageType.MONGO)

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
