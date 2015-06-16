package com.useful.ucars;

import com.useful.uCarsAPI.uCarsAPI;
import com.useful.ucarsCommon.StatValue;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

public class ControlInput
{
  private static float getAccel(Player player)
  {
    if (!ucars.smoothDrive) {
      return 1.0F;
    }
    float accMod = uCarsAPI.getAPI().getAcceleration(player, 1.0F);
    SmoothMeta smooth = null;
    if (!player.hasMetadata("ucars.smooth"))
    {
      smooth = new SmoothMeta(accMod);
      player.setMetadata("ucars.smooth", new StatValue(smooth, ucars.plugin));
    }
    else
    {
      Object o = ((MetadataValue)player.getMetadata("ucars.smooth").get(0)).value();
      if ((o instanceof SmoothMeta))
      {
        smooth = (SmoothMeta)o;
      }
      else
      {
        smooth = new SmoothMeta(accMod);
        player.setMetadata("ucars.smooth", new StatValue(smooth, ucars.plugin));
      }
    }
    return smooth.getFactor();
  }
  
  public static void input(Minecart car, Vector travel, ucarUpdateEvent event)
  {
    if (ucars.smoothDrive)
    {
      float a = getAccel(event.getPlayer());
      travel.setX(travel.getX() * a);
      travel.setZ(travel.getZ() * a);
    }
    uCarsAPI api = uCarsAPI.getAPI();
    StatValue controlScheme = api.getUcarMeta(ucars.plugin, "car.controls", car.getUniqueId());
    if ((controlScheme == null) && (!ucars.forceRaceControls))
    {
      ucars.plugin.getServer().getPluginManager().callEvent(event);
      return;
    }
    if ((ucars.forceRaceControls) || (((String)controlScheme.getValue()).equalsIgnoreCase("race")))
    {
      event.player = null;
      car.removeMetadata("car.vec", ucars.plugin);
      car.setMetadata("car.vec", new StatValue(event, ucars.plugin));
      return;
    }
  }
}
