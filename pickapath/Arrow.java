package pickapath;

import java.io.Serializable;
public class Arrow implements Serializable{

	private static final long serialVersionUID = 4384512514370109657L;
	private String text;
	private Box start;
	private Box end;
	public final static int HEIGHT = 24;
	public final static int HALF_WIDTH = 18;



	public Arrow(Box start, Box end, String text) {
		this.start = start;
		this.end = end;
		this.text = text;

		start.addOutgoing(this);
		end.addIncoming(this);
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
}
