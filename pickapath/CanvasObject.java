package pickapath;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class CanvasObject {
	
	private String text;
	
	protected CanvasObject(String text) {
		this.text = text;
	}
	
	protected CanvasObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		text = (String)in.readObject();
	}
	
	public void write(ObjectOutputStream out) throws IOException {	//write out text info to a file
		out.writeObject(text);
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public abstract void draw(Graphics2D g, Color fill, Color outline, Font font, double zoom);
}