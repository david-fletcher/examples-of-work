/*
http://www.javaprogrammingforums.com/java-programming-tutorials/696-multi-dimension-arraylist-example.html
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Worm {

   private int myLength, direction;
	//0 is up, 1 is down, 2 is left, 3 is right
   public int myX, myY;
   public WormPos blocks;

   public Worm() {
   
      myX = 0;
      myY = 0;
      myLength = 3;
      direction = 0;
      blocks = new WormPos(this);
      
   }

   public Worm(int x, int y) {
   
      myX = x;
      myY = y;
      myLength = 3;
      direction = 0;
      blocks = new WormPos(this);
   
   }

   public void moveUp() {
      
      for(int k = blocks.getSize() - 1; k > 0; k--) {
         
         blocks.setX(k, blocks.getX((k-1)));
         blocks.setY(k, blocks.getY((k-1)));
         
      }
      
      blocks.setY(0, blocks.getY(0) - 10);
   
   }

   public void moveDown() {
      
      for(int k = blocks.getSize() - 1; k > 0; k--) {
         
         blocks.setX(k, blocks.getX((k-1)));
         blocks.setY(k, blocks.getY((k-1)));
         
      }
      
      blocks.setY(0, blocks.getY(0) + 10);
   
   }

   public void moveLeft() {
      
      for(int k = blocks.getSize() - 1; k > 0; k--) {
      
         blocks.setX(k, blocks.getX((k-1)));
         blocks.setY(k, blocks.getY((k-1)));
         
      }
      
      blocks.setX(0, blocks.getX(0) - 10);
   
   }

   public void moveRight() {
      
      for(int k = blocks.getSize() - 1; k > 0; k--) {
         
         blocks.setX(k, blocks.getX((k-1)));
         blocks.setY(k, blocks.getY((k-1)));
         
      }
      
      blocks.setX(0, blocks.getX(0) + 10);
   
   }

   public int getDirection() {
   
      return direction;
   
   }

   public void setDirection(int a) {
   
      if(a < 4)
         direction = a;
   
   }

   public void setLocation(int a, int b) {
   
      myX = a;
      myY = b;
   
   }

   public void addBlock() {
   
      blocks.addBlock(myX, myY);
      myLength++;
   
   }

   public int getX() {
   
      int ans = blocks.getX(0);
      return ans;
   
   }
   
   public int getX(int row) {
   
      int ans = blocks.getX(row);
      return ans;
   
   }

   public int getY() {
   
      int ans = blocks.getY(0);
      return ans;
   
   }
   
   public int getY(int row) {
   
      int ans = blocks.getY(row);
      return ans;
   
   }   

   public int getLength() {
   
      return myLength;
   
   }
   
   public void reset() {
   
      myX = 200;
      myY = 200;
      myLength = 3;
      direction = 0;
      blocks.reset(this);
   
   }

   public void keyTyped(KeyEvent e) {
   
      char dir = e.getKeyChar();
   
      if(dir == 'w')
         setDirection(0);
      if(dir == 's')
         setDirection(1);
      if(dir == 'a')
         setDirection(2);
      if(dir == 'd')
         setDirection(3);
   
   }

   public void move() {
   
      switch(direction) {
      
         case 0: moveUp();
            break;
         case 1: moveDown();
            break;
         case 2: moveLeft();
            break;
         case 3: moveRight();
            break;
      
      }
   
   }

   public void draw(Graphics myBuffer) {
   
      myBuffer.setColor(Color.BLACK);
      
      for(int k = 0; k < myLength; k++) 
         myBuffer.fillRect(blocks.getX(k), blocks.getY(k), 10, 10);
   
   }

}
