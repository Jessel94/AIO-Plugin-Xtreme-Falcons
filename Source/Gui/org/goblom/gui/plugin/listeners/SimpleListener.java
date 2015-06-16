package org.goblom.gui.plugin.listeners;

import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.goblom.gui.plugin.SimpleGuiCreator;

public abstract class SimpleListener
  implements Listener
{
  public SimpleListener(SimpleGuiCreator plugin)
  {
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }
}
