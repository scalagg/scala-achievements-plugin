package gg.scala.achievements.plugin.model

import gg.scala.achievements.plugin.profile.AchievementProfile

/**
 * @author GrowlyX
 * @since 9/27/2022
 */
data class Achievement(
    val id: String, val name: String,
    val stages: List<AchievementStage>
)
{
    fun metAllGoalRequirementsAndCollected(profile: AchievementProfile, stage: AchievementStage) =
        stage.metAllGoalRequirements(profile) && stage.goals
            .toMutableList()
            .all {
                profile.hasCollectedStage(this, stage.goals.indexOf(it))
            }

    fun completedAllStages(profile: AchievementProfile) =
        this.stages.all {
            it.metAllGoalRequirements(profile)
        }
}
