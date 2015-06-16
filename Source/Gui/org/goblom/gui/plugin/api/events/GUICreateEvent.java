package org.goblom.gui.plugin.api.events;

import org.goblom.gui.plugin.handler.menu.InvMenu;

public class GUICreateEvent
  extends GUIEvent
{
  private final String name;
  private final String fileName;
  private final InvMenu gui;
  
  public GUICreateEvent(String fileName, String name, InvMenu gui)
  {
    this.fileName = fileName;
    this.name = name;
    this.gui = gui;
  }
  
  public String getGUITitle()
  {
    return this.name;
  }
  
  public InvMenu getGUI()
  {
    return this.gui;
  }
  
  public String getFileName()
  {
    return this.fileName;
  }
}
