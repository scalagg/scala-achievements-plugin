package gg.scala.achievements.plugin.model

/**
 * @author GrowlyX
 * @since 5/12/2023
 */
enum class AchievementBrandType(
    val singular: String,
    val plural: String = "${singular}s",
)
{
    Achievement(singular = "Achievement"),
    Challenge(singular = "Challenge")
}
