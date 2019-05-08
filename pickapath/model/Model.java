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

public class Model implements TableModel {

	private List<Box> boxes = new ArrayList<>();
	private List<Arrow> arrows = new ArrayList<>();
	private List<Item> items = new ArrayList<>();
	private String title = "";
	private String currencyName = "";
	private boolean dirty = false;
	private List<ModelListener> modelListeners = new ArrayList<ModelListener>();
	private ArrayList<TableModelListener> tableListeners = new ArrayList<>();
	private int itemIdCount = 1;
	private CanvasObject selected = null;
	private SnapShot snapShot = null;

	public enum Event {
		CREATE,
		DELETE,
		SELECT,
		MOVE,
		RECOLOR,
		TEXT_CHANGE,
		TITLE_CHANGE,
		CURRENCY_CHANGE,
		DETAILS_CHANGE,
		ORDER_EARLIER,
		ORDER_LATER,
		SAVE,
		LOAD
	}
	
	private class SnapShot {
		List<Box> boxes;
		List<Arrow> arrows;
		List<Item> items;
		boolean dirty;
		int itemIdCount;
		CanvasObject selected = null;
		
		public SnapShot() {
			Model model = Model.this;
			boxes = new ArrayList<>(model.boxes.size());
			arrows = new ArrayList<>(model.arrows.size());
			items = new ArrayList<>(model.items.size());
			
			Map<Box, Integer> boxMap = new HashMap<>(model.boxes.size() * 2);
			Map<Item, Integer> itemMap = new HashMap<>(model.items.size() * 2);
			
			//Recreate all boxes in order
			//Make a mapping from old boxes to their indexes in the list
			for( int i = 0; i < model.boxes.size(); ++i ) {
				Box box = model.boxes.get(i);
				boxMap.put(box, i);
				boxes.add(new Box(box));
				if( model.selected == box )
					selected = boxes.get(i);
			}
			
			
			//Recreate all items in order
			//Make a mapping from old items to their indexes in the list
			for( int i = 0; i < model.items.size(); ++i ) {
				Item item = model.items.get(i);
				itemMap.put(item, i);
				items.add(new Item(item));
			}
			
			//Recreate all arrows
			//Use the maps from boxes to indexes and items to indexes to put
			//put the newly created boxes and items in the new arrows
			for( int i = 0; i < model.arrows.size(); ++i ) {
				Arrow arrow = model.arrows.get(i);
				arrows.add(new Arrow(arrow, boxMap, boxes, itemMap, items));
				if( model.selected == arrow )
					selected = arrows.get(i);
			}
			
			dirty = model.dirty;
			itemIdCount = model.itemIdCount;			
		}
	}

	public void clear() {
		boxes.clear();
		arrows.clear();

		if( items.size() > 0 ) {			
			TableModelEvent event = new TableModelEvent(this, 0, items.size() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
			items.clear();	
			for (TableModelListener listener: tableListeners)
				listener.tableChanged(event);
			
		}
		title = "";
		currencyName = "";
		selected = null;
		
		dirty = false;
		
		updateListeners(Event.SELECT, null, false);
	}

	public void deselect() {
		selected = null;
		updateListeners(Event.SELECT, null, false);
	}

	public CanvasObject getSelected() {
		return selected;
	}

	public boolean isDirty() {
		return dirty;
	}

	public Box getBox(int index) {
		return boxes.get(index);
	}

	public void add(Box box) {
		boxes.add(box);
		selected = box;
		updateListeners(Event.CREATE, box, true);
	}

	public void add(Arrow arrow) {
		arrows.add(arrow);
		selected = arrow;
		updateListeners(Event.CREATE, arrow, true);
	}

	public void removeBox() {		
		if( selected instanceof Box) {
			Box box = (Box)selected;
			if( boxes.remove(box) ) {
				for(Arrow arrow: box.getIncoming()) 
					arrows.remove(arrow);

				for(Arrow arrow: box.getOutgoing())
					arrows.remove(arrow);

				updateListeners(Event.DELETE, box, true);
			}
		}
	}

	public void removeArrow() {	
		if( selected instanceof Arrow) {
			Arrow arrow = (Arrow)selected;
			if( arrows.remove(arrow) ) {
				arrow.getStart().getOutgoing().remove(arrow);
				arrow.getEnd().getIncoming().remove(arrow);

				updateListeners(Event.DELETE, arrow, true);
			}
		}
	}

	public void makeArrowEarlier() {
		if( selected instanceof Arrow) {
			Arrow arrow = (Arrow)selected;
			arrow.makeEarlier();
			updateListeners(Event.ORDER_EARLIER, arrow, true);
		}
	}

	public void makeArrowLater() {
		if( selected instanceof Arrow) {
			Arrow arrow = (Arrow)selected;
			arrow.makeLater();
			updateListeners(Event.ORDER_LATER, arrow, true);
		}
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
		updateListeners(Event.TITLE_CHANGE, null, true);
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
		updateListeners(Event.CURRENCY_CHANGE, null, true);
	}

	public void write(ObjectOutputStream out) throws FileNotFoundException, IOException{ //write out to a file

		out.writeObject(title);
		out.writeObject(currencyName);

		Map<Box, Integer> boxIndexes = new HashMap<>();
		Map<Item, Integer> itemIndexes = new HashMap<>();

		
		out.writeInt(boxes.size());
		for(int i = 0; i < boxes.size(); ++i ) {
			Box box = boxes.get(i);
			boxIndexes.put(box, i);
			box.write(out);
		}

		out.writeInt(items.size());
		for (int i = 0; i < items.size(); ++i ) {
			Item item = items.get(i);
			itemIndexes.put(item, i);
			item.write(out);
		}

		//Write arrows from boxes to preserve their internal ordering
		out.writeInt(arrows.size());
		for (Box box: boxes)
			for( Arrow arrow: box.getOutgoing() )
				arrow.write(out, boxIndexes, itemIndexes);
		
		dirty = false;

		updateListeners(Event.SAVE, null, false);
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


		updateListeners(Event.LOAD, null, false);
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
		Box box = boxes.get(index);
		selected = box;
		if( index != 0 ) {			
			boxes.remove(index);
			boxes.add(0, box);
			updateListeners(Event.SELECT, box, true);
		}
		else
			updateListeners(Event.SELECT, box, false);
	}

	public void recolorBox() {
		if( selected instanceof Box ) {
			Box box = (Box) selected;
			box.recolor();
			updateListeners(Event.RECOLOR, box, true);
		}
	}

	public void selectArrow(int index) {
		Arrow arrow = arrows.get(index);
		selected = arrow;
		if( index != 0 ) {			
			arrows.remove(index);
			arrows.add(0, arrow);
			updateListeners(Event.SELECT, arrow, true);
		}
		else
			updateListeners(Event.SELECT, arrow, false);
	}

	public void addModelListener(ModelListener listener) {
		modelListeners.add(listener);
	}

	private void updateListeners(Event event, CanvasObject object, boolean makeDirty) {
		dirty = dirty || makeDirty;

		for( ModelListener listener : modelListeners )
			listener.updateModel(event, object);
	}

	protected List<Box> getStartingBoxes() {
		List<Box> startingBoxes = new ArrayList<>();
		for (Box box : boxes) {
			//Boxes with nothing incoming or whose only incoming is a self loop can be starting points 
			if (box.getIncoming().isEmpty() || (box.getIncoming().size() == 1 && box.getIncoming().get(0).getStart() == box ))
				startingBoxes.add(box);
		}
		return startingBoxes;
	}

	public void setPosition(Box box, int x, int y, double zoom) {
		box.setX(x, zoom);
		box.setY(y, zoom);
		updateListeners(Event.MOVE, box, true);
	}

	public void setText(String text) {
		if( selected != null ) {
			selected.setText(text);
			updateListeners(Event.TEXT_CHANGE, selected, true);
		}
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
		updateListeners(Event.DETAILS_CHANGE, null, true);
	}

	public void addItem(String itemName) {
		Item item = new Item(itemIdCount,itemName);
		items.add(item);
		itemIdCount++;
		TableModelEvent event = new TableModelEvent(this, items.size() - 1, items.size() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
		for (TableModelListener listener: tableListeners)
			listener.tableChanged(event);
		updateListeners(Event.DETAILS_CHANGE, null, true);
	}
	public void deleteItem (int row) {
		Item item = items.remove(row);
		for(Arrow arrow : arrows)
			arrow.deleteItem(item);
		TableModelEvent event = new TableModelEvent(this, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
		for (TableModelListener listener: tableListeners)
			listener.tableChanged(event);		
		updateListeners(Event.DETAILS_CHANGE, null, true);
	}

	public void addGainedItem(Item item) {
		if( selected instanceof Arrow) {
			Arrow arrow = (Arrow)selected;
			arrow.addGainedItem(item);
			updateListeners(Event.DETAILS_CHANGE, arrow, true);
		}
	}

	public void removeGainedItem(Item item) {
		if( selected instanceof Arrow) {
			Arrow arrow = (Arrow)selected;
			arrow.removeGainedItem(item);
			updateListeners(Event.DETAILS_CHANGE, arrow, true);
		}
	}

	public void addLostItem(Item item) {
		if( selected instanceof Arrow) {
			Arrow arrow = (Arrow)selected;
			arrow.addLostItem(item);
			updateListeners(Event.DETAILS_CHANGE, arrow, true);
		}
	}

	public void removeLostItem(Item item) {
		if( selected instanceof Arrow) {
			Arrow arrow = (Arrow)selected;
			arrow.removeLostItem(item);
			updateListeners(Event.DETAILS_CHANGE, arrow, true);
		}
	}

	public void setBooleanExpression(BooleanExpression expression) {
		if( selected instanceof Arrow) {
			Arrow arrow = (Arrow)selected;
			arrow.setBooleanExpression(expression);
			updateListeners(Event.DETAILS_CHANGE, arrow, true);
		}		
	}
	
	public void makeSnapShot() {
		snapShot = new SnapShot();
	}
	
	public void restoreSnapShot() {
		int rows = items.size();
		if( rows > 0 ) {
			TableModelEvent event = new TableModelEvent(this, 0, rows - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
			for (TableModelListener listener: tableListeners)
				listener.tableChanged(event);
		}
		
		boxes = snapShot.boxes;
		arrows = snapShot.arrows;
		items = snapShot.items;
		dirty = snapShot.dirty;
		itemIdCount = snapShot.itemIdCount;
		selected = snapShot.selected;
		
		rows = items.size();
		if( rows > 0 ) {
			TableModelEvent event = new TableModelEvent(this, 0, rows - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
			for (TableModelListener listener: tableListeners)
				listener.tableChanged(event);
		}
	}

	public void saveDetails() {
		// TODO Use to support undos for details changes
	}
}
