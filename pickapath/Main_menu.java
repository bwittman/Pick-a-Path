package pickapath;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Main_menu {
	public static void main(String[] args) {
		JFrame frame = new JFrame("Welcome to Pick-a-Path");
		JPanel panel = new JPanel();
		JButton editor_mode = new JButton("Editor Mode");
		JButton player_mode = new JButton("Player Mode");
		panel.add(editor_mode);
		panel.add(player_mode);
		frame.add(panel);
		frame.setSize(400, 100);
		frame.setDefaultCloseOperation(
				JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
		
	}

}
