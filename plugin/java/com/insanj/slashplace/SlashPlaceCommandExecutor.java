package com.insanj.slashplace;

import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.lang.Math;
import java.io.File;

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

import net.minecraft.server.v1_13_R2.EnumBlockRotation;
import net.minecraft.server.v1_13_R2.DefinedStructure;

import com.insanj.slashplace.schematic.SlashPlaceSchematic
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
        return false;
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
    SlashPlaceSchematicPlace place = plugin.places.get(player.getUUID());
    if (place == null) {
      sender.sendMessage(ChatColor.RED + "You must set a location with /placeloc before using /place!");
      return false;
    }

    Location target = place.location; // player.getLocation();
    sender.sendMessage(ChatColor.BLUE + "Generating " + arg + "...");

    if (schematics == null || schematics.size() <= 0) {
        sender.sendMessage(ChatColor.RED + "No schematics found in /plugins/slashplace/");
        return false;
    }

    String schematicName = argumentString;
    SlashPlaceSchematic schematic = schematics.get(schematicName);
    if (schematic == null) {
        sender.sendMessage(ChatColor.RED + "Could not find schematic with name: " + schematicName);
        return false;
    }

    handler.pasteSchematic(player.getWorld(), target, schematic);
    sender.sendMessage(ChatColor.GREEN + String.format("Done building schematic!"));
    return true;
  }

  public boolean onPlaceLocCommand(Player player, String arg) {

  }

  public boolean onPlaceUndoCommand(Player player, String arg) {

  }
}
