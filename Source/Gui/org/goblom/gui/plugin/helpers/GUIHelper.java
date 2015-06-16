package org.goblom.gui.plugin.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.goblom.gui.plugin.handler.InventoryGUI;
import org.goblom.gui.plugin.handler.menu.InvMenu;
import org.goblom.gui.plugin.util.MessageManager;
import org.goblom.gui.plugin.util.PermManager;

public class GUIHelper
{
  public static int setRows(int rows)
  {
    return rows * 9;
  }
  
  public static void addItem(InvMenu menu, int slot, ItemStack itemStack, String itemName, String action, String[] lore)
  {
    List<String> newLore = new ArrayList();
    for (String string : lore) {
      newLore.add(MessageManager.parseColor(string));
    }
    menu.setOption(slot, itemStack, itemName, action, newLore);
  }
  
  public static void addItem(InvMenu menu, int slot, ItemStack itemStack, String itemName, String action, List<String> lore)
  {
    List<String> newLore = new ArrayList();
    for (String string : lore) {
      newLore.add(MessageManager.parseColor(string));
    }
    menu.setOption(slot, itemStack, itemName, action, newLore);
  }
  
  public static void addItem(InvMenu menu, int slot, ItemStack itemStack, String itemName, String action)
  {
    menu.setOption(slot, itemStack, itemName, action, new String[0]);
  }
  
  public static void openWithPermission(Player player, String action)
  {
    for (String guiTitle : InventoryGUI.guiNames) {
      if (guiTitle.equalsIgnoreCase(action))
      {
        InvMenu gui = (InvMenu)InventoryGUI.guis.get(guiTitle);
        if (PermManager.guiRequiresPerm(guiTitle))
        {
          if (Helper.isAuthorized(player, PermManager.openGUIPerm(guiTitle))) {
            gui.open(player);
          } else {
            player.sendMessage(MessageManager.prefix + "You do not have permissions to open this gui.");
          }
          return;
        }
        gui.open(player);
        return;
      }
    }
    player.sendMessage(MessageManager.prefix + "Cannot find gui " + action + ".");
  }
  
  public static void openWithoutPermission(Player player, String action)
  {
    for (String guiTitle : InventoryGUI.guiNames) {
      if (guiTitle.equalsIgnoreCase(action))
      {
        ((InvMenu)InventoryGUI.guis.get(guiTitle)).open(player);
        return;
      }
    }
    player.sendMessage(MessageManager.prefix + "Cannot find gui " + action + ".");
  }
}
