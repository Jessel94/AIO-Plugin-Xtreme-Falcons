package org.goblom.gui.plugin.handler;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.goblom.gui.plugin.SimpleGuiCreator;
import org.goblom.gui.plugin.api.events.GUICreateEvent;
import org.goblom.gui.plugin.handler.menu.InvMenu;
import org.goblom.gui.plugin.handler.menu.InvMenu.OptionClickEvent;
import org.goblom.gui.plugin.handler.menu.InvMenu.OptionClickEventHandler;
import org.goblom.gui.plugin.helpers.GUIHelper;
import org.goblom.gui.plugin.util.MessageManager;
import org.goblom.gui.plugin.util.Util;
import org.goblom.gui.plugin.util.Util.FileFilter;

public class InventoryGUI
{
  private final SimpleGuiCreator plugin;
  public static Map<String, InvMenu> guis = new HashMap();
  public static Map<String, Boolean> guiPerm = new HashMap();
  public static Map<String, String> guiNode = new HashMap();
  public static Map<String, String> guiPriceMessage = new HashMap();
  public static Map<InvMenu, FileConfiguration> guiConfig = new HashMap();
  public static Map<String, Map<Integer, Integer>> slotPrice = new HashMap();
  public static Map<String, Map<Integer, Integer>> slotWait = new HashMap();
  public static List<String> guiFiles = new ArrayList();
  public static List<String> guiNames = new ArrayList();
  public static Map<Material, String> openWithItem = new HashMap();
  
  public InventoryGUI(SimpleGuiCreator plugin)
  {
    this.plugin = plugin;
  }
  
  public FileConfiguration loadGUIConfig(String name)
  {
    File file = new File(this.plugin.getDataFolder(), name);
    if (!file.exists()) {
      try
      {
        this.plugin.saveResource(name, false);
      }
      catch (Exception e)
      {
        e.printStackTrace();
        return null;
      }
    }
    return YamlConfiguration.loadConfiguration(file);
  }
  
  public boolean createGUI(FileConfiguration guiConfig, String fileName)
  {
    if (guiConfig == null) {
      return false;
    }
    String guiTitle = MessageManager.parseColor(getGUIData(guiConfig, "Name"));
    final String tempTitle = MessageManager.stripColors(guiTitle).replaceAll(" ", "");
    int guiSlots = GUIHelper.setRows(Integer.parseInt(getGUIData(guiConfig, "Rows")));
    boolean guiPerm = Boolean.parseBoolean(getGUIData(guiConfig, "Require_Perm"));
    String guiPriceMessage = MessageManager.parseColor(getGUIData(guiConfig, "Price_Message"));
    
    InvMenu menu = new InvMenu(guiTitle, guiSlots, new InvMenu.OptionClickEventHandler()
    {
      public void onOptionClick(InvMenu.OptionClickEvent event)
      {
        event.setWillClose(true);
        if (SimpleGuiCreator.econ_enabled)
        {
          if (SimpleGuiCreator.econ.getBalance(event.getPlayer().getName()) < ((Integer)((Map)InventoryGUI.slotPrice.get(tempTitle)).get(Integer.valueOf(event.getPosition()))).intValue())
          {
            event.getPlayer().sendMessage(MessageManager.prefix + ChatColor.RED + "You do not have enough money to do this.");
          }
          else
          {
            if (SimpleGuiCreator.isDebug()) {
              System.out.println(MessageManager.prefix + "Withdrawing " + ((Map)InventoryGUI.slotPrice.get(tempTitle)).get(Integer.valueOf(event.getPosition())) + " from " + event.getPlayer().getName());
            }
            SimpleGuiCreator.econ.withdrawPlayer(event.getPlayer().getName(), ((Integer)((Map)InventoryGUI.slotPrice.get(tempTitle)).get(Integer.valueOf(event.getPosition()))).intValue());
            Util.run(event.getPlayer(), event.getAction(), ((Integer)((Map)InventoryGUI.slotWait.get(tempTitle)).get(Integer.valueOf(event.getPosition()))).intValue());
          }
        }
        else {
          Util.run(event.getPlayer(), event.getAction(), ((Integer)((Map)InventoryGUI.slotWait.get(tempTitle)).get(Integer.valueOf(event.getPosition()))).intValue());
        }
      }
    }, this.plugin);
    
    guiTitle = MessageManager.stripColors(guiTitle);
    guiTitle = guiTitle.replaceAll(" ", "");
    if (!guiNames.contains(guiTitle)) {
      guiNames.add(guiTitle);
    }
    if (!guis.containsKey(guiTitle)) {
      guis.put(guiTitle, menu);
    }
    if (!guiPerm.containsKey(guiTitle)) {
      guiPerm.put(guiTitle, Boolean.valueOf(guiPerm));
    }
    if (!guiNode.containsKey(guiTitle)) {
      guiNode.put(guiTitle, guiTitle);
    }
    if (!guiPriceMessage.containsKey(guiTitle)) {
      guiPriceMessage.put(guiTitle, guiPriceMessage);
    }
    if (!guiConfig.containsKey(menu)) {
      guiConfig.put(menu, guiConfig);
    }
    getGUIOpenItem(guiConfig, guiTitle);
    
    Util.callEvent(new GUICreateEvent(fileName, guiTitle, menu));
    
    return true;
  }
  
  public void getGUIFiles()
  {
    File[] files = this.plugin.getDataFolder().listFiles(new Util.FileFilter("gui-", ".yml"));
    for (File file : files)
    {
      String guiFile = file.getName();
      guiFiles.add(guiFile);
    }
  }
  
  public void loadGUIs()
  {
    for (String guiFile : guiFiles)
    {
      FileConfiguration guiConfig = loadGUIConfig(guiFile);
      if (guiConfig != null)
      {
        if (!createGUI(guiConfig, guiFile)) {
          this.plugin.getLogger().info(MessageManager.prefix + "Unable to create a gui for " + guiFile);
        }
      }
      else {
        this.plugin.getLogger().info(MessageManager.prefix + "Unable to load " + guiFile);
      }
    }
  }
  
  public void purge()
  {
    guiFiles.clear();
    guiNames.clear();
    InvMenu i;
    for (Iterator i$ = guis.values().iterator(); i$.hasNext(); i.destroy()) {
      i = (InvMenu)i$.next();
    }
    guis.clear();
    guiPerm.clear();
    guiNode.clear();
    guiPriceMessage.clear();
    
    slotPrice.clear();
    slotWait.clear();
  }
  
  public void loadSlots()
  {
    for (String guiTitle : guiNames)
    {
      InvMenu gui;
      FileConfiguration guiConfig;
      if (guis.containsKey(guiTitle))
      {
        gui = (InvMenu)guis.get(guiTitle);
        guiConfig = (FileConfiguration)guiConfig.get(gui);
        slotPrice.put(guiTitle, new HashMap());
        slotWait.put(guiTitle, new HashMap());
        for (String slot : guiConfig.getConfigurationSection("Slots").getKeys(false))
        {
          String item = getSlotData(guiConfig, slot, "Item");
          String name = getSlotData(guiConfig, slot, "Name");
          List<String> lore = getSlotDataList(guiConfig, slot, "Lore");
          String action = getSlotData(guiConfig, slot, "Action");
          int price = 0;
          if (slotHasPrice(guiConfig, slot))
          {
            price = getSlotPrice(guiConfig, slot);
            if (SimpleGuiCreator.isDebug()) {
              System.out.println(MessageManager.prefix + guiTitle + ": Slot " + slot + " -- Price: " + price);
            }
          }
          short itemDamageValue = 0;
          int itemAmount = 1;
          if (price != 0)
          {
            lore.add("");
            lore.add(MessageManager.parseColor((String)guiPriceMessage.get(guiTitle) + price));
          }
          ((Map)slotPrice.get(guiTitle)).put(Integer.valueOf(Integer.valueOf(slot).intValue() - 1), Integer.valueOf(price));
          ((Map)slotWait.get(guiTitle)).put(Integer.valueOf(Integer.valueOf(slot).intValue() - 1), Integer.valueOf(getSlotWait(guiConfig, slot)));
          if (item.contains(":"))
          {
            String[] data = item.split(":");
            
            Material itemMaterial = Material.valueOf(data[0].toUpperCase());
            if (data.length == 2) {
              itemDamageValue = (short)Integer.parseInt(data[1]);
            }
            if (data.length == 3) {
              itemAmount = Integer.parseInt(data[2]);
            }
            GUIHelper.addItem(gui, Integer.parseInt(slot) - 1, new ItemStack(itemMaterial, itemAmount, itemDamageValue), MessageManager.parseColor(name), action, lore);
          }
          else
          {
            Material itemMaterial = Material.valueOf(item);
            GUIHelper.addItem(gui, Integer.parseInt(slot) - 1, new ItemStack(itemMaterial, itemAmount, itemDamageValue), MessageManager.parseColor(name), action, lore);
          }
        }
      }
      else
      {
        this.plugin.getLogger().severe(MessageManager.prefix + "Unable to add data to " + guiTitle);
      }
    }
  }
  
  public boolean slotContainsData(FileConfiguration config, String slot, String data)
  {
    return config.contains("Slots." + slot + "." + data);
  }
  
  public String getSlotData(FileConfiguration config, String slot, String data)
  {
    return config.getString("Slots." + slot + "." + data);
  }
  
  public List<String> getSlotDataList(FileConfiguration config, String slot, String data)
  {
    return config.getStringList("Slots." + slot + "." + data);
  }
  
  public String getGUIData(FileConfiguration config, String data)
  {
    return config.getString("GUI." + data);
  }
  
  public boolean slotHasPrice(FileConfiguration config, String slot)
  {
    return config.contains("Slots." + slot + ".Price");
  }
  
  public int getSlotPrice(FileConfiguration config, String slot)
  {
    return Integer.parseInt(config.getString("Slots." + slot + ".Price"));
  }
  
  public int getSlotWait(FileConfiguration config, String slot)
  {
    if (config.contains("Slots." + slot + ".Wait")) {
      return Integer.parseInt(config.getString("Slots." + slot + ".Wait"));
    }
    return 0;
  }
  
  public void getGUIOpenItem(FileConfiguration config, String guiName)
  {
    if (config.contains("GUI.Open-With-Item")) {
      openWithItem.put(Material.getMaterial(config.getString("GUI.Open-With-Item")), guiName);
    }
  }
  
  public boolean createFromChest(Player player, Chest chest)
  {
    Inventory inv = chest.getInventory();
    ItemStack[] items = inv.getContents();
    File file = new File(getPlugin().getDataFolder(), "gui-" + inv.getTitle().replaceAll(" ", "").toLowerCase() + ".yml");
    FileConfiguration guiConfig = YamlConfiguration.loadConfiguration(file);
    if (guiConfig != null)
    {
      guiConfig.set("GUI.Name", inv.getTitle());
      guiConfig.set("GUI.Rows", Integer.valueOf(6));
      guiConfig.set("GUI.Require_Perm", Boolean.valueOf(false));
      guiConfig.set("GUI.Price_Message", "&aCosts:&b ");
      for (int i = 0; i < items.length; i++) {
        setSlot(guiConfig, i, items[i]);
      }
      try
      {
        guiConfig.save(file);
        player.sendMessage(MessageManager.prefix + "GUI Created Successfully");
        return true;
      }
      catch (IOException e)
      {
        return false;
      }
    }
    player.sendMessage(MessageManager.prefix + ChatColor.RED + "Unable to convert chest to GUI.");
    return false;
  }
  
  public void setSlot(FileConfiguration guiConfig, int slot, ItemStack item)
  {
    slot += 1;
    if (item == null) {
      return;
    }
    if (item.getType().equals(Material.AIR)) {
      return;
    }
    guiConfig.set("Slots." + slot + ".Item", item.getType().name().toUpperCase() + ":" + item.getDurability() + ":" + item.getAmount());
    guiConfig.set("Slots." + slot + ".Name", MessageManager.stripColors(item.getItemMeta().getDisplayName()));
    if (item.hasItemMeta())
    {
      List<String> lore = new ArrayList();
      if (item.getItemMeta().hasLore())
      {
        for (String line : item.getItemMeta().getLore()) {
          if (line.startsWith("Action:"))
          {
            line = line.substring(7);
            guiConfig.set("Slots." + slot + ".Action", line);
          }
          else if (line.startsWith("Price:"))
          {
            line = line.substring(6);
            guiConfig.set("Slots." + slot + ".Price", line);
          }
          else
          {
            lore.add(MessageManager.stripColors(line));
          }
        }
        guiConfig.set("Slots." + slot + ".Lore", lore);
      }
    }
  }
  
  private SimpleGuiCreator getPlugin()
  {
    return (SimpleGuiCreator)Bukkit.getPluginManager().getPlugin("SimpleGUI Creator");
  }
}
