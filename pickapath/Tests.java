package pickapath;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JScrollPane;
import javax.swing.JViewport;

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
		Main main = new Main();
		Canvas canvas = main.getCanvas();

		JViewport viewport = canvas.getViewport();
		JScrollPane pane = (JScrollPane) viewport.getParent();
		 
		Assert.assertEquals("Scrollbars are visible (but shouldn't be)", false, pane.getVerticalScrollBar().isVisible() || pane.getHorizontalScrollBar().isVisible());
		main.dispose();

		
	}
	
	@Test 
	public void scrollbarIsVisibleTest() {
		Main main = new Main();
		Canvas canvas = main.getCanvas();

		JViewport viewport = canvas.getViewport();
		JScrollPane pane = (JScrollPane) viewport.getParent();
		 
		Assert.assertEquals("Scrollbars are visible (but shouldn't be)", false, pane.getVerticalScrollBar().isVisible() || pane.getHorizontalScrollBar().isVisible());
		main.dispose();

		
	} 
	
	@Test
	public void updateBoundsTest() {

		Main main = new Main();
		Canvas canvas = main.getCanvas();
		
		Box boxy = new Box(2000,1000,100,50, "Boxy"); //creating a new box out of default viewing area
		canvas.addBox(boxy);
		Dimension currentBounds = canvas.getPreferredSize();
			
		 
		Assert.assertEquals("The bounds were not updated", new Dimension(2050, 1025), currentBounds);
		main.dispose();
		
	}
	
	@Test
	public void resetBoundsTest() {

		Main main = new Main();
		Canvas canvas = main.getCanvas();
		
		Box boxy = new Box(2000,1000,100,50, "Boxy"); //creating a new box out of default viewing area
		canvas.addBox(boxy);
		canvas.mousePressed(new MouseEvent(canvas, MouseEvent.MOUSE_PRESSED, System.nanoTime(), 0, 2010, 1005, 1, false));
		canvas.deleteBox();
		Dimension currentBounds = canvas.getPreferredSize();
		
		 
		Assert.assertEquals("The bounds were not reset", canvas.getViewport().getExtentSize(), currentBounds);
		main.dispose();
		
		}

	
	@Test
	public void newBoxInBounds() {
		
		List<Box> boxes = new ArrayList<Box>();

		Main main = new Main();
		Canvas canvas = main.getCanvas();
		
		Box boxy = new Box(200,100,100,50, "Boxy");
		
	}
	

	
	@Test
	public void invalidInputTest(Box box) {
		Scanner in = new Scanner(System.in);

		System.out.println();
		while(box.getOutgoing().size() > 0) {
			int counter = 1;
			System.out.println(box.getText());
			for (Arrow arrow : box.getOutgoing()) {
				System.out.println(counter+ ". " + arrow.getText());
				counter++;


			}
		System.out.print("\nEnter choice: ");
		int choice = in.nextInt() -1;
		Arrow arrow = box.getOutgoing().get(choice);
		box = arrow.getEnd();
		
		choice = 18;
		
		//if (choice != counter???) {
			Assert.assertEquals("That is not a valid numerical input",false);
			
		}
	}
	
	}

	
	

