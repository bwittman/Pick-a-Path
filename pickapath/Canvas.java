package pickapath;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Canvas extends JPanel {
	public void paint(Graphics g) {
		super.paint(g);
		
	    g.setColor(Color.PINK);

	      g.fillRect(0, 0, 200, 300);

	      g.setColor(Color.BLACK);

	      g.drawRect(0, 0, 200, 300);
	}
	

}
