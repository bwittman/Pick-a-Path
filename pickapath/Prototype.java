package pickapath;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.*;



public class Prototype {

	private JTextArea textArea;
	
	public static void main(String[] args) {

		try {
			// Set cross-platform Java L&F (also called "Metal")
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} 
		catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			// handle exception
		}

		new Prototype();
		
	}
	
	public Prototype() {
		
		List<Box> boxes = new ArrayList<Box>();
		JFrame frame = new JFrame("PICK A PATH"); //title of window 
		
		
		JMenuBar bar = new JMenuBar();  //menu bar
		JMenu file = new JMenu("File"); //file button
		
		bar.add(file);

		JMenuItem nproject = new JMenuItem("New Project");  //new project button
		file.add(nproject);
		nproject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//deleteAllBoxes();
			}
		});
		
		JMenuItem openp = new JMenuItem("Open Project");  //open project button
		file.add(openp);
		openp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				    File selectedFile = chooser.getSelectedFile();
				    JOptionPane.showMessageDialog(frame, "You Selected: " + selectedFile);
				}
			}
		});

		JMenuItem save = new JMenuItem("Save");  //save button
		file.add(save);
		save.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String filename = JOptionPane.showInputDialog("Enter a filename");
						JOptionPane.showMessageDialog(frame, "you entered: " + filename);
					}
				}				
				);
		
		
		
		JMenuItem exit = new JMenuItem("Exit");  //save button
		
		
		
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}});
		file.add(exit);
		JMenu edit = new JMenu("Edit");  //edit button
		bar.add(edit);
		JMenuItem undo = new JMenuItem("Undo");  //undo button
		edit.add(undo);

		frame.setJMenuBar(bar);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JButton("North"), BorderLayout.NORTH);
		JPanel modes = new JPanel(new GridLayout(0,2));
		modes.add(new JButton("Editor Mode")); //Shows you are in editor mode
		modes.add(new JButton("Player Mode")); //Puts you in player mode
		panel.add(modes, BorderLayout.NORTH); //assigns the boxes to the north container
		frame.add(panel);

		JPanel numbers = new JPanel(new GridLayout(5,0)); //how many buttons there are on the right side, needs adjusting if adding a button
		panel.add(new JButton("East"), BorderLayout.EAST); //right container in GUI
		
		Canvas canvas = new Canvas(boxes);
		panel.add(canvas, BorderLayout.CENTER);
		JButton makeBox = new JButton("Make Box");
		
		Random random = new Random();
		
		JButton arrowButton = new JButton("Make Arrow");
		arrowButton.setEnabled(false);
		makeBox.addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						Box box = new Box(random.nextInt(500), random.nextInt(500), 100, 50, "String");
						boxes.add(box);
						if (boxes.size() < 2) {
							arrowButton.setEnabled(false); // un-gray the button when there are 2 or more boxes
						} else {
							arrowButton.setEnabled(true);
						}
						canvas.repaint();
					}
					
				}
				
				);
		
		
		numbers.add(makeBox); //make box button
		numbers.add(arrowButton); //make arrow button
		JButton delete = new JButton("Delete");
		numbers.add(delete); // delete button (for boxes and arrows)
		delete.addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						canvas.deleteBox();
						
					}
					
				}
				
				);
		numbers.add(new JButton("Add Text")); // add text button
		JButton deleteAll = new JButton("Delete All"); // delete all button
		numbers.add(deleteAll);
		deleteAll.addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						canvas.deleteAllBoxes();
						
					}
					
				}
				);
		panel.add(numbers, BorderLayout.EAST); //assigns the boxes to the right container
		frame.add(panel);
		
		
		//panel.add(new JButton("Insert Text Here"), BorderLayout.WEST); //left container in GUI 
		
	//	JLabel lblFName = new JLabel("Insert Text Here:");
      //  JTextField tfFName = new JTextField(10);
     //   lblFName.setLabelFor(tfFName);
     //   panel.add(lblFName, BorderLayout.WEST);
      //  panel.add(tfFName, BorderLayout.WEST);

		
		textArea = new JTextArea("Insert Text Here");
		textArea.setColumns(20);
		textArea.setLineWrap(true);
		textArea.setRows(5);
		JScrollPane scrolling = new JScrollPane(textArea);
		panel.add(scrolling, BorderLayout.SOUTH);
	
		
				
				

		
		frame.setSize(800,700); //size of window
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //this closes the GUI
		frame.setVisible(true); //allows the GUI to start as visible
		// panel.add(new JButton("South"), BorderLayout.SOUTH); We can use this to add a bottom container if we want


	}

}



