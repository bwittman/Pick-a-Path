package pickapath;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;

public class Canvas extends JPanel {
	private List<Box> boxes;

	public Canvas(List<Box> boxes) {
		// TODO Auto-generated constructor stub
		this.boxes = boxes;
	}

	public void paint(Graphics g) {
		super.paint(g);
		for (Box box: boxes) {
			g.setColor(Color.GREEN);

			g.fillRect(box.getX(), box.getY(), box.getWidth(), box.getHeight());

			g.setColor(Color.BLACK);

			g.drawRect(box.getX(), box.getY(), box.getWidth(), box.getHeight());
		}


	}
}
