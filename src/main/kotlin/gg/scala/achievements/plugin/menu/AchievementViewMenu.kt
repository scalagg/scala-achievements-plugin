package gg.scala.achievements.plugin.menu

import com.cryptomorin.xseries.XMaterial
import gg.scala.achievements.plugin.ScalaAchievementsPlugin
import gg.scala.achievements.plugin.model.Achievement
import gg.scala.achievements.plugin.model.AchievementBrandType
import gg.scala.achievements.plugin.model.goals.AchievementGoalDataCollector
import gg.scala.achievements.plugin.model.goals.ttl.TTLRespectingGoalDataCollector
import gg.scala.achievements.plugin.numeric.RomanNumerals
import gg.scala.achievements.plugin.profile.AchievementProfile
import gg.scala.achievements.plugin.profile.AchievementProfileOrchestrator
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.time.TimeUtil
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 9/27/2022
 */
class AchievementViewMenu(
    private val branding: AchievementBrandType =
        ScalaAchievementsPlugin.branding,
    private val achievements: List<Achievement>,
    private val customName: String? = null
) : PaginatedMenu()
{
    init
    {
        updateAfterClick = true
        autoUpdate = true
        autoUpdateInterval = 500L
        placeholdBorders = true
    }

    override fun getAllPagesButtonSlots() = listOf(
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25
    )

    override fun getMaxItemsPerPage(player: Player) = 14

    override fun getAllPagesButtons(player: Player): Map<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()

        val profile = AchievementProfileOrchestrator
            .find(player) ?: return buttons

        achievements
            .filter(Achievement::visibleWithinMenu)
            .filter { branding.filter.invoke(it) }
            .forEach {
                buttons[buttons.size] = AchievementButton(it, profile)
            }

        return buttons
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>?
    {
        val buttons = mutableMapOf<Int, Button>()

        buttons[31] = ItemBuilder
            .of(Material.ARROW)
            .name("${CC.GREEN}Go back")
            .toButton { _, _ ->
                openPreviousMenu(player)
            }

        return buttons
    }

    private fun openPreviousMenu(player: Player)
    {
        AchievementMainMenu().openMenu(player)
    }

    override fun onClose(
        player: Player, manualClose: Boolean
    )
    {
        if (manualClose)
        {
            Tasks.delayed(1L) {
                openPreviousMenu(player)
            }
        }
    }

    override fun getPrePaginatedTitle(player: Player) = customName ?: branding.plural

    override fun size(buttons: Map<Int, Button>) = 36

    inner class AchievementButton(
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
                .description
                .map {
                    "${CC.GRAY}$it"
                }
                .toMutableList()

            var timeUntilNextReset = -1

            description += ""
            description += "${CC.GREEN}Goals:"

            for (goal in determinedStage.goals)
            {
                val dataCollector = profile
                    .dataCollector<AchievementGoalDataCollector<*>>(goal.id)
                    ?: continue

                description += "${CC.WHITE} - ${goal.description} ${CC.GRAY}(${
                    if (dataCollector.usesAlternativeDisplayType())
                        dataCollector.parseAlternativeDisplayType(goal.requirement)
                    else
                        "${dataCollector.value}/${goal.requirement}"
                })"

                if (dataCollector is TTLRespectingGoalDataCollector<*>)
                {
                    timeUntilNextReset = dataCollector
                        .timeUntilNextRefreshMillis()
                }
            }

            if (determinedStage.rewards.isNotEmpty())
            {
                description += ""
                description += "${CC.GREEN}Rewards:"

                for (reward in determinedStage.rewards)
                {
                    description += "${CC.WHITE} - ${reward.description}"
                }
            }

            if (timeUntilNextReset != -1)
            {
                description += ""
                description += "${CC.GREEN}Resets in: ${CC.WHITE}${
                    TimeUtil.formatIntoAbbreviatedString((timeUntilNextReset / 1000))
                }"
            }

            description += ""
            description += if (requiresLiterallyNothing)
            {
                "${CC.GREEN}${branding.singular} completed!"
            } else if (requiresAwardCollection && achievement.rewardsPlayer)
            {
                "${CC.PINK}Click to collect rewards!"
            } else
            {
                "${CC.YELLOW}Completion in-progress..."
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
                    "${CC.B_GREEN}${achievement.name}${
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
                    "${CC.GREEN}You've collected rewards for the ${CC.WHITE}${achievement.name}${CC.GREEN} ${branding.singular.lowercase()}!"
                )
            }
        }
    }
}
