package pickapath;

import java.io.Serializable;

public class Box implements Serializable {

	private static final long serialVersionUID = -8277069112909394633L;
	private String text;
	private int x;
	private int y;
	private int width;
	private int height;
	
	public Box(int x,int y,int width,int height, String text) {
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
	
	
	public boolean contains(int x, int y) {
	 return (x >= this.x-(width/2) && x <= this.x + (width/2) && y >= this.y - (height/2) && y <= this.y + (height/2));
		
	}
}
