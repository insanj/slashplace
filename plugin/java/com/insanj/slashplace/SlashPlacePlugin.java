package com.insanj.slashplace;

import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.lang.Math;
import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.FluidCollisionMode;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.insanj.slashplace.schematic.SlashPlaceSchematic;
import com.insanj.slashplace.schematic.SlashPlaceSchematicPlace;
import com.insanj.slashplace.schematic.SlashPlaceSchematicHandler;

public class SlashPlacePlugin extends JavaPlugin {
    public SlashPlaceConfig config;
    public SlashPlaceCommandExecutor executor;
    public SlashPlaceSchematicHandler handler;

    public HashMap<String, SlashPlaceSchematic> schematics; // file name (used in commands as well) : schematic
    public HashMap<UUID, HashMap<String, SlashPlaceSchematicPlace>> places; // player uuid : queued schematics (name : schematic rep with targeted location)

    @Override
    public void onEnable() {
      // setup config, which is used to read schematics from disk
      config = new SlashPlaceConfig(this);

      // load schematics into memory (held in the plugin class as a central source of data)
      schematics = readSchematics();
      getLogger().info("-> finished reading .schematics! " + schematics.toString());

      // prepare helper classes; these will be used by the command executor to construct the schematics
      places = new HashMap<UUID, HashMap<String, SlashPlaceSchematicPlace>>();
      handler = new SlashPlaceSchematicHandler(this);

      // register commands which will allow for everything to be used!
      executor = new SlashPlaceCommandExecutor(this);
      getCommand("place").setExecutor(executor); // /place
      getCommand("placeloc").setExecutor(executor); // /placeloc
      getCommand("placeundo").setExecutor(executor); // /placeundo
    }

    private HashMap<String, SlashPlaceSchematic> readSchematics() {
      ArrayList<File> schematicFiles = config.getSchematicFiles();

      // loop thru and parse each NBT/.schematic file
      final String schematicFolderPath = SlashPlaceConfig.getSchematicsFolderPath(this);
      HashMap<String, SlashPlaceSchematic> readSchematics = new HashMap<String, SlashPlaceSchematic>();
      for (File file : schematicFiles) {
          String fileName = file.getName();
          SlashPlaceSchematic readFile = handler.readSchematicFile(fileName, file);
          readSchematics.put(fileName, readFile);
      }

      return readSchematics;
    }

    public void logError(Throwable e) {
      getLogger().warning(String.format("[%s] %s", "slashplace", ExceptionUtils.getStackTrace(e)));
    }
}