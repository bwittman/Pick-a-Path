package pickapath;

import static org.junit.Assert.fail;

import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

class Tests {
			
	@Test
	public void deleteAllBoxesTest() {
		
		Main main = new Main();
		Canvas canvas = main.getCanvas();
		
		Box box1 = new Box(40,60,100,50, "Olivia");
		canvas.addBox(box1);
		Box box2 = new Box(25,70,100,50, "Lucia");
		canvas.addBox(box2);
		
		List<Arrow> arrows = canvas.getArrows();
		arrows.add(new Arrow (box1, box2, "friends"));
		
		List<Box> boxes = canvas.getBoxes();
		
		canvas.mousePressed(new MouseEvent(canvas, MouseEvent.MOUSE_PRESSED, System.nanoTime(), 0, 30, 75, 1, false));
		canvas.deleteBox();
		main.dispose();
		canvas.deleteAllBoxes();
		Assert.assertEquals("boxes not deleted", true, boxes.size()==0 && arrows.size()==0);
	}

	
	@Test
	public void addIncomingTest1() {
		Box box0 = new Box(40,60,100,50, "Olivia");
		Box box1 = new Box(25,70,100,50, "Lucia");
		Arrow arrow = new Arrow (box0, box1, "friends");
		Assert.assertEquals("Incoming arrow not added", 1, box1.getIncoming().size());
	}
	

	@Test
	public void addIncomingTest2() {
		Box box0 = new Box(40,60,100,50, "Olivia");
		Box box1 = new Box(25,70,100,50, "Lucia");
		Box box2 = new Box(25,70,100,50, "Jimmy");
		Arrow arrow0 = new Arrow (box0, box1, "friends");
		Arrow arrow1 = new Arrow (box2, box1, "enemies");
		Assert.assertEquals("Incoming arrows not added", 2, box1.getIncoming().size());
	}
	
	
	
	@Test
	public void addOutgoingTest1() {
		Box box0 = new Box(40,60,100,50, "Olivia");
		Box box1 = new Box(25,70,100,50, "Lucia");
		Arrow arrow = new Arrow (box0, box1, "friends");
		Assert.assertEquals("Outgoing arrow not added", 1, box0.getOutgoing().size());
	}
	
	@Test
	public void addOutgoingTest2() {
		Box box0 = new Box(40,60,100,50, "Olivia");
		Box box1 = new Box(25,70,100,50, "Lucia");
		Box box2 = new Box(25,70,100,50, "Jimmy");
		Arrow arrow0 = new Arrow (box1, box0, "friends");
		Arrow arrow1 = new Arrow (box1, box2, "enemies");
		Assert.assertEquals("Outgoing arrows not added", 2, box1.getOutgoing().size());
	}
	//sucks
	/*@Test
	public void addOutgoingTest() {
		List<Box> boxes = new ArrayList<Box>();
		List<Arrow> outgoing = new ArrayList<Arrow>();
		boxes.add(new Box(40,60,100,50, "Olivia"));
		boxes.add(new Box(25,70,100,50, "Lucia"));
		outgoing.add(new Arrow (boxes.get(0), boxes.get(1), "friends"));
		Assert.assertEquals("Outgoing arrow not added", true, outgoing.size() != 0);
	} */
	
	@Test
	public void boxContainsTest() {
		int x = 45;
		int y = 50;
		Box box = new Box(40,60,100,50, "Olivia");
		Assert.assertEquals("points not contained in box", true, box.contains(x, y, 1.0));
	}
	
	@Test
	public void boxDoesntContainTest() {
		int x = 45;
		int y = 50;
		Box box = new Box(40,60,100,50, "Olivia");
		Assert.assertEquals("points are within box", false, box.contains(30, 29, 1.0));
	}
	
	//sucks
	@Test
	public void arrowContainsTest() {
		List<Box> boxes = new ArrayList<Box>();
		List<Arrow> arrows = new ArrayList<Arrow>();
		boxes.add(new Box(40,60,100,50, "Olivia"));
		boxes.add(new Box(25,70,100,50, "Lucia"));
		arrows.add(new Arrow(boxes.get(0), boxes.get(1), "friends"));
		Arrow arrow = new Arrow(boxes.get(0), boxes.get(1), null);
		int boxX =  boxes.get(0).getX();
		int boxY = boxes.get(0).getY();

		Assert.assertEquals("mouse X and Y not inside arrow", false, arrow.contains(boxX, boxY, 1.0));
	}
	
	
	
	@Test
	public void deleteButtonTest() {
		
		Main main = new Main();
		Canvas canvas = main.getCanvas();
		
		Box box1 = new Box(40,60,100,50, "Olivia");
		canvas.addBox(box1);
		Box box2 = new Box(25,70,100,50, "Lucia");
		canvas.addBox(box2);
		
		List<Arrow> arrows = canvas.getArrows();
		arrows.add(new Arrow (box1, box2, "friends"));
		
		List<Box> boxes = canvas.getBoxes();
		
		canvas.mousePressed(new MouseEvent(canvas, MouseEvent.MOUSE_PRESSED, System.nanoTime(), 0, 30, 75, 1, false));
		canvas.deleteBox();
		main.dispose();
		Assert.assertEquals("box not deleted", true, boxes.size() == 1 && arrows.size() == 0);
	}
	
	@Test
	public void boxHeightTest(){
		Box box = new Box(40,60,100,50, "Logan");
		Assert.assertEquals("That is not the height",50,box.getHeight());
		
		}
	
	/*@Test
	public void boxWidthTest(){
		List<Box> boxes = new ArrayList<Box>();
		Box box = new Box(40,60,100,50, "Logan");
		Assert.assertEquals("That is not the width",true,boxes.get(0).getWidth() == 100);
		
		} */
	
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
		items.add(new Item(7,"Sword"));
		Assert.assertEquals("That is not the item ID",true,items.get(0).getId() == 7);

	}

	@Test
	public void itemNameTest() {
		ArrayList<Item> items = new ArrayList<>();
		items.add(new Item(7,"Sword"));
		Assert.assertEquals("That is not the item name",true,items.get(0).getName() =="Sword");
	}
	@Test
	public void getColumnNameTest() {
		ArrayList<Item> items = new ArrayList<>();
		//Assert.assertEquals("That is not the column name", true, items.getClass().getName() == "Item Number" );
	}
	@Test
	public void getRowCountTest() {
		ArrayList<Item> items = new ArrayList<>();
		items.add(new Item(7,"Sword"));
		Assert.assertEquals("That is not the number of rows",true,items.size() == 1);
	}
	@Test
	public void isCellEditableTest() {
		ArrayList<Item> items = new ArrayList<>();
		items.add(new Item(7,"Sword"));
		//Assert.assertEquals("This cell is not editable",true,items.is);
	}
	public void addTableModelListenerTest() {
		//ArrayList<TableModelListener> listeners = new ArrayList<>();
		//listeners.add(new TableModelListener listener());
		//Assert.assertEquals("The listener was not added", true,);
	}

	@Test 
	public void scrollbarDefaultTest() {

	}
	
	@Test
	public void updateBoundsTest() {
		int boxMaxX = 200;
		int boxMaxY = 200;
		int boxMinX = -100;
		int boxMinY = -100;

		
	}
	
	@Test
	public void resetBoundsTest() {
		int boxMaxX = 200;
		int boxMaxY = 200;
		int boxMinX = -100;
		int boxMinY = -100;
		List<Arrow> arrows = new ArrayList<Arrow>();
		List<Box> boxes = new ArrayList<Box>();
		}
	}
	
	
	

