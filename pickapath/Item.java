package pickapath;

import java.util.List;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author jimer
 * This is to create the jTable and HashMap for the creation of in game items.
 */
public class Item {
	private final int id;
	private String name;
	
	
	public String toString() {
		return id + ": " + name;
	}
	public Item (int id,String name) {
		this.id = id;
		this.name = name;
		
	}
	
	public int getId() {
		return  id;
	}
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Item(ObjectInputStream in) throws IOException, ClassNotFoundException {
		id = in.readInt();
		name = (String)in.readObject();
		
		
	}
	
	public void write(ObjectOutputStream out) throws IOException {
		out.writeInt(id);
		out.writeObject(name);
		
		
/*		int startIndex = -1;
		int endIndex = -1;
		for(int i = 0; i < arrows.size(); i++) {
			if (arrows.get(i) == start) {
				startIndex = i;
			}
			if (boxes.get(i) == end) {
				endIndex = i;
			}
		}*/
/*		out.writeInt(startIndex);
		out.writeInt(endIndex);*/
	}


}
