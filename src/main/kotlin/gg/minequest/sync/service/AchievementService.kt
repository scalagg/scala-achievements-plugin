package gg.minequest.sync.service

import gg.minequest.sync.model.Achievement
import gg.minequest.sync.model.AchievementReward
import gg.minequest.sync.model.AchievementStage
import gg.minequest.sync.model.goals.AchievementGoal
import gg.minequest.sync.model.goals.AchievementGoalDataCollector
import gg.minequest.sync.profile.AchievementProfile
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import net.evilblock.cubed.util.CC
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 9/27/2022
 */
@Service
object AchievementService
{
    private val registered = mutableListOf<Achievement>()

    class IntegerDataCollector : AchievementGoalDataCollector<Int>
    {
        override var value = 0
    }


    fun achievements() = this.registered

    @Configure
    fun configure()
    {
        // test
        registerAchievement(
            Achievement(
                "horse", "Horse Driving", listOf(
                    AchievementStage(
                        listOf("${CC.GRAY}Drive a single horse!"),
                        listOf(
                            object : AchievementGoal<Int>
                            {
                                override val id = "horses_driven"
                                override val requirement = 1
                                override val description = listOf("Driven horses")

                                override fun createDataCollector(profile: AchievementProfile): AchievementGoalDataCollector<Int>
                                {
                                    return IntegerDataCollector()
                                }
                            }
                        ),
                        listOf(
                            object : AchievementReward
                            {
                                override val description = listOf("1 wool")

                                override fun applyTo(player: Player)
                                {
                                    player.inventory.addItem(ItemStack(Material.WOOL))
                                }
                            }
                        )
                    )
                )
            )
        )
    }

    fun registerAchievement(achievement: Achievement)
    {
        if (achievement.stages.isEmpty())
            throw IllegalArgumentException(
                "Cannot register achievement with no stages!"
            )

        this.registered += achievement
    }
}
