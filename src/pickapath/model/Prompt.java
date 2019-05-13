package pickapath.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Prompt extends Element {
	private int x;
	private int y;
	public static int WIDTH = 100;
	public static int HEIGHT = 50;
	private List<Choice> incoming = new ArrayList<Choice>();
	private List<Choice> outgoing = new ArrayList<Choice>();
	private Color color;
	
	public Prompt(int x, int y, String text) {
		super(text);
		this.x = x;
		this.y = y;
		color = Color.getHSBColor((float)Math.random(), 0.35f, 1.0f);
	}
	
	//Copies everything from another Prompt, except for the choices
	public Prompt(Prompt other) {
		super(other.getText());
		x = other.x;
		y = other.y;
		color = other.color;
	}

	//Read in prompt info from a file
	public Prompt(ObjectInputStream in) throws IOException, ClassNotFoundException {	
		super(in);
		x = in.readInt();
		y = in.readInt();
		color = (Color)in.readObject();
	}
	
	//Write out prompt info to a file
	public void write(ObjectOutputStream out) throws IOException {	
		super.write(out);
		out.writeInt(x);
		out.writeInt(y);
		out.writeObject(color);
	}	
	
	//Package-private
	static Color generateColor() {
		return Color.getHSBColor((float)Math.random(), 0.35f, 1.0f);
	}
	
	//Package-private
	void setColor(Color color) {
		this.color = color;
	}
	
	public int getX() {
		return x;
	}
	
	public int getX(double zoom) {
		return (int)Math.round(x * zoom);
	}
	
	//Package-private
	void setX(int x) {
		this.x = x;
	}
	
	//Package-private
	void setX(int x, double zoom) {
		setX((int)Math.round(x / zoom));
	}
	
	public int getY() {
		return y;
	}
	
	public int getY(double zoom) {
		return (int)Math.round(y * zoom);
	}
	
	//Package-private
	void setY(int y) {
		this.y = y;
	}
	
	//Package-private
	void setY(int y, double zoom) {
		setY((int)Math.round(y / zoom));
	}
	
	public Color getColor() {
		return color;
	}

	//Package-private
	void addIncoming(Choice choice) {
		incoming.add(choice);
	}
	
	//Package-private
	void addOutgoing(Choice choice) {
		outgoing.add(choice);
	}
	public List<Choice> getIncoming(){
		return incoming;
	}
	public List<Choice> getOutgoing(){
		return outgoing;
	}
	
	public void draw(Graphics2D g, boolean selected, Font font, double zoom) {
		int x = (int)Math.round(zoom*(this.x - Prompt.WIDTH/2));
		int y = (int)Math.round(zoom*(this.y - Prompt.HEIGHT/2));
		int width = (int)Math.round(zoom*Prompt.WIDTH);
		int height = (int)Math.round(zoom*Prompt.HEIGHT);
		
		g.setColor(color);
		g.fillRect(x, y, width, height);
		
		//Sets colors for arrow and selected arrow
		if( selected ) {
			g.setColor(Color.WHITE);
			Stroke oldStroke = g.getStroke();
			BasicStroke newStroke = new BasicStroke((float) (5.0*zoom), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER); //thickness of the lines is at 2f
			
			g.setStroke(newStroke);
			g.drawRect(x, y, width, height);
			g.setStroke(oldStroke);
		}
		
		
		//Draws text characters in the prompt
		g.setColor(Color.BLACK);
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
		if( stringLength > WIDTH * zoom ) {
			int space = text.indexOf(' ');
			text = text.substring(0, Math.min(space < 0 ? text.length() : space,10))+ "...";
			stringLength = metrics.stringWidth(text);
		}
		g.drawString(text, textX - stringLength/2, textY + stringHeight/2);	
	}
	
	public String getToolTipText() {
		String result = getText().trim();
		if( result.isEmpty() )
			return null;
		else
			return "<html>" + result.replaceAll("\\n", "<br/>") + "</html>";
	}
	
	
	public boolean contains(int x, int y, double zoom) {
	 return (x >= zoom*(this.x-(WIDTH/2)) && x <= zoom*(this.x + (WIDTH/2)) && y >= zoom*(this.y - (HEIGHT/2)) && y <= zoom*(this.y + (HEIGHT/2)));
		
	}
}
