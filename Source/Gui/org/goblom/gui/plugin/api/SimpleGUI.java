package org.goblom.gui.plugin.api;

import java.util.List;
import org.bukkit.plugin.Plugin;

public abstract interface SimpleGUI
{
  public abstract GuiAPI getAPI(Plugin paramPlugin);
  
  public abstract List<String> getDevelopers();
}
