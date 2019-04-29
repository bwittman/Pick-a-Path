package pickapath.player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import pickapath.Arrow;
import pickapath.Box;
import pickapath.Item;
import pickapath.Saving;
import pickapath.editor.Editor;

public class PlayerModeCLI {

	private List<Box> boxes = new ArrayList<Box>();
	private List<Arrow> arrows = new ArrayList<Arrow>();
	private List<Item> items = new ArrayList<Item>();
	private Set<Item> itemsHeld = new HashSet<Item>();

	private Box loadGame(Scanner in) {


		while(true) {
			System.out.print("Please enter a file to open: ");
			String fileName = in.nextLine().trim();
			File file = new File(fileName);
			if((fileName.toLowerCase().endsWith(".pap") || fileName.toLowerCase().endsWith(".ppp")) ) {
				try {
					ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
					if( file.toString().toLowerCase().endsWith(".ppp")) {
						return Saving.readProgress(stream, boxes, arrows, items, itemsHeld);

					}
					else {
						Saving.read(stream, boxes, arrows, items);
						List<Box> startingBoxes = Editor.getStartingBoxes(boxes);
						if (startingBoxes.size() == 1) {	

							return startingBoxes.get(0);


						} else {
							System.out.println(
									"This is an unplayable game because no starting point is indicated.");

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
		System.out.println("To play, enter the number that corresponds to the choice you would like to make.");
		System.out.println("To display your items, enter I");
		System.out.println("To save the current state of your game, enter S");
		System.out.println("To open a saved game, enter O");
		System.out.println("To stop playing a game, enter Q");



		new PlayerModeCLI();

	}


	//Console mode
	public PlayerModeCLI() {

		Scanner in = new Scanner(System.in);

		Box box = loadGame(in);


		List<Arrow> choices = new ArrayList<Arrow>();
		System.out.println();
		while(box.getOutgoing().size() > 0) {
			choices.clear();
			int counter = 1;
			System.out.println(box.getText());
			for (Arrow arrow : box.getOutgoing()) {

				//if statement for list of items 
				if( arrow.satisfies(itemsHeld) ) {
					choices.add(arrow);
					System.out.println(counter+ ". " + arrow.getText());
					counter++;
					//		 = new JRadioButton(arrow.getText());  //i dont think this is right
				}


			}
			System.out.println("Or enter I for items, S for save, O for open, Q to quit.");
			System.out.println();
			System.out.print("Enter choice: ");
			String input = in.nextLine().toUpperCase();
			System.out.println();

			if( input.equals("S")) {
				saveGame(in,box);


			}

			else if(input.equals("O")) {
				items.clear();
				boxes.clear();
				arrows.clear();
				itemsHeld.clear();
				box = loadGame(in);


			}

			else if( input.equals("Q")) {
				return;
			}

			else if( input.equals("I")) {

				if( itemsHeld.size() > 0 ) {				
					System.out.println("Items:");
					for (Item item: itemsHeld) {
						System.out.println("\t"+ item.getName());
					}
				}
				else
					System.out.println("You have no items.");
				System.out.println();
			}
			else {
				try {		
					int choice = Integer.parseInt(input)-1;
					if( choice >= 0 && choice < choices.size() ) {
						Arrow arrow = choices.get(choice);
						Set<Item> arrowItems = arrow.getItems();

						itemsHeld.addAll(arrowItems);
						box = arrow.getEnd();
					}
					else
						System.out.println("Invalid choice. Please enter another one.");
				}
				catch(NumberFormatException e) {
					System.out.println("Invalid choice. Please enter another one.");
				}



			}




		}
		System.out.println(box.getText());

	}




	private void saveGame(Scanner in, Box box) {
		while(true) {
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
			if (safe) {
				try {
					ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
					Saving.writeProgress(stream, boxes, arrows, items, box, itemsHeld);
					stream.close();
					System.out.println("Game successfully saved.");
					System.out.println();
					return;



				} catch(IOException e) {
					System.out.println("Unable to save to file.");
				}
			}
			
			
		}
		
	}

}

