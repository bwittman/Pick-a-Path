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
	
	public Item(ObjectInputStream in, List<Arrow> arrows) throws IOException, ClassNotFoundException {
		name = (String)in.readObject();
		id = in.readInt();
		
	}
	
	public void write(ObjectOutputStream out, List<Arrow> arrows) throws IOException {
		out.writeObject(name);
		out.writeInt(id);
		
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
