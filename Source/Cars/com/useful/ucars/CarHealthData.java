package com.useful.ucars;

import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class CarHealthData
  implements MetadataValue
{
  double health = 5.0D;
  Runnable onDeath = null;
  Plugin plugin = null;
  
  public CarHealthData(double health, Runnable onDeath, Plugin plugin)
  {
    this.health = health;
    this.onDeath = onDeath;
    this.plugin = plugin;
  }
  
  public boolean asBoolean()
  {
    return false;
  }
  
  public byte asByte()
  {
    return 0;
  }
  
  public double asDouble()
  {
    return this.health;
  }
  
  public float asFloat()
  {
    return (float)this.health;
  }
  
  public int asInt()
  {
    return (int)Math.floor(this.health + 0.5D);
  }
  
  public long asLong()
  {
    return Math.round(this.health);
  }
  
  public short asShort()
  {
    return Short.parseShort(this.health);
  }
  
  public String asString()
  {
    return this.health;
  }
  
  public Plugin getOwningPlugin()
  {
    return this.plugin;
  }
  
  public void invalidate()
  {
    this.health = 0.0D;
    die();
  }
  
  public Object value()
  {
    return Double.valueOf(this.health);
  }
  
  public void damage(double amount)
  {
    this.health -= amount;
    if (this.health <= 0.0D) {
      die();
    }
  }
  
  public void setHealth(double amount)
  {
    this.health = amount;
  }
  
  public double getHealth()
  {
    return this.health;
  }
  
  public void die()
  {
    this.onDeath.run();
  }
}
