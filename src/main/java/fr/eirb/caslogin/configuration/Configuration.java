package fr.eirb.caslogin.configuration;

import org.bukkit.configuration.file.FileConfiguration;

public record Configuration(FileConfiguration defaultConfig, FileConfiguration adminConfig) {
}
