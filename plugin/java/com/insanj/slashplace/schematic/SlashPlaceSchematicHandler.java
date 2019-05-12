package com.insanj.slashplace.schematic;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.bukkit.block.BlockState;

import org.apache.commons.io.FilenameUtils;

import net.minecraft.server.v1_13_R2.NBTTagCompound;
import net.minecraft.server.v1_13_R2.NBTCompressedStreamTools;

public class SlashPlaceSchematicHandler {
    public final SlashPlacePlugin plugin;

    public SlashPlaceSchematicHandler(SlashPlacePlugin plugin) {
        this.plugin = plugin;
    }

    public SlashPlaceSchematic readSchematicFile(String name, File file) {
        try {
            FileInputStream stream = new FileInputStream(file);
            NBTTagCompound nbtdata = NBTCompressedStreamTools.a(stream);

            short width = nbtdata.getShort("Width");
            short height = nbtdata.getShort("Height");
            short length = nbtdata.getShort("Length");

            byte[] blocks = nbtdata.getByteArray("Blocks");
            byte[] data = nbtdata.getByteArray("Data");

            byte[] addId = new byte[0];

            if (nbtdata.hasKey("AddBlocks")) {
                addId = nbtdata.getByteArray("AddBlocks");
            }

            short[] sblocks = new short[blocks.length];
            for (int index = 0; index < blocks.length; index++) {
                if ((index >> 1) >= addId.length) {
                    sblocks[index] = (short) (blocks[index] & 0xFF);
                } else {
                    if ((index & 1) == 0) {
                        sblocks[index] = (short) (((addId[index >> 1] & 0x0F) << 8) + (blocks[index] & 0xFF));
                    } else {
                        sblocks[index] = (short) (((addId[index >> 1] & 0xF0) << 4) + (blocks[index] & 0xFF));
                    }
                }
            }

            stream.close();

            String prettyName = FilenameUtils.getBaseName(name);
            return new SlashPlaceSchematic(prettyName, sblocks, data, width, length, height);
        } catch (Exception e) {
            plugin.logError(e);
        }

        return null;
    }

    public void pasteSchematic(World world, Location loc, SlashPlaceSchematic schematic) {
        short[] blocks = schematic.blocks;
        byte[] blockData = schematic.data;

        short length = schematic.length;
        short width = schematic.width;
        short height = schematic.height;

        int delay = 0;

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                for (int z = 0; z < length; ++z) {
                    int index = y * width * length + z * width + x;
                    Location l = new Location(loc.getWorld(), x + loc.getX(), y + loc.getY(), z + loc.getZ());
                    Block block = l.getBlock();

                    // ??
                    // int b = blocks[index] & 0xFF; <-- make the block unsigned, so that blocks with an id over 127, like quartz and emerald, can be pasted

                    int b = (int)blocks[index];
                    Material material = convertMaterial(b, blockData[index]);
                    if (material == null) {
                      // incompatible material!!
                      plugin.logError(new Exception("Unable to convert Material bytes from schematic file! Block integer: " + Integer.toString(b)));
                      continue;
                    }

                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                      public void run() {
                        block.setType(material);
   
                        BlockState bs = block.getState();
                        bs.setRawData(blockData[index]);
                        bs.update(true);
                      }
                    }, delay++);

                }
            }
        }
    }

    public Material convertMaterial(int legacyMaterialId, byte legacyDataByte) {
        for (Material material : EnumSet.allOf(Material.class)) {
            if (material.getId() == legacyMaterialId) {
                return Bukkit.getUnsafe().fromLegacy(new MaterialData(material, legacyDataByte));
            }
        }
        return null;
    }
}
