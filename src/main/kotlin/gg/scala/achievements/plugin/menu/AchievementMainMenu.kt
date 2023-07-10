package gg.scala.achievements.plugin.menu

import com.cryptomorin.xseries.XMaterial
import gg.scala.achievements.plugin.ScalaAchievementsPlugin
import gg.scala.achievements.plugin.model.AchievementCategory
import gg.scala.achievements.plugin.service.AchievementService
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 7/9/2023
 */
class AchievementMainMenu : Menu("Achievements")
{
    override fun getButtons(player: Player): Map<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()
        val gameModeExists = ScalaAchievementsPlugin.gamemode != null

        buttons[if (gameModeExists) 12 else 13] = ItemBuilder
            .of(
                XMaterial.WHITE_DYE
            )
            .name("${CC.WHITE}Network")
            .addToLore(
                "${CC.GRAY}Unlock-able on any game server",
                "${CC.GRAY}on the network.",
                "",
                "${CC.YELLOW}Click to open!",
            )
            .toButton { _, _ ->
                AchievementViewMenu(
                    achievements = AchievementService
                        .achievements()
                        .filter {
                            it.category == AchievementCategory.Global
                        },
                    customName = "Network Achievements"
                ).openMenu(player)
            }

        if (gameModeExists)
        {
            buttons[14] = ItemBuilder
                .of(
                    ScalaAchievementsPlugin.gamemode!!.item
                )
                .name("${CC.GREEN}${
                    ScalaAchievementsPlugin.gamemode!!.name
                }")
                .addToLore(
                    "${CC.GRAY}Unlock-able only on this",
                    "${CC.GRAY}game server type.",
                    "",
                    "${CC.YELLOW}Click to open!",
                )
                .toButton { _, _ ->
                    AchievementViewMenu(
                        achievements = AchievementService
                            .achievements()
                            .filter {
                                it.category == AchievementCategory.GameSpecific
                            },
                        customName = "${ScalaAchievementsPlugin.gamemode!!.name} Achievements"
                    ).openMenu(player)
                }
        }

        return buttons
    }

    override fun size(buttons: Map<Int, Button>) = 27
}
