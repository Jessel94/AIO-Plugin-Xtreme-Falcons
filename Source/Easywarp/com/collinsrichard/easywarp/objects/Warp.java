package com.collinsrichard.easywarp.objects;

import org.bukkit.Location;
import org.bukkit.World;

public class Warp
{
  private String name;
  private Location location;
  
  public Warp(String n, Location l)
  {
    setName(n);
    setLocation(l);
  }
  
  public void setName(String s)
  {
    this.name = s;
  }
  
  public void setLocation(Location l)
  {
    this.location = l;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public Location getLocation()
  {
    return this.location;
  }
  
  public String toString()
  {
    StringBuilder sb = new StringBuilder(this.name + ",");
    
    String world = getLocation().getWorld().getName();
    String x = getLocation().getX() + "";
    String y = getLocation().getY() + "";
    String z = getLocation().getZ() + "";
    String pitch = getLocation().getPitch() + "";
    String yaw = getLocation().getYaw() + "";
    
    sb.append(world + ",");
    sb.append(x + ",");
    sb.append(y + ",");
    sb.append(z + ",");
    sb.append(pitch + ",");
    sb.append(yaw + ",");
    
    return sb.toString();
  }
}
