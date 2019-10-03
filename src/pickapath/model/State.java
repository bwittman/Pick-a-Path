package pickapath.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class State {
	
	private Model model;
	private Prompt prompt = null;
	private Set<Item> inventory = new HashSet<Item>();
	private int currency = 0;
	private List<Choice> choices = new ArrayList<Choice>();
	
	public State(Model model) throws InvalidStartingPromptException {
		this.model = model;
		
		List<Prompt> startingPrompts = model.getStartingPrompts();
		if( startingPrompts.size() == 1 )
			prompt = startingPrompts.get(0);
		else
			throw new InvalidStartingPromptException();
		
		updateChoices();
	}
	
	public State(ObjectInputStream in) throws FileNotFoundException, ClassNotFoundException, IOException {
		model = new Model();
		readProgress(in);		

		updateChoices();
	}
	
	public void writeProgress(ObjectOutputStream out) throws FileNotFoundException, IOException {
		model.write(out);
		
		out.writeInt(model.getPromptIndex(prompt));	
	
		out.writeInt(inventory.size());
		for(Item item : inventory)
			out.writeInt(model.getItemIndex(item));
		
		out.writeInt(currency);
	}
	
	public void readProgress(ObjectInputStream in) throws FileNotFoundException, IOException, ClassNotFoundException { 
		model.read(in);
		int promptIndex = in.readInt();
		prompt = model.getPrompt(promptIndex);
				
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
	
	public Prompt getPrompt() {
		return prompt;
	}
	
	private void updateChoices() {
		choices.clear();

		for (Choice choice : prompt.getOutgoing()) {
			if( choice.satisfies(inventory, currency) )
				choices.add(choice);
		}
	}
	
	public List<Choice> getChoices() {
		return choices;
	}
	
	public Model getModel() {
		return model;
	}
	
	public void makeChoice(int index) {
		if( index >= 0 && index < choices.size() ) {
			Choice choice = choices.get(index);
			
			// Remove items first
			inventory.removeAll(choice.getLostItems());
			inventory.addAll(choice.getGainedItems());
			currency += choice.getCurrencyChange();
			
			prompt = choice.getEnd();
			
			updateChoices();			
		}
	}
}
