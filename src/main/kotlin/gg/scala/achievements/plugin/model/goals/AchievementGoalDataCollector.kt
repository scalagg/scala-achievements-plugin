package gg.scala.achievements.plugin.model.goals

import net.evilblock.cubed.serializers.impl.AbstractTypeSerializable

/**
 * @author GrowlyX
 * @since 9/27/2022
 */
interface AchievementGoalDataCollector<T> : AbstractTypeSerializable
{
    var value: T
}
