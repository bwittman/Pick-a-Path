package pickapath;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

import javax.swing.JPanel;

public class Canvas extends JPanel implements MouseMotionListener, MouseListener {
	private List<Box> boxes;
	private Box selectedBox = null;
	
	
	int mx, my;
	boolean mouseDragged;
	boolean selected = false;
	

	public Canvas(List<Box> boxes) {
		// TODO Auto-generated constructor stub
		this.boxes = boxes;
	}

	public void paint(Graphics g) {
		super.paint(g);
		for (Box box: boxes) {
			g.setColor(Color.GREEN);

			g.fillRect(box.getX() - box.getWidth()/2, box.getY() - box.getHeight()/2, box.getWidth(), box.getHeight());
			if (box == selectedBox) {
				g.setColor(Color.WHITE);
			} else {
				g.setColor(Color.BLACK);
			}
			
			g.drawRect(box.getX() - box.getWidth()/2, box.getY() - box.getHeight()/2, box.getWidth(), box.getHeight());

		}


	}
	@Override
	public void mouseDragged(MouseEvent e) {
		mx = e.getX();
		my = e.getY();
		
		mouseDragged = true;
		

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mx = e.getX();
		my = e.getY();
		
		mouseDragged = false;
		

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		selected = true;
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
