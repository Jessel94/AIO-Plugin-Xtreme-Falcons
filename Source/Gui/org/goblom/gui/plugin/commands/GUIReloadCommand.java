package org.goblom.gui.plugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.goblom.gui.plugin.SimpleGuiCreator;
import org.goblom.gui.plugin.util.MessageManager;

public class GUIReloadCommand
  implements CommandExecutor
{
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (label.equalsIgnoreCase("guireload")) {
      if ((sender.hasPermission("gui.reload")) || ((sender instanceof ConsoleCommandSender)))
      {
        getPlugin().load();
        sender.sendMessage(MessageManager.prefix + "GUIs Reloaded");
      }
      else
      {
        sender.sendMessage(MessageManager.prefix + "You do not have permission to use this command");
      }
    }
    return true;
  }
  
  private SimpleGuiCreator getPlugin()
  {
    return (SimpleGuiCreator)Bukkit.getPluginManager().getPlugin("SimpleGUI Creator");
  }
}
