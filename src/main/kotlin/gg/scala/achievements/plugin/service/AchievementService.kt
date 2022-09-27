package gg.scala.achievements.plugin.service

import gg.scala.achievements.plugin.model.Achievement
import gg.scala.achievements.plugin.model.AchievementReward
import gg.scala.achievements.plugin.model.AchievementStage
import gg.scala.achievements.plugin.model.goals.AchievementGoal
import gg.scala.achievements.plugin.model.goals.AchievementGoalDataCollector
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

    class IntegerDataCollector : AchievementGoalDataCollector<Int>
    {
        override var value = 0

        override fun getAbstractType() =
            IntegerDataCollector::class.java
    }

    @Configure
    fun configure()
    {
        // testing
        Events.subscribe(PlayerGameModeChangeEvent::class.java)
            .handler {
                val profile = AchievementProfileOrchestrator
                    .find(it.player)
                    ?: return@handler

                profile
                    .dataCollector<IntegerDataCollector>("horses_driven")
                    .updateValue(1)
            }

        registerAchievement(
            Achievement(
                "horse", "Horse Driving", linkedSetOf(
                    AchievementStage(
                        listOf("${CC.GRAY}Drive a single horse!"),
                        listOf(
                            object : AchievementGoal<Int>
                            {
                                override val id = "horses_driven"
                                override val requirement = 1
                                override val description = "Driven horses"

                                override fun createDataCollector(profile: AchievementProfile): AchievementGoalDataCollector<Int>
                                {
                                    return IntegerDataCollector()
                                }
                            }
                        ),
                        listOf(
                            object : AchievementReward
                            {
                                override val description = "1 wool"

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

        registered += achievement
    }
}
