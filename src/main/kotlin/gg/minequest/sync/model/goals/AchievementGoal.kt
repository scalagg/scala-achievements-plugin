package gg.minequest.sync.model.goals

import gg.minequest.sync.profile.AchievementProfile

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
    val description: List<String>

    fun createDataCollector(profile: AchievementProfile): AchievementGoalDataCollector<T>
}
