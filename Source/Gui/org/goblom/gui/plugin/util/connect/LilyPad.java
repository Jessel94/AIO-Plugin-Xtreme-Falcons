package org.goblom.gui.plugin.util.connect;

import lilypad.client.connect.api.Connect;
import lilypad.client.connect.api.request.impl.RedirectRequest;
import lilypad.client.connect.api.result.FutureResult;
import lilypad.client.connect.api.result.FutureResultListener;
import lilypad.client.connect.api.result.StatusCode;
import lilypad.client.connect.api.result.impl.RedirectResult;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.goblom.gui.plugin.SimpleGuiCreator;
import org.goblom.gui.plugin.util.MessageManager;

public class LilyPad
{
  private static Connect lilyPadConnect;
  private static boolean returnStatus = true;
  
  public boolean getConnect()
  {
    if (lilyPadConnect == null) {
      lilyPadConnect = (Connect)getPlugin().getServer().getServicesManager().getRegistration(Connect.class).getProvider();
    }
    return lilyPadConnect != null;
  }
  
  public boolean connect(final Player player, String server)
  {
    if (getConnect())
    {
      try
      {
        Connect conn = lilyPadConnect;
        
        conn.request(new RedirectRequest(server, player.getName())).registerListener(new FutureResultListener()
        {
          public void onResult(RedirectResult result)
          {
            if (result.getStatusCode().equals(StatusCode.SUCCESS))
            {
              LilyPad.access$002(true);
            }
            else
            {
              LilyPad.access$002(false);
              player.sendMessage(MessageManager.prefix + "An error occurred when trying to connect to server.");
            }
          }
        });
      }
      catch (Exception e)
      {
        returnStatus = false;
        player.sendMessage(MessageManager.prefix + "An error occurred when trying to connect to server.");
      }
    }
    else
    {
      returnStatus = false;
      player.sendMessage(MessageManager.prefix + "LilyPad was not found on this server. Unable to connect to different LilyPad server");
    }
    return returnStatus;
  }
  
  private SimpleGuiCreator getPlugin()
  {
    return (SimpleGuiCreator)Bukkit.getPluginManager().getPlugin("SimpleGUI Creator");
  }
}
