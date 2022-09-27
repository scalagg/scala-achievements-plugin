package gg.scala.achievements.plugin.menu

import com.cryptomorin.xseries.XMaterial
import gg.scala.achievements.plugin.model.Achievement
import gg.scala.achievements.plugin.numeric.RomanNumerals
import gg.scala.achievements.plugin.profile.AchievementProfile
import gg.scala.achievements.plugin.profile.AchievementProfileOrchestrator
import gg.scala.achievements.plugin.service.AchievementService
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 9/27/2022
 */
class AchievementViewMenu : PaginatedMenu()
{
    init
    {
        updateAfterClick = true
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()

        val profile = AchievementProfileOrchestrator
            .find(player) ?: return buttons

        AchievementService.achievements()
            .forEach {
                buttons[buttons.size] = AchievementButton(it, profile)
            }

        return buttons
    }

    override fun getPrePaginatedTitle(player: Player) = "Achievements"

    data class AchievementButton(
        private val achievement: Achievement,
        private val profile: AchievementProfile
    ) : Button()
    {
        private val determinedStage = achievement.stages
            .firstOrNull {
                !achievement.collectedAllRewards(profile, it) || !it.metAllGoalRequirements(profile)
            }
            // We're expecting the player to be on their last
            // stage if they don't pass any of these checks.
            ?: achievement.stages.last()

        private val stageIndex = achievement.stages
            .indexOf(determinedStage)

        private val collectedRewards = achievement
            .collectedAllRewards(profile, determinedStage)

        private val metGoalReqs = determinedStage
            .metAllGoalRequirements(profile)

        private val requiresAwardCollection = !collectedRewards && metGoalReqs
        private val requiresLiterallyNothing = collectedRewards && metGoalReqs

        override fun getButtonItem(player: Player): ItemStack
        {
            val description = determinedStage
                .description.toMutableList()

            description += ""
            description += "${CC.GOLD}Goals:"

            for (goal in determinedStage.goals)
            {
                val dataCollector = profile.dataCollectors[goal.id]
                    ?: continue

                description += "${CC.WHITE} - ${goal.description} ${CC.GRAY}(${
                    "${CC.GOLD}${dataCollector.value}${CC.GRAY}/${CC.GOLD}${goal.requirement}${CC.GRAY}"
                })"
            }

            description += ""
            description += "${CC.GOLD}Rewards:"

            for (reward in determinedStage.rewards)
            {
                description += "${CC.WHITE} - ${reward.description}"
            }

            description += ""
            description += if (requiresLiterallyNothing)
            {
                "${CC.GREEN}You've completed this achievement!"
            } else if (requiresAwardCollection)
            {
                "${CC.PINK}Click to collect rewards!"
            } else
            {
                "${CC.YELLOW}You're working on this achievement."
            }

            return ItemBuilder
                .of(
                    if (requiresLiterallyNothing)
                        XMaterial.LIME_DYE
                    else if (requiresAwardCollection)
                        XMaterial.PINK_DYE
                    else
                        XMaterial.GRAY_DYE
                )
                .name(
                    "${CC.B_GOLD}${achievement.name}${
                        if (achievement.stages.size == 1) "" else ": ${
                            RomanNumerals.toRoman(stageIndex + 1)
                        }"
                    }"
                )
                .setLore(description)
                .build()
        }

        override fun clicked(
            player: Player, slot: Int,
            clickType: ClickType, view: InventoryView
        )
        {
            if (requiresAwardCollection)
            {
                for (reward in determinedStage.rewards)
                {
                    reward.applyTo(player)
                }

                profile.setCollectedStage(achievement, stageIndex)
                profile.save()
            }
        }
    }
}
