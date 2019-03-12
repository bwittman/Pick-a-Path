package pickapath;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class PlayerMode {
	
	private JPanel choicePanel;
	private JFrame playerMode;
	private JTextArea boxInformation;
	private List<JRadioButton> buttonList;
	private Box situation;

	public static void main(String[] args) {


	}
	//Console mode
	public PlayerMode(Box box) {
		
		Scanner in = new Scanner(System.in);
		
		while(box.getOutgoing().size() > 0) {
			int counter = 1;
			for (Arrow arrow : box.getOutgoing()) {
				System.out.println(counter+ arrow.getText());
				counter++;
				

			}
			System.out.print("enter choice" + "\n");
			int choice = in.nextInt() -1;
			Arrow arrow = box.getOutgoing().get(choice);
			box = arrow.getEnd();
			
		}
		
		
	}

	
	//gui mode
	public PlayerMode(Box startingBox, JFrame frame) {

		buttonList = new ArrayList<JRadioButton>();
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
				if (situation.getOutgoing().size() > 0) {
					Arrow arrow = null;

					for (int i = 0; i < buttonList.size(); i++) {
						if (buttonList.get(i).isSelected())
							arrow = situation.getOutgoing().get(i);
					}
					if (arrow != null) {
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
				frame.setVisible(true);
				playerMode.setVisible(false);
			}
		});


		populateChoices(startingBox);
		playerMode.setVisible(true);

	}
	
	private void populateChoices(Box box) {
		situation = box;
		buttonList.clear();
		choicePanel.removeAll();
		choicePanel.setLayout(new GridLayout(box.getOutgoing().size(), 1));// where the JRadio Button info is formed
		// from the arrows
		boxInformation.setText(box.getText()); // text in the boxes
		boxInformation.validate();
		ButtonGroup group = new ButtonGroup();// groups the JButtons together for formatting in the gridLayout
		for (Arrow arrow : box.getOutgoing()) {
			JRadioButton button = new JRadioButton(arrow.getText());
			group.add(button);// adds all the buttons to the middle
			buttonList.add(button);
			choicePanel.add(button);

		}
		
		boxInformation.setMaximumSize(new Dimension(2048, 400));
		playerMode.validate();
		playerMode.pack();
	}

}
