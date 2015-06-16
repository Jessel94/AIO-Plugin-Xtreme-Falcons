package org.goblom.gui.plugin.listeners;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;
import org.goblom.gui.plugin.SimpleGuiCreator;
import org.goblom.gui.plugin.helpers.GUIHelper;

public class SignListener
  extends SimpleListener
{
  public SignListener(SimpleGuiCreator plugin)
  {
    super(plugin);
  }
  
  @EventHandler
  public void signInteractEvent(PlayerInteractEvent event)
  {
    if ((event.getAction().equals(Action.RIGHT_CLICK_AIR)) || (event.getAction().equals(Action.LEFT_CLICK_AIR))) {
      return;
    }
    if ((event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) || (event.getAction().equals(Action.LEFT_CLICK_BLOCK)))
    {
      Block block = event.getClickedBlock();
      if ((block.getState() instanceof Sign))
      {
        Sign sign = (Sign)block.getState();
        if ((sign.getLine(0).equalsIgnoreCase(getPlugin().getConfig().getString("Sign-GUI"))) && 
          (!sign.getLine(1).equalsIgnoreCase("")))
        {
          String guiTitle = sign.getLine(1).replaceAll(" ", "");
          GUIHelper.openWithoutPermission(event.getPlayer(), guiTitle);
        }
      }
    }
  }
  
  private SimpleGuiCreator getPlugin()
  {
    return (SimpleGuiCreator)Bukkit.getPluginManager().getPlugin("SimpleGUI Creator");
  }
}
