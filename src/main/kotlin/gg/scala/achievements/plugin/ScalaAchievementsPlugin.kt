package gg.scala.achievements.plugin

import gg.scala.commons.ExtendedScalaPlugin
import me.lucko.helper.plugin.ap.Plugin
import me.lucko.helper.plugin.ap.PluginDependency

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
