package gg.scala.achievements.plugin.model.goals.ttl

import gg.scala.achievements.plugin.model.goals.AchievementGoalDataCollector
import gg.scala.achievements.plugin.profile.AchievementProfile
import org.joda.time.DateTime
import org.joda.time.Period
import org.joda.time.PeriodType

/**
 * @author GrowlyX
 * @since 9/27/2022
 */
interface TTLRespectingGoalDataCollector<T> : AchievementGoalDataCollector<T>
{
    var lastTtlHeartbeat: Long

    fun defaultValueOf(): T
    fun nextExpectedTtlRefresh(): DateTime

    fun ttlRefresh(
        profile: AchievementProfile,
        bulk: Boolean = false,
        key: String
    ): Boolean
    {
        if (nextExpectedTtlRefresh().isAfterNow)
        {
            return false
        }

        lastTtlHeartbeat = DateTime.now().millis
        value = defaultValueOf()

        profile.removeCollectedStage(key)

        if (!bulk)
        {
            profile.save()
        }

        return true
    }

    fun timeUntilNextRefreshMillis() =
        Period(
            DateTime.now(),
            nextExpectedTtlRefresh(),
            PeriodType.millis()
        ).millis
}
