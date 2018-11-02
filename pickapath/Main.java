package pickapath;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;




public class Main extends JFrame{

	private JTextArea textArea;
	private JButton arrowButton;
	private JSlider slider;
	private JLabel label;

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
		List<Arrow> arrows = new ArrayList<Arrow>();
		JFrame frame = new JFrame("PICK A PATH"); //title of window 

		JFrame playerMode = new JFrame("PlayerMode");
		JPanel playerPanel = new JPanel(new FlowLayout());
		JLabel label1 = new JLabel("Situation");

		final int MAX_BUTTONS = 7;
		for(int i = 1; i < MAX_BUTTONS; i++) 
        playerPanel.add(new JRadioButton("Choices" + i + "   "));
		playerPanel.setLayout(new GridLayout(7, 0, 0, 1));
		playerMode.add(label1, BorderLayout.NORTH);

		
		//playerPanel.setSize(10,500);

		
	/*	JRadioButton JRadioButton = new JRadioButton("Choice1");
		JRadioButton.setSelected(false);
		JRadioButton JRadioButton2 = new JRadioButton("Choice2");
		JRadioButton2.setSelected(false);
		JRadioButton JRadioButton3 = new JRadioButton("Choice3");
		JRadioButton2.setSelected(false);
		playerPanel.add(JRadioButton, BorderLayout.CENTER);
		playerPanel.add(JRadioButton2, BorderLayout.CENTER);
		playerPanel.add(JRadioButton3, BorderLayout.CENTER);
*/
		
		JFrame submit = new JFrame("Submit");
		JPanel bottom = new JPanel(new FlowLayout());
		
		playerPanel.add(bottom);
		playerMode.add(new JButton("Submit"), BorderLayout.SOUTH);
		

		playerMode.add(playerPanel);

		playerMode.setSize(800,700);
		playerMode.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		playerMode.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
            	frame.setVisible(true);
            	playerMode.setVisible(false);
            }
        });
		playerMode.setVisible(false);


		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JButton("North"), BorderLayout.NORTH);
		JPanel modes = new JPanel(new GridLayout(0,2));
		panel.add(modes, BorderLayout.NORTH); //assigns the boxes to the north container
		frame.add(panel);

		JPanel numbers = new JPanel(new GridLayout(4,0)); //how many buttons there are on the right side, needs adjusting if adding a button
		panel.add(new JButton("East"), BorderLayout.EAST); //right container in GUI

		Canvas canvas = new Canvas(arrows,boxes, this);
		panel.add(canvas, BorderLayout.CENTER);
		JButton makeBox = new JButton("Make Box");

		Random random = new Random();

		arrowButton = new JButton("Make Arrow");
		arrowButton.addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						canvas.startArrowCheck();
					}

				}

				);

		arrowButton.setEnabled(false);
		makeBox.addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						Box box = new Box(random.nextInt(500), random.nextInt(500), 100, 50, "");
						boxes.add(box);

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
		//	numbers.add(new JButton("Add Text")); // add text button
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
								out.writeObject(arrows);
								out.close();
								System.out.printf("Serialized data is saved in " + selectedFile);
							} catch (FileNotFoundException e1) {

							} catch (IOException e1) {

							}

						}
					}else {
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
								out.writeObject(arrows);
								out.close();
								System.out.printf("Serialized data is saved in " + selectedFile);
							} catch (FileNotFoundException e1) {

							} catch (IOException e1) {

							}
						}
						canvas.deleteAllBoxes();
						JFileChooser chooser2 = new JFileChooser();
						chooser2.setFileFilter(new FileFilter(){
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
						if(chooser2.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
							File selectedFile = chooser2.getSelectedFile();

							try {
								ObjectInputStream in = new ObjectInputStream(new FileInputStream(selectedFile));
								List<Box> listbox = (List<Box>) in.readObject();
								boxes.clear();
								boxes.addAll(listbox);
								List<Arrow> listarrow = (List<Arrow>) in.readObject();
								arrows.clear();
								arrows.addAll(listarrow);
								in.close();
								canvas.repaint();
								System.out.printf("Serialized data is read from " + selectedFile);
							} catch (FileNotFoundException e1) {

							} catch (IOException e1) {

							} catch (ClassNotFoundException e1) {

							}
						}
					}else {
						canvas.deleteAllBoxes();
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
								List<Arrow> listarrow = (List<Arrow>) in.readObject();
								arrows.clear();
								arrows.addAll(listarrow);
								in.close();
								canvas.repaint();
								System.out.printf("Serialized data is read from " + selectedFile);
							} catch (FileNotFoundException e1) {

							} catch (IOException e1) {

							} catch (ClassNotFoundException e1) {

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
							List<Arrow> listarrow = (List<Arrow>) in.readObject();
							arrows.clear();
							arrows.addAll(listarrow);
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
						out.writeObject(arrows);
						out.close();
						System.out.printf("Serialized data is saved in " + selectedFile);
					} catch (FileNotFoundException e1) {

					} catch (IOException e1) {

					}

				}
			}
		});



		JMenuItem exit = new JMenuItem("Exit");  //exit button
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

		JMenu mode = new JMenu("Mode"); //mode button
		JMenuItem editorMode = new JMenuItem("Editor Mode");
		JMenuItem playerModeItem = new JMenuItem("Player Mode");
		playerModeItem.addActionListener(new ActionListener () {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				playerMode.setVisible(true);
				frame.setVisible(false);

			}

		});
		bar.add(mode);

		mode.add(editorMode);
		mode.add(playerModeItem);
		frame.setSize(800,700); 
		frame.getSize();//size of window
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //this closes the GUI
		frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {

            	playerMode.dispose();
            }
        });
		frame.setVisible(true); //allows the GUI to start as visible
		// panel.add(new JButton("South"), BorderLayout.SOUTH); We can use this to add a bottom container if we want

	}

	public void setText(String text) {
		textArea.setText(text);		

	}
	public void setMakeArrowEnabled(boolean enabled) {
		arrowButton.setEnabled(enabled);
	}
	public void boxResizeSlider() {
		//need to add mouse listener to this, and when it slides either way, have zoom increase or decrease by .05
		float zoom = 1;
		slider.setLayout(new BorderLayout());
		slider = new JSlider(JSlider.HORIZONTAL, 25, 100, 50);
		slider.setMajorTickSpacing(25);
		slider.setPaintTicks(true);
		add(slider);
		
		
		label = new JLabel("current zoom: 50");
		add(label);
		
		event e = new event();
		slider.addChangeListener(e);
		
	}
	
	public class event implements ChangeListener {
		public void stateChanged (ChangeEvent e) {
			int value = slider.getValue();
			label.setText("Current zoom: " + value);
		}
	}


}