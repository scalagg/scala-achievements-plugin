package gg.scala.achievements.plugin.model.goals

import gg.scala.achievements.plugin.profile.AchievementProfile
import gg.scala.achievements.plugin.service.AchievementService
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializable

/**
 * @author GrowlyX
 * @since 9/27/2022
 */
interface AchievementGoalDataCollector<T> : AbstractTypeSerializable
{
    val goalId: String

    /**
     * Please use the [updateValue] function below
     * rather than using the built-in Kotlin setter.
     */
    var value: T

    fun usesAlternativeDisplayType() = false
    fun parseAlternativeDisplayType(requirement: Any?) = ""

    /**
     * Unfortunately, we require all this logic to prevent data
     * collectors from being added onto when a player's achievement isn't at the proper stage.
     *
     * Please call this asynchronously if run a lot.
     */
    fun updateValue(newValue: T, profile: AchievementProfile)
    {
        val achievementPairs = AchievementService
            .achievements()
            .map {
                it to it.stages.firstOrNull { stage ->
                    stage.getGoalByIdString(goalId) != null
                }
            }

        val achievementPair = achievementPairs
            .firstOrNull {
                it.second != null
            }
            ?: return

        val achievement = achievementPair.first

        val determinedStage = achievement.stages
            .firstOrNull {
                (achievement.rewardsPlayer && !achievement.collectedRewardsFor(profile, it)) || !it.metAllGoalRequirements(profile)
            }
            ?: achievement.stages.last()

        if (
            achievement.stages.indexOf(determinedStage) !=
            achievement.stages.indexOf(achievementPair.second)
        )
        {
            return
        }

        val collectedRewards = achievement
            .collectedRewardsFor(profile, determinedStage)

        val metGoalReqs = determinedStage
            .metAllGoalRequirements(profile)

        if (!achievement.rewardsPlayer)
        {
            if (!metGoalReqs)
            {
                this.value = newValue
                profile.save()
            }

            return
        }

        val requiresAwardCollection = !collectedRewards && metGoalReqs
        val requiresLiterallyNothing = collectedRewards && metGoalReqs

        if (
            !requiresLiterallyNothing && !requiresAwardCollection
        )
        {
            this.value = newValue
            profile.save()
        }
    }
}
