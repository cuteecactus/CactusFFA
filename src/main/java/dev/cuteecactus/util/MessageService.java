package dev.cuteecactus.util;

import dev.cuteecactus.CactusFFAPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;

public final class MessageService {

    private final CactusFFAPlugin plugin;
    private YamlConfiguration messages;

    public MessageService(CactusFFAPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        this.messages = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml"));
    }

    public void send(CommandSender sender, String path) {
        send(sender, path, Map.of());
    }

    public void send(CommandSender sender, String path, Map<String, String> replacements) {
        String message = messages.getString(path, path);
        String prefix = messages.getString("prefix", "");
        message = message.replace("%prefix%", prefix);
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            message = message.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        sender.sendMessage(ColorUtil.component(message));
    }

    public String get(String path) {
        String prefix = messages.getString("prefix", "");
        return messages.getString(path, "").replace("%prefix%", prefix);
    }
}
