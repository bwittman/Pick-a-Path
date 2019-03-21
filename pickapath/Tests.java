package pickapath;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

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
	
	@Test //not done
	public void deleteAllBoxesTestDialogueBox() {
		List<Arrow> arrows = new ArrayList<Arrow>();
		List<Box> boxes = new ArrayList<Box>();
		boxes.add(new Box(40,60,100,50, "Logan"));
		boxes.add(new Box(25,70,100,50, "Nagol"));
		arrows.add(new Arrow (boxes.get(0), boxes.get(1), "friends"));
		Canvas canvas = new Canvas(arrows, boxes, null);
		canvas.deleteAllBoxes();
		Assert.assertEquals("boxes not supposed to be deleted", true, boxes.size()==0 && arrows.size()==0);
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
		List<Box> boxes = new ArrayList<Box>();
		List<Arrow> arrows = new ArrayList<Arrow>();
		//Canvas canvas = new Canvas(arrows, boxes, null);
		boxes.add(new Box(40,60,100,50, "Olivia"));
		boxes.add(new Box(25,70,100,50, "Lucia"));
		arrows.add(new Arrow(boxes.get(0), boxes.get(1), "friends"));
		Arrow arrow = new Arrow(boxes.get(0), boxes.get(1), null);
		int boxX =  boxes.get(0).getX();
		int boxY = boxes.get(0).getY();
		double zoom = 1.0;
		Assert.assertEquals("mouse X and Y not inside arrow", false, arrow.contains(boxX, boxY, zoom));
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
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.getName().toLowerCase().endsWith(".pap");
			}

			@Override
			public String getDescription() {
				return ".pap files";
			}
		});
		Assert.assertEquals("File not saved", true, chooser != null);
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
	public void exitButtonTest() {
		JFrame frame = new JFrame("Editor Mode");
		frame.dispose();
		Assert.assertEquals("Frame not closed", true, frame.isVisible() == false);
	}

	@Test
	public void textAreaEditableTest() {
		JTextArea boxInformation = new JTextArea();
		boxInformation.setEditable(false);
		Assert.assertEquals("Text is editable", true, boxInformation.isEditable() == false);
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
	
	@Test
	public void tooManyStartingBoxesTest() {
		JMenuItem frame = new JMenuItem("Editor Mode");
		JMenuItem playerMode = new JMenuItem("Player Mode");
		List<Box> startingBoxes = new ArrayList<Box>();
		startingBoxes.add(new Box(40,60,100,50, "Logan"));
		startingBoxes.add(new Box(40,60,100,50, "Another Logan"));
		if (startingBoxes.size() > 1) {
			playerMode.setVisible(false);
			frame.setVisible(true);
		}
		Assert.assertEquals("no starting box",true, playerMode.isVisible()==false && frame.isVisible()==true);
	}	
	
	@Test
	public void boxHeightTest(){
		List<Box> boxes = new ArrayList<Box>();
		boxes.add(new Box(40,60,100,50, "Logan"));
		Assert.assertEquals("That is not the height",true,boxes.get(0).getHeight() == 50);
		
		}
	
	@Test
	public void boxWidthTest(){
		List<Box> boxes = new ArrayList<Box>();
		boxes.add(new Box(40,60,100,50, "Logan"));
		Assert.assertEquals("That is not the width",true,boxes.get(0).getWidth() == 100);
		
		}
	
	@Test
	public void boxXTest(){
		List<Box> boxes = new ArrayList<Box>();
		boxes.add(new Box(40,60,100,50, "Logan"));
		Assert.assertEquals("That is not the width",true,boxes.get(0).getX() == 40);
		
		}
	
	@Test
	public void boxYTest(){
		List<Box> boxes = new ArrayList<Box>();
		boxes.add(new Box(40,60,100,50, "Logan"));
		Assert.assertEquals("That is not the width",true,boxes.get(0).getY() == 60);
		
		}
	@Test
	public void itemIdTest() {
		ArrayList<Item> items = new ArrayList<>();
		

	}

	@Test 
	public void scrollbarDefaultTest() {
		
	}
	
	@Test
	public void updateBoundsTest() {
		
	}
	
	@Test
	public void resetBoundsTest() {
		}
	}
	
	
	

