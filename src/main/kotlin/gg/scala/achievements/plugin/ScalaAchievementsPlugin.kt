package gg.scala.achievements.plugin

import gg.scala.achievements.plugin.model.AchievementBrandType
import gg.scala.achievements.plugin.model.GameMode
import gg.scala.achievements.plugin.model.goals.ReferenceDataCollector
import gg.scala.achievements.plugin.model.goals.ReferenceDataCollectorSerializer
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.annotations.container.ContainerEnable
import net.evilblock.cubed.serializers.Serializers

/**
 * @author GrowlyX
 * @since 9/27/2022
 */

@Plugin(
    name = "Achievements",
    version = "%remote%/%branch%/%id%"
)

@PluginAuthor("Tropic")
@PluginWebsite("https://tropic.gg")

@PluginDependency("scala-commons")
@PluginDependency("cloudsync", soft = true)
class ScalaAchievementsPlugin : ExtendedScalaPlugin()
{
    companion object
    {
        var branding = AchievementBrandType.Achievement
        var gamemode: GameMode? = null
    }

    @ContainerEnable
    fun containerEnable()
    {
        Serializers.create {
            registerTypeAdapter(
                ReferenceDataCollector::class.java,
                ReferenceDataCollectorSerializer
            )
        }
    }
}
