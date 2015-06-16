package org.goblom.gui.plugin;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.goblom.gui.plugin.api.API;
import org.goblom.gui.plugin.api.GuiAPI;
import org.goblom.gui.plugin.api.SimpleGUI;
import org.goblom.gui.plugin.commands.GUICommand;
import org.goblom.gui.plugin.commands.GUICreateCommand;
import org.goblom.gui.plugin.commands.GUIReloadCommand;
import org.goblom.gui.plugin.handler.InventoryGUI;
import org.goblom.gui.plugin.listeners.ChestConverter;
import org.goblom.gui.plugin.listeners.DevListener;
import org.goblom.gui.plugin.listeners.OpenWithItem;
import org.goblom.gui.plugin.listeners.SignListener;
import org.goblom.gui.plugin.util.connect.BungeeCord;
import org.goblom.gui.plugin.util.connect.LilyPad;

public class SimpleGuiCreator
  extends JavaPlugin
  implements SimpleGUI
{
  public static InventoryGUI gui;
  private static boolean debug;
  public static Economy econ;
  public static boolean econ_enabled;
  
  public void onEnable()
  {
    saveResources();
    
    getUpdater();
    
    debug = getConfig().getBoolean("Debug-Mode");
    
    gui = new InventoryGUI(this);
    load();
    
    new SignListener(this);
    new ChestConverter(this);
    new DevListener(this);
    new OpenWithItem(this);
    
    getCommand("gui").setExecutor(new GUICommand());
    getCommand("guireload").setExecutor(new GUIReloadCommand());
    getCommand("guicreate").setExecutor(new GUICreateCommand());
    
    econ_enabled = setupEconomy();
    try
    {
      MetricsLite metrics = new MetricsLite(this);
      metrics.start();
    }
    catch (IOException e)
    {
      getLogger().warning("Unable to Start metrics. Stats will not be collected this time. :(");
    }
  }
  
  public void load()
  {
    gui.purge();
    gui.getGUIFiles();
    gui.loadGUIs();
    gui.loadSlots();
  }
  
  private void saveResources()
  {
    saveDefaultConfig();
    saveResource("gui-example.yml", false);
  }
  
  public static boolean isDebug()
  {
    return debug;
  }
  
  private boolean setupEconomy()
  {
    if (getServer().getPluginManager().getPlugin("Vault") == null)
    {
      getLogger().severe("Vault Not Found");
      return false;
    }
    RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
    if (rsp == null)
    {
      getLogger().severe("Unable to find an economy plugin");
      return false;
    }
    econ = (Economy)rsp.getProvider();
    return econ != null;
  }
  
  public boolean checkLilypad()
  {
    if (getServer().getPluginManager().getPlugin("LilyPad-Connect") == null)
    {
      getLogger().warning("Not Loading LilyPad Support. Action Aborted.");
      return false;
    }
    return getLilyPad().getConnect();
  }
  
  public BungeeCord getBungeeCord()
  {
    return new BungeeCord();
  }
  
  public LilyPad getLilyPad()
  {
    return new LilyPad();
  }
  
  private void getUpdater()
  {
    Updater update;
    if (getConfig().getBoolean("Auto-Update")) {
      update = new Updater(this, 62334, getFile(), Updater.UpdateType.DEFAULT, true);
    }
  }
  
  private final Map<String, API> apiHandler = new HashMap();
  
  public boolean removePlugin(Plugin plugin)
  {
    if (this.apiHandler.containsKey(plugin.getName()))
    {
      ((API)this.apiHandler.get(plugin.getName())).unRegister();
      this.apiHandler.remove(plugin.getName());
      return true;
    }
    return false;
  }
  
  public GuiAPI getAPI(Plugin plugin)
  {
    if (this.apiHandler.containsKey(plugin.getName())) {
      return (GuiAPI)this.apiHandler.get(plugin.getName());
    }
    return (GuiAPI)this.apiHandler.put(plugin.getName(), new API(plugin));
  }
  
  public List<String> getDevelopers()
  {
    return Arrays.asList(new String[] { "Goblom", "efreak1996" });
  }
}
