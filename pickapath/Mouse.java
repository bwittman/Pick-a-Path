package pickapath;
import java.awt.*;
import javax.swing.JFrame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class Mouse implements MouseMotionListener {

	int mx, my;
	boolean mouseDragged;
	
	
	public static void main(String[] args) {
		Mouse mouse = new Mouse();
	}
	
	/*public Mouse() {
		addMouseMotionListener(this);
	}
	*/
	@Override
	public void mouseDragged(MouseEvent m) {
		mx = m.getX();
		my = m.getY();
		
		mouseDragged = true;

	}

	@Override
	public void mouseMoved(MouseEvent m) {
		mx = m.getX();
		my = m.getY();
		
		mouseDragged = false;
		

	}

}
