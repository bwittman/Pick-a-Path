package pickapath;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
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

public class Main extends JFrame {

	private Canvas canvas;
	private JTextArea textArea;
	private JButton arrowButton;
	private JButton items;
	private JSlider slider;
	private JTextArea operatorField;
	private Font[] fonts;
	private static final int MAX_SLIDER = 5;
	private static final int MIN_SLIDER = 1;

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

	private JDialog makeItemDialog(){

		//Setting parameters to create the table for item creation
		JDialog itemWindow = new JDialog(this,"Items",true);
		itemWindow.setLayout(new BorderLayout());
		JPanel tablePanel = new JPanel(new BorderLayout());
		JPanel buttonPanel = new JPanel(new GridLayout(2,1));
		JPanel mainPanel = new JPanel(new BorderLayout());

		ItemTableModel tableModel = new ItemTableModel();
		JTable itemTable = new JTable(tableModel);
		itemTable.setFillsViewportHeight(true);
		JScrollPane tableScroll = new JScrollPane(itemTable);
		tableScroll.setPreferredSize(new Dimension(200, 500));
		JButton addItem = new JButton("Add Item");
		JButton deleteItem = new JButton("Delete Item");
		buttonPanel.add(addItem);
		addItem.addActionListener(new ActionListener() {
			//Listener for add item button that creates a new item
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				tableModel.addItem("New Item");
			}

		});
		// Adding delete item button to the button panel
		buttonPanel.add(deleteItem);
		deleteItem.setEnabled(false);
		deleteItem.addActionListener(new ActionListener() {
			// Listener for delete button that checks if the user wants to delete the item
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub

				if(itemTable.getSelectedRow()!= -1) {
					if(JOptionPane.showConfirmDialog(itemWindow, "Are you sure you want to delete " + 
							tableModel.getValueAt(itemTable.getSelectedRow(), 1) + "?", "Delete Item?", 
							JOptionPane.YES_NO_OPTION)== JOptionPane.YES_OPTION)
						tableModel.deleteItem(itemTable.getSelectedRow());

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
		JPanel panel = new JPanel(new GridLayout(2,2));
		JButton and = new JButton("AND");
		JButton or = new JButton("OR");
		JButton not = new JButton("NOT");
		JButton check = new JButton("Check");
		//Listener for check button in the item window
		check.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// read from text, convert to number, look through list,get it, then evaluate
				String text = operatorField.getText().trim();
				try {
					int itemNumber = Integer.parseInt(text);
					List<Item> items = tableModel.getItems();
					for(Item item: items ) {
						if(item.getId() == itemNumber) {
							canvas.setBooleanExpression(new BooleanExpression(item));
							itemWindow.setVisible(false);
						}
					}

				}
				catch (NumberFormatException e) {}
			}
		});
		
		
				panel.add(and);
		panel.add(check);
		panel.add(or);
		
		panel.add(not);
		
		JButton cancel = new JButton("Cancel");
		
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				itemWindow.setVisible(false);
			}

		});
		
		
		JPanel itemsChecked = new JPanel(new BorderLayout());
		itemsChecked.setBorder(BorderFactory.createTitledBorder("Items Checked"));
		itemsChecked.add(operatorField,BorderLayout.CENTER);
		itemsChecked.add(panel,BorderLayout.SOUTH);
		mainPanel.add(itemsChecked,BorderLayout.CENTER);
		itemWindow.add(mainPanel, BorderLayout.CENTER);
		JPanel okCancel = new JPanel(new GridLayout(1,2));//south
		okCancel.setMinimumSize(new Dimension(300,100));
		okCancel.setPreferredSize(new Dimension(300,100));
		okCancel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JButton ok = new JButton("OK");
		okCancel.add(ok);
		okCancel.add(cancel);
		
		JTextArea itemTextArea = new JTextArea();
		itemTextArea.setEditable(false);
		JPanel itemsGiven = new JPanel(new BorderLayout());//north
		itemsGiven.setBorder(BorderFactory.createTitledBorder("Items Given"));
		itemsGiven.setMinimumSize(new Dimension(300,200));
		itemsGiven.setPreferredSize(new Dimension(300,200));
		
		itemsGiven.add(itemTextArea,BorderLayout.CENTER);
		JButton givenAdd = new JButton("Add");
		givenAdd.setEnabled(false);
		JButton givenDelete = new JButton("Delete");
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
				cancel.doClick();

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

	public Main() {
		super("PICK A PATH");
		List<Box> boxes = new ArrayList<Box>();
		List<Arrow> arrows = new ArrayList<Arrow>();

		JPanel panel = new JPanel(new BorderLayout());
		add(panel);
		JPanel numbers = new JPanel(new GridLayout(5, 0)); // how many buttons there are on the right side, needs
		canvas = new Canvas(arrows, boxes, this);
		JPanel extra = new JPanel(new BorderLayout());
		extra.setOpaque(true);
		extra.add(canvas, BorderLayout.CENTER);
		JScrollPane scrollPane = new JScrollPane(extra); //adding the scrollpane to our canvas
		canvas.setViewport(scrollPane.getViewport());


		panel.add(scrollPane, BorderLayout.CENTER);
		JDialog itemWindow = makeItemDialog();

		slider = new JSlider(JSlider.HORIZONTAL, MIN_SLIDER, MAX_SLIDER, 1);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(1);

		fonts = new Font[MAX_SLIDER - MIN_SLIDER + 1];
		fonts[0] = new JLabel().getFont();
		for (int i = 1; i < fonts.length; ++i)
			fonts[i] = fonts[0].deriveFont(fonts[0].getSize() / ((i + MIN_SLIDER + 1) / 2.0f));

		panel.add(slider, BorderLayout.NORTH);
		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				canvas.setZoom(1.0 / ((slider.getValue() + 1) / 2.0), fonts[slider.getValue() - MIN_SLIDER]);
			}

		});


		JButton makeBox = new JButton("Make Box");

		Random random = new Random();

		arrowButton = new JButton("Make Arrow");
		arrowButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				canvas.startArrowCheck();
			}

		}

				);

		arrowButton.setEnabled(false);
		makeBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JViewport viewport = canvas.getViewport();
				Dimension size = viewport.getExtentSize();
				canvas.addBox(new Box((int) ((random.nextInt((int)size.getWidth()) + (int)viewport.getViewPosition().getX())*canvas.getZoom()), random.nextInt((int) (((int)size.getHeight()) + (int)viewport.getViewPosition().getY()*canvas.getZoom())), 100, 50, ""));
			}
		}
				);

		numbers.add(makeBox); // make box button
		numbers.add(arrowButton); // make arrow button

		items = new JButton("Items"); //create item button
		items.setEnabled(false);
		numbers.add(items);
		items.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				itemWindow.setVisible(true);

			}

		}

				);

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
				if (JOptionPane.showConfirmDialog(Main.this, "Are you sure you want to delete all?", "",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
					canvas.deleteAllBoxes();

			}



		});
		panel.add(numbers, BorderLayout.EAST); // assigns the boxes to the right container

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

		KeyStroke keyStrokeToNewProject = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK); 
		nproject.setAccelerator(keyStrokeToNewProject); //hotkey to create a new project
		file.add(nproject);
		nproject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!boxes.isEmpty()) {
					if (JOptionPane.showConfirmDialog(Main.this, "Do you want to save first?", "Save?",
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						Saving.saveFile(boxes, arrows);
						canvas.deleteAllBoxes();
						Saving.openFile(boxes, arrows);
						canvas.repaint();
					} else {
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
					if (JOptionPane.showConfirmDialog(Main.this, "Do you want to save first?", "Save?",
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

						Saving.saveFile(boxes, arrows);
						canvas.deleteAllBoxes();
						Saving.openFile(boxes, arrows);
						canvas.repaint();

					} else {
						canvas.deleteAllBoxes();
						Saving.openFile(boxes, arrows);
						canvas.repaint();
						// boxes = Saving.openFile().boxes;
					}
				} else {
					Saving.openFile(boxes, arrows);
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
				Saving.saveFile(boxes, arrows);
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


		JMenu mode = new JMenu("Mode"); // mode button
		setJMenuBar(bar);


		JMenuItem playerModeItem = new JMenuItem("Player Mode");
		KeyStroke playerModeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK); 
		playerModeItem.setAccelerator(playerModeKeyStroke); //hotkey to get to player mode
		playerModeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				List<Box> startingBoxes = getStartingBoxes(boxes);
				if (startingBoxes.size() == 1) {					
					setVisible(false);
					//new PlayerMode(startingBoxes.get(0), frame);
					new PlayerMode_cli(startingBoxes.get(0));
				} else {
					JOptionPane.showMessageDialog(null,
							"You must have exactly one box with no incoming arrows before entering player mode!");
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

	public void setItemsEnabled(boolean enabled) {
		items.setEnabled(enabled);
	}
	
	public Canvas getCanvas() {
		return canvas;
	}
}