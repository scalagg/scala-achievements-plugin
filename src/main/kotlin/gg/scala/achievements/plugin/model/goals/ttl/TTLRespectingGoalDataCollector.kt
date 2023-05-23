package gg.scala.achievements.plugin.model.goals.ttl

import gg.scala.achievements.plugin.model.goals.AchievementGoalDataCollector
import gg.scala.achievements.plugin.profile.AchievementProfile
import org.joda.time.DateTime
import org.joda.time.Instant
import org.joda.time.LocalDateTime
import org.joda.time.Period
import java.time.LocalDate

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
        if (timeUntilNextRefreshMillis() >= 0)
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
            DateTime.now().millis,
            nextExpectedTtlRefresh().millis
        ).millis
}
