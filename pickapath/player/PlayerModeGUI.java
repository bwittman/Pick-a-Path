package pickapath.player;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import pickapath.Arrow;
import pickapath.Box;
import pickapath.Item;
import pickapath.Saving;
import pickapath.editor.Editor;




public class PlayerModeGUI extends JFrame {



	private JPanel choicePanel;
	private JTextArea boxInformation;
	private JTextArea itemInformation;
	private List<JRadioButton> buttonList;
	private List<Arrow> arrowList;

	private List<Box> boxes;
	private List<Arrow> arrows;
	private List<Item> items;
	private Set<Item> itemsHeld;
	private Box box;


	public static void main(String[] args) {

		try {
			// Set cross-platform Java L&F (also called "Metal")
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException
				| IllegalAccessException e) {
			// handle exception
		}

		List<Box> boxes = new ArrayList<Box>();
		List<Arrow> arrows = new ArrayList<Arrow>();
		List<Item> items = new ArrayList<Item>();


		JFileChooser fileSelect = new JFileChooser();
		fileSelect.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.getName().toLowerCase().endsWith(".pap")|| file.getName().toLowerCase().endsWith(".ppp")||file.isDirectory();
			}

			@Override
			public String getDescription() {
				return "Pick-a-Path Files";
			}
		});
		if (fileSelect.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileSelect.getSelectedFile();

			try {
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(selectedFile));
				Box box = null;
				Set<Item> itemsHeld = new HashSet<Item>();

				if (selectedFile.toString().toLowerCase().endsWith(".ppp")) 
					box = Saving.readProgress(in, boxes, arrows, items, itemsHeld);
				else {
					Saving.read(in, boxes, arrows, items);
					List<Box> startingBoxes = Editor.getStartingBoxes(boxes);
					if (startingBoxes.size() == 1) {	

						box = startingBoxes.get(0);

					} else {
						JOptionPane.showMessageDialog(null, "This is an unplayable game because no starting point is indicated." , "Error!", JOptionPane.ERROR_MESSAGE);
						return;
					}

				}


				in.close();

				new PlayerModeGUI(box, null, boxes, arrows, items, itemsHeld);

			} catch (FileNotFoundException e1) {

			} catch (IOException e1) {

			} catch (ClassNotFoundException e1) {

			}
		}


	}

	public PlayerModeGUI(Box startingBox, JFrame frame, List<Box> boxes, List<Arrow> arrows, List<Item> items) {
		this(startingBox, frame, boxes, arrows, items, new HashSet<Item>());
	}
	private void saveFile() {   //save current work to a file

		JFileChooser fileSelect = new JFileChooser();
		fileSelect.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.getName().toLowerCase().endsWith(".ppp")||file.isDirectory();
			}

			@Override
			public String getDescription() {
				return ".ppp files";
			}
		});
		if (fileSelect.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileSelect.getSelectedFile();
			String path = selectedFile.getAbsolutePath();
			if (!path.toLowerCase().endsWith(".ppp")) {
				selectedFile = new File(path + ".ppp");



			}
			boolean safe = true;
			if(selectedFile.exists()) {
				if( JOptionPane.showConfirmDialog(this, "Are you sure you want to save over " + selectedFile + "?", "Overwrite File?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
					safe = false;
				}
			}
			if(safe) {
				try {
					ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(selectedFile));
					Saving.writeProgress(out, boxes, arrows, items, box, itemsHeld);
					out.close();
				} catch ( IOException e) {
					JOptionPane.showMessageDialog(this, "Unable to save to file.", "Saving Failed!", JOptionPane.ERROR_MESSAGE);
				} 
			}
		}

	}
	private void openFile() {
		JFileChooser fileSelect = new JFileChooser();
		fileSelect.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.getName().toLowerCase().endsWith(".pap")||file.isDirectory() 
						|| file.getName().toLowerCase().endsWith(".ppp");
			}

			@Override
			public String getDescription() {
				return "Pick-a-Path Files";
			}
		});
		if (fileSelect.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileSelect.getSelectedFile();

			try {
				ObjectInputStream stream = new ObjectInputStream(new FileInputStream(selectedFile));
				List <Box> boxes = new ArrayList<Box>();
				List <Arrow> arrows = new ArrayList<Arrow>();
				List <Item> items =new ArrayList<Item>();
				Set <Item> itemsHeld = new HashSet<Item>();
				if( selectedFile.toString().toLowerCase().endsWith(".ppp")) {
					box = Saving.readProgress(stream, boxes, arrows, items, itemsHeld);

				}
				else {
					Saving.read(stream, boxes, arrows, items);
					List<Box> startingBoxes = Editor.getStartingBoxes(boxes);
					if (startingBoxes.size() == 1) {	

						box = startingBoxes.get(0);


					} else {
						JOptionPane.showMessageDialog(this, "This is an unplayable game because no starting point is indicated.",
								"Game is not Playable!", JOptionPane.ERROR_MESSAGE);

						return;

					}
				}

				this.boxes = boxes;
				this.arrows = arrows;
				this.items = items;
				this.itemsHeld = itemsHeld;


			} catch(IOException | ClassNotFoundException e) {
				System.out.println("File missing or corrupted.");
			}

		}

	}

	public PlayerModeGUI(Box startingBox, JFrame frame, List<Box> boxes, List<Arrow> arrows, List<Item> items, Set<Item> outsideItems) {
		super("Player Mode");
		this.itemsHeld = outsideItems;
		this.boxes = boxes;
		this.arrows = arrows;
		this.items = items;
		this.box = startingBox;
		buttonList = new ArrayList<JRadioButton>();
		arrowList = new ArrayList<Arrow>();

		choicePanel = new JPanel();
		boxInformation = new JTextArea("Situation");
		boxInformation.setEditable(false);

		boxInformation.setLineWrap(true);
		
		itemInformation = new JTextArea("");
		itemInformation.setEditable(false);


		JScrollPane scrolling = new JScrollPane(boxInformation);

		scrolling.setPreferredSize(new Dimension(400, 200));		
		scrolling.setMaximumSize(new Dimension(2048, 400));
		add(scrolling, BorderLayout.NORTH);
		
		

		scrolling = new JScrollPane(itemInformation);
		add(scrolling, BorderLayout.EAST);



		JPanel bottom = new JPanel(new FlowLayout());
		JPanel center = new JPanel(new FlowLayout());


		JButton submitButton = new JButton("Submit");
		submitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (arrowList.size() > 0) {
					Arrow arrow = null;

					for (int i = 0; i < buttonList.size(); i++) {
						if (buttonList.get(i).isSelected())
							arrow = arrowList.get(i);
					}
					if (arrow != null) {
						itemsHeld.addAll(arrow.getItems());
						String text = "";
						for(Item item : itemsHeld)
							text += item.getName() + "\n";
						itemInformation.setText(text);
						
						box = arrow.getEnd();
						populateChoices();

					}
				} else {
					if( frame != null )
						frame.setVisible(true);
					setVisible(false);
					dispose();
				}

			}

		});
		bottom.add(submitButton);
		add(bottom, BorderLayout.SOUTH);
		center.add(choicePanel);
		add(center, BorderLayout.CENTER);

		JMenuBar bar = new JMenuBar(); // menu bar
		JMenu file = new JMenu("File"); // file button

		bar.add(file);
		JMenuItem save = new JMenuItem("Save");
		JMenuItem open = new JMenuItem("Open");
		if( frame != null )
			open.setEnabled(false);
		file.add(save);
		file.add(open);
		
		
		setJMenuBar(bar);
		KeyStroke keyStrokeToSave = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK); 
		save.setAccelerator(keyStrokeToSave); //hotkey to save a project
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveFile();

			}
		});
		KeyStroke keyStrokeToOpen = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK); 
		open.setAccelerator(keyStrokeToOpen); //hotkey to open a project
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				openFile();

			}


		});




		setSize(800, 700);
		setMinimumSize(new Dimension(350,350));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if( frame != null )
					frame.setVisible(true);
				setVisible(false);
				dispose();
			}
		});


		populateChoices();
		setVisible(true);

	}

	private void populateChoices() {
		buttonList.clear();
		arrowList.clear();
		choicePanel.removeAll();
		int count = 0;
		for (Arrow arrow : box.getOutgoing()) 
			if( arrow.satisfies(itemsHeld) ) 
				count++;
		choicePanel.setLayout(new GridLayout(count, 1));// where the JRadio Button info is formed
		// from the arrows
		boxInformation.setText(box.getText()); // text in the boxes
		boxInformation.validate();
		ButtonGroup group = new ButtonGroup();// groups the JButtons together for formatting in the gridLayout
		for (Arrow arrow : box.getOutgoing()) {
			if( arrow.satisfies(itemsHeld) ) {
				JRadioButton button = new JRadioButton(arrow.getText());
				group.add(button);// adds all the buttons to the middle
				buttonList.add(button);
				arrowList.add(arrow);
				choicePanel.add(button);
			}
		}

		boxInformation.setMaximumSize(new Dimension(2048, 400));
		validate();
		pack();
	}






}

