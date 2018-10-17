package pickapath;

public class Arrow {
		private String text;
		private int x;
		private int y;
		private int length;
		private int height;
		
		public Arrow(int x,int y,int length,int height, String text) {
			this.x = x;
			this.y = y;
			this.length = length;
			this.height = height;
			this.text = text;
}
		public int getHeight() {
			return height;
		}
		public void setHeight(int height) {
			this.height = height;
		}
		public int getLength() {
			return length;
		}
		public void setLength(int width) {
			this.length = width;
		}
		public int getY() {
			//y = box.getY()*0.5;
			return y;
		}
		public void setY(int y) {
			this.y = y;
		}
		public int getX() {
			//y = box.getX()*0.5;
			return x;
		}
		public void setX(int x) {
			this.x = x;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
}
