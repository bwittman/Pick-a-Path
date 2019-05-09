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

	private List<Prompt> prompts = new ArrayList<>();
	private List<Choice> choices = new ArrayList<>();
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
		NEW,
		SAVE,
		LOAD
	}
	
	private class SnapShot {
		List<Prompt> prompts;
		List<Choice> choices;
		List<Item> items;
		boolean dirty;
		int itemIdCount;
		CanvasObject selected = null;
		
		public SnapShot() {
			Model model = Model.this;
			prompts = new ArrayList<>(model.prompts.size());
			choices = new ArrayList<>(model.choices.size());
			items = new ArrayList<>(model.items.size());
			
			Map<Prompt, Integer> promptIndexes = new HashMap<>(model.prompts.size() * 2);
			Map<Item, Integer> itemMap = new HashMap<>(model.items.size() * 2);
			
			//Recreate all prompts in order
			//Make a mapping from old prompts to their indexes in the list
			for( int i = 0; i < model.prompts.size(); ++i ) {
				Prompt prompt = model.prompts.get(i);
				promptIndexes.put(prompt, i);
				prompts.add(new Prompt(prompt));
				if( model.selected == prompt )
					selected = prompts.get(i);
			}
			
			
			//Recreate all items in order
			//Make a mapping from old items to their indexes in the list
			for( int i = 0; i < model.items.size(); ++i ) {
				Item item = model.items.get(i);
				itemMap.put(item, i);
				items.add(new Item(item));
			}
			
			//Recreate all choices
			//Use the maps from prompts to indexes and items to indexes to put
			//put the newly created prompts and items in the new choices
			for( int i = 0; i < model.choices.size(); ++i ) {
				Choice choice = model.choices.get(i);
				choices.add(new Choice(choice, promptIndexes, prompts, itemMap, items));
				if( model.selected == choice )
					selected = choices.get(i);
			}
			
			dirty = model.dirty;
			itemIdCount = model.itemIdCount;			
		}
	}

	private void clear() {
		prompts.clear();
		choices.clear();

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

	public Prompt getPrompt(int index) {
		return prompts.get(index);
	}

	public void add(Prompt prompt) {
		prompts.add(prompt);
		selected = prompt;
		updateListeners(Event.CREATE, prompt, true);
	}

	public void add(Choice choice) {
		choices.add(choice);
		selected = choice;
		updateListeners(Event.CREATE, choice, true);
	}

	public void removePrompt() {		
		if( selected instanceof Prompt) {
			Prompt prompt = (Prompt)selected;
			if( prompts.remove(prompt) ) {
				for(Choice choice: prompt.getIncoming()) 
					choices.remove(choice);

				for(Choice choice: prompt.getOutgoing())
					choices.remove(choice);

				updateListeners(Event.DELETE, prompt, true);
			}
		}
	}

	public void removeArrow() {	
		if( selected instanceof Choice) {
			Choice choice = (Choice)selected;
			if( choices.remove(choice) ) {
				choice.getStart().getOutgoing().remove(choice);
				choice.getEnd().getIncoming().remove(choice);

				updateListeners(Event.DELETE, choice, true);
			}
		}
	}

	public void makeArrowEarlier() {
		if( selected instanceof Choice) {
			Choice choice = (Choice)selected;
			choice.makeEarlier();
			updateListeners(Event.ORDER_EARLIER, choice, true);
		}
	}

	public void makeArrowLater() {
		if( selected instanceof Choice) {
			Choice choice = (Choice)selected;
			choice.makeLater();
			updateListeners(Event.ORDER_LATER, choice, true);
		}
	}

	public Choice getArrow(int index) {
		return choices.get(index);
	}

	public int arrowCount() {
		return choices.size();
	}

	public int promptCount() {
		return prompts.size();
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

		Map<Prompt, Integer> promptIndexes = new HashMap<>();
		Map<Item, Integer> itemIndexes = new HashMap<>();

		
		out.writeInt(prompts.size());
		for(int i = 0; i < prompts.size(); ++i ) {
			Prompt prompt = prompts.get(i);
			promptIndexes.put(prompt, i);
			prompt.write(out);
		}

		out.writeInt(items.size());
		for (int i = 0; i < items.size(); ++i ) {
			Item item = items.get(i);
			itemIndexes.put(item, i);
			item.write(out);
		}

		//Write choices from prompts to preserve their internal ordering
		out.writeInt(choices.size());
		for (Prompt prompt: prompts)
			for( Choice choice: prompt.getOutgoing() )
				choice.write(out, promptIndexes, itemIndexes);
		
		dirty = false;

		updateListeners(Event.SAVE, null, false);
	}
	
	public void newProject() {
		clear();
		updateListeners(Event.NEW, null, false);
	}


	public void read(ObjectInputStream in) throws FileNotFoundException, IOException, ClassNotFoundException{  //read in from a file
		clear();

		title = (String)in.readObject(); //title
		currencyName = (String)in.readObject(); //currency

		int promptCount = in.readInt();
		for (int i = 0; i < promptCount; ++i )
			prompts.add(new Prompt(in));

		int itemCount = in.readInt();
		for (int i = 0; i <  itemCount; ++i)
			items.add(new Item(in));
		if( items.size() > 0 )
			itemIdCount = items.get(items.size() - 1).getId() + 1;
		else
			itemIdCount = 1;

		int arrowCount = in.readInt();
		for (int i = 0; i <  arrowCount; ++i)
			choices.add(new Choice(in, this));


		updateListeners(Event.LOAD, null, false);
	}

	protected int getPromptIndex(Prompt prompt) {
		for (int i = 0; i < prompts.size(); i++) {
			if (prompts.get(i) == prompt)
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

	public void selectPrompt(int index) {
		Prompt prompt = prompts.get(index);
		selected = prompt;
		if( index != 0 ) {			
			prompts.remove(index);
			prompts.add(0, prompt);
			updateListeners(Event.SELECT, prompt, true);
		}
		else
			updateListeners(Event.SELECT, prompt, false);
	}

	public void recolorPrompt() {
		if( selected instanceof Prompt ) {
			Prompt prompt = (Prompt) selected;
			prompt.recolor();
			updateListeners(Event.RECOLOR, prompt, true);
		}
	}

	public void selectArrow(int index) {
		Choice choice = choices.get(index);
		selected = choice;
		if( index != 0 ) {			
			choices.remove(index);
			choices.add(0, choice);
			updateListeners(Event.SELECT, choice, true);
		}
		else
			updateListeners(Event.SELECT, choice, false);
	}

	public void addModelListener(ModelListener listener) {
		modelListeners.add(listener);
	}

	private void updateListeners(Event event, CanvasObject object, boolean makeDirty) {
		dirty = dirty || makeDirty;

		for( ModelListener listener : modelListeners )
			listener.updateModel(event, object);
	}

	protected List<Prompt> getStartingPrompts() {
		List<Prompt> startingPrompts = new ArrayList<>();
		for (Prompt prompt : prompts) {
			//Prompts with no choices incoming or whose only incoming choice is a self-loop can be starting points 
			if (prompt.getIncoming().isEmpty() || (prompt.getIncoming().size() == 1 && prompt.getIncoming().get(0).getStart() == prompt ))
				startingPrompts.add(prompt);
		}
		return startingPrompts;
	}

	public void setPosition(Prompt prompt, int x, int y, double zoom) {
		prompt.setX(x, zoom);
		prompt.setY(y, zoom);
		updateListeners(Event.MOVE, prompt, true);
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
		for(Choice choice : choices)
			choice.deleteItem(item);
		TableModelEvent event = new TableModelEvent(this, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
		for (TableModelListener listener: tableListeners)
			listener.tableChanged(event);		
		updateListeners(Event.DETAILS_CHANGE, null, true);
	}

	public void addGainedItem(Item item) {
		if( selected instanceof Choice) {
			Choice choice = (Choice)selected;
			choice.addGainedItem(item);
			updateListeners(Event.DETAILS_CHANGE, choice, true);
		}
	}

	public void removeGainedItem(Item item) {
		if( selected instanceof Choice) {
			Choice choice = (Choice)selected;
			choice.removeGainedItem(item);
			updateListeners(Event.DETAILS_CHANGE, choice, true);
		}
	}

	public void addLostItem(Item item) {
		if( selected instanceof Choice) {
			Choice choice = (Choice)selected;
			choice.addLostItem(item);
			updateListeners(Event.DETAILS_CHANGE, choice, true);
		}
	}

	public void removeLostItem(Item item) {
		if( selected instanceof Choice) {
			Choice choice = (Choice)selected;
			choice.removeLostItem(item);
			updateListeners(Event.DETAILS_CHANGE, choice, true);
		}
	}

	public void setBooleanExpression(BooleanExpression expression) {
		if( selected instanceof Choice) {
			Choice choice = (Choice)selected;
			choice.setBooleanExpression(expression);
			updateListeners(Event.DETAILS_CHANGE, choice, true);
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
		
		prompts = snapShot.prompts;
		choices = snapShot.choices;
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
		if( selected instanceof Choice) {
			Choice choice = (Choice)selected;
			updateListeners(Event.DETAILS_CHANGE, choice, true);
		}
	}

	public void setCurrencyChange(int change) {
		if( selected instanceof Choice) {
			Choice choice = (Choice)selected;
			choice.setCurrencyChange(change);
		}		
	}
}
