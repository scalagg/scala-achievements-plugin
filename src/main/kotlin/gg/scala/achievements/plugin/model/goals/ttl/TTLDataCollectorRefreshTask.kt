package gg.scala.achievements.plugin.model.goals.ttl

import gg.scala.achievements.plugin.profile.AchievementProfileOrchestrator
import gg.scala.commons.annotations.runnables.Repeating
import me.lucko.helper.promise.ThreadContext
import me.lucko.helper.utils.Players

/**
 * @author GrowlyX
 * @since 5/12/2023
 */
@Repeating(20L, context = ThreadContext.ASYNC)
object TTLDataCollectorRefreshTask : Runnable
{
    override fun run()
    {
        Players.all()
            .forEach {
                val profile = AchievementProfileOrchestrator
                    .find(it)
                    ?: return@forEach

                if (profile.ensureDataCollectorsAreHealthy())
                {
                    profile.save()
                }
            }
    }
}
