package gg.scala.achievements.plugin.model.goals

import com.google.gson.JsonObject
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.util.Reflection

/**
 * A reference class which wraps model data, allowing platforms
 * without the original model [clazz] loaded at runtime
 * to deserialize other, available, reference items without issues arising.
 *
 * @author GrowlyX
 * @since 7/4/2023
 */
class ReferenceDataCollector(
    val properties: JsonObject,
    val clazz: String
)
{
    var backingInstance: Any? = null
    var backingType: Class<*>? = null
    var unwrapChecked: Boolean? = null

    inline fun <reified T> unwrapped(): T?
    {
        if (unwrapChecked != null)
            return if (backingInstance == null)
                null else backingInstance as T?

        val clazz = Reflection
            .getClassSuppressed(clazz)
            ?: return let {
                unwrapChecked = true
                backingInstance = null
                null
            }

        backingType = clazz

        val unwrapped = Serializers
            .gson
            .fromJson(properties, clazz)
            ?: return let {
                unwrapChecked = true
                backingInstance = null
                null
            }

        unwrapChecked = true
        backingInstance = unwrapped
        return unwrapped as T
    }
}
