package pickapath;

import java.util.ArrayList;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import javafx.util.Pair;

public class ItemTableModel implements TableModel {
	
	private TableModelListener listener;
	ArrayList<Pair<Integer, String>> items = new ArrayList<>();

	@Override
	public void addTableModelListener(TableModelListener listener) {
		this.listener = listener;

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
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	//Work in progress
	public Object getValueAt(int row, int column) {
		// TODO Auto-generated method stub
		if (items.get(row).getKey() != 0) {
			return items.size();
		} else {
			return 0;}
		}
	

	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeTableModelListener(TableModelListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setValueAt(Object arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
