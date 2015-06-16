package org.goblom.gui.plugin.api.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.goblom.gui.plugin.handler.menu.InvMenu;

public class GUIClickEvent
  extends GUIEvent
{
  private final String playerName;
  private final String action;
  private final InvMenu gui;
  private final int slot;
  private final ItemStack item;
  
  public GUIClickEvent(Player player, InvMenu gui, int slot, ItemStack item, String action)
  {
    this.playerName = player.getName();
    this.gui = gui;
    this.slot = slot;
    this.item = item;
    this.action = action;
  }
  
  public Player getPlayer()
  {
    return Bukkit.getPlayer(this.playerName);
  }
  
  public InvMenu getGUI()
  {
    return this.gui;
  }
  
  public String getGUITitle()
  {
    return getGUI().getName();
  }
  
  public int getSlotClicked()
  {
    return this.slot;
  }
  
  public ItemStack getClickedItem()
  {
    return this.item;
  }
  
  public String getAction()
  {
    return this.action;
  }
}
