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

   public Objective objSelf(Scoreboard board)
   {
     Objective objective = board.registerNewObjective("Points", "dummy");
     objective.setDisplaySlot(DisplaySlot.SIDEBAR);
     String headersc = getConfig().getString("cnfg.scoreboard-header");
     objective.setDisplayName(ChatColor.ITALIC + headersc);
     return objective;
   }

   public Objective objSelfTime(Scoreboard board, String headersc)
   {
     Objective objective = board.registerNewObjective("Points", "dummy");
     objective.setDisplaySlot(DisplaySlot.SIDEBAR);
     objective.setDisplayName(ChatColor.ITALIC + headersc);
     return objective;
   }

   Boolean glow = Boolean.valueOf(false);
   int i = 0;
   Location buhu;

   public void onEnable()
   {
     getServer().getPluginManager().registerEvents(this, this);
     System.out.println(getDescription().getName() + " V" + getDescription().getVersion() + " enabled!");

     loadConfig();

     String pluginFolder = getDataFolder().getAbsolutePath();

     new File(pluginFolder).mkdirs();

     this.list = new ListStore(new File(pluginFolder + File.separator + "disabled.txt"));

     this.list.load();
     if (getConfig().getBoolean("cnfg.start-Timer-on-serverstart")) {
       doTimer(getConfig().getInt("cnfg.starttimer.days"), getConfig().getInt("cnfg.starttimer.hours"), getConfig().getInt("cnfg.starttimer.minutes"), getConfig().getInt("cnfg.starttimer.seconds"), getServer().getPlayer("Console"));
     }
     getServer().getScheduler().runTaskTimer(this, new Runnable()
     {
       public void run()
       {
         ScoreboardTimer.this.action();
       }
     }, 20L, 20L);
     if (Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].split(".")[0]) > 1) {
       this.over176 = true;
     } else if ((Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].split(".")[0]) == 1) && (Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].split(".")[1]) >= 7)) {
       this.over176 = true;
     } else if ((Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].split(".")[0]) == 1) && (Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].split(".")[1]) == 7) && (Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].split(".")[2]) >= 6)) {
       this.over176 = true;
     } else {
       this.over176 = false;
     }
   }

   public void onDisable()
   {
     this.list.save();

     System.out.println(getDescription().getName() + " V" + getDescription().getVersion() + " disabled!");
   }

   public void loadConfig()
   {
     FileConfiguration cfg = getConfig();
     cfg.options().copyDefaults(true);
     saveConfig();
   }

   public void action()
   {
     Scoreboard board;
     Objective objective;
     Score score;
     if (this.makesb)
     {
       Player[] onlinePlayerList = Bukkit.getServer().getOnlinePlayers();
       if (this.doTime)
       {
         if (this.globalsec == 0)
         {
           if (this.globalmin >= 1)
           {
             this.globalmin -= 1;
             this.globalsec = 59;
           }
           else if (this.globalhour > 0)
           {
             this.globalhour -= 1;
             this.globalmin = 59;
             this.globalsec = 59;
           }
           else if (this.globalday > 0)
           {
             this.globalday -= 1;
             this.globalhour = 23;
             this.globalmin = 59;
             this.globalsec = 59;
           }
           else
           {
             this.blink = getConfig().getInt("cnfg.stoptime");
             this.doTime = false;
             this.stop = true;
           }
         }
         else {
           this.globalsec -= 1;
         }
       }
       else
       {
         if ((this.stop) &&
           (getConfig().getBoolean("cnfg.do-commands"))) {
           for (int i = 1; i <= getConfig().getInt("cnfg.commands.amount"); i++) {
             this.globalsender.performCommand(getConfig().getString("cnfg.commands.command" + i));
           }
         }
         this.stop = false;
       }
       for (Player player : onlinePlayerList)
       {
         Boolean show = Boolean.valueOf(false);
         if (this.list.contains(player.getName()))
         {
           if (!getConfig().getBoolean("cnfg.toggle")) {
             show = Boolean.valueOf(true);
           }
         }
         else {
           show = Boolean.valueOf(true);
         }
         board = boardSelf();
         objective = objSelf(board);
         if ((this.stop) && (show.booleanValue()))
         {
           boolean stopmsg = getConfig().getBoolean("cnfg.stop-msg");
           if (stopmsg)
           {
             player.sendMessage(ChatColor.WHITE + "#####################");
             player.sendMessage(ChatColor.WHITE + "#" + ChatColor.RED + "000" + ChatColor.WHITE + "#" + ChatColor.RED + "000" + ChatColor.WHITE + "#" + ChatColor.RED + "0000" + ChatColor.WHITE + "#" + ChatColor.RED + "000" + ChatColor.WHITE + "##" + ChatColor.RED + "0" + ChatColor.WHITE + "#");
             player.sendMessage(ChatColor.WHITE + "#" + ChatColor.RED + "0" + ChatColor.WHITE + "###" + ChatColor.WHITE + "#" + ChatColor.RED + "0" + ChatColor.WHITE + "##" + ChatColor.RED + "0" + ChatColor.WHITE + "##" + ChatColor.RED + "0" + ChatColor.WHITE + "#" + ChatColor.RED + "0" + ChatColor.WHITE + "##" + ChatColor.RED + "0" + ChatColor.WHITE + "#" + ChatColor.RED + "0" + ChatColor.WHITE + "#");
             player.sendMessage(ChatColor.WHITE + "#" + ChatColor.RED + "000" + ChatColor.WHITE + "#" + ChatColor.WHITE + "#" + ChatColor.RED + "0" + ChatColor.WHITE + "##" + ChatColor.RED + "0" + ChatColor.WHITE + "##" + ChatColor.RED + "0" + ChatColor.WHITE + "#" + ChatColor.RED + "000" + ChatColor.WHITE + "##" + ChatColor.RED + "0" + ChatColor.WHITE + "#");
             player.sendMessage(ChatColor.WHITE + "###" + ChatColor.RED + "0" + ChatColor.WHITE + "#" + ChatColor.WHITE + "#" + ChatColor.RED + "0" + ChatColor.WHITE + "##" + ChatColor.RED + "0" + ChatColor.WHITE + "##" + ChatColor.RED + "0" + ChatColor.WHITE + "#" + ChatColor.RED + "0" + ChatColor.WHITE + "######");
             player.sendMessage(ChatColor.WHITE + "#" + ChatColor.RED + "000" + ChatColor.WHITE + "#" + ChatColor.WHITE + "#" + ChatColor.RED + "0" + ChatColor.WHITE + "##" + ChatColor.RED + "0000" + ChatColor.WHITE + "#" + ChatColor.RED + "0" + ChatColor.WHITE + "####" + ChatColor.RED + "0" + ChatColor.WHITE + "#");
             player.sendMessage(ChatColor.WHITE + "#####################");
           }
           player.playSound(player.getLocation(), Sound.LEVEL_UP, 10.0F, 1.0F);
           getConfig().getBoolean("cnfg.toggle");
         }
         else if (this.doTime)
         {
           int type = getConfig().getInt("cnfg.type");
           if ((type == 1) || (this.globalday > 0) || (this.globalhour > 0))
           {
             String globalsec2 = this.globalsec;
             if (this.globalsec < 10) {
               globalsec2 = "0" + this.globalsec;
             }
             String msg2 = "";
             if (this.globalday > 0) {
               msg2 = msg2 + this.globalday + " ";
             }
             if ((this.globalhour > 0) || (this.globalday > 0)) {
               msg2 = msg2 + this.globalhour + ":";
             }
             String globalmin2 = this.globalmin;
             if ((this.globalmin < 10) && ((this.globalhour > 0) || (this.globalday > 0))) {
               globalmin2 = "0" + this.globalmin;
             }
             if (show.booleanValue())
             {
               Score score = objective.getScore(ChatColor.GREEN + msg2 + globalmin2 + ":" + globalsec2);
               score.setScore(0);
             }
           }
           else if ((type == 2) &&
             (show.booleanValue()))
           {
             Score score = objective.getScore(ChatColor.GREEN + "Min " + ChatColor.RED + this.globalmin + ChatColor.GREEN + "/Sec");
             score.setScore(this.globalsec);
           }
         }
         else if (this.blink >= 0)
         {
           if ((this.red.containsKey(player.getName())) && (((Boolean)this.red.get(player.getName())).booleanValue()))
           {
             if (show.booleanValue())
             {
               Score score = objective.getScore(ChatColor.RED + getConfig().getString("cnfg.stopmsg"));
               score.setScore(this.blink);

               this.red.put(player.getName(), Boolean.valueOf(false));
             }
           }
           else if (show.booleanValue())
           {
             score = objective.getScore(ChatColor.BOLD + getConfig().getString("cnfg.stopmsg"));
             score.setScore(this.blink);

             this.red.put(player.getName(), Boolean.valueOf(true));
           }
         }
         }