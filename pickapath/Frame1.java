package pickapath;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.SystemColor;
import javax.swing.JComboBox;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class Frame1 {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Frame1 window = new Frame1();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Frame1() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 915, 476);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnMakeBox = new JButton("Make Box");
		btnMakeBox.setForeground(SystemColor.activeCaption);
		btnMakeBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(null, "MakeBox");
			}
		});
		btnMakeBox.setBackground(Color.DARK_GRAY);
		btnMakeBox.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnMakeBox.setBounds(0, 13, 125, 46);
		frame.getContentPane().add(btnMakeBox);
		
		JButton btnMakeArrow = new JButton("Make Arrow");
		btnMakeArrow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnMakeArrow.setForeground(SystemColor.activeCaption);
		btnMakeArrow.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnMakeArrow.setBackground(Color.DARK_GRAY);
		btnMakeArrow.setBounds(124, 13, 125, 46);
		frame.getContentPane().add(btnMakeArrow);
		
		JButton btnDeleteBox = new JButton("Delete Box");
		btnDeleteBox.setForeground(SystemColor.activeCaption);
		btnDeleteBox.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnDeleteBox.setBackground(Color.DARK_GRAY);
		btnDeleteBox.setBounds(248, 13, 125, 46);
		frame.getContentPane().add(btnDeleteBox);
		
		JButton btnDeleteArrow = new JButton("Delete Arrow");
		btnDeleteArrow.setForeground(SystemColor.activeCaption);
		btnDeleteArrow.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnDeleteArrow.setBackground(Color.DARK_GRAY);
		btnDeleteArrow.setBounds(371, 13, 125, 46);
		frame.getContentPane().add(btnDeleteArrow);
		
		JButton btnAddText = new JButton("Add Text");
		btnAddText.setForeground(SystemColor.activeCaption);
		btnAddText.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnAddText.setBackground(Color.DARK_GRAY);
		btnAddText.setBounds(489, 13, 125, 46);
		frame.getContentPane().add(btnAddText);
		
		JButton btnDeleteAll = new JButton("Delete All");
		btnDeleteAll.setForeground(SystemColor.activeCaption);
		btnDeleteAll.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnDeleteAll.setBackground(Color.DARK_GRAY);
		btnDeleteAll.setBounds(614, 13, 125, 46);
		frame.getContentPane().add(btnDeleteAll);
		
		JButton btnAddVariable = new JButton("Add Variable");
		btnAddVariable.setForeground(SystemColor.activeCaption);
		btnAddVariable.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnAddVariable.setBackground(Color.DARK_GRAY);
		btnAddVariable.setBounds(739, 13, 125, 46);
		frame.getContentPane().add(btnAddVariable);
		
		JScrollBar scrollBar = new JScrollBar();
		scrollBar.setBounds(876, 13, 21, 382);
		frame.getContentPane().add(scrollBar);
		
		JScrollBar scrollBar_1 = new JScrollBar();
		scrollBar_1.setOrientation(JScrollBar.HORIZONTAL);
		scrollBar_1.setBounds(0, 408, 897, 21);
		frame.getContentPane().add(scrollBar_1);
	}
}
