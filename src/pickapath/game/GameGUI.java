package pickapath.game;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
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
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import pickapath.editor.Editor;
import pickapath.model.Choice;
import pickapath.model.InvalidStartingPromptException;
import pickapath.model.Item;
import pickapath.model.Model;
import pickapath.model.State;

@SuppressWarnings("serial")
public class GameGUI extends JFrame {

	private JPanel choicePanel;
	private JTextArea promptArea;
	private JTextArea inventoryArea;
	private List<JRadioButton> buttonList;
	private JFrame editor;
	private State state;
	private JLabel currencyLabel = new JLabel();
	private JLabel currencyNameLabel = new JLabel();
	private JButton submitButton;


	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException
				| IllegalAccessException e) {}

		new GameGUI();
	}

	private void saveGame() {   //save current work to a file
		JFileChooser fileSelect = new JFileChooser();
		fileSelect.setDialogTitle("Save Game");
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
		fileSelect.setDialogTitle("Open Game");
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
					setCurrencyLabels();					
					return true;
				}
				else {
					Model model = new Model();
					model.read(stream);
					try {
						state = new State(model);
						
						return true;
					}
					catch(InvalidStartingPromptException e) {
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
	
	private void setCurrencyLabels() {		
		if( !state.getModel().getCurrencyName().isEmpty() )
			currencyNameLabel.setText(state.getModel().getCurrencyName() + ": ");
		else
			currencyNameLabel.setText("Money: ");
		
		currencyLabel.setText("" + state.getCurrency());		
	}

	public GameGUI() {
		if( loadGame() ) {
			setTitle(state.getModel().getTitle());
			setup();
		}
	}
	

	private void setup() {		
		
		//Prompt
		promptArea = new JTextArea("Prompt");
		promptArea.setEditable(false);
		promptArea.setLineWrap(true);		
		
		JScrollPane promptScroll = new JScrollPane(promptArea);
		promptScroll.setBorder(Editor.border("Prompt"));
		promptScroll.setPreferredSize(new Dimension(800, 200));		
		
	
		//Choices
		choicePanel = new JPanel();
		choicePanel.setLayout(new BoxLayout(choicePanel, BoxLayout.Y_AXIS));
		JScrollPane choiceScroll = new JScrollPane(choicePanel);
		choiceScroll.setBorder(Editor.border("Choices"));
		choiceScroll.setPreferredSize(new Dimension(400, 400));
		buttonList = new ArrayList<JRadioButton>();		
		
		//Inventory
		inventoryArea = new JTextArea("");
		inventoryArea.setEditable(false);
		JScrollPane inventoryScroll = new JScrollPane(inventoryArea);
		inventoryScroll.setBorder(Editor.border("Inventory"));
		inventoryScroll.setPreferredSize(new Dimension(400, 400));
		
		JSplitPane choiceInventorySplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, choiceScroll, inventoryScroll);
		JSplitPane topBottomSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, promptScroll, choiceInventorySplit);
		
		add(topBottomSplit, BorderLayout.CENTER);
		
		
		JPanel bottomPanel = new JPanel(new GridLayout(1, 2, Editor.GAP, Editor.GAP));
		bottomPanel.setBorder(Editor.border());

		
		//Submit
		JPanel submitPanel = new JPanel(new FlowLayout());
		submitPanel.setBorder(Editor.border());
		submitButton = new JButton("Submit");
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
						
						currencyLabel.setText("" + state.getCurrency());	
						populateChoices();
					}
				} else {
					if( editor != null )
						editor.setVisible(true);
					setVisible(false);
					dispose();
				}

			}

		});
		submitPanel.add(submitButton);
		bottomPanel.add(submitPanel);
		
		//Currency
		JPanel currencyPanel = new JPanel(new FlowLayout());
		currencyPanel.setBorder(Editor.border());
		currencyPanel.add(currencyNameLabel);
		currencyPanel.add(currencyLabel);
		bottomPanel.add(currencyPanel);
		
		add(bottomPanel, BorderLayout.SOUTH);
		
		
		JMenuBar bar = new JMenuBar(); // menu bar
		JMenu file = new JMenu("File"); // file button

		bar.add(file);
		JMenuItem save = new JMenuItem("Save As...");
		JMenuItem open = new JMenuItem("Open");
		if( editor != null )
			open.setEnabled(false);
		file.add(save);
		file.add(open);
		
		
		setJMenuBar(bar);
		KeyStroke keyStrokeToSave = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK); 
		save.setAccelerator(keyStrokeToSave); //hotkey to save a game
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveGame();

			}
		});
		KeyStroke keyStrokeToOpen = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK); 
		open.setAccelerator(keyStrokeToOpen); //hotkey to open a game
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadGame();
				setTitle(state.getModel().getTitle());
				populateChoices();
			}
		});




		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if( editor != null )
					editor.setVisible(true);
				setVisible(false);
				dispose();
			}
		});


		populateChoices();
		setMinimumSize(getPreferredSize());
		pack();
		
		
		List<Image> icons = new ArrayList<Image>();
		icons.add(new ImageIcon("icon16x16.png").getImage());
		icons.add(new ImageIcon("icon32x32.png").getImage());
		icons.add(new ImageIcon("icon64x64.png").getImage());
		icons.add(new ImageIcon("icon128x128.png").getImage());
		icons.add(new ImageIcon("icon256x256.png").getImage());		
		
		setIconImages(icons);	
		
		setLocationRelativeTo(null); // Starts the GUI centered
		setVisible(true);		
	}

	public GameGUI(State state, JFrame editor) {
		super(state.getModel().getTitle());
		this.state = state;
		this.editor = editor;
		setCurrencyLabels();		
		setup();		
	}

	private void populateChoices() {
		buttonList.clear();
		choicePanel.removeAll();
		List<Choice> choices = state.getChoices();
		promptArea.setText(state.getPrompt().getText());
		ButtonGroup group = new ButtonGroup();
		for (Choice choice : choices) {			
			JRadioButton button = new JRadioButton("<html>" + choice.getText().replaceAll("\\n", "<br/>") + "</html>");
			group.add(button);
			buttonList.add(button);
			choicePanel.add(button);			
		}
		
		choicePanel.add(Box.createVerticalGlue());
		choicePanel.revalidate();
		choicePanel.repaint();
		
		if( choices.size() == 0)
			submitButton.setText("End");			
	}
}

