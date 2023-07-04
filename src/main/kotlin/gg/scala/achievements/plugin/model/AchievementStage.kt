package gg.scala.achievements.plugin.model

import gg.scala.achievements.plugin.model.goals.AchievementGoal
import gg.scala.achievements.plugin.model.goals.AchievementGoalDataCollector
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
    fun getGoalByIdString(id: String) =
        this.goals.firstOrNull { it.id == id }

    fun metAllGoalRequirements(profile: AchievementProfile) =
        this.goals.all {
            val collector = profile.dataCollectors.entries
                .find { collector ->
                    collector.key == it.id
                }
                ?: return@all false

            val thing = collector.value
                .unwrapped<AchievementGoalDataCollector<*>>()
                ?: return@all false

            it.matchesRequirement(thing.value)
        }
}
