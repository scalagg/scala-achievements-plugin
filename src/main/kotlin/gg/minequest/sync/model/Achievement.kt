package gg.minequest.sync.model

import gg.minequest.sync.profile.AchievementProfile

/**
 * @author GrowlyX
 * @since 9/27/2022
 */
data class Achievement(
    val id: String, val name: String,
    val stages: List<AchievementStage>
)
{
    fun completedAllStages(profile: AchievementProfile) =
        this.stages.all {
            it.metAllGoalRequirements(profile)
        }
}
