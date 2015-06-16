package com.collinsrichard.easywarp;

import com.collinsrichard.easywarp.commands.DeleteWarpCommand;
import com.collinsrichard.easywarp.commands.EasyWarpCommand;
import com.collinsrichard.easywarp.commands.ListWarpsCommand;
import com.collinsrichard.easywarp.commands.SetWarpCommand;
import com.collinsrichard.easywarp.commands.WarpCommand;
import com.collinsrichard.easywarp.managers.FileManager;
import java.io.IOException;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class EasyWarp
  extends JavaPlugin
{
  public static String name = "";
  
  public void onEnable()
  {
    name = getName();
    
    getServer().getPluginManager().registerEvents(new EWListener(this), this);
    try
    {
      Metrics metrics = new Metrics(this);
      metrics.start();
    }
    catch (IOException localIOException) {}
    saveDefaultConfig();
    reloadConfig();
    Settings.load(this);
    
    FileManager.loadWarps();
    
    getCommand("delwarp").setExecutor(new DeleteWarpCommand());
    getCommand("easywarp").setExecutor(new EasyWarpCommand());
    getCommand("listwarp").setExecutor(new ListWarpsCommand());
    getCommand("setwarp").setExecutor(new SetWarpCommand());
    getCommand("warp").setExecutor(new WarpCommand());
  }
  
  public void onDisable() {}
}
