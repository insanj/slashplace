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

import com.insanj.slashplace.schematic.SlashPlaceSchematic;
import com.insanj.slashplace.schematic.SlashPlaceSchematicPlace;
import com.insanj.slashplace.schematic.SlashPlaceSchematicHandler;

class SlashPlaceCommandExecutor implements CommandExecutor {
  public final SlashPlacePlugin plugin;

  public SlashPlaceCommandExecutor(SlashPlacePlugin plugin) {
    this.plugin = plugin;
  }

  public boolean hasPermission(CommandSender sender, String cmd) {
    return (sender instanceof Player) && (sender.isOp() || sender.hasPermission(String.format("slashplace.%s", cmd)));
  }

  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length != 1) {
      return false;
    }

    String cmdName = command.getName();
    if (hasPermission(sender, cmdName) == false) {
        sender.sendMessage(ChatColor.RED + "Sorry! You do not have permission to use this slashplace command.");
        return true;
    }

    Player player = (Player) sender;
    if (cmdName.equals("place")) {
      return onPlaceCommand(player, args[0]);
    }

    if (cmdName.equals("placeloc")) {
      return onPlaceLocCommand(player, args[0]);
    }

    if (cmdName.equals("placeundo")) {
      return onPlaceUndoCommand(player, args[0]);
    }

    return false;
  }

  public boolean onPlaceCommand(Player player, String arg) {
    UUID playerUUID = player.getUniqueId();
    HashMap<String, SlashPlaceSchematicPlace> playerPlaces = plugin.places.get(playerUUID);
    if (playerPlaces == null) {
      player.sendMessage(ChatColor.RED + "<slashplace> You must set a location with /placeloc before using /place!");
      return true;
    }

    SlashPlaceSchematicPlace place = playerPlaces.get(arg);
    if (place == null) {
      player.sendMessage(ChatColor.RED + "<slashplace> You must set a location with /placeloc before using /place!");
      return true;
    }

    Location target = place.location; // player.getLocation();
    SlashPlaceSchematic schematic = place.schematic;

    player.sendMessage(ChatColor.BLUE + "<slashplace> Placing " + arg + "...");

    place.invertedSchematic = plugin.handler.pasteSchematic(target.getWorld(), target, schematic);
    playerPlaces.put(arg, place);
    plugin.places.put(playerUUID, playerPlaces);

    player.sendMessage(ChatColor.GREEN + "<slashplace> Done placing schematic!");

    return true;
  }

  public boolean onPlaceLocCommand(Player player, String arg) {
    if (plugin.schematics == null) {
      player.sendMessage(ChatColor.RED + "<slashplace> No schematics found in /plugins/slashplace/");
      return true;
    }

    SlashPlaceSchematic schematic = plugin.schematics.get(arg);
    if (schematic == null) {
      player.sendMessage(ChatColor.RED + "<slashplace> No schematic found with the name: " + arg);
      return true;
    }

    UUID playerUUID = player.getUniqueId();
    Location location = player.getLocation();
    SlashPlaceSchematicPlace place = new SlashPlaceSchematicPlace(schematic, location, playerUUID);

    HashMap<String, SlashPlaceSchematicPlace> playerPlaces = plugin.places.get(playerUUID);
    if (playerPlaces == null) {
      playerPlaces = new HashMap<String, SlashPlaceSchematicPlace>();
    }

    playerPlaces.put(arg, place);
    plugin.places.put(playerUUID, playerPlaces);
    player.sendMessage(ChatColor.GREEN + String.format("<slashplace> Saved your location, ready to place %s!", arg));
    return true;
  }

  public boolean onPlaceUndoCommand(Player player, String arg) {
 if (plugin.schematics == null) {
      player.sendMessage(ChatColor.RED + "<slashplace> No schematics found in /plugins/slashplace/");
      return true;
    }

    SlashPlaceSchematic schematic = plugin.schematics.get(arg);
    if (schematic == null) {
      player.sendMessage(ChatColor.RED + "<slashplace> No schematic found with the name: " + arg);
      return true;
    }

    HashMap<String, SlashPlaceSchematicPlace> playerPlaces = plugin.places.get(player.getUniqueId());
    if (playerPlaces == null) {
      player.sendMessage(ChatColor.RED + "<slashplace> Could not find any previously built schematics for your player UUID");
      return true;
    }

    SlashPlaceSchematicPlace place = playerPlaces.get(arg);
    if (place == null) {
      player.sendMessage(ChatColor.RED + "<slashplace> Could not find any place where you previously built this schematic :(");
      return true;
    }

    if (place.invertedSchematic == null) {
      player.sendMessage(ChatColor.RED + "<slashplace> Found that you ran /placeloc for this schematic, but never ran /place... We cannot undo what wasn't once done?");
      return true;
    }

    player.sendMessage(ChatColor.GREEN + String.format("<slashplace> Removed placed blocks from schematic %s!", arg));
    plugin.handler.pasteSchematic(place.location.getWorld(), place.location, place.invertedSchematic);
    return true;
  }
}
