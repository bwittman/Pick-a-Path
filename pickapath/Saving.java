package pickapath;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Saving {

	public static void write(ObjectOutputStream out, List<Box> boxes, List<Arrow> arrows, List<Item> items) throws FileNotFoundException, IOException{ //write out to a file
		
		Map<Box, Integer> boxIndexes = new HashMap<>();
		
		out.writeInt(boxes.size());
		for(int i = 0; i < boxes.size(); ++i ) {
			Box box = boxes.get(i);
			boxIndexes.put(box, i);
			box.write(out);
		}

		out.writeInt(items.size());
		for (Item item:items)
			item.write(out);
		
		//Write arrows from boxes to preserve their internal ordering
		out.writeInt(arrows.size());
		for (Box box: boxes)
			for( Arrow arrow: box.getOutgoing() )
				arrow.write(out, boxIndexes, items);
	}
	
	public static void writeProgress(ObjectOutputStream out, List<Box> boxes, List<Arrow> arrows, List<Item> items, Box current, Set<Item> itemsHeld) throws FileNotFoundException, IOException {
		write(out,boxes,arrows,items);
		 for (int i = 0; i < boxes.size(); i++) {
			if (boxes.get(i) == current) {
				out.writeInt(i);
				break;
			}
			
		}
		out.writeInt(itemsHeld.size());
		for(Item item : itemsHeld) {
			for (int i = 0; i < items.size(); i++) {
				if (items.get(i) == item) {
					out.writeInt(i);
					break;
				}
				
			}
		}	
	}
	
	public static Box readProgress(ObjectInputStream in, List<Box> boxes, List<Arrow> arrows, List<Item> items , Set<Item> itemsHeld) throws FileNotFoundException, IOException, ClassNotFoundException { 
		read(in,boxes,arrows,items);
		int boxNumber = in.readInt();
		int totalItems = in.readInt();
		for (int i =0;  i < totalItems; i ++) {
			int itemNumber = in.readInt();
			itemsHeld.add(items.get(itemNumber));
		}
		return boxes.get(boxNumber);
	}
	

	public static void read(ObjectInputStream in, List<Box> boxes, List<Arrow> arrows, List<Item> items) throws FileNotFoundException, IOException, ClassNotFoundException{  //read in from a file
		
		int boxCount = in.readInt();
		boxes.clear();
		for (int i = 0; i < boxCount; ++i ) {
			boxes.add(new Box(in));
		}

		int itemCount = in.readInt();
		items.clear();
		for (int i = 0; i <  itemCount; ++i) {
			items.add(new Item(in));
		}
		
		int arrowCount = in.readInt();
		arrows.clear();
		for (int i = 0; i <  arrowCount; ++i) {
			arrows.add(new Arrow(in, boxes, items));
		}
	}	
}
