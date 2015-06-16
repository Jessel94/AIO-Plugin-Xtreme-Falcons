package org.goblom.gui.plugin.api.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.goblom.gui.plugin.handler.menu.InvMenu;

public class GUIOpenEvent
  extends GUIEvent
{
  private final String player;
  private final InvMenu gui;
  
  public GUIOpenEvent(Player player, InvMenu gui)
  {
    this.player = player.getName();
    this.gui = gui;
  }
  
  public Player getPlayer()
  {
    return Bukkit.getPlayer(this.player);
  }
  
  public InvMenu getGUI()
  {
    return this.gui;
  }
  
  public String getGUITitle()
  {
    return getGUI().getName();
  }
}
