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
		ToolTipManager.sharedInstance().registerComponent(this);

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
	}

	public JViewport  getViewport() {
		return viewport;
	}

	@Override
	//Paints the boxes and arrows
	public void paint(Graphics g) {
		super.paint(g);
		Color fill, outline;

		Graphics2D graphics = (Graphics2D) g;		
		graphics.addRenderingHints(hints);

		//Draw arrows back to front
		for( int i = arrows.size() - 1; i >= 0; --i ) {
			Arrow arrow = arrows.get(i);
			
			if(arrow == selected) {
				if (arrow.givesItem() && arrow.requiresItem())
					fill = SELECTED_GIVING_AND_REQUIRING_ITEMS_ARROW;
				else if (arrow.givesItem())
					fill = SELECTED_GIVING_ITEMS_ARROW;
				else if (arrow.requiresItem())
					fill = SELECTED_REQUIRING_ITEMS_ARROW;
				else 
					fill = SELECTED_OUTLINE;

				outline = OUTLINE;
			}
			else {
				if (arrow.givesItem() && arrow.requiresItem())
					fill = GIVING_AND_REQUIRING_ITEMS_ARROW;
				else if (arrow.givesItem())
					fill = GIVING_ITEMS_ARROW;
				else if (arrow.requiresItem())
					fill = REQUIRING_ITEMS_ARROW;
				else 
					fill = OUTLINE;

				outline = SELECTED_OUTLINE;
			}

			arrow.draw(graphics, arrow == selected, font, zoom);
		}
		
		//Draw boxes back to front
		for(int i = boxes.size() - 1; i >= 0; --i) {
			Box box = boxes.get(i);
			//Sets color for the boxes and their outline
			if (box == selected) {
				fill = SELECTED_BOX_FILL;
				outline = SELECTED_OUTLINE;
			} else {
				fill = BOX_FILL;
				outline = OUTLINE;		
			}

			box.draw(graphics, box == selected, font, zoom);
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
			main.deselect();
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
		main.deselect();
		repaint();
	}
	
	
	public void makeArrowEarlier() {
		if (selected != null && selected instanceof Arrow) {
			Arrow arrow = (Arrow) selected;
			arrow.makeEarlier();			
		}
		repaint();
	}
	
	public void makeArrowLater() {
		if (selected != null && selected instanceof Arrow) {
			Arrow arrow = (Arrow) selected;
			arrow.makeLater();			
		}
		repaint();
	}
	
	public void deselect() {
		selected = null;
		arrowCheck = false;
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
			int width = getWidth();
			int height = getHeight();
			if (x < width && x >= 0 && y < height && y >= 0) {
				int deltaX =  x - startXDrag;
				int deltaY = y - startYDrag;
				selectedBox.setX(startXBox + deltaX, zoom);
				selectedBox.setY(startYBox + deltaY, zoom);
				updateBounds(selectedBox);
				main.makeDirty();
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
			for (Box box: boxes)
				updateBounds(box);
		}
	}
	
	@Override
	public String getToolTipText(MouseEvent event) {		
		int mouseX = event.getX();
		int mouseY = event.getY();

		for (Arrow arrow: arrows) {
			if (arrow.contains(mouseX, mouseY, zoom)) {
				if( arrow.getText().trim().isEmpty() )
					return null;
				else
					return arrow.getText();
			}
		}
		
		for (Box box: boxes) {
			if (box.contains(mouseX, mouseY, zoom)) {
				if( box.getText().trim().isEmpty() )
					return null;
				else
					return box.getText();		
			}
		}

		return super.getToolTipText(event);
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
	
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
	public void mousePressed(MouseEvent event) {
		int mouseX = event.getX();
		int mouseY = event.getY();
		
		//If currently trying to make an arrow from a box...
		if( arrowCheck ) {
			Box selectedBox = (Box) selected;
			Box otherBox = null;
			for(int i = 0; i < boxes.size() && otherBox == null; ++i) {
				Box box = boxes.get(i);
				if (box.contains(mouseX, mouseY, zoom))
					otherBox = box;
			}
			
			//Don't let a second arrow be added between boxes			
			for( int i = 0; i < selectedBox.getOutgoing().size() && otherBox != null; ++i ) {
				if( selectedBox.getOutgoing().get(i).getEnd() == otherBox )
					otherBox = null;
			}
			
			if(otherBox != null) {
				Arrow arrow = new Arrow(selectedBox,otherBox,"");
				arrows.add(arrow);
				selected = arrow;
				main.selectArrow(arrow, true);				
			} 
			else
				main.deselect();			
			arrowCheck = false;
		}
		//Otherwise, select whatever's been clicked on
		else {
			selected = null;
			for( int i = 0; i < boxes.size(); ++i ) {
				Box box = boxes.get(i);
				if (box.contains(mouseX, mouseY, zoom)) {
					selectBox(i);
					startXBox = box.getX(zoom);
					startYBox = box.getY(zoom);	
					startXDrag = mouseX;
					startYDrag = mouseY;
					repaint();
					return;
				}
			}			
			for( int i = 0; i < arrows.size(); ++i ) {
				Arrow arrow = arrows.get(i);
				if (arrow.contains(mouseX, mouseY, zoom)) {
					selectArrow(i);
					repaint();
					return;
				}
			}

			//if we reach here, nothing's selected
			main.deselect();
		}

		repaint();
	}

	public void selectBox(int index) {
		Box box = boxes.get(index);
		selected = box;
		main.selectBox(box, false);
		if( index != 0 ) {
			boxes.remove(index);
			boxes.add(0, box);
			main.makeDirty();
		}
	}
	
	public void selectArrow(int index) {
		Arrow arrow = arrows.get(index);
		selected = arrow;
		main.selectArrow(arrow, false);
		if( index != 0 ) {
			arrows.remove(index);
			arrows.add(0, arrow);
			main.makeDirty();
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
	}

	public CanvasObject getSelected() {
		return selected;
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
		selected = box;
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
