package me.cactusffa.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class ColorUtil {

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    private ColorUtil() {
    }

    public static Component component(String text) {
        return SERIALIZER.deserialize(text == null ? "" : text);
    }

    public static String text(String input) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(component(input));
    }
}
