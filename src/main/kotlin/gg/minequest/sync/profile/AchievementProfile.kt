package gg.minequest.sync.profile

import gg.minequest.sync.model.Achievement
import gg.minequest.sync.model.goals.AchievementGoalDataCollector
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

    fun hasCollectedStage(achievement: Achievement, stage: Int) =
        this.collectedStages.contains("${achievement.id}-$stage")

    fun save() = DataStoreObjectControllerCache
        .findNotNull<AchievementProfile>()
        .save(this, DataStoreStorageType.MONGO)
}
