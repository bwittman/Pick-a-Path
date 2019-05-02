package pickapath;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Box extends CanvasObject {
	private int x;
	private int y;
	public static int WIDTH = 100;
	public static int HEIGHT = 50;
	private List<Arrow> incoming = new ArrayList<Arrow>();
	private List<Arrow> outgoing = new ArrayList<Arrow>();
	private Color color;
	
	public Box(int x, int y, String text) {
		super(text);
		this.x = x;
		this.y = y;
		color = Color.getHSBColor((float)Math.random(), 0.35f, 1.0f);
	}

	public Box(ObjectInputStream in) throws IOException, ClassNotFoundException {	//read in box info from a file
		super(in);
		x = in.readInt();
		y = in.readInt();
		color = (Color)in.readObject();
	}
	
	public void write(ObjectOutputStream out) throws IOException {	//write out box info to a file
		super.write(out);
		out.writeInt(x);
		out.writeInt(y);
		out.writeObject(color);
	}	
	
	public void recolor() {
		color = Color.getHSBColor((float)Math.random(), 0.35f, 1.0f);
	}
	
	public int getX() {
		return x;
	}
	
	public int getX(double zoom) {
		return (int)Math.round(x * zoom);
	}
	public void setX(int x) {
		this.x = x;
	}
	
	public void setX(int x, double zoom) {
		setX((int)Math.round(x / zoom));
	}
	
	public int getY() {
		return y;
	}
	
	public int getY(double zoom) {
		return (int)Math.round(y * zoom);
	}
	public void setY(int y) {
		this.y = y;
	}
	
	public void setY(int y, double zoom) {
		setY((int)Math.round(y / zoom));
	}
	
	public Color getColor() {
		return color;
	}

	public void addIncoming(Arrow arrow) {
		incoming.add(arrow);
	}
	public void addOutgoing(Arrow arrow) {
		outgoing.add(arrow);
	}
	public List<Arrow> getIncoming(){
		return incoming;
	}
	public List<Arrow> getOutgoing(){
		return outgoing;
	}
	
	public void draw(Graphics2D g, boolean selected, Font font, double zoom) {
		int x = (int)Math.round(zoom*(this.x - Box.WIDTH/2));
		int y = (int)Math.round(zoom*(this.y - Box.HEIGHT/2));
		int width = (int)Math.round(zoom*Box.WIDTH);
		int height = (int)Math.round(zoom*Box.HEIGHT);
		
		//Sets colors for arrow and selected arrow
		if( selected ) {
			g.setColor(Color.WHITE);
			g.fillRect(x, y, width, height);
			g.setColor(color);
			g.drawRect(x, y, width, height);
		}
		else {
			g.setColor(color);
			g.fillRect(x, y, width, height);
		}
		//Draws text characters in the box
		//g.setColor(outline);

		g.setColor(Color.BLACK);
		Shape oldClip = g.getClip();
		g.setClip(x, y, width, height);
		int textX = getX(zoom);
		int textY = getY(zoom);
		FontMetrics metrics;
		if( font != null ) {
			g.setFont(font);
			metrics = g.getFontMetrics(font); 
		}
		else
			metrics = g.getFontMetrics(); 
		String text = getText();
		int stringLength = metrics.stringWidth(text);
		int stringHeight = metrics.getAscent();
		if( stringLength > WIDTH ) {
			int space = text.indexOf(' ');
			text = text.substring(0, Math.min(space < 0 ? text.length() : space,10))+ "...";
			stringLength = metrics.stringWidth(text);
		}
		g.drawString(text, textX - stringLength/2, textY + stringHeight/2);
		g.setClip(oldClip);	
	}
	
	
	public boolean contains(int x, int y, double zoom) {
	 return (x >= zoom*(this.x-(WIDTH/2)) && x <= zoom*(this.x + (WIDTH/2)) && y >= zoom*(this.y - (HEIGHT/2)) && y <= zoom*(this.y + (HEIGHT/2)));
		
	}
	
	public static List<Box> getStartingBoxes(List<Box> boxes) {
		List<Box> startingBoxes = new ArrayList<Box>();
		for (Box box : boxes) {
			if (box.getIncoming().isEmpty())
				startingBoxes.add(box);
		}
		return startingBoxes;
	}
}
