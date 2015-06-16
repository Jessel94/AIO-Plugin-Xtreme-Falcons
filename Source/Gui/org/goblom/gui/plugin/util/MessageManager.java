package org.goblom.gui.plugin.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageManager
{
  public static String prefix = ChatColor.GOLD + "[" + ChatColor.DARK_GREEN + "GUI" + ChatColor.GOLD + "] " + ChatColor.AQUA;
  public static String TARGET_SERVER_NOT_EXIST = ChatColor.RED + "Target server does not exist. Unable to connect.";
  public static String AN_ERROR_HAS_OCCURED = ChatColor.RED + "An error has occurred. Please notify a staff member immediately.";
  
  public static String COULD_NOT_HANDLE_BUNGEECORD_COMMAND(Player player, String server)
  {
    return "Could not handle BungeeCord command from " + player.getName() + ": tried to connect to " + ChatColor.GOLD + server + ChatColor.RED + ".";
  }
  
  public static String stripColors(String message)
  {
    return ChatColor.stripColor(message);
  }
  
  public static String parseColor(String message)
  {
    return ChatColor.translateAlternateColorCodes('&', message);
  }
}
