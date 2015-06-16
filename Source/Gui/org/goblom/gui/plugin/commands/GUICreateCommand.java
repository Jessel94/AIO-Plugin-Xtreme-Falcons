package org.goblom.gui.plugin.commands;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.goblom.gui.plugin.listeners.ChestConverter;
import org.goblom.gui.plugin.util.MessageManager;

public class GUICreateCommand
  implements CommandExecutor
{
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (label.equalsIgnoreCase("guicreate")) {
      if ((sender instanceof Player))
      {
        if (!ChestConverter.modeEnabled.contains(((Player)sender).getName()))
        {
          ChestConverter.modeEnabled.add(((Player)sender).getName());
          ((Player)sender).sendMessage(MessageManager.prefix + "Chest Conversion Mode Enabled. Now, right click the chest");
        }
        else
        {
          ChestConverter.modeEnabled.remove(((Player)sender).getName());
          ((Player)sender).sendMessage(MessageManager.prefix + "Chest Conversion Mode Disabled.");
        }
      }
      else {
        sender.sendMessage(MessageManager.prefix + "Only players can use this command");
      }
    }
    return true;
  }
}
