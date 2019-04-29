package pickapath.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;

import pickapath.Arrow;
import pickapath.BooleanExpression;
import pickapath.Box;
import pickapath.CanvasObject;

@SuppressWarnings("serial")
public class Canvas extends JPanel implements MouseMotionListener, MouseListener, Scrollable {
	private List<Box> boxes;
	private List<Arrow> arrows;
	private CanvasObject selected = null;
	private int startXBox;
	private int startYBox;
	private int startXDrag;
	private int startYDrag;
	boolean mouseDragged;
	private Editor main;
	boolean arrowCheck;
	private double zoom = 1.0;
	private RenderingHints hints;
	private Font font = null;
	private int boxMaxX;
	private int boxMaxY;
	private int boxMinX;
	private int boxMinY;
	private JViewport viewport = null;

	private static final Color BOX_FILL = Color.WHITE;
	private static final Color SELECTED_BOX_FILL = Color.BLACK;
	private static final Color OUTLINE = Color.BLACK;
	private static final Color SELECTED_OUTLINE = Color.WHITE;
	private static final Color GIVING_ITEMS_ARROW = Color.BLUE;
	private static final Color REQUIRING_ITEMS_ARROW = Color.RED;
	private static final Color GIVING_AND_REQUIRING_ITEMS_ARROW =  new Color(128, 0, 128); //purple
	private static final Color SELECTED_GIVING_ITEMS_ARROW = new Color(191, 191, 255); //light blue
	private static final Color SELECTED_REQUIRING_ITEMS_ARROW = new Color(255, 191, 191); //light red (pink)
	private static final Color SELECTED_GIVING_AND_REQUIRING_ITEMS_ARROW = new Color(255, 191, 255);  //light purple
	private static final Color REQUIRING_CURRENCY_ARROW = Color.ORANGE;
	private static final Color SELECTED_REQUIRING_CURRENCY_ARROW = new Color(255, 223, 191); //light orange
	private static final Color GIVING_CURRENCY_ARROW = Color.GREEN;
	private static final Color SELECTED_GIVING_CURRENCY_ARROW = new Color(191, 255, 191); //light green



	//Canvas constructor 
	public Canvas(List<Arrow> arrows, List<Box> boxes, Editor main) {
		ToolTipManager.sharedInstance().setInitialDelay(100);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		//this.setBackground(new Color(0x6e,0x64,0xaf));
		setBackground(Color.GRAY);
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
		Color fill, outline;

		Graphics2D graphics = (Graphics2D) g;		
		graphics.addRenderingHints(hints);

		for (Arrow arrow: arrows) {
			if(arrow == selected) {
				if (arrow.givesItem() && arrow.requiresItem())
					outline = SELECTED_GIVING_AND_REQUIRING_ITEMS_ARROW;
				else if (arrow.givesItem())
					outline = SELECTED_GIVING_ITEMS_ARROW;
				else if (arrow.requiresItem())
					outline = SELECTED_REQUIRING_ITEMS_ARROW;
				else 
					outline = SELECTED_OUTLINE;
				
				fill = OUTLINE;
			}
			else {
				if (arrow.givesItem() && arrow.requiresItem())
					outline = GIVING_AND_REQUIRING_ITEMS_ARROW;
				else if (arrow.givesItem())
					outline = GIVING_ITEMS_ARROW;
				else if (arrow.requiresItem())
					outline = REQUIRING_ITEMS_ARROW;
				else 
					outline = OUTLINE;
				
				fill = SELECTED_OUTLINE;
			}
			
			arrow.draw(graphics, fill, outline, font, zoom);
		}
		//Sets color for the boxes and their outline
		for (Box box: boxes) {
			if (box == selected) {
				fill = SELECTED_BOX_FILL;
				outline = SELECTED_OUTLINE;
			} else {
				fill = BOX_FILL;
				outline = OUTLINE;		
			}

			box.draw(graphics, fill, outline, font, zoom);
		}

	}
	//Deletes selected box and arrows attached to it
	public void deleteBox() {
		if (selected != null && selected instanceof Box) {
			Box selectedBox = (Box) selected;
			boxes.remove(selected);
			for(Arrow arrow: selectedBox.getIncoming()) 
				arrows.remove(arrow);
			
			for(Arrow arrow: selectedBox.getOutgoing())
				arrows.remove(arrow);
			
			selected = null;
			resetBounds();
			repaint();
		}
	}
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
			selected.setText(text);
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
		if ((box.getX()-Box.WIDTH/2)* zoom < boxMinX)
			boxMinX = (int) Math.floor((box.getX()-Box.WIDTH/2)* zoom);
		if ((box.getX()+Box.WIDTH/2)* zoom > boxMaxX)
			boxMaxX = (int) Math.ceil((box.getX()+Box.WIDTH/2)* zoom);
		if ((box.getY()-Box.HEIGHT/2)* zoom < boxMinY)
			boxMinY = (int) Math.floor((box.getY()-Box.HEIGHT/2)* zoom);
		if ((box.getY()+Box.HEIGHT/2)* zoom > boxMaxY)
			boxMaxY = (int) Math.ceil((box.getY()+Box.HEIGHT/2)* zoom);

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
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
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

		} else {
			selected = null;
			for (Box box: boxes) {
				if (box.contains(mouseX, mouseY, zoom)) {
					selected = box;
					startXDrag = mouseX;
					startYDrag = mouseY;
					startXBox = box.getX(zoom);
					startYBox = box.getY(zoom);
				}
			}
			if (selected != null) {
				Box selectedBox = (Box) selected;
				main.setText(selectedBox.getText());
				if( boxes.size() >= 2 )
					main.setMakeArrowEnabled(true);
			} else {
				for (Arrow arrow: arrows) {
					if (arrow.contains(mouseX, mouseY, zoom))
						selected = arrow;
				}
				if (selected != null) {
					main.setText(((Arrow) selected).getText());
					main.setItemsEnabled(true);
				}
			}
			
		}
		
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent event) {
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
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
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
