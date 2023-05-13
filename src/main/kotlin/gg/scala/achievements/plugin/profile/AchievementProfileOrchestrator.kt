package gg.scala.achievements.plugin.profile

import gg.scala.achievements.plugin.model.goals.AchievementGoalDataCollector
import gg.scala.commons.persist.ProfileOrchestrator
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializer
import java.util.*

/**
 * @author GrowlyX
 * @since 8/21/2022
 */
@Service
object AchievementProfileOrchestrator : ProfileOrchestrator<AchievementProfile>()
{
    @Configure
    fun configure()
    {
        Serializers.create {
            registerTypeAdapter(
                AchievementGoalDataCollector::class.java,
                AbstractTypeSerializer<AchievementGoalDataCollector<*>>()
            )
        }
    }

    override fun postLoad(uniqueId: UUID)
    {
        val profile = this.find(uniqueId)
            ?: return

        profile.populateDataCollectors()
        if (profile.ensureTtlValidities())
        {
            profile.save()
        }
    }

    override fun new(uniqueId: UUID) = AchievementProfile(uniqueId)
    override fun type() = AchievementProfile::class
}
