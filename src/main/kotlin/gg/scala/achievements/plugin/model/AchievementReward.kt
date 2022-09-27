package gg.scala.achievements.plugin.model

import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 9/27/2022
 */
interface AchievementReward
{
    val description: List<String>

    fun applyTo(player: Player)
}
