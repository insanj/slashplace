package com.insanj.slashplace.schematic;

import java.util.UUID;
import org.bukkit.Location;

public class SlashPlaceSchematicPlace {
   public final SlashPlaceSchematic schematic;
   public final Location location;
   public final UUID playerUUID;
   public SlashPlaceSchematic invertedSchematic;

    public SlashPlaceSchematicPlace(SlashPlaceSchematic schematic, Location location, UUID playerUUID) {
        this.schematic = schematic;
        this.location = location;
        this.playerUUID = playerUUID;
    }

    @Override
    public String toString() {
        return String.format("SlashPlaceSchematicPlace %s, schematic=%s, location=%s, playerUUID=%s, invertedSchematic=%s", super.toString(), this.schematic, this.location, this.playerUUID, this.invertedSchematic);
    }
}
