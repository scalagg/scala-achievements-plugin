package gg.scala.achievements.plugin.commands

import gg.scala.achievements.plugin.menu.AchievementViewMenu
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer

/**
 * @author GrowlyX
 * @since 9/27/2022
 */
@AutoRegister
object AchievementsCommand : ScalaCommand()
{
    @CommandAlias("achievement|achievements|achieve")
    fun onAchievements(player: ScalaPlayer)
    {
        AchievementViewMenu().openMenu(player.bukkit())
    }
}
