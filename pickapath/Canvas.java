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
	private Object selected = null;
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
			Box start = arrow.getStart();
			Box end = arrow.getEnd();
			if(arrow == selected) {
				g.setColor(Color.RED);
			} else {
				g.setColor(Color.BLACK);
			}
			g.drawLine(start.getX(), start.getY(), end.getX(), end.getY());
			double theta = Math.atan2(end.getY()-start.getY(), end.getX()-start.getX());
			double midX = (start.getX() + end.getX())/2.0;
			double midY = (start.getY() + end.getY())/2.0;
			double tipX = midX - Arrow.HEIGHT*Math.sin(theta-Math.PI/2);
			double tipY = midY + Arrow.HEIGHT*Math.cos(theta-Math.PI/2);
			double leftX = midX + Arrow.HALF_WIDTH*Math.cos(theta-Math.PI/2);
			double leftY = midY + Arrow.HALF_WIDTH*Math.sin(theta-Math.PI/2);
			double rightX = midX -Arrow.HALF_WIDTH*Math.cos(theta-Math.PI/2);
			double rightY = midY - Arrow.HALF_WIDTH*Math.sin(theta-Math.PI/2);
			int[] xPoints = {(int)Math.round(leftX),(int)Math.round(tipX), (int)Math.round(rightX)};
			int[] yPoints = {(int)Math.round(leftY),(int)Math.round(tipY), (int)Math.round(rightY)};
			g.fillPolygon(xPoints, yPoints, 3);
		}
		for (Box box: boxes) {
			g.setColor(Color.GREEN);

			g.fillRect(box.getX() - box.getWidth()/2, box.getY() - box.getHeight()/2, box.getWidth(), box.getHeight());
			if (box == selected) {
				g.setColor(Color.WHITE);
			} else {
				g.setColor(Color.BLACK);
			}

			g.drawRect(box.getX() - box.getWidth()/2, box.getY() - box.getHeight()/2, box.getWidth(), box.getHeight());
			g.drawString(box.getText(), box.getX() - 40, box.getY());
		}

	}

	public void deleteBox() {
		if (selected != null && selected instanceof Box) {
			Box selectedBox = (Box) selected;
			boxes.remove(selected);
			for(Arrow arrow: selectedBox.getIncoming()) {
				arrows.remove(arrow);
			}
			for(Arrow arrow: selectedBox.getOutgoing()) {
				arrows.remove(arrow);
			}
			selected = null;
			repaint();
		}
	}

	public void updateText(String text) {
		if (selected != null) {
			if (selected instanceof Box) {
				Box selectedBox = (Box) selected;
				selectedBox.setText(text);
			} else if (selected instanceof Arrow) {
				Arrow selectedArrow = (Arrow) selected;
				selectedArrow.setText(text);
			}
			repaint();
		}
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		if (selected != null && selected instanceof Box) {
			Box selectedBox = (Box) selected;
			
			if (e.getX() < this.getWidth() && e.getX() >= 0 && e.getY() < this.getHeight() && e.getY() >= 0) {
				int deltaX = e.getX()-startXDrag;
				int deltaY = e.getY()-startYDrag;
				selectedBox.setX(startXBox + deltaX);
				selectedBox.setY(startYBox + deltaY);
			}
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
			Box selectedBox = (Box) selected;
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
				selected = null;
			}
			arrowCheck = false;
			main.setMakeArrowEnabled(false);
			repaint();
		} else {
			selected = null;
			for (Box box: boxes) {
				if (box.contains(mouseX, mouseY)) {
					selected = box;
					startXDrag = mouseX;
					startYDrag = mouseY;
					startXBox = box.getX();
					startYBox = box.getY();
				}
			}
			if (selected != null) {
					Box selectedBox = (Box) selected;
					main.setText(selectedBox.getText());
					if(boxes.size()>= 2) {
						main.setMakeArrowEnabled(true);
					
					} 
			} else {
				for (Arrow arrow: arrows) {
					if (arrow.contains(mouseX, mouseY)) {
						selected = arrow;
					}
				}
				if (selected != null) {
					main.setText(((Arrow) selected).getText());
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
		selected = null;
		arrowCheck = false;
		repaint();
	}



}
