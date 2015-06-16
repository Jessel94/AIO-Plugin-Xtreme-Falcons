package org.goblom.gui.plugin.util;

import java.util.Map;
import org.goblom.gui.plugin.handler.InventoryGUI;

public class PermManager
{
  public static String GUI_USE = "gui.use";
  public static String GUI_RELOAD = "gui.reload";
  public static String GUI_BYPASS = "gui.bypass";
  public static String GUI_BYPASS_ECON = GUI_BYPASS + ".economy";
  public static String GUI_BYPASS_PERM = GUI_BYPASS + ".permissions";
  
  public static String openGUIPerm(String guiTitle)
  {
    return "gui.open." + (String)InventoryGUI.guiNode.get(guiTitle);
  }
  
  public static boolean guiRequiresPerm(String guiTitle)
  {
    return ((Boolean)InventoryGUI.guiPerm.get(guiTitle)).booleanValue();
  }
}
