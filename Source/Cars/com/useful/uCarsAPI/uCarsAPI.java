package com.useful.uCarsAPI;

import com.useful.ucars.uCarsListener;
import com.useful.ucars.ucars;
import com.useful.ucarsCommon.StatValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.Vector;

public class uCarsAPI
{
  private ucars plugin = null;
  private Map<Plugin, CarCheck> carChecks = new HashMap();
  private Map<Plugin, ItemCarCheck> itemCarChecks = new HashMap();
  private Map<Plugin, CarSpeedModifier> carSpeedMods = new HashMap();
  private Map<Plugin, CarAccelerationModifier> carAccelMods = new HashMap();
  private Map<UUID, Map<String, StatValue>> ucarsMeta = new HashMap();
  
  public uCarsAPI()
  {
    this.plugin = ucars.plugin;
  }
  
  public static uCarsAPI getAPI()
  {
    return ucars.plugin.getAPI();
  }
  
  public void hookPlugin(Plugin plugin)
  {
    ucars.plugin.getLogger().info(
      "Successfully hooked into by: " + plugin.getName());
    ucars.plugin.hookedPlugins.add(plugin);
  }
  
  public void unHookPlugin(Plugin plugin)
  {
    ucars.plugin.getLogger().info("Successfully unhooked: " + plugin.getName());
    ucars.plugin.hookedPlugins.remove(plugin);
  }
  
  public void unHookPlugins()
  {
    this.plugin.hookedPlugins.removeAll(this.plugin.hookedPlugins);
    this.plugin.getLogger().info("Successfully unhooked all plugins!");
  }
  
  public Boolean isPluginHooked(Plugin plugin)
  {
    if (plugin == ucars.plugin) {
      return Boolean.valueOf(true);
    }
    return Boolean.valueOf(ucars.plugin.hookedPlugins.contains(plugin));
  }
  
  public Boolean registerCarCheck(Plugin plugin, CarCheck carCheck)
  {
    if (!isPluginHooked(plugin).booleanValue()) {
      return Boolean.valueOf(false);
    }
    this.carChecks.put(plugin, carCheck);
    return Boolean.valueOf(true);
  }
  
  public Boolean registerItemCarCheck(Plugin plugin, ItemCarCheck carCheck)
  {
    if (!isPluginHooked(plugin).booleanValue()) {
      return Boolean.valueOf(false);
    }
    this.itemCarChecks.put(plugin, carCheck);
    return Boolean.valueOf(true);
  }
  
  public Boolean unRegisterCarCheck(Plugin plugin)
  {
    if (!isPluginHooked(plugin).booleanValue()) {
      return Boolean.valueOf(false);
    }
    this.carChecks.remove(plugin);
    return Boolean.valueOf(true);
  }
  
  public Boolean unRegisterItemCarCheck(Plugin plugin)
  {
    if (!isPluginHooked(plugin).booleanValue()) {
      return Boolean.valueOf(false);
    }
    this.itemCarChecks.remove(plugin);
    return Boolean.valueOf(true);
  }
  
  public synchronized Boolean runCarChecks(Minecart car)
  {
    for (CarCheck c : this.carChecks.values()) {
      if (!c.isACar(car).booleanValue()) {
        return Boolean.valueOf(false);
      }
    }
    return Boolean.valueOf(true);
  }
  
  public synchronized Boolean runCarChecks(ItemStack carStack)
  {
    for (ItemCarCheck c : this.itemCarChecks.values()) {
      if (!c.isACar(carStack).booleanValue()) {
        return Boolean.valueOf(false);
      }
    }
    return Boolean.valueOf(true);
  }
  
  public Boolean registerSpeedMod(Plugin plugin, CarSpeedModifier speedMod)
  {
    if (!isPluginHooked(plugin).booleanValue()) {
      return Boolean.valueOf(false);
    }
    this.carSpeedMods.put(plugin, speedMod);
    return Boolean.valueOf(true);
  }
  
  public Boolean unRegisterSpeedMod(Plugin plugin)
  {
    if (!isPluginHooked(plugin).booleanValue()) {
      return Boolean.valueOf(false);
    }
    this.carSpeedMods.remove(plugin);
    return Boolean.valueOf(true);
  }
  
  public Boolean registerAccelerationMod(Plugin plugin, CarAccelerationModifier accMod)
  {
    if (!isPluginHooked(plugin).booleanValue()) {
      return Boolean.valueOf(false);
    }
    this.carAccelMods.put(plugin, accMod);
    return Boolean.valueOf(true);
  }
  
  public Boolean unRegisterAccelerationMod(Plugin plugin)
  {
    if (!isPluginHooked(plugin).booleanValue()) {
      return Boolean.valueOf(false);
    }
    this.carAccelMods.remove(plugin);
    return Boolean.valueOf(true);
  }
  
  public float getAcceleration(Player driver, float currentMult)
  {
    if (this.carAccelMods.size() < 1) {
      return currentMult;
    }
    for (CarAccelerationModifier m : new ArrayList(this.carAccelMods.values())) {
      currentMult = m.getAccelerationDecimal(driver, currentMult);
    }
    return currentMult;
  }
  
  public synchronized Vector getTravelVector(Minecart car, Vector travelVector, double currentMult)
  {
    for (CarSpeedModifier m : this.carSpeedMods.values()) {
      travelVector = m.getModifiedSpeed(car, travelVector, currentMult);
    }
    return travelVector;
  }
  
  public Map<String, StatValue> getuCarMeta(UUID entityId)
  {
    if (!this.ucarsMeta.containsKey(entityId)) {
      return new HashMap();
    }
    return (Map)this.ucarsMeta.get(entityId);
  }
  
  public boolean setUseRaceControls(UUID id, Plugin plugin)
  {
    return adduCarsMeta(plugin, id, "car.controls", new StatValue("race", ucars.plugin)).booleanValue();
  }
  
  public Boolean adduCarsMeta(Plugin plugin, UUID entityId, String statName, StatValue toAdd)
  {
    if (!isPluginHooked(plugin).booleanValue()) {
      return Boolean.valueOf(false);
    }
    Map<String, StatValue> stats = new HashMap();
    if (this.ucarsMeta.containsKey(entityId)) {
      stats = (Map)this.ucarsMeta.get(entityId);
    }
    stats.put(statName, toAdd);
    this.ucarsMeta.put(entityId, stats);
    return Boolean.valueOf(true);
  }
  
  public StatValue getUcarMeta(Plugin plugin, String statName, UUID entityId)
  {
    if ((!isPluginHooked(plugin).booleanValue()) || (!this.ucarsMeta.containsKey(entityId))) {
      return null;
    }
    Map<String, StatValue> metas = (Map)this.ucarsMeta.get(entityId);
    if (!metas.containsKey(statName)) {
      return null;
    }
    return (StatValue)metas.get(statName);
  }
  
  public Boolean removeUcarMeta(Plugin plugin, String statName, UUID entityId)
  {
    if ((!isPluginHooked(plugin).booleanValue()) || (!this.ucarsMeta.containsKey(entityId))) {
      return Boolean.valueOf(false);
    }
    Map<String, StatValue> metas = (Map)this.ucarsMeta.get(entityId);
    if (!metas.containsKey(statName)) {
      return Boolean.valueOf(false);
    }
    metas.remove(statName);
    this.ucarsMeta.put(entityId, metas);
    return Boolean.valueOf(true);
  }
  
  public Boolean clearCarMeta(Plugin plugin, UUID entityId)
  {
    if (!isPluginHooked(plugin).booleanValue()) {
      return Boolean.valueOf(false);
    }
    this.ucarsMeta.remove(entityId);
    return Boolean.valueOf(true);
  }
  
  public void updateUcarMeta(UUID previousId, UUID newId)
  {
    if (!this.ucarsMeta.containsKey(previousId)) {
      return;
    }
    this.ucarsMeta.put(newId, 
      new HashMap((Map)this.ucarsMeta.get(previousId)));
    this.ucarsMeta.remove(previousId);
  }
  
  public Boolean checkIfCar(Minecart car)
  {
    return Boolean.valueOf(ucars.listener.isACar(car));
  }
  
  public Boolean checkInCar(Player player)
  {
    return Boolean.valueOf(ucars.listener.inACar(player));
  }
  
  public Boolean checkInCar(String player)
  {
    return Boolean.valueOf(ucars.listener.inACar(player));
  }
  
  public String getUCarsVersion()
  {
    return this.plugin.getDescription().getVersion();
  }
  
  public Boolean atTrafficLight(Minecart car)
  {
    Location loc = car.getLocation();
    Block under = loc.getBlock().getRelative(BlockFace.DOWN);
    Block underunder = under.getRelative(BlockFace.DOWN);
    return ucars.listener.atTrafficLight(car, under, underunder, loc);
  }
}
