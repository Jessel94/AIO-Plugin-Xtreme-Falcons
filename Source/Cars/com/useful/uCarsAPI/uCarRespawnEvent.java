package com.useful.uCarsAPI;

import java.util.UUID;
import org.bukkit.entity.Minecart;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class uCarRespawnEvent
  extends Event
  implements Cancellable
{
  private Minecart newCar;
  private UUID oldId;
  private UUID newId;
  private Boolean cancelled = Boolean.valueOf(false);
  private CarRespawnReason reason;
  public static HandlerList handlers = new HandlerList();
  
  public uCarRespawnEvent(Minecart newCar, UUID oldId, UUID newId, CarRespawnReason reason)
  {
    this.newCar = newCar;
    this.oldId = oldId;
    this.newId = newId;
    this.reason = reason;
  }
  
  public Minecart getNewCar()
  {
    return this.newCar;
  }
  
  public UUID getOldEntityId()
  {
    return this.oldId;
  }
  
  public UUID getNewEntityId()
  {
    return this.newId;
  }
  
  public boolean isCancelled()
  {
    return this.cancelled.booleanValue();
  }
  
  public void setCancelled(boolean arg0)
  {
    this.cancelled = Boolean.valueOf(arg0);
  }
  
  public CarRespawnReason getReason()
  {
    return this.reason;
  }
  
  public HandlerList getHandlers()
  {
    return handlers;
  }
  
  public static HandlerList getHandlerList()
  {
    return handlers;
  }
}
