package com.github.jsh32.astroisles.common

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.spongepowered.configurate.serialize.ScalarSerializer
import org.spongepowered.configurate.serialize.TypeSerializerCollection
import java.lang.reflect.Type
import java.util.function.Predicate


class ComponentSerializer : ScalarSerializer<Component>(Component::class.java) {
    override fun deserialize(type: Type, obj: Any): Component {
        return MiniMessage.miniMessage().deserialize(obj.toString().ifEmpty { "<red>Missing string</red>" })
    }

    override fun serialize(item: Component?, typeSupported: Predicate<Class<*>?>?): Any {
        val component = item ?: Component.text("Missing string").color(NamedTextColor.RED)
        return MiniMessage.miniMessage().serialize(component)
    }

    companion object {
        val instance = ComponentSerializer()
    }
}

/**
 * Standard minecraft serializers which should be in each config loader.
 */
val minecraftSerializers: (TypeSerializerCollection.Builder) -> Unit = {
    it.register(ComponentSerializer.instance)
}