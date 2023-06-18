package pl.profmc.betterautomessages;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class AutoMessage extends JavaPlugin {
    private List<String> messages;
    private int currentIndex;
    private int delay;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();

        new UpdateChecker(this, 110559).getVersion(version -> {
                    if (this.getDescription().getVersion().equals(version)) {
                        getLogger().warning("There is not a new update available.");
                    } else {
                        getLogger().warning("There is a new update available! Your currently versiom:" + this.getDescription().getVersion() + "New version:" + version);
                        getLogger().warning("Download from https://www.spigotmc.org/resources/betterautomessages.110559");
                    }
                });

        // Tworzenie asynchronicznego taska, który będzie wysyłał wiadomości co określony czas


        Bukkit.getScheduler().runTaskTimer(this, bukkitTask -> {
            String message = getNextMessage();
            Bukkit.broadcastMessage(message);
        }, 0L, delay * 20L);
    }

    private void loadConfig() {
        FileConfiguration config = getConfig();
        messages = config.getStringList("messages");
        delay = config.getInt("delay");

        // Jeśli brak wiadomości w konfiguracji, zatrzymaj plugin
        if (messages.isEmpty()) {
            getLogger().warning("There is no messages in config file.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private String getNextMessage() {
        String message = messages.get(currentIndex);
        currentIndex++;

        // Jeśli osiągnięto koniec listy, wróć na początek
        if (currentIndex >= messages.size()) {
            currentIndex = 0;
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
