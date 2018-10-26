package pickapath;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JPanel;

public class Canvas extends JPanel implements MouseMotionListener, MouseListener {
	private List<Box> boxes;
	private List<Arrow> arrows;
	private Box selectedBox = null;
	private int startXBox;
	private int startYBox;
	private int startXDrag;
	private int startYDrag;
	boolean mouseDragged;
	private Main main;
	boolean arrowCheck;


	public Canvas(List<Arrow> arrows, List<Box> boxes, Main main) {
		// TODO Auto-generated constructor stub
		this.boxes = boxes;
		this.arrows = arrows;
		addMouseListener(this);
		addMouseMotionListener(this);
		this.main = main;
	}

	public void paint(Graphics g) {
		super.paint(g);
		for (Arrow arrow: arrows) {
			g.setColor(Color.BLACK);
			g.drawLine(arrow.getStart().getX(), arrow.getStart().getY(), arrow.getEnd().getX(), arrow.getEnd().getY());
		}
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

	public void deleteBox() {
		if (selectedBox != null) {
			boxes.remove(selectedBox);
			for(Arrow arrow: selectedBox.getIncoming()) {
				arrows.remove(arrow);
			}
			for(Arrow arrow: selectedBox.getOutgoing()) {
				arrows.remove(arrow);
			}
			selectedBox = null;
			repaint();
		}
	}

	public void updateText(String text) {
		if (selectedBox != null) {
			selectedBox.setText(text);
			repaint();
		}
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		if (selectedBox != null) {
			int deltaX = e.getX()-startXDrag;
			int deltaY = e.getY()-startYDrag;
			selectedBox.setX(startXBox + deltaX);
			selectedBox.setY(startYBox + deltaY);
			repaint();
		}
	}


	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	public void startArrowCheck() {
		arrowCheck = true;
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		int mouseX = arg0.getX();
		int mouseY = arg0.getY();
		if (arrowCheck) {
			Box otherBox = null;
			for (Box box: boxes) {
				if (box.contains(mouseX, mouseY)) {
					otherBox = box;
				}
			}
			if(otherBox != null) {
				Arrow arrow = new Arrow(selectedBox,otherBox,"");
				arrows.add(arrow);
				selectedBox.addOutgoing(arrow);
				otherBox.addIncoming(arrow);
			} else {
				selectedBox = null;
			}
			arrowCheck = false;
			main.setMakeArrowEnabled(false);
			repaint();
		} else {
			selectedBox = null;
			for (Box box: boxes) {
				if (box.contains(mouseX, mouseY)) {
					selectedBox = box;
					startXDrag = mouseX;
					startYDrag = mouseY;
					startXBox = box.getX();
					startYBox = box.getY();
				}
			}
			if (selectedBox != null) {
				main.setText(selectedBox.getText());
				if(boxes.size()>= 2) {
					main.setMakeArrowEnabled(true);
				
				}
			}
			repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void deleteAllBoxes() {
		boxes.clear();
		arrows.clear();
		selectedBox = null;
		arrowCheck = false;
		repaint();
	}



}
