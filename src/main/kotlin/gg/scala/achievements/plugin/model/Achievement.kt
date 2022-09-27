package gg.scala.achievements.plugin.model

import gg.scala.achievements.plugin.profile.AchievementProfile

/**
 * @author GrowlyX
 * @since 9/27/2022
 */
data class Achievement(
    val id: String, val name: String,
    val stages: LinkedHashSet<AchievementStage>
)
{
    fun collectedRewardsFor(profile: AchievementProfile, stage: AchievementStage) =
        profile.hasCollectedStage(this, stages.indexOf(stage))

    fun completedAllStages(profile: AchievementProfile) =
        this.stages.all {
            it.metAllGoalRequirements(profile)
        }
}
