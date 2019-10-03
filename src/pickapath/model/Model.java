package pickapath.model;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
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
	private Element selected = null;
	private SnapShot snapShot = null;
	
	private static final int UNDO_LIMIT = 32;

	//Undo stuff
	private List<Action> actions = new LinkedList<Action>();
	private int actionIndex = -1;
	private boolean undoOrRedo = false;

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
		ORDER_CHANGE,
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
		Element selected;

		public SnapShot() {
			Model model = Model.this;
			prompts = model.prompts;
			choices = model.choices;
			items = model.items;
			dirty = model.dirty;
			itemIdCount = model.itemIdCount;
			selected = model.selected;
		}

		public void restore() {
			Model model = Model.this;
			model.prompts = prompts;
			model.choices = choices;
			model.items = items;
			model.dirty = dirty;
			model.itemIdCount = itemIdCount;
			model.selected = selected;
		}
	}

	private abstract class Action {
		public final Event kind;
		public Action(Event kind) {
			this.kind = kind;
		}

		public abstract void undo();
		public abstract void redo();
		public abstract String getDescription();
	}

	private class CreateAction extends Action {
		private Element element;
		private Element previousSelection; 
		public CreateAction(Element element) {
			super(Event.CREATE);
			this.element = element;
			this.previousSelection = selected;
		}
		@Override
		public void undo() {
			removeElement(previousSelection);
		}
		@Override
		public void redo() {
			if( element instanceof Prompt )
				add((Prompt)element);
			else if( element instanceof Choice ) {
				Choice choice = (Choice)element;
				choice.getStart().addOutgoing(choice);
				choice.getEnd().addIncoming(choice);	
				add(choice);			
			}
		}
		@Override
		public String getDescription() {
			return "Create " + (element instanceof Prompt ? "Prompt" : "Choice");
		}
	}

	private class DeleteAction extends Action {
		private Element element;
		public DeleteAction() {
			super(Event.DELETE);
			this.element = selected;
		}
		@Override
		public void undo() {
			if( element instanceof Prompt ) {
				Prompt prompt = (Prompt)element;
				prompts.add(0, prompt); //it was selected, so it must have been at the front
					
				for(Choice choice: prompt.getIncoming()) {
					choices.add(choice);
					
					//Add choice back to outgoing of start
					List<Choice> startOutgoing = choice.getStart().getOutgoing();
					startOutgoing.add(choice.getOrder() - 1, choice);
					
					//Then reorder all the choices
					for( int i = 0; i < startOutgoing.size(); ++i )
						startOutgoing.get(i).setOrder(i + 1);
				}

				for(Choice choice: prompt.getOutgoing()) {
					choices.add(choice);
					
					//Add choice back to incoming of end
					choice.getEnd().getIncoming().add(choice);
				}
			}
			else if( element instanceof Choice ){
				Choice choice = (Choice)element;
				choices.add(0, choice); //it was selected, so it must have been at the front
				//Add choice back to outgoing of start
				List<Choice> startOutgoing = choice.getStart().getOutgoing();
				startOutgoing.add(choice.getOrder() - 1, choice);
				
				//Then reorder all the choices
				for( int i = 0; i < startOutgoing.size(); ++i )
					startOutgoing.get(i).setOrder(i + 1);
				
				//Add choice back to incoming of end
				choice.getEnd().addIncoming(choice);						
			}
			
			selected = element;		
			updateListeners(Event.CREATE, element, true);
		}
		@Override
		public void redo() {
			removeElement();
		}
		
		@Override
		public String getDescription() {
			return "Delete " + (element instanceof Prompt ? "Prompt" : "Choice");
		}
	}

	private class SelectAction extends Action {
		private Element oldSelection;
		private Element newSelection;
		private int index;
		public SelectAction(Element newSelection, int index) {
			super(Event.SELECT);
			oldSelection = selected;
			this.newSelection = newSelection;
			this.index = index;
		}
		@Override
		public void undo() {
			boolean changed = index != 0;

			if( changed ) {
				if( newSelection instanceof Prompt ) {
					Prompt prompt = (Prompt) newSelection;
					prompts.remove(prompt); //should always be at location 0, but we'll be defensive
					prompts.add(index, prompt);
				
				}	
				else if( newSelection instanceof Choice ) {
					Choice choice = (Choice) newSelection;	
					choices.remove(choice); //should always be at location 0, but we'll be defensive
					choices.add(index, choice);					
				}
			}
			
			selected = oldSelection;
			updateListeners(Event.SELECT, oldSelection, changed);
		}
		@Override
		public void redo() {
			if( newSelection instanceof Prompt )
				selectPrompt(index);
			else if( newSelection instanceof Choice )
				selectChoice(index);
			else
				deselect();
		}
		
		@Override
		public String getDescription() {
			if( newSelection instanceof Prompt )
				return "Select Prompt";
			else if( newSelection instanceof Choice )
				return "Select Choice";
			else
				return "Deselect";
		}
	}

	private class MoveAction extends Action {
		private int oldX;
		private int oldY;
		private int newX;
		private int newY;
		public MoveAction(int newX, int newY) {
			super(Event.MOVE);			
			Prompt prompt = (Prompt) selected;
			oldX = prompt.getX();
			oldY = prompt.getY();
			this.newX = newX;
			this.newY = newY;
		}
		@Override
		public void undo() {			
			setPosition(oldX, oldY, 1.0);
		}
		@Override
		public void redo() {
			setPosition(newX, newY, 1.0);
		}

		public void update(int x, int y) {
			newX = x;
			newY = y;
		}
		
		@Override
		public String getDescription() {			
			return "Move Prompt";
		}		
	}

	private class ColorAction extends Action {
		private Color oldColor;
		private Color newColor;
		public ColorAction(Color newColor) {
			super(Event.RECOLOR);
			Prompt prompt = (Prompt)selected;
			oldColor = prompt.getColor();
			this.newColor = newColor;
		}
		@Override
		public void undo() {
			Prompt prompt = (Prompt)selected;
			prompt.setColor(oldColor);
			updateListeners(Event.RECOLOR, prompt, true);
		}
		@Override
		public void redo() {
			Prompt prompt = (Prompt)selected;
			prompt.setColor(newColor);
			updateListeners(Event.RECOLOR, prompt, true);
		}
		
		@Override
		public String getDescription() {			
			return "Recolor Prompt";
		}
	}

	private class TextAction extends Action {
		private String oldText;
		private String newText;
		public TextAction(String newText) {
			super(Event.TEXT_CHANGE);
			oldText = selected.getText();
			this.newText = newText;
		}
		@Override
		public void undo() {
			setText(oldText);
		}
		@Override
		public void redo() {
			setText(newText);
		}

		public void update(String text) {
			newText = text;
		}
		
		@Override
		public String getDescription() {			
			return  (selected instanceof Prompt ? "Prompt" : "Choice") + " Text Change";
		}
	}

	private class TitleAction extends Action {
		private String oldTitle;
		private String newTitle;
		public TitleAction(String newTitle) {
			super(Event.TITLE_CHANGE);
			this.oldTitle = title;
			this.newTitle = newTitle;
		}
		@Override
		public void undo() {
			setTitle(oldTitle);
		}
		@Override
		public void redo() {
			setTitle(newTitle);
		}
		public void update(String title) {
			newTitle = title;
		}
		
		@Override
		public String getDescription() {			
			return  "Title Change";
		}
	}

	private class CurrencyAction extends Action {
		private String oldCurrency;
		private String newCurrency;
		public CurrencyAction(String newCurrency) {
			super(Event.CURRENCY_CHANGE);
			this.oldCurrency = currencyName;
			this.newCurrency = newCurrency;
		}
		@Override
		public void undo() {
			setCurrencyName(oldCurrency);
		}
		@Override
		public void redo() {
			setCurrencyName(newCurrency);
		}
		public void update(String currency) {
			newCurrency = currency;
		}
		
		@Override
		public String getDescription() {			
			return  "Currency Name Change";
		}
	}

	private class DetailsAction extends Action {
		private SnapShot oldSnapShot;
		private SnapShot newSnapShot;
		public DetailsAction(SnapShot oldSnapShot, SnapShot newSnapShot) {
			super(Event.DETAILS_CHANGE);
			this.oldSnapShot = oldSnapShot;
			this.newSnapShot = newSnapShot;
		}
		@Override
		public void undo() {
			oldSnapShot.restore();
			updateListeners(Event.DETAILS_CHANGE, selected, true);

		}
		@Override
		public void redo() {
			newSnapShot.restore();
			updateListeners(Event.DETAILS_CHANGE, selected, true);
		}
		
		@Override
		public String getDescription() {			
			return  "Choice Details Change";
		}
	}

	private class OrderAction extends Action {
		private int change;
		public OrderAction(int change) {
			super(Event.ORDER_CHANGE);
			this.change = change;
		}
		@Override
		public void undo() {
			changeChoiceOrder(-change);			
		}
		@Override
		public void redo() {
			changeChoiceOrder(change);			
		}
		
		@Override
		public String getDescription() {			
			return  "Choice Order " + (change > 0 ? "Increase" : "Decrease");
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
		snapShot = null;

		dirty = false;
		
		actions.clear();
		actionIndex = -1;
	}

	public void deselect() {
		if( selected != null ) {
			if( !undoOrRedo )
				addAction(new SelectAction(null, 0));
			selected = null;
			updateListeners(Event.SELECT, null, false);
		}
	}

	public Element getSelected() {
		return selected;
	}

	public boolean isDirty() {
		return dirty;
	}

	public Prompt getPrompt(int index) {
		return prompts.get(index);
	}

	public void add(Prompt prompt) {
		if( !undoOrRedo )
			addAction(new CreateAction(prompt));
		prompts.add(0, prompt);
		selected = prompt;
		updateListeners(Event.CREATE, prompt, true);
	}

	public void add(Choice choice) {
		if( !undoOrRedo )
			addAction(new CreateAction(choice));
		choices.add(0, choice);
		selected = choice;
		updateListeners(Event.CREATE, choice, true);
	}

	private void addAction(Action action) {
		//All future updates are lost
		if( actionIndex >= 0 && actionIndex < actions.size() - 1 )
			actions = actions.subList(0, actionIndex + 1);

		//Combining actions
		if( actionIndex >= 0 && action.kind == actions.get(actionIndex).kind &&
				( action.kind == Event.MOVE || action.kind == Event.TEXT_CHANGE || action.kind == Event.TITLE_CHANGE || action.kind == Event.CURRENCY_CHANGE ) ) {

			Action lastAction = actions.get(actionIndex);
			if( action.kind == Event.MOVE ) {
				MoveAction oldMove = (MoveAction)lastAction;
				MoveAction newMove = (MoveAction)action;
				oldMove.update(newMove.newX, newMove.newY);
			}
			else if( action.kind == Event.TEXT_CHANGE ) {
				TextAction oldText = (TextAction)lastAction;
				TextAction newText = (TextAction)action;
				oldText.update(newText.newText);
			}
			else if( action.kind == Event.TITLE_CHANGE ) {
				TitleAction oldTitle = (TitleAction)lastAction;
				TitleAction newTitle = (TitleAction)action;
				oldTitle.update(newTitle.newTitle);
			}
			else if( action.kind == Event.CURRENCY_CHANGE ) {
				CurrencyAction oldCurrency = (CurrencyAction)lastAction;
				CurrencyAction newCurrency = (CurrencyAction)action;
				oldCurrency.update(newCurrency.newCurrency);
			}
		}
		else {
			actions.add(action);
			
			if( actions.size() > UNDO_LIMIT )
				actions = actions.subList(actions.size() - UNDO_LIMIT, actions.size());
			
			actionIndex = actions.size() - 1;
		}
	}

	private void removeElement(Element previousSelection) {
		if( selected instanceof Prompt) {
			Prompt prompt = (Prompt)selected;
			if( prompts.remove(prompt) ) {
				for(Choice choice: prompt.getIncoming()) {
					choices.remove(choice);
					List<Choice> startOutgoing = choice.getStart().getOutgoing();
					choice.getStart().getOutgoing().remove(choice);
					
					//Then reorder all the choices
					for( int i = 0; i < startOutgoing.size(); ++i )
						startOutgoing.get(i).setOrder(i + 1);
				}

				for(Choice choice: prompt.getOutgoing()) {
					choices.remove(choice);
					choice.getEnd().getIncoming().remove(choice);
				}

				selected = previousSelection;
				updateListeners(Event.DELETE, prompt, true);
			}
		}
		else if( selected instanceof Choice) {
			Choice choice = (Choice)selected;
			if( choices.remove(choice) ) {
				List<Choice> startOutgoing = choice.getStart().getOutgoing();
				choice.getStart().getOutgoing().remove(choice);
				
				//Then reorder all the choices
				for( int i = 0; i < startOutgoing.size(); ++i )
					startOutgoing.get(i).setOrder(i + 1);
				
				choice.getEnd().getIncoming().remove(choice);		

				selected = previousSelection;
				updateListeners(Event.DELETE, choice, true);
			}
		}
	}

	public void removeElement() {
		if( !undoOrRedo )
			addAction(new DeleteAction());
		removeElement(null);
	}

	public void changeChoiceOrder(int steps) {
		if( selected instanceof Choice) {
			if( !undoOrRedo )
				addAction(new OrderAction(steps));
			Choice choice = (Choice)selected;
			choice.changeOrder(steps);
			updateListeners(Event.ORDER_CHANGE, choice, true);
		}
	}

	public Choice getChoice(int index) {
		return choices.get(index);
	}

	public int choiceCount() {
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
		if( !undoOrRedo )
			addAction(new TitleAction(title));
		this.title = title;
		updateListeners(Event.TITLE_CHANGE, null, true);
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		if( !undoOrRedo )
			addAction(new CurrencyAction(currencyName));
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

	public void newFlowchart() {
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
		if( selected != prompt ) {
			if( !undoOrRedo )
				addAction(new SelectAction(prompt, index));
			selected = prompt;
			if( index != 0 ) {			
				prompts.remove(index);
				prompts.add(0, prompt);
				updateListeners(Event.SELECT, prompt, true);
			}
			else
				updateListeners(Event.SELECT, prompt, false);
		}
	}

	public void recolorPrompt() {
		if( selected instanceof Prompt ) {
			Prompt prompt = (Prompt) selected;
			Color newColor = Prompt.generateColor();
			if( !undoOrRedo )
				addAction(new ColorAction(newColor));
			prompt.setColor(newColor);
			updateListeners(Event.RECOLOR, prompt, true);
		}
	}

	public void selectChoice(int index) {
		Choice choice = choices.get(index);		
		if( selected != choice ) {			
			if( !undoOrRedo)
				addAction(new SelectAction(choice, index));
			selected = choice;
			if( index != 0 ) {			
				choices.remove(index);
				choices.add(0, choice);
				updateListeners(Event.SELECT, choice, true);
			}
			else
				updateListeners(Event.SELECT, choice, false);
		}
	}

	public void addModelListener(ModelListener listener) {
		modelListeners.add(listener);
	}

	private void updateListeners(Event event, Element object, boolean makeDirty) {
		dirty = dirty || makeDirty;

		for( ModelListener listener : modelListeners )
			listener.updateModel(event, object, undoOrRedo);
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

	public void setPosition(int x, int y, double zoom) {
		if( selected instanceof Prompt ) {
			if( !undoOrRedo )
				addAction(new MoveAction((int)Math.round(x / zoom), (int)Math.round(y / zoom)));
			Prompt prompt = (Prompt) selected;
			prompt.setX(x, zoom);
			prompt.setY(y, zoom);
			updateListeners(Event.MOVE, prompt, true);
		}
	}

	public void setText(String text) {
		if( selected != null ) {
			if( !undoOrRedo && !selected.getText().equals(text) )
				addAction(new TextAction(text));
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

		prompts = new ArrayList<>(snapShot.prompts.size());
		choices = new ArrayList<>(snapShot.choices.size());
		items = new ArrayList<>(snapShot.items.size());

		Map<Prompt, Integer> promptIndexes = new HashMap<>(snapShot.prompts.size() * 2);
		Map<Item, Integer> itemMap = new HashMap<>(snapShot.items.size() * 2);

		//Recreate all prompts in order
		//Make a mapping from old prompts to their indexes in the list
		for( int i = 0; i < snapShot.prompts.size(); ++i ) {
			Prompt prompt = snapShot.prompts.get(i);
			promptIndexes.put(prompt, i);
			prompts.add(new Prompt(prompt));
			if( snapShot.selected == prompt )
				selected = prompts.get(i);
		}


		//Recreate all items in order
		//Make a mapping from old items to their indexes in the list
		for( int i = 0; i < snapShot.items.size(); ++i ) {
			Item item = snapShot.items.get(i);
			itemMap.put(item, i);
			items.add(new Item(item));
		}

		//Recreate all choices
		//Use the maps from prompts to indexes and items to indexes to put
		//put the newly created prompts and items in the new choices
		for( int i = 0; i < snapShot.choices.size(); ++i ) {
			Choice choice = snapShot.choices.get(i);
			choices.add(new Choice(choice, promptIndexes, prompts, itemMap, items));
			if( snapShot.selected == choice )
				selected = choices.get(i);
		}

		dirty = snapShot.dirty;
		itemIdCount = snapShot.itemIdCount;
	}

	public void restoreSnapShot() {
		int rows = items.size();
		if( rows > 0 ) {
			TableModelEvent event = new TableModelEvent(this, 0, rows - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
			for (TableModelListener listener: tableListeners)
				listener.tableChanged(event);
		}

		snapShot.restore();

		rows = items.size();
		if( rows > 0 ) {
			TableModelEvent event = new TableModelEvent(this, 0, rows - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
			for (TableModelListener listener: tableListeners)
				listener.tableChanged(event);
		}
		
		updateListeners(Event.DETAILS_CHANGE, selected, true);
	}

	public void saveDetails(String text) {
		// Supports undos for details changes
		if( selected instanceof Choice) {
			selected.setText(text);
			if( !undoOrRedo )
				addAction(new DetailsAction(snapShot, new SnapShot()));

			updateListeners(Event.DETAILS_CHANGE, selected, true);
		}
	}

	public void setCurrencyChange(int change) {
		if( selected instanceof Choice ) {
			Choice choice = (Choice)selected;
			choice.setCurrencyChange(change);
		}		
	}

	public boolean canUndo() {
		return actionIndex >= 0 && actionIndex < actions.size();
	}
	
	public String getUndoDescription() {
		if( canUndo() ) {
			Action action = actions.get(actionIndex);
			return action.getDescription();
		}
		
		return "";
	}

	public boolean canRedo() {
		return actionIndex >= -1 && actionIndex < actions.size() - 1;
	}
	
	public String getRedoDescription() {
		if( canRedo() ) {
			Action action = actions.get(actionIndex + 1);
			return action.getDescription();
		}
		
		return "";
	}

	public void undo() {
		if( canUndo() ) {
			undoOrRedo = true;
			Action action = actions.get(actionIndex);
			actionIndex--;
			action.undo();
			undoOrRedo = false;
		}
	}

	public void redo() {
		if( canRedo() ) {
			undoOrRedo = true;
			actionIndex++;
			Action action = actions.get(actionIndex);
			action.redo();
			undoOrRedo = false;
		}
	}
}
