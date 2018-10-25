package pickapath;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;



public class Main {

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

		new Main();
		
	}
	
	public Main() {
		
		List<Box> boxes = new ArrayList<Box>();
		JFrame frame = new JFrame("PICK A PATH"); //title of window 
		
		
		

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JButton("North"), BorderLayout.NORTH);
		JPanel modes = new JPanel(new GridLayout(0,2));
		modes.add(new JButton("Editor Mode")); //Shows you are in editor mode
		modes.add(new JButton("Player Mode")); //Puts you in player mode
		panel.add(modes, BorderLayout.NORTH); //assigns the boxes to the north container
		frame.add(panel);

		JPanel numbers = new JPanel(new GridLayout(5,0)); //how many buttons there are on the right side, needs adjusting if adding a button
		panel.add(new JButton("East"), BorderLayout.EAST); //right container in GUI
		
		Canvas canvas = new Canvas(boxes, this);
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
						Box box = new Box(random.nextInt(500), random.nextInt(500), 100, 50, "");
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
		textArea.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				update();
				
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				update();
				
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				update();
			}

			private void update() {
				canvas.updateText(textArea.getText());
			}
			
			
		});
		JScrollPane scrolling = new JScrollPane(textArea);
		panel.add(scrolling, BorderLayout.SOUTH);
	
		
				
				
		JMenuBar bar = new JMenuBar();  //menu bar
		JMenu file = new JMenu("File"); //file button
		
		bar.add(file);

		JMenuItem nproject = new JMenuItem("New Project");  //new project button
		file.add(nproject);
		nproject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (! boxes.isEmpty()) {
					if (JOptionPane.showConfirmDialog(frame,"Do you want to save first?", "Save?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						JFileChooser chooser = new JFileChooser();
						chooser.setFileFilter(new FileFilter(){
		                   @Override
		                   public boolean accept(File file)
		                   {
		                      return file.getName().toLowerCase().endsWith(".pap");
		                   }

		                   @Override
		                   public String getDescription()
		                   {
		                      return ".pap files";
		                   }
		                });
						if(chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
						    File selectedFile = chooser.getSelectedFile();
						
							try {
								ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(selectedFile));
								 out.writeObject(boxes);
						         out.close();
						         System.out.printf("Serialized data is saved in " + selectedFile);
							} catch (FileNotFoundException e1) {
								
							} catch (IOException e1) {
								
							}
					        
						}
					}else {
						canvas.repaint();
						canvas.deleteAllBoxes();
					}
				} 
			}
		});
		
		JMenuItem openp = new JMenuItem("Open Project");  //open project button
		file.add(openp);
		openp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (! boxes.isEmpty()) {
					if (JOptionPane.showConfirmDialog(frame,"Do you want to save first?", "Save?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						JFileChooser chooser = new JFileChooser();
						chooser.setFileFilter(new FileFilter(){
		                   @Override
		                   public boolean accept(File file)
		                   {
		                      return file.getName().toLowerCase().endsWith(".pap");
		                   }

		                   @Override
		                   public String getDescription()
		                   {
		                      return ".pap files";
		                   }
		                });
						if(chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
						    File selectedFile = chooser.getSelectedFile();
						
							try {
								ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(selectedFile));
								 out.writeObject(boxes);
						         out.close();
						         System.out.printf("Serialized data is saved in " + selectedFile);
							} catch (FileNotFoundException e1) {
								
							} catch (IOException e1) {
								
							}
						}
						}
					}else{
						JFileChooser chooser = new JFileChooser();
						chooser.setFileFilter(new FileFilter(){
		                   @Override
		                   public boolean accept(File file)
		                   {
		                      return file.getName().toLowerCase().endsWith(".pap");
		                   }

		                   @Override
		                   public String getDescription()
		                   {
		                      return ".pap files";
		                   }
		                });
						if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						    File selectedFile = chooser.getSelectedFile();
						    
						    try {
								ObjectInputStream in = new ObjectInputStream(new FileInputStream(selectedFile));
								 List<Box> listbox = (List<Box>) in.readObject();
								 boxes.clear();
								 boxes.addAll(listbox);
								 //List<Box> listarrow = (List<Box>) in.readObject();
								 //arrows.clear();
								 //arrows.addAll(listarrow);
						         in.close();
						         canvas.repaint();
						         System.out.printf("Serialized data is read from " + selectedFile);
							} catch (FileNotFoundException e1) {
								
							} catch (IOException e1) {
								
							} catch (ClassNotFoundException e1) {
								
							}
						}
					}
				} 
				
				
				
		});

		JMenuItem save = new JMenuItem("Save");  //save button
		file.add(save);
		save.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JFileChooser chooser = new JFileChooser();
						chooser.setFileFilter(new FileFilter(){
		                   @Override
		                   public boolean accept(File file)
		                   {
		                      return file.getName().toLowerCase().endsWith(".pap");
		                   }

		                   @Override
		                   public String getDescription()
		                   {
		                      return ".pap files";
		                   }
		                });
						if(chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
						    File selectedFile = chooser.getSelectedFile();
						
							try {
								ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(selectedFile));
								 out.writeObject(boxes);
						         out.close();
						         System.out.printf("Serialized data is saved in " + selectedFile);
							} catch (FileNotFoundException e1) {
								
							} catch (IOException e1) {
								
							}
					        
						}
					}
				});
		
		
		
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
		
		frame.setSize(800,700); //size of window
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //this closes the GUI
		frame.setVisible(true); //allows the GUI to start as visible
		// panel.add(new JButton("South"), BorderLayout.SOUTH); We can use this to add a bottom container if we want


	}
	
	public void setText(String text) {
		textArea.setText(text);
		
	}

}



