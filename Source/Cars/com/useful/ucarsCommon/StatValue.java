package com.useful.ucarsCommon;

import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class StatValue
  implements MetadataValue
{
  public Object value = null;
  public Plugin plugin = null;
  
  public StatValue(Object value, Plugin plugin)
  {
    this.value = value;
    this.plugin = plugin;
  }
  
  public Object getValue()
  {
    return this.value;
  }
  
  public void setValue(Object value)
  {
    this.value = value;
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
    return 0.0D;
  }
  
  public float asFloat()
  {
    return 0.0F;
  }
  
  public int asInt()
  {
    return 0;
  }
  
  public long asLong()
  {
    return 0L;
  }
  
  public short asShort()
  {
    return 0;
  }
  
  public String asString()
  {
    return null;
  }
  
  public Plugin getOwningPlugin()
  {
    return this.plugin;
  }
  
  public void invalidate() {}
  
  public Object value()
  {
    return this.value;
  }
}
