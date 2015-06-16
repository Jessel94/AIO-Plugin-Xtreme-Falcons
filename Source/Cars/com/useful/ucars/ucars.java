package com.useful.ucars;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.useful.uCarsAPI.uCarsAPI;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class ucars
  extends JavaPlugin
{
  public static HashMap<String, Double> carBoosts = new HashMap();
  public static HashMap<String, Double> fuel = new HashMap();
  public static YamlConfiguration lang = new YamlConfiguration();
  public static ucars plugin;
  public static FileConfiguration config;
  public static Boolean vault = Boolean.valueOf(false);
  public static Economy economy = null;
  public static Colors colors;
  public Boolean protocolLib = Boolean.valueOf(false);
  public Object protocolManager = null;
  public ArrayList<ItemStack> ufuelitems = new ArrayList();
  public ListStore licensedPlayers = null;
  public uCarsCommandExecutor cmdExecutor = null;
  public ArrayList<Plugin> hookedPlugins = new ArrayList();
  public Boolean ucarsTrade = Boolean.valueOf(false);
  public static uCarsListener listener = null;
  protected uCarsAPI API = null;
  public static boolean forceRaceControls = false;
  public static boolean smoothDrive = true;
  public static boolean playersIgnoreTrafficLights = false;
  
  public static String colorise(String prefix)
  {
    return ChatColor.translateAlternateColorCodes('&', prefix);
  }
  
  public ListStore getLicensedPlayers()
  {
    return this.licensedPlayers;
  }
  
  public void setLicensedPlayers(ListStore licensed)
  {
    this.licensedPlayers = licensed;
  }
  
  private void copy(InputStream in, File file)
  {
    try
    {
      OutputStream out = new FileOutputStream(file);
      byte[] buf = new byte['Ѐ'];
      int len;
      while ((len = in.read(buf)) > 0)
      {
        int len;
        out.write(buf, 0, len);
      }
      out.close();
      in.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static HashMap<String, Double> loadHashMapDouble(String path)
  {
    try
    {
      ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
        path));
      Object result = ois.readObject();
      ois.close();
      
      return (HashMap)result;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  public static void saveHashMap(HashMap<String, Double> map, String path)
  {
    try
    {
      ObjectOutputStream oos = new ObjectOutputStream(
        new FileOutputStream(path));
      oos.writeObject(map);
      oos.flush();
      oos.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  protected boolean setupEconomy()
  {
    RegisteredServiceProvider<Economy> economyProvider = getServer()
      .getServicesManager().getRegistration(
      Economy.class);
    if (economyProvider != null) {
      economy = (Economy)economyProvider.getProvider();
    }
    return economy != null;
  }
  
  private Boolean setupProtocol()
  {
    try
    {
      this.protocolLib = Boolean.valueOf(true);
      this.protocolManager = ProtocolLibrary.getProtocolManager();
      
      ((ProtocolManager)this.protocolManager).addPacketListener(
        new PacketAdapter(this, new PacketType[] { PacketType.Play.Client.STEER_VEHICLE })
        {
          public void onPacketReceiving(final PacketEvent event)
          {
            PacketContainer packet = event.getPacket();
            final float sideways = ((Float)packet.getFloat().read(0)).floatValue();
            final float forwards = ((Float)packet.getFloat().read(1)).floatValue();
            Bukkit.getScheduler().runTask(ucars.plugin, new Runnable()
            {
              public void run()
              {
                MotionManager.move(event.getPlayer(), forwards, 
                  sideways);
              }
            });
          }
        });
    }
    catch (Exception e)
    {
      return Boolean.valueOf(false);
    }
    return Boolean.valueOf(true);
  }
  
  public void onEnable()
  {
    plugin = this;
    File langFile = new File(getDataFolder().getAbsolutePath() + 
      File.separator + "lang.yml");
    if ((!langFile.exists()) || (langFile.length() < 1L)) {
      try
      {
        langFile.createNewFile();
      }
      catch (IOException localIOException1) {}
    }
    try
    {
      lang.load(langFile);
    }
    catch (Exception e1)
    {
      getLogger().log(Level.WARNING, 
        "Error creating/loading lang file! Regenerating..");
    }
    File configFile = new File(getDataFolder().getAbsolutePath() + 
      File.separator + "config.yml");
    if ((!configFile.exists()) || (configFile.length() < 1L))
    {
      try
      {
        configFile.createNewFile();
      }
      catch (IOException localIOException2) {}
      copy(getResource("ucarsConfigHeader.yml"), configFile);
    }
    try
    {
      config = getConfig();
    }
    catch (Exception e2)
    {
      try
      {
        configFile.createNewFile();
      }
      catch (IOException localIOException3) {}
      copy(getResource("ucarsConfigHeader.yml"), configFile);
    }
    try
    {
      if (!config.contains("general.cars.# description")) {
        config.set("general.cars.# description", 
          "If enabled this will allow for drivable cars(Minecarts not on rails)");
      }
      if (!lang.contains("lang.messages.place")) {
        lang.set("lang.messages.place", 
          "&eYou placed a car! Cars can be driven with similar controls to a horse!");
      }
      if (!lang.contains("lang.error.pluginNull")) {
        lang.set("lang.error.pluginNull", 
          "&4Error in ucars: Caused by: plugin = null? Report on bukkitdev immediately!");
      }
      if (!lang.contains("lang.messages.noDrivePerm")) {
        lang.set("lang.messages.noDrivePerm", 
          "You don't have the permission ucars.cars required to drive a car!");
      }
      if (!lang.contains("lang.messages.noPlacePerm")) {
        lang.set("lang.messages.noPlacePerm", 
          "You don't have the permission %perm% required to place a car!");
      }
      if (!lang.contains("lang.messages.noPlaceHere")) {
        lang.set("lang.messages.noPlaceHere", 
          "&4You are not allowed to place a car here!");
      }
      if (!lang.contains("lang.messages.hitByCar")) {
        lang.set("lang.messages.hitByCar", "You were hit by a car!");
      }
      if (!lang.contains("lang.cars.remove")) {
        lang.set("lang.cars.remove", 
          "&e%amount%&a cars in world &e%world%&a were removed!");
      }
      if (!lang.contains("lang.boosts.already")) {
        lang.set("lang.boosts.already", "&4Already boosting!");
      }
      if (!lang.contains("lang.boosts.low")) {
        lang.set("lang.boosts.low", "Initiated low level boost!");
      }
      if (!lang.contains("lang.boosts.med")) {
        lang.set("lang.boosts.med", "Initiated medium level boost!");
      }
      if (!lang.contains("lang.boosts.high")) {
        lang.set("lang.boosts.high", "Initiated high level boost!");
      }
      if (!lang.contains("lang.fuel.empty")) {
        lang.set("lang.fuel.empty", "You don't have any fuel left!");
      }
      if (!lang.contains("lang.fuel.disabled")) {
        lang.set("lang.fuel.disabled", "Fuel is not enabled!");
      }
      if (!lang.contains("lang.fuel.unit")) {
        lang.set("lang.fuel.unit", "litres");
      }
      if (!lang.contains("lang.fuel.isItem")) {
        lang.set("lang.fuel.isItem", 
          "&9[Important:]&eItem fuel is enabled-The above is irrelevant!");
      }
      if (!lang.contains("lang.fuel.invalidAmount")) {
        lang.set("lang.fuel.invalidAmount", "Amount invalid!");
      }
      if (!lang.contains("lang.fuel.noMoney")) {
        lang.set("lang.fuel.noMoney", "You have no money!");
      }
      if (!lang.contains("lang.fuel.notEnoughMoney")) {
        lang.set("lang.fuel.notEnoughMoney", 
          "That purchase costs %amount% %unit%! You only have %balance% %unit%!");
      }
      if (!lang.contains("lang.fuel.success")) {
        lang.set(
          "lang.fuel.success", 
          "Successfully purchased %quantity% of fuel for %amount% %unit%! You now have %balance% %unit% left!");
      }
      if (!lang.contains("lang.fuel.sellSuccess")) {
        lang.set(
          "lang.fuel.sellSuccess", 
          "Successfully sold %quantity% of fuel for %amount% %unit%! You now have %balance% %unit% left!");
      }
      if (!lang.contains("lang.messages.rightClickWith")) {
        lang.set("lang.messages.rightClickWith", "Right click with ");
      }
      if (!lang.contains("lang.messages.driveOver")) {
        lang.set("lang.messages.driveOver", "Drive over ");
      }
      if (!lang.contains("lang.messages.playersOnly")) {
        lang.set("lang.messages.playersOnly", "Players only!");
      }
      if (!lang.contains("lang.messages.reload")) {
        lang.set("lang.messages.reload", 
          "The config has been reloaded!");
      }
      if (!lang.contains("lang.messages.noProtocolLib")) {
        lang.set(
          "lang.messages.noProtocolLib", 
          "Hello operator, ProtocolLib (http://dev.bukkit.org/bukkit-plugins/protocollib/) was not detected and is required for ucars in MC 1.6 or higher. Please install it if necessary!");
      }
      if (!lang.contains("lang.licenses.next")) {
        lang.set("lang.licenses.next", "Now do %command% to continue!");
      }
      if (!lang.contains("lang.licenses.nocheat")) {
        lang.set("lang.licenses.nocheat", "You need to do all the stages of ulicense to obtain a license! You need to do %command%!");
      }
      if (!lang.contains("lang.licenses.basics")) {
        lang.set(
          "lang.licenses.basics", 
          "A car is just a minecart placed on the ground, not rails. To place a car simply look and the floor while holding a minecart and right click!");
      }
      if (!lang.contains("lang.licenses.controls")) {
        lang.set(
          "lang.licenses.controls", 
          "1) Look where you would like to go. 2) Use the 'w' key to go forward and 's' to go backwards. 3) Use the 'd' key to slow down/brake and the 'a' key to activate any action assgined to the car!");
      }
      if (!lang.contains("lang.licenses.effects")) {
        lang.set(
          "lang.licenses.effects", 
          "Car speed can change depending on what block you may drive over. These can be short term boosts or a speedmod block. Do /ucars for more info on boosts!");
      }
      if (!lang.contains("lang.licenses.itemBoosts")) {
        lang.set(
          "lang.licenses.itemBoosts", 
          "Right clicking with certain items can give you different boosts. Do /ucars for more info!");
      }
      if (!lang.contains("lang.licenses.success")) {
        lang.set("lang.licenses.success", 
          "Congratulations! You can now drive a ucar!");
      }
      if (!lang.contains("lang.licenses.noLicense")) {
        lang.set("lang.licenses.noLicense", 
          "To drive a car you need a license, do /ulicense to obtain one!");
      }
      if (!config.contains("general.cars.enable")) {
        config.set("general.cars.enable", Boolean.valueOf(true));
      } else if (!config.contains("misc.configVersion")) {
        config.set("misc.configVersion", Double.valueOf(1.0D));
      }
      if (!config.contains("misc.configVersion")) {
        config.set("misc.configVersion", Double.valueOf(1.1D));
      }
      if (!config.contains("general.permissions.enable")) {
        config.set("general.permissions.enable", Boolean.valueOf(true));
      }
      if (!config.contains("general.cars.defSpeed")) {
        config.set("general.cars.defSpeed", Double.valueOf(30.0D));
      }
      if (!config.contains("general.cars.smooth")) {
        config.set("general.cars.smooth", Boolean.valueOf(true));
      }
      if (!config.contains("general.cars.effectBlocks.enable")) {
        config.set("general.cars.effectBlocks.enable", Boolean.valueOf(true));
      }
      if (!config.contains("general.cars.lowBoost")) {
        config.set("general.cars.lowBoost", new String[] { "COAL" });
      }
      if (!config.contains("general.cars.medBoost")) {
        config.set("general.cars.medBoost", new String[] { "IRON_INGOT" });
      }
      if (!config.contains("general.cars.highBoost")) {
        config.set("general.cars.highBoost", new String[] { "DIAMOND" });
      }
      if (!config.contains("general.cars.blockBoost")) {
        config.set("general.cars.blockBoost", new String[] { "GOLD_BLOCK" });
      }
      if (!config.contains("general.cars.HighblockBoost")) {
        config.set("general.cars.HighblockBoost", new String[] { "DIAMOND_BLOCK" });
      }
      if (!config.contains("general.cars.ResetblockBoost")) {
        config.set("general.cars.ResetblockBoost", new String[] { "EMERALD_BLOCK" });
      }
      if (!config.contains("general.cars.turret")) {
        config.set("general.cars.turret", null);
      }
      if (!config.contains("general.cars.jumpBlock")) {
        config.set("general.cars.jumpBlock", new String[] { "IRON_BLOCK" });
      }
      if (!config.contains("general.cars.jumpAmount")) {
        config.set("general.cars.jumpAmount", Double.valueOf(30.0D));
      }
      if (!config.contains("general.cars.teleportBlock")) {
        config.set("general.cars.teleportBlock", new String[] { "STAINED_CLAY:2" });
      }
      if (!config.contains("general.cars.trafficLights.enable")) {
        config.set("general.cars.trafficLights.enable", Boolean.valueOf(true));
      }
      if (!config.contains("general.cars.trafficLights.waitingBlock")) {
        config.set("general.cars.trafficLights.waitingBlock", new String[] { "QUARTZ_BLOCK" });
      }
      if (!config.contains("general.cars.hitBy.enable")) {
        config.set("general.cars.hitBy.enable", Boolean.valueOf(false));
      }
      if (!config.contains("general.cars.hitBy.enableMonsterDamage")) {
        config.set("general.cars.hitBy.enableMonsterDamage", Boolean.valueOf(true));
      }
      if (!config.contains("general.cars.hitBy.enableAllMonsterDamage")) {
        config.set("general.cars.hitBy.enableAllMonsterDamage", Boolean.valueOf(true));
      }
      if (!config.contains("general.cars.hitBy.power")) {
        config.set("general.cars.hitBy.power", Double.valueOf(5.0D));
      }
      if (!config.contains("general.cars.hitBy.damage")) {
        config.set("general.cars.hitBy.damage", Double.valueOf(1.5D));
      }
      if (!config.contains("general.cars.roadBlocks.enable")) {
        config.set("general.cars.roadBlocks.enable", Boolean.valueOf(false));
      }
      if (!config.contains("general.cars.roadBlocks.ids")) {
        config.set("general.cars.roadBlocks.ids", new String[] {
          "WOOL:15", "WOOL:8", "WOOL:0", "WOOL:7" });
      }
      if (!config.contains("general.cars.licenses.enable")) {
        config.set("general.cars.licenses.enable", Boolean.valueOf(false));
      }
      if (!config.contains("general.cars.fuel.enable")) {
        config.set("general.cars.fuel.enable", Boolean.valueOf(false));
      }
      if (!config.contains("general.cars.fuel.price")) {
        config.set("general.cars.fuel.price", Double.valueOf(2.0D));
      }
      if (!config.contains("general.cars.fuel.check")) {
        config.set("general.cars.fuel.check", new String[] { "FEATHER" });
      }
      if (!config.contains("general.cars.fuel.cmdPerm")) {
        config.set("general.cars.fuel.cmdPerm", "ucars.ucars");
      }
      if (!config.contains("general.cars.fuel.bypassPerm")) {
        config.set("general.cars.fuel.bypassPerm", "ucars.bypassfuel");
      }
      if (!config.contains("general.cars.fuel.items.enable")) {
        config.set("general.cars.fuel.items.enable", Boolean.valueOf(false));
      }
      if (!config.contains("general.cars.fuel.items.ids")) {
        config.set("general.cars.fuel.items.ids", new String[] {
          "WOOD", "COAL:0", "COAL:1" });
      }
      if (!config.contains("general.cars.fuel.sellFuel")) {
        config.set("general.cars.fuel.sellFuel", Boolean.valueOf(true));
      }
      if (!config.contains("general.cars.barriers")) {
        config.set("general.cars.barriers", new String[] {
          "COBBLE_WALL", "FENCE", "FENCE_GATE", "NETHER_FENCE" });
      }
      if (!config.contains("general.cars.speedMods")) {
        config.set("general.cars.speedMods", new String[] {
          "SOUL_SAND:0-10", "SPONGE:0-20" });
      }
      if (!config.contains("general.cars.placePerm.enable")) {
        config.set("general.cars.placePerm.enable", Boolean.valueOf(false));
      }
      if (!config.contains("general.cars.placePerm.perm")) {
        config.set("general.cars.placePerm.perm", "ucars.place");
      }
      if (!config.contains("general.cars.health.default")) {
        config.set("general.cars.health.default", Double.valueOf(10.0D));
      }
      if (!config.contains("general.cars.health.max")) {
        config.set("general.cars.health.max", Double.valueOf(100.0D));
      }
      if (!config.contains("general.cars.health.min")) {
        config.set("general.cars.health.min", Double.valueOf(5.0D));
      }
      if (!config.contains("general.cars.health.overrideDefault")) {
        config.set("general.cars.health.overrideDefault", Boolean.valueOf(true));
      }
      if (!config.contains("general.cars.health.underwaterDamage")) {
        config.set("general.cars.health.underwaterDamage", Double.valueOf(0.0D));
      }
      if (!config.contains("general.cars.health.lavaDamage")) {
        config.set("general.cars.health.lavaDamage", Double.valueOf(0.0D));
      }
      if (!config.contains("general.cars.health.punchDamage")) {
        config.set("general.cars.health.punchDamage", Double.valueOf(50.0D));
      }
      if (!config.contains("general.cars.health.cactusDamage")) {
        config.set("general.cars.health.cactusDamage", Double.valueOf(0.0D));
      }
      if (!config.contains("general.cars.health.crashDamage")) {
        config.set("general.cars.health.crashDamage", Double.valueOf(0.0D));
      }
      if (!config.contains("general.cars.forceRaceControlSystem")) {
        config.set("general.cars.forceRaceControlSystem", Boolean.valueOf(false));
      }
      forceRaceControls = config.getBoolean("general.cars.forceRaceControlSystem");
      if (!config.contains("general.cars.playersIgnoreTrafficLights")) {
        config.set("general.cars.playersIgnoreTrafficLights", Boolean.valueOf(false));
      }
      playersIgnoreTrafficLights = config.getBoolean("general.cars.playersIgnoreTrafficLights");
      if (!config.contains("colorScheme.success")) {
        config.set("colorScheme.success", "&a");
      }
      if (!config.contains("colorScheme.error")) {
        config.set("colorScheme.error", "&c");
      }
      if (!config.contains("colorScheme.info")) {
        config.set("colorScheme.info", "&e");
      }
      if (!config.contains("colorScheme.title")) {
        config.set("colorScheme.title", "&9");
      }
      if (!config.contains("colorScheme.tp")) {
        config.set("colorScheme.tp", "&5");
      }
      if ((config.getBoolean("general.cars.fuel.enable")) && 
        (!config.getBoolean("general.cars.fuel.items.enable"))) {
        try
        {
          if (!setupEconomy())
          {
            plugin.getLogger().warning(
              "Attempted to enable fuel but vault NOT found. Please install vault to use fuel!");
            plugin.getLogger().warning("Disabling fuel system...");
            config.set("general.cars.fuel.enable", Boolean.valueOf(false));
          }
          else
          {
            vault = Boolean.valueOf(true);
            fuel = new HashMap();
            File fuels = new File(plugin.getDataFolder()
              .getAbsolutePath() + 
              File.separator + 
              "fuel.bin");
            if ((fuels.exists()) && (fuels.length() > 1L))
            {
              fuel = loadHashMapDouble(plugin.getDataFolder()
                .getAbsolutePath() + 
                File.separator + 
                "fuel.bin");
              if (fuel == null) {
                fuel = new HashMap();
              }
            }
          }
        }
        catch (Exception e)
        {
          plugin.getLogger().warning(
            "Attempted to enable fuel but vault NOT found. Please install vault to use fuel!");
          plugin.getLogger().warning("Disabling fuel system...");
          config.set("general.cars.fuel.enable", Boolean.valueOf(false));
        }
      }
      latestConfigVersion = 1.1D;
    }
    catch (Exception localException1) {}
    double latestConfigVersion;
    double configVersion = config.getDouble("misc.configVersion");
    while (configVersion < latestConfigVersion)
    {
      configVersion += 0.1D;
      config = ConfigVersionConverter.convert(config, configVersion);
    }
    saveConfig();
    try
    {
      lang.save(langFile);
    }
    catch (IOException e1)
    {
      getLogger().info("Error parsing lang file!");
    }
    List<String> ids = config.getStringList("general.cars.fuel.items.ids");
    this.ufuelitems = new ArrayList();
    for (String raw : ids)
    {
      ItemStack stack = ItemStackFromId.get(raw);
      if (stack != null) {
        this.ufuelitems.add(stack);
      }
    }
    colors = new Colors(config.getString("colorScheme.success"), 
      config.getString("colorScheme.error"), 
      config.getString("colorScheme.info"), 
      config.getString("colorScheme.title"), 
      config.getString("colorScheme.title"));
    PluginDescriptionFile pldesc = plugin.getDescription();
    Object commands = pldesc.getCommands();
    Set<String> keys = ((Map)commands).keySet();
    for (String k : keys) {
      try
      {
        this.cmdExecutor = new uCarsCommandExecutor(this);
        getCommand(k).setExecutor(this.cmdExecutor);
      }
      catch (Exception e)
      {
        getLogger().log(Level.SEVERE, 
          "Error registering command " + k.toString());
        e.printStackTrace();
      }
    }
    if (getServer().getPluginManager().getPlugin("ProtocolLib") != null)
    {
      Boolean success = setupProtocol();
      if (!success.booleanValue())
      {
        this.protocolLib = Boolean.valueOf(false);
        getLogger()
          .log(Level.WARNING, 
          "ProtocolLib (http://http://dev.bukkit.org/bukkit-plugins/protocollib/) was not found! For servers running MC 1.6 or above this is required for ucars to work!");
      }
    }
    else
    {
      this.protocolLib = Boolean.valueOf(false);
      getLogger()
        .log(Level.WARNING, 
        "ProtocolLib (http://http://dev.bukkit.org/bukkit-plugins/protocollib/) was not found! For servers running MC 1.6 or above this is required for ucars to work!");
    }
    this.licensedPlayers = new ListStore(new File(getDataFolder() + 
      File.separator + "licenses.txt"));
    this.licensedPlayers.load();
    listener = new uCarsListener(this);
    getServer().getPluginManager().registerEvents(listener, this);
    this.API = new uCarsAPI();
    smoothDrive = config.getBoolean("general.cars.smooth");
    getLogger().info("uCars has been enabled!");
  }
  
  public void onDisable()
  {
    saveHashMap(fuel, plugin.getDataFolder().getAbsolutePath() + 
      File.separator + "fuel.bin");
    this.licensedPlayers.save();
    unHookPlugins();
    getLogger().info("uCars has been disabled!");
  }
  
  public static String getIdList(String configKey)
  {
    List<String> s = config.getStringList(configKey);
    String msg = "";
    for (String str : s) {
      if (msg.length() < 1) {
        msg = str;
      } else {
        msg = msg + ", " + str;
      }
    }
    return msg;
  }
  
  public final Boolean isBlockEqualToConfigIds(String configKey, Block block)
  {
    return isBlockEqualToConfigIds(config.getStringList(configKey), block);
  }
  
  public final Boolean isBlockEqualToConfigIds(List<String> rawIds, Block block)
  {
    for (String raw : rawIds)
    {
      String[] parts = raw.split(":");
      if (parts.length >= 1) {
        if (parts.length < 2)
        {
          if (parts[0].equalsIgnoreCase(block.getType().name())) {
            return Boolean.valueOf(true);
          }
        }
        else
        {
          String mat = parts[0];
          int data = Integer.parseInt(parts[1]);
          int bdata = block.getData();
          if ((mat.equalsIgnoreCase(block.getType().name())) && (bdata == data)) {
            return Boolean.valueOf(true);
          }
        }
      }
    }
    return Boolean.valueOf(false);
  }
  
  public final Boolean isItemEqualToConfigIds(List<String> rawIds, ItemStack item)
  {
    for (String raw : rawIds)
    {
      String[] parts = raw.split(":");
      if (parts.length >= 1) {
        if (parts.length < 2)
        {
          if (parts[0].equalsIgnoreCase(item.getType().name())) {
            return Boolean.valueOf(true);
          }
        }
        else
        {
          String mat = parts[0];
          int data = Integer.parseInt(parts[1]);
          int bdata = item.getDurability();
          if ((mat.equalsIgnoreCase(item.getType().name())) && (bdata == data)) {
            return Boolean.valueOf(true);
          }
        }
      }
    }
    return Boolean.valueOf(false);
  }
  
  public final Boolean isItemOnList(ArrayList<ItemStack> items, ItemStack item)
  {
    for (ItemStack raw : items)
    {
      String mat = raw.getType().name().toUpperCase();
      int data = raw.getDurability();
      int bdata = item.getDurability();
      if ((mat.equalsIgnoreCase(item.getType().name())) && (bdata == data)) {
        return Boolean.valueOf(true);
      }
    }
    return Boolean.valueOf(false);
  }
  
  public uCarsAPI getAPI()
  {
    return this.API;
  }
  
  public void hookPlugin(Plugin plugin)
  {
    getAPI().hookPlugin(plugin);
  }
  
  public void unHookPlugin(Plugin plugin)
  {
    getAPI().unHookPlugin(plugin);
  }
  
  public void unHookPlugins()
  {
    getAPI().unHookPlugins();
  }
  
  public Boolean isPluginHooked(Plugin plugin)
  {
    return getAPI().isPluginHooked(plugin);
  }
  
  public Plugin getPlugin(String name)
  {
    try
    {
      for (Plugin p : this.hookedPlugins) {
        if (p.getName().equalsIgnoreCase(name)) {
          return p;
        }
      }
    }
    catch (Exception e)
    {
      return null;
    }
    return null;
  }
}
