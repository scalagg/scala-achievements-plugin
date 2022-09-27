package gg.scala.achievements.plugin.model

import gg.scala.achievements.plugin.model.goals.AchievementGoal
import gg.scala.achievements.plugin.profile.AchievementProfile

/**
 * @author GrowlyX
 * @since 9/27/2022
 */
class AchievementStage(
    val description: List<String>,
    val goals: List<AchievementGoal<*>>,
    val rewards: List<AchievementReward>
)
{
    inline fun <reified T> getGoalByType() =
        this.goals.filterIsInstance<T>().first()

    inline fun <reified T> getGoalById(id: Int) =
        this.goals[id] as T

    fun getGoalById(id: Int) =
        this.goals[id]

    fun metAllGoalRequirements(profile: AchievementProfile) =
        this.goals.all {
            profile.dataCollectors.entries
                .find { collector ->
                    collector.key == it.id
                }
                ?.value == it.requirement
        }
}
