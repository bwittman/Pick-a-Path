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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;

import pickapath.model.Choice;
import pickapath.model.Prompt;
import pickapath.model.Element;
import pickapath.model.Model;
import pickapath.model.ModelListener;

@SuppressWarnings("serial")
public class Canvas extends JPanel implements MouseMotionListener, MouseListener, Scrollable, ModelListener {
	private Model model;
	private int startXPrompt;
	private int startYPrompt;
	private int startXDrag;
	private int startYDrag;
	private boolean arrowCheck;
	private double zoom = 1.0;
	private RenderingHints hints;
	private JScrollPane scrollPane = null;
	private final Font NORMAL_FONT = new JLabel().getFont();
	private Font font = NORMAL_FONT;

	public static int MIN_WIDTH = 640;
	public static int MIN_HEIGHT = 480;
	public static int SPACING = Prompt.HEIGHT;
	
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
	}

	public void setScrollPane(JScrollPane scrollPane) {
		this.scrollPane = scrollPane;
		scrollPane.getViewport().setViewPosition(new Point(0,0));
	}

	public JScrollPane  getScrollPane() {
		return scrollPane;
	}

	@Override
	//Draws the prompts and choices
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D graphics = (Graphics2D) g;		
		graphics.addRenderingHints(hints);

		//Draw choices back to front
		for( int i = model.choiceCount() - 1; i >= 0; --i ) {
			Choice choice = model.getChoice(i);
			choice.draw(graphics, choice == model.getSelected(), font, zoom);
		}

		//Draw prompts back to front
		for(int i = model.promptCount() - 1; i >= 0; --i) {
			Prompt prompt = model.getPrompt(i);
			prompt.draw(graphics, prompt == model.getSelected(), font, zoom);
		}
	}


	@Override
	//Determines if a mouse is inside a prompt based on its area and coordinates
	public void mouseDragged(MouseEvent e) {

			int x = e.getX();
			int y = e.getY();
			int deltaX =  x - startXDrag;
			int deltaY = y - startYDrag;
			int xMin = (int)Math.round((SPACING + Prompt.WIDTH /2)*zoom);
			int yMin = (int)Math.round((SPACING + Prompt.HEIGHT /2)*zoom);
			model.setPosition(Math.max(startXPrompt + deltaX, xMin), Math.max(startYPrompt + deltaY, yMin), zoom);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}

	public void resetBounds() {
		JViewport viewport = scrollPane.getViewport();
		width = Math.max(MIN_WIDTH, viewport.getExtentSize().width);
		height = Math.max(MIN_HEIGHT, viewport.getExtentSize().height);
		
		
		if( model.promptCount() == 0 ) {
			//setPreferredSize(viewport.getExtentSize());
			viewport.setViewPosition(new Point(0,0)); 
		}
		else {
			for(int i = 0; i < model.promptCount(); ++i) {
				Prompt prompt = model.getPrompt(i);
				width = Math.max(width, (int) Math.ceil((prompt.getX()+Prompt.WIDTH/2 + SPACING)* zoom));
				height = Math.max(height, (int) Math.ceil((prompt.getY()+3*Prompt.HEIGHT/2)* zoom));	
			}		 
		}
		
		revalidate();
		repaint();

	}

	@Override
	public String getToolTipText(MouseEvent event) {		
		int mouseX = event.getX();
		int mouseY = event.getY();

		for (int i = 0; i < model.promptCount(); ++i) {
			Prompt prompt = model.getPrompt(i);
			if (prompt.contains(mouseX, mouseY, zoom))
				return prompt.getToolTipText();		
		}		

		for(int i = 0; i < model.choiceCount(); ++i) {
			Choice choice = model.getChoice(i);
			if (choice.contains(mouseX, mouseY, zoom))
				return choice.getToolTipText();			
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
	//Reacts when a choice or prompt is clicked 
	public void mousePressed(MouseEvent event) {
		int mouseX = event.getX();
		int mouseY = event.getY();

		//If currently trying to make a choice from a prompt...
		if( arrowCheck ) {
			Prompt selectedPrompt = (Prompt) model.getSelected();
			Prompt otherPrompt = null;
			for(int i = 0; i < model.promptCount() && otherPrompt == null; ++i) {
				Prompt prompt = model.getPrompt(i);
				if (prompt.contains(mouseX, mouseY, zoom))
					otherPrompt = prompt;
			}

			//Don't let a second choice be added between prompts			
			for( int i = 0; i < selectedPrompt.getOutgoing().size() && otherPrompt != null; ++i ) {
				if( selectedPrompt.getOutgoing().get(i).getEnd() == otherPrompt )
					otherPrompt = null;
			}

			if( otherPrompt == null )
				model.deselect();
			else { //It is possible to add a choice from a prompt to itself
				Choice choice = new Choice(selectedPrompt,otherPrompt,"");
				model.add(choice);
			} 

			arrowCheck = false;
		}
		//Otherwise, select whatever's been clicked on
		else {
			for( int i = 0; i < model.promptCount(); ++i ) {
				Prompt prompt = model.getPrompt(i);
				if (prompt.contains(mouseX, mouseY, zoom)) {
					model.selectPrompt(i);
					startXPrompt = prompt.getX(zoom);
					startYPrompt = prompt.getY(zoom);	
					startXDrag = mouseX;
					startYDrag = mouseY;
					return;
				}
			}			
			for( int i = 0; i < model.choiceCount(); ++i ) {
				Choice choice = model.getChoice(i);
				if (choice.contains(mouseX, mouseY, zoom)) {
					model.selectChoice(i);
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

	public void setZoom(double zoom) {

		Point position = scrollPane.getViewport().getViewPosition();
		double x = position.x / this.zoom;
		double y = position.y / this.zoom;

		this.zoom = zoom;
		font = NORMAL_FONT.deriveFont(NORMAL_FONT.getSize() * (float)zoom);
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
	public void updateModel(Model.Event event, Element object, boolean undoOrRedo) {
		if(event == Model.Event.LOAD || event == Model.Event.MOVE || event == Model.Event.DELETE || event == Model.Event.NEW )
			resetBounds();

		if( event == Model.Event.MOVE && object instanceof Prompt ) {
			JViewport viewport = scrollPane.getViewport();
			Prompt prompt = (Prompt) object;
			Rectangle view = viewport.getViewRect();
			int x = view.x;
			int y = view.y;

			if ((prompt.getX()-Prompt.WIDTH/2 - SPACING)* zoom < view.x)
				x = (int) Math.floor((prompt.getX()-Prompt.WIDTH/2 - SPACING)* zoom);
			else if ((prompt.getX()+Prompt.WIDTH/2 + SPACING)* zoom > view.x + view.width)
				x = (int) Math.ceil((prompt.getX()+Prompt.WIDTH/2 + SPACING)* zoom) - view.width;

			if ((prompt.getY()-Prompt.HEIGHT/2 - SPACING)* zoom < view.y)
				y = (int) Math.floor((prompt.getY()-Prompt.HEIGHT/2 - SPACING)* zoom);
			else if ((prompt.getY()+Prompt.HEIGHT/2 + SPACING)* zoom > view.y + view.height)
				y = (int) Math.ceil((prompt.getY()+Prompt.HEIGHT/2 + SPACING)* zoom) - view.height;	

			x = Math.max(x, 0);
			y = Math.max(y, 0);			

			if( x != view.x || y != view.y ) {
				viewport.setViewPosition(new Point(x, y));
			}
		}			

		repaint();		
	}
}
