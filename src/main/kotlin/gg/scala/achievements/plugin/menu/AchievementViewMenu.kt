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
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Bukkit
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
            .filter(Achievement::visibleWithinMenu)
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
                (achievement.rewardsPlayer && !achievement.collectedRewardsFor(profile, it)) || !it.metAllGoalRequirements(profile)
            }
            // We're expecting the player to be on their last
            // stage if they don't pass any of these checks.
            ?: achievement.stages.last()

        private val stageIndex = achievement.stages
            .indexOf(determinedStage)

        private val collectedRewards = achievement
            .collectedRewardsFor(profile, determinedStage)

        private val metGoalReqs = determinedStage
            .metAllGoalRequirements(profile)

        private val requiresAwardCollection = (!achievement.rewardsPlayer || !collectedRewards) && metGoalReqs
        private val requiresLiterallyNothing = (!achievement.rewardsPlayer || collectedRewards) && metGoalReqs

        override fun getButtonItem(player: Player): ItemStack
        {
            val description = determinedStage
                .description.toMutableList()

            description += ""
            description += "${CC.PRI}Goals:"

            for (goal in determinedStage.goals)
            {
                val dataCollector = profile.dataCollectors[goal.id]
                    ?: continue

                description += "${CC.WHITE} - ${goal.description} ${CC.GRAY}(${
                    if (dataCollector.usesAlternativeDisplayType())
                        dataCollector.parseAlternativeDisplayType(goal.requirement)
                    else
                        "${dataCollector.value}/${goal.requirement}"
                })"
            }

            if (determinedStage.rewards.isNotEmpty())
            {
                description += ""
                description += "${CC.PRI}Rewards:"

                for (reward in determinedStage.rewards)
                {
                    description += "${CC.WHITE} - ${reward.description}"
                }
            }

            description += ""
            description += if (requiresLiterallyNothing)
            {
                "${CC.GREEN}You've completed this achievement!"
            } else if (requiresAwardCollection && achievement.rewardsPlayer)
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
                    else if (requiresAwardCollection && achievement.rewardsPlayer)
                        XMaterial.PINK_DYE
                    else
                        XMaterial.GRAY_DYE
                )
                .name(
                    "${CC.B_PRI}${achievement.name}${
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
            if (achievement.rewardsPlayer && requiresAwardCollection)
            {
                for (reward in determinedStage.rewards)
                {
                    reward.applyTo(player)
                }

                profile.setCollectedStage(achievement, stageIndex)
                profile.save()

                player.sendMessage(
                    "${CC.B_PRI}Achievements ${CC.B_GRAY}${Constants.DOUBLE_ARROW_RIGHT} ${CC.GREEN}You've collected rewards for the ${CC.SEC}${achievement.name}${CC.GREEN} achievement!"
                )
            }
        }
    }
}
