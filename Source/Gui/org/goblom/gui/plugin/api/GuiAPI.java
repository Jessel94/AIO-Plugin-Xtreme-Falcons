package org.goblom.gui.plugin.api;

import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.goblom.gui.plugin.handler.menu.InvMenu;

public abstract interface GuiAPI
{
  public abstract Set<String> getGUIs();
  
  public abstract InvMenu getGUI(String paramString);
  
  public abstract boolean openGUI(String paramString, Player paramPlayer);
  
  public abstract boolean createGUI(String paramString, int paramInt, ItemStack[] paramArrayOfItemStack, String[] paramArrayOfString);
  
  public abstract boolean editGUI(String paramString1, int paramInt, String paramString2, ItemStack paramItemStack, String paramString3);
  
  public abstract InvMenu tempGUI(String paramString, int paramInt, ItemStack[] paramArrayOfItemStack, String[] paramArrayOfString);
}
