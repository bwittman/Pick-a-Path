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
		switch(kind) {
		case ITEM: return "" + item.getId();
		case AND: return "(" + op1 + " AND " + op2 + ")";
		case OR: return "(" + op1 + " OR " + op2 + ")";
		case NOT: return "NOT (" + op1 + ")";
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

	public static BooleanExpression makeExpression(String expressionText, Model model) throws BooleanExpressionException {
		return makeExpression(expressionText.toUpperCase(), model, 0, null);
	}
	private static BooleanExpression makeExpression(String expressionText, Model model, int i, BooleanExpression previous) throws BooleanExpressionException {
		while(i < expressionText.length() && Character.isWhitespace(expressionText.charAt(i)))
			i++;

		if( i < expressionText.length() ) {

			char c = expressionText.charAt(i);
			if (c == '(') {
				return makeExpression(expressionText, model, i + 1, previous);
			} else if (c ==')') {
				return previous;
			} 
			else if (Character.isDigit(c)) {	
				String number = "";
				while (i < expressionText.length() && Character.isDigit(expressionText.charAt(i))) {
					number += expressionText.charAt(i);
					i++;
				}

				try {
					int id = Integer.parseInt(number);
					Item item = model.getItemById(id);
					if( item != null )
						return makeExpression(expressionText, model, i,  new BooleanExpression(item));
				}
				catch(NumberFormatException e) {}
				throw new BooleanExpressionException();
			}
			else if(i + 2 < expressionText.length() && expressionText.substring(i, i + 3).equals("AND")) {
				return and(previous, makeExpression(expressionText, model, i + 3, previous));
			}
			else if(i + 1 < expressionText.length() && expressionText.substring(i, i + 2).equals("OR")) {
				return or(previous, makeExpression(expressionText, model, i + 2, previous));
			}
			else if(i + 2 < expressionText.length() && expressionText.substring(i, i + 3).equals("NOT")) {
				return not( makeExpression(expressionText, model, i + 3, previous));
			} else {
				throw new BooleanExpressionException();
			}


		}

		return previous;
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
