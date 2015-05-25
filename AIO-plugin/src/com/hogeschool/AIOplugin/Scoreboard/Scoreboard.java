package com.hogeschool.AIOplugin.Scoreboard;

import com.hogeschool.AIOplugin.Scoreboard.util.ListStore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import com.hogeschool.AIOplugin.Main;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.HashMap;
import java.util.Map;

public class Scoreboard
        extends JavaPlugin
        implements Listener {

    public ScoreboardManager manager()
    {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        return manager;
    }

    public int globalcount = 0;
    public boolean doTime = false;
    public int globalday = 0;
    public int globalhour = 0;
    public int globalmin = 0;
    public int globalsec = 0;
    public int globalday2 = 0;
    public int globalhour2 = 0;
    public int globalmin2 = 0;
    public int globalsec2 = 0;
    public Player globalsender;
    public boolean stop = false;
    public boolean globalstopmsg = false;
    public boolean forcestop = false;
    Map<String, Boolean> red = new HashMap();
    public boolean makesb = false;
    public int blink = 100;
    public ListStore list;
    private boolean over176;

    public Scoreboard boardSelf()
    {
        Scoreboard board = (Scoreboard) manager().getNewScoreboard();
        return board;
    }

}