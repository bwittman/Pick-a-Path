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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Arrow extends CanvasObject {
	
	private Box start;
	private Box end;
	private BooleanExpression expression;
	public final static int HEIGHT = 24;
	public final static int HALF_WIDTH = 18;
	private Set<Item> gainedItems = new HashSet<Item>();
	private Set<Item> lostItems = new HashSet<Item>();
	private int currencyChange;
	private int order;

	//Constructor for arrows
	public Arrow(Box start, Box end, String text) {
		super(text);
		this.start = start;
		this.end = end;
	
		start.addOutgoing(this);		
		end.addIncoming(this);
		currencyChange = 0;
		order = start.getOutgoing().size();		
	}
	
	//Package-private
	Arrow(Arrow other, Map<Box, Integer> boxMap, List<Box> boxes, Map<Item, Integer> itemMap, List<Item> items ) {
		super(other.getText());
		start = boxes.get(boxMap.get(other.start));
		end = boxes.get(boxMap.get(other.end));
		
		start.addOutgoing(this);
		end.addIncoming(this);
		
		order = other.order;
		
		for( Item item : other.gainedItems )
			gainedItems.add(items.get(itemMap.get(item)));
		
		for( Item item : other.lostItems )
			lostItems.add(items.get(itemMap.get(item)));
		
		if( other.expression == null )
			expression = null;
		else
			expression = new BooleanExpression(other.expression, itemMap, items);
		
		currencyChange = other.currencyChange;
		
	}

	public Arrow(ObjectInputStream in, Model model) throws IOException, ClassNotFoundException {	//populate info from saved file
		super(in);
		int startIndex = in.readInt();
		int endIndex = in.readInt();
		start = model.getBox(startIndex);
		end = model.getBox(endIndex);
		start.addOutgoing(this);
		end.addIncoming(this);
		order = start.getOutgoing().size();
		int gainedItemsTotal = in.readInt();
		for(int i = 0; i < gainedItemsTotal; ++i) 
			gainedItems.add(model.getItem(in.readInt()));
	
		int lostItemsTotal = in.readInt();
		for(int i = 0; i < lostItemsTotal; ++i)
			lostItems.add(model.getItem(in.readInt()));
	
		String expressionText = (String)in.readObject();
		if (!expressionText.equals("")) {
			try {
				expression = BooleanExpression.makeExpression(expressionText, model);
			} catch (BooleanExpressionException e) {
				//expression is still null
			}
		}
		
		currencyChange = in.readInt();
	}
	
	public int getCurrencyChange() {
		return currencyChange;
	}
	
	public String getGainedItemsText() {
		return itemsToString(gainedItems);
	}
	
	public String getLostItemsText() {
		return itemsToString(lostItems);
	}
	
	private static String itemsToString(Set<Item> items) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for(Item item: items) {
			if( first ) {
				builder.append(item.toString());
				first = false;
			}
			else
				builder.append(", ").append(item.toString());
		}
		return builder.toString();
	}

	//Write arrow information to a file
	//Package private
	void write(ObjectOutputStream out, Map<Box, Integer> boxIndexes, List<Item> items) throws IOException {	
		super.write(out);
		int startIndex = boxIndexes.get(start);
		int endIndex = boxIndexes.get(end);
		
		out.writeInt(startIndex);
		out.writeInt(endIndex);
		out.writeInt(gainedItems.size());
		for(Item item: gainedItems)
			out.writeInt(item.getId());
		
		out.writeInt(lostItems.size());
		for(Item item: lostItems)
			out.writeInt(item.getId());

		if(expression == null)
			out.writeObject("");
		else
			out.writeObject(expression.toString());
		
		out.writeInt(currencyChange);
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

		double theta = Math.atan2(end.getY()-start.getY(), end.getX()-start.getX());
		
		double startX = start.getX();
		double startY = start.getY();
		double endX = end.getX();
		double endY = end.getY();
		
		if( isTandem() ) {
			double perpendicular = theta - Math.PI/2.0;
			double deltaX = Math.cos(perpendicular)*Box.WIDTH/5.0;
			double deltaY = Math.sin(perpendicular)*Box.WIDTH/5.0;
			
			startX += deltaX;
			endX += deltaX;
			startY += deltaY;
			endY += deltaY;
		}		
		
		double midX = .55*startX + .45*endX;
		double midY = .55*startY + .45*endY;
		double aX = midX - HEIGHT*Math.sin(theta-Math.PI/2);
		double aY = midY + HEIGHT*Math.cos(theta-Math.PI/2);
		double bX = midX + HALF_WIDTH*Math.cos(theta-Math.PI/2);
		double bY = midY + HALF_WIDTH*Math.sin(theta-Math.PI/2);
		double cX = midX - HALF_WIDTH*Math.cos(theta-Math.PI/2);
		double cY = midY - HALF_WIDTH*Math.sin(theta-Math.PI/2);

		//Use dot product and barycentric coordinates to make the triangle on the line 
		double d00 = dot(bX - aX, bY - aY, bX - aX, bY - aY); 
		double d01 = dot(bX - aX, bY - aY, cX - aX, cY - aY);
		double d11 = dot(cX - aX, cY - aY,  cX - aX, cY - aY);
		double d20 = dot(x - aX, y - aY, bX - aX, bY - aY);
		double d21 = dot(x - aX, y - aY, cX - aX, cY - aY);
		double denominator = d00 * d11 - d01 * d01;
		double v = (d11 * d20 - d01 * d21) / denominator;
		double w = (d00 * d21 - d01 * d20) / denominator;
		double u = 1.0f - v - w;
		return u >= 0 && w >= 0 && v >= 0 && u <= 1 && w <= 1 && v <= 1;
	}
	
	private static double dot(double x1,double y1, double x2, double y2) {
		return x1 * x2 + y1 * y2;
	}
	
	public void setBooleanExpression(BooleanExpression expression) {
		this.expression = expression;
	}
	
	public Set<Item> getGainedItems(){
		return gainedItems;
	}
	
	public Set<Item> getLostItems(){
		return lostItems;
	}
	
	//Package private
	void deleteItem(Item item) {
		gainedItems.remove(item);
		lostItems.remove(item);
		if(expression != null)
			expression = expression.removeItem(item);
	}
	
	//Package private
	void addGainedItem(Item item) {
		gainedItems.add(item);
	}
	
	//Package private
	void removeGainedItem(Item item) {
		gainedItems.remove(item);
	}
	
	//Package private
	void addLostItem(Item item) {
		lostItems.add(item);
	}
	
	//Package private
	void removeLostItem(Item item) {
		lostItems.remove(item);
	}
	
	public boolean satisfies(Set<Item> items, int currency) {
		if( expression == null )
			return currency >= -currencyChange;
		else
			return expression.isTrue(items) && currency >= -currencyChange;		
	}

	public String getMustHaveText() {
		if( expression == null )
			return "";
		else		
			return expression.toString();
	}


	@Override
	public void draw(Graphics2D g, boolean selected, Font font, double zoom) {
		Stroke oldStroke = g.getStroke();
		BasicStroke newStroke = new BasicStroke((float) (5.0*zoom), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND); //thickness of the lines is at 2f
		
		double theta = Math.atan2(end.getY()-start.getY(), end.getX()-start.getX());
		
		if( selected )
			g.setColor(Color.WHITE);
		else
			g.setColor(start.getColor());
		//g.setColor(fill);
		
		double startX = start.getX();
		double startY = start.getY();
		double endX = end.getX();
		double endY = end.getY();
		
		if( isTandem() ) {
			double perpendicular = theta - Math.PI/2.0;
			double deltaX = Math.cos(perpendicular)*Box.WIDTH/5.0;
			double deltaY = Math.sin(perpendicular)*Box.WIDTH/5.0;
			
			startX += deltaX;
			endX += deltaX;
			startY += deltaY;
			endY += deltaY;
		}
			
		g.setStroke(newStroke);
		g.drawLine(fix(startX, zoom), fix(startY, zoom), fix(endX, zoom), fix(endY, zoom));
		g.setStroke(oldStroke);
		
		double midX = .55*startX + .45*endX;
		double midY = .55*startY + .45*endY;
		double tipX = midX - Arrow.HEIGHT*Math.sin(theta-Math.PI/2.0);
		double tipY = midY + Arrow.HEIGHT*Math.cos(theta-Math.PI/2.0);
		double leftX = midX + Arrow.HALF_WIDTH*Math.cos(theta-Math.PI/2.0);
		double leftY = midY + Arrow.HALF_WIDTH*Math.sin(theta-Math.PI/2.0);
		double rightX = midX -Arrow.HALF_WIDTH*Math.cos(theta-Math.PI/2.0);
		double rightY = midY - Arrow.HALF_WIDTH*Math.sin(theta-Math.PI/2.0);
		
		//zoom variables
		int tipYZoom = fix(tipY, zoom);
		int tipXZoom = fix(tipX, zoom);
		int leftXZoom = fix(leftX, zoom);
		int leftYZoom = fix(leftY, zoom);
		int rightXZoom = fix(rightX, zoom);
		int rightYZoom = fix(rightY, zoom);

		int[] xPoints = {leftXZoom, tipXZoom, rightXZoom};
		int[] yPoints = {leftYZoom, tipYZoom, rightYZoom};
		g.fillPolygon(xPoints, yPoints, 3);
		
		g.setColor(Color.BLACK);
		//g.setColor(outline);
		
		String text = "" + order;
		FontMetrics metrics;
		if( font != null ) {
			g.setFont(font);
			metrics = g.getFontMetrics(font); 
		}
		else
			metrics = g.getFontMetrics(); 
		int stringLength = metrics.stringWidth(text);
		int stringHeight = metrics.getAscent();
		int textX = (int)Math.round(2*midX*zoom/3 + tipX*zoom/3 - stringLength/2.0); 
		int textY = (int)Math.round(2*midY*zoom/3 + tipY*zoom/3 + stringHeight/2.0); 
		g.drawString(text, textX, textY);
	}

	private boolean isTandem() {
		for(Arrow arrow : end.getOutgoing() )
			if( arrow.end == start)
				return true;
		
		return false;
	}

	//Package private
	void makeEarlier() {
		int index = order - 1;
		List<Arrow> arrows = start.getOutgoing();
		if( index > 0 ) {
			Arrow swap = arrows.get(index - 1);
			swap.order++;
			order--;
			arrows.set(index, swap);
			arrows.set(index - 1, this);
		}
	}

	//Package private
	void makeLater() {
		int index = order - 1;
		List<Arrow> arrows = start.getOutgoing();
		if( index < arrows.size() - 1 ) {
			Arrow swap = arrows.get(index + 1);
			swap.order--;
			order++;
			arrows.set(index, swap);
			arrows.set(index + 1, this);
		}		
	}
	
	public int getOrder() {
		return order;
	}
}
