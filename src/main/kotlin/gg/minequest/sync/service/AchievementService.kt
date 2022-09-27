package gg.minequest.sync.service

import gg.minequest.sync.model.Achievement
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service

/**
 * @author GrowlyX
 * @since 9/27/2022
 */
@Service
object AchievementService
{
    private val registered = mutableListOf<Achievement>()

    @Configure
    fun configure()
    {
        registered += Achievement(
            "horse", "Horse Driving", listOf()
        )
    }
}
