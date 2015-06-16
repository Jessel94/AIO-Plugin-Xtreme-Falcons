package org.goblom.gui.plugin.helpers;

import java.util.HashMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.goblom.gui.plugin.util.MessageManager;

public class CommandHelper
{
  private static HashMap<String, String[]> helpMessages = new HashMap();
  
  public static boolean isPlayer(CommandSender sender)
  {
    return sender instanceof Player;
  }
  
  public static String getArgs(String[] args, boolean noSpace)
  {
    String message = "";
    if (noSpace) {
      for (int i = 0; i < args.length; i++) {
        message = message + args[i];
      }
    } else {
      for (int i = 0; i < args.length; i++) {
        message = message + args[i] + " ";
      }
    }
    return message;
  }
  
  public static String[] sendHelpMessage(String command)
  {
    if (helpMessages.containsKey(command))
    {
      String[] message = (String[])helpMessages.get(command);
      return message;
    }
    return new String[] { MessageManager.prefix + "Error: No help messages found for " + command };
  }
  
  private static void addMessage(String command, String[] messages)
  {
    helpMessages.put(command, messages);
  }
  
  static
  {
    addMessage("gui", new String[] { MessageManager.prefix + "Usage: /gui [gui_name]" });
    addMessage("guireload", new String[] { MessageManager.prefix + "Usage: /guireload" });
    addMessage("guicreate", new String[] { MessageManager.prefix + "Usage: /guicreate" });
  }
}
