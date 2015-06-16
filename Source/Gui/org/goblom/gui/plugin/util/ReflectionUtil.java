package org.goblom.gui.plugin.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtil
{
  public static boolean doesClassExist(ClassType type, String name)
  {
    Class<?> clazz;
    try
    {
      clazz = Class.forName(type.getPackage() + "." + name);
    }
    catch (ClassNotFoundException e)
    {
      clazz = null;
    }
    return clazz != null;
  }
  
  public static Class<?> getClass(ClassType type, String name)
  {
    Class<?> clazz = null;
    try
    {
      clazz = Class.forName(type.getPackage() + "." + name);
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    }
    return clazz;
  }
  
  public static Class<?> getCraftClass(String name)
  {
    Class<?> clazz = null;
    try
    {
      clazz = Class.forName("org.bukkit.craftbukkit." + getVersion() + "." + name);
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    }
    return clazz;
  }
  
  public static Class<?> getNMSClass(String name)
  {
    Class<?> clazz = null;
    try
    {
      clazz = Class.forName("net.minecraft.server." + getVersion() + "." + name);
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    }
    return clazz;
  }
  
  public static <T> T getField(Object o, String fieldName)
  {
    Class<?> checkClass = o.getClass();
    do
    {
      try
      {
        Field field = checkClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T)field.get(o);
      }
      catch (NoSuchFieldException e)
      {
        e.printStackTrace();
      }
      catch (IllegalAccessException e)
      {
        e.printStackTrace();
      }
    } while ((checkClass.getSuperclass() != Object.class) && ((checkClass = checkClass.getSuperclass()) != null));
    return null;
  }
  
  public static Method getMethod(Class<?> clazz, String method)
  {
    for (Method m : clazz.getMethods()) {
      if (m.getName().equals(method))
      {
        m.setAccessible(true);
        return m;
      }
    }
    return null;
  }
  
  private static String getVersion()
  {
    return org.bukkit.Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
  }
  
  public static enum ClassType
  {
    NMS("net.minecraft.server", ReflectionUtil.access$100()),  CRAFT("org.bukkit.craftbukkit", ReflectionUtil.access$100());
    
    private final String pakage;
    private final String version;
    
    private ClassType(String pakage, String version)
    {
      this.pakage = pakage;
      this.version = version;
    }
    
    private String getPackage()
    {
      return this.pakage + "." + this.version;
    }
  }
}
