package pickapath;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class PlayerModeCLI {

	private Set<Item> items;

	private static File openFile(Scanner in) {

		while( true) {
			System.out.print("Please enter a file to open: ");
			String fileName = in.nextLine().trim();
			File file = new File(fileName);
			if(file.exists() && (fileName.toLowerCase().endsWith(".pap") || fileName.toLowerCase().endsWith(".ppp")) ) {
				return file;
			}


			System.out.println("File missing or corrupted.");

		}
	}




	public static void main(String[] args) {

		System.out.println("Welcome to Pick a Path!");
		System.out.println("To play, enter the number that corresponds to the choice you would like to make.");
		System.out.println("To display your items, enter I");
		System.out.println("To save the current state of your game, enter S");
		System.out.println("To load a saved game, enter L");
		System.out.println("To stop playing a game, enter Q");


		Scanner in = new Scanner(System.in);


		//File file = new File("/Users/logan/Desktop/simple.pap");

		List<Box> boxes = new ArrayList<Box>();
		List<Arrow> arrows = new ArrayList<Arrow>();
		List<Item> items = new ArrayList<Item>();
		Set<Item> itemsHeld = new HashSet<Item>();
		File file = openFile(in);
		try {
			ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
			Box box = null;
			if( file.toString().toLowerCase().endsWith(".ppp"))
					box = Saving.readProgress(stream, boxes, arrows, items, itemsHeld);
			else {
					Saving.read(stream, boxes, arrows, items);
					List<Box> startingBoxes = Main.getStartingBoxes(boxes);
					if (startingBoxes.size() == 1) {	

						box = startingBoxes.get(0);

					} else {
						System.out.println(
								"This is an unplayable game because no starting point is indicated.");
						return;
					}

			}
			
			new PlayerModeCLI(box, in, itemsHeld);
		
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Corrupted file.");
			return;
		}
		
		
		
		
		


		/*       
        Console console = Console.getConsole();
        if(file.exists()) console.open(file);

        else {
        	 System.out.println("file failed to load");
        }
		 */



	}


	//Console mode
	public PlayerModeCLI(Box box, Scanner in, Set<Item> itemsHeld) {


		items = itemsHeld;
		List<Arrow> choices = new ArrayList<Arrow>();
		System.out.println();
		while(box.getOutgoing().size() > 0) {
			choices.clear();
			int counter = 1;
			System.out.println(box.getText());
			for (Arrow arrow : box.getOutgoing()) {

				//if statement for list of items 
				if( arrow.satisfies(items) ) {
					choices.add(arrow);
					System.out.println(counter+ ". " + arrow.getText());
					counter++;
					//		 = new JRadioButton(arrow.getText());  //i dont think this is right
				}


			}
			System.out.println("Or enter I for items, S for save, L for load, Q to quit.");
			System.out.println();
			System.out.print("Enter choice: ");
			String input = in.nextLine().toUpperCase();
			System.out.println();

			if( input.equals("S")) {
				//do save

			}

			else if(input.equals("L")) {
				File file = openFile(in);
				List<Box> boxes = new ArrayList<Box>();
				List<Arrow> arrows = new ArrayList<Arrow>();
				List<Item> items = new ArrayList<Item>();
				this.items.clear();
				
				try {
					ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
					if( file.toString().toLowerCase().endsWith(".ppp"))
							box = Saving.readProgress(stream, boxes, arrows, items, this.items);
					else {
							Saving.read(stream, boxes, arrows, items);
							List<Box> startingBoxes = Main.getStartingBoxes(boxes);
							if (startingBoxes.size() == 1) {	

								box = startingBoxes.get(0);

							} else {
								System.out.println(
										"This is an unplayable game because no starting point is indicated.");
								return;
							}

					}
					
				
				} catch (IOException | ClassNotFoundException e) {
					System.out.println("Corrupted file.");
					return;
				}


			}

			else if( input.equals("Q")) {
				return;
			}

			else if( input.equals("I")) {

				if( items.size() > 0 ) {				
					System.out.println("Items:");
					for (Item item: items) {
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

						items.addAll(arrowItems);
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
}
