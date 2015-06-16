package org.goblom.gui.plugin.util.connect;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.messaging.Messenger;
import org.goblom.gui.plugin.SimpleGuiCreator;
import org.goblom.gui.plugin.util.MessageManager;

public class BungeeCord
{
  public boolean connect(Player player, String server)
  {
    try
    {
      Messenger messenger = Bukkit.getMessenger();
      if (!messenger.isOutgoingChannelRegistered(getPlugin(), "BungeeCord")) {
        messenger.registerOutgoingPluginChannel(getPlugin(), "BungeeCord");
      }
      if (server.length() == 0)
      {
        player.sendMessage(MessageManager.TARGET_SERVER_NOT_EXIST);
        return false;
      }
      ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
      DataOutputStream out = new DataOutputStream(byteArray);
      
      out.writeUTF("Connect");
      out.writeUTF(server);
      
      player.sendPluginMessage(getPlugin(), "BungeeCord", byteArray.toByteArray());
    }
    catch (Exception e)
    {
      e.printStackTrace();
      player.sendMessage(MessageManager.AN_ERROR_HAS_OCCURED);
      getPlugin().getLogger().warning(MessageManager.COULD_NOT_HANDLE_BUNGEECORD_COMMAND(player, server));
      return false;
    }
    return true;
  }
  
  private SimpleGuiCreator getPlugin()
  {
    return (SimpleGuiCreator)Bukkit.getPluginManager().getPlugin("SimpleGUI Creator");
  }
}
