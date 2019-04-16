package pickapath;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;

public class Canvas extends JPanel implements MouseMotionListener, MouseListener, Scrollable {
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
	private RenderingHints hints;
	private Font font = null;
	private int boxMaxX;
	private int boxMaxY;
	private int boxMinX;
	private int boxMinY;
	private JViewport viewport = null;

	
	//Canvas constructor 
	public Canvas(List<Arrow> arrows, List<Box> boxes, Main main) {
		// TODO Auto-generated constructor stub
		ToolTipManager.sharedInstance().setInitialDelay(100);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		this.setBackground(new Color(0x6e,0x64,0xaf));
		this.boxes = boxes;
		this.arrows = arrows;
		addMouseListener(this);
		addMouseMotionListener(this);
		this.main = main;
		hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		
	}
	
	public void setViewport(JViewport viewport) {
		this.viewport = viewport;
		
		//viewport.setOpaque(false);
	}
	
	public JViewport  getViewport() {
		return viewport;
		//viewport.setOpaque(false);
	}
	
	@Override
	//Paints the boxes arrows
	public void paint(Graphics g) {
		super.paint(g);
		
		Graphics2D graphics = (Graphics2D) g;		
		graphics.addRenderingHints(hints);
		Stroke oldStroke = graphics.getStroke();
		BasicStroke newStroke = new BasicStroke((float) (2f*zoom), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND); //thickness of the lines is at 2f
		for (Arrow arrow: arrows) {
			Box start = arrow.getStart();
			Box end = arrow.getEnd();
			if(arrow == selected) {
				g.setColor(Color.WHITE);
				//g.setColor(new Color(0xF2,0x99,0x99));
			} else if (arrow.givesItem() && arrow.requiresItem()) {
				g.setColor(Color.ORANGE);
			} else if (arrow.givesItem()) {
				g.setColor(Color.GREEN);
			} else if (arrow.requiresItem()) {
				g.setColor(Color.RED);
			} else {
				g.setColor(Color.BLACK);
			}
			//Variables to draw boxes and arrows
			int startX = (int) Math.round(zoom*start.getX());
			int startY = (int) Math.round(zoom*start.getY());
			int endX = (int) Math.round(zoom*end.getX());
			int endY = (int) Math.round(zoom*end.getY());
			graphics.setStroke(newStroke);
			g.drawLine(startX, startY, endX, endY);
			graphics.setStroke(oldStroke);
			double theta = Math.atan2(end.getY()-start.getY(), end.getX()-start.getX());
			double midX = .45*start.getX() + .55*end.getX();
			double midY = .45*start.getY() + .55*end.getY();
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
		//Sets color for the boxes and their outline
		for (Box box: boxes) {
			if (box == selected) {
				g.setColor(new Color(0xF2,0x99,0x99));
			} else {
			g.setColor(new Color(0xF2,0xD3,0xAC));
			}
			int x = (int)Math.round(zoom*(box.getX() - box.getWidth()/2));
			int y = (int)Math.round(zoom*(box.getY() - box.getHeight()/2));
			int width = (int)Math.round(zoom*box.getWidth());
			int height = (int)Math.round(zoom*box.getHeight());
			
			//Sets colors for arrow and selected arrow
			g.fillRect(x, y, width, height);
			if (box == selected) {
				g.setColor(Color.BLUE);
			} else {
				g.setColor(Color.BLACK);
			}
			//Draws text characters in the box
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
	//Deletes selected box and arrows attached to it
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
			resetBounds();
			repaint();
		}}
	//Deletes selected arrow
	public void deleteArrow() {	
		if (selected != null && selected instanceof Arrow) {
			Arrow arrow = (Arrow) selected;
			arrows.remove(selected);
			arrow.getStart().getOutgoing().remove(arrow);
			arrow.getEnd().getIncoming().remove(arrow);			
		} 
		selected = null;
		repaint();
	}
	//Allows boxes and arrows to contain text 
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
	//Determines if a mouse is inside a box based on its area and coordinates
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
				updateBounds(selectedBox);
			
			}
			repaint();
		}
	}
	
	public void updateBounds(Box box) {
		if ((box.getX()-box.getWidth()/2)* zoom < boxMinX) {
			boxMinX = (int) Math.floor((box.getX()-box.getWidth()/2)* zoom);
		}
		if ((box.getX()+box.getWidth()/2)* zoom > boxMaxX) {
			boxMaxX = (int) Math.ceil((box.getX()+box.getWidth()/2)* zoom);
		}
		if ((box.getY()-box.getHeight()/2)* zoom < boxMinY) {
			boxMinY = (int) Math.floor((box.getY()-box.getHeight()/2)* zoom);
		}
		if ((box.getY()+box.getHeight()/2)* zoom > boxMaxY) {
			boxMaxY = (int) Math.ceil((box.getY()+box.getHeight()/2)* zoom);
		} 
		
		setPreferredSize(new Dimension (boxMaxX-boxMinX, boxMaxY-boxMinY));
		revalidate(); 
	}


	public void resetBounds() {
		
		boxMaxX = Integer.MIN_VALUE;
		boxMaxY = Integer.MIN_VALUE;
		boxMinX = Integer.MAX_VALUE;
		boxMinY = Integer.MAX_VALUE;
		
		
		if( boxes.size() == 0 ) {
			setPreferredSize(viewport.getExtentSize());
			viewport.setViewPosition(new Point(0,0));
			revalidate(); 
		}
		else {
			
			for (Box box: boxes) {
				updateBounds(box);
			}
		}
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		Arrow hoverArrow = null;
		Box hoverBox = null;
		int mouseX = e.getX();
		int mouseY = e.getY();


		for (Arrow arrow: arrows) {

			if (arrow.contains(mouseX, mouseY, zoom)) {
				hoverArrow = arrow;
				
				
			}
		}
		if (hoverArrow != null) {
			
			setToolTipText(hoverArrow.getText());
		} else {
			setToolTipText("");
		}
		
		for (Box box: boxes) {

		if (box.contains(mouseX, mouseY, zoom)) {
			hoverBox = box;
		}
	}
	
	if (hoverBox != null) {
		
		setToolTipText(hoverBox.getText());
		}
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
	//Performs "selected" functions when a box or arrow is clicked 
	public void mousePressed(MouseEvent arg0) {
		int mouseX = arg0.getX();
		int mouseY = arg0.getY();
		main.setItemsEnabled(false);
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
				selected = null;
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
					main.setItemsEnabled(true);
				}
			}
			repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	
	public Object getSelected() {
		return selected;
	}
	
	//Deletes all boxes on the canvas
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

	public void setZoom(double zoom, Font font) {
		this.zoom = zoom;
		this.font = font;
		resetBounds();
		repaint();
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {

		if( viewport == null )
			return getSize();
		else
			return viewport.getExtentSize();
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect,
            int orientation,
            int direction) {
		if( orientation == SwingConstants.HORIZONTAL )
			return (boxMaxX  - boxMinX) / 5;
		else
			return (boxMaxY  - boxMinY) / 5;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return 1;
	}

	public void setBooleanExpression(BooleanExpression expression) {
		if (selected != null && selected instanceof Arrow) {
			Arrow arrow = (Arrow) selected;
			arrow.setBooleanExpression(expression);
		}
		
	}
	
	public void addBox(Box box) {
		boxes.add(box);
		updateBounds(box);
		repaint();
	}

	public List<Box> getBoxes() {
		return boxes;
	}
	
	public List<Arrow> getArrows() {
		return arrows;
	}
	
	public int getMaxX() {
		return boxMaxX;
	}
	public int getMaxY() {
		return boxMaxY;
	}
	public int getMinX() {
		return boxMinX;
	}
	public int getMinY() {
		return boxMinY;
	}


}
