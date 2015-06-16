package com.useful.uCarsAPI;

import org.bukkit.entity.Minecart;
import org.bukkit.util.Vector;

public abstract interface CarSpeedModifier
{
  public abstract Vector getModifiedSpeed(Minecart paramMinecart, Vector paramVector, double paramDouble);
}
