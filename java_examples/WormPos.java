import java.util.*;

public class WormPos {

   public List<List<Integer>> blocks = new ArrayList<List<Integer>>();

   public WormPos(Worm worm) {
   
      for(int k = 0; k < worm.getLength(); k++)
         blocks.add(Arrays.asList(worm.myX, worm.myY + (k * 10)));
   
   }
   
   public int getX(int row) {
   
      int ans = blocks.get(row).get(0);
      return ans;
   
   }
   
   public int getY(int row) {
   
      int ans = blocks.get(row).get(1);
      return ans;
   
   }
   
   public void setX(int row, int x) {
   
      blocks.get(row).set(0, x);
   
   }
   
   public void setY(int row, int y) {
   
      blocks.get(row).set(1, y);
   
   }
   
   public int getSize() {
   
      return blocks.size();
   
   }
   
   public void addBlock(int x, int y) {
   
      blocks.add(Arrays.asList(x, y));
   
   }
   
   public void reset(Worm worm) {
   
      blocks.clear();
      for(int k = 0; k < worm.getLength(); k++)
         blocks.add(Arrays.asList(worm.myX, worm.myY + (k * 10)));

   
   }

}