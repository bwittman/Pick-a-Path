package pickapath;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Arrow extends CanvasObject {
	
	private Box start;
	private Box end;
	private BooleanExpression expression;
	public final static int HEIGHT = 24;
	public final static int HALF_WIDTH = 18;
	private Set<Item> itemsHeld = new HashSet<Item>();
	

	//Constructor for arrows
	public Arrow(Box start, Box end, String text) {
		super(text);
		this.start = start;
		this.end = end;
	
		start.addOutgoing(this);
		end.addIncoming(this);
	}

	public Arrow(ObjectInputStream in, List<Box> boxes, List<Item> items) throws IOException, ClassNotFoundException {	//populate info from saved file
		super(in);
		int startIndex = in.readInt();
		int endIndex = in.readInt();
		start = boxes.get(startIndex);
		end = boxes.get(endIndex);
		start.addOutgoing(this);
		end.addIncoming(this);
		int totalItems = in.readInt();
		for(int i = 0; i < totalItems; ++i) {
			int itemId = in.readInt();
			for(Item item:items) {
				if (itemId == item.getId()) {
					itemsHeld.add(item);
					break;
				}
			}
		}
		String expressionText = (String)in.readObject();
		if (!expressionText.equals("")) {
			try {
				expression = BooleanExpression.makeExpression(expressionText, items);
			} catch (BooleanExpressionException e) {
				//expression is still null
			}
		}
	}
	
	public String heldItemText() {
		String text = "";
		boolean first = true;
		for(Item item: itemsHeld) {
			if( first ) {
				text += item;
				first = false;
			}
			else
				text += ", " + item;
		}
		return text;
	}

	public void write(ObjectOutputStream out, List<Box> boxes, List<Item> items) throws IOException {	//write arrow information to a file
		super.write(out);
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
		out.writeInt(itemsHeld.size());
		for(Item item: itemsHeld) {
			out.writeInt(item.getId());
		}
		if(expression == null) {
			out.writeObject("");
		}else {
			out.writeObject(expression.toString());
		}
	}
	
	public Box getStart() {
		return start;
	}
	
	public Box getEnd() {
		return end;
	}

	public boolean contains(int x, int y, double zoom) {
		x = (int) Math.round(x/zoom); 
		y = (int) Math.round(y/zoom);
		
		//Sets variables to draw the line and arrow between two boxes
		Box start = getStart();
		Box end = getEnd();
		double theta = Math.atan2(end.getY()-start.getY(), end.getX()-start.getX());
		double midX = .45*start.getX() + .55*end.getX();
		double midY = .45*start.getY() + .55*end.getY();
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
		return u >= 0 && w >= 0 && v >= 0 && u <= 1 && w <= 1 && v <= 1;
	}
	
	private static double dot(double x1,double y1, double x2, double y2) {
		return x1 * x2 + y1 * y2;
	}
	
	public void setBooleanExpression(BooleanExpression expression) {
		this.expression = expression;
	}
	
	public Set<Item> getItems(){
		return itemsHeld;
	}
	
	public void removeItem(Item item) {
		itemsHeld.remove(item);
		if(expression != null)
			expression = expression.removeItem(item);
	}
	
	public void addItem(Item item) {
		itemsHeld.add(item);
	}
	
	public boolean satisfies(Set<Item> items) {
		if( expression == null )
			return true;
		else
			return expression.isTrue(items);		
	}

	public String getRequirementsText() {
		if( expression == null )
			return "";
		else		
			return expression.toString();
	}
	
	public boolean givesItem() {
		return itemsHeld.size() > 0;
	}
	
	public boolean requiresItem() {
		return expression != null;
	}

	@Override
	public void draw(Graphics2D g, Color fill, Color outline, Font font, double zoom) {
		Stroke oldStroke = g.getStroke();
		BasicStroke newStroke = new BasicStroke((float) (2.0*zoom), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND); //thickness of the lines is at 2f
		
		g.setColor(outline);
		
		int startX = start.getX(zoom);
		int startY = start.getY(zoom);
		int endX = end.getX(zoom);
		int endY = end.getY(zoom);
		
		g.setStroke(newStroke);
		g.drawLine(startX, startY, endX, endY);
		g.setStroke(oldStroke);
		
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

	public void makeEarlier() {
		// TODO Auto-generated method stub
		
	}

	public void makeLater() {
		// TODO Auto-generated method stub
		
	}	
}
