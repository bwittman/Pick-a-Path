package pickapath;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PlayerModeCLI {


	public static void main(String[] args) {

		System.out.println("Welcome to Pick a Path!");
		System.out.println("To play, enter the number that corresponds to the choice you would like to make.");
		System.out.println("To display your items, enter I");
		System.out.println("To save the current state of your game, enter S");
		

		Scanner in = new Scanner(System.in);


		//File file = new File("/Users/logan/Desktop/simple.pap");

		List<Box> boxes = new ArrayList<Box>();
		List<Arrow> arrows = new ArrayList<Arrow>();
		List<Item> items = new ArrayList<Item>();
		boolean successful = false;
		while( !successful) {
			System.out.print("Please enter a file to open: ");
			String fileName = in.nextLine();
			File file = new File(fileName);
			if(file.exists()) {
				try {
					Saving.read(file, boxes, arrows, items);
					successful = true;
				} catch (ClassNotFoundException | IOException e) {

				}	
			}

			if(!successful) {
				System.out.println("File missing or corrupted.");
			}
		}


		List<Box> startingBoxes = Main.getStartingBoxes(boxes);
		if (startingBoxes.size() == 1) {	

			new PlayerModeCLI(startingBoxes.get(0), in);

		} else {
			System.out.println(
					"This is an unplayable game because no starting point is indicated.");
		};



		/*       
        Console console = Console.getConsole();
        if(file.exists()) console.open(file);

        else {
        	 System.out.println("file failed to load");
        }
		 */
		


	}




	//Console mode
	public PlayerModeCLI(Box box, Scanner in) {

		System.out.println();
		while(box.getOutgoing().size() > 0) {
			int counter = 1;
			System.out.println(box.getText());
			for (Arrow arrow : box.getOutgoing()) {
				System.out.println(counter+ ". " + arrow.getText());
				counter++;


			}
			System.out.println();
			System.out.print("Enter choice: ");
			String input = in.next().toLowerCase();

			if( input.equals("s")) {
				//do save
			}
			else if( input.equals("i")) {
				//display items
			}
			else {
				try {		
					int choice = Integer.parseInt(input)-1;
					if( choice >= 0 && choice < box.getOutgoing().size() ) {
						Arrow arrow = box.getOutgoing().get(choice);
						box = arrow.getEnd();
					}
					else
						System.out.println("Invalid choice. Please enter another one.");
				}
				catch(NumberFormatException e) {
					System.out.println("Invalid choice. Please enter another one.");
				}



			}

			System.out.println(box.getText());


		}


	}
}
