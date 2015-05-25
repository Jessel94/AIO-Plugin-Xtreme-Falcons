/*  1:   */ package com.hogeschool.AIOplugin.Scoreboard.util;
/*  2:   */ 
/*  3:   */ import java.io.BufferedReader;
/*  4:   */ import java.io.BufferedWriter;
/*  5:   */ import java.io.DataInputStream;
/*  6:   */ import java.io.File;
/*  7:   */ import java.io.FileInputStream;
/*  8:   */ import java.io.FileWriter;
/*  9:   */ import java.io.IOException;
/* 10:   */ import java.io.InputStreamReader;
/* 11:   */ import java.util.ArrayList;
/* 12:   */ 
/* 13:   */ public class ListStore
/* 14:   */ {
/* 15:   */   private File storageFile;
/* 16:   */   private ArrayList<String> values;
/* 17:   */   
/* 18:   */   public ListStore(File file)
/* 19:   */   {
/* 20:20 */     this.storageFile = file;
/* 21:21 */     this.values = new ArrayList();
/* 22:23 */     if (!this.storageFile.exists()) {
/* 23:   */       try
/* 24:   */       {
/* 25:25 */         this.storageFile.createNewFile();
/* 26:   */       }
/* 27:   */       catch (IOException e)
/* 28:   */       {
/* 29:27 */         e.printStackTrace();
/* 30:   */       }
/* 31:   */     }
/* 32:   */   }
/* 33:   */   
/* 34:   */   public void load()
/* 35:   */   {
/* 36:   */     try
/* 37:   */     {
/* 38:34 */       DataInputStream input = new DataInputStream(new FileInputStream(this.storageFile));
/* 39:35 */       BufferedReader reader = new BufferedReader(new InputStreamReader(input));
/* 40:   */       String line;
/* 41:39 */       while ((line = reader.readLine()) != null)
/* 42:   */       {
/* 43:   */
/* 44:40 */         if (!contains(line)) {
/* 45:41 */           this.values.add(line);
/* 46:   */         }
/* 47:   */       }
/* 48:45 */       reader.close();
/* 49:46 */       input.close();
/* 50:   */     }
/* 51:   */     catch (Exception e)
/* 52:   */     {
/* 53:49 */       e.printStackTrace();
/* 54:   */     }
/* 55:   */   }
/* 56:   */   
/* 57:   */   public void save()
/* 58:   */   {
/* 59:   */     try
/* 60:   */     {
/* 61:55 */       FileWriter stream = new FileWriter(this.storageFile);
/* 62:56 */       BufferedWriter out = new BufferedWriter(stream);
/* 63:58 */       for (String value : this.values)
/* 64:   */       {
/* 65:59 */         out.write(value);
/* 66:60 */         out.newLine();
/* 67:   */       }
/* 68:63 */       out.close();
/* 69:64 */       stream.close();
/* 70:   */     }
/* 71:   */     catch (IOException e)
/* 72:   */     {
/* 73:67 */       e.printStackTrace();
/* 74:   */     }
/* 75:   */   }
/* 76:   */   
/* 77:   */   public boolean contains(String value)
/* 78:   */   {
/* 79:72 */     return this.values.contains(value);
/* 80:   */   }
/* 81:   */   
/* 82:   */   public void add(String value)
/* 83:   */   {
/* 84:76 */     if (!contains(value)) {
/* 85:77 */       this.values.add(value);
/* 86:   */     }
/* 87:   */   }
/* 88:   */   
/* 89:   */   public void remove(String value)
/* 90:   */   {
/* 91:82 */     this.values.remove(value);
/* 92:   */   }
/* 93:   */   
/* 94:   */   public ArrayList<String> getValues()
/* 95:   */   {
/* 96:86 */     return this.values;
/* 97:   */   }
/* 98:   */ }
