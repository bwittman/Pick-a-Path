package pickapath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class Saving {

	public static void write(File selectedFile, List<Box> boxes, List<Arrow> arrows) throws FileNotFoundException, IOException{
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(selectedFile));
		out.writeInt(boxes.size());
		for (Box box: boxes) {
			box.write(out);
		}

		out.writeInt(arrows.size());
		for (Arrow arrow:arrows) {
			arrow.write(out, boxes);
		}
		out.close();
		System.out.printf("Saved data is saved in " + selectedFile);
	}

	public static void read(File selectedFile, List<Box> boxes, List<Arrow> arrows) throws FileNotFoundException, IOException, ClassNotFoundException{
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(selectedFile));
		int boxCount = in.readInt();
		boxes.clear();
		for (int i = 0; i < boxCount; ++i ) {
			boxes.add(new Box(in));
		}
		
		int arrowCount = in.readInt();
		arrows.clear();
		for (int i = 0; i <  arrowCount; ++i) {
			arrows.add(new Arrow(in, boxes));
		}
		in.close();
		System.out.printf("Saved data is read from " + selectedFile);
	}
	
	
	
	
	//move below somewhere else
	
	
	public static void saveFile(List<Box> boxes, List<Arrow> arrows) {

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
		if (fileSelect.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileSelect.getSelectedFile();
			String path = selectedFile.getAbsolutePath();
			if (!path.toLowerCase().endsWith(".pap")) {
				selectedFile = new File(path + ".pap");
			}
			try {
				Saving.write(selectedFile, boxes, arrows);
			} catch (FileNotFoundException e1) {

			} catch (IOException e1) {

			}

		}
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
				
				Saving.read(selectedFile, boxes, arrows);
				
				/*ObjectInputStream in = new ObjectInputStream(new FileInputStream(selectedFile));
				List<Box> listbox = (List<Box>) in.readObject();
				boxes.clear();
				boxes.addAll(listbox);
				List<Arrow> listarrow = (List<Arrow>) in.readObject();
				arrows.clear();
				arrows.addAll(listarrow);
				in.close();
				System.out.printf("Saved data is read from " + selectedFile);*/
			} catch (FileNotFoundException e1) {

			} catch (IOException e1) {

			} catch (ClassNotFoundException e1) {

			}
		}
	}

}
