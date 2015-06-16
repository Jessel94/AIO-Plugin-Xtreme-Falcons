package com.collinsrichard.easywarp;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLoader;

public class Settings
{
  public static int delay = 0;
  public static boolean perWarpPerms = false;
  public static boolean signsReqPerms = false;
  public static boolean signsPerWarpPerms = false;
  public static boolean permsBypassDelay = false;
  public static boolean opsBypassDelay = false;
  public static boolean warpOtherBypassDelay = false;
  public static boolean signsBypassDelay = false;
  public static boolean canOverwrite = false;
  public static String prefix = "&3[&6EasyWarp&3]";
  
  public static void load(EasyWarp plugin)
  {
    loadSettings(plugin);
  }
  
  public static void loadSettings(Plugin plugin)
  {
    try
    {
      prefix = plugin.getConfig().getString("server-name");
      delay = plugin.getConfig().getInt("warp-delay");
      perWarpPerms = plugin.getConfig().getBoolean("per-warp-permissions");
      signsReqPerms = plugin.getConfig().getBoolean("signs-require-permissions");
      signsPerWarpPerms = plugin.getConfig().getBoolean("signs-per-warp-permissions");
      permsBypassDelay = plugin.getConfig().getBoolean("permissions-bypass-delay");
      opsBypassDelay = plugin.getConfig().getBoolean("ops-bypass-delay");
      warpOtherBypassDelay = plugin.getConfig().getBoolean("warp-other-bypass-delay");
      signsBypassDelay = plugin.getConfig().getBoolean("signs-bypass-delay");
      canOverwrite = plugin.getConfig().getBoolean("allow-warp-overwrite");
    }
    catch (Exception e)
    {
      plugin.getLogger().log(Level.SEVERE, "Error loading config: disabling.");
      plugin.getPluginLoader().disablePlugin(plugin);
    }
  }
  
  public static List<String> getMessage(String search)
  {
    Plugin plugin = Helper.getPlugin();
    
    List<String> toReturn = plugin.getConfig().getStringList("messages." + search);
    if ((toReturn == null) || (toReturn.isEmpty()))
    {
      toReturn = new ArrayList();
      toReturn.add(plugin.getConfig().getString("messages." + search));
    }
    return toReturn;
  }
}
