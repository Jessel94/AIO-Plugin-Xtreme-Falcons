package org.goblom.gui.plugin.listeners;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.goblom.gui.plugin.SimpleGuiCreator;
import org.goblom.gui.plugin.util.MessageManager;

public class DevListener
  extends SimpleListener
{
  public DevListener(SimpleGuiCreator plugin)
  {
    super(plugin);
  }
  
  @EventHandler
  public void onDevJoin(PlayerJoinEvent event)
  {
    if (getPlugin().getDevelopers().contains(MessageManager.stripColors(event.getPlayer().getName())))
    {
      if (getPlugin().getConfig().getBoolean("Broadcast-Dev-Join")) {
        Bukkit.broadcastMessage(MessageManager.prefix + "A SimpleGUI Creator Developer has joined.");
      }
      event.getPlayer().sendMessage(MessageManager.prefix + "This server is using SimpleGUI Creator v" + getPlugin().getDescription().getVersion());
    }
  }
  
  private SimpleGuiCreator getPlugin()
  {
    return (SimpleGuiCreator)Bukkit.getPluginManager().getPlugin("SimpleGUI Creator");
  }
}
