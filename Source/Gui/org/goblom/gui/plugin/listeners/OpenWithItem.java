package org.goblom.gui.plugin.listeners;

import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.goblom.gui.plugin.SimpleGuiCreator;
import org.goblom.gui.plugin.handler.InventoryGUI;
import org.goblom.gui.plugin.handler.menu.InvMenu;

public class OpenWithItem
  extends SimpleListener
{
  public OpenWithItem(SimpleGuiCreator plugin)
  {
    super(plugin);
  }
  
  @EventHandler
  public void openWithItem(PlayerInteractEvent event)
  {
    Material handMaterial;
    if ((event.getPlayer().getItemInHand() != null) && (event.getPlayer().getItemInHand().getType() != Material.AIR))
    {
      handMaterial = event.getPlayer().getItemInHand().getType();
      for (Material mat : InventoryGUI.openWithItem.keySet()) {
        if (handMaterial.equals(mat))
        {
          String invName = (String)InventoryGUI.openWithItem.get(mat);
          ((InvMenu)InventoryGUI.guis.get(invName)).open(event.getPlayer());
        }
      }
    }
  }
}
