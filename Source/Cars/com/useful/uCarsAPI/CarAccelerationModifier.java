package com.useful.uCarsAPI;

import org.bukkit.entity.Player;

public abstract interface CarAccelerationModifier
{
  public abstract float getAccelerationDecimal(Player paramPlayer, float paramFloat);
}
