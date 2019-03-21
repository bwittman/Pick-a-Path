package pickapath;

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
	
	public static BooleanExpression and(BooleanExpression op1, BooleanExpression op2) {
		return new BooleanExpression(op1, op1, Kind.AND);
	}
	
	public static BooleanExpression or(BooleanExpression op1, BooleanExpression op2) {
		return new BooleanExpression(op1, op1, Kind.OR);
	}
	
	public static BooleanExpression or(BooleanExpression op) {
		return new BooleanExpression(op, null, Kind.NOT);
	}
}
