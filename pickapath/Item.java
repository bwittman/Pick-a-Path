package pickapath;

import java.util.HashMap;
import java.util.Set;
import javax.swing.JTable;
/**
 * @author jimer
 * This is to create the jTable and HashMap for the creation of in game items.
 */
public class Item {
	String [] columnNames = {"Item Number", "Item Name"};
	Object [][] itemInfo = {};// Need to figure out how to let the user input item data 
	HashMap<String, Set<String>>map = new HashMap<>(); // Does the HashMap populate the table?
	JTable table = new JTable(itemInfo, columnNames);
		


}
