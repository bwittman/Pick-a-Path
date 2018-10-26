package pickapath;

import java.io.Serializable;
public class Arrow implements Serializable{
		private String text;
		private Box start;
		private Box end;
		
		
		
		public Arrow(Box start, Box end, String text) {
			this.start = start;
			this.end = end;
			this.text = text;
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
}
