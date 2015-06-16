package com.useful.ucars;

import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemStackFromId
{
  public static ItemStack get(String raw)
  {
    String[] parts = raw.split(":");
    String m = parts[0];
    Material mat = Material.getMaterial(m);
    if (mat == null)
    {
      ucars.plugin.getLogger().info("[WARNING] Invalid config value: " + raw + " (" + m + ")");
      return new ItemStack(Material.STONE);
    }
    short data = 0;
    Boolean hasdata = Boolean.valueOf(false);
    if (parts.length > 1)
    {
      hasdata = Boolean.valueOf(true);
      data = Short.parseShort(parts[1]);
    }
    ItemStack item = new ItemStack(mat);
    if (hasdata.booleanValue()) {
      item.setDurability(data);
    }
    return item;
  }
  
  public static Boolean equals(String rawid, String materialName, int tdata)
  {
    String[] parts = rawid.split(":");
    String m = parts[0];
    int data = 0;
    Boolean hasdata = Boolean.valueOf(false);
    if (parts.length > 1)
    {
      hasdata = Boolean.valueOf(true);
      data = Integer.parseInt(parts[1]);
    }
    if (materialName.equalsIgnoreCase(m))
    {
      Boolean valid = Boolean.valueOf(true);
      if ((hasdata.booleanValue()) && 
        (tdata != data)) {
        valid = Boolean.valueOf(false);
      }
      if (valid.booleanValue()) {
        return Boolean.valueOf(true);
      }
    }
    return Boolean.valueOf(false);
  }
  
  public static Boolean equals(List<String> rawids, String materialName, int tdata)
  {
    boolean match = false;
    for (String id : rawids) {
      if ((match) || (equals(id, materialName, tdata).booleanValue())) {
        match = true;
      }
    }
    return Boolean.valueOf(match);
  }
}
