package gg.scala.achievements.plugin.model.goals

import gg.scala.achievements.plugin.model.goals.ttl.TTLRespectingGoalDataCollector
import java.time.Duration

/**
 * @author GrowlyX
 * @since 9/27/2022
 */
class IntegerDataCollector(
    override val goalId: String
) : AchievementGoalDataCollector<Int>
{
    override var value = 0

    override fun getAbstractType() =
        IntegerDataCollector::class.java
}

class TtlIntegerDataCollector(
    override val goalId: String,
    override var lastTtlHeartbeat: Long =
        System.currentTimeMillis()
) : TTLRespectingGoalDataCollector<Int>
{
    override fun ttl() = Duration.ofSeconds(10L)!!
    override fun defaultValueOf() = 0

    override var value = 0

    override fun getAbstractType() =
        TtlIntegerDataCollector::class.java
}
