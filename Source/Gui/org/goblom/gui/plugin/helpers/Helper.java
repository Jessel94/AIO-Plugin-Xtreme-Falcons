package org.goblom.gui.plugin.helpers;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public class Helper
{
  public static String startsWithSpace(String message)
  {
    if (message.startsWith(" "))
    {
      message = message.substring(1);
      startsWithSpace(message);
    }
    return message;
  }
  
  public static String startsWithSlash(String message)
  {
    if (message.startsWith("/"))
    {
      message = message.substring(1);
      startsWithSlash(message);
    }
    return message;
  }
  
  public static boolean isAuthorized(CommandSender sender, Permission perm)
  {
    return sender.hasPermission(perm);
  }
  
  public static boolean isAuthorized(CommandSender sender, String perm)
  {
    return sender.hasPermission(perm);
  }
}
