package gg.scala.achievements.plugin.service

import gg.scala.achievements.plugin.model.Achievement
import gg.scala.flavor.service.Service

/**
 * @author GrowlyX
 * @since 9/27/2022
 */
@Service
object AchievementService
{
    private val registered = mutableListOf<Achievement>()

    fun achievements() = registered

    fun registerAchievement(achievement: Achievement)
    {
        if (achievement.stages.isEmpty())
            throw IllegalArgumentException(
                "Cannot register achievement with no stages!"
            )

        registered += achievement
    }
}
