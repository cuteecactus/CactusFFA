package me.cactusffa.util;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public final class ItemSerializer {

    private ItemSerializer() {
    }

    public static ItemStack[] decodeItems(String base64) {
        if (base64 == null || base64.isBlank()) {
            return new ItemStack[0];
        }

        try (BukkitObjectInputStream input = new BukkitObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(base64)))) {
            Object object = input.readObject();
            return toItems(object);
        } catch (IOException | ClassNotFoundException exception) {
            return new ItemStack[0];
        }
    }

    public static String encodeItems(ItemStack[] items) {
        ItemStack[] source = items == null ? new ItemStack[0] : items;
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream output = new BukkitObjectOutputStream(byteStream)) {
            output.writeObject(source);
            output.flush();
            return Base64.getEncoder().encodeToString(byteStream.toByteArray());
        } catch (IOException exception) {
            return "";
        }
    }

    @SuppressWarnings("unchecked")
    private static ItemStack[] toItems(Object object) {
        if (object == null) {
            return new ItemStack[0];
        }
        if (object instanceof ItemStack itemStack) {
            return new ItemStack[]{itemStack};
        }
        if (object instanceof ItemStack[] itemStacks) {
            return itemStacks;
        }
        if (object instanceof List<?> list) {
            List<ItemStack> items = new ArrayList<>();
            for (Object entry : list) {
                ItemStack parsed = deserializeSingle(entry);
                if (parsed != null) {
                    items.add(parsed);
                }
            }
            return items.toArray(new ItemStack[0]);
        }
        if (object instanceof Map<?, ?> map && isSerializedMap((Map<?, ?>) object)) {
            ItemStack single = ItemStack.deserialize((Map<String, Object>) map);
            return single == null ? new ItemStack[0] : new ItemStack[]{single};
        }
        return new ItemStack[0];
    }

    private static boolean isSerializedMap(Map<?, ?> map) {
        return map.containsKey("==") && String.valueOf(map.get("==")).contains("ItemStack");
    }

    @SuppressWarnings("unchecked")
    private static ItemStack deserializeSingle(Object object) {
        if (object instanceof ItemStack itemStack) {
            return itemStack;
        }
        if (object instanceof Map<?, ?> raw) {
            Map<String, Object> map = (Map<String, Object>) raw;
            if (isSerializedMap(map)) {
                return ItemStack.deserialize(map);
            }
            Object deserialized = ConfigurationSerialization.deserializeObject(map);
            if (deserialized instanceof ItemStack itemStack) {
                return itemStack;
            }
        }
        return null;
    }
}
