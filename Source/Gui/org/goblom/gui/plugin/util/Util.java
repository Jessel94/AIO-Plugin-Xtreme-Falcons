package org.goblom.gui.plugin.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.goblom.gui.plugin.SimpleGuiCreator;
import org.goblom.gui.plugin.api.events.GUIEvent;
import org.goblom.gui.plugin.helpers.GUIHelper;
import org.goblom.gui.plugin.helpers.Helper;
import org.goblom.gui.plugin.util.connect.BungeeCord;
import org.goblom.gui.plugin.util.connect.LilyPad;

public class Util
{
  public static String messageParser(Player player, String message)
  {
    message = message.replaceAll("<player>", player.getName());
    message = message.replaceAll("<world>", player.getWorld().getName());
    if (SimpleGuiCreator.econ_enabled) {
      message = message.replaceAll("<money>", String.valueOf(SimpleGuiCreator.econ.getBalance(player.getName())));
    }
    return message;
  }
  
  public static void run(Player player, final String action, int runLater)
  {
    if (runLater != 0) {
      Bukkit.getScheduler().runTaskLater(getPlugin(), new Runnable()
      {
        public void run()
        {
          Util.performAction(this.val$player, action);
        }
      }, runLater);
    } else {
      performAction(player, action);
    }
  }
  
  public static boolean performAction(final Player player, String action)
  {
    if ((action != null) && (!action.equals("")))
    {
      action = messageParser(player, action);
      if (action.startsWith("reply:"))
      {
        action = action.substring(6);
        
        action = Helper.startsWithSpace(action);
        player.sendMessage(MessageManager.parseColor(action));
        
        return true;
      }
      if (action.startsWith("console:"))
      {
        action = action.substring(8);
        action = Helper.startsWithSpace(action);
        
        return Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action);
      }
      if (action.startsWith("op:"))
      {
        action = action.substring(3);
        action = Helper.startsWithSpace(action);
        
        boolean isOP = player.isOp();
        player.setOp(true);
        try
        {
          player.performCommand(action);
        }
        catch (Exception e) {}
        try
        {
          player.setOp(isOP);
        }
        catch (Exception e)
        {
          e.printStackTrace();
          player.setOp(false);
          getPlugin().getLogger().severe("An exception has occurred while removing " + player.getName() + "from OPs.");
        }
        return true;
      }
      if (action.startsWith("open:"))
      {
        action = action.substring(5);
        action = Helper.startsWithSpace(action);
        
        String n = action;
        Bukkit.getScheduler().runTaskLater(getPlugin(), new Runnable()
        {
          String a2 = this.val$n;
          
          public void run()
          {
            if ((this.a2.endsWith("-perm")) || (this.a2.endsWith("-permission")))
            {
              if (this.a2.contains("-perm")) {
                this.a2 = this.a2.replaceAll("-perm", "");
              }
              if (this.a2.contains("-permission")) {
                this.a2 = this.a2.replaceAll("-permission", "");
              }
              GUIHelper.openWithPermission(player, this.a2);
            }
            else
            {
              GUIHelper.openWithoutPermission(player, this.a2);
            }
          }
        }, 5L);
        
        return true;
      }
      if (action.startsWith("cmd:"))
      {
        action = action.substring(4);
        action = Helper.startsWithSpace(action);
        
        return player.performCommand(action);
      }
      if (action.startsWith("command:"))
      {
        action = action.substring(8);
        action = Helper.startsWithSpace(action);
        
        return player.performCommand(action);
      }
      if (action.startsWith("bungeecord:"))
      {
        action = action.substring(11);
        action = Helper.startsWithSpace(action);
        
        return getPlugin().getBungeeCord().connect(player, action);
      }
      if (action.startsWith("bungee:"))
      {
        action = action.substring(7);
        action = Helper.startsWithSpace(action);
        
        return getPlugin().getBungeeCord().connect(player, action);
      }
      if (action.startsWith("lilypad:"))
      {
        action = action.substring(8);
        action = Helper.startsWithSpace(action);
        if (getPlugin().checkLilypad()) {
          return getPlugin().getLilyPad().connect(player, action);
        }
        return false;
      }
      if (action.startsWith("lilypadmc:"))
      {
        action = action.substring(10);
        action = Helper.startsWithSpace(action);
        if (getPlugin().checkLilypad()) {
          return getPlugin().getLilyPad().connect(player, action);
        }
        return false;
      }
      if (action.startsWith("event:")) {
        return true;
      }
      player.chat(action);
      return true;
    }
    return false;
  }
  
  public static void callEvent(GUIEvent event)
  {
    Bukkit.getServer().getPluginManager().callEvent(event);
  }
  
  public static class FileFilter
    implements FilenameFilter
  {
    String suffix;
    String prefix;
    
    public FileFilter(String prefix, String suffix)
    {
      this.suffix = suffix;
      this.prefix = prefix;
    }
    
    public boolean accept(File dir, String name)
    {
      return (name.endsWith(this.suffix)) && (name.startsWith(this.prefix));
    }
  }
  
  private static SimpleGuiCreator getPlugin()
  {
    return (SimpleGuiCreator)Bukkit.getPluginManager().getPlugin("SimpleGUI Creator");
  }
}
