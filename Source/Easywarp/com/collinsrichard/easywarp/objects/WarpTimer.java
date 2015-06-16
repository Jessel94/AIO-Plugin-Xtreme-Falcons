package com.collinsrichard.easywarp.objects;

import com.collinsrichard.easywarp.Helper;
import org.bukkit.entity.Player;

public class WarpTimer
  implements Runnable
{
  public Player player = null;
  public Warp warp = null;
  public int id = 0;
  
  public WarpTimer(Player p, Warp w)
  {
    this.player = p;
    this.warp = w;
  }
  
  public void run()
  {
    Helper.stopWarping(this.player);
    
    Helper.warp(this.player, this.warp);
  }
}
