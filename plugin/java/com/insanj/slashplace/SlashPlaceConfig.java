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

import com.insanj.slashplace.schematic.SlashPlaceSchematic;

public class SlashPlaceConfig {
    private final SlashPlacePlugin plugin;

    public SlashPlaceConfig(SlashPlacePlugin plugin) {
        this.plugin = plugin;
    }

    private String getSchematicsFolderPath() {
        return plugin.getDataFolder().toString();
    }

    private ArrayList<File> getSchematicFiles() {
        String pluginDataFolderPath = getSchematicsFolderPath();
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

    private File readFile(String folderPath, String name) {
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

    public HashMap<String, SlashPlaceSchematic> readSchematics() {
      ArrayList<File> schematicFiles = getSchematicFiles();

      // loop thru and parse each NBT/.schematic file
      HashMap<String, SlashPlaceSchematic> readSchematics = new HashMap<String, SlashPlaceSchematic>();
      for (File file : schematicFiles) {
          String fileName = file.getName();
          SlashPlaceSchematic readFile = plugin.handler.readSchematicFile(fileName, file);
          readSchematics.put(fileName, readFile);
      }

      return readSchematics;
    }
}
