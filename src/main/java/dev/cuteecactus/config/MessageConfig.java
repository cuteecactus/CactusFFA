package dev.cuteecactus.config;

import org.bukkit.configuration.file.FileConfiguration;

import dev.cuteecactus.utils.ColorUtil;
import net.kyori.adventure.text.Component;

public class MessageConfig {
    private FileConfiguration config;

    private static MessageConfig instance;

    public MessageConfig() {
        this.config = ConfigManager.get().load("messages");
        instance = this;
    }

    public static MessageConfig get() {
        return instance;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void reload() {
        this.config = ConfigManager.get().load("messages");
    }

    public boolean save(FileConfiguration config) {
        return ConfigManager.get().save(config, "messages");
    }

    public Component getMessage(String path, String... replacements) {
        String prefix = config.getString("prefix", "&8[&aCactusFFA&8] ");
        String message = config.getString(path, path);

        message = prefix + message;
        for (int i = 0; i < replacements.length - 1; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }

        return ColorUtil.color(message);
    }
}
