package gg.scala.achievements.plugin.service

import gg.scala.achievements.plugin.model.Achievement
import gg.scala.achievements.plugin.model.AchievementReward
import gg.scala.achievements.plugin.model.AchievementStage
import gg.scala.achievements.plugin.model.goals.AchievementGoal
import gg.scala.achievements.plugin.model.goals.AchievementGoalDataCollector
import gg.scala.achievements.plugin.model.goals.IntegerDataCollector
import gg.scala.achievements.plugin.model.goals.TtlIntegerDataCollector
import gg.scala.achievements.plugin.profile.AchievementProfile
import gg.scala.achievements.plugin.profile.AchievementProfileOrchestrator
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import me.lucko.helper.Events
import net.evilblock.cubed.util.CC
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.inventory.ItemStack

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
