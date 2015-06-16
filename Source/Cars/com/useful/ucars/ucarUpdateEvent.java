package com.useful.ucars;

import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Cancellable;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.util.Vector;

public class ucarUpdateEvent
  extends VehicleUpdateEvent
  implements Cancellable
{
  public Vector toTravel = new Vector();
  public Boolean changePlayerYaw = Boolean.valueOf(false);
  public float yaw = 90.0F;
  public Boolean doDivider = Boolean.valueOf(false);
  public double divider = 1.0D;
  public Boolean cancelled = Boolean.valueOf(false);
  public Player player = null;
  private int readCount = 0;
  
  public ucarUpdateEvent(Vehicle vehicle, Vector toTravel, Player player)
  {
    super(vehicle);
    this.toTravel = toTravel;
    this.player = player;
  }
  
  public void setRead(int r)
  {
    this.readCount = r;
  }
  
  public void incrementRead()
  {
    this.readCount += 1;
  }
  
  public int getReadCount()
  {
    return this.readCount;
  }
  
  public Player getPlayer()
  {
    return this.player;
  }
  
  public Vector getTravelVector()
  {
    return this.toTravel;
  }
  
  public void setChangePlayerYaw(Boolean change)
  {
    this.changePlayerYaw = change;
  }
  
  public Boolean getChangePlayerYaw()
  {
    return this.changePlayerYaw;
  }
  
  public void setDoDivider(Boolean doDivider)
  {
    this.doDivider = doDivider;
  }
  
  public Boolean getDoDivider()
  {
    return this.doDivider;
  }
  
  public void setDivider(double divider)
  {
    this.divider = divider;
  }
  
  public double getDivider()
  {
    return this.divider;
  }
  
  public boolean isCancelled()
  {
    return this.cancelled.booleanValue();
  }
  
  public void setCancelled(boolean arg0)
  {
    this.cancelled = Boolean.valueOf(arg0);
  }
}
