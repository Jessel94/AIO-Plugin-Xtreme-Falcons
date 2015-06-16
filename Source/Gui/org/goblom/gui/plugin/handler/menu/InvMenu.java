package org.goblom.gui.plugin.handler.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.goblom.gui.plugin.api.events.GUIClickEvent;
import org.goblom.gui.plugin.api.events.GUICloseEvent;
import org.goblom.gui.plugin.api.events.GUIOpenEvent;
import org.goblom.gui.plugin.util.Util;

public class InvMenu
  implements Listener
{
  private String name;
  private int size;
  private OptionClickEventHandler handler;
  private Plugin plugin;
  private String[] optionNames;
  private ItemStack[] optionIcons;
  private String[] optionActions;
  private List<String> viewing = new ArrayList();
  
  public InvMenu(String name, int size, OptionClickEventHandler handler, Plugin plugin)
  {
    this.name = name;
    this.size = size;
    this.handler = handler;
    this.plugin = plugin;
    this.optionNames = new String[size];
    this.optionActions = new String[size];
    this.optionIcons = new ItemStack[size];
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }
  
  public InvMenu setOption(int position, ItemStack icon, String name, String action, String... info)
  {
    this.optionNames[position] = name;
    this.optionActions[position] = action;
    this.optionIcons[position] = setItemNameAndLore(icon, name, info);
    return this;
  }
  
  public InvMenu setOption(int position, ItemStack icon, String name, String action, List<String> info)
  {
    this.optionNames[position] = name;
    this.optionActions[position] = action;
    this.optionIcons[position] = setItemNameAndLore(icon, name, info);
    return this;
  }
  
  public void open(Player player)
  {
    Inventory inventory = Bukkit.createInventory(player, this.size, this.name);
    for (int i = 0; i < this.optionIcons.length; i++) {
      if (this.optionIcons[i] != null)
      {
        if ((this.optionIcons[i].hasItemMeta()) && 
          (this.optionIcons[i].getItemMeta().hasLore()))
        {
          List<String> lore = this.optionIcons[i].getItemMeta().getLore();
          List<String> newLore = new ArrayList();
          for (String string : lore) {
            newLore.add(Util.messageParser(player, string));
          }
          this.optionIcons[i].getItemMeta().setLore(newLore);
        }
        inventory.setItem(i, this.optionIcons[i]);
      }
    }
    player.openInventory(inventory);
    Util.callEvent(new GUIOpenEvent(player, this));
  }
  
  public void destroy()
  {
    HandlerList.unregisterAll(this);
    this.handler = null;
    this.plugin = null;
    this.optionNames = null;
    this.optionIcons = null;
    this.optionActions = null;
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  void onInventoryClick(InventoryClickEvent event)
  {
    if (event.getInventory().getTitle().equals(this.name))
    {
      event.setCancelled(true);
      int slot = event.getRawSlot();
      if ((slot >= 0) && (slot < this.size) && (this.optionNames[slot] != null) && 
        (this.optionIcons[slot] != null) && 
        (this.optionActions[slot] != null) && (!this.optionActions[slot].equals("")))
      {
        Plugin plugin = this.plugin;
        OptionClickEvent e = new OptionClickEvent((Player)event.getWhoClicked(), slot, this.optionNames[slot], this.optionActions[slot], this.optionIcons[slot].getItemMeta());
        
        this.handler.onOptionClick(e);
        
        Util.callEvent(new GUIClickEvent((Player)event.getWhoClicked(), this, slot, this.optionIcons[slot], this.optionActions[slot]));
        if (e.willClose())
        {
          final Player p = (Player)event.getWhoClicked();
          Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
          {
            public void run()
            {
              p.closeInventory();
            }
          }, 1L);
          
          Util.callEvent(new GUICloseEvent(p, this));
        }
        if (e.willDestroy())
        {
          Util.callEvent(new GUICloseEvent((Player)event.getWhoClicked(), this));
          destroy();
        }
      }
    }
  }
  
  public class OptionClickEvent
  {
    private Player player;
    private int position;
    private String name;
    private boolean close;
    private boolean destroy;
    private ItemMeta meta;
    private String action;
    
    public OptionClickEvent(Player player, int position, String name, String action, ItemMeta meta)
    {
      this.player = player;
      this.position = position;
      this.name = name;
      this.action = action;
      this.meta = meta;
    }
    
    public Player getPlayer()
    {
      return this.player;
    }
    
    public int getPosition()
    {
      return this.position;
    }
    
    public String getName()
    {
      return this.name;
    }
    
    public String getAction()
    {
      return this.action;
    }
    
    public ItemMeta getMeta()
    {
      return this.meta;
    }
    
    public boolean willClose()
    {
      return this.close;
    }
    
    public boolean willDestroy()
    {
      return this.destroy;
    }
    
    public void setWillClose(boolean close)
    {
      this.close = close;
    }
    
    public void setWillDestroy(boolean destroy)
    {
      this.destroy = destroy;
    }
  }
  
  private ItemStack setItemNameAndLore(ItemStack item, String name, String[] lore)
  {
    ItemMeta im = item.getItemMeta();
    im.setDisplayName(name);
    im.setLore(Arrays.asList(lore));
    item.setItemMeta(im);
    return item;
  }
  
  public ItemStack setItemNameAndLore(ItemStack item, String name, List<String> lore)
  {
    ItemMeta im = item.getItemMeta();
    im.setDisplayName(name);
    im.setLore(lore);
    item.setItemMeta(im);
    return item;
  }
  
  public InvMenu resetOptions()
  {
    this.optionNames = new String[this.size];
    this.optionActions = new String[this.size];
    this.optionIcons = new ItemStack[this.size];
    return this;
  }
  
  public void updateName(String name)
  {
    this.name = name;
  }
  
  public void updateSize(int size)
  {
    this.size = size;
  }
  
  public void updateAction(int position, String action)
  {
    this.optionActions[position] = action;
  }
  
  public void updateIcon(int position, String optionName, ItemStack icon)
  {
    this.optionNames[position] = optionName;
    this.optionIcons[position] = icon;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public int getSize()
  {
    return this.size;
  }
  
  public String getAction(int position)
  {
    return this.optionActions[position];
  }
  
  public ItemStack getIcon(int position)
  {
    return this.optionIcons[position];
  }
  
  public String getIconName(int position)
  {
    return this.optionNames[position];
  }
  
  @EventHandler
  private void onPluginDisable(PluginDisableEvent event)
  {
    for (Player player : getViewers()) {
      close(player);
    }
  }
  
  @EventHandler
  private void onInventoryClose(InventoryCloseEvent event)
  {
    if (this.viewing.contains(event.getPlayer().getName())) {
      this.viewing.remove(event.getPlayer().getName());
    }
  }
  
  public InvMenu close(Player player)
  {
    if (player.getOpenInventory().getTitle().equals(this.name)) {
      player.closeInventory();
    }
    return this;
  }
  
  public List<Player> getViewers()
  {
    List<Player> viewers = new ArrayList();
    for (String s : this.viewing) {
      viewers.add(Bukkit.getPlayer(s));
    }
    return viewers;
  }
  
  public InvMenu closeViewers()
  {
    for (Player player : getViewers()) {
      close(player);
    }
    return this;
  }
  
  public static abstract interface OptionClickEventHandler
  {
    public abstract void onOptionClick(InvMenu.OptionClickEvent paramOptionClickEvent);
  }
}
