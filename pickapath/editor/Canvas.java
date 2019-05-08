package pickapath.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
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
	private boolean arrowCheck;
	private double zoom = 1.0;
	private RenderingHints hints;
	private Font font = null;
	private JScrollPane scrollPane = null;

	public static int MIN_WIDTH = 640;
	public static int MIN_HEIGHT = 480;
	public static int SPACING = Box.HEIGHT;
	
	private int width = MIN_WIDTH;
	private int height = MIN_HEIGHT;

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


		//setPreferredSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));		
	}

	public void setScrollPane(JScrollPane scrollPane) {
		this.scrollPane = scrollPane;
		scrollPane.getViewport().setViewPosition(new Point(0,0));
	}

	public JScrollPane  getScrollPane() {
		return scrollPane;
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
		if( selected instanceof Box ) {
			Box selectedBox = (Box) selected;
			int x = e.getX();
			int y = e.getY();
			int deltaX =  x - startXDrag;
			int deltaY = y - startYDrag;
			int xMin = (int)Math.round((SPACING + Box.WIDTH /2)*zoom);
			int yMin = (int)Math.round((SPACING + Box.HEIGHT /2)*zoom);
			model.setPosition(selectedBox, Math.max(startXBox + deltaX, xMin), Math.max(startYBox + deltaY, yMin), zoom);
		}
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}

	public void resetBounds() {
		JViewport viewport = scrollPane.getViewport();
		width = Math.max(MIN_WIDTH, viewport.getExtentSize().width);
		height = Math.max(MIN_HEIGHT, viewport.getExtentSize().height);
		
		
		if( model.boxCount() == 0 ) {
			//setPreferredSize(viewport.getExtentSize());
			viewport.setViewPosition(new Point(0,0)); 
		}
		else {
			for(int i = 0; i < model.boxCount(); ++i) {
				Box box = model.getBox(i);
				width = Math.max(width, (int) Math.ceil((box.getX()+Box.WIDTH/2 + SPACING)* zoom));
				height = Math.max(height, (int) Math.ceil((box.getY()+3*Box.HEIGHT/2)* zoom));	
			}		 
		}
		
		revalidate();
		repaint();

	}

	@Override
	public String getToolTipText(MouseEvent event) {		
		int mouseX = event.getX();
		int mouseY = event.getY();

		for (int i = 0; i < model.boxCount(); ++i) {
			Box box = model.getBox(i);
			if (box.contains(mouseX, mouseY, zoom))
				return box.getToolTipText();		
		}		

		for(int i = 0; i < model.arrowCount(); ++i) {
			Arrow arrow = model.getArrow(i);
			if (arrow.contains(mouseX, mouseY, zoom))
				return arrow.getToolTipText();			
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
			else { //It is possible to add an arrow from a box to itself
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

		Point position = scrollPane.getViewport().getViewPosition();
		double x = position.x / this.zoom;
		double y = position.y / this.zoom;

		this.zoom = zoom;
		this.font = font;


		scrollPane.getViewport().setViewPosition(new Point((int)Math.round(x * zoom), (int)Math.round(y * zoom)));

		resetBounds();
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation,
			int direction) {
		if( orientation == SwingConstants.HORIZONTAL )
			return visibleRect.width / 5;
		else
			return visibleRect.height / 5;
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
	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation,
			int direction) {
		if( orientation == SwingConstants.HORIZONTAL )
			return visibleRect.width / 10;
		else
			return visibleRect.height / 10;
	}

	@Override
	public void updateModel(Model.Event event, CanvasObject object) {
		if(event == Model.Event.LOAD || event == Model.Event.MOVE || event == Model.Event.DELETE ) {
			resetBounds();
		}
		
		/*
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
								
				}				
			});
			*/

		if( event == Model.Event.MOVE && object instanceof Box ) {
			JViewport viewport = scrollPane.getViewport();
			Box box = (Box) object;
			Rectangle view = viewport.getViewRect();
			int x = view.x;
			int y = view.y;

			if ((box.getX()-Box.WIDTH/2 - SPACING)* zoom < view.x)
				x = (int) Math.floor((box.getX()-Box.WIDTH/2 - SPACING)* zoom);
			else if ((box.getX()+Box.WIDTH/2 + SPACING)* zoom > view.x + view.width)
				x = (int) Math.ceil((box.getX()+Box.WIDTH/2 + SPACING)* zoom) - view.width;

			if ((box.getY()-Box.HEIGHT/2 - SPACING)* zoom < view.y)
				y = (int) Math.floor((box.getY()-Box.HEIGHT/2 - SPACING)* zoom);
			else if ((box.getY()+Box.HEIGHT/2 + SPACING)* zoom > view.y + view.height)
				y = (int) Math.ceil((box.getY()+Box.HEIGHT/2 + SPACING)* zoom) - view.height;	

			x = Math.max(x, 0);
			y = Math.max(y, 0);			

			if( x != view.x || y != view.y ) {
				viewport.setViewPosition(new Point(x, y));
			}
		}			

		repaint();		
	}
}
