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
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
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
	      //JPanel panel = new JPanel(new BorderLayout());
	      
	   //   JPanel content = new JPanel(new GridLayout(0,1));
	  //    JLabel label1 = new JLabel("Situation");
	//	     content.add(label1, BorderLayout.NORTH);
	//	     frame.add(content);
		    
	   
	      JPanel panel = new JPanel(new GridLayout(4,0));
		    JLabel label1 = new JLabel("Situation");
		     panel.add(label1, BorderLayout.CENTER);
	      JRadioButton JRadioButton = new JRadioButton("Choice1");
	      JRadioButton.setSelected(true);
	      JRadioButton JRadioButton2 = new JRadioButton("Choice2");
	      JRadioButton2.setSelected(true);
	     panel.add(JRadioButton, BorderLayout.CENTER);
	     panel.add(JRadioButton2, BorderLayout.CENTER);
	    panel.add(new JButton("Submit"), BorderLayout.CENTER);

	    frame.add(panel);
	      
	      

	     
	      


			
	
		     
	     frame.setSize(800,700);
	     frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	     frame.setVisible(true);
	   }
	 }


