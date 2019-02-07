package pickapath;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.io.File;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

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
		frame.setMinimumSize(new Dimension(400,100));
		frame.setDefaultCloseOperation(
				JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
		
		
	}
	public class Button extends JPanel {

	}
	

	}
	


