package pickapath.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import pickapath.Item;

public class Model implements TableModel {

	private List<Box> boxes = new ArrayList<>();
	private List<Arrow> arrows = new ArrayList<>();
	private List<Item> items = new ArrayList<>();
	private String title = "";
	private String currencyName = "";
	private boolean dirty = false;
	private List<DirtyListener> dirtyListeners = new ArrayList<DirtyListener>();
	private ArrayList<TableModelListener> tableListeners = new ArrayList<>();
	private int itemIdCount = 1;

	public void clear() {
		boxes.clear();
		arrows.clear();

		if( items.size() > 0 ) {
			items.clear();	
			TableModelEvent event = new TableModelEvent(this, 0, items.size() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
			for (TableModelListener listener: tableListeners)
				listener.tableChanged(event);
		}
		title = "";
		currencyName = "";
		setDirty(false);
	}

	public boolean isDirty() {
		return dirty;
	}

	public Box getBox(int index) {
		return boxes.get(index);
	}

	public void add(Box box) {
		boxes.add(box);
		setDirty(true);
	}

	public void add(Arrow arrow) {
		arrows.add(arrow);
		setDirty(true);
	}

	public void removeBox(Box box) {
		if( boxes.remove(box) ) {
			for(Arrow arrow: box.getIncoming()) 
				arrows.remove(arrow);

			for(Arrow arrow: box.getOutgoing())
				arrows.remove(arrow);

			setDirty(true);
		}		
	}

	public void removeArrow(Arrow arrow) {		
		if( arrows.remove(arrow) ) {
			arrow.getStart().getOutgoing().remove(arrow);
			arrow.getEnd().getIncoming().remove(arrow);

			setDirty(true);
		}
	}

	public void makeEarlier(Arrow arrow) {
		arrow.makeEarlier();
		setDirty(true);
	}

	public void makeLater(Arrow arrow) {
		arrow.makeLater();
		setDirty(true);
	}

	//Intentionally package private
	List<Item> getItems() {
		return items;
	}	

	public Arrow getArrow(int index) {
		return arrows.get(index);
	}

	public int arrowCount() {
		return arrows.size();
	}

	public int boxCount() {
		return boxes.size();
	}

	public Item getItem(int index) {
		return items.get(index);
	}

	public Item getItemById(int id) {
		for( Item item : items) {
			if( item.getId() == id ) 
				return item;
		}

		return null;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		setDirty(true);
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
		setDirty(true);
	}

	public void write(ObjectOutputStream out) throws FileNotFoundException, IOException{ //write out to a file

		out.writeObject(title);
		out.writeObject(currencyName);

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

		setDirty(false);
	}


	public void read(ObjectInputStream in) throws FileNotFoundException, IOException, ClassNotFoundException{  //read in from a file
		clear();

		title = (String)in.readObject(); //title
		currencyName = (String)in.readObject(); //currency

		int boxCount = in.readInt();
		for (int i = 0; i < boxCount; ++i )
			boxes.add(new Box(in));

		int itemCount = in.readInt();
		for (int i = 0; i <  itemCount; ++i)
			items.add(new Item(in));
		if( items.size() > 0 )
			itemIdCount = items.get(items.size() - 1).getId() + 1;
		else
			itemIdCount = 1;
		
		int arrowCount = in.readInt();
		for (int i = 0; i <  arrowCount; ++i)
			arrows.add(new Arrow(in, this));

		setDirty(false);
	}

	protected int getBoxIndex(Box box) {
		for (int i = 0; i < boxes.size(); i++) {
			if (boxes.get(i) == box)
				return i;
		}

		return -1;
	}


	protected int getItemIndex(Item item) {
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i) == item)
				return i;
		}

		return -1;
	}

	public void selectBox(int index) {
		if( index != 0 ) {
			Box box = boxes.get(index);
			boxes.remove(index);
			boxes.add(0, box);
			setDirty(true);
		}		
	}

	public void recolorBox(Box box) {
		box.recolor();
		setDirty(true);		
	}

	public void selectArrow(int index) {
		if( index != 0 ) {
			Arrow arrow = arrows.get(index);
			arrows.remove(index);
			arrows.add(0, arrow);
			setDirty(true);
		}		
	}

	public void addDirtyListener(DirtyListener listener) {
		dirtyListeners.add(listener);
	}

	private void setDirty(boolean dirty) {
		this.dirty = dirty;

		for( DirtyListener listener : dirtyListeners )
			listener.changeDirtiness(dirty);
	}

	protected List<Box> getStartingBoxes() {
		List<Box> startingBoxes = new ArrayList<Box>();
		for (Box box : boxes) {
			if (box.getIncoming().isEmpty())
				startingBoxes.add(box);
		}
		return startingBoxes;
	}

	public void setX(Box box, int x, double zoom) {
		box.setX(x, zoom);
		setDirty(true);
	}

	public void setY(Box box, int y, double zoom) {
		box.setY(y, zoom);
		setDirty(true);
	}

	public void setText(CanvasObject selected, String text) {
		selected.setText(text);
		setDirty(true);		
	}


	//Table stuff
	@Override
	public void addTableModelListener(TableModelListener listener) {
		tableListeners.add(listener);
	}

	@Override
	public Class<?> getColumnClass(int column) {
		if( column == 0 )
			return Integer.class;
		else if( column == 1)
			return String.class;
		return null;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(int column) {
		if( column == 0 )
			return "Item Number";
		else if( column == 1)
			return "Item Name";
		return null;
	}

	@Override
	public int getRowCount() {
		return items.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		Item item = items.get(row);
		if (column ==0) {
			return item.getId();
		} else if (column == 1) {
			return item.getName();
		} else {
			return null;
		}
	}
	@Override
	public boolean isCellEditable(int row, int column) {
		return column == 1 && row >= 0 && row < items.size();
	}

	@Override
	public void removeTableModelListener(TableModelListener listener) {
		tableListeners.remove(listener);
	}

	@Override
	public void setValueAt(Object object, int row, int column) {
		Item item = items.get(row);

		if (column == 1) {
			item.setName((String) object);

			TableModelEvent event = new TableModelEvent(this, row, row, column,TableModelEvent.UPDATE);
			for (TableModelListener listener: tableListeners)
				listener.tableChanged(event);
		}
	}

	//Increments itemIdCount
	public void addItem(String itemName) {
		Item item = new Item(itemIdCount,itemName);
		items.add(item);
		itemIdCount ++;
		TableModelEvent event = new TableModelEvent(this, items.size() - 1, items.size() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
		for (TableModelListener listener: tableListeners)
			listener.tableChanged(event);
		setDirty(true);
	}
	public void deleteItem (int row) {
		Item item = items.remove(row);
		for(Arrow arrow : arrows)
			arrow.deleteItem(item);
		TableModelEvent event = new TableModelEvent(this, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
		for (TableModelListener listener: tableListeners)
			listener.tableChanged(event);		
		setDirty(true);
	}

	public void addGainedItem(Arrow arrow, Item item) {
		arrow.addGainedItem(item);
		setDirty(true);
	}

	//Package private
	public void removeGainedItem(Arrow arrow, Item item) {
		arrow.removeGainedItem(item);
		setDirty(true);
	}

	//Package private
	public void addLostItem(Arrow arrow, Item item) {
		arrow.addLostItem(item);
		setDirty(true);
	}

	//Package private
	public void removeLostItems(Arrow arrow, Item item) {
		arrow.removeLostItem(item);
		setDirty(true);
	}
}
