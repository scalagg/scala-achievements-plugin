package gg.scala.achievements.plugin.model.goals

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
