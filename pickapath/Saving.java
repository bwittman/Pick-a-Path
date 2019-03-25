package pickapath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class Saving {

	public static void write(File selectedFile, List<Box> boxes, List<Arrow> arrows, List<Item> items) throws FileNotFoundException, IOException{ //write out to a file
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(selectedFile));
		out.writeInt(boxes.size());
		for (Box box: boxes) {	
			box.write(out);
		}

		out.writeInt(items.size());
		for (Item item:items) {
			item.write(out);
		}
		
		out.writeInt(arrows.size());
		for (Arrow arrow:arrows) {
			arrow.write(out, boxes, items);
			
		}
		
		out.close();
		System.out.printf("Saved data is saved in " + selectedFile);
	}

	public static void read(File selectedFile, List<Box> boxes, List<Arrow> arrows, List<Item> items) throws FileNotFoundException, IOException, ClassNotFoundException{  //read in from a file
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(selectedFile));
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
		
		in.close();
		System.out.printf("Saved data is read from " + selectedFile);

	}
	
}
