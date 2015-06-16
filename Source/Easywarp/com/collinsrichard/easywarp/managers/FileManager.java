package com.collinsrichard.easywarp.managers;

import com.collinsrichard.easywarp.EasyWarp;
import com.collinsrichard.easywarp.objects.Warp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class FileManager
{
  public static File getWarpsFile()
  {
    String fName = "warps.data";
    return new File("plugins/" + EasyWarp.name + "/" + fName);
  }
  
  public static void loadWarps()
  {
    File file = getWarpsFile();
    if (file.exists()) {
      try
      {
        ois = new ObjectInputStream(new FileInputStream(file.getAbsolutePath()));
        Object result = ois.readObject();
        
        ois.close();
        if (result != null)
        {
          ArrayList<String> parse = (ArrayList)result;
          for (String i : parse) {
            try
            {
              WarpManager.addWarp(WarpManager.deserialize(i));
              
              ois.close();
            }
            catch (Exception e)
            {
              System.out.println("Easy Warp had an error loading warps.");
            }
          }
        }
      }
      catch (Exception e)
      {
        ObjectInputStream ois;
        System.out.println("Easy Warp had an error loading warps.");
      }
    }
  }
  
  public static void saveWarps()
  {
    File file = getWarpsFile();
    
    ArrayList<String> format = new ArrayList();
    for (Warp w : WarpManager.getWarpObjects()) {
      format.add(w.toString());
    }
    new File("plugins/").mkdir();
    new File("plugins/" + EasyWarp.name + "/").mkdir();
    if (!file.exists()) {
      try
      {
        file.createNewFile();
      }
      catch (IOException e)
      {
        System.out.println("Easy Warp had an error saving warps.");
      }
    }
    try
    {
      ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file.getAbsolutePath()));
      oos.writeObject(format);
      oos.flush();
      oos.close();
    }
    catch (Exception e)
    {
      System.out.println("Easy Warp had an error saving warps.");
    }
  }
}
