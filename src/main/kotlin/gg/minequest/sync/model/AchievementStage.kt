package gg.minequest.sync.model

import gg.minequest.sync.model.goals.AchievementGoal
import gg.minequest.sync.profile.AchievementProfile

/**
 * @author GrowlyX
 * @since 9/27/2022
 */
class AchievementStage(
    val description: List<String>,
    val goals: List<AchievementGoal<*>>
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
