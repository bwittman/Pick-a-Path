package pickapath.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author jimer
 * This class is used to hold the ID and name for in-game items.
 */
public class Item {
	private final int id;
	private String name;
	
	public String toString() {
		return id + ": " + name;
	}
	public Item(int id,String name) {
		this.id = id;
		this.name = name;		
	}
	
	public Item(Item other) {
		id = other.id;
		name = other.name;
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
	
	public Item(ObjectInputStream in) throws IOException, ClassNotFoundException {	//read in item info from a file
		id = in.readInt();
		name = (String)in.readObject();
	}
	
	public void write(ObjectOutputStream out) throws IOException {	//write out item info to file
		out.writeInt(id);
		out.writeObject(name);
	}
}
