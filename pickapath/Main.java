package pickapath;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
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

import javax.swing.ButtonGroup;
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

public class Main extends JFrame {

	private JTextArea textArea;
	private JButton arrowButton;
	private JSlider slider;
	private JLabel label;
	private JPanel choicePanel;
	private JFrame playerMode;
	private JFrame mainMenu;
	private JTextArea boxInformation;
	private List<JRadioButton> buttonList;
	private Box situation;
	private Font[] fonts;
	private static int MAX_SLIDER = 5;
	private static int MIN_SLIDER = 1;

	public static void main(String[] args) {

		try {
			// Set cross-platform Java L&F (also called "Metal")
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException
				| IllegalAccessException e) {
			// handle exception
		}

		new Main();

	}

	public Main() {
		buttonList = new ArrayList<JRadioButton>();
		List<Box> boxes = new ArrayList<Box>();
		List<Arrow> arrows = new ArrayList<Arrow>();
		JFrame frame = new JFrame("PICK A PATH"); // title of window

		playerMode = new JFrame("PlayerMode");
		choicePanel = new JPanel();
		boxInformation = new JTextArea("Situation");
		boxInformation.setEditable(false);

		/*
		 * final int MAX_BUTTONS = 7; for(int i = 1; i < MAX_BUTTONS; i++)
		 * choicePanel.add(new JRadioButton("Choices" + i + "   "));
		 * choicePanel.setLayout(new GridLayout(7, 0, 0, 1));
		 */
		playerMode.add(boxInformation, BorderLayout.NORTH);

		// playerPanel.setSize(10,500);

		/*
		 * JRadioButton JRadioButton = new JRadioButton("Choice1");
		 * JRadioButton.setSelected(false); JRadioButton JRadioButton2 = new
		 * JRadioButton("Choice2"); JRadioButton2.setSelected(false); JRadioButton
		 * JRadioButton3 = new JRadioButton("Choice3");
		 * JRadioButton2.setSelected(false); playerPanel.add(JRadioButton,
		 * BorderLayout.CENTER); playerPanel.add(JRadioButton2, BorderLayout.CENTER);
		 * playerPanel.add(JRadioButton3, BorderLayout.CENTER);
		 */

		JPanel bottom = new JPanel(new FlowLayout());
		JPanel center = new JPanel(new FlowLayout());

		JButton submitButton = new JButton("Submit");
		submitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (situation.getOutgoing().size() > 0 ) {
					Arrow arrow = null;

					for (int i = 0; i < buttonList.size(); i++) {
						if(buttonList.get(i).isSelected())
							arrow = situation.getOutgoing().get(i);
					}
					if(arrow != null) {
						Box next = arrow.getEnd();
						populateChoices(next);

					}
				}
				else {
					frame.setVisible(true);
					playerMode.setVisible(false);
				}

			}

		});
		bottom.add(submitButton);
		playerMode.add(bottom, BorderLayout.SOUTH);
		center.add(choicePanel);
		playerMode.add(center, BorderLayout.CENTER);

		playerMode.setSize(800, 700);
		playerMode.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		playerMode.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.setVisible(true);
				playerMode.setVisible(false);
			}
		});
		playerMode.setVisible(false);
		
		
		

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JButton("North"), BorderLayout.NORTH);
		JPanel modes = new JPanel(new GridLayout(0, 2));
		panel.add(modes, BorderLayout.NORTH); // assigns the boxes to the north container
		frame.add(panel);
		JPanel numbers = new JPanel(new GridLayout(4, 0)); // how many buttons there are on the right side, needs
		// adjusting if adding a button
		panel.add(new JButton("East"), BorderLayout.EAST); // right container in GUI
//=======
	//	JPanel numbers = new JPanel(new GridLayout(4,0)); //how many buttons there are on the right side, needs adjusting if adding a button
		panel.add(new JButton("East"), BorderLayout.EAST); //right container in GUI
		
		
		//Main Menu frame and panels
		mainMenu = new JFrame("PICK-A-PATH Main Menu");
		mainMenu.setSize(800, 700);
		JPanel menuPanel = new JPanel(new BorderLayout());
		menuPanel.add(new JButton("South"), BorderLayout.SOUTH);
		frame.add(panel);
		JPanel menuButtonPanel = new JPanel(new GridLayout(0, 2)); // how many buttons there are on the right side, needs
		menuPanel.add(new JButton("South"), BorderLayout.SOUTH);
	//add(label);c
	//label = new JLabel("current zoom: 50");
	//panel.add(label, BorderLayout.NORTH);
	//JTextArea sliderText = new JTextArea("Current zoom: ");
	//panel.add(sliderText, BorderLayout.NORTH);
	
	
		//label.setText("Current zoom: " + value);
		
				
				
		Canvas canvas = new Canvas(arrows,boxes, this);
		panel.add(canvas, BorderLayout.CENTER);
		
		
		slider = new JSlider(JSlider.HORIZONTAL, MIN_SLIDER, MAX_SLIDER, 1);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(1);
		//slider.setPaintLabels(true);
		
		fonts = new Font[MAX_SLIDER - MIN_SLIDER + 1];
		fonts[0] = new JLabel().getFont();
		for( int i = 1; i < fonts.length; ++i )
			fonts[i] = fonts[0].deriveFont(fonts[0].getSize()/((i + MIN_SLIDER+1)/2.0f));		
		
		panel.add(slider, BorderLayout.NORTH);
		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				canvas.setZoom(1.0 / ((slider.getValue()+1)/2.0), fonts[slider.getValue() - MIN_SLIDER]); 
			}
			
		});
		
		JButton makeBox = new JButton("Make Box");

		Random random = new Random();

		arrowButton = new JButton("Make Arrow");
		arrowButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				canvas.startArrowCheck();
			}

		}

				);

		arrowButton.setEnabled(false);
		makeBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				Box box = new Box(random.nextInt(500), random.nextInt(500), 100, 50, "");
				boxes.add(box);

				canvas.repaint();
			}

		}

				);

		numbers.add(makeBox); // make box button
		numbers.add(arrowButton); // make arrow button
		JButton delete = new JButton("Delete");
		numbers.add(delete); // delete button (for boxes and arrows)
		delete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				canvas.deleteBox();
				canvas.deleteArrow();

			}

		}

				);
		// numbers.add(new JButton("Add Text")); // add text button
		JButton deleteAll = new JButton("Delete All"); // delete all button
		numbers.add(deleteAll);
		deleteAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				canvas.deleteAllBoxes();

			}

		});
		panel.add(numbers, BorderLayout.EAST); // assigns the boxes to the right container
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

		JMenuBar bar = new JMenuBar(); // menu bar
		JMenu file = new JMenu("File"); // file button

		bar.add(file);

		JMenuItem nproject = new JMenuItem("New Project"); // new project button
		file.add(nproject);
		nproject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!boxes.isEmpty()) {
					if (JOptionPane.showConfirmDialog(frame, "Do you want to save first?", "Save?",
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						JFileChooser chooser = new JFileChooser();
						chooser.setFileFilter(new FileFilter() {
							@Override
							public boolean accept(File file) {
								return file.getName().toLowerCase().endsWith(".pap");
							}

							@Override
							public String getDescription() {
								return ".pap files";
							}
						});
						if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
							File selectedFile = chooser.getSelectedFile();
							String path = selectedFile.getAbsolutePath();
							if( !path.toLowerCase().endsWith(".pap")) {
								selectedFile = new File(path + ".pap");
							}
							try {
								ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(selectedFile));
								out.writeObject(boxes);
								out.writeObject(arrows);
								out.close();
								System.out.printf("Serialized data is saved in " + selectedFile);
								canvas.deleteAllBoxes();
							} catch (FileNotFoundException e1) {

							} catch (IOException e1) {

							}

						}
					} else {
						canvas.deleteAllBoxes();
					}
				}
			}
		});

		JMenuItem openp = new JMenuItem("Open Project"); // open project button
		file.add(openp);
		openp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!boxes.isEmpty()) {
					if (JOptionPane.showConfirmDialog(frame, "Do you want to save first?", "Save?",
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						JFileChooser chooser = new JFileChooser();
						chooser.setFileFilter(new FileFilter() {
							@Override
							public boolean accept(File file) {
								return file.getName().toLowerCase().endsWith(".pap");
							}

							@Override
							public String getDescription() {
								return ".pap files";
							}
						});
						if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
							File selectedFile = chooser.getSelectedFile();
							String path = selectedFile.getAbsolutePath();
							if( !path.toLowerCase().endsWith(".pap")) {
								selectedFile = new File(path + ".pap");
							}
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
						chooser2.setFileFilter(new FileFilter() {
							@Override
							public boolean accept(File file) {
								return file.getName().toLowerCase().endsWith(".pap");
							}

							@Override
							public String getDescription() {
								return ".pap files";
							}
						});
						if (chooser2.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
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
					} else {
						canvas.deleteAllBoxes();
						JFileChooser chooser = new JFileChooser();
						chooser.setFileFilter(new FileFilter() {
							@Override
							public boolean accept(File file) {
								return file.getName().toLowerCase().endsWith(".pap");
							}

							@Override
							public String getDescription() {
								return ".pap files";
							}
						});
						if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
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
								e1.printStackTrace();

							} catch (ClassNotFoundException e1) {

							}
						}
					}
				} else {
					JFileChooser chooser = new JFileChooser();
					chooser.setFileFilter(new FileFilter() {
						@Override
						public boolean accept(File file) {
							return file.getName().toLowerCase().endsWith(".pap");
						}

						@Override
						public String getDescription() {
							return ".pap files";
						}
					});
					if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
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

		JMenuItem save = new JMenuItem("Save"); // save button
		file.add(save);
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new FileFilter() {
					@Override
					public boolean accept(File file) {
						return file.getName().toLowerCase().endsWith(".pap");
					}

					@Override
					public String getDescription() {
						return ".pap files";
					}
				});
				if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					File selectedFile = chooser.getSelectedFile();
					String path = selectedFile.getAbsolutePath();
					if( !path.toLowerCase().endsWith(".pap")) {
						selectedFile = new File(path + ".pap");
					}

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

		JMenuItem exit = new JMenuItem("Exit"); // exit button
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
		});
		file.add(exit);

//<<<<<<< HEAD

		frame.setJMenuBar(bar);

		JMenu mode = new JMenu("Mode"); // mode button
//=======
		frame.setJMenuBar(bar);	

		//JMenu mode = new JMenu("Mode"); //mode button
//>>>>>>> branch 'master' of https://github.com/bwittman/comp3100-fall2018-2.git
		JMenuItem editorMode = new JMenuItem("Editor Mode");
		JMenuItem playerModeItem = new JMenuItem("Player Mode");
		playerModeItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				List<Box> startingBoxes = getStartingBoxes(boxes);
				if (startingBoxes.size() == 1) {
					populateChoices(startingBoxes.get(0));

					playerMode.setVisible(true);
					frame.setVisible(false);
				} else {
					JOptionPane.showMessageDialog(null,
							"You must have exactly one box with no incoming arrows before entering player mode!");
				}
			}

		});
		bar.add(mode);

		mode.add(editorMode);
		mode.add(playerModeItem);
		frame.setSize(800, 700);
		frame.getSize();// size of window
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // this closes the GUI
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				playerMode.dispose();
			}
		});
		frame.setVisible(true); // allows the GUI to start as visible
		// panel.add(new JButton("South"), BorderLayout.SOUTH); We can use this to add a
		// bottom container if we want

	}

	protected void populateChoices(Box box) {
		situation = box;
		buttonList.clear();
		choicePanel.removeAll();
		choicePanel.setLayout(new GridLayout(box.getOutgoing().size(), 1));// where the JRadio Button info is formed
		// from the arrows
		boxInformation.setText(box.getText()); // text in the boxes
		ButtonGroup group = new ButtonGroup();// groups the JButtons together for formatting in the gridLayout
		for (Arrow arrow : box.getOutgoing()) {
			JRadioButton button = new JRadioButton(arrow.getText());
			group.add(button);// adds all the buttons to the middle
			buttonList.add(button);
			choicePanel.add(button);

		}
		playerMode.setSize(500,400);
	}

	protected List<Box> getStartingBoxes(List<Box> boxes) {
		List<Box> startingBoxes = new ArrayList<Box>();
		for (Box box : boxes) {
			if (box.getIncoming().isEmpty())
				startingBoxes.add(box);
		}

		return startingBoxes;
	}

	public void setText(String text) {
		textArea.setText(text);

	}

	public void setMakeArrowEnabled(boolean enabled) {
		arrowButton.setEnabled(enabled);
	}


}