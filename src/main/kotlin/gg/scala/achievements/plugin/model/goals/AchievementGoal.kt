package gg.scala.achievements.plugin.model.goals

import gg.scala.achievements.plugin.profile.AchievementProfile

/**
 * @author GrowlyX
 * @since 9/27/2022
 */
interface AchievementGoal<T>
{
    /**
     * This ID should be unique. It's the only identifier
     * used for mapping [AchievementGoalDataCollector]s.
     */
    val id: String

    val requirement: T
    val description: String

    fun createDataCollector(profile: AchievementProfile): AchievementGoalDataCollector<T>
}
