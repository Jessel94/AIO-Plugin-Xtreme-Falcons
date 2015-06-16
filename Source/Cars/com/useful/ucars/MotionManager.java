package com.useful.ucars;

import com.useful.ucarsCommon.StatValue;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

public class MotionManager
{
  public static void move(Player player, float f, float s)
  {
    Vector vec = new Vector();
    Entity ent = player.getVehicle();
    if (ent == null) {
      return;
    }
    while ((!(ent instanceof Minecart)) && (ent.getVehicle() != null)) {
      ent = ent.getVehicle();
    }
    ucars.listener.inACar(player);
    if ((!ucars.listener.inACar(player)) || (!(ent instanceof Minecart))) {
      return;
    }
    Minecart car = (Minecart)ent;
    
    Vector plaD = player.getEyeLocation().getDirection();
    if (f == 0.0F) {
      return;
    }
    Boolean forwards = Boolean.valueOf(true);
    int side = 0;
    Boolean turning = Boolean.valueOf(false);
    if (f < 0.0F) {
      forwards = Boolean.valueOf(false);
    } else {
      forwards = Boolean.valueOf(true);
    }
    if (s > 0.0F)
    {
      side = -1;
      turning = Boolean.valueOf(true);
    }
    if (s < 0.0F)
    {
      side = 1;
      turning = Boolean.valueOf(true);
    }
    double y = -0.35D;
    double d = 27.0D;
    Boolean doDivider = Boolean.valueOf(false);
    Boolean doAction = Boolean.valueOf(false);
    double divider = 0.5D;
    if (turning.booleanValue()) {
      if (side < 0)
      {
        doAction = Boolean.valueOf(true);
        car.setMetadata("car.action", new StatValue(Boolean.valueOf(true), ucars.plugin));
      }
      else if (side > 0)
      {
        doDivider = Boolean.valueOf(true);
        car.setMetadata("car.braking", 
          new StatValue(Boolean.valueOf(true), ucars.plugin));
      }
    }
    if (forwards.booleanValue())
    {
      double x = plaD.getX() / d;
      double z = plaD.getZ() / d;
      if ((!doDivider.booleanValue()) && 
        (car.hasMetadata("car.braking"))) {
        car.removeMetadata("car.braking", ucars.plugin);
      }
      if ((!doAction.booleanValue()) && 
        (car.hasMetadata("car.action"))) {
        car.removeMetadata("car.action", ucars.plugin);
      }
      vec = new Vector(x, y, z);
      final ucarUpdateEvent event = new ucarUpdateEvent(car, vec, player);
      event.setDoDivider(doDivider);
      event.setDivider(divider);
      final Vector v = vec;
      ucars.plugin.getServer().getScheduler()
        .runTask(ucars.plugin, new Runnable()
        {
          public void run()
          {
            ControlInput.input(MotionManager.this, v, event);
          }
        });
      return;
    }
    if (!forwards.booleanValue())
    {
      double x = plaD.getX() / d;
      double z = plaD.getZ() / d;
      if ((!doDivider.booleanValue()) && 
        (car.hasMetadata("car.braking"))) {
        car.removeMetadata("car.braking", ucars.plugin);
      }
      x = 0.0D - x;
      z = 0.0D - z;
      vec = new Vector(x, y, z);
      final Vector v = vec;
      final ucarUpdateEvent event = new ucarUpdateEvent(car, vec, player);
      event.setDoDivider(doDivider);
      event.setDivider(divider);
      Bukkit.getScheduler().runTask(ucars.plugin, new Runnable()
      {
        public void run()
        {
          ControlInput.input(MotionManager.this, v, event);
        }
      });
      return;
    }
  }
}
