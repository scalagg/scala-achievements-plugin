package gg.scala.achievements.plugin.model.goals

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

/**
 * Since data collectors can, and will probably be plugin-specific, we
 * need a way to keep original model data without losing it
 * we fail to unwrap it into a Java class.
 *
 * This serializer also pushes new collector properties if the model has
 * been unwrapped (assuming updates have been made).
 *
 * @author GrowlyX
 * @since 7/4/2023
 */
object ReferenceDataCollectorSerializer : JsonSerializer<ReferenceDataCollector>
{
    override fun serialize(
        collectorRef: ReferenceDataCollector,
        type: Type,
        context: JsonSerializationContext
    ): JsonElement
    {
        val unwrapped = collectorRef
            .unwrapped<AchievementGoalDataCollector<*>>()

        val json = JsonObject()
        json.addProperty("clazz", collectorRef.clazz)
        json.add(
            "properties",
            if (unwrapped == null)
            {
                collectorRef.properties
            } else
            {
                context
                    .serialize(
                        unwrapped,
                        // Since the unwrapped model is not null, we assume the backing
                        // type is also non-null.
                        collectorRef.backingType!!
                    )
            }
        )

        return json
    }
}
