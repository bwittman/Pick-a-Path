package pickapath.player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Scanner;

import pickapath.model.Arrow;
import pickapath.model.InvalidStartingBoxException;
import pickapath.model.Item;
import pickapath.model.Model;
import pickapath.model.State;

public class PlayerModeCLI {

	private State state;

	private void loadGame(Scanner in) {
		
		while(true) {
			System.out.println();
			System.out.print("Please enter a file to open: ");
			String fileName = in.nextLine().trim();
			File file = new File(fileName);
			if((fileName.toLowerCase().endsWith(".pap") || fileName.toLowerCase().endsWith(".ppp")) ) {
				try {
					ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
					if( file.toString().toLowerCase().endsWith(".ppp")) {
						state = new State(stream);
						return;
					}
					else {
						Model model = new Model();
						model.read(stream);
						
						try {
							state = new State(model);
							return;
						}
						catch(InvalidStartingBoxException e){
							System.out.println("This is an unplayable game because no starting point is indicated.");
						}
					}

				} catch(IOException | ClassNotFoundException e) {
					System.out.println("File missing or corrupted.");
				}

			}
			else {
				System.out.println("File name does not end with .ppp or .pap.");
			}
		}
	}

	public static void main(String[] args) {
		System.out.println("Welcome to Pick a Path!");	

		new PlayerModeCLI();
	}


	//Console mode
	public PlayerModeCLI() {
		Scanner in = new Scanner(System.in);

		loadGame(in);
		
		
		String currencyName = state.getModel().getCurrencyName();
		if( currencyName.trim().isEmpty() )
			currencyName = "Money";
		
		final String DEFAULT_CHOICES = "Or enter I for Inventory, M for " + currencyName + ", S for Save, O for Open, or Q to Quit.";

		System.out.println();
		System.out.println("To play, enter the number that corresponds to the choice you would like to make.");
		System.out.println("To display your inventory, enter I.");
		System.out.println("To display your " + currencyName.toLowerCase() + ", enter M.");
		System.out.println("To save the current state of your game, enter S.");
		System.out.println("To open a previously saved game, enter O.");
		System.out.println("To stop playing a game, enter Q.");		
		System.out.println();
		System.out.println();
		
		System.out.println(state.getModel().getTitle());
		System.out.println();

		
		List<Arrow> choices = state.getChoices();
		while(choices.size() > 0) {
			
			System.out.println(state.getPrompt().getText());
			System.out.println();
			
			for( int i = 0; i < choices.size(); ++i )
				System.out.println((i + 1) + ". " + choices.get(i).getText());
			
			System.out.println(DEFAULT_CHOICES);
			System.out.println();
			System.out.print("Enter choice: ");
			String input = in.nextLine().trim().toUpperCase();

			if( input.equals("S")) {
				saveGame(in);
			}
			else if(input.equals("O")) {
				loadGame(in);
				System.out.println();
				System.out.println(state.getModel().getTitle());
				System.out.println();
			}
			else if( input.equals("Q")) {
				return;
			}
			else if( input.equals("I")) {
				System.out.println();
				if( state.getInventory().size() > 0 ) {				
					System.out.println("Inventory:");
					for (Item item: state.getInventory()) {
						System.out.println("\t"+ item.getName());
					}
				}
				else
					System.out.println("Your inventory is empty.");
			}
			else if(input.contentEquals("M")) {
				System.out.println(currencyName + ": " + state.getCurrency());
			}
			else {
				try {		
					int choice = Integer.parseInt(input)-1;
					if( choice >= 0 && choice < choices.size() ) {
						Arrow arrow = choices.get(choice);
						if( arrow.getLostItems().size() > 0 ) {
							if( arrow.getLostItems().size() > 1 ) 
								System.out.println("You lost the following items:");
							else
								System.out.println("You lost the following item:");
							for( Item item : arrow.getLostItems())
								System.out.println("\t" + item.getName());
						}
						if( arrow.getGainedItems().size() > 0 ) {
							if( arrow.getGainedItems().size() > 1 ) 
								System.out.println("You gained the following items:");
							else
								System.out.println("You gained the following item:");
							for( Item item : arrow.getGainedItems())
								System.out.println("\t" + item.getName());
						}
						if( arrow.getCurrencyChange() > 0)
							System.out.println("Your " + currencyName.toLowerCase() + " increased by " + arrow.getCurrencyChange() + ".");
						else if( arrow.getCurrencyChange() < 0 )
							System.out.println("Your " + currencyName.toLowerCase() + " decreased by " + -arrow.getCurrencyChange() + ".");
						
						state.makeChoice(choice);	
					}
					else
						System.out.println("Invalid choice. Please enter another one.");
				}
				catch(NumberFormatException e) {
					System.out.println("Invalid choice. Please enter another one.");
				}
			}
			
			System.out.println();
		}
		System.out.println(state.getPrompt().getText());
	}

	private void saveGame(Scanner in) {
		while(true) {
			System.out.println();
			System.out.print("Please enter a file to save to: ");
			String fileName = in.nextLine().trim();

			if(!fileName.toLowerCase().endsWith(".ppp") ) {
				fileName += ".ppp";
			}
			File file = new File(fileName);
			boolean safe = true;
			if(file.exists()) {
				System.out.print("Are you sure you want to save over " + fileName + "? (y/n) ");
				String answer = in.nextLine().toLowerCase().trim();
				if(!answer.equals("yes") && !answer.equals("y")) {
					safe = false;
				}			
			}
			if( safe ) {
				try {
					ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
					state.writeProgress(stream);
					stream.close();
					System.out.println("Game successfully saved.");
					System.out.println();
					return;
				}
				catch(IOException e) {
					System.out.println("Unable to save to file.");
				}
			}
		}		
	}
}

