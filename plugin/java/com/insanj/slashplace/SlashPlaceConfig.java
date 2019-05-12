package com.insanj.slashplace;

import java.util.Map;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.bukkit.World;
import org.bukkit.Location;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class SlashPlaceConfig {
    private final SlashPlacePlugin plugin;

    public SlashPlaceConfig(SlashPlacePlugin plugin) {
        this.plugin = plugin;
    }

    public static String getSchematicsFolderPath(SlashPlacePlugin plugin) {
        return plugin.getDataFolder();
    }

    public ArrayList<File> getSchematicFiles() {
        String pluginDataFolderPath = SlashPlaceConfig.getSchematicsFolderPath(plugin);
        File pluginDataFolder = new File(pluginDataFolderPath);
        if (!pluginDataFolder.exists()) {
            pluginDataFolder.mkdir();
        }

        ArrayList<File> schematicFiles = new ArrayList<File>();
        File[] pluginFolderListing = pluginDataFolder.listFiles();
        for (File file : pluginFolderListing) {
            if (file.isFile()) {
                schematicFiles.add(file);
            }
        }
        return schematicFiles;
    }

    public File readFile(String folderPath, String name) {
        if (!name.endsWith(".schematic")) {
            name = name + ".schematic";
        }

        String filePath = folderPath + name;
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }

        return file;
    }
}
