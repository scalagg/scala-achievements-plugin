package gg.scala.achievements.plugin.model.goals.ttl

import gg.scala.achievements.plugin.model.goals.AchievementGoalDataCollector
import gg.scala.achievements.plugin.profile.AchievementProfile
import java.time.Duration

/**
 * @author GrowlyX
 * @since 9/27/2022
 */
interface TTLRespectingGoalDataCollector<T> : AchievementGoalDataCollector<T>
{
    var lastTtlHeartbeat: Long

    fun ttl(): Duration
    fun defaultValueOf(): T

    fun ttlRefresh(
        profile: AchievementProfile,
        bulk: Boolean = false
    ): Boolean
    {
        if (timeUntilNextRefreshMillis() >= 0)
        {
            return false
        }

        lastTtlHeartbeat = System.currentTimeMillis()
        value = defaultValueOf()

        if (!bulk)
        {
            profile.save()
        }

        return true
    }

    fun nextExpectedTtlRefreshMillis() = lastTtlHeartbeat + ttl().toMillis()
    fun timeUntilNextRefreshMillis() = nextExpectedTtlRefreshMillis() - System.currentTimeMillis()
}
