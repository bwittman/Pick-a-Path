package pickapath.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;

import pickapath.model.Arrow;
import pickapath.model.Box;
import pickapath.model.CanvasObject;
import pickapath.model.Model;
import pickapath.model.ModelListener;

@SuppressWarnings("serial")
public class Canvas extends JPanel implements MouseMotionListener, MouseListener, Scrollable, ModelListener {
	private Model model;
	private int startXBox;
	private int startYBox;
	private int startXDrag;
	private int startYDrag;
	boolean mouseDragged;
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
	public Canvas(Model model, Editor main) {
		this.model = model;
		model.addModelListener(this);

		ToolTipManager.sharedInstance().setInitialDelay(100);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		ToolTipManager.sharedInstance().registerComponent(this);

		setBackground(Color.GRAY);
		addMouseListener(this);
		addMouseMotionListener(this);
		
		hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
		        resetBounds();  
		    }
		});

		setPreferredSize(new Dimension(640, 480));
		setMinimumSize(getPreferredSize());		
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

		Graphics2D graphics = (Graphics2D) g;		
		graphics.addRenderingHints(hints);

		//Draw arrows back to front
		for( int i = model.arrowCount() - 1; i >= 0; --i ) {
			Arrow arrow = model.getArrow(i);
			arrow.draw(graphics, arrow == model.getSelected(), font, zoom);
		}

		//Draw boxes back to front
		for(int i = model.boxCount() - 1; i >= 0; --i) {
			Box box = model.getBox(i);
			box.draw(graphics, box == model.getSelected(), font, zoom);
		}

	}


	@Override
	//Determines if a mouse is inside a box based on its area and coordinates
	public void mouseDragged(MouseEvent e) {
		CanvasObject selected = model.getSelected();
		if (selected != null && selected instanceof Box) {
			Box selectedBox = (Box) selected;
			int x = e.getX();
			int y = e.getY();
			int width = getWidth();
			int height = getHeight();
			if (x < width && x >= 0 && y < height && y >= 0) {
				int deltaX =  x - startXDrag;
				int deltaY = y - startYDrag;
				model.setPosition(selectedBox, startXBox + deltaX, startYBox + deltaY, zoom);
			}
		}
	}

	private void updateBounds(Box box) {
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

		if( model.boxCount() == 0 ) {
			setPreferredSize(viewport.getExtentSize());
			viewport.setViewPosition(new Point(0,0));
			revalidate(); 
		}
		else {
			for(int i = 0; i < model.boxCount(); ++i)
				updateBounds(model.getBox(i));
		}
	}

	@Override
	public String getToolTipText(MouseEvent event) {		
		int mouseX = event.getX();
		int mouseY = event.getY();

		for(int i = 0; i < model.arrowCount(); ++i) {
			Arrow arrow = model.getArrow(i);
			if (arrow.contains(mouseX, mouseY, zoom)) {
				if( arrow.getText().trim().isEmpty() )
					return null;
				else
					return arrow.getText();
			}
		}

		for (int i = 0; i < model.boxCount(); ++i) {
			Box box = model.getBox(i);
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
			Box selectedBox = (Box) model.getSelected();
			Box otherBox = null;
			for(int i = 0; i < model.boxCount() && otherBox == null; ++i) {
				Box box = model.getBox(i);
				if (box.contains(mouseX, mouseY, zoom))
					otherBox = box;
			}

			//Don't let a second arrow be added between boxes			
			for( int i = 0; i < selectedBox.getOutgoing().size() && otherBox != null; ++i ) {
				if( selectedBox.getOutgoing().get(i).getEnd() == otherBox )
					otherBox = null;
			}

			if( otherBox == null )
				model.deselect();
			else if(otherBox != selectedBox) { //don't add an arrow from a box to itself
				Arrow arrow = new Arrow(selectedBox,otherBox,"");
				model.add(arrow);
			} 
						
			arrowCheck = false;
		}
		//Otherwise, select whatever's been clicked on
		else {
			for( int i = 0; i < model.boxCount(); ++i ) {
				Box box = model.getBox(i);
				if (box.contains(mouseX, mouseY, zoom)) {
					model.selectBox(i);
					startXBox = box.getX(zoom);
					startYBox = box.getY(zoom);	
					startXDrag = mouseX;
					startYDrag = mouseY;
					return;
				}
			}			
			for( int i = 0; i < model.arrowCount(); ++i ) {
				Arrow arrow = model.getArrow(i);
				if (arrow.contains(mouseX, mouseY, zoom)) {
					model.selectArrow(i);
					return;
				}
			}

			//if we reach here, nothing's selected
			model.deselect();
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
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

	@Override
	public void updateModel(Model.Event event, CanvasObject object) {
		if(event == Model.Event.LOAD)
			resetBounds();
		else if( event == Model.Event.MOVE && object instanceof Box ) {
			Box box = (Box)object;
			updateBounds(box);
		}	
		
		repaint();		
	}
}
