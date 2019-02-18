/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.MouseInfo;
import java.util.ArrayList;
import java.awt.Rectangle;

/**
 *
 * @author Lee Song  & Daniel Siegel
 */
public class GameServer extends Server{
  
  ArrayList<String> obstacles = new ArrayList(); //"XY.Z", X = object type 0-9, Y = y coordinate 0-1440, Z = x coordinate 0-640 
  int baseHorizontalSpeed = 5;
  int baseVerticalSpeed = 2;
  int playerX = 320;
  int playerY = 70;
  int spriteNum = 2;
  boolean exit = false;
  GameServerGUI gui = new GameServerGUI(this);
  Thread loopThread;
  int send = 0;
  
  public GameServer(int pPortNr) {
    super(pPortNr);
    this.loopThread = new Thread(gameLoop);
    System.out.println(this.toString());
    //sample obstacles.add("1152.74");
  }
  
  
  Runnable gameLoop = new Runnable() {
    @Override
    public void run() {
      try {
        while(!exit){
          update();
          gui.draw();
          Thread.sleep(16L);
        }
      } catch(Exception e) {}
    }
  };
  
  @Override
  public void processMessage(String pClientIP, int pClientPort, String pMessage) {
    //client msg new obstacle XY.Z x type y/z coords
    //type 1 - big rock type 2 - small rock type 3 - tree
    //server msg X.Y.Z. x current sprite y current speed z playerx
    System.out.println("received: " + pMessage);
    obstacles.add(pMessage);
  }
  
  //TODO DRAW OBSTACLES
  
  /*
  Update player position 
  update position d. Obstacles
  entferne ggf. obstacles wenn ausserhalb des spielfelds
  pruefe auf Kollision
  */
  public void update(){
    String data = ""; //dir playerx playery crash
    
    //set direction
    float direction;
    int mouseRelativeX = MouseInfo.getPointerInfo().getLocation().x - gui.getLocation().x - playerX;
    
    direction = (float) mouseRelativeX / 320;
    
    if(direction > 1)
    direction = 1;
    else if(direction < -1)
    direction = -1;
    
    spriteNum = (int) (direction * 2);
    
    data += spriteNum + ".";
    
    //update player position
    playerX += baseHorizontalSpeed * direction;
    
    if(playerX > 576)
    playerX = 576;
    else if(playerX < 0)
    playerX = 0;
    
    data += playerX + ".";
    
    /*
    Obstacle-Update und anschliessende Pruefung d. Kollision*/
    for(int i = 0; i < obstacles.size(); i++){
      String s = obstacles.get(i);
      int type = Integer.parseInt(s.substring(0, 1));
      int yCoord = (Integer.parseInt(getYCoord(s))) - (int) baseVerticalSpeed;
      int xCoord = Integer.parseInt(getXCoord(s));
      
      int height = 0; 
      int width = 0;
      
      if(yCoord > -64) {
        String updatedValues = type + "" + yCoord + "." + xCoord;
        obstacles.set(i, updatedValues);
      } else 
      obstacles.remove(i);
      //holt hhe und Breite je nach Typ des Hindernisses
      if(type == 1){
        height = gui.rock1.getHeight();
        width = gui.rock1.getWidth();
      } else if(type == 2){
        height = gui.rock2.getHeight();
        width = gui.rock2.getWidth();
      } else if(type == 3){
        height = gui.rock1.getHeight();
        width = gui.rock1.getWidth();
      }
      // teh real player obstacle collision //rect1 = Spieler rect2= derzeitiges Objekt
      Rectangle player = new Rectangle(playerX+16,playerY,32,64);
      Rectangle currentobstacle = new Rectangle(xCoord,yCoord,width,height);
      if (player.intersects(currentobstacle)) {
        System.out.println("Hit detected");
        this.exit();
      } // end of if
    }
//    if (send%5==0) {
//      sendToAll(data);
//    }  end of if
//    send++;
  }
  
  /*
  Hindernis wird als String bergeben 
  
  */
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
  
  public void exit() {
    exit = true;
    sendToAll("exit");
  }
}
