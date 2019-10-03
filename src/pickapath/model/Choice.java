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

public class Choice extends Element {

	private Prompt start;
	private Prompt end;
	private BooleanExpression expression;
	public final static int HEIGHT = 24;
	public final static int HALF_WIDTH = 18;
	private Set<Item> gainedItems = new HashSet<Item>();
	private Set<Item> lostItems = new HashSet<Item>();
	private int currencyChange;
	private int order;

	// Used to avoid reallocating arrays
	private int[] xPoints = new int[3];
	private int[] yPoints = new int[3];	

	// Constructor for choices
	public Choice(Prompt start, Prompt end, String text) {
		super(text);
		this.start = start;
		this.end = end;

		start.addOutgoing(this);		
		end.addIncoming(this);
		currencyChange = 0;
		order = start.getOutgoing().size();		
	}

	// Package-private
	Choice(Choice other, Map<Prompt, Integer> promptIndexes, List<Prompt> prompts, Map<Item, Integer> itemMap, List<Item> items ) {
		super(other.getText());
		start = prompts.get(promptIndexes.get(other.start));
		end = prompts.get(promptIndexes.get(other.end));

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

	public Choice(ObjectInputStream in, Model model) throws IOException, ClassNotFoundException {	//populate info from saved file
		super(in);
		int startIndex = in.readInt();
		int endIndex = in.readInt();
		start = model.getPrompt(startIndex);
		end = model.getPrompt(endIndex);
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
	
	// Package-private
	void setCurrencyChange(int change) {
		currencyChange = change;
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

	// Write arrow information to a file
	// Package private
	void write(ObjectOutputStream out, Map<Prompt, Integer> promptIndexes, Map<Item, Integer> itemIndexes) throws IOException {	
		super.write(out);
		int startIndex = promptIndexes.get(start);
		int endIndex = promptIndexes.get(end);

		out.writeInt(startIndex);
		out.writeInt(endIndex);
		out.writeInt(gainedItems.size());
		for(Item item: gainedItems)
			out.writeInt(itemIndexes.get(item));

		out.writeInt(lostItems.size());
		for(Item item: lostItems)
			out.writeInt(itemIndexes.get(item));

		if(expression == null)
			out.writeObject("");
		else
			out.writeObject(expression.toString());

		out.writeInt(currencyChange);
	}

	public Prompt getStart() {
		return start;
	}

	public Prompt getEnd() {
		return end;
	}

	private void makeTriangle(int[] x, int[] y, double zoom) {
		double theta;
		double startX = start.getX();
		double startY = start.getY();
		double midX;
		double midY;

		if( start == end ) {
			theta = 3.0*Math.PI/2.0;			
			midX = startX + Prompt.WIDTH/2.0 + Prompt.HEIGHT/2.0;
			midY = startY - Prompt.HEIGHT/2.0 + Choice.HEIGHT/2.0;
		}		
		else {
			theta = Math.atan2(end.getY()-start.getY(), end.getX()-start.getX());
			double endX = end.getX();
			double endY = end.getY();

			if( isTandem() ) {

				double perpendicular = theta - Math.PI/2.0;
				double deltaX = Math.cos(perpendicular)*Prompt.WIDTH/5.0;
				double deltaY = Math.sin(perpendicular)*Prompt.WIDTH/5.0;

				startX += deltaX;
				endX += deltaX;
				startY += deltaY;
				endY += deltaY;
			}

			midX = .55*startX + .45*endX;
			midY = .55*startY + .45*endY;
		}


		double tipX = midX - Choice.HEIGHT*Math.sin(theta-Math.PI/2.0);
		double tipY = midY + Choice.HEIGHT*Math.cos(theta-Math.PI/2.0);
		double leftX = midX + Choice.HALF_WIDTH*Math.cos(theta-Math.PI/2.0);
		double leftY = midY + Choice.HALF_WIDTH*Math.sin(theta-Math.PI/2.0);
		double rightX = midX -Choice.HALF_WIDTH*Math.cos(theta-Math.PI/2.0);
		double rightY = midY - Choice.HALF_WIDTH*Math.sin(theta-Math.PI/2.0);

		//zoom variables
		x[0] = fix(tipX, zoom);		
		y[0] = fix(tipY, zoom);
		x[1] = fix(leftX, zoom);
		y[1] = fix(leftY, zoom);
		x[2] = fix(rightX, zoom);
		y[2] = fix(rightY, zoom);		
	}

	public boolean contains(int x, int y, double zoom) {
		makeTriangle(xPoints, yPoints, zoom);

		double aX = xPoints[0];
		double aY = yPoints[0];
		double bX = xPoints[1];
		double bY = yPoints[1];
		double cX = xPoints[2];
		double cY = yPoints[2];

		// Use dot product and barycentric coordinates to make the triangle on the line 
		double d00 = dot(bX - aX, bY - aY, bX - aX, bY - aY); 
		double d01 = dot(bX - aX, bY - aY, cX - aX, cY - aY);
		double d11 = dot(cX - aX, cY - aY,  cX - aX, cY - aY);
		double d20 = dot(x - aX, y - aY, bX - aX, bY - aY);
		double d21 = dot(x - aX, y - aY, cX - aX, cY - aY);
		double denominator = d00 * d11 - d01 * d01;
		double v = (d11 * d20 - d01 * d21) / denominator;
		double w = (d00 * d21 - d01 * d20) / denominator;
		double u = 1.0 - v - w;
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

	// Package private
	void deleteItem(Item item) {
		gainedItems.remove(item);
		lostItems.remove(item);
		if(expression != null)
			expression = expression.removeItem(item);
	}

	// Package private
	void addGainedItem(Item item) {
		gainedItems.add(item);
	}

	// Package private
	void removeGainedItem(Item item) {
		gainedItems.remove(item);
	}

	//Package private
	void addLostItem(Item item) {
		lostItems.add(item);
	}

	// Package private
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
	

		if( selected )
			g.setColor(Color.WHITE);
		else
			g.setColor(start.getColor());

		double startX = start.getX();
		double startY = start.getY();
		double midX;
		double midY;
		
		g.setStroke(newStroke);

		if( start == end ) {
			g.drawOval(fix(startX + Prompt.WIDTH/2.0 - Prompt.HEIGHT/2.0, zoom), fix(startY - Prompt.HEIGHT, zoom), fix(Prompt.HEIGHT, zoom), fix(Prompt.HEIGHT, zoom));

			midX = startX + Prompt.WIDTH/2.0 + Prompt.HEIGHT/2.0;
			midY = startY - Prompt.HEIGHT/2.0 + Choice.HEIGHT/2.0;
		}
		else {			
			double endX = end.getX();
			double endY = end.getY();
			
			if( isTandem() ) {
				double theta = Math.atan2(end.getY()-start.getY(), end.getX()-start.getX());
				double perpendicular = theta - Math.PI/2.0;
				double deltaX = Math.cos(perpendicular)*Prompt.WIDTH/5.0;
				double deltaY = Math.sin(perpendicular)*Prompt.WIDTH/5.0;

				startX += deltaX;
				endX += deltaX;
				startY += deltaY;
				endY += deltaY;
			}
			
			midX = .55*startX + .45*endX;
			midY = .55*startY + .45*endY;
			
			g.drawLine(fix(startX, zoom), fix(startY, zoom), fix(endX, zoom), fix(endY, zoom));
		}	
		
		g.setStroke(oldStroke);

		makeTriangle(xPoints, yPoints, zoom);		
		g.fillPolygon(xPoints, yPoints, 3);

		g.setColor(Color.BLACK);

		String text = "" + order;

		if( gainedItems.size() > 0 || lostItems.size() > 0 || expression != null || currencyChange != 0 )
			text += "*";

		FontMetrics metrics;
		if( font != null ) {
			g.setFont(font);
			metrics = g.getFontMetrics(font); 
		}
		else
			metrics = g.getFontMetrics(); 
		int stringLength = metrics.stringWidth(text);
		int stringHeight = metrics.getAscent();

		int textX = (int)Math.round(2.0*midX*zoom/3.0 + xPoints[0]/3.0 - stringLength/2.0); 
		int textY = (int)Math.round(2.0*midY*zoom/3.0 + yPoints[0]/3.0 + stringHeight/2.0); 
		g.drawString(text, textX, textY);
	}

	public String getToolTipText() {
		StringBuilder builder = new StringBuilder(getText().trim());

		if( gainedItems.size() > 0 ) {
			if( builder.length() > 0 )
				builder.append("\n");
			builder.append("Gained items: ").append(toString(gainedItems));
		}

		if( lostItems.size() > 0 ) {
			if( builder.length() > 0 )
				builder.append("\n");
			builder.append("Lost items: ").append(toString(lostItems));
		}

		if( expression != null ) {
			if( builder.length() > 0 )
				builder.append("\n");
			builder.append("Must have: ").append(expression.getToolTipText());
		}

		if( currencyChange > 0 ) {
			if( builder.length() > 0 )
				builder.append("\n");
			builder.append("Currency gained: " + currencyChange);
		}
		else if( currencyChange > 0 ) {
			if( builder.length() > 0 )
				builder.append("\n");
			builder.append("Currency lost: " + -currencyChange);
		}		

		if( builder.indexOf("\n") > -1 ) {
			builder.insert(0, "<html>");
			builder.append("</html>");
		}

		String result = builder.toString().trim();
		if( result.isEmpty() )
			return null;
		else
			return result.replaceAll("\\n", "<br/>");
	}


	private boolean isTandem() {
		for(Choice choice : end.getOutgoing() )
			if( choice.end == start)
				return true;

		return false;
	}

	//Package-private
	void changeOrder(int steps) {
		int index = order - 1;
		List<Choice> choices = start.getOutgoing();
		if( index + steps >= 0 && index + steps < start.getOutgoing().size() ) {
			Choice swap = choices.get(index + steps);
			int temp = swap.order;
			swap.order = order;
			order = temp;
			choices.set(index, swap);
			choices.set(index + steps, this);
		}
	}

	public int getOrder() {
		return order;
	}
	
	// Package-private
	void setOrder(int order) {
		this.order = order;
	}

	private static String toString(Set<Item> items) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for(Item item : items) {
			if( first )
				first = false;
			else
				builder.append(", ");
			builder.append(item.getName());
		}

		return builder.toString();
	}
}
