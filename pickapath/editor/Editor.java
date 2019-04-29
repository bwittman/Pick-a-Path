package pickapath.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicArrowButton;

import pickapath.Arrow;
import pickapath.BooleanExpression;
import pickapath.BooleanExpressionException;
import pickapath.Box;
import pickapath.Item;
import pickapath.Saving;
import pickapath.player.PlayerModeGUI;

@SuppressWarnings("serial")
public class Editor extends JFrame {

	private Canvas canvas;
	private ItemTableModel tableModel;
	private JTextArea textArea;
	private JButton arrowButton;
	private JButton itemButton;
	private JSlider slider;
	private JTextArea operatorField;
	private JTextArea itemTextArea; 
	private BasicArrowButton upButton;
	private BasicArrowButton downButton;
	private JButton deletePromptButton;
	private JButton deleteChoiceButton;
	private JLabel arrowOrderLabel;
	private JLabel statusLabel;
	private Font[] fonts;
	private static final int MAX_SLIDER = 5;
	private static final int MIN_SLIDER = 1;

	private static final int GAP = 5;

	private JTextField titleField;
	private JTextField currencyField;

	private Random random = new Random();

	public static void main(String[] args) {

		try {
			// Set cross-platform Java L&F (also called "Metal")
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException
				| IllegalAccessException e) {
			// handle exception
		}

		new Editor();

	}

	private JDialog makeItemDialog(){

		//Setting parameters to create the table for item creation
		JDialog itemWindow = new JDialog(this,"Items",true);
		itemWindow.setLayout(new BorderLayout());
		JPanel tablePanel = new JPanel(new BorderLayout());
		JPanel buttonPanel = new JPanel(new GridLayout(2,1));
		JPanel mainPanel = new JPanel(new BorderLayout());


		JTable itemTable = new JTable(tableModel);
		itemTable.setFillsViewportHeight(true);
		JScrollPane tableScroll = new JScrollPane(itemTable);
		tableScroll.setPreferredSize(new Dimension(200, 500));
		JButton addItem = new JButton("Add Item");
		JButton deleteItem = new JButton("Delete Item");
		buttonPanel.add(addItem);
		addItem.setToolTipText("Add an item to the list");

		//Listener for add item button that creates a new item
		addItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				tableModel.addItem("New Item");
			}

		});
		// Adding delete item button to the button panel
		buttonPanel.add(deleteItem);
		deleteItem.setToolTipText("Delete the selected item(s) from the list");
		deleteItem.setEnabled(false);
		deleteItem.addActionListener(new ActionListener() {
			// Listener for delete button that checks if the user wants to delete the item
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub

				if(itemTable.getSelectedRow()!= -1) {
					if(JOptionPane.showConfirmDialog(itemWindow, "Are you sure you want to delete " + 
							tableModel.getValueAt(itemTable.getSelectedRow(), 1) + "?", "Delete Item?", 
							JOptionPane.YES_NO_OPTION)== JOptionPane.YES_OPTION) {

						Arrow selected = (Arrow) canvas.getSelected();
						Item item = tableModel.getItems().get(itemTable.getSelectedRow());
						for(Arrow arrow : canvas.getArrows()) 
							arrow.removeItem(item);
						itemTextArea.setText(selected.heldItemText());
						operatorField.setText(selected.getRequirementsText());
						tableModel.deleteItem(itemTable.getSelectedRow());
					}	
				}
			}

		});
		//Enables a row to be selected 
		itemTable.setRowSelectionAllowed(true);
		tablePanel.add(buttonPanel, BorderLayout.SOUTH);
		tablePanel.add(tableScroll, BorderLayout.CENTER);
		itemWindow.add(tablePanel, BorderLayout.WEST);
		//End table stuff

		operatorField = new JTextArea();
		JPanel panel = new JPanel(new GridLayout(1,3));
		JButton and = new JButton("AND");
		JButton or = new JButton("OR");
		JButton not = new JButton("NOT");


		panel.add(and);
		and.setToolTipText("Makes the arrow path require two items. "
				+ "It should be used between two items");

		and.addActionListener(new ActionListener() {
			// Listener for AND button that adds text to the items checked field 
			@Override
			public void actionPerformed(ActionEvent arg0) {
				operatorField.append(" AND ");
			}
		});

		panel.add(or);
		or.setToolTipText("Makes the arrow path require either of the two items being compared. "
				+ "It should be used between two items");

		or.addActionListener(new ActionListener() {
			// Listener for OR button that adds text to the items checked field 
			@Override
			public void actionPerformed(ActionEvent arg0) {
				operatorField.append(" OR ");
			}
		});

		panel.add(not);
		not.setToolTipText("Used for negating operations and opens up more complex item "
				+ "requirements. It should be used in front of an operation.");


		not.addActionListener(new ActionListener() {
			// Listener for NOT button that adds text to the items checked field 
			@Override
			public void actionPerformed(ActionEvent arg0) {
				operatorField.append("NOT ");
			}
		});



		JPanel itemsChecked = new JPanel(new BorderLayout());
		itemsChecked.setBorder(BorderFactory.createTitledBorder("Items Checked"));
		itemsChecked.add(operatorField,BorderLayout.CENTER);
		itemsChecked.add(panel,BorderLayout.SOUTH);
		mainPanel.add(itemsChecked,BorderLayout.CENTER);
		itemWindow.add(mainPanel, BorderLayout.CENTER);
		JPanel okCancel = new JPanel(new GridLayout(1,1));//south
		okCancel.setMinimumSize(new Dimension(300,100));
		okCancel.setPreferredSize(new Dimension(300,100));
		okCancel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JButton saveClose = new JButton("Save and Close");
		okCancel.add(saveClose);
		saveClose.setToolTipText("Evaluates the expression in the Items Checked field. "
				+ "If the expression is valid then it saves it to the arrow.");
		//Listener for check button in the item window
		saveClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// read from text, convert to number, look through list,get it, then evaluate
				String text = operatorField.getText().trim();


				if( !text.isEmpty() ) {
					Arrow arrow = (Arrow)canvas.getSelected();

					List<Item> items = tableModel.getItems();
					try {
						arrow.setBooleanExpression(BooleanExpression.makeExpression(text, items));
					} catch (BooleanExpressionException e) {
						JOptionPane.showMessageDialog(itemWindow, "Your expression describing item requirements was invalid.", "Invalid Item Requirements!", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}

				itemWindow.setVisible(false);

			}
		});


		itemTextArea = new JTextArea();
		itemTextArea.setEditable(false);
		JPanel itemsGiven = new JPanel(new BorderLayout());//north
		itemsGiven.setBorder(BorderFactory.createTitledBorder("Items Given"));
		itemsGiven.setMinimumSize(new Dimension(300,200));
		itemsGiven.setPreferredSize(new Dimension(300,200));

		itemsGiven.add(itemTextArea,BorderLayout.CENTER);
		JButton givenAdd = new JButton("Add");
		givenAdd.setToolTipText("Add an item to be given to the player when they use the arrow path.");
		givenAdd.setEnabled(false);
		givenAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Arrow arrow = (Arrow) canvas.getSelected();
				for(int row:itemTable.getSelectedRows())
					arrow.addItem(tableModel.getItems().get(row));

				itemTextArea.setText(arrow.heldItemText());
			}

		});
		JButton givenDelete = new JButton("Delete");
		givenDelete.setEnabled(false);
		givenDelete.setToolTipText("Deletes the selected item from the Items Given field.");
		givenDelete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Arrow arrow = (Arrow) canvas.getSelected();
				for(int row:itemTable.getSelectedRows())
					arrow.removeItem(tableModel.getItems().get(row));

				itemTextArea.setText(arrow.heldItemText());
			}

		});
		JPanel givenButtons = new JPanel(new GridLayout(1,2));
		givenButtons.add(givenAdd);
		givenButtons.add(givenDelete);
		itemsGiven.add(givenButtons, BorderLayout.SOUTH);
		mainPanel.add(itemsGiven, BorderLayout.NORTH);

		mainPanel.add(okCancel, BorderLayout.SOUTH);
		itemWindow.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		itemWindow.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent arg0) {
				itemWindow.setVisible(false);

			}


		});
		itemTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			//Listener that enables delete button if a row is selected
			@Override
			public void valueChanged(ListSelectionEvent event) {
				deleteItem.setEnabled(itemTable.getSelectedRow() != -1);
				givenAdd.setEnabled(itemTable.getSelectedRow() != -1);
				givenDelete.setEnabled(itemTable.getSelectedRow() != -1);


			}

		});
		itemWindow.pack();
		return itemWindow;
	}

	public boolean saveFile(List<Box> boxes, List<Arrow> arrows, List<Item> items) {   //save current work to a file

		JFileChooser fileSelect = new JFileChooser();
		fileSelect.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.getName().toLowerCase().endsWith(".pap")||file.isDirectory();
			}

			@Override
			public String getDescription() {
				return ".pap files";
			}
		});
		if (fileSelect.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileSelect.getSelectedFile();
			String path = selectedFile.getAbsolutePath();
			if (!path.toLowerCase().endsWith(".pap")) {
				selectedFile = new File(path + ".pap");


			}
			try {
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(selectedFile));
				Saving.write(out, boxes, arrows, items);
				out.close();
			} catch (FileNotFoundException e1) {

			} catch (IOException e1) {

			}
			return true;
		}
		return false;
	}


	public void openFile(List<Box> boxes, List<Arrow> arrows, List<Item> items) {  //open a saved file


		JFileChooser fileSelect = new JFileChooser();
		fileSelect.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.getName().toLowerCase().endsWith(".pap")||file.isDirectory();
			}

			@Override
			public String getDescription() {
				return ".pap files";
			}
		});
		if (fileSelect.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileSelect.getSelectedFile();

			try {
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(selectedFile));
				Saving.read(in, boxes, arrows, items);
				in.close();

			} catch (FileNotFoundException e1) {

			} catch (IOException e1) {

			} catch (ClassNotFoundException e1) {

			}
		}
	}


	public Editor() {
		super("Pick-a-Path");
		List<Box> boxes = new ArrayList<Box>();
		List<Arrow> arrows = new ArrayList<Arrow>();
		List<Item> items = new ArrayList<Item>();
		tableModel = new ItemTableModel(items);

		createNorthPanel();
		createSouthPanel();
		createEastPanel();

		canvas = new Canvas(arrows, boxes, this);
		JPanel extra = new JPanel(new BorderLayout());
		extra.setOpaque(true);
		extra.add(canvas, BorderLayout.CENTER);
		JScrollPane scrollPane = new JScrollPane(extra); //adding the scrollpane to our canvas
		scrollPane.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
		canvas.setViewport(scrollPane.getViewport());


		add(scrollPane, BorderLayout.CENTER);


		JMenuBar bar = new JMenuBar(); // menu bar
		JMenu file = new JMenu("File"); // file button

		bar.add(file);


		JMenu edit = new JMenu("Edit"); // file button
		JMenuItem makebox = new JMenuItem("Make Box"); //another way to make situation
		KeyStroke keyStrokeToNewBox = KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_DOWN_MASK); 
		makebox.setAccelerator(keyStrokeToNewBox); //hotkey to create a new situation
		edit.add(makebox);
		makebox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addBox();
			}
		});
		bar.add(edit);

		JMenuItem nproject = new JMenuItem("New Project"); // new project button

		KeyStroke keyStrokeToNewProject = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK); 
		nproject.setAccelerator(keyStrokeToNewProject); //hotkey to create a new project
		file.add(nproject);
		nproject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!boxes.isEmpty()) {
					int ask = JOptionPane.showConfirmDialog(Editor.this, "Do you want to save first?", "Save?",
							JOptionPane.YES_NO_CANCEL_OPTION);
					if (ask == JOptionPane.YES_OPTION) {
						if (saveFile(boxes, arrows, items)) {
							canvas.deleteAllBoxes();
							openFile(boxes, arrows, items);
							tableModel.setItemList(items);
							canvas.repaint();
						}
					}else if (ask == JOptionPane.NO_OPTION) {
						canvas.deleteAllBoxes();

					}
				}
			}
		});

		JMenuItem openp = new JMenuItem("Open Project"); // open project button
		KeyStroke keyStrokeToOpen = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK); 
		openp.setAccelerator(keyStrokeToOpen); //hotkey to open a project
		file.add(openp);
		openp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!boxes.isEmpty()) {
					int ask = JOptionPane.showConfirmDialog(Editor.this, "Do you want to save first?", "Save?",
							JOptionPane.YES_NO_CANCEL_OPTION);
					if (ask == JOptionPane.YES_OPTION) {

						if (saveFile(boxes, arrows, items)) {
							canvas.deleteAllBoxes();
							openFile(boxes, arrows, items);
							tableModel.setItemList(items);
							canvas.repaint();
						}

					}else if (ask == JOptionPane.NO_OPTION) {
						canvas.deleteAllBoxes();
						openFile(boxes, arrows, items);
						tableModel.setItemList(items);
						canvas.repaint();
					}
				} else {
					openFile(boxes, arrows, items);
					tableModel.setItemList(items);
					canvas.repaint();
				}
			}

		});

		JMenuItem save = new JMenuItem("Save"); // save button
		KeyStroke keyStrokeToSave = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK); 
		save.setAccelerator(keyStrokeToSave); //hotkey to save a project
		file.add(save);
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveFile(boxes, arrows, items);
			}
		});

		JMenuItem exit = new JMenuItem("Exit"); // exit button
		KeyStroke keyStrokeToExit = KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK); 
		exit.setAccelerator(keyStrokeToExit); //hotkey for exiting
		file.add(exit);
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});


		JMenu mode = new JMenu("Game"); // mode button
		setJMenuBar(bar);


		JMenuItem playerModeItem = new JMenuItem("Play Game");
		KeyStroke playerModeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK); 
		playerModeItem.setAccelerator(playerModeKeyStroke); //hotkey to get to game mode
		playerModeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				List<Box> startingBoxes = getStartingBoxes(boxes);
				if (startingBoxes.size() == 1) {					
					setVisible(false);
					new PlayerModeGUI(startingBoxes.get(0), Editor.this, boxes, arrows, items);
				} else {
					JOptionPane.showMessageDialog(Editor.this,
							"You must have exactly one prompt with no incoming choices to make the game playable.", "Game Not Playable!", JOptionPane.ERROR_MESSAGE);
				};
			}

		});

		bar.add(mode);
		mode.add(playerModeItem);
		setSize(800, 700);
		setMinimumSize(new Dimension(800, 700));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // this closes the GUI

		setVisible(true); // allows the GUI to start as visible

	}


	private void addBox() {
		JViewport viewport = canvas.getViewport();
		Dimension size = viewport.getExtentSize();
		canvas.addBox(new Box((int) ((random.nextInt((int)size.getWidth()) + (int)viewport.getViewPosition().getX())*canvas.getZoom()), random.nextInt((int) (((int)size.getHeight()) + (int)viewport.getViewPosition().getY()*canvas.getZoom())), ""));
	}

	private void createEastPanel() {

		JDialog itemWindow = makeItemDialog();

		//Panel for entire east section
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		//Panel for prompts-related buttons
		JPanel promptsPanel = new JPanel(new GridLayout(3,1, GAP, GAP));
		promptsPanel.setBorder(BorderFactory.createTitledBorder("Prompts"));

		JButton makePromptButton = new JButton("Make Prompt");
		makePromptButton.setToolTipText("Make a new prompt.");
		makePromptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addBox();
			}
		});

		promptsPanel.add(makePromptButton);

		arrowButton = new JButton("Start Choice");
		arrowButton.setToolTipText("Create a new choice, starting at the currently selected prompt. Then, click on the ending prompt to link the two with a choice.");
		arrowButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				canvas.startArrowCheck();
			}
		});

		arrowButton.setEnabled(false);
		promptsPanel.add(arrowButton);
		
		
		deletePromptButton = new JButton("Delete Prompt");
		
		deletePromptButton.setToolTipText("Delete the selected prompt.");
		deletePromptButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				canvas.deleteBox();
			}

		});
		deletePromptButton.setEnabled(false);
		
		promptsPanel.add(deletePromptButton);
		promptsPanel.setMaximumSize(promptsPanel.getMinimumSize());
		
		panel.add(promptsPanel);
		
		panel.add(javax.swing.Box.createVerticalStrut(GAP));
		
		
		//Panel for choices-related buttons
		JPanel choicesPanel = new JPanel(new GridLayout(5,1, GAP, GAP));
		choicesPanel.setBorder(BorderFactory.createTitledBorder("Choices"));

		itemButton = new JButton("Details");
		itemButton.setToolTipText("Decide whether making this choice requires items or currency or gives items or currency.");
		itemButton.setEnabled(false);
		choicesPanel.add(itemButton);
		itemButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				Arrow arrow = (Arrow)canvas.getSelected();
				itemTextArea.setText(arrow.heldItemText());
				operatorField.setText(arrow.getRequirementsText());
				itemWindow.setVisible(true);
			}

		});
		
		upButton = new BasicArrowButton(BasicArrowButton.NORTH);
		upButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				canvas.makeArrowEarlier();
			}

		});		
		upButton.setEnabled(false);
		choicesPanel.add(upButton);
		
		arrowOrderLabel = new JLabel("");
		arrowOrderLabel.setHorizontalAlignment(JLabel.CENTER);
		choicesPanel.add(arrowOrderLabel);
		
		downButton = new BasicArrowButton(BasicArrowButton.SOUTH);
		downButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				canvas.makeArrowEarlier();
			}

		});		
		downButton.setEnabled(false);
		choicesPanel.add(downButton);
	

		deleteChoiceButton = new JButton("Delete Choice");
		
		deleteChoiceButton.setToolTipText("Delete the selected choice.");
		deleteChoiceButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				canvas.deleteArrow();
			}

		});
		deleteChoiceButton.setEnabled(false);
		
		choicesPanel.add(deleteChoiceButton);
		choicesPanel.setMaximumSize(choicesPanel.getMinimumSize());
		
		panel.add(choicesPanel);
		
		panel.add(javax.swing.Box.createVerticalGlue());

		add(panel, BorderLayout.EAST); // assigns the boxes to the right container
	}

	private void createSouthPanel() {
		
		JPanel panel = new JPanel(new BorderLayout());
		
		
		JPanel statusPanel = new JPanel(new BorderLayout());
		statusPanel.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
		statusLabel = new JLabel("Status:");
		
		statusPanel.add(statusLabel, BorderLayout.CENTER);
		
		panel.add(statusPanel, BorderLayout.NORTH);
		
		textArea = new JTextArea();
		textArea.setColumns(20);
		textArea.setLineWrap(true);
		textArea.setRows(6);
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
		scrolling.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP), BorderFactory.createTitledBorder("Text")));

		panel.add(scrolling, BorderLayout.SOUTH);
		
		add(panel, BorderLayout.SOUTH);
	}

	private void createNorthPanel() {		
		//Panel for whole north area
		JPanel panel = new JPanel(new BorderLayout());

		//Panel for title and currency labels
		JPanel labelPanel = new JPanel(new GridLayout(2,1, GAP, GAP));
		labelPanel.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
		JLabel titleLabel = new JLabel("Title: ");
		titleLabel.setHorizontalAlignment(JLabel.RIGHT);
		labelPanel.add(titleLabel);
		labelPanel.add(new JLabel("Currency: "));		
		panel.add(labelPanel, BorderLayout.WEST);

		//Panel for title and currency fields
		JPanel fieldPanel = new JPanel(new GridLayout(2, 1, GAP, GAP));
		fieldPanel.setBorder(BorderFactory.createEmptyBorder(GAP, 0, GAP, GAP));
		titleField = new JTextField();
		currencyField = new JTextField();
		fieldPanel.add(titleField);
		fieldPanel.add(currencyField);		
		panel.add(fieldPanel, BorderLayout.CENTER);

		//Zoom slider		
		slider = new JSlider(JSlider.HORIZONTAL, MIN_SLIDER, MAX_SLIDER, 1);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(1);

		//Set up different fonts for different slider settings
		fonts = new Font[MAX_SLIDER - MIN_SLIDER + 1];
		fonts[0] = new JLabel().getFont();
		for (int i = 1; i < fonts.length; ++i)
			fonts[i] = fonts[0].deriveFont(fonts[0].getSize() / ((i + MIN_SLIDER + 1) / 2.0f));

		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				canvas.setZoom(1.0 / ((slider.getValue() + 1) / 2.0), fonts[slider.getValue() - MIN_SLIDER]);
			}

		});	

		slider.setPaintLabels(true);

		JPanel zoomPanel = new JPanel(new BorderLayout());
		zoomPanel.setBorder(BorderFactory.createTitledBorder("Zoom"));
		zoomPanel.add(slider, BorderLayout.CENTER);

		panel.add(zoomPanel, BorderLayout.SOUTH);		

		add(panel, BorderLayout.NORTH);
	}

	public static List<Box> getStartingBoxes(List<Box> boxes) {
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

	public void setItemsEnabled(boolean enabled) {
		itemButton.setEnabled(enabled);
	}

	public Canvas getCanvas() {
		return canvas;
	}
}