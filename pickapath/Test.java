package pickapath;
import javax.swing.*;
import java.awt.*;



public class Test {
	public static void main(String[] args) {

		try {
			// Set cross-platform Java L&F (also called "Metal")
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} 
		catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			// handle exception
		}



		JFrame frame = new JFrame("BorderLayout Example");
		JMenuBar bar = new JMenuBar();  //menu bar
		JMenu file = new JMenu("File"); //file button
		bar.add(file);
		JMenuItem save = new JMenuItem("Save");  //save button
		file.add(save);
		JMenu edit = new JMenu("Edit");  //edit button
		bar.add(edit);
		JMenuItem undo = new JMenuItem("Undo");  //undo button
		edit.add(undo);

		JFrame frame1 = new JFrame("PIK A PATH"); //title of window
		frame1.setJMenuBar(bar);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JButton("North"), BorderLayout.NORTH);
		JPanel modes = new JPanel(new GridLayout(0,2));
		modes.add(new JButton("Editor Mode")); //Shows you are in editor mode
		modes.add(new JButton("Player Mode")); //Puts you in player mode
		panel.add(modes, BorderLayout.NORTH); //assigns the boxes to the north container
		frame1.add(panel);

		JPanel numbers = new JPanel(new GridLayout(5,0)); //how many buttons there are on the right side, needs adjusting if adding a button
		panel.add(new JButton("East"), BorderLayout.EAST); //right container in GUI
		panel.add(new JPanel(), BorderLayout.CENTER);
		numbers.add(new JButton("Make Box")); //make box button
		
		JButton arrowButton = new JButton("Make Arrow");
		arrowButton.setEnabled(false);
		
		numbers.add(arrowButton); //make arrow button
		numbers.add(new JButton("Delete")); // delete button (for boxes and arrows)
		numbers.add(new JButton("Add Text")); // add text button
		numbers.add(new JButton("Delete All")); // delete all button
		panel.add(numbers, BorderLayout.EAST); //assigns the boxes to the right container
		frame1.add(panel);
		frame1.setSize(800,700); //size of window
		frame1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //this closes the GUI
		frame1.setVisible(true); //allows the GUI to start as visible
		// panel.add(new JButton("South"), BorderLayout.SOUTH); We can use this to add a bottom container if we want
		// panel.add(new JButton("West"), BorderLayout.WEST); We can use this to add a left container if we want
	}

}



