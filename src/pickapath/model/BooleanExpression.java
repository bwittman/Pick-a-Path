package pickapath.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class BooleanExpression {

	enum Kind {
		ITEM,
		AND,
		OR,
		NOT	
	}

	private BooleanExpression op1;
	private BooleanExpression op2;
	private Item item;
	private Kind kind;

	public String toString() {
		return toString(null);
	}
	
	private String toString(Kind parent) {
		switch(kind) {
		case ITEM: return "" + item.getId();
		case AND: 
			if( parent == Kind.AND || parent == null )
				return op1.toString(kind) + " AND " + op2.toString(kind);
			else
				return "(" +  op1.toString(kind) + " AND " + op2.toString(kind) + ")";
		case OR: 
			if( parent == Kind.OR || parent == null )
				return op1.toString(kind) + " OR " + op2.toString(kind);
			else
				return "(" +  op1.toString(kind) + " OR " + op2.toString(kind) + ")";
		case NOT:			
				return "NOT " + op1.toString(kind);
		}
		return "";
	}
	
	public String getToolTipText() {
		return getToolTipText(null);
	}
	
	private String getToolTipText(Kind parent) {
		switch(kind) {
		case ITEM: return "" + item.getName();
		case AND: 
			if( parent == Kind.AND || parent == null )
				return op1.getToolTipText(kind) + " AND " + op2.getToolTipText(kind);
			else
				return "(" +  op1.getToolTipText(kind) + " AND " + op2.getToolTipText(kind) + ")";
		case OR: 
			if( parent == Kind.OR || parent == null )
				return op1.getToolTipText(kind) + " OR " + op2.getToolTipText(kind);
			else
				return "(" +  op1.getToolTipText(kind) + " OR " + op2.getToolTipText(kind) + ")";
		case NOT:			
				return "NOT " + op1.getToolTipText(kind);
		}
		return "";
	}

	public Kind getKind() {
		return kind;
	}

	public boolean isTrue(Set<Item> items) {
		switch(kind) {
		case ITEM: return items.contains(item);
		case AND: return op1.isTrue(items) && op2.isTrue(items);
		case OR: return op1.isTrue(items) || op2.isTrue(items);
		case NOT: return !op1.isTrue(items);
		}
		return false;
	}

	public BooleanExpression(Item item) {
		kind = Kind.ITEM;
		this.item = item;		
	}

	private BooleanExpression(BooleanExpression op1, BooleanExpression op2, Kind kind) {
		this.kind = kind;
		this.op1 = op1;
		this.op2 = op2;
	}


	//Package-private
	BooleanExpression(BooleanExpression other, Map<Item, Integer> itemMap, List<Item> items) {
		kind = other.kind;
		if( kind == Kind.ITEM )
			item = items.get(itemMap.get(other.item));
		else {
			op1 = new BooleanExpression(other.op1, itemMap, items);
			
			if( kind != Kind.NOT )
				op2 = new BooleanExpression(other.op2, itemMap, items);
		}
	}

	public static BooleanExpression makeExpression(String text, Model model) throws BooleanExpressionException {
		text = text.toUpperCase();
		int[] length = new int[1];

		BooleanExpression operand = makeOperand(text, model, length);
		
		int i = length[0];
		
		while( i < text.length() ) {
		
			while(i < text.length() && Character.isWhitespace(text.charAt(i)))
				i++;
			
			if( i < text.length() ) {
				if(i + 2 < text.length() && text.substring(i, i + 3).equals("AND")) {
					operand = and(operand, makeOperand(text.substring(i + 3), model, length));
					i += 3 + length[0];
				}
				else if(i + 1 < text.length() && text.substring(i, i + 2).equals("OR")) {
					operand = or(operand, makeOperand(text.substring(i + 2), model, length));
					i += 2 + length[0];
				}
				else
					throw new BooleanExpressionException();
			}
		}
		
		return operand;
	}
	
	
	private static BooleanExpression makeOperand(String text, Model model, int[] length) throws BooleanExpressionException {
		int i = 0;
		
		while(i < text.length() && Character.isWhitespace(text.charAt(i)))
			i++;

		if( i < text.length() ) {
			char c = text.charAt(i);
			if (c == '(') {
				int start = i;
				int count = 1;
				i++;
				while( i < text.length() && count > 0 ) {
					char symbol = text.charAt(i);
					if( symbol == ')' )
						count--;
					else if( symbol == '(')
						count++;
					
					i++;
				}
				if( count == 0 ) {
					length[0] = i;
					return makeExpression(text.substring(start + 1, i - 1), model);
				}
				else
					throw new BooleanExpressionException();
				
			}
			else if (Character.isDigit(c)) {	
				String number = "";
				while (i < text.length() && Character.isDigit(text.charAt(i))) {
					number += text.charAt(i);
					i++;
				}

				try {
					int id = Integer.parseInt(number);
					Item item = model.getItemById(id);
					if( item != null ) {
						length[0] = i;
						return new BooleanExpression(item);
					}						
				}
				catch(NumberFormatException e) {}
				throw new BooleanExpressionException();			
		
			}
			else if( i + 2 < text.length() && text.substring(i, i + 3).equals("NOT") ) {
				int[] notLength = new int[1];
				BooleanExpression expression = makeOperand(text.substring(i + 3), model, notLength);
				length[0] = i + 3 + notLength[0];
				return not(expression);				
			}
		}
			
		throw new BooleanExpressionException();		
	}

	public static BooleanExpression and(BooleanExpression op1, BooleanExpression op2) {
		return new BooleanExpression(op1, op2, Kind.AND);
	}

	public static BooleanExpression or(BooleanExpression op1, BooleanExpression op2) {
		return new BooleanExpression(op1, op2, Kind.OR);
	}

	public static BooleanExpression not(BooleanExpression op) {
		return new BooleanExpression(op, null, Kind.NOT);
	}
	public BooleanExpression removeItem (Item item) {
		switch(kind) {
		case ITEM: 
			if(this.item.getId() == item.getId())
			return null;
			else
				return this;
		case AND:
		case OR:
			op1 = op1.removeItem(item);
			op2 = op2.removeItem(item);
			if (op1 == null)
				return op2;
			else if (op2 == null)
				return op1;
			else
				return this;
			
		case NOT: 
			op1 = op1.removeItem(item);
			if (op1 == null)
				return null;
			else 
				return this;
		}
		return this;
	}
}
