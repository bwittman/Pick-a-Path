package pickapath;

/**
 * @author jimer
 * This is to create the jTable and HashMap for the creation of in game items.
 */
public class Item {
	private final int id;
	private String name;
	
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
	


}
