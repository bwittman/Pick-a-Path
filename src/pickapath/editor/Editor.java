package pickapath.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
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
import javax.swing.JSplitPane;
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

import pickapath.game.GameGUI;
import pickapath.model.BooleanExpression;
import pickapath.model.BooleanExpressionException;
import pickapath.model.CanvasObject;
import pickapath.model.Choice;
import pickapath.model.InvalidStartingPromptException;
import pickapath.model.Model;
import pickapath.model.ModelListener;
import pickapath.model.Prompt;
import pickapath.model.State;

@SuppressWarnings("serial")
public class Editor extends JFrame implements ModelListener {

	//Editor window widgets
	private JTextField titleField;
	private JTextField currencyField;
	private Canvas canvas;
	private JTextArea textArea;
	private JButton beginChoiceButton;
	private JButton detailsButton;
	private JSlider slider;
	private BasicArrowButton upButton;
	private BasicArrowButton downButton;

	private JButton deletePromptButton;
	private JButton deleteChoiceButton;
	private JButton recolorPromptButton;
	private JLabel choiceOrderLabel;
	private JLabel statusLabel;

	//Details dialog widgets
	private JDialog detailsDialog;

	//Menu items
	private JMenuItem saveItem;
	private JMenuItem beginChoiceItem;
	private JMenuItem detailsItem;
	private JMenuItem recolorPromptItem;

	//Functional members
	private Model model = new Model();
	private File saveFile = null;
	private Random random = new Random();
	private boolean loading = false;

	//Constants
	private static final int SLIDER_MAX = 150;
	private static final int SLIDER_MIN = 25;
	private static final int SLIDER_MAJOR_TICK = 25;
	private static final int SLIDER_MINOR_TICK = 5;
	public static final int GAP = 5;
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

	public Editor() {
		super(TITLE + " - New Document");		
		model.addModelListener(this);

		createMenus();
		createDetailsDialog();
		
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(createNorthPanel(), BorderLayout.NORTH);
		topPanel.add(createEastPanel(), BorderLayout.EAST);
		
		canvas = new Canvas(model, this);
		JScrollPane scrollPane = new JScrollPane(canvas); //adding the scrollpane to our canvas
		JViewport viewport = scrollPane.getViewport();
		viewport.setPreferredSize(new Dimension(Canvas.MIN_WIDTH, Canvas.MIN_HEIGHT));
		scrollPane.setBorder(border());
		canvas.setScrollPane(scrollPane);
		
		//Change mouse-wheel listener so that it only fires if Ctrl is not down.
		//That way, Ctrl is used for zoom and no Ctrl is used for scrolling.
		MouseWheelListener[] listeners = scrollPane.getMouseWheelListeners();
		if( listeners.length > 0 ) {
			MouseWheelListener oldListener = listeners[0];
			scrollPane.removeMouseWheelListener(oldListener);
			scrollPane.addMouseWheelListener(new MouseWheelListener() {
	
				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
					if( !e.isControlDown() ) {
						oldListener.mouseWheelMoved(e);
					}
				}				
			});
		}
		topPanel.add(scrollPane, BorderLayout.CENTER);


		scrollPane.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				viewport.setViewSize(scrollPane.getSize());
				//viewport.setSize(scrollPane.getSize());
				canvas.resetBounds();  
			}
		});
		
		JPanel bottomPanel = createSouthPanel();

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPanel);
		add(splitPane, BorderLayout.CENTER);

		pack();
		setMinimumSize(getPreferredSize());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});

		setLocationRelativeTo(null); //starts the GUI centered (when not maximized)
		setVisible(true); // allows the GUI to start as visible
		setExtendedState(JFrame.MAXIMIZED_BOTH); //maximizes GUI


	}


	private boolean saveProject() {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(saveFile));
			model.write(out);
			out.close();
		} 
		catch (IOException e) {
			return false;
		}

		return true;
	}

	private boolean saveProjectAs() {

		JFileChooser fileSelect = new JFileChooser();
		fileSelect.setDialogTitle("Save Project");
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

			return saveProject();
		}
		return false;
	}


	private void openProject() {  //open a saved file
		JFileChooser fileSelect = new JFileChooser();
		fileSelect.setDialogTitle("Open Project");
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
				model.read(in);
				in.close();				
			} 
			catch (IOException | ClassNotFoundException e) {
			}
		}
	}


	private void createMenus() {
		JMenuBar bar = new JMenuBar(); // menu bar

		JMenu file = new JMenu("File"); // file menu
		bar.add(file);
		JMenuItem newProject = new JMenuItem("New Project"); // new project menu item
		newProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK)); //hotkey to create a new project
		file.add(newProject);
		newProject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if( saveIfNeeded("starting a new project") ) {
					model.newProject();
				}
			}
		});

		JMenuItem openProject = new JMenuItem("Open Project..."); // open project menu item		
		//hotkey to open a project
		openProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK)); 
		file.add(openProject);
		openProject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if( saveIfNeeded("opening another project") ) {					
					openProject();
				}
			}
		});
		file.addSeparator();

		saveItem = new JMenuItem("Save"); // save menu
		KeyStroke keyStrokeToSave = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK); 
		saveItem.setAccelerator(keyStrokeToSave); //hotkey to save a project
		file.add(saveItem);
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if( saveFile == null )
					saveProjectAs();
				else
					saveProject();
			}
		});
		saveItem.setEnabled(false);

		JMenuItem saveAs = new JMenuItem("Save As..."); // save button		 
		file.add(saveAs);
		saveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveProjectAs();
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
				exit();		
			}
		});	

		JMenu editMenu = new JMenu("Edit"); // edit menu
		JMenuItem makePromptItem = new JMenuItem("Make Prompt"); //another way to make prompt
		makePromptItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK)); //hotkey to create a new prompt
		editMenu.add(makePromptItem);
		makePromptItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addPrompt();
			}
		});
		beginChoiceItem = new JMenuItem("Begin Choice..."); //another way to make a choice
		beginChoiceItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_DOWN_MASK)); //hotkey to start a new choice
		editMenu.add(beginChoiceItem);
		beginChoiceItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				beginChoice();
			}
		});		
		beginChoiceItem.setEnabled(false);


		recolorPromptItem = new JMenuItem("Recolor Prompt"); //another way to recolor a prompt
		recolorPromptItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK)); //hotkey to recolor a prompt
		editMenu.add(recolorPromptItem);
		recolorPromptItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				model.recolorPrompt();
			}
		});		
		recolorPromptItem.setEnabled(false);




		detailsItem = new JMenuItem("Choice Details...");
		detailsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK)); //hotkey to create a new prompt
		editMenu.add(detailsItem);
		detailsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				detailsDialog.setVisible(true);
			}
		});
		detailsItem.setEnabled(false);

		bar.add(editMenu);


		JMenu mode = new JMenu("Game"); // game menu
		JMenuItem playerModeItem = new JMenuItem("Play Game");		 
		//hotkey to get to game mode
		playerModeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK)); 
		playerModeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				try{					
					setVisible(false);
					new GameGUI(new State(model), Editor.this);
				} catch(InvalidStartingPromptException e) {
					JOptionPane.showMessageDialog(Editor.this,
							"You must have exactly one prompt with no incoming choices to make the game playable.", "Game Not Playable!", JOptionPane.ERROR_MESSAGE);
				};
			}

		});

		mode.add(playerModeItem);
		bar.add(mode);

		setJMenuBar(bar);	
	}

	private void exit() {
		if (saveIfNeeded("exiting"))
			dispose();		
	}


	private void addPrompt() {
		JViewport viewport = canvas.getScrollPane().getViewport();
		Dimension size = viewport.getExtentSize();
		int x = (int)Math.round((random.nextDouble()*(size.getWidth() - Prompt.WIDTH - 2*Canvas.SPACING) + viewport.getViewPosition().getX() + Prompt.WIDTH / 2 + Canvas.SPACING)*canvas.getZoom());
		int y = (int)Math.round((random.nextDouble()*(size.getHeight() - Prompt.HEIGHT - 2*Canvas.SPACING) + viewport.getViewPosition().getY() + Prompt.HEIGHT / 2 + Canvas.SPACING)*canvas.getZoom());

		Prompt prompt = new Prompt(x, y, "");
		model.add(prompt);
	}

	private JPanel createEastPanel() {		

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
				addPrompt();
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
			public void actionPerformed(ActionEvent event) {
				model.recolorPrompt();
			}
		});
		recolorPromptButton.setEnabled(false);
		promptsPanel.add(recolorPromptButton);


		deletePromptButton = new JButton("Delete Prompt");		
		deletePromptButton.setToolTipText("Delete the selected prompt.");
		deletePromptButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				model.removePrompt();
			}
		});
		deletePromptButton.setEnabled(false);

		promptsPanel.add(deletePromptButton);
		promptsPanel.setMaximumSize(promptsPanel.getMinimumSize());

		panel.add(promptsPanel);

		panel.add(Box.createVerticalStrut(GAP));


		//Panel for choices-related buttons
		JPanel choicesPanel = new JPanel(new GridLayout(5,1, GAP, GAP));
		choicesPanel.setBorder(border("Choices"));

		detailsButton = new JButton("Choice Details...");
		detailsButton.setToolTipText("Specify details including items lost, items gained, items required, and currency changed by this choice.");
		detailsButton.setEnabled(false);
		choicesPanel.add(detailsButton);
		detailsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				detailsDialog.setVisible(true);			
			}

		});

		upButton = new BasicArrowButton(BasicArrowButton.NORTH);
		upButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				model.makeArrowEarlier();
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
				model.makeArrowLater();
			}

		});		
		downButton.setEnabled(false);
		choicesPanel.add(downButton);


		deleteChoiceButton = new JButton("Delete Choice");

		deleteChoiceButton.setToolTipText("Delete the selected choice.");
		deleteChoiceButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {				
				model.removeArrow();			}

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
				model.setText(textArea.getText());
			}
		});
		JScrollPane scrolling = new JScrollPane(textArea);
		scrolling.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
		scrolling.setBorder(border("Text"));

		panel.add(scrolling, BorderLayout.CENTER);

		return panel;
	}

	private JPanel createNorthPanel() {		
		//Panel for whole north area
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(border());		
		
		JPanel labelAndFieldsPanel = new JPanel(new GridLayout(1,2, GAP*2, GAP*2));
		labelAndFieldsPanel.setBorder(border());
		
		//Panel for title label and field
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
		JLabel titleLabel = new JLabel("Title:");
		titlePanel.add(titleLabel);
		titlePanel.add(javax.swing.Box.createHorizontalStrut(GAP));
		titleField = new JTextField();
		titleField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent event) {
				update();
			}

			@Override
			public void insertUpdate(DocumentEvent event) {
				update();
			}

			@Override
			public void removeUpdate(DocumentEvent event) {
				update();
			}

			private void update() {
				if( !loading )
					model.setTitle(titleField.getText().trim());
			}
		});		
		
		titlePanel.add(titleField);
		
		labelAndFieldsPanel.add(titlePanel);		

		//Panel for currency label and field
		JPanel currencyPanel = new JPanel();
		currencyPanel.setLayout(new BoxLayout(currencyPanel, BoxLayout.X_AXIS));
		currencyPanel.add(new JLabel("Currency:"));		
		currencyPanel.add(Box.createHorizontalStrut(GAP));	

		currencyField = new JTextField();
		currencyField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				update();
			}

			private void update() {
				if( !loading )
					model.setCurrencyName(currencyField.getText().trim());
			}
		});
		currencyPanel.add(currencyField);		
		labelAndFieldsPanel.add(currencyPanel);
		
		panel.add(labelAndFieldsPanel, BorderLayout.NORTH);

		//Zoom slider		
		slider = new JSlider(JSlider.HORIZONTAL, SLIDER_MIN, SLIDER_MAX, 100);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(SLIDER_MAJOR_TICK);
		slider.setMinorTickSpacing(SLIDER_MINOR_TICK);
		slider.setSnapToTicks(true);
		
		Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
		for( int i = SLIDER_MIN; i <= SLIDER_MAX; i += SLIDER_MAJOR_TICK )
			labelTable.put(i, new JLabel(i + "%"));
		
		slider.setLabelTable(labelTable);

		//Set up different fonts for different slider settings
	
		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				canvas.setZoom(slider.getValue()/ 100.0);
				statusLabel.setText("Zoom changed to " + slider.getValue()  + "%");
			}
		});	

		slider.setPaintLabels(true);

		JPanel zoomPanel = new JPanel(new BorderLayout());
		zoomPanel.setBorder(border("Zoom"));
		zoomPanel.add(slider, BorderLayout.CENTER);

		panel.add(zoomPanel, BorderLayout.SOUTH);

		//Adds Ctrl+mouse wheel for zooming in and out		
		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {

				if( e.isControlDown() ) {
					int value = slider.getValue();
					if( e.getWheelRotation() > 0 ) {
						if( value > slider.getMinimum() )
							slider.setValue(value - SLIDER_MINOR_TICK);
					}
					else if( e.getWheelRotation() < 0 ) {
						if( value < slider.getMaximum() )
							slider.setValue(value + SLIDER_MINOR_TICK);
						
					}	
				}
			}

		});

		return panel;
	}

	private void createDetailsDialog(){

		//Setting parameters to create the table for item creation
		detailsDialog = new JDialog(this, "Choice Details", true);
		detailsDialog.setLayout(new BorderLayout());

		detailsDialog.getRootPane().registerKeyboardAction(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						cancelDetails();
					}
				},
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);

		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.setBorder(border("Available Items"));
		JPanel buttonPanel = new JPanel(new GridLayout(2,1, GAP, GAP));

		JTable itemTable = new JTable(model);
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
				String name = JOptionPane.showInputDialog(detailsDialog, "What is the name of the new item?", "Item Name?", JOptionPane.QUESTION_MESSAGE);
				if( name != null ) {
					name = name.trim();
					if( name.isEmpty() )
						model.addItem("New Item");
					else
						model.addItem(name);
				}
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

				if(itemTable.getSelectedRowCount() == 1 ) {					
					if(JOptionPane.showConfirmDialog(detailsDialog, "Are you sure you want to delete " + 
							model.getValueAt(itemTable.getSelectedRow(), 1) + "?", "Delete Item?", 
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {	
						model.deleteItem(itemTable.getSelectedRow());					
					}	
				}
				else if(itemTable.getSelectedRowCount() > 1 ) {
					int[] rows = itemTable.getSelectedRows();
					Arrays.sort(rows); //in case they don't arrive in order
					StringBuilder message = new StringBuilder("Are you sure you want to delete ");
					if( rows.length == 2 )
						message.append(model.getValueAt(rows[0], 1)).append(" and ").append(model.getValueAt(rows[1], 1));
					else {
						for(int i = 0; i < rows.length - 1; ++i )
							message.append(model.getValueAt(rows[i], 1)).append(", ");
						message.append("and ").append(model.getValueAt(rows[rows.length - 1], 1));
					}
					message.append("?");

					if(JOptionPane.showConfirmDialog(detailsDialog, message.toString(), "Delete Items?", 
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {							
						for( int i = rows.length - 1; i >= 0; --i )
							model.deleteItem(rows[i]);					
					}					
				}
			}

		});
		//Enables a row to be selected 
		itemTable.setRowSelectionAllowed(true);
		tablePanel.add(buttonPanel, BorderLayout.SOUTH);
		tablePanel.add(tableScroll, BorderLayout.CENTER);
		detailsDialog.add(tablePanel, BorderLayout.WEST);

		//makes it so that clicking away finishes the table edit
		//otherwise, item names might be saved after the user adds the item to gained or lost lists
		itemTable.putClientProperty("terminateEditOnFocusLost", true);


		//End table stuff


		JPanel mainPanel = new JPanel(new BorderLayout());

		JTextArea textArea = new JTextArea(6, 20);
		textArea.setLineWrap(true);
		JScrollPane scrolling = new JScrollPane(textArea);
		scrolling.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 	
		scrolling.setBorder(border("Choice Text"));

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
				model.setText(textArea.getText());
			}
		});

		mainPanel.add(scrolling, BorderLayout.NORTH);	



		JPanel centerPanel = new JPanel(new GridLayout(2, 2, GAP, GAP));


		JTextArea gainedItemsTextArea = new JTextArea();
		gainedItemsTextArea.setEditable(false);
		JPanel itemsGottenPanel = new JPanel(new BorderLayout());//north
		itemsGottenPanel.setBorder(border("Items Player Gains for this Choice"));
		itemsGottenPanel.setMinimumSize(new Dimension(300,200));
		itemsGottenPanel.setPreferredSize(new Dimension(300,200));

		itemsGottenPanel.add(gainedItemsTextArea,BorderLayout.CENTER);
		JButton getsAddButton = new JButton("Add");
		getsAddButton.setToolTipText("Add an item that the player gains when they make this choice.");
		getsAddButton.setEnabled(false);
		getsAddButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Choice choice = (Choice) model.getSelected();
				for(int row: itemTable.getSelectedRows())
					model.addGainedItem(model.getItem(row));

				gainedItemsTextArea.setText(choice.getGainedItemsText());
			}

		});
		JButton getRemoveButton = new JButton("Remove");
		getRemoveButton.setEnabled(false);
		getRemoveButton.setToolTipText("Removes the selected item from the items they player gains when they make this choice.");
		getRemoveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Choice choice = (Choice) model.getSelected();
				for(int row:itemTable.getSelectedRows())
					model.removeGainedItem(model.getItem(row));

				gainedItemsTextArea.setText(choice.getGainedItemsText());
			}

		});
		JPanel givenButtons = new JPanel(new GridLayout(1,2, GAP, GAP));
		givenButtons.setBorder(border());
		givenButtons.add(getsAddButton);
		givenButtons.add(getRemoveButton);
		itemsGottenPanel.add(givenButtons, BorderLayout.SOUTH);
		centerPanel.add(itemsGottenPanel);


		JTextArea mustHaveTextArea = new JTextArea();
		JPanel panel = new JPanel(new GridLayout(1,3, GAP, GAP));
		panel.setBorder(border());
		JButton and = new JButton("AND");
		JButton or = new JButton("OR");
		JButton not = new JButton("NOT");

		panel.add(and);
		and.setToolTipText("Inserts AND, requiring both items or expressions to fulfill the requirement.");

		and.addActionListener(new ActionListener() {
			// Listener for AND button that adds text to the items checked field 
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mustHaveTextArea.append(" AND ");
			}
		});

		panel.add(or);
		or.setToolTipText("Inserts OR, allowing either of the two items or expressions to fulfill the requirement.");

		or.addActionListener(new ActionListener() {
			// Listener for OR button that adds text to the items checked field 
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mustHaveTextArea.append(" OR ");
			}
		});

		panel.add(not);
		not.setToolTipText("Inserts NOT, requiring an item to be absent or the negation of an expression to fulfill the requirement.");


		not.addActionListener(new ActionListener() {
			// Listener for NOT button that adds text to the items checked field 
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mustHaveTextArea.append("NOT ");
			}
		});

		JPanel mustHavePanel = new JPanel(new BorderLayout());
		mustHavePanel.setBorder(border("Items Player Must Have to Make this Choice"));
		mustHavePanel.add(mustHaveTextArea,BorderLayout.CENTER);
		mustHavePanel.add(panel,BorderLayout.SOUTH);
		centerPanel.add(mustHavePanel);



		JTextArea lostItemsTextArea = new JTextArea();
		lostItemsTextArea.setEditable(false);
		JPanel itemsLostPanel = new JPanel(new BorderLayout());
		itemsLostPanel.setBorder(border("Items Player Loses for this Choice"));


		itemsLostPanel.add(lostItemsTextArea,BorderLayout.CENTER);
		JButton lostAddButton = new JButton("Add");
		lostAddButton.setToolTipText("Add an item that the player loses when they make this choice.");
		lostAddButton.setEnabled(false);
		lostAddButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				Choice choice = (Choice) model.getSelected();
				for(int row: itemTable.getSelectedRows())
					model.addLostItem(model.getItem(row));

				lostItemsTextArea.setText(choice.getLostItemsText());
			}

		});
		JButton lostRemoveButton = new JButton("Remove");
		lostRemoveButton.setEnabled(false);
		lostRemoveButton.setToolTipText("Removes the selected item from the items they player loses when they make this choice.");
		lostRemoveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Choice choice = (Choice) model.getSelected();
				for(int row:itemTable.getSelectedRows())
					model.removeLostItem(model.getItem(row));

				lostItemsTextArea.setText(choice.getLostItemsText());
			}

		});
		JPanel lostButtons = new JPanel(new GridLayout(1,2, GAP, GAP));
		lostButtons.setBorder(border());
		lostButtons.add(lostAddButton);
		lostButtons.add(lostRemoveButton);
		itemsLostPanel.add(lostButtons, BorderLayout.SOUTH);
		centerPanel.add(itemsLostPanel);

		JTextArea currencyChangeTextArea = new JTextArea();
		JPanel currencyPanel = new JPanel(new BorderLayout());
		currencyPanel.setBorder(border("Currency Change for this Choice"));


		currencyPanel.add(currencyChangeTextArea,BorderLayout.CENTER);
		centerPanel.add(currencyPanel);


		itemTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			//Listener that enables delete button if a row is selected
			@Override
			public void valueChanged(ListSelectionEvent event) {
				deleteItem.setEnabled(itemTable.getSelectedRow() != -1);
				getsAddButton.setEnabled(itemTable.getSelectedRow() != -1);
				getRemoveButton.setEnabled(itemTable.getSelectedRow() != -1);
				lostAddButton.setEnabled(itemTable.getSelectedRow() != -1);
				lostRemoveButton.setEnabled(itemTable.getSelectedRow() != -1);
			}

		});

		mainPanel.add(centerPanel, BorderLayout.CENTER);

		//Save or cancel buttons at the bottom
		JPanel saveOrCancelPanel = new JPanel(new GridLayout(1,2, GAP, GAP));//south
		saveOrCancelPanel.setMinimumSize(new Dimension(300,100));
		saveOrCancelPanel.setPreferredSize(new Dimension(300,100));
		saveOrCancelPanel.setBorder(border());
		JButton saveClose = new JButton("Save and Close");
		saveOrCancelPanel.add(saveClose);
		saveClose.setToolTipText("Evaluates all the fields and saves them to the choice, unless there are errors.");
		saveClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String text = mustHaveTextArea.getText().trim();

				if( !text.isEmpty() ) {
					try {
						model.setBooleanExpression(BooleanExpression.makeExpression(text, model));
					} catch (BooleanExpressionException e) {
						JOptionPane.showMessageDialog(detailsDialog, "The expression describing what items the player must have was invalid.", "Invalid Must-Have Items!", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				else
					model.setBooleanExpression(null);

				
				text = currencyChangeTextArea.getText().trim();
				
				if( !text.isEmpty() ) {				
					try {
						model.setCurrencyChange(Integer.parseInt(currencyChangeTextArea.getText().trim()));
					}
					catch(NumberFormatException e) {
						JOptionPane.showMessageDialog(detailsDialog, "The currency change must be a positive or negative integer.", "Invalid Currency Change!", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				else
					model.setCurrencyChange(0);
				
				model.saveDetails();
				Editor.this.textArea.setText(model.getSelected().getText());
				detailsDialog.setVisible(false);				
			}
		});

		JButton cancel = new JButton("Cancel");
		saveOrCancelPanel.add(cancel);
		cancel.setToolTipText("Close the Details window without saving any changes.");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cancelDetails();
			}			
		});		

		mainPanel.add(saveOrCancelPanel, BorderLayout.SOUTH);

		detailsDialog.add(mainPanel, BorderLayout.CENTER);


		detailsDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		detailsDialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				cancelDetails();
			}			
		});

		detailsDialog.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				Choice choice = (Choice)model.getSelected();
				textArea.setText(choice.getText());
				gainedItemsTextArea.setText(choice.getGainedItemsText());
				lostItemsTextArea.setText(choice.getLostItemsText());
				mustHaveTextArea.setText(choice.getMustHaveText());
				if( choice.getCurrencyChange() == 0 )
					currencyChangeTextArea.setText("");
				else
					currencyChangeTextArea.setText("" + choice.getCurrencyChange());
				model.makeSnapShot();				
			}					
		});

		model.addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				if( model.getSelected() instanceof Choice ) {
					Choice choice = (Choice) model.getSelected();
					gainedItemsTextArea.setText(choice.getGainedItemsText());
					lostItemsTextArea.setText(choice.getLostItemsText());
					mustHaveTextArea.setText(choice.getMustHaveText());
				}
			}			
		});

		detailsDialog.pack();
	}

	private void cancelDetails() {
		detailsDialog.setVisible(false);
		model.restoreSnapShot();
	}

	private void select(CanvasObject object, boolean isNew) {
		//Deselect
		if( object == null ) {
			textArea.setText("");
			beginChoiceButton.setEnabled(false);
			beginChoiceItem.setEnabled(false);
			recolorPromptButton.setEnabled(false);
			recolorPromptItem.setEnabled(false);
			deletePromptButton.setEnabled(false);
			deleteChoiceButton.setEnabled(false);
			detailsButton.setEnabled(false);
			detailsItem.setEnabled(false);

			upButton.setEnabled(false);
			downButton.setEnabled(false);
			choiceOrderLabel.setText("");		

			statusLabel.setText("");
		}
		else {
			textArea.setText(object.getText());
			String kind;
			boolean isPrompt;

			//Select arrow
			if( object instanceof Choice ) {				
				Choice choice = (Choice)object;				
				
				upButton.setEnabled(choice.getOrder() > 1);
				downButton.setEnabled(choice.getOrder() < choice.getStart().getOutgoing().size());
				choiceOrderLabel.setText(choice.getOrder() + "");	

				kind = "Choice";
				isPrompt = false;			

			}
			//Select prompt
			else {
				upButton.setEnabled(false);
				downButton.setEnabled(false);
				choiceOrderLabel.setText("");

				kind = "Prompt";
				isPrompt = true;
			}
			
			beginChoiceButton.setEnabled(isPrompt);
			beginChoiceItem.setEnabled(isPrompt);
			recolorPromptButton.setEnabled(isPrompt);
			recolorPromptItem.setEnabled(isPrompt);
			deletePromptButton.setEnabled(isPrompt);
			deleteChoiceButton.setEnabled(!isPrompt);
			detailsButton.setEnabled(!isPrompt);
			detailsItem.setEnabled(!isPrompt);

			if( isNew )
				statusLabel.setText(kind + " created");
			else
				statusLabel.setText(kind + " selected");	

			textArea.grabFocus();
		}
	}

	//Tries to save if there's unsaved work
	//Returns true is everything's fine and false if you need to stop what you're doing
	private boolean saveIfNeeded(String activity) {

		if (model.isDirty()) {
			int ask = JOptionPane.showConfirmDialog(Editor.this, "Do you want to save before " + activity + "?", "Save?",
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (ask == JOptionPane.YES_OPTION) {
				if( saveFile == null )
					return saveProjectAs(); 
				else
					return saveProject();
			}
			else if(ask == JOptionPane.CANCEL_OPTION)
				return false;					
		}

		return true;
	}	

	public static Border border(String title) {
		return BorderFactory.createCompoundBorder(border(), BorderFactory.createTitledBorder(title));
	}	

	public static Border border() {	
		return BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP);
	}

	@Override
	public void updateModel(Model.Event event, CanvasObject object) {
		if( loading )
			return;	

		String asterisk = model.isDirty() ? "*" : "";
		if( saveFile != null )
			setTitle(TITLE + " - " + asterisk + saveFile.getName());
		else
			setTitle(TITLE + " - " + asterisk + "New Document");

		saveItem.setEnabled(model.isDirty());
		boolean prompt = object instanceof Prompt;

		switch(event) {
		case CREATE: 
			select(object, true);
			break;
		case DELETE:			
			statusLabel.setText((prompt ? "Prompt" : "Choice") + " deleted");
			break;
		case SELECT: 
			select(object, false);
			break;
		case MOVE:			
			statusLabel.setText((prompt ? "Prompt" : "Choice") + " moved");
			break;
		case RECOLOR:
			statusLabel.setText("Prompt recolored");
			break;
		case TEXT_CHANGE:
			statusLabel.setText((prompt ? "Prompt" : "Choice") + " text changed");
			break;		
		case TITLE_CHANGE:
			statusLabel.setText("Title changed to \"" + model.getTitle() + "\"");
			break;
		case CURRENCY_CHANGE:
			statusLabel.setText("Currency name changed to \"" + model.getCurrencyName() + "\"");
			break;
		case DETAILS_CHANGE: 
			statusLabel.setText("Choice details updated");			
			break;		
		case ORDER_EARLIER:
		case ORDER_LATER: {
			Choice choice = (Choice) model.getSelected();
			choiceOrderLabel.setText("" + choice.getOrder());
			statusLabel.setText("Choice shifted to " + (event == Model.Event.ORDER_EARLIER ? "earlier" : "later" ) + " position");			 
			break;
		}	
		case NEW:
			loading = true;
			saveFile = null;
			titleField.setText("");	
			currencyField.setText("");
			statusLabel.setText("Created new project");
			loading = false;
			break;
		case SAVE:
			statusLabel.setText("Successfully saved to file " + saveFile.getName());
			break;
		case LOAD:
			loading = true;
			select(null, false);
			titleField.setText(model.getTitle());
			currencyField.setText(model.getCurrencyName());
			statusLabel.setText("Successfully loaded from file " + saveFile.getName());
			loading = false;
			break;
		}	
	}
}