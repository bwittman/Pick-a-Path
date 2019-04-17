package pickapath;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;




public class PlayerModeGUI {



	private JPanel choicePanel;
	private JFrame playerMode;
	private JTextArea boxInformation;
	private List<JRadioButton> buttonList;
	private Set<Item> items;
	private List<Arrow> arrowList;

	public static void main(String[] args) {
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
					List<Box> startingBoxes = Main.getStartingBoxes(boxes);
					if (startingBoxes.size() == 1) {	

						box = startingBoxes.get(0);

					} else {
						JOptionPane.showMessageDialog(null, "This is an unplayable game because no starting point is indicated." , "Error!", JOptionPane.ERROR_MESSAGE);
						return;
					}

				}


				in.close();

				new PlayerModeGUI(box, null, itemsHeld);

			} catch (FileNotFoundException e1) {

			} catch (IOException e1) {

			} catch (ClassNotFoundException e1) {

			}
		}
	}

	public PlayerModeGUI(Box startingBox, JFrame frame) {
		this(startingBox, null, new HashSet<Item>());
	}

	public PlayerModeGUI(Box startingBox, JFrame frame, Set<Item> items) {

		this.items = items;
		buttonList = new ArrayList<JRadioButton>();
		arrowList = new ArrayList<Arrow>();
		playerMode = new JFrame("PlayerMode");
		choicePanel = new JPanel();
		boxInformation = new JTextArea("Situation");
		boxInformation.setEditable(false);

		boxInformation.setLineWrap(true);
		JScrollPane scrolling = new JScrollPane(boxInformation);

		scrolling.setPreferredSize(new Dimension(400, 200));		
		scrolling.setMaximumSize(new Dimension(2048, 400));
		playerMode.add(scrolling, BorderLayout.NORTH);



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
						items.addAll(arrow.getItems());
						Box next = arrow.getEnd();
						populateChoices(next);

					}
				} else {
					if( frame != null )
						frame.setVisible(true);
					playerMode.setVisible(false);
					playerMode.dispose();
				}

			}

		});
		bottom.add(submitButton);
		playerMode.add(bottom, BorderLayout.SOUTH);
		center.add(choicePanel);
		playerMode.add(center, BorderLayout.CENTER);

		playerMode.setSize(800, 700);
		playerMode.setMinimumSize(new Dimension(350,350));
		playerMode.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		playerMode.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if( frame != null )
					frame.setVisible(true);
				playerMode.setVisible(false);
			}
		});


		populateChoices(startingBox);
		playerMode.setVisible(true);

	}

	private void populateChoices(Box box) {
		buttonList.clear();
		arrowList.clear();
		choicePanel.removeAll();
		choicePanel.setLayout(new GridLayout(box.getOutgoing().size(), 1));// where the JRadio Button info is formed
		// from the arrows
		boxInformation.setText(box.getText()); // text in the boxes
		boxInformation.validate();
		ButtonGroup group = new ButtonGroup();// groups the JButtons together for formatting in the gridLayout
		for (Arrow arrow : box.getOutgoing()) {
			if( arrow.satisfies(items) ) {
				JRadioButton button = new JRadioButton(arrow.getText());
				group.add(button);// adds all the buttons to the middle
				buttonList.add(button);
				arrowList.add(arrow);
				choicePanel.add(button);
			}
		}

		boxInformation.setMaximumSize(new Dimension(2048, 400));
		playerMode.validate();
		playerMode.pack();
	}






}

