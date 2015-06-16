package com.useful.ucars;

import com.useful.ucarsCommon.IdMaterialConverter;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class PlaceManager
{
  @Deprecated
  public static Boolean placeableOn(int id, byte data)
  {
    String materialName = IdMaterialConverter.getMaterialById(id).name().toUpperCase();
    return placeableOn(materialName, data);
  }
  
  public static Boolean placeableOn(String materialName, byte data)
  {
    Boolean placeable = Boolean.valueOf(false);
    if (!ucars.config.getBoolean("general.cars.roadBlocks.enable")) {
      return Boolean.valueOf(true);
    }
    List<String> rBlocks = ucars.config
      .getStringList("general.cars.roadBlocks.ids");
    for (String raw : rBlocks) {
      if (ItemStackFromId.equals(raw, materialName, data).booleanValue()) {
        placeable = Boolean.valueOf(true);
      }
    }
    return placeable;
  }
}
