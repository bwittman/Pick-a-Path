package pickapath.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pickapath.Item;

public class State {
	
	private Model model;
	private Box prompt = null;
	private Set<Item> inventory = new HashSet<Item>();
	private int currency = 0;
	private List<Arrow> choices = new ArrayList<Arrow>();
	
	public State(Model model) throws InvalidStartingBoxException {
		this.model = model;
		
		List<Box> startingBoxes = model.getStartingBoxes();
		if( startingBoxes.size() == 1 )
			prompt = startingBoxes.get(0);
		else
			throw new InvalidStartingBoxException();
		
		updateChoices();
	}
	
	public State(ObjectInputStream in) throws FileNotFoundException, ClassNotFoundException, IOException {
		model = new Model();
		readProgress(in);		

		updateChoices();
	}
	
	public void writeProgress(ObjectOutputStream out) throws FileNotFoundException, IOException {
		model.write(out);
		
		out.writeInt(model.getBoxIndex(prompt));	
	
		out.writeInt(inventory.size());
		for(Item item : inventory)
			out.writeInt(model.getItemIndex(item));
		
		out.writeInt(currency);
	}
	
	public void readProgress(ObjectInputStream in) throws FileNotFoundException, IOException, ClassNotFoundException { 
		model.read(in);
		int boxIndex = in.readInt();
		prompt = model.getBox(boxIndex);
				
		inventory.clear();
		int totalItems = in.readInt();
		for (int i = 0;  i < totalItems; i ++) {
			inventory.add(model.getItem(in.readInt()));
		}
		
		currency = in.readInt();
	}
	
	public Set<Item> getInventory() {
		return inventory;
	}
	
	public int getCurrency() {
		return currency;
	}
	
	public Box getPrompt() {
		return prompt;
	}
	
	private void updateChoices() {
		choices.clear();

		for (Arrow arrow : prompt.getOutgoing()) {
			if( arrow.satisfies(inventory, currency) )
				choices.add(arrow);
		}
	}
	
	public List<Arrow> getChoices() {
		return choices;
	}
	
	public Model getModel() {
		return model;
	}
	
	public void makeChoice(int choice) {
		if( choice >= 0 && choice < choices.size() ) {
			Arrow arrow = choices.get(choice);
	
			choices.clear();
			
			//remove items first
			inventory.removeAll(arrow.getLostItems());
			inventory.addAll(arrow.getGainedItems());
			currency += arrow.getCurrencyChange();
			
			prompt = arrow.getEnd();
		}
	}
}
