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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

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

import pickapath.model.Arrow;
import pickapath.model.InvalidStartingBoxException;
import pickapath.model.Item;
import pickapath.model.Model;
import pickapath.model.State;

@SuppressWarnings("serial")
public class PlayerModeGUI extends JFrame {

	private JPanel choicePanel;
	private JTextArea promptArea;
	private JTextArea inventoryArea;
	private List<JRadioButton> buttonList;
	private JFrame frame;
	private State state;


	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException
				| IllegalAccessException e) {}

		new PlayerModeGUI();
	}

	private void saveGame() {   //save current work to a file
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
			if (!path.toLowerCase().endsWith(".ppp"))
				selectedFile = new File(path + ".ppp");			
			boolean safe = true;
			if(selectedFile.exists()) {
				if( JOptionPane.showConfirmDialog(this, "Are you sure you want to save over " + selectedFile + "?", "Overwrite File?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
					safe = false;
				}
			}
			if(safe) {
				try {
					ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(selectedFile));
					state.writeProgress(out);
					out.close();
				} catch ( IOException e) {
					JOptionPane.showMessageDialog(this, "Unable to save to file.", "Saving Failed!", JOptionPane.ERROR_MESSAGE);
				} 
			}
		}

	}
	private boolean loadGame() {
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
				if( selectedFile.toString().toLowerCase().endsWith(".ppp")) {
					state.readProgress(stream);
					setTitle(state.getModel().getTitle());
					return true;
				}
				else {
					Model model = new Model();
					model.read(stream);
					try {
						state = new State(model);
						
						return true;
					}
					catch(InvalidStartingBoxException e) {
						JOptionPane.showMessageDialog(this, "This is an unplayable game because no starting point is indicated.",
								"Game is not Playable!", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			catch(IOException | ClassNotFoundException e) {
				JOptionPane.showMessageDialog(this,"File missing or corrupted.", "File Missing or Corrupted!", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		return false;
	}
	
	public PlayerModeGUI() {
		if( loadGame() ) {
			setTitle(state.getModel().getTitle());
			setup();
		}
	}
	

	private void setup() {
		buttonList = new ArrayList<JRadioButton>();
		choicePanel = new JPanel();
		
		promptArea = new JTextArea("Prompt");
		promptArea.setEditable(false);
		promptArea.setLineWrap(true);
		
		inventoryArea = new JTextArea("");
		inventoryArea.setEditable(false);

		JScrollPane scrolling = new JScrollPane(promptArea);

		scrolling.setPreferredSize(new Dimension(400, 200));		
		scrolling.setMaximumSize(new Dimension(2048, 400));
		add(scrolling, BorderLayout.NORTH);
		
		

		scrolling = new JScrollPane(inventoryArea);
		add(scrolling, BorderLayout.EAST);



		JPanel bottom = new JPanel(new FlowLayout());
		JPanel center = new JPanel(new FlowLayout());


		JButton submitButton = new JButton("Submit");
		submitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (state.getChoices().size() > 0) {
					int choice = -1;
					for (int i = 0; i < buttonList.size(); i++) {
						if (buttonList.get(i).isSelected())
							choice = i;
					}
					if (choice != -1) {
						
						state.makeChoice(choice);

						String text = "";
						for(Item item : state.getInventory())
							text += item.getName() + "\n";
						inventoryArea.setText(text);
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
				saveGame();

			}
		});
		KeyStroke keyStrokeToOpen = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK); 
		open.setAccelerator(keyStrokeToOpen); //hotkey to open a project
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				loadGame();
				setTitle(state.getModel().getTitle());
				populateChoices();
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

	public PlayerModeGUI(State state, JFrame frame) {
		super(state.getModel().getTitle());
		this.state = state;
		this.frame = frame;
		
		setup();		
	}

	private void populateChoices() {
		buttonList.clear();
		choicePanel.removeAll();
		List<Arrow> choices = state.getChoices();
		choicePanel.setLayout(new GridLayout(choices.size(), 1));
		promptArea.setText(state.getPrompt().getText());
		ButtonGroup group = new ButtonGroup();
		for (Arrow arrow : choices) {			
			JRadioButton button = new JRadioButton(arrow.getText());
			group.add(button);
			buttonList.add(button);
			choicePanel.add(button);			
		}

		promptArea.setMaximumSize(new Dimension(2048, 400));
		validate();
		pack();
	}
}

