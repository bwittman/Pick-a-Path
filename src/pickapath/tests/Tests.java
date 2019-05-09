package pickapath.tests;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import pickapath.editor.Canvas;
import pickapath.editor.Editor;
import pickapath.model.Choice;
import pickapath.model.Prompt;
import pickapath.model.Item;

class Tests {

	/*
	
	@Test
	public void deleteAllPromptsTest() { //test to see if all prompts are successfully deleted from the canvas
		Editor main = new Editor();
		Canvas canvas = main.getCanvas();

		Prompt prompt1 = new Prompt(40,60,"Olivia");
		canvas.addPrompt(prompt1);
		Prompt prompt2 = new Prompt(25,70, "Lucia");
		canvas.addPrompt(prompt2);

		List<Choice> choices = canvas.getChoices();
		choices.add(new Choice (prompt1, prompt2, "friends"));

		canvas.selectPrompt(0);
		canvas.deletePrompt();
		
		canvas.selectPrompt(0);
		canvas.deletePrompt();
		main.dispose();

		Assert.assertEquals("prompts not deleted", true, canvas.getPrompts().size()==0 && choices.size()==0);
	}


	@Test
	public void addIncomingTest1() { //test to check if a prompt has an choice incoming from another prompt
		Prompt prompt0 = new Prompt(40,60,"Olivia");
		Prompt prompt1 = new Prompt(25,70,"Lucia");
		Choice choice = new Choice (prompt0, prompt1, "friends");
		Assert.assertEquals("Incoming choice not added", 1, prompt1.getIncoming().size());
	}


	@Test
	public void addIncomingTest2() { //test to check that a prompt has two choices incoming from two other prompts
		Prompt prompt0 = new Prompt(40,60,"Olivia");
		Prompt prompt1 = new Prompt(25,70,"Lucia");
		Prompt prompt2 = new Prompt(25,70,"Jimmy");
		Choice choice0 = new Choice (prompt0, prompt1, "friends");
		Choice choice1 = new Choice (prompt2, prompt1, "enemies");
		Assert.assertEquals("Incoming choices not added", 2, prompt1.getIncoming().size());
	}



	@Test
	public void addOutgoingTest1() { //test to check if a prompt has an outgoing choice connecting it to another prompt
		Prompt prompt0 = new Prompt(40,60,"Olivia");
		Prompt prompt1 = new Prompt(25,70,"Lucia");
		Choice choice = new Choice (prompt0, prompt1, "friends");
		Assert.assertEquals("Outgoing choice not added", 1, prompt0.getOutgoing().size());
	}

	@Test
	public void addOutgoingTest2() { //test to check if a prompt has two outgoing choices connecting to different prompts
		Prompt prompt0 = new Prompt(40,60,"Olivia");
		Prompt prompt1 = new Prompt(25,70,"Lucia");
		Prompt prompt2 = new Prompt(25,70,"Jimmy");
		Choice choice0 = new Choice (prompt1, prompt0, "friends");
		Choice choice1 = new Choice (prompt1, prompt2, "enemies");
		Assert.assertEquals("Outgoing choices not added", 2, prompt1.getOutgoing().size());
	}

	@Test
	public void promptContainsTest() { //test to check if the specified prompt contains the specified points
		int x = 45;
		int y = 50;
		Prompt prompt = new Prompt(40,60,"Olivia");
		Assert.assertEquals("points not contained in prompt", true, prompt.contains(x, y, 1.0));
	}

	@Test
	public void promptDoesntContainTest() { //test to check that a random set of points are outside of a specific prompt
		int x = 45;
		int y = 50;
		Prompt prompt = new Prompt(40,60,"Olivia");
		Assert.assertEquals("points are within prompt", false, prompt.contains(30, 29, 1.0));
	}

	//sucks
	@Test
	public void choiceContainsTest() { //test to check if an choice is made to connect prompt x and prompt y
		List<Prompt> prompts = new ArrayList<Prompt>();
		List<Choice> choices = new ArrayList<Choice>();
		prompts.add(new Prompt(40,60,"Olivia"));
		prompts.add(new Prompt(25,70,"Lucia"));
		choices.add(new Choice(prompts.get(0), prompts.get(1), "friends"));
		Choice choice = new Choice(prompts.get(0), prompts.get(1), null);
		int promptX =  prompts.get(0).getX();
		int promptY = prompts.get(0).getY();

		Assert.assertEquals("mouse X and Y not inside choice", false, choice.contains(promptX, promptY, 1.0));
	}



	@Test




	public void deleteButtonTest() { //test to check if the delete button deletes a selected prompt

		Editor main = new Editor();
		Canvas canvas = main.getCanvas();

		Prompt prompt1 = new Prompt(40,60,"Olivia");
		canvas.addPrompt(prompt1);
		Prompt prompt2 = new Prompt(25,70,"Lucia");
		canvas.addPrompt(prompt2);

		List<Choice> choices = canvas.getChoices();
		choices.add(new Choice (prompt1, prompt2, "friends"));

		List<Prompt> prompts = canvas.getPrompts();

		canvas.mousePressed(new MouseEvent(canvas, MouseEvent.MOUSE_PRESSED, System.nanoTime(), 0, 30, 75, 1, false));
		canvas.deletePrompt();
		main.dispose();
		Assert.assertEquals("prompt not deleted", true, prompts.size() == 1 && choices.size() == 0);
	}





	
	


	@Test
	public void promptXTest(){
		List<Prompt> prompts = new ArrayList<Prompt>();
		prompts.add(new Prompt(40,60,"Logan"));
		Assert.assertEquals("That is not the width",true,prompts.get(0).getX() == 40);
		
		}
	
	@Test
	public void promptYTest(){
		Prompt prompt = new Prompt(40,60,"Logan");
		Assert.assertEquals("That is not the y location",60,prompt.getY());
		
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
	public void scrollbarDefaultTest() { //tests to see if the scroll bar is visible when it's not supposed to be (when prompts are in the default viewing area)
		Editor main = new Editor();
		Canvas canvas = main.getCanvas();

		JViewport viewport = canvas.getViewport();
		JScrollPane pane = (JScrollPane) viewport.getParent();

		Assert.assertEquals("Scrollbars are visible (but shouldn't be)", false, pane.getVerticalScrollBar().isVisible() || pane.getHorizontalScrollBar().isVisible());
		main.dispose();


	}

	@Test 
	public void scrollbarIsVisibleTest() { //tests to see if the scroll bar is visible when a prompt is created outside of the viewing area
		Editor main = new Editor();
		Canvas canvas = main.getCanvas();
		
		Prompt prompty = new Prompt(200,100,"Prompty"); //creating a new prompt within the default viewing area
		canvas.addPrompt(prompty);
		
		Prompt prompty2 = new Prompt(2000,1000,"Prompty"); //creating a new prompt outside of default viewing area
		canvas.addPrompt(prompty2);

		
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
	public void updateBoundsTest() { //tests to see if the scroll bar is visible when it's not supposed to be (when prompts are in the default viewing area)

		Editor main = new Editor();
		Canvas canvas = main.getCanvas();

		Prompt prompty = new Prompt(2000,1000,"Prompty"); //creating a new prompt out of default viewing area
		canvas.addPrompt(prompty);
		Dimension currentBounds = canvas.getPreferredSize();


		Assert.assertEquals("The bounds were not updated", new Dimension(2050, 1025), currentBounds);
		main.dispose();

	}

	@Test
	public void resetBoundsTest() { //test to check if the bounds were reset after removing the last prompt in the canvas

		Editor main = new Editor();
		Canvas canvas = main.getCanvas();

		Prompt prompty = new Prompt(2000,1000,"Prompty"); //creating a new prompt out of default viewing area
		canvas.addPrompt(prompty);
		canvas.mousePressed(new MouseEvent(canvas, MouseEvent.MOUSE_PRESSED, System.nanoTime(), 0, 2010, 1005, 1, false));
		canvas.deletePrompt();
		Dimension currentBounds = canvas.getPreferredSize();


		Assert.assertEquals("The bounds were not reset", canvas.getViewport().getExtentSize(), currentBounds);
		main.dispose();

	}


	public void newPromptInBounds() { //tests to make sure that a new prompt populates within the viewing area

		Editor main = new Editor();
		Canvas canvas = main.getCanvas();

		Prompt prompty = new Prompt(2000,1000,"Prompty"); //creating a new prompt out of default viewing area
		canvas.addPrompt(prompty);
		
		Prompt randomLocationPrompt = new Prompt((int)Math.random(),(int)Math.random(),"randomLocationPrompt"); //creates a new prompt at random coordinates in the canvas 
		
		 
		Assert.assertEquals("The prompt was not created within bounds", true, canvas.contains(new Point(randomLocationPrompt.getX(), randomLocationPrompt.getY())));
		main.dispose();

		

	}
	*/

}






