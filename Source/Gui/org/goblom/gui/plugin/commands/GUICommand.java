package org.goblom.gui.plugin.commands;

import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.goblom.gui.plugin.handler.InventoryGUI;
import org.goblom.gui.plugin.helpers.CommandHelper;
import org.goblom.gui.plugin.helpers.GUIHelper;
import org.goblom.gui.plugin.util.MessageManager;
import org.goblom.gui.plugin.util.PermManager;

public class GUICommand
  implements CommandExecutor
{
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (label.equalsIgnoreCase("gui"))
    {
      if (args.length >= 1)
      {
        if (sender.hasPermission(PermManager.GUI_USE))
        {
          if (CommandHelper.isPlayer(sender))
          {
            if (InventoryGUI.guis.containsKey(CommandHelper.getArgs(args, true))) {
              GUIHelper.openWithPermission((Player)sender, CommandHelper.getArgs(args, true));
            } else {
              sender.sendMessage(MessageManager.prefix + "That GUI does not exist.");
            }
          }
          else {
            sender.sendMessage(MessageManager.prefix + "Only players can run this command");
          }
        }
        else {
          sender.sendMessage(MessageManager.prefix + "You do not have permission to run this command.");
        }
      }
      else {
        sender.sendMessage(CommandHelper.sendHelpMessage("gui"));
      }
      return true;
    }
    return false;
  }
}
