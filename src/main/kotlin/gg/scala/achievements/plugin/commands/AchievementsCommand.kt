package gg.scala.achievements.plugin.commands

import gg.scala.achievements.plugin.menu.AchievementMainMenu
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
    @CommandAlias("achievement|achievements|achieve|quests|challenges")
    fun onAchievements(player: ScalaPlayer)
    {
        AchievementMainMenu().openMenu(player.bukkit())
    }
}
