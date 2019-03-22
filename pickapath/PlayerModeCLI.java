package pickapath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;

public class PlayerMode_cli {
	
	private JPanel choicePanel;
	private JFrame playerMode;
	private JTextArea boxInformation;
	private List<JRadioButton> buttonList;
	private Box situation;
	
	public static void main(String[] args) {


	}
	
public static void openFile(List<Box> boxes, List<Arrow> arrows) {
		

		JFileChooser fileSelect = new JFileChooser();
		fileSelect.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.getName().toLowerCase().endsWith(".pap");
			}

			@Override
			public String getDescription() {
				return ".pap files";
			}
		});
		if (fileSelect.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileSelect.getSelectedFile();

			try {
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(selectedFile));
				List<Box> listbox = (List<Box>) in.readObject();
				boxes.clear();
				boxes.addAll(listbox);
				List<Arrow> listarrow = (List<Arrow>) in.readObject();
				arrows.clear();
				arrows.addAll(listarrow);
				in.close();
				System.out.printf("Serialized data is read from " + selectedFile);
			} catch (FileNotFoundException e1) {

			} catch (IOException e1) {

			} catch (ClassNotFoundException e1) {

			}
		}
	}




	//Console mode
		public PlayerMode_cli(Box box) {
			
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
				
			}
			
			
		}

	
}
