package pickapath;


import java.awt.Dimension;

import java.awt.Point;

import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

class Tests {

	@Test



	public void deleteAllBoxesTest() { //test to see if all boxes are successfully deleted from the canvas
		

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
	public void addIncomingTest1() { //test to check if a box has an arrow incoming from another box
		Box box0 = new Box(40,60,100,50, "Olivia");
		Box box1 = new Box(25,70,100,50, "Lucia");
		Arrow arrow = new Arrow (box0, box1, "friends");
		Assert.assertEquals("Incoming arrow not added", 1, box1.getIncoming().size());
	}


	@Test
	public void addIncomingTest2() { //test to check that a box has two arrows incoming from two other boxes
		Box box0 = new Box(40,60,100,50, "Olivia");
		Box box1 = new Box(25,70,100,50, "Lucia");
		Box box2 = new Box(25,70,100,50, "Jimmy");
		Arrow arrow0 = new Arrow (box0, box1, "friends");
		Arrow arrow1 = new Arrow (box2, box1, "enemies");
		Assert.assertEquals("Incoming arrows not added", 2, box1.getIncoming().size());
	}



	@Test
	public void addOutgoingTest1() { //test to check if a box has an outgoing arrow connecting it to another box
		Box box0 = new Box(40,60,100,50, "Olivia");
		Box box1 = new Box(25,70,100,50, "Lucia");
		Arrow arrow = new Arrow (box0, box1, "friends");
		Assert.assertEquals("Outgoing arrow not added", 1, box0.getOutgoing().size());
	}

	@Test
	public void addOutgoingTest2() { //test to check if a box has two outgoing arrows connecting to different boxes
		Box box0 = new Box(40,60,100,50, "Olivia");
		Box box1 = new Box(25,70,100,50, "Lucia");
		Box box2 = new Box(25,70,100,50, "Jimmy");
		Arrow arrow0 = new Arrow (box1, box0, "friends");
		Arrow arrow1 = new Arrow (box1, box2, "enemies");
		Assert.assertEquals("Outgoing arrows not added", 2, box1.getOutgoing().size());
	}

	@Test
	public void boxContainsTest() { //test to check if the specified box contains the specified points
		int x = 45;
		int y = 50;
		Box box = new Box(40,60,100,50, "Olivia");
		Assert.assertEquals("points not contained in box", true, box.contains(x, y, 1.0));
	}

	@Test
	public void boxDoesntContainTest() { //test to check that a random set of points are outside of a specific box
		int x = 45;
		int y = 50;
		Box box = new Box(40,60,100,50, "Olivia");
		Assert.assertEquals("points are within box", false, box.contains(30, 29, 1.0));
	}

	//sucks
	@Test
	public void arrowContainsTest() { //test to check if an arrow is made to connect box x and box y
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




	public void deleteButtonTest() { //test to check if the delete button deletes a selected box

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
	public void boxXTest(){
		List<Box> boxes = new ArrayList<Box>();
		boxes.add(new Box(40,60,100,50, "Logan"));
		Assert.assertEquals("That is not the width",true,boxes.get(0).getX() == 40);
		
		}
	
	@Test
	public void boxYTest(){
		Box box = new Box(40,60,100,50, "Logan");
		Assert.assertEquals("That is not the y location",60,box.getY());
		
		}
	@Test // Checks to make sure an item knows its ID number
	public void itemIdTest() {
		Item item = new Item(7,"Sword");
		Assert.assertEquals("That is not the item name",7,item.getId());
	}

	@Test // Checks to make sure an item knows its name
	public void itemNameTest() {
		Item item = new Item(7,"Sword");
		Assert.assertEquals("That is not the item name","Sword",item.getName());
	}

	@Test // Checks if a cell is selected and editable from item table model
	public void isItemNameEditableTest() {
		ItemTableModel model  = new ItemTableModel(new ArrayList<Item>());

		model.addItem("Item 1");
		model.addItem("Item 2");
		model.addItem("Item 3");

		Assert.assertEquals("This cell is not editable",true,model.isCellEditable(2, 1));
	}
	
	@Test // Checks if a cell is selected and editable from item table model
	public void isItemIdEditableTest() {
		ItemTableModel model  = new ItemTableModel(new ArrayList<Item>());

		model.addItem("Item 1");
		model.addItem("Item 2");
		model.addItem("Item 3");

		Assert.assertEquals("This cell is not editable",false,model.isCellEditable(2, 0));
	}

	@Test // Checks if the add item function from the item table model works
	public void addItemTest() {
		ItemTableModel model  = new ItemTableModel(new ArrayList<Item>());

		model.addItem("Item 1");
		model.addItem("Item 2");
		model.addItem("Item 3");

		
		Assert.assertEquals("We didn't add the items right", 3, model.getRowCount());
	}
	@Test // Checks if the remove item function from the item table model works
	public void removeItemTest() {
		ItemTableModel model  = new ItemTableModel(new ArrayList<Item>());

		model.addItem("Item 1");
		model.addItem("Item 2");
		model.addItem("Item 3");
		model.deleteItem(1);
		
		Assert.assertEquals("We didn't remove the right item", "Item 3", model.getValueAt(1, 1));
	}
	
	@Test // Checks if the get item function from the table model works
	public void getItemTest() {
		ItemTableModel model  = new ItemTableModel(new ArrayList<Item>());
		model.addItem("Item");
		
		Assert.assertEquals("We didn't get the right item", "Item", model.getValueAt(0, 1));
	}
	@Test // Checks if the table model can update listeners when there is a new TableModelEvent
	public void updateTableModelListenerTest() {
		ItemTableModel model  = new ItemTableModel(new ArrayList<Item>());
		int[] value = new int[1];
		
		TableModelListener listener = new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				value[0]++;
			}
			
			
		};
		
		model.addTableModelListener(listener);
		
		
		model.addItem("Item 1");
		model.addItem("Item 2");
		
		model.removeTableModelListener(listener);
		
		model.addItem("Item 3");
		Assert.assertEquals("Table listener not updated", 2, value[0]);
		
	}

	@Test 
	public void scrollbarDefaultTest() { //tests to see if the scroll bar is visible when it's not supposed to be (when boxes are in the default viewing area)
		Main main = new Main();
		Canvas canvas = main.getCanvas();

		JViewport viewport = canvas.getViewport();
		JScrollPane pane = (JScrollPane) viewport.getParent();

		Assert.assertEquals("Scrollbars are visible (but shouldn't be)", false, pane.getVerticalScrollBar().isVisible() || pane.getHorizontalScrollBar().isVisible());
		main.dispose();


	}

	@Test 
	public void scrollbarIsVisibleTest() { //tests to see if the scroll bar is visible when a box is created outside of the viewing area
		Main main = new Main();
		Canvas canvas = main.getCanvas();
		
		Box boxy = new Box(200,100,100,50, "Boxy"); //creating a new box within the default viewing area
		canvas.addBox(boxy);
		
		Box boxy2 = new Box(2000,1000,100,50, "Boxy"); //creating a new box outside of default viewing area
		canvas.addBox(boxy2);

		
		JViewport viewport = canvas.getViewport();

		JScrollPane pane = (JScrollPane) viewport.getParent();

		Assert.assertEquals("Scrollbars are visible (but shouldn't be)", false, pane.getVerticalScrollBar().isVisible() || pane.getHorizontalScrollBar().isVisible());

		//JScrollPane pane = (JScrollPane) viewport.getParent();	

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		Assert.assertEquals("Scrollbars are not visible (but should be)", true, pane.getVerticalScrollBar().isVisible() || pane.getHorizontalScrollBar().isVisible());

		main.dispose();


	} 

	@Test
	public void updateBoundsTest() { //tests to see if the scroll bar is visible when it's not supposed to be (when boxes are in the default viewing area)

		Main main = new Main();
		Canvas canvas = main.getCanvas();

		Box boxy = new Box(2000,1000,100,50, "Boxy"); //creating a new box out of default viewing area
		canvas.addBox(boxy);
		Dimension currentBounds = canvas.getPreferredSize();


		Assert.assertEquals("The bounds were not updated", new Dimension(2050, 1025), currentBounds);
		main.dispose();

	}

	@Test
	public void resetBoundsTest() { //test to check if the bounds were reset after removing the last box in the canvas

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





	public void newBoxInBounds() { //tests to make sure that a new box populates within the viewing area

		Main main = new Main();
		Canvas canvas = main.getCanvas();





		

		Box boxy = new Box(2000,1000,100,50, "Boxy"); //creating a new box out of default viewing area
		canvas.addBox(boxy);
		Dimension currentBounds = canvas.getPreferredSize();
		
		Box randomLocationBox = new Box((int)Math.random(),(int)Math.random(),100,50, "randomLocationBox"); //creates a new box at random coordinates in the canvas 
		
		 
		Assert.assertEquals("The box was not created within bounds", true, canvas.contains(new Point(randomLocationBox.getX(), randomLocationBox.getY())));
		main.dispose();

		

	}



	@Test
	public void validInputTest() {
		String line = System.lineSeparator();
		String input = "simple.pap" + line + "1" + line;
		String expected = "Welcome to Pick a Path!" + line
				+ "Please enter a file to open: " + line +
				"pick choice 1" + line + 
				"1. choice 1" + line + line +
				"Enter choice: " + 
				"succesful" + line;


		// set stdin
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		

		// set stdout
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		System.setOut(ps);
		
		PlayerModeCLI.main(new String[0]);



		//if (choice != counter???) {
		Assert.assertEquals("Unexpected output",expected,baos.toString());

	}
}






