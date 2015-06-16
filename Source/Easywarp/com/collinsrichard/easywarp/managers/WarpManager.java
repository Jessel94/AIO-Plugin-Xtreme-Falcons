package com.collinsrichard.easywarp.managers;

import com.collinsrichard.easywarp.Settings;
import com.collinsrichard.easywarp.objects.Warp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class WarpManager
{
  private static HashMap<String, Warp> warps = new HashMap();
  
  public static void addWarp(String name, Location location)
  {
    if (!isWarp(name)) {
      warps.put(name.toLowerCase(), new Warp(name, location));
    }
  }
  
  public static void addWarp(Warp warp)
  {
    if ((warp != null) && (!isWarp(warp.getName()))) {
      warps.put(warp.getName().toLowerCase(), warp);
    }
  }
  
  public static Warp getWarp(String name)
  {
    if (isWarp(name)) {
      return (Warp)warps.get(name.toLowerCase());
    }
    return null;
  }
  
  public static void removeWarp(Warp warp)
  {
    if (isWarp(warp)) {
      warps.remove(warp.getName().toLowerCase());
    }
  }
  
  public static boolean isWarp(String name)
  {
    return warps.containsKey(name.toLowerCase());
  }
  
  public static boolean isWarp(Warp warp)
  {
    return isWarp(warp.getName().toLowerCase());
  }
  
  public static Warp deserialize(String warp)
  {
    String[] args = warp.split(",");
    
    String name = args[0];
    String worldS = args[1];
    String xS = args[2];
    String yS = args[3];
    String zS = args[4];
    String pitchS = args[5];
    String yawS = args[6];
    try
    {
      World world = Bukkit.getWorld(worldS);
      double x = Double.parseDouble(xS);
      double y = Double.parseDouble(yS);
      double z = Double.parseDouble(zS);
      Float pitch = Float.valueOf(Float.parseFloat(pitchS));
      Float yaw = Float.valueOf(Float.parseFloat(yawS));
      
      Location loc = new Location(world, x, y, z, yaw.floatValue(), pitch.floatValue());
      
      return new Warp(name, loc);
    }
    catch (Exception e)
    {
      Bukkit.getLogger().log(Level.WARNING, "Error parsing warp '" + name + "'");
    }
    return null;
  }
  
  public static ArrayList<String> getWarpNames()
  {
    ArrayList<String> ret = new ArrayList();
    for (String s : warps.keySet()) {
      ret.add(s);
    }
    return ret;
  }
  
  public static ArrayList<Warp> getWarpObjects()
  {
    ArrayList<Warp> ret = new ArrayList();
    
    Iterator it = warps.entrySet().iterator();
    while (it.hasNext())
    {
      Map.Entry<String, Warp> entry = (Map.Entry)it.next();
      ret.add(entry.getValue());
    }
    return ret;
  }
  
  public static ArrayList<String> getAvailable(CommandSender s)
  {
    if ((!Settings.perWarpPerms) || (s.hasPermission("easywarp.warp.*"))) {
      return getWarpNames();
    }
    ArrayList<String> ret = new ArrayList();
    for (String warp : getWarpNames()) {
      if (s.hasPermission("easywarp.warp." + warp.toLowerCase())) {
        ret.add(warp);
      }
    }
    return ret;
  }
}
