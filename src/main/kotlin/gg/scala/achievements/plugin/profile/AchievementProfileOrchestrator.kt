package gg.scala.achievements.plugin.profile

import gg.scala.commons.persist.ProfileOrchestrator
import gg.scala.flavor.service.Service
import java.util.*

/**
 * @author GrowlyX
 * @since 8/21/2022
 */
@Service
object AchievementProfileOrchestrator : ProfileOrchestrator<AchievementProfile>()
{
    override fun postLoad(uniqueId: UUID)
    {
        val profile = this.find(uniqueId)
            ?: return

        profile.populateDataCollectors()
        if (profile.ensureDataCollectorsAreHealthy())
        {
            profile.save()
        }
    }

    override fun new(uniqueId: UUID) = AchievementProfile(uniqueId)
    override fun type() = AchievementProfile::class
}
