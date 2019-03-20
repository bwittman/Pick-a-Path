package pickapath;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Arrow{

	private String text;
	private Box start;
	private Box end;
	private BooleanExpression expression;
	public final static int HEIGHT = 24;
	public final static int HALF_WIDTH = 18;
	private Set<Item> itemsHeld = new HashSet<Item>();
	

	//Constructor for arrows
	public Arrow(Box start, Box end, String text) {
		this.start = start;
		this.end = end;
		this.text = text;

		start.addOutgoing(this);
		end.addIncoming(this);
	}
	
	public Arrow(ObjectInputStream in, List<Box> boxes) throws IOException, ClassNotFoundException {
		text = (String)in.readObject();
	}
	
	public void write(ObjectOutputStream out, List<Box> boxes) throws IOException {
		out.writeObject(text);
		int startIndex = -1;
		int endIndex = -1;
		for(int i = 0; i < boxes.size(); i++) {
			if (boxes.get(i) == start) {
				startIndex = i;
			}
			if (boxes.get(i) == end) {
				endIndex = i;
			}
		}
		out.writeInt(startIndex);
		out.writeInt(endIndex);
	}
	
	public Box getStart() {
		return start;
	}
	
	public Box getEnd() {
		return end;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public boolean contains(int x, int y, double zoom) {
		// TODO Auto-generated method stub
		
		x = (int) Math.round(x/zoom); 
		y = (int) Math.round(y/zoom);
		//Sets variables to draw the line and arrow between two boxes
		Box start = getStart();
		Box end = getEnd();
		double theta = Math.atan2(end.getY()-start.getY(), end.getX()-start.getX());
		double midX = (start.getX() + end.getX())/2.0;
		double midY = (start.getY() + end.getY())/2.0;
		double aX = midX - HEIGHT*Math.sin(theta-Math.PI/2);
		double aY = midY + HEIGHT*Math.cos(theta-Math.PI/2);
		double bX = midX + HALF_WIDTH*Math.cos(theta-Math.PI/2);
		double bY = midY + HALF_WIDTH*Math.sin(theta-Math.PI/2);
		double cX = midX - HALF_WIDTH*Math.cos(theta-Math.PI/2);
		double cY = midY - HALF_WIDTH*Math.sin(theta-Math.PI/2);

		//Use dot product and Barry-centric coordinates to make the triangle on the line 
		double d00 = dot(bX - aX, bY - aY, bX - aX, bY - aY ); 
		double d01 = dot(bX - aX, bY - aY, cX - aX, cY - aY);
		double d11 = dot( cX - aX, cY - aY,  cX - aX, cY - aY);
		double d20 = dot(x - aX, y - aY, bX - aX, bY - aY);
		double d21 = dot(x - aX, y - aY, cX - aX, cY - aY);
		double denom = d00 * d11 - d01 * d01;
		double v = (d11 * d20 - d01 * d21) / denom;
		double w = (d00 * d21 - d01 * d20) / denom;
		double u = 1.0f - v - w;
		return u>=0 && w>=0 && v>=0 && u<=1 && w<=1 && v<=1;
	}
	private static double dot(double x1,double y1, double x2, double y2) {
		return x1 * x2 + y1 * y2;
	}
	public void setBooleanExpression(BooleanExpression expression) {
		this.expression = expression;
	}
	public Set<Item> getItem(){
		return itemsHeld;
	}
	public void removeItem(Item item) {
		itemsHeld.remove(item);
	}
	public void addItem(Item item) {
		itemsHeld.add(item);
	}
	
}
