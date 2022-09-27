package gg.minequest.sync.profile

import gg.minequest.sync.ScalaAchievementsPlugin
import gg.minequest.sync.model.goals.AchievementGoalDataCollector
import gg.scala.commons.persist.ProfileOrchestrator
import gg.scala.flavor.inject.Inject
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
    @Inject
    lateinit var plugin: ScalaAchievementsPlugin

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
    }

    override fun new(uniqueId: UUID) = AchievementProfile(uniqueId)
    override fun type() = AchievementProfile::class
}
