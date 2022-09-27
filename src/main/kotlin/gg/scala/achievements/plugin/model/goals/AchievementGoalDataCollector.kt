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

    /**
     * Unfortunately, we require all this logic to prevent data
     * collectors from being added onto when a player's achievement isn't at the proper stage.
     *
     * Please call this asynchronously if run a lot.
     */
    fun updateValue(newValue: T, profile: AchievementProfile)
    {
        val achievement = AchievementService
            .achievements()
            .firstOrNull {
                it.stages.any { stage -> stage.getGoalByIdString(goalId) != null }
            }
            ?: return

        val determinedStage = achievement.stages
            .firstOrNull {
                !achievement.collectedRewardsFor(profile, it)
            }
            ?: achievement.stages.last()

        val collectedRewards = achievement
            .collectedRewardsFor(profile, determinedStage)

        val metGoalReqs = determinedStage
            .metAllGoalRequirements(profile)

        val requiresAwardCollection = !collectedRewards && metGoalReqs
        val requiresLiterallyNothing = collectedRewards && metGoalReqs

        if (!requiresLiterallyNothing && !requiresAwardCollection)
        {
            this.value = newValue
        }
    }
}
