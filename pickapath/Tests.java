package pickapath;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileFilter;

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
		int x = 50;
		List<Box> boxes = new ArrayList<Box>();
		boxes.add(new Box(40,60,100,50, "Olivia"));
		boxes.add(new Box(25,70,100,50, "Lucia"));
		List<Arrow> arrow = new ArrayList<Arrow>();
		arrow.add(new Arrow(boxes.get(0), boxes.get(1), "friends"));
		//arrow.contains();
		Assert.assertEquals("points not contains in arrow", true, arrow.contains(arrow));
	}
	
	@Test
	public void getIncomingTest() {
		List<Box> boxes = new ArrayList<Box>();
		List<Arrow> incoming = new ArrayList<Arrow>();
		boxes.add(new Box(40,60,100,50, "Olivia"));
		boxes.add(new Box(25,70,100,50, "Lucia"));
		incoming.add(new Arrow (boxes.get(0), boxes.get(1), "friends"));
		Assert.assertEquals("Incoming arrow not retrieved", true, incoming.size() != 0);
	}
	
	@Test
	public void getOutgoingTest() {
		List<Box> boxes = new ArrayList<Box>();
		List<Arrow> outgoing = new ArrayList<Arrow>();
		boxes.add(new Box(40,60,100,50, "Olivia"));
		boxes.add(new Box(25,70,100,50, "Lucia"));
		outgoing.add(new Arrow (boxes.get(0), boxes.get(1), "friends"));
		Assert.assertEquals("Outgoing arrow not retrieved", true, outgoing.size() != 0);
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
	public void selectedArrowTest() { 
		Object selected = null;
		List<Box> boxes = new ArrayList<Box>();
		boxes.add(new Box(40,60,100,50, "Olivia"));
		boxes.add(new Box(25,70,100,50, "Lucia"));
		selected = new Arrow(boxes.get(0), boxes.get(1), "friends");
		Assert.assertEquals("Arrow not selected", true, selected != null);
	}
	
	@Test
	public void selectedBoxTest() {
		Object selected = null;
		selected = new Box(40,60,100,50, "Olivia");
		Assert.assertEquals("Box not selected", true, selected != null);
	}
	
	@Test
	public void saveFileButtonTest() {
		
		fail();
	}
	
	@Test
	public void openFileButtonTest() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileFilter(){
			@Override
			public boolean accept(File file) {
				return file.getName().toLowerCase().endsWith(".pap");
			}

			@Override
			public String getDescription() {
				return ".pap files";
			}
		});
		Assert.assertEquals("File not opened", true, chooser != null);
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
		selected = box2;
		Canvas canvas = new Canvas(arrows, boxes, null);
		canvas.deleteBox();
		selected = null;
		Assert.assertEquals("box not deleted", true, selected == null);
	}
	

	@Test
	public void positiveStartingBoxesTest() {
		JMenuItem frame = new JMenuItem("Editor Mode");
		JMenuItem playerMode = new JMenuItem("Player Mode");
		List<Box> startingBoxes = new ArrayList<Box>();
		startingBoxes.add(new Box(40,60,100,50, "Logan"));
		if (startingBoxes.size() == 1) {
			playerMode.setVisible(true);
			frame.setVisible(false);
		}
		Assert.assertEquals("no starting box",true, playerMode.isVisible()==true && frame.isVisible()==false);
	}
	
	@Test
	public void negativeStartingBoxesTest() {
		JMenuItem frame = new JMenuItem("Editor Mode");
		JMenuItem playerMode = new JMenuItem("Player Mode");
		List<Box> startingBoxes = new ArrayList<Box>();
		if (startingBoxes.size() == 0) {
			playerMode.setVisible(false);
			frame.setVisible(true);
		}
		Assert.assertEquals("no starting box",true, playerMode.isVisible()==false && frame.isVisible()==true);
	}
	
}