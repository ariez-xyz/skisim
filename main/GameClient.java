/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.MouseInfo;
import java.util.ArrayList;

/**
 *
 * @author Lee Song
 */
public class GameClient extends Client{
  
  ArrayList<String> obstacles = new ArrayList(); //"XY.Z", X = object type 0-9, Y = y coordinate 0-720, Z = x coordinate 0-640 
  GameClientGUI gui;
  int playerX = 320;
  int playerY = 35;
  int score = 0;
  int baseVerticalSpeed = 1;
  int spriteNum = 2;
  boolean exit = false;
  
  public GameClient(String pIP, int pPort) {
    super(pIP, pPort);
    gui = new GameClientGUI(this);
    gui.lel = 42;
  }
  
  
  
  public void update() {
    for(int i = 0; i < obstacles.size(); i++){
      String s = obstacles.get(i);
      int type = Integer.parseInt(s.substring(0, 1));
      int yCoord = (Integer.parseInt(getYCoord(s))) - (int) baseVerticalSpeed;
      int xCoord = Integer.parseInt(getXCoord(s));
      
      if(yCoord > -64) {
        String updatedValues = type + "" + yCoord + "." + xCoord;
        obstacles.set(i, updatedValues);
      } else 
      obstacles.remove(i);
    } 
    score++;
  }
  
  @Override
  public void processMessage(String pMessage){
    ArrayList<String> data = new ArrayList(); //dir playerx playery crash
    
    if(pMessage.equals("exit")){
      exit = true;
      System.out.println("Score: " + score);
    }
    else {
      for(int i = 0; i < pMessage.length(); i++){
        String dataString = "";
        for(int j = i; j < pMessage.length(); j++){
          if(pMessage.charAt(j) == '.') {
            data.add(dataString);
            i = j + 1;
            dataString = "";
          }
          else {
            dataString += pMessage.charAt(j);
          }
        }
      }
      
      try{
        playerX = Integer.parseInt(data.get(1)) / 2;
        spriteNum = Integer.parseInt(data.get(0));
      } catch (Exception e) {
        System.err.println("Parsing error");
      }
      /*try {
      gui.draw(); 
      } catch(Exception e) {System.out.println(e);}  */ 
    }
    System.out.println("aktuell: " + playerX);
  }
  
  public void newObstacle(char c) {
    int mouseRelativeX = 0;
    int mouseRelativeY = 0;
    try {
      mouseRelativeX = MouseInfo.getPointerInfo().getLocation().x - gui.getLocation().x;
      mouseRelativeY = MouseInfo.getPointerInfo().getLocation().y - gui.getLocation().y;
    } catch (Exception e) {
      System.out.println(e);
    }
    
    if(mouseRelativeX < 0)
    mouseRelativeX = 0;
    else if(mouseRelativeX > 320)
    mouseRelativeX = 320;
    
    if(mouseRelativeY < 240)
    mouseRelativeY = 240;
    else if(mouseRelativeY > 720)
    mouseRelativeY = 720;
    
    String s = "1700.0";
    String t = "1700.0";
    
    switch (c) {
      case '1': s = ("1" + mouseRelativeY * 2 + "." + mouseRelativeX * 2);
      t = ("1" + mouseRelativeY + "." + mouseRelativeX);
      break;
      case '2': s = ("2" + mouseRelativeY * 2 + "." + mouseRelativeX * 2);
      t = ("2" + mouseRelativeY + "." + mouseRelativeX);
      break;
      case '3': s = ("3" + mouseRelativeY * 2 + "." + mouseRelativeX * 2);
      t = ("3" + mouseRelativeY + "." + mouseRelativeX);
      break;
    }
    
    send(s);
    obstacles.add(t);
  }
  
  public String getYCoord(String s){
    String coord = "";
    for(int i = 1; i < s.length(); i++){
      if(s.charAt(i) == '.')
      break;
      coord += s.charAt(i);
    }
    return coord;
  }
  
  public String getXCoord(String s){
    String coord = "";
    for(int i = 2; i < s.length(); i++){
      if(s.charAt(i) == '.'){
        for(int j = i + 1; j < s.length(); j++){
          coord += s.charAt(j);
        }
        i = s.length() + 1;
      }
    }
    return coord;
  }
}
