package gg.scala.achievements.plugin.model

/**
 * @author GrowlyX
 * @since 5/12/2023
 */
enum class AchievementBrandType(
    val singular: String,
    val plural: String = "${singular}s",
    val filter: (gg.scala.achievements.plugin.model.Achievement) -> Boolean = { true }
)
{
    Achievement(singular = "Achievement"),
    Challenge(singular = "Challenge")
}
