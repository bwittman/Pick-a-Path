package pickapath;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;


public class PlayerMode {
	
	
	    public static void main(String[] args) {
	      JFrame frame = new JFrame("PlayerMode");
	      JPanel panel = new JPanel(new BorderLayout());
	    //  panel.add(new JButton("Center"), BorderLayout.CENTER);
	      panel.add(new JButton("South"), BorderLayout.SOUTH);
	      


			
			
	     frame.add(panel);
	     frame.setSize(800,700);
	     frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	     frame.setVisible(true);
	   }
	 }


