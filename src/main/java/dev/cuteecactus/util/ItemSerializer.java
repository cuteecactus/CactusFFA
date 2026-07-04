package dev.cuteecactus.util;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public final class ItemSerializer {

    private static final String FORMAT_HEADER = "CACTUSFFA_ITEMSTACKS_V2";

    private ItemSerializer() {
    }

    public static ItemStack[] decodeItems(String base64) {
        if (base64 == null || base64.isBlank()) {
            return new ItemStack[0];
        }

        ItemStack[] standard = decodeStandard(base64);
        if (standard != null) {
            return standard;
        }
        return decodeLegacy(base64);
    }

    public static String encodeItems(ItemStack[] items) {
        ItemStack[] source = items == null ? new ItemStack[0] : items;
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream output = new BukkitObjectOutputStream(byteStream)) {
            output.writeUTF(FORMAT_HEADER);
            output.writeInt(source.length);
            for (ItemStack itemStack : source) {
                output.writeObject(itemStack);
            }
            output.flush();
            return Base64.getEncoder().encodeToString(byteStream.toByteArray());
        } catch (IOException exception) {
            return "";
        }
    }

    private static ItemStack[] decodeStandard(String base64) {
        try (BukkitObjectInputStream input = new BukkitObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(base64)))) {
            String header = input.readUTF();
            if (!FORMAT_HEADER.equals(header)) {
                return null;
            }
            int size = input.readInt();
            if (size < 0 || size > 256) {
                return null;
            }
            ItemStack[] items = new ItemStack[size];
            for (int i = 0; i < size; i++) {
                Object value = input.readObject();
                items[i] = value instanceof ItemStack itemStack ? itemStack : null;
            }
            return items;
        } catch (IOException | ClassNotFoundException exception) {
            return null;
        }
    }

    private static ItemStack[] decodeLegacy(String base64) {
        try (BukkitObjectInputStream input = new BukkitObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(base64)))) {
            List<ItemStack> collected = new ArrayList<>();
            while (true) {
                try {
                    Object object = input.readObject();
                    ItemStack[] parsed = toItems(object);
                    for (ItemStack itemStack : parsed) {
                        if (itemStack != null) {
                            collected.add(itemStack);
                        }
                    }
                } catch (EOFException ignored) {
                    break;
                }
            }
            return collected.toArray(new ItemStack[0]);
        } catch (IOException | ClassNotFoundException exception) {
            return new ItemStack[0];
        }
    }

    @SuppressWarnings("unchecked")
    private static ItemStack[] toItems(Object object) {
        object = unwrap(object);
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
        object = unwrap(object);
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

    private static Object unwrap(Object object) {
        if (object == null) {
            return null;
        }
        Class<?> type = object.getClass();
        if (!"org.bukkit.util.io.Wrapper".equals(type.getName())) {
            return object;
        }
        try {
            java.lang.reflect.Field mapField = type.getDeclaredField("map");
            mapField.setAccessible(true);
            return mapField.get(object);
        } catch (ReflectiveOperationException exception) {
            return object;
        }
    }
}
