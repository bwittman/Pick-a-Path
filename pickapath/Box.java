package pickapath;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Box implements Serializable {

	private static final long serialVersionUID = -8277069112909394633L;
	private String text;
	private int x;
	private int y;
	private int width;
	private int height;
	private List<Arrow>incoming;
	private List<Arrow>outgoing;
	
	public Box(int x,int y,int width,int height, String text) {
		incoming = new ArrayList<Arrow>();
		outgoing = new ArrayList<Arrow>();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.text = text;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
		
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
	
	
	public boolean contains(int x, int y) {
	 return (x >= this.x-(width/2) && x <= this.x + (width/2) && y >= this.y - (height/2) && y <= this.y + (height/2));
		
	}
}
