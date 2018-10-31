package pickapath;

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
		
		
	}
	
	
	
	
}
