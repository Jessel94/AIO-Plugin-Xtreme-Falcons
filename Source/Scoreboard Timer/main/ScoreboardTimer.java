package main;

import java.io.File;
import java.io.PrintStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import util.ListStore;

public class ScoreboardTimer
  extends JavaPlugin
  implements Listener
{
  public ScoreboardManager manager()
  {
    ScoreboardManager manager = Bukkit.getScoreboardManager();
    return manager;
  }
  
  public int globalobcount = 0;
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
    Scoreboard board = manager().getNewScoreboard();
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
      Player[] arrayOfPlayer1;
      int k = (arrayOfPlayer1 = onlinePlayerList).length;
      for (int j = 0; j < k; j++)
      {
        Player player = arrayOfPlayer1[j];
        
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
        else
        {
          this.makesb = false;
          if ((getConfig().getBoolean("cnfg.auto-restart")) && (!this.forcestop)) {
            doTimer(this.globalday, this.globalhour, this.globalmin, this.globalsec, this.globalsender);
          }
        }
        player.setScoreboard(board);
      }
      this.blink -= 1;
      this.i += 1;
    }
    else if (getConfig().getBoolean("cnfg.showClock"))
    {
      Date date = new Date();
      Scoreboard board = boardSelf();
      Objective objective = objSelfTime(board, getConfig().getString("cnfg.nameClock"));
      
      Score score2 = objective.getScore(" " + convertTime(date));
      score2.setScore(0);
      
      Player[] onlinePlayerList = Bukkit.getServer().getOnlinePlayers();
      objective = (score = onlinePlayerList).length;
      for (board = 0; board < objective; board++)
      {
        Player player = score[board];
        
        Scoreboard board2 = boardSelf();
        player.setScoreboard(board2);
        if (this.list.contains(player.getName()))
        {
          if (!getConfig().getBoolean("cnfg.toggle")) {
            player.setScoreboard(board);
          }
        }
        else {
          player.setScoreboard(board);
        }
      }
    }
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
    if ((cmd.getName().equalsIgnoreCase("Timer")) && 
      ((sender instanceof Player)) && (sender.hasPermission("SetTimer"))) {
      if (args.length == 1) {
        doTimer(0, 0, Integer.parseInt(args[0]), 0, (Player)sender);
      } else if (args.length == 2) {
        doTimer(0, 0, Integer.parseInt(args[0]), Integer.parseInt(args[1]), (Player)sender);
      } else {
        return false;
      }
    }
    if ((cmd.getName().equalsIgnoreCase("Timeh")) && 
      ((sender instanceof Player)) && (sender.hasPermission("SetTimer"))) {
      if (args.length == 1) {
        doTimer(Integer.parseInt(args[0]), 0, 0, 0, (Player)sender);
      } else if (args.length == 2) {
        doTimer(Integer.parseInt(args[0]), Integer.parseInt(args[1]), 0, 0, (Player)sender);
      } else if (args.length == 3) {
        doTimer(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), 0, (Player)sender);
      } else if (args.length == 4) {
        doTimer(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), (Player)sender);
      } else {
        return false;
      }
    }
    if ((cmd.getName().equalsIgnoreCase("Timerstop")) && (sender.hasPermission("StopTimer")) && 
      ((sender instanceof Player)))
    {
      this.blink = getConfig().getInt("cnfg.stoptime");
      this.stop = true;
      this.doTime = false;
      this.forcestop = true;
      sendAll(ChatColor.BLUE + "[Timer]" + ChatColor.RED + " stopped by " + sender.getName() + "!");
    }
    if ((cmd.getName().equalsIgnoreCase("Timertoggle")) && 
      ((sender instanceof Player))) {
      if (getConfig().getBoolean("cnfg.toggle"))
      {
        if (this.list.contains(sender.getName()))
        {
          this.list.remove(sender.getName());
          sender.sendMessage(ChatColor.BLUE + "[Timer]" + ChatColor.DARK_PURPLE + " toggled on!");
        }
        else
        {
          this.list.add(sender.getName());
          sender.sendMessage(ChatColor.BLUE + "[Timer]" + ChatColor.DARK_PURPLE + " toggled off!");
        }
      }
      else {
        sender.sendMessage(ChatColor.BLUE + "[Timer]" + ChatColor.RED + " toggeling Timer is disabled!");
      }
    }
    if (cmd.getName().equalsIgnoreCase("tt")) {
      (sender instanceof Player);
    }
    return true;
  }
  
  public void sendAll(String msg)
  {
    Bukkit.broadcastMessage(msg);
  }
  
  private void doTimer(int day, int hour, int min, int sec, Player sender)
  {
    this.globalsender = sender;
    
    this.forcestop = false;
    this.doTime = true;
    this.makesb = true;
    this.blink = getConfig().getInt("cnfg.stoptime");
    
    this.globalday = day;
    this.globalhour = hour;
    this.globalmin = min;
    this.globalsec = sec;
    
    this.globalday2 = day;
    this.globalhour2 = hour;
    this.globalmin2 = min;
    this.globalsec2 = sec;
    
    sendAll(ChatColor.BLUE + "[Timer]" + ChatColor.GREEN + " started by " + sender.getName() + "!");
  }
  
  public String convertTime(Date date)
  {
    Format format = new SimpleDateFormat("HH:mm:ss");
    return format.format(date).toString();
  }
}
