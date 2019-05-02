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
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
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
	private JButton beginChoiceButton;
	private JButton itemButton;
	private JSlider slider;
	private JTextArea operatorField;
	private JTextArea itemTextArea; 
	private BasicArrowButton upButton;
	private BasicArrowButton downButton;
	private JButton deletePromptButton;
	private JButton deleteChoiceButton;
	private JButton recolorPromptButton;
	private JLabel choiceOrderLabel;
	private JLabel statusLabel;
	private Font[] fonts;
	private List<Box> boxes = new ArrayList<Box>();
	private JMenuItem save;
	private JMenuItem beginChoiceItem;

	private JTextField titleField;
	private JTextField currencyField;
	private File saveFile = null;	

	private Random random = new Random();
	
	
	private static final int MAX_SLIDER = 5;
	private static final int MIN_SLIDER = 1;
	private static final int GAP = 5;
	private static final String TITLE = "Pick-a-Path";
	

	public static void main(String[] args) {

		try { 
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException
				| IllegalAccessException e) {			
		}

		new Editor();
	}	
	
	private boolean save(List<Box> boxes, List<Arrow> arrows, List<Item> items) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(saveFile));
			Saving.write(out, boxes, arrows, items, titleField.getText().trim(), currencyField.getText().trim());
			out.close();
		} 
		catch (IOException e) {
			return false;
		}
		
		setTitle(TITLE + " - " + saveFile.getName());		
		save.setEnabled(false);
		return true;
	}

	private boolean saveAs(List<Box> boxes, List<Arrow> arrows, List<Item> items) {   //save current work to a file

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
			File file = fileSelect.getSelectedFile();
			String path = file.getAbsolutePath();
			if (!path.toLowerCase().endsWith(".pap"))
				file = new File(path + ".pap");	
			
			if(file.exists()) {
				if( JOptionPane.showConfirmDialog(this, "Are you sure you want to save over " + file.getName() + "?", "Overwrite File?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
					return false;
			}
			
			saveFile = file;
			
			return save(boxes, arrows, items);
		}
		return false;
	}


	public void openFile(List<Box> boxes, List<Arrow> arrows, List<Item> items) {  //open a saved file
		JFileChooser fileSelect = new JFileChooser();
		String[] strings = new String[2];
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
			saveFile = fileSelect.getSelectedFile();

			try {
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(saveFile));
				Saving.read(in, boxes, arrows, items, strings);
				in.close();
				
				setTitle(TITLE + " - " + saveFile.getName());
				titleField.setText(strings[0]);
				currencyField.setText(strings[1]);
				save.setEnabled(false);
			} 
			catch (IOException | ClassNotFoundException e) {
			}
		}
	}


	public Editor() {
		super(TITLE + " - New Document");		
		List<Arrow> arrows = new ArrayList<Arrow>();
		List<Item> items = new ArrayList<Item>();
		tableModel = new ItemTableModel(items);

		add(createNorthPanel(), BorderLayout.NORTH);
		add(createSouthPanel(), BorderLayout.SOUTH);
		add(createEastPanel(), BorderLayout.EAST);
		
		createMenus(arrows, items);

		canvas = new Canvas(arrows, boxes, this);
		JPanel extra = new JPanel(new BorderLayout());
		extra.setOpaque(true);
		extra.add(canvas, BorderLayout.CENTER);
		JScrollPane scrollPane = new JScrollPane(extra); //adding the scrollpane to our canvas
		scrollPane.setBorder(border());
		canvas.setViewport(scrollPane.getViewport());
		add(scrollPane, BorderLayout.CENTER);
		
		pack();
		setMinimumSize(getPreferredSize());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (saveIfNeeded(arrows, items))
					dispose();
			}
		});
		
		setLocationRelativeTo(null);
		setVisible(true); // allows the GUI to start as visible
		setExtendedState(JFrame.MAXIMIZED_BOTH);
	}


	private void createMenus(List<Arrow> arrows, List<Item> items) {
		JMenuBar bar = new JMenuBar(); // menu bar
		
		JMenu file = new JMenu("File"); // file menu
		bar.add(file);
		JMenuItem newProject = new JMenuItem("New Project"); // new project menu item
		newProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK)); //hotkey to create a new project
		file.add(newProject);
		newProject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if( saveIfNeeded(arrows, items) ) {
					deselect();
					items.clear();
					arrows.clear();
					boxes.clear();									
					tableModel.setItemList(items);
					canvas.deselect();				
					saveFile = null;
					setTitle(TITLE + " - New Document");
					save.setEnabled(false);
				}
			}
		});

		JMenuItem openProject = new JMenuItem("Open Project..."); // open project menu item		
		//hotkey to open a project
		openProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK)); 
		file.add(openProject);
		openProject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if( saveIfNeeded(arrows, items) ) {					
					deselect();	
					openFile(boxes, arrows, items);
					tableModel.setItemList(items);
					canvas.deselect();
				}
			}
		});
		file.addSeparator();

		save = new JMenuItem("Save"); // save menu
		KeyStroke keyStrokeToSave = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK); 
		save.setAccelerator(keyStrokeToSave); //hotkey to save a project
		file.add(save);
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if( saveFile == null )
					saveAs(boxes, arrows, items);
				else
					save(boxes, arrows, items);
			}
		});
		save.setEnabled(false);
		
		JMenuItem saveAs = new JMenuItem("Save As..."); // save button		 
		file.add(saveAs);
		saveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAs(boxes, arrows, items);
			}
		});
		
		file.addSeparator();

		JMenuItem exit = new JMenuItem("Exit"); // exit button
		KeyStroke keyStrokeToExit = KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK); 
		exit.setAccelerator(keyStrokeToExit); //hotkey for exiting
		file.add(exit);
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (saveIfNeeded(arrows, items))
					dispose();			
			}
		});	

		JMenu edit = new JMenu("Edit"); // edit menu
		JMenuItem makebox = new JMenuItem("Make Prompt"); //another way to make prompt
		makebox.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK)); //hotkey to create a new prompt
		edit.add(makebox);
		makebox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addBox();
			}
		});
		beginChoiceItem = new JMenuItem("Begin Choice..."); //another way to make prompt
		beginChoiceItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_DOWN_MASK)); //hotkey to start a new choice
		edit.add(beginChoiceItem);
		beginChoiceItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				beginChoice();
			}
		});		
		beginChoiceItem.setEnabled(false);
		
		bar.add(edit);
		

		JMenu mode = new JMenu("Game"); // game menu
		JMenuItem playerModeItem = new JMenuItem("Play Game");		 
		//hotkey to get to game mode
		playerModeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK)); 
		playerModeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				List<Box> startingBoxes = Box.getStartingBoxes(boxes);
				if (startingBoxes.size() == 1) {					
					setVisible(false);
					new PlayerModeGUI(startingBoxes.get(0), Editor.this, boxes, arrows, items, titleField.getText().trim(), currencyField.getText().trim());
				} else {
					JOptionPane.showMessageDialog(Editor.this,
							"You must have exactly one prompt with no incoming choices to make the game playable.", "Game Not Playable!", JOptionPane.ERROR_MESSAGE);
				};
			}

		});

		mode.add(playerModeItem);
		bar.add(mode);

		setJMenuBar(bar);	
	}

	private void addBox() {
		JViewport viewport = canvas.getViewport();
		Dimension size = viewport.getExtentSize();
		int x = (int)Math.round((random.nextDouble()*(size.getWidth() - Box.WIDTH) + viewport.getViewPosition().getX() + Box.WIDTH / 2)*canvas.getZoom());
		int y = (int)Math.round((random.nextDouble()*(size.getHeight() - Box.HEIGHT) + viewport.getViewPosition().getY() + Box.HEIGHT / 2)*canvas.getZoom());

		Box box = new Box(x, y, "");
		canvas.addBox(box);
		selectBox(box, true);
		makeDirty();
		textArea.grabFocus();
	}

	private JPanel createEastPanel() {

		JDialog itemWindow = makeItemDialog();

		//Panel for entire east section
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		//Panel for prompts-related buttons
		JPanel promptsPanel = new JPanel(new GridLayout(4,1, GAP, GAP));
		promptsPanel.setBorder(border("Prompts"));

		JButton makePromptButton = new JButton("Make Prompt");
		makePromptButton.setToolTipText("Make a new prompt.");
		makePromptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				addBox();
			}
		});

		promptsPanel.add(makePromptButton);

		beginChoiceButton = new JButton("Begin Choice...");
		beginChoiceButton.setToolTipText("Create a new choice, beginning at the currently selected prompt. Then, click on the ending prompt to link the two with a choice.");
		beginChoiceButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				beginChoice();
			}
		});

		beginChoiceButton.setEnabled(false);
		promptsPanel.add(beginChoiceButton);
		
		recolorPromptButton = new JButton("Recolor Prompt");
		recolorPromptButton.setToolTipText("Recolor the selected prompt.");
		recolorPromptButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Box box = (Box) canvas.getSelected();
				box.recolor();
				canvas.repaint();
				makeDirty();
			}

		});
		recolorPromptButton.setEnabled(false);
		promptsPanel.add(recolorPromptButton);
		
		
		deletePromptButton = new JButton("Delete Prompt");		
		deletePromptButton.setToolTipText("Delete the selected prompt.");
		deletePromptButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				canvas.deleteBox();
				statusLabel.setText("Prompt deleted");
				makeDirty();
			}

		});
		deletePromptButton.setEnabled(false);
		
		promptsPanel.add(deletePromptButton);
		promptsPanel.setMaximumSize(promptsPanel.getMinimumSize());
		
		panel.add(promptsPanel);
		
		panel.add(javax.swing.Box.createVerticalStrut(GAP));
		
		
		//Panel for choices-related buttons
		JPanel choicesPanel = new JPanel(new GridLayout(5,1, GAP, GAP));
		choicesPanel.setBorder(border("Choices"));

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
				statusLabel.setText("Choice shifted to earlier position");
				makeDirty();
			}

		});		
		upButton.setEnabled(false);
		choicesPanel.add(upButton);
		
		choiceOrderLabel = new JLabel("");
		choiceOrderLabel.setHorizontalAlignment(JLabel.CENTER);
		choicesPanel.add(choiceOrderLabel);
		
		downButton = new BasicArrowButton(BasicArrowButton.SOUTH);
		downButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				canvas.makeArrowLater();
				statusLabel.setText("Choice shifted to later position");
				makeDirty();
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
				statusLabel.setText("Choice deleted");
				makeDirty();
			}

		});
		deleteChoiceButton.setEnabled(false);
		
		choicesPanel.add(deleteChoiceButton);
		choicesPanel.setMaximumSize(choicesPanel.getMinimumSize());
		
		panel.add(choicesPanel);
		
		panel.add(javax.swing.Box.createVerticalGlue());

		return panel;
	}

	private void beginChoice() {
		canvas.startArrowCheck();
		statusLabel.setText("Click second prompt to create choice...");		
	}

	private JPanel createSouthPanel() {
		
		JPanel panel = new JPanel(new BorderLayout());
		
		
		JPanel statusPanel = new JPanel(new BorderLayout());
		statusPanel.setBorder(border());
		
		statusPanel.add(new JLabel("Status: "), BorderLayout.WEST);
		
		statusLabel = new JLabel("");
		statusPanel.add(statusLabel, BorderLayout.CENTER);
		
		panel.add(statusPanel, BorderLayout.NORTH);
		
		textArea = new JTextArea(6, 20);
		textArea.setLineWrap(true);
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
		scrolling.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
		scrolling.setBorder(border("Text"));

		panel.add(scrolling, BorderLayout.SOUTH);
		
		return panel;
	}

	private JPanel createNorthPanel() {		
		//Panel for whole north area
		JPanel panel = new JPanel(new BorderLayout());

		//Panel for title and currency labels
		JPanel labelPanel = new JPanel(new GridLayout(2,1, GAP, GAP));
		labelPanel.setBorder(border());
		JLabel titleLabel = new JLabel("Title:");
		titleLabel.setHorizontalAlignment(JLabel.RIGHT);
		labelPanel.add(titleLabel);
		labelPanel.add(new JLabel("Currency:"));		
		panel.add(labelPanel, BorderLayout.WEST);

		//Panel for title and currency fields
		JPanel fieldPanel = new JPanel(new GridLayout(2, 1, GAP, GAP));
		fieldPanel.setBorder(border());
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
				statusLabel.setText("Zoom changed to " + (slider.getValue() + 0.0));
			}
		});	

		slider.setPaintLabels(true);

		JPanel zoomPanel = new JPanel(new BorderLayout());
		zoomPanel.setBorder(border("Zoom"));
		zoomPanel.add(slider, BorderLayout.CENTER);

		panel.add(zoomPanel, BorderLayout.SOUTH);	
		

		return panel;
	}
	
	private JDialog makeItemDialog(){

		//Setting parameters to create the table for item creation
		JDialog itemWindow = new JDialog(this, "Choice Details", true);
		itemWindow.setLayout(new BorderLayout());
		
		JTextArea textArea = new JTextArea(6, 20);
		textArea.setLineWrap(true);
		JScrollPane scrolling = new JScrollPane(textArea);
		scrolling.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 	
		scrolling.setBorder(border("Choice Text"));
		
		textArea.setEditable(false);	
		
		itemWindow.add(scrolling, BorderLayout.NORTH);	
		
		
		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.setBorder(border("Available Items"));
		JPanel buttonPanel = new JPanel(new GridLayout(2,1, GAP, GAP));
		JPanel mainPanel = new JPanel(new BorderLayout());


		JTable itemTable = new JTable(tableModel);
		itemTable.setFillsViewportHeight(true);
		JScrollPane tableScroll = new JScrollPane(itemTable);
		tableScroll.setBorder(border());
		tableScroll.setPreferredSize(new Dimension(250, 500));
		JButton addItem = new JButton("Add Item");
		JButton deleteItem = new JButton("Delete Item");
		buttonPanel.add(addItem);
		addItem.setToolTipText("Add an item to the list of available items.");

		//Listener for add item button that creates a new item
		addItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				tableModel.addItem("New Item");
				makeDirty();
			}

		});
		// Adding delete item button to the button panel
		buttonPanel.add(deleteItem);
		deleteItem.setToolTipText("Delete the selected item(s) from the list of available items.");
		deleteItem.setEnabled(false);
		deleteItem.addActionListener(new ActionListener() {
			// Listener for delete button that checks if the user wants to delete the item
			@Override
			public void actionPerformed(ActionEvent arg0) {
			
				if(itemTable.getSelectedRow()!= -1) {
					if(JOptionPane.showConfirmDialog(itemWindow, "Are you sure you want to delete " + 
							tableModel.getValueAt(itemTable.getSelectedRow(), 1) + "?", "Delete Item?", 
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						Arrow selected = (Arrow) canvas.getSelected();
						Item item = tableModel.getItems().get(itemTable.getSelectedRow());
						for(Arrow arrow : canvas.getArrows()) 
							arrow.removeItem(item);
						itemTextArea.setText(selected.heldItemText());
						operatorField.setText(selected.getRequirementsText());
						tableModel.deleteItem(itemTable.getSelectedRow());
						makeDirty();
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
		JPanel panel = new JPanel(new GridLayout(1,3, GAP, GAP));
		panel.setBorder(border());
		JButton and = new JButton("AND");
		JButton or = new JButton("OR");
		JButton not = new JButton("NOT");

		panel.add(and);
		and.setToolTipText("Makes the choice require both items or expressions. "
				+ "Use AND between two expressions.");

		and.addActionListener(new ActionListener() {
			// Listener for AND button that adds text to the items checked field 
			@Override
			public void actionPerformed(ActionEvent arg0) {
				operatorField.append(" AND ");
			}
		});

		panel.add(or);
		or.setToolTipText("Makes the choice require either of the two items or expressions. "
				+ "Use OR between two expressions.");

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

		JPanel itemsNeededPanel = new JPanel(new BorderLayout());
		itemsNeededPanel.setBorder(border("Items Player Must Have to Make this Choice"));
		itemsNeededPanel.add(operatorField,BorderLayout.CENTER);
		itemsNeededPanel.add(panel,BorderLayout.SOUTH);
		mainPanel.add(itemsNeededPanel,BorderLayout.CENTER);
		itemWindow.add(mainPanel, BorderLayout.CENTER);
		JPanel saveOrCancelPanel = new JPanel(new GridLayout(1,2, GAP, GAP));//south
		saveOrCancelPanel.setMinimumSize(new Dimension(300,100));
		saveOrCancelPanel.setPreferredSize(new Dimension(300,100));
		saveOrCancelPanel.setBorder(border());
		JButton saveClose = new JButton("Save and Close");
		saveOrCancelPanel.add(saveClose);
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
		
		JButton cancel = new JButton("Cancel");
		saveOrCancelPanel.add(cancel);
		cancel.setToolTipText("Close the Details window without saving the expression in the Items the Player Needs field.");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				itemWindow.setVisible(false);
			}			
		});


		itemTextArea = new JTextArea();
		itemTextArea.setEditable(false);
		JPanel itemsGottenPanel = new JPanel(new BorderLayout());//north
		itemsGottenPanel.setBorder(border("Items Player Gets for this Choice"));
		itemsGottenPanel.setMinimumSize(new Dimension(300,200));
		itemsGottenPanel.setPreferredSize(new Dimension(300,200));

		itemsGottenPanel.add(itemTextArea,BorderLayout.CENTER);
		JButton givenAdd = new JButton("Add");
		givenAdd.setToolTipText("Add an item that the player gets when they make this choice.");
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
		JButton givenDelete = new JButton("Remove");
		givenDelete.setEnabled(false);
		givenDelete.setToolTipText("Removes the selected item from the items they player gets when they make this choice.");
		givenDelete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Arrow arrow = (Arrow) canvas.getSelected();
				for(int row:itemTable.getSelectedRows())
					arrow.removeItem(tableModel.getItems().get(row));

				itemTextArea.setText(arrow.heldItemText());
			}

		});
		JPanel givenButtons = new JPanel(new GridLayout(1,2, GAP, GAP));
		givenButtons.setBorder(border());
		givenButtons.add(givenAdd);
		givenButtons.add(givenDelete);
		itemsGottenPanel.add(givenButtons, BorderLayout.SOUTH);
		mainPanel.add(itemsGottenPanel, BorderLayout.NORTH);

		mainPanel.add(saveOrCancelPanel, BorderLayout.SOUTH);
		itemWindow.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		itemWindow.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				itemWindow.setVisible(false);
			}
			
			@Override
			public void windowOpened(WindowEvent e) {
				textArea.setText(canvas.getSelected().getText());
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
		
		itemTable.putClientProperty("terminateEditOnFocusLost", true);
		
		
		tableModel.addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				Arrow arrow = (Arrow) canvas.getSelected();
				itemTextArea.setText(arrow.heldItemText());				
			}			
		});
		
		
		itemWindow.pack();
		return itemWindow;
	}

	public void selectBox(Box box, boolean isNew) {
		textArea.setText(box.getText());
		beginChoiceButton.setEnabled(boxes.size() >= 2);
		beginChoiceItem.setEnabled(boxes.size() >= 2);
		recolorPromptButton.setEnabled(true);
		deletePromptButton.setEnabled(true);
		deleteChoiceButton.setEnabled(false);
		itemButton.setEnabled(false);
		
		if( isNew )
			statusLabel.setText("Prompt created");
		else
			statusLabel.setText("Prompt selected");		
	}
	
	public void selectArrow(Arrow arrow, boolean isNew) {
		textArea.setText(arrow.getText());
		beginChoiceButton.setEnabled(false);
		beginChoiceItem.setEnabled(false);
		recolorPromptButton.setEnabled(false);
		deletePromptButton.setEnabled(false);
		deleteChoiceButton.setEnabled(true);
		itemButton.setEnabled(true);
		
		upButton.setEnabled(arrow.getOrder() > 1);
		downButton.setEnabled(arrow.getOrder() < arrow.getStart().getOutgoing().size());
		choiceOrderLabel.setText(arrow.getOrder() + "");		
		
		if( isNew ) {
			statusLabel.setText("Choice created");
			makeDirty();
		}
		else
			statusLabel.setText("Choice selected");
	}
	
	public void deselect() {
		textArea.setText("");
		beginChoiceButton.setEnabled(false);
		beginChoiceItem.setEnabled(false);
		recolorPromptButton.setEnabled(false);
		deletePromptButton.setEnabled(false);
		deleteChoiceButton.setEnabled(false);
		itemButton.setEnabled(false);
		
		upButton.setEnabled(false);
		downButton.setEnabled(false);
		choiceOrderLabel.setText("");		
		
		statusLabel.setText("");
	}
	
	public void makeDirty() {
		if( saveFile != null )
			setTitle(TITLE + " - *" + saveFile.getName());
		else
			setTitle(TITLE + " - *New Document");		
		save.setEnabled(true);		
	}
	
	//Tries to save if there's unsaved work
	//Returns true is everything's fine and false if you need to stop what you're doing
	private boolean saveIfNeeded(List<Arrow> arrows, List<Item> items) {
		if (save.isEnabled()) { //save menu is only enabled when project has unsaved changes
			int ask = JOptionPane.showConfirmDialog(Editor.this, "Do you want to save first?", "Save?",
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (ask == JOptionPane.YES_OPTION) {
				if( saveFile == null )
					return saveAs(boxes, arrows, items); 
				else
					return save(boxes, arrows, items);
			}
			else if(ask == JOptionPane.CANCEL_OPTION)
				return false;					
		}
		
		return true;
	}	

	public Canvas getCanvas() {
		return canvas;
	}
	
	private static Border border(String title) {
		return BorderFactory.createCompoundBorder(border(), BorderFactory.createTitledBorder(title));
	}	
	
	private static Border border() {	
		return BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP);
	}
}