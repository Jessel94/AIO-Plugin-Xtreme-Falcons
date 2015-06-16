package com.useful.ucars;

import com.useful.uCarsAPI.CarRespawnReason;
import com.useful.uCarsAPI.uCarRespawnEvent;
import com.useful.uCarsAPI.uCarsAPI;
import com.useful.ucarsCommon.StatValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

public class uCarsListener
  implements Listener
{
  private ucars plugin;
  private List<String> ignoreJump = null;
  private Boolean carsEnabled = Boolean.valueOf(true);
  private Boolean licenseEnabled = Boolean.valueOf(false);
  private Boolean roadBlocksEnabled = Boolean.valueOf(false);
  private Boolean trafficLightsEnabled = Boolean.valueOf(true);
  private Boolean effectBlocksEnabled = Boolean.valueOf(true);
  private Boolean usePerms = Boolean.valueOf(false);
  private Boolean fuelEnabled = Boolean.valueOf(false);
  private Boolean fuelUseItems = Boolean.valueOf(false);
  private double defaultSpeed = 30.0D;
  private double defaultHealth = 10.0D;
  private double damage_water = 0.0D;
  private double damage_lava = 10.0D;
  private double damage_cactus = 5.0D;
  private double uCar_jump_amount = 20.0D;
  private double crash_damage = 0.0D;
  private String fuelBypassPerm = "ufuel.bypass";
  private List<String> roadBlocks = new ArrayList();
  private List<String> trafficLightRawIds = new ArrayList();
  private List<String> blockBoost = new ArrayList();
  private List<String> highBlockBoost = new ArrayList();
  private List<String> resetBlockBoost = new ArrayList();
  private List<String> jumpBlock = new ArrayList();
  private List<String> teleportBlock = new ArrayList();
  private List<String> barriers = new ArrayList();
  private ConcurrentHashMap<String, Double> speedMods = new ConcurrentHashMap();
  
  public uCarsListener(ucars plugin)
  {
    this.plugin = ucars.plugin;
    this.ignoreJump = new ArrayList();
    this.ignoreJump.add("AIR");
    this.ignoreJump.add("LAVA");
    this.ignoreJump.add("STATIONARY_LAVA");
    this.ignoreJump.add("WATER");
    this.ignoreJump.add("STATIONARY_WATER");
    this.ignoreJump.add("COBBLE_WALL");
    this.ignoreJump.add("FENCE");
    this.ignoreJump.add("NETHER_FENCE");
    this.ignoreJump.add("STONE_PLATE");
    this.ignoreJump.add("WOOD_PLATE");
    this.ignoreJump.add("TRIPWIRE");
    this.ignoreJump.add("TRIPWIRE_HOOK");
    this.ignoreJump.add("TORCH");
    this.ignoreJump.add("REDSTONE_TORCH_ON");
    this.ignoreJump.add("REDSTONE_TORCH_OFF");
    this.ignoreJump.add("DIODE_BLOCK_OFF");
    this.ignoreJump.add("DIODE_BLOCK_ON");
    this.ignoreJump.add("REDSTONE_COMPARATOR_OFF");
    this.ignoreJump.add("REDSTONE_COMPARATOR_ON");
    this.ignoreJump.add("VINE");
    this.ignoreJump.add("LONG_GRASS");
    this.ignoreJump.add("STONE_BUTTON");
    this.ignoreJump.add("WOOD_BUTTON");
    this.ignoreJump.add("FENCE_GATE");
    this.ignoreJump.add("LEVER");
    this.ignoreJump.add("SNOW");
    this.ignoreJump.add("DAYLIGHT_DETECTOR");
    this.ignoreJump.add("SIGN_POST");
    this.ignoreJump.add("WALL_SIGN");
    
    this.usePerms = Boolean.valueOf(ucars.config.getBoolean("general.permissions.enable"));
    this.carsEnabled = Boolean.valueOf(ucars.config.getBoolean("general.cars.enable"));
    this.defaultHealth = ucars.config.getDouble("general.cars.health.default");
    
    this.damage_water = ucars.config
      .getDouble("general.cars.health.underwaterDamage");
    this.damage_lava = ucars.config
      .getDouble("general.cars.health.lavaDamage");
    this.damage_cactus = ucars.config
      .getDouble("general.cars.health.cactusDamage");
    this.defaultSpeed = ucars.config
      .getDouble("general.cars.defSpeed");
    this.fuelBypassPerm = ucars.config
      .getString("general.cars.fuel.bypassPerm");
    this.uCar_jump_amount = ucars.config
      .getDouble("general.cars.jumpAmount");
    this.crash_damage = ucars.config
      .getDouble("general.cars.health.crashDamage");
    
    this.licenseEnabled = Boolean.valueOf(ucars.config.getBoolean("general.cars.licenses.enable"));
    this.roadBlocksEnabled = Boolean.valueOf(ucars.config.getBoolean("general.cars.roadBlocks.enable"));
    this.trafficLightsEnabled = Boolean.valueOf(ucars.config.getBoolean("general.cars.trafficLights.enable"));
    this.effectBlocksEnabled = Boolean.valueOf(ucars.config.getBoolean("general.cars.effectBlocks.enable"));
    this.fuelEnabled = Boolean.valueOf(ucars.config.getBoolean("general.cars.fuel.enable"));
    this.fuelUseItems = Boolean.valueOf(ucars.config.getBoolean("general.cars.fuel.items.enable"));
    if (this.roadBlocksEnabled.booleanValue())
    {
      List<String> ids = ucars.config
        .getStringList("general.cars.roadBlocks.ids");
      ids.addAll(ucars.config.getStringList("general.cars.blockBoost"));
      ids.addAll(ucars.config.getStringList("general.cars.HighblockBoost"));
      ids.addAll(ucars.config.getStringList("general.cars.ResetblockBoost"));
      ids.addAll(ucars.config.getStringList("general.cars.jumpBlock"));
      ids.add("AIR");
      ids.add("LAVA");
      ids.add("STATIONARY_LAVA");
      ids.add("WATER");
      ids.add("STATIONARY_WATER");
      this.roadBlocks = ids;
    }
    if (this.trafficLightsEnabled.booleanValue()) {
      this.trafficLightRawIds = ucars.config.getStringList("general.cars.trafficLights.waitingBlock");
    }
    if (this.effectBlocksEnabled.booleanValue())
    {
      this.blockBoost = ucars.config.getStringList("general.cars.blockBoost");
      this.highBlockBoost = ucars.config.getStringList("general.cars.HighblockBoost");
      this.resetBlockBoost = ucars.config.getStringList("general.cars.ResetblockBoost");
      this.jumpBlock = ucars.config.getStringList("general.cars.jumpBlock");
      this.teleportBlock = ucars.config.getStringList("general.cars.teleportBlock");
    }
    this.barriers = ucars.config.getStringList("general.cars.barriers");
    
    List<String> units = ucars.config.getStringList("general.cars.speedMods");
    for (String unit : units)
    {
      String[] sections = unit.split("-");
      try
      {
        String rawMat = sections[0];
        double mult = Double.parseDouble(sections[1]);
        this.speedMods.put(rawMat, Double.valueOf(mult));
      }
      catch (NumberFormatException localNumberFormatException) {}
    }
  }
  
  public Vector calculateCarStats(Minecart car, Player player, Vector velocity, double currentMult)
  {
    if (car.hasMetadata("car.frozen"))
    {
      velocity = new Vector(0, 0, 0);
      return velocity;
    }
    velocity = this.plugin.getAPI().getTravelVector(car, velocity, currentMult);
    return velocity;
  }
  
  public boolean trafficlightSignOn(Block block)
  {
    if ((block.getRelative(BlockFace.NORTH).getState() instanceof Sign))
    {
      Sign sign = (Sign)block.getRelative(BlockFace.NORTH).getState();
      if (ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase(ChatColor.stripColor("[TrafficLight]"))) {
        return true;
      }
    }
    else if ((block.getRelative(BlockFace.EAST).getState() instanceof Sign))
    {
      Sign sign = (Sign)block.getRelative(BlockFace.EAST).getState();
      if (ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase(ChatColor.stripColor("[TrafficLight]"))) {
        return true;
      }
    }
    else if ((block.getRelative(BlockFace.SOUTH).getState() instanceof Sign))
    {
      Sign sign = (Sign)block.getRelative(BlockFace.SOUTH).getState();
      if (ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase(ChatColor.stripColor("[TrafficLight]"))) {
        return true;
      }
    }
    else if ((block.getRelative(BlockFace.WEST).getState() instanceof Sign))
    {
      Sign sign = (Sign)block.getRelative(BlockFace.WEST).getState();
      if (ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase(ChatColor.stripColor("[TrafficLight]"))) {
        return true;
      }
    }
    else if ((block.getRelative(BlockFace.DOWN).getState() instanceof Sign))
    {
      Sign sign = (Sign)block.getRelative(BlockFace.DOWN).getState();
      if (ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase(ChatColor.stripColor("[TrafficLight]"))) {
        return true;
      }
    }
    else if ((block.getRelative(BlockFace.UP).getState() instanceof Sign))
    {
      Sign sign = (Sign)block.getRelative(BlockFace.UP).getState();
      if (ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase(ChatColor.stripColor("[TrafficLight]"))) {
        return true;
      }
    }
    return false;
  }
  
  public boolean inACar(String playername)
  {
    try
    {
      Player p = this.plugin.getServer().getPlayer(playername);
      return inACar(p);
    }
    catch (Exception e) {}
    return false;
  }
  
  public boolean isACar(Minecart cart)
  {
    if (cart.hasMetadata("ucars.ignore")) {
      return false;
    }
    Location loc = cart.getLocation();
    Block b = loc.getBlock();
    String mat = b.getType().name().toUpperCase();
    String underMat = b.getRelative(BlockFace.DOWN).getType().name().toUpperCase();
    String underUnderMat = b.getRelative(BlockFace.DOWN, 2).getType().name().toUpperCase();
    List<String> checks = new ArrayList();
    checks.add("POWERED_RAIL");
    checks.add("RAILS");
    checks.add("DETECTOR_RAIL");
    checks.add("ACTIVATOR_RAIL");
    if ((checks.contains(mat)) || 
      (checks.contains(underMat)) || 
      (checks.contains(underUnderMat))) {
      return false;
    }
    if (!this.plugin.getAPI().runCarChecks(cart).booleanValue()) {
      return false;
    }
    return true;
  }
  
  public void ResetCarBoost(String playername, Minecart car, double defaultSpeed)
  {
    String p = playername;
    World w = this.plugin.getServer().getPlayer(p).getLocation().getWorld();
    w.playSound(this.plugin.getServer().getPlayer(p).getLocation(), 
      Sound.BAT_TAKEOFF, 1.5F, -2.0F);
    if (ucars.carBoosts.containsKey(p)) {
      ucars.carBoosts.remove(p);
    }
  }
  
  public boolean carBoost(String playerName, double power, final long lengthMillis, double defaultSpeed)
  {
    final String p = playerName;
    double defMult = defaultSpeed;
    double Cur = defMult;
    if (ucars.carBoosts.containsKey(p)) {
      Cur = ((Double)ucars.carBoosts.get(p)).doubleValue();
    }
    if (Cur > defMult) {
      return false;
    }
    final double current = Cur;
    if (this.plugin == null) {
      this.plugin.getLogger().log(Level.SEVERE, 
        Lang.get("lang.error.pluginNull"));
    }
    this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable()
    {
      public void run()
      {
        World w = uCarsListener.this.plugin.getServer().getPlayer(p).getLocation()
          .getWorld();
        w.playSound(uCarsListener.this.plugin.getServer().getPlayer(p)
          .getLocation(), Sound.FIZZ, 1.5F, -2.0F);
        double speed = current + lengthMillis;
        ucars.carBoosts.put(p, Double.valueOf(speed));
        try
        {
          Thread.sleep(this.val$lengthMillis);
        }
        catch (InterruptedException e)
        {
          ucars.carBoosts.remove(p);
          return;
        }
        ucars.carBoosts.remove(p);
      }
    });
    return true;
  }
  
  public boolean inACar(Player p)
  {
    try
    {
      if (p == null) {
        return false;
      }
      if (p.getVehicle() == null) {
        return false;
      }
      Entity ent = p.getVehicle();
      if (!(ent instanceof Minecart))
      {
        while ((!(ent instanceof Minecart)) && (ent.getVehicle() != null)) {
          ent = ent.getVehicle();
        }
        if (!(ent instanceof Minecart)) {
          return false;
        }
      }
      Minecart cart = (Minecart)ent;
      return isACar(cart);
    }
    catch (Exception e) {}
    return false;
  }
  
  @EventHandler
  public void signWriter(SignChangeEvent event)
  {
    String[] lines = event.getLines();
    if (ChatColor.stripColor(lines[1]).equalsIgnoreCase("[TrafficLight]")) {
      lines[1] = "[TrafficLight]";
    }
    if (ChatColor.stripColor(lines[0]).equalsIgnoreCase("[uFuel]")) {
      lines[0] = "[uFuel]";
    }
    if (ChatColor.stripColor(lines[0]).equalsIgnoreCase("[Teleport]")) {
      lines[0] = "[Teleport]";
    }
    if ((ChatColor.stripColor(lines[0]).equalsIgnoreCase("[wir]")) && 
      (!event.getPlayer().hasPermission("wirelessredstone")))
    {
      event.getPlayer().sendMessage(ChatColor.RED + "Sorry you need the permisson 'wirelessredstone' to do this!");
      lines[0] = "";
    }
  }
  
  @EventHandler
  public void playerJoin(PlayerJoinEvent event)
  {
    if ((event.getPlayer().isOp()) && 
      (!this.plugin.protocolLib.booleanValue())) {
      event.getPlayer().sendMessage(
        ucars.colors.getError() + 
        Lang.get("lang.messages.noProtocolLib"));
    }
  }
  
  @EventHandler
  public void tickCalcsAndLegacy(VehicleUpdateEvent event)
  {
    Vehicle vehicle = event.getVehicle();
    Entity passenger = vehicle.getPassenger();
    Boolean driven = Boolean.valueOf(true);
    if ((passenger == null) || (!(vehicle instanceof Minecart))) {
      return;
    }
    if (!(passenger instanceof Player))
    {
      while ((!(passenger instanceof Player)) && 
        (passenger.getPassenger() != null)) {
        passenger = passenger.getPassenger();
      }
      if (!(passenger instanceof Player)) {
        driven = Boolean.valueOf(false);
      }
    }
    if (!driven.booleanValue()) {
      return;
    }
    if ((!(event instanceof ucarUpdateEvent)) && 
      (vehicle.hasMetadata("car.vec")))
    {
      ucarUpdateEvent evt = (ucarUpdateEvent)((MetadataValue)vehicle.getMetadata("car.vec").get(0)).value();
      evt.player = ((Player)passenger);
      evt.incrementRead();
      vehicle.removeMetadata("car.vec", ucars.plugin);
      ucarUpdateEvent et = new ucarUpdateEvent(vehicle, evt.getTravelVector().clone(), null);
      et.setRead(evt.getReadCount());
      vehicle.setMetadata("car.vec", new StatValue(et, ucars.plugin));
      ucars.plugin.getServer().getPluginManager().callEvent(evt);
      return;
    }
    Location under = vehicle.getLocation();
    under.setY(vehicle.getLocation().getY() - 1.0D);
    
    Block normalblock = vehicle.getLocation().getBlock();
    
    Player player = null;
    if (driven.booleanValue()) {
      player = (Player)passenger;
    }
    if ((vehicle instanceof Minecart))
    {
      if (!this.carsEnabled.booleanValue()) {
        return;
      }
      Minecart car = (Minecart)vehicle;
      if (!isACar(car)) {
        return;
      }
      Vector vel = car.getVelocity();
      if ((car.getVelocity().getY() > 0.1D) && 
        (!car.hasMetadata("car.falling")) && 
        (!car.hasMetadata("car.ascending")))
      {
        if (car.hasMetadata("car.jumping"))
        {
          vel.setY(2.5D);
          car.removeMetadata("car.jumping", this.plugin);
        }
        else if (car.hasMetadata("car.jumpFull"))
        {
          if (car.getVelocity().getY() > 10.0D) {
            vel.setY(5);
          }
          car.removeMetadata("car.jumpFull", this.plugin);
        }
        else
        {
          vel.setY(0);
        }
        car.setVelocity(vel);
      }
      if (car.hasMetadata("car.jumpUp"))
      {
        double amt = ((Double)((MetadataValue)car.getMetadata("car.jumpUp").get(0)).value()).doubleValue();
        car.removeMetadata("car.jumpUp", this.plugin);
        if (amt >= 1.5D)
        {
          double y = amt * 0.1D;
          car.setMetadata("car.jumpUp", new StatValue(Double.valueOf(amt - y), this.plugin));
          vel.setY(y);
          car.setVelocity(vel);
          return;
        }
        car.setMetadata("car.falling", new StatValue(Double.valueOf(0.01D), this.plugin));
      }
      if (car.hasMetadata("car.falling"))
      {
        double gravity = ((Double)((MetadataValue)car.getMetadata("car.falling").get(0)).value()).doubleValue();
        double newGravity = gravity + gravity * 0.6D;
        car.removeMetadata("car.falling", this.plugin);
        if (gravity <= 0.6D)
        {
          car.setMetadata("car.falling", new StatValue(
            Double.valueOf(newGravity), ucars.plugin));
          vel.setY(-(gravity * 1.333D + 0.2D));
          car.setVelocity(vel);
        }
      }
      final Minecart cart = car;
      Runnable onDeath = new Runnable()
      {
        public void run()
        {
          uCarsListener.this.plugin.getServer().getPluginManager().callEvent(new ucarDeathEvent(cart));
        }
      };
      CarHealthData health = new CarHealthData(
        this.defaultHealth, 
        onDeath, this.plugin);
      Boolean recalculateHealth = Boolean.valueOf(false);
      if (car.hasMetadata("carhealth"))
      {
        List<MetadataValue> vals = car.getMetadata("carhealth");
        for (MetadataValue val : vals) {
          if ((val instanceof CarHealthData)) {
            health = (CarHealthData)val;
          }
        }
      }
      if ((normalblock.getType().equals(Material.WATER)) || 
        (normalblock.getType().equals(Material.STATIONARY_WATER)))
      {
        double damage = this.damage_water;
        if (damage > 0.0D)
        {
          if (driven.booleanValue())
          {
            double max = this.defaultHealth;
            double left = health.getHealth() - damage;
            ChatColor color = ChatColor.YELLOW;
            if (left > max * 0.66D) {
              color = ChatColor.GREEN;
            }
            if (left < max * 0.33D) {
              color = ChatColor.RED;
            }
            player.sendMessage(ChatColor.RED + "-" + damage + "[" + 
              Material.WATER.name().toLowerCase() + "]" + 
              color + " (" + left + ")");
          }
          health.damage(damage);
          recalculateHealth = Boolean.valueOf(true);
        }
      }
      if ((normalblock.getType().equals(Material.LAVA)) || 
        (normalblock.getType().equals(Material.STATIONARY_LAVA)))
      {
        double damage = this.damage_lava;
        if (damage > 0.0D)
        {
          if (driven.booleanValue())
          {
            double max = this.defaultHealth;
            double left = health.getHealth() - damage;
            ChatColor color = ChatColor.YELLOW;
            if (left > max * 0.66D) {
              color = ChatColor.GREEN;
            }
            if (left < max * 0.33D) {
              color = ChatColor.RED;
            }
            player.sendMessage(ChatColor.RED + "-" + damage + "[" + 
              Material.LAVA.name().toLowerCase() + "]" + 
              color + " (" + left + ")");
          }
          health.damage(damage);
          recalculateHealth = Boolean.valueOf(true);
        }
      }
      if (recalculateHealth.booleanValue())
      {
        if (car.hasMetadata("carhealth")) {
          car.removeMetadata("carhealth", this.plugin);
        }
        car.setMetadata("carhealth", health);
      }
      if (!driven.booleanValue()) {
        return;
      }
      if (this.plugin.protocolLib.booleanValue()) {
        return;
      }
      Vector playerVelocity = car.getPassenger().getVelocity();
      ucarUpdateEvent ucarupdate = new ucarUpdateEvent(car, 
        playerVelocity, player);
      this.plugin.getServer().getPluginManager().callEvent(ucarupdate);
      return;
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void onUcarUpdate(ucarUpdateEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    Boolean modY = Boolean.valueOf(true);
    Vehicle vehicle = event.getVehicle();
    if (event.getReadCount() > 2) {
      vehicle.removeMetadata("car.vec", ucars.plugin);
    }
    Location under = vehicle.getLocation();
    under.setY(vehicle.getLocation().getY() - 1.0D);
    Block underblock = under.getBlock();
    Block underunderblock = underblock.getRelative(BlockFace.DOWN);
    Block normalblock = vehicle.getLocation().getBlock();
    
    final Player player = event.getPlayer();
    if (player == null) {
      return;
    }
    if ((vehicle instanceof Minecart))
    {
      if (!this.carsEnabled.booleanValue()) {
        return;
      }
      try
      {
        if ((this.licenseEnabled.booleanValue()) && 
          (!this.plugin.licensedPlayers.contains(player.getName()).booleanValue()))
        {
          player.sendMessage(ucars.colors.getError() + 
            Lang.get("lang.licenses.noLicense"));
          return;
        }
      }
      catch (Exception localException1)
      {
        Minecart car = (Minecart)vehicle;
        final Minecart cart = (Minecart)vehicle;
        Runnable onDeath = new Runnable()
        {
          public void run()
          {
            uCarsListener.this.plugin.getServer().getPluginManager().callEvent(new ucarDeathEvent(cart));
          }
        };
        CarHealthData health = new CarHealthData(
          this.defaultHealth, 
          onDeath, this.plugin);
        Boolean recalculateHealth = Boolean.valueOf(false);
        if ((car.getVelocity().getY() > 0.01D) && 
          (!car.hasMetadata("car.falling")) && 
          (!car.hasMetadata("car.ascending"))) {
          modY = Boolean.valueOf(false);
        }
        if (car.hasMetadata("car.jumping"))
        {
          if (!car.hasMetadata("car.ascending")) {
            modY = Boolean.valueOf(false);
          }
          car.removeMetadata("car.jumping", this.plugin);
        }
        car.setMaxSpeed(5.0D);
        if (car.hasMetadata("carhealth"))
        {
          List<MetadataValue> vals = car.getMetadata("carhealth");
          for (MetadataValue val : vals) {
            if ((val instanceof CarHealthData)) {
              health = (CarHealthData)val;
            }
          }
        }
        if (this.roadBlocksEnabled.booleanValue())
        {
          Location loc = car.getLocation().getBlock()
            .getRelative(BlockFace.DOWN).getLocation();
          if (!this.plugin.isBlockEqualToConfigIds(this.roadBlocks, loc.getBlock()).booleanValue()) {
            return;
          }
        }
        Location loc = car.getLocation();
        if ((!ucars.playersIgnoreTrafficLights) && (atTrafficLight(car, underblock, underunderblock, loc).booleanValue())) {
          return;
        }
        if (this.effectBlocksEnabled.booleanValue())
        {
          if (((this.plugin.isBlockEqualToConfigIds(this.blockBoost, underblock).booleanValue()) || 
          
            (this.plugin.isBlockEqualToConfigIds(this.blockBoost, underunderblock).booleanValue())) && 
            (inACar(player))) {
            carBoost(player.getName(), 20.0D, 6000L, 
              this.defaultSpeed);
          }
          if (((this.plugin.isBlockEqualToConfigIds(this.highBlockBoost, underblock).booleanValue()) || 
          
            (this.plugin.isBlockEqualToConfigIds(this.highBlockBoost, underunderblock).booleanValue())) && 
            (inACar(player))) {
            carBoost(player.getName(), 50.0D, 8000L, 
              this.defaultSpeed);
          }
          if (((this.plugin.isBlockEqualToConfigIds(this.resetBlockBoost, underblock).booleanValue()) || 
          
            (this.plugin.isBlockEqualToConfigIds(this.resetBlockBoost, underunderblock).booleanValue())) && 
            (inACar(player))) {
            ResetCarBoost(player.getName(), car, 
              this.defaultSpeed);
          }
        }
        Vector playerVelocity = event.getTravelVector();
        
        double multiplier = this.defaultSpeed;
        try
        {
          if (ucars.carBoosts.containsKey(player.getName())) {
            multiplier = ((Double)ucars.carBoosts.get(player.getName())).doubleValue();
          }
        }
        catch (Exception e1)
        {
          return;
        }
        String underMat = under.getBlock().getType().name().toUpperCase();
        int underdata = under.getBlock().getData();
        
        String key = underMat + ":" + underdata;
        if (this.speedMods.containsKey(key)) {
          if (!ucars.carBoosts.containsKey(player.getName())) {
            multiplier = ((Double)this.speedMods.get(key)).doubleValue();
          } else {
            multiplier = (((Double)this.speedMods.get(key)).doubleValue() + multiplier) * 0.5D;
          }
        }
        if (event.getDoDivider().booleanValue()) {
          multiplier *= event.getDivider();
        }
        Vector Velocity = playerVelocity.multiply(multiplier);
        if (!player.isInsideVehicle()) {
          return;
        }
        if ((this.usePerms.booleanValue()) && 
          (!player.hasPermission("ucars.cars")))
        {
          player.sendMessage(ucars.colors.getInfo() + 
            Lang.get("lang.messages.noDrivePerm"));
          return;
        }
        if ((normalblock.getType() != Material.AIR) && 
          (normalblock.getType() != Material.WATER) && 
          (normalblock.getType() != Material.STATIONARY_WATER) && 
          (normalblock.getType() != Material.STEP) && 
          (normalblock.getType() != Material.DOUBLE_STEP) && 
          (normalblock.getType() != Material.LONG_GRASS)) {
          if (!normalblock.getType().name().toLowerCase().contains("stairs")) {
            car.setVelocity(new Vector(0.0D, 1.1D, 0.0D));
          }
        }
        Location before = car.getLocation();
        float dir = player.getLocation().getYaw();
        BlockFace faceDir = ClosestFace.getClosestFace(dir);
        
        double fx = Velocity.getX();
        if (Math.abs(fx) > 1.0D) {
          fx = faceDir.getModX();
        }
        double fz = Velocity.getZ();
        if (Math.abs(fz) > 1.0D) {
          fz = faceDir.getModZ();
        }
        before.add(new Vector(fx, faceDir.getModY(), fz));
        Block block = before.getBlock();
        if (block.getType().equals(Material.CACTUS))
        {
          double damage = this.damage_cactus;
          if (damage > 0.0D)
          {
            double max = this.defaultHealth;
            double left = health.getHealth() - damage;
            ChatColor color = ChatColor.YELLOW;
            if (left > max * 0.66D) {
              color = ChatColor.GREEN;
            }
            if (left < max * 0.33D) {
              color = ChatColor.RED;
            }
            player.sendMessage(ChatColor.RED + "-" + damage + "[" + 
              Material.CACTUS.name().toLowerCase() + "]" + 
              color + " (" + left + ")");
            health.damage(damage);
            recalculateHealth = Boolean.valueOf(true);
          }
        }
        if ((this.fuelEnabled.booleanValue()) && 
          (!this.fuelUseItems.booleanValue()) && 
          (!player.hasPermission(this.fuelBypassPerm)))
        {
          double fuel = 0.0D;
          if (ucars.fuel.containsKey(player.getName())) {
            fuel = ((Double)ucars.fuel.get(player.getName())).doubleValue();
          }
          if (fuel < 0.1D)
          {
            player.sendMessage(ucars.colors.getError() + 
              Lang.get("lang.fuel.empty"));
            return;
          }
          int amount = 0 + (int)(Math.random() * 250.0D);
          if (amount == 10)
          {
            fuel -= 0.1D;
            fuel = Math.round(fuel * 10.0D) / 10.0D;
            ucars.fuel.put(player.getName(), Double.valueOf(fuel));
          }
        }
        else if ((this.fuelEnabled.booleanValue()) && 
          (this.fuelUseItems.booleanValue()) && 
          (!player.hasPermission(this.fuelBypassPerm)))
        {
          double fuel = 0.0D;
          ArrayList<ItemStack> items = this.plugin.ufuelitems;
          Inventory inv = player.getInventory();
          for (ItemStack item : items) {
            if (inv.contains(item.getType(), 1)) {
              fuel += 0.1D;
            }
          }
          if (fuel < 0.1D)
          {
            player.sendMessage(ucars.colors.getError() + 
              Lang.get("lang.fuel.empty"));
            return;
          }
          int amount = 0 + (int)(Math.random() * 150.0D);
          if (amount == 10)
          {
            Boolean taken = Boolean.valueOf(false);
            Boolean last = Boolean.valueOf(false);
            int toUse = 0;
            for (int i = 0; i < inv.getContents().length; i++)
            {
              ItemStack item = inv.getItem(i);
              Boolean ignore = Boolean.valueOf(false);
              try
              {
                item.getType();
              }
              catch (Exception e)
              {
                ignore = Boolean.valueOf(true);
              }
              if ((!ignore.booleanValue()) && 
                (!taken.booleanValue()) && 
                (this.plugin.isItemOnList(items, item).booleanValue()))
              {
                taken = Boolean.valueOf(true);
                if (item.getAmount() < 2)
                {
                  last = Boolean.valueOf(true);
                  toUse = i;
                }
                item.setAmount(item.getAmount() - 1);
              }
            }
            if (last.booleanValue()) {
              inv.setItem(toUse, new ItemStack(Material.AIR));
            }
          }
        }
        if (Velocity.getY() < 0.0D)
        {
          double newy = Velocity.getY() + 2.0D;
          Velocity.setY(newy);
        }
        Material bType = block.getType();
        int bData = block.getData();
        Boolean fly = Boolean.valueOf(false);
        if (normalblock.getRelative(faceDir).getType() == Material.STEP) {
          fly = Boolean.valueOf(true);
        }
        if (this.effectBlocksEnabled.booleanValue())
        {
          if ((this.plugin.isBlockEqualToConfigIds(this.jumpBlock, underblock).booleanValue()) || 
          
            (this.plugin.isBlockEqualToConfigIds(this.jumpBlock, underunderblock).booleanValue()))
          {
            double y = this.uCar_jump_amount;
            car.setMetadata("car.jumpUp", new StatValue(Double.valueOf(this.uCar_jump_amount), this.plugin));
            Velocity.setY(y);
            car.setVelocity(Velocity);
          }
          if ((this.plugin.isBlockEqualToConfigIds(this.teleportBlock, underblock).booleanValue()) || 
          
            (this.plugin.isBlockEqualToConfigIds(this.teleportBlock, underunderblock).booleanValue()))
          {
            Sign s = null;
            if ((underunderblock.getState() instanceof Sign)) {
              s = (Sign)underunderblock.getState();
            }
            if ((underunderblock.getRelative(BlockFace.DOWN).getState() instanceof Sign)) {
              s = (Sign)underunderblock.getRelative(BlockFace.DOWN)
                .getState();
            }
            if (s != null)
            {
              String[] lines = s.getLines();
              if (lines[0].equalsIgnoreCase("[Teleport]"))
              {
                Boolean raceCar = Boolean.valueOf(false);
                if (car.hasMetadata("kart.racing")) {
                  raceCar = Boolean.valueOf(true);
                }
                car.setMetadata("safeExit.ignore", new StatValue(null, this.plugin));
                car.eject();
                
                UUID carId = car.getUniqueId();
                
                car.remove();
                
                final Minecart ca = car;
                Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable()
                {
                  public void run()
                  {
                    if (ca != null) {
                      ca.remove();
                    }
                  }
                }, 2L);
                
                String xs = lines[1];
                String ys = lines[2];
                String zs = lines[3];
                Boolean valid = Boolean.valueOf(true);
                double x = 0.0D;double y = 0.0D;double z = 0.0D;
                try
                {
                  x = Double.parseDouble(xs);
                  y = Double.parseDouble(ys);
                  y += 0.5D;
                  z = Double.parseDouble(zs);
                }
                catch (NumberFormatException e)
                {
                  valid = Boolean.valueOf(false);
                }
                if (valid.booleanValue())
                {
                  List<MetadataValue> metas = null;
                  if (player.hasMetadata("car.stayIn"))
                  {
                    metas = player.getMetadata("car.stayIn");
                    for (MetadataValue val : metas) {
                      player.removeMetadata("car.stayIn", 
                        val.getOwningPlugin());
                    }
                  }
                  Location toTele = new Location(s.getWorld(), x, 
                    y, z);
                  Chunk ch = toTele.getChunk();
                  if (ch.isLoaded()) {
                    ch.load(true);
                  }
                  car = (Minecart)s.getWorld().spawnEntity(
                    toTele, EntityType.MINECART);
                  final Minecart v = car;
                  car.setMetadata("carhealth", health);
                  if (raceCar.booleanValue()) {
                    car.setMetadata("kart.racing", 
                      new StatValue(null, this.plugin));
                  }
                  health.onDeath = new Runnable()
                  {
                    public void run()
                    {
                      uCarsListener.this.plugin.getServer().getPluginManager().callEvent(
                        new ucarDeathEvent(
                        v));
                    }
                  };
                  uCarRespawnEvent evnt = new uCarRespawnEvent(car, carId, car.getUniqueId(), 
                    CarRespawnReason.TELEPORT);
                  this.plugin.getServer().getPluginManager().callEvent(evnt);
                  if (evnt.isCancelled())
                  {
                    car.remove();
                  }
                  else
                  {
                    player.sendMessage(ucars.colors.getTp() + 
                      "Teleporting...");
                    car.setPassenger(player);
                    final Minecart ucar = car;
                    Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable()
                    {
                      public void run()
                      {
                        ucar.setPassenger(player);
                      }
                    }, 2L);
                    car.setVelocity(Velocity);
                    if (metas != null) {
                      for (MetadataValue val : metas) {
                        player.setMetadata("car.stayIn", val);
                      }
                    }
                    this.plugin.getAPI().updateUcarMeta(carId, 
                      car.getUniqueId());
                  }
                }
              }
            }
          }
        }
        Location theNewLoc = block.getLocation();
        Location bidUpLoc = block.getLocation().add(0.0D, 1.0D, 0.0D);
        Material bidU = bidUpLoc.getBlock().getType();
        Boolean cont = Boolean.valueOf(true);
        
        cont = Boolean.valueOf(!this.plugin.isBlockEqualToConfigIds(this.barriers, block).booleanValue());
        
        Boolean inStairs = Boolean.valueOf(false);
        Material carBlock = car.getLocation().getBlock().getType();
        if (carBlock.name().toLowerCase().contains("stairs")) {
          inStairs = Boolean.valueOf(true);
        }
        if (car.hasMetadata("car.ascending")) {
          car.removeMetadata("car.ascending", this.plugin);
        }
        if ((inStairs.booleanValue()) || (
          (!this.ignoreJump.contains(bType.name().toUpperCase())) && (cont.booleanValue()) && (modY.booleanValue())))
        {
          if ((bidU == Material.AIR) || (bidU == Material.LAVA) || 
            (bidU == Material.STATIONARY_LAVA) || (bidU == Material.WATER) || 
            (bidU == Material.STATIONARY_WATER) || (bidU == Material.STEP) || 
            (bidU == Material.CARPET) || 
            (bidU == Material.DOUBLE_STEP) || (inStairs.booleanValue()))
          {
            theNewLoc.add(0.0D, 1.5D, 0.0D);
            Boolean calculated = Boolean.valueOf(false);
            double y = 7.0D;
            if (block.getType().name().toLowerCase().contains("step"))
            {
              calculated = Boolean.valueOf(true);
              y = 8.0D;
            }
            if (carBlock.name().toLowerCase().contains("step"))
            {
              calculated = Boolean.valueOf(true);
              y = 8.0D;
            }
            if ((carBlock.name().toLowerCase().contains(Pattern.quote("stairs"))) || 
            
              (block.getType().name().toLowerCase().contains(Pattern.quote("stairs"))) || 
              (inStairs.booleanValue()))
            {
              calculated = Boolean.valueOf(true);
              y = 2.5D;
            }
            Boolean ignore = Boolean.valueOf(false);
            if (car.getVelocity().getY() > 4.0D) {
              ignore = Boolean.valueOf(true);
            }
            if (!ignore.booleanValue())
            {
              Velocity.setY(y);
              if (calculated.booleanValue()) {
                car.setMetadata("car.jumping", new StatValue(null, 
                  this.plugin));
              } else {
                car.setMetadata("car.jumpFull", new StatValue(null, 
                  this.plugin));
              }
            }
          }
          if ((fly.booleanValue()) && (cont.booleanValue()))
          {
            Velocity.setY(0.8D);
            car.setMetadata("car.ascending", 
              new StatValue(null, this.plugin));
          }
          car.setVelocity(calculateCarStats(car, player, Velocity, 
            multiplier));
        }
        else
        {
          if (fly.booleanValue())
          {
            Velocity.setY(0.8D);
            car.setMetadata("car.ascending", 
              new StatValue(null, this.plugin));
          }
          car.setVelocity(calculateCarStats(car, player, Velocity, 
            multiplier));
        }
        if (recalculateHealth.booleanValue())
        {
          if (car.hasMetadata("carhealth")) {
            car.removeMetadata("carhealth", this.plugin);
          }
          car.setMetadata("carhealth", health);
        }
      }
    }
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  void safeFly(EntityDamageEvent event)
  {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    if (event.isCancelled()) {
      return;
    }
    Player p = (Player)event.getEntity();
    if (inACar(p.getName()))
    {
      Vector vel = p.getVehicle().getVelocity();
      if ((vel.getY() <= -0.1D) || (vel.getY() >= 0.1D)) {
        event.setCancelled(true);
      } else {
        try
        {
          p.damage(event.getDamage());
        }
        catch (Exception localException) {}
      }
    }
  }
  
  @EventHandler
  void hitByCar(VehicleEntityCollisionEvent event)
  {
    Vehicle veh = event.getVehicle();
    if (!(veh instanceof Minecart)) {
      return;
    }
    final Minecart cart = (Minecart)veh;
    if (!isACar(cart)) {
      return;
    }
    Entity ent = event.getEntity();
    if (cart.getPassenger() == null) {
      return;
    }
    double x = cart.getVelocity().getX();
    double y = cart.getVelocity().getY();
    double z = cart.getVelocity().getZ();
    if (x < 0.0D) {
      x = -x;
    }
    if (y < 0.0D) {
      y = -y;
    }
    if (z < 0.0D) {
      z = -z;
    }
    if ((x < 0.3D) && (z < 0.3D)) {
      return;
    }
    double speed = x * z / 2.0D;
    if (speed > 0.0D)
    {
      Runnable onDeath = new Runnable()
      {
        public void run()
        {
          uCarsListener.this.plugin.getServer().getPluginManager().callEvent(new ucarDeathEvent(cart));
        }
      };
      CarHealthData health = new CarHealthData(
        this.defaultHealth, 
        onDeath, this.plugin);
      if (cart.hasMetadata("carhealth"))
      {
        List<MetadataValue> vals = cart.getMetadata("carhealth");
        for (MetadataValue val : vals) {
          if ((val instanceof CarHealthData)) {
            health = (CarHealthData)val;
          }
        }
      }
      double dmg = this.crash_damage;
      if (dmg > 0.0D)
      {
        if ((cart.getPassenger() instanceof Player))
        {
          double max = this.defaultHealth;
          double left = health.getHealth() - dmg;
          ChatColor color = ChatColor.YELLOW;
          if (left > max * 0.66D) {
            color = ChatColor.GREEN;
          }
          if (left < max * 0.33D) {
            color = ChatColor.RED;
          }
          ((Player)cart.getPassenger()).sendMessage(ChatColor.RED + "-" + dmg + "[crash]" + 
            color + " (" + left + ")");
        }
        health.damage(dmg);
      }
      if (cart.hasMetadata("carhealth")) {
        cart.removeMetadata("carhealth", this.plugin);
      }
      cart.setMetadata("carhealth", health);
    }
    if (speed <= 0.0D) {
      return;
    }
    if (!ucars.config.getBoolean("general.cars.hitBy.enable")) {
      return;
    }
    if ((ucars.config.getBoolean("general.cars.hitBy.enableMonsterDamage")) && (
      ((ent instanceof Monster)) || ((ucars.config.getBoolean("general.cars.hitBy.enableAllMonsterDamage")) && ((ent instanceof Damageable)))))
    {
      double mult = ucars.config
        .getDouble("general.cars.hitBy.power") / 7.0D;
      ent.setVelocity(cart.getVelocity().setY(0.5D).multiply(mult));
      ((Damageable)ent).damage(0.75D * (speed * 100.0D));
    }
    if (!(ent instanceof Player)) {
      return;
    }
    Player p = (Player)ent;
    if (inACar(p)) {
      return;
    }
    double mult = ucars.config.getDouble("general.cars.hitBy.power") / 5.0D;
    p.setVelocity(cart.getVelocity().setY(0.5D).multiply(mult));
    p.sendMessage(ucars.colors.getInfo() + 
      Lang.get("lang.messages.hitByCar"));
    double damage = this.crash_damage;
    p.damage((int)(damage * speed));
  }
  
  @EventHandler
  void interact(PlayerInteractEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }
    Block block = event.getClickedBlock();
    if (event.getPlayer().getItemInHand().getType() == Material.MINECART)
    {
      Material iar = block.getType();
      if ((iar == Material.RAILS) || (iar == Material.ACTIVATOR_RAIL) || 
        (iar == Material.POWERED_RAIL) || (iar == Material.DETECTOR_RAIL)) {
        return;
      }
      if (!PlaceManager.placeableOn(iar.name().toUpperCase(), block.getData()).booleanValue()) {
        return;
      }
      if (!ucars.config.getBoolean("general.cars.enable")) {
        return;
      }
      if (ucars.config.getBoolean("general.cars.placePerm.enable"))
      {
        String perm = ucars.config
          .getString("general.cars.placePerm.perm");
        if (!event.getPlayer().hasPermission(perm))
        {
          String noPerm = Lang.get("lang.messages.noPlacePerm");
          noPerm = noPerm.replaceAll("%perm%", perm);
          event.getPlayer().sendMessage(
            ucars.colors.getError() + noPerm);
          return;
        }
      }
      if (event.isCancelled())
      {
        event.getPlayer().sendMessage(
          ucars.colors.getError() + 
          Lang.get("lang.messages.noPlaceHere"));
        return;
      }
      if (!this.plugin.API.runCarChecks(event.getPlayer().getItemInHand()).booleanValue()) {
        return;
      }
      Location loc = block.getLocation().add(0.0D, 1.5D, 0.0D);
      loc.setYaw(event.getPlayer().getLocation().getYaw() + 270.0F);
      final Entity car = event.getPlayer().getWorld()
        .spawnEntity(loc, EntityType.MINECART);
      double health = ucars.config
        .getDouble("general.cars.health.default");
      Runnable onDeath = new Runnable()
      {
        public void run()
        {
          uCarsListener.this.plugin.getServer().getPluginManager().callEvent(new ucarDeathEvent((Minecart)car));
        }
      };
      car.setMetadata("carhealth", new CarHealthData(health, onDeath, 
        this.plugin));
      
      event.getPlayer().sendMessage(
        ucars.colors.getInfo() + Lang.get("lang.messages.place"));
      if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
      {
        ItemStack placed = event.getPlayer().getItemInHand();
        placed.setAmount(placed.getAmount() - 1);
        event.getPlayer().getInventory().setItemInHand(placed);
      }
    }
    if ((inACar(event.getPlayer())) && 
      (ucars.config.getBoolean("general.cars.fuel.enable"))) {
      if (this.plugin.isItemEqualToConfigIds(ucars.config.getStringList("general.cars.fuel.check"), event.getPlayer().getItemInHand()).booleanValue()) {
        event.getPlayer().performCommand("ufuel view");
      }
    }
    List<String> LowBoostRaw = ucars.config.getStringList("general.cars.lowBoost");
    List<String> MedBoostRaw = ucars.config.getStringList("general.cars.medBoost");
    List<String> HighBoostRaw = ucars.config.getStringList("general.cars.highBoost");
    
    ItemStack inHand = event.getPlayer().getItemInHand();
    String bid = inHand.getType().name().toUpperCase();
    int bdata = inHand.getDurability();
    ItemStack remove = inHand.clone();
    remove.setAmount(1);
    if ((ItemStackFromId.equals(LowBoostRaw, bid, bdata).booleanValue()) && 
      (inACar(event.getPlayer())))
    {
      boolean boosting = carBoost(event.getPlayer().getName(), 10.0D, 
        3000L, ucars.config.getDouble("general.cars.defSpeed"));
      if (boosting)
      {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
          event.getPlayer().getInventory().removeItem(new ItemStack[] { remove });
        }
        event.getPlayer().sendMessage(
          ucars.colors.getSuccess() + 
          Lang.get("lang.boosts.low"));
        return;
      }
      event.getPlayer().sendMessage(
        ucars.colors.getError() + 
        Lang.get("lang.boosts.already"));
      
      return;
    }
    if ((ItemStackFromId.equals(MedBoostRaw, bid, bdata).booleanValue()) && 
      (inACar(event.getPlayer())))
    {
      boolean boosting = carBoost(event.getPlayer().getName(), 20.0D, 
        6000L, ucars.config.getDouble("general.cars.defSpeed"));
      if (boosting)
      {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
          event.getPlayer().getInventory().removeItem(new ItemStack[] { remove });
        }
        event.getPlayer().sendMessage(
          ucars.colors.getSuccess() + 
          Lang.get("lang.boosts.med"));
        return;
      }
      event.getPlayer().sendMessage(
        ucars.colors.getError() + 
        Lang.get("lang.boosts.already"));
      
      return;
    }
    if ((ItemStackFromId.equals(HighBoostRaw, bid, bdata).booleanValue()) && 
      (inACar(event.getPlayer())))
    {
      boolean boosting = carBoost(event.getPlayer().getName(), 50.0D, 
        10000L, ucars.config.getDouble("general.cars.defSpeed"));
      if (boosting)
      {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
          event.getPlayer().getInventory().removeItem(new ItemStack[] { remove });
        }
        event.getPlayer().sendMessage(
          ucars.colors.getSuccess() + 
          Lang.get("lang.boosts.high"));
        return;
      }
      event.getPlayer().sendMessage(
        ucars.colors.getError() + 
        Lang.get("lang.boosts.already"));
      
      return;
    }
  }
  
  @EventHandler
  void signInteract(PlayerInteractEvent event)
  {
    if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }
    Block block = event.getClickedBlock();
    if (!(block.getState() instanceof Sign)) {
      return;
    }
    Sign sign = (Sign)block.getState();
    String[] lines = sign.getLines();
    if (!lines[0].equalsIgnoreCase("[uFuel]")) {
      return;
    }
    event.setCancelled(true);
    String action = lines[1];
    String quantity = lines[2];
    double amount = 0.0D;
    try
    {
      amount = Double.parseDouble(quantity);
    }
    catch (NumberFormatException e)
    {
      return;
    }
    if (action.equalsIgnoreCase("buy"))
    {
      String[] args = { "buy", amount };
      this.plugin.cmdExecutor.ufuel(event.getPlayer(), args);
    }
    else if (action.equalsIgnoreCase("sell"))
    {
      String[] args = { "sell", amount };
      this.plugin.cmdExecutor.ufuel(event.getPlayer(), args);
    }
    else {}
  }
  
  @EventHandler(priority=EventPriority.LOW)
  void minecartBreak(VehicleDamageEvent event)
  {
    if ((!(event.getVehicle() instanceof Minecart)) || 
      (!(event.getAttacker() instanceof Player))) {
      return;
    }
    if (event.isCancelled()) {
      return;
    }
    final Minecart car = (Minecart)event.getVehicle();
    Player player = (Player)event.getAttacker();
    if (!isACar(car)) {
      return;
    }
    if (!ucars.config.getBoolean("general.cars.health.overrideDefault")) {
      return;
    }
    if (car.hasMetadata("carhealth")) {
      car.removeMetadata("carhealth", this.plugin);
    }
    Runnable onDeath = new Runnable()
    {
      public void run()
      {
        uCarsListener.this.plugin.getServer().getPluginManager().callEvent(new ucarDeathEvent(car));
      }
    };
    CarHealthData health = new CarHealthData(
      ucars.config.getDouble("general.cars.health.default"), onDeath, 
      this.plugin);
    if (car.hasMetadata("carhealth"))
    {
      List<MetadataValue> vals = car.getMetadata("carhealth");
      for (MetadataValue val : vals) {
        if ((val instanceof CarHealthData)) {
          health = (CarHealthData)val;
        }
      }
    }
    double damage = ucars.config
      .getDouble("general.cars.health.punchDamage");
    if ((event.getDamage() > 0.0D) && (damage > 0.0D))
    {
      double max = ucars.config.getDouble("general.cars.health.default");
      double left = health.getHealth() - damage;
      ChatColor color = ChatColor.YELLOW;
      if (left > max * 0.66D) {
        color = ChatColor.GREEN;
      }
      if (left < max * 0.33D) {
        color = ChatColor.RED;
      }
      if (left < 0.0D) {
        left = 0.0D;
      }
      player.sendMessage(ChatColor.RED + "-" + damage + ChatColor.YELLOW + 
        "[" + player.getName() + "]" + color + " (" + left + ")");
      health.damage(damage);
      car.setMetadata("carhealth", health);
      event.setCancelled(true);
      event.setDamage(0.0D);
    }
    else
    {
      event.setCancelled(true);
      event.setDamage(0.0D);
    }
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  void carDeath(ucarDeathEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    Minecart cart = event.getCar();
    if (cart.hasMetadata("car.destroyed")) {
      return;
    }
    cart.setMetadata("car.destroyed", new StatValue(Boolean.valueOf(true), ucars.plugin));
    cart.eject();
    Location loc = cart.getLocation();
    cart.remove();
    loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.MINECART));
  }
  
  @EventHandler
  void wirelessRedstone(BlockRedstoneEvent event)
  {
    Block block = event.getBlock();
    if ((!block.getType().equals(Material.REDSTONE_LAMP_ON)) && (!block.getType().equals(Material.REDSTONE_LAMP_OFF))) {
      return;
    }
    boolean on = block.isBlockPowered();
    Sign sign = null;
    BlockFace[] arrayOfBlockFace;
    int j = (arrayOfBlockFace = BlockFace.values()).length;
    for (int i = 0; i < j; i++)
    {
      BlockFace dir = arrayOfBlockFace[i];
      Block bd = block.getRelative(dir);
      if ((bd.getState() instanceof Sign)) {
        sign = (Sign)bd.getState();
      }
    }
    if (sign == null) {
      return;
    }
    if ((sign.getLine(0) == null) || (!sign.getLine(0).equalsIgnoreCase("[wir]"))) {
      return;
    }
    String otherLoc = sign.getLine(1);
    if (otherLoc == null) {
      return;
    }
    String[] parts = otherLoc.split(",");
    if (parts.length < 3) {
      return;
    }
    try
    {
      int z;
      if (otherLoc.matches("-*\\d+,-*\\d+,-*\\d+"))
      {
        int x = Integer.parseInt(parts[0]);
        int y = Integer.parseInt(parts[1]);
        z = Integer.parseInt(parts[2]);
      }
      else
      {
        return;
      }
      int z;
      int y;
      int x;
      Block otherBlock = block.getWorld().getBlockAt(x, y, z);
      otherBlock.getLocation().getChunk();
      if (on) {
        otherBlock.setType(Material.REDSTONE_BLOCK);
      } else {
        otherBlock.setType(Material.AIR);
      }
    }
    catch (Exception e) {}
  }
  
  private int getCoord(String in, int current)
    throws Exception
  {
    if (in.matches("-*\\d+")) {
      try
      {
        return Integer.parseInt(in);
      }
      catch (Exception e)
      {
        throw new Exception();
      }
    }
    if ((in.matches("~-*\\d+")) && (in.length() > 1)) {
      try
      {
        return Integer.parseInt(in.substring(1)) + current;
      }
      catch (Exception e)
      {
        throw new Exception();
      }
    }
    throw new Exception();
  }
  
  @EventHandler
  void trafficIndicators(BlockRedstoneEvent event)
  {
    Block block = event.getBlock();
    if ((!block.getType().equals(Material.REDSTONE_LAMP_ON)) && (!block.getType().equals(Material.REDSTONE_LAMP_OFF))) {
      return;
    }
    boolean on = block.isBlockPowered();
    Sign sign = null;
    BlockFace[] arrayOfBlockFace;
    int j = (arrayOfBlockFace = dirs()).length;
    for (int i = 0; i < j; i++)
    {
      BlockFace dir = arrayOfBlockFace[i];
      Block bd = block.getRelative(dir);
      if ((bd.getState() instanceof Sign)) {
        sign = (Sign)bd.getState();
      }
    }
    if (sign == null) {
      return;
    }
    if ((sign.getLine(1) == null) || (!sign.getLine(1).equalsIgnoreCase("[trafficlight]"))) {
      return;
    }
    String otherLoc = sign.getLine(2);
    if (otherLoc == null) {
      return;
    }
    String[] parts = otherLoc.split(",");
    if (parts.length < 3) {
      return;
    }
    try
    {
      if (otherLoc.matches(".+,.+,.+")) {
        try
        {
          int x = getCoord(parts[0], sign.getX());
          int y = getCoord(parts[1], sign.getY());
          z = getCoord(parts[2], sign.getZ());
        }
        catch (Exception e1)
        {
          int z;
          return;
        }
      } else {
        return;
      }
    }
    catch (Exception e)
    {
      int z;
      int y;
      int x;
      Block otherBlock;
      Entity[] arrayOfEntity;
      int m;
      int k;
      return;
    }
    otherBlock = block.getWorld().getBlockAt(x, y, z);
    m = (arrayOfEntity = otherBlock.getLocation().getChunk().getEntities()).length;k = 0;
    for (;;)
    {
      Entity e = arrayOfEntity[k];
      if ((e.getLocation().distanceSquared(otherBlock.getLocation()) < 4.0D) && 
        ((e instanceof ItemFrame)))
      {
        ItemFrame ifr = (ItemFrame)e;
        if (on) {
          ifr.setItem(new ItemStack(Material.EMERALD_BLOCK));
        } else {
          ifr.setItem(new ItemStack(Material.REDSTONE_BLOCK));
        }
      }
      k++;
      if (k >= m) {
        break;
      }
    }
  }
  
  public Boolean atTrafficLight(Minecart car, Block underblock, Block underunderblock, Location loc)
  {
    if (this.trafficLightsEnabled.booleanValue()) {
      if ((this.plugin.isBlockEqualToConfigIds(this.trafficLightRawIds, underblock).booleanValue()) || 
      
        (this.plugin.isBlockEqualToConfigIds(this.trafficLightRawIds, underunderblock).booleanValue()) || 
        
        (this.plugin.isBlockEqualToConfigIds(this.trafficLightRawIds, underunderblock.getRelative(BlockFace.DOWN)).booleanValue()) || 
        
        (this.plugin.isBlockEqualToConfigIds(this.trafficLightRawIds, underunderblock.getRelative(BlockFace.DOWN, 2)).booleanValue()))
      {
        Boolean found = Boolean.valueOf(false);
        Boolean on = Boolean.valueOf(false);
        int radius = 3;
        int radiusSquared = radius * radius;
        for (int x = -radius; (x <= radius) && (!found.booleanValue()); x++) {
          for (int z = -radius; (z <= radius) && (!found.booleanValue()); z++) {
            if (x * x + z * z <= radiusSquared)
            {
              double locX = loc.getX() + x;
              double locZ = loc.getZ() + z;
              for (int y = (int)Math.round(loc.getY() - 3.0D); (y < loc.getY() + 4.0D) && (!found.booleanValue()); y++)
              {
                Location light = new Location(
                  loc.getWorld(), locX, y, locZ);
                if (light.getBlock().getType() == Material.REDSTONE_LAMP_OFF)
                {
                  if (trafficlightSignOn(light.getBlock()))
                  {
                    found = Boolean.valueOf(true);
                    on = Boolean.valueOf(false);
                  }
                }
                else if ((light.getBlock().getType() == Material.REDSTONE_TORCH_ON) && 
                  (trafficlightSignOn(light.getBlock())))
                {
                  found = Boolean.valueOf(true);
                  on = Boolean.valueOf(true);
                }
              }
            }
          }
        }
        if ((found.booleanValue()) && 
          (!on.booleanValue())) {
          return Boolean.valueOf(true);
        }
      }
    }
    return Boolean.valueOf(false);
  }
  
  private static BlockFace[] dirs()
  {
    return new BlockFace[] {
      BlockFace.NORTH, 
      BlockFace.EAST, 
      BlockFace.SOUTH, 
      BlockFace.WEST, 
      BlockFace.NORTH_WEST, 
      BlockFace.NORTH_EAST, 
      BlockFace.SOUTH_EAST, 
      BlockFace.NORTH_WEST };
  }
}
