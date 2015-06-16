package org.goblom.gui.plugin.listeners;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;
import org.goblom.gui.plugin.SimpleGuiCreator;
import org.goblom.gui.plugin.handler.InventoryGUI;

public class ChestConverter
  extends SimpleListener
{
  public static List<String> modeEnabled = new ArrayList();
  
  public ChestConverter(SimpleGuiCreator plugin)
  {
    super(plugin);
  }
  
  @EventHandler
  public void onChestClick(PlayerInteractEvent event)
  {
    if ((modeEnabled.contains(event.getPlayer().getName())) && 
      ((event.getClickedBlock().getState() instanceof Chest)))
    {
      event.setCancelled(true);
      if (SimpleGuiCreator.gui.createFromChest(event.getPlayer(), (Chest)event.getClickedBlock().getState()))
      {
        event.getClickedBlock().breakNaturally();
        getPlugin().load();
      }
      modeEnabled.remove(event.getPlayer().getName());
    }
  }
  
  private SimpleGuiCreator getPlugin()
  {
    return (SimpleGuiCreator)Bukkit.getPluginManager().getPlugin("SimpleGUI Creator");
  }
}
