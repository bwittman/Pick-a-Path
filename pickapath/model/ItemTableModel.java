package pickapath.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import pickapath.Item;

public class ItemTableModel implements TableModel {

	private ArrayList<TableModelListener> listeners = new ArrayList<>();
	private List<Item> items = new ArrayList<Item>();
	private int itemIdCount = 1;

	public ItemTableModel() {
	}
	
	public void setItemList(List<Item> items) {
		this.items = items;
		if( items.size() == 0 )
			itemIdCount = 1;
		else
			itemIdCount = items.get(items.size() - 1).getId() + 1;
	}

	@Override
	public void addTableModelListener(TableModelListener listener) {
		listeners.add(listener);
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
		listeners.remove(listener);
	}

	@Override
	public void setValueAt(Object object, int row, int column) {
		Item item = items.get(row);

		if (column == 1) {
			item.setName((String) object);

			TableModelEvent event = new TableModelEvent(this, row, row, column,TableModelEvent.UPDATE);
			for (TableModelListener listener: listeners)
				listener.tableChanged(event);
		}
	}
	
	//Increments itemIdCount
	public void addItem(String item) {
		Item addedItem = new Item(itemIdCount,item);
		items.add(addedItem);
		itemIdCount ++;
		TableModelEvent event = new TableModelEvent(this, items.size() - 1, items.size() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
		for (TableModelListener listener: listeners)
			listener.tableChanged(event);
	}
	public void deleteItem (int row) {
		items.remove(row);
		TableModelEvent event = new TableModelEvent(this, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
		for (TableModelListener listener: listeners)
			listener.tableChanged(event);
	}
	public List<Item> getItems() {
		return items;
	}

	public void clear() {
		items.clear();		
	}
}
