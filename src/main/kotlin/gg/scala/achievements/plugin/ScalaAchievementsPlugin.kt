package gg.scala.achievements.plugin

import gg.scala.achievements.plugin.model.AchievementBrandType
import gg.scala.achievements.plugin.model.GameMode
import gg.scala.achievements.plugin.model.goals.ReferenceDataCollector
import gg.scala.achievements.plugin.model.goals.ReferenceDataCollectorSerializer
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.annotations.container.ContainerEnable
import me.lucko.helper.plugin.ap.Plugin
import me.lucko.helper.plugin.ap.PluginDependency
import net.evilblock.cubed.serializers.Serializers

/**
 * @author GrowlyX
 * @since 9/27/2022
 */
@Plugin(
    name = "Achievements",
    depends = [
        PluginDependency("scala-commons"),
        PluginDependency("store-spigot"),
        PluginDependency("cloudsync", soft = true)
    ]
)
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
