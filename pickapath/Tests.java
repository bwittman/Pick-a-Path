package pickapath;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

class Tests {
			
	@Test
	public void deleteAllBoxesTest() {
		List<Arrow> arrows = new ArrayList<Arrow>();
		List<Box> boxes = new ArrayList<Box>();
		boxes.add(new Box(40,60,100,50, "Olivia"));
		boxes.add(new Box(25,70,100,50, "Lucia"));
		arrows.add(new Arrow (boxes.get(0), boxes.get(1), "friends"));
		Canvas canvas = new Canvas(arrows, boxes, null);
		canvas.deleteAllBoxes();
		Assert.assertEquals("boxes not deleted", true, boxes.size()==0 && arrows.size()==0);
	}
	
	@Test
	public void addIncomingTest() {
		List<Box> boxes = new ArrayList<Box>();
		List<Arrow> incoming = new ArrayList<Arrow>();
		boxes.add(new Box(40,60,100,50, "Olivia"));
		boxes.add(new Box(25,70,100,50, "Lucia"));
		incoming.add(new Arrow (boxes.get(0), boxes.get(1), "friends"));
		Assert.assertEquals("Incoming arrow not added", true, incoming.size() != 0);
	}
	
	@Test
	public void addOutgoingTest() {
		List<Box> boxes = new ArrayList<Box>();
		List<Arrow> outgoing = new ArrayList<Arrow>();
		boxes.add(new Box(40,60,100,50, "Olivia"));
		boxes.add(new Box(25,70,100,50, "Lucia"));
		outgoing.add(new Arrow (boxes.get(0), boxes.get(1), "friends"));
		Assert.assertEquals("Outgoing arrow not added", true, outgoing.size() != 0);
	}
	
	@Test
	public void boxContainsTest() {
		int x = 45;
		int y = 50;
		Box box = new Box(40,60,100,50, "Olivia");
		Assert.assertEquals("points not contained in box", true, (x >= box.getX()-(box.getWidth()/2) && x <= box.getX() + (box.getWidth()/2) && y >= box.getY() - (box.getHeight()/2) && y <= box.getY() + (box.getHeight()/2)));
	}
	
	@Test
	public void arrowContainsTest() {
		fail();
	}
	
	@Test
	public void getIncomingTest() {
		fail();
	}
	
	@Test
	public void getOutgoingTest() {
		fail();
	}
	
	@Test
	public void canvasContainsBoxes() {
		List<Box> boxes = new ArrayList<Box>();
		boxes.add(new Box(40,60,100,50, "Olivia"));
		Assert.assertEquals("Canvas doesn't have any boxes", true, boxes.size() != 0);
	}
	
	@Test
	public void canvasContainsArrows() {
		List<Arrow> arrows = new ArrayList<Arrow>();
		List<Box> boxes = new ArrayList<Box>();
		boxes.add(new Box(40,60,100,50, "Olivia"));
		boxes.add(new Box(25,70,100,50, "Lucia"));
		arrows.add(new Arrow (boxes.get(0), boxes.get(1), "friends"));
		Assert.assertEquals("Canvas doesn't have any arrows", true, arrows.size() != 0);
	}

	@Test
	public void makeBoxButtonTest() {	//same as canvasContainsBoxes()?
		fail();
		}
	
	@Test
	public void makeArrowButtonTest() {	//same as canvasContainsArrows()?
		fail();
	}
	
	@Test
	public void selectedArrowTest() {
		fail();
	}
	
	@Test
	public void selectedBoxTest() {
		fail();
	}
	
	@Test
	public void saveFileButtonTest() {
		fail();
	}
	
	@Test
	public void openFileButtonTest() {
		fail();
	}
	
	@Test
	public void newProjectButtonTest() {
		fail();
	}
	
	@Test
	public void exitButtonTest() {
		fail();
	}
	
	@Test
	public void playerModeButtonTest() {
		fail();
	}
	
	@Test
	public void deleteButtonTest() {
		Object selected = null;
		List<Arrow> arrows = new ArrayList<Arrow>();
		List<Box> boxes = new ArrayList<Box>();
		Box box1 = new Box(40,60,100,50, "Olivia");
		boxes.add(box1);
		Box box2 = new Box(25,70,100,50, "Lucia");
		boxes.add(box2);
		arrows.add(new Arrow (boxes.get(0), boxes.get(1), "friends"));
		Canvas canvas = new Canvas(arrows, boxes, null);
		selected = box2;	//this isnt reading into deletebox() and idk why
		canvas.deleteBox();
		Assert.assertEquals("box not deleted", true, boxes.size()<2);
	}
	
	@Test
	public void somethingTest() {
		fail();
	}
}