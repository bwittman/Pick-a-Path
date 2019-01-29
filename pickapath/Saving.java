package pickapath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class Saving {


	public static void saveFile(List<Box> boxes, List<Arrow> arrows) {
		
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
		if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			File selectedFile = chooser.getSelectedFile();
			String path = selectedFile.getAbsolutePath();
			if (!path.toLowerCase().endsWith(".pap")) {
				selectedFile = new File(path + ".pap");
			}
			try {
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(selectedFile));
				out.writeObject(boxes);
				out.writeObject(arrows);
				out.close();
				System.out.printf("Serialized data is saved in " + selectedFile);
			} catch (FileNotFoundException e1) {

			} catch (IOException e1) {

			}

		}
	}

	public static void openFile(List<Box> boxes, List<Arrow> arrows) {
		
		JFileChooser chooser2 = new JFileChooser();
		chooser2.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.getName().toLowerCase().endsWith(".pap");
			}

			@Override
			public String getDescription() {
				return ".pap files";
			}
		});
		if (chooser2.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File selectedFile = chooser2.getSelectedFile();

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

}
