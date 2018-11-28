package pickapath;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.KeyStroke;

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
	private double zoom = 1.0;
	private Font font = null;

	public Canvas(List<Arrow> arrows, List<Box> boxes, Main main) {
		// TODO Auto-generated constructor stub
		this.setBackground(new Color(185,185,185));
		this.boxes = boxes;
		this.arrows = arrows;
		addMouseListener(this);
		addMouseMotionListener(this);
		this.main = main;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		for (Arrow arrow: arrows) {
			Box start = arrow.getStart();
			Box end = arrow.getEnd();
			if(arrow == selected) {
				g.setColor(Color.WHITE);
			} else {
				g.setColor(Color.BLACK);
			}
			int startX = (int) Math.round(zoom*start.getX());
			int startY = (int) Math.round(zoom*start.getY());
			int endX = (int) Math.round(zoom*end.getX());
			int endY = (int) Math.round(zoom*end.getY());
			g.drawLine(startX, startY, endX, endY);
			double theta = Math.atan2(end.getY()-start.getY(), end.getX()-start.getX());
			double midX = (start.getX() + end.getX())/2.0;
			double midY = (start.getY() + end.getY())/2.0;
			double tipX = midX - Arrow.HEIGHT*Math.sin(theta-Math.PI/2);
			double tipY = midY + Arrow.HEIGHT*Math.cos(theta-Math.PI/2);
			double leftX = midX + Arrow.HALF_WIDTH*Math.cos(theta-Math.PI/2);
			double leftY = midY + Arrow.HALF_WIDTH*Math.sin(theta-Math.PI/2);
			double rightX = midX -Arrow.HALF_WIDTH*Math.cos(theta-Math.PI/2);
			double rightY = midY - Arrow.HALF_WIDTH*Math.sin(theta-Math.PI/2);
			//zoom variables

			int tipYZoom = (int) Math.round(zoom*tipY);
			int tipXZoom = (int) Math.round(zoom*tipX);
			int leftXZoom = (int) Math.round(zoom*leftX);
			int leftYZoom = (int) Math.round(zoom*leftY);
			int rightXZoom = (int) Math.round(zoom*rightX);
			int rightYZoom = (int) Math.round(zoom*rightY);

			int[] xPoints = {leftXZoom, tipXZoom, rightXZoom};
			int[] yPoints = {leftYZoom, tipYZoom, rightYZoom};
			g.fillPolygon(xPoints, yPoints, 3);
		}
		for (Box box: boxes) {
			if (box == selected) {
				g.setColor(new Color(35,6,200));
			} else {
			g.setColor(new Color(194,211,250));
			}

			int x = (int)Math.round(zoom*(box.getX() - box.getWidth()/2));
			int y = (int)Math.round(zoom*(box.getY() - box.getHeight()/2));
			int width = (int)Math.round(zoom*box.getWidth());
			int height = (int)Math.round(zoom*box.getHeight());

			g.fillRect(x, y, width, height);
			if (box == selected) {
				g.setColor(Color.WHITE);
			} else {
				g.setColor(Color.BLACK);
			}

			g.drawRect(x, y, width, height);
			Shape oldClip = g.getClip();
			g.setClip(x, y, width, height);
			int textX = (int)Math.round(zoom*(box.getX()));
			int textY = (int)Math.round(zoom*box.getY());
			FontMetrics metrics;
			if( font != null ) {
				g.setFont(font);
				metrics = g.getFontMetrics(font); 
			}
			else
				metrics = g.getFontMetrics(); 

			int stringLength = metrics.stringWidth(box.getText());
			int stringHeight = metrics.getAscent();
			String text = box.getText();
			if( stringLength > box.getWidth() ) {
				int space = text.indexOf(' ');
				text = text.substring(0, Math.min(space < 0 ? text.length() : space,10))+ "...";
				stringLength = metrics.stringWidth(text);

			}
			g.drawString(text, textX - (stringLength/2), textY + stringHeight/2);
			g.setClip(oldClip);		
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
		}}
	public void deleteArrow() {	
		if (selected != null && selected instanceof Arrow) {
			arrows.remove(selected);
		} 
		selected = null;
		repaint();
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
			int x = e.getX();
			int y = e.getY();
			int width = this.getWidth();
			int height = this.getHeight();
			if (x < width && x >= 0 && y < height && y >= 0) {
				int deltaX =  x - startXDrag;
				int deltaY = y - startYDrag;
				selectedBox.setX(startXBox + deltaX, zoom);
				selectedBox.setY(startYBox + deltaY, zoom);
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
				if (box.contains(mouseX, mouseY, zoom)) {
					otherBox = box;
				}
			}
			if(otherBox != null) {
				Arrow arrow = new Arrow(selectedBox,otherBox,"");
				arrows.add(arrow);
			} else {
				selected = null;
			}
			arrowCheck = false;
			main.setMakeArrowEnabled(false);
			repaint();
		} else {
			selected = null;
			for (Box box: boxes) {
				if (box.contains(mouseX, mouseY, zoom)) {
					selected = box;
					startXDrag = mouseX;
					startYDrag = mouseY;
					startXBox = (int)Math.round(zoom*box.getX());
					startYBox = (int)Math.round(zoom*box.getY());
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
					if (arrow.contains(mouseX, mouseY, zoom)) {
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

	public double getZoom() {
		return zoom;
	}
	

	//Test comment

	public void setZoom(double zoom, Font font) {
		this.zoom = zoom;
		this.font = font;
		repaint();
	}



}
