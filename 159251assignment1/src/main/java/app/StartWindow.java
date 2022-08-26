package app;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.text.html.HTML;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.*;
import java.net.MalformedURLException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.odf.OpenDocumentParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import static javax.print.ServiceUI.printDialog;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class StartWindow {
	private JFrame frame;
	private JMenuBar mainMenuBar;
	private JMenu fileMenu;
	private JMenu searchMenu;
	private JMenuItem newItem;
	private JMenuItem saveItem;
	private JMenuItem exitItem;
	private JMenuItem printItem;
	private JEditorPane editorPane;
	private JMenuItem openItem;
	private JFileChooser chooser;
	private Container container;
	private int dialogValue;
	private String filename;
	private JScrollPane scrPane;

	public StartWindow() {
// Create main window
		frame = new JFrame("Text Editor");
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setBounds(200,170,500,500);

// Create main menu
		mainMenuBar = new JMenuBar();
		frame.setJMenuBar(mainMenuBar);

// Create main menu item: File
		fileMenu = new JMenu("File");
		mainMenuBar.add(fileMenu);

// Create main menu item: Search
		searchMenu = new JMenu("Search");
		searchMenu.setPopupMenuVisible(false);
		searchMenu.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(MenuEvent e) {
				searchFile();
				editorPane.requestFocusInWindow();
			}

			@Override
			public void menuDeselected(MenuEvent e) {

			}

			@Override
			public void menuCanceled(MenuEvent e) {

			}
		});
		mainMenuBar.add(searchMenu);

// Create main menu item: File > New
		newItem = new JMenuItem(new AbstractAction("New") {
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(frame, "<html><b>Save changes?</b></html>\n"+ "Unsaved changes will be lost.");
				switch(result) {
					case JOptionPane.YES_OPTION:
						saveFile();
						break;
					case JOptionPane.NO_OPTION:
						editorPane.setText("");
						break;
					case JOptionPane.CANCEL_OPTION:
						break;
				}
			}
		});
		fileMenu.add(newItem);


// Create main menu item: File > Open
		openItem = new JMenuItem("Open");
		container = frame.getContentPane();
		chooser = new JFileChooser();
		//container = null;
		dialogValue = 0;
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadFile();
			}
		});
		fileMenu.add(openItem);

// Create main menu item: File > Save
		saveItem = new JMenuItem("Save");
		container = frame.getContentPane();
		chooser = new JFileChooser();
		dialogValue = 0;
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveFile();
			}
		});
		fileMenu.add(saveItem);
		
//File menu item: Exit
		exitItem = new JMenuItem("Exit");
		fileMenu.add(exitItem);
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (editorPane.getText() == "") {
					System.exit(0);
				} else {
					//use alert save dialog to trigger save or exit anyway, convert the saveitem actionlistener to a single method that can be called from anywhere
					System.out.println("You have unsaved changes");
					int result = JOptionPane.showConfirmDialog(frame, "<html><b>Save changes?</b></html>\n"+ "Unsaved changes will be lost.");
					switch(result) {
						case JOptionPane.YES_OPTION:
							saveFile();
							System.exit(0);
							break;
						case JOptionPane.NO_OPTION:
							System.exit(0);
							break;
						case JOptionPane.CANCEL_OPTION:
							break;
					}
					
				}
			}
		});

// Create main menu item: File > Print
		printItem = new JMenuItem("Print");
		printItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					printFile();
				} catch (PrinterException | PrintException pe) {
				JOptionPane.showMessageDialog(null, pe.getMessage());
				return;
				}
			}
		});
		fileMenu.add(printItem);

// Create editor pane
		editorPane = new JEditorPane();
		scrPane = new JScrollPane(editorPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS );

// Show all main elements
		frame.setContentPane(scrPane);
		frame.setVisible(true);
	}

	void saveFile() {
		dialogValue = chooser.showSaveDialog(container);
		if (dialogValue == JFileChooser.APPROVE_OPTION) {
			String filePath = chooser.getSelectedFile().getPath();
			try {
				File myObj = new File(filePath);
				if (myObj.createNewFile()) {
					System.out.println("File created: " + myObj.getName());
				} else {
					System.out.println("File already exists.");
				}
			} catch (IOException e2) {
				System.out.println("An error occurred.");
				e2.printStackTrace();
			}
			try {
				FileWriter myWriter = new FileWriter(filePath);
				myWriter.write(editorPane.getText());
				myWriter.close();
				System.out.println("Successfully wrote to file.");
			} catch (IOException e3) {
				System.out.println("An error occurred.");
				e3.printStackTrace();
			}
		}
	}

	void loadFile() {
		dialogValue = chooser.showOpenDialog(container);
		if (dialogValue == JFileChooser.APPROVE_OPTION) {
			String filePath = chooser.getSelectedFile().getPath();
			//if filePath contains .odt run another method
			if (filePath.contains(".odt")) {
				System.out.println("ODT file detected");
				try {
					BodyContentHandler handler
	                = new BodyContentHandler();
	 
					Metadata metadata = new Metadata();
					//parse the odt file
					FileInputStream fis2 = new FileInputStream(new File(filePath));
					ParseContext parsecontent = new ParseContext();
					OpenDocumentParser opendocumentparser = new OpenDocumentParser();
					//passing InputStream, ContentHandler, Metadata and ParseContext to parse method
					opendocumentparser.parse(fis2, handler, metadata, parsecontent);
					System.out.println("Content in the document :\n"
                            + handler.toString());
					editorPane.setText(handler.toString());
				} catch (Exception e4) {
					System.out.println("Failed to extract content: " + e4);
					e4.printStackTrace();
				}
				
			} else {
				try {
					FileInputStream fr = new FileInputStream(filePath);
					InputStreamReader isr = new InputStreamReader(fr, "UTF-8");
					BufferedReader reader = new BufferedReader(isr);
					StringBuffer buffer = new StringBuffer();
					String line = null;
					while ((line = reader.readLine()) != null) {
						buffer.append(line);
						buffer.append("\n");
					}
					reader.close();
					editorPane.setText(buffer.toString());
				} catch (IOException e2) {
					e2.printStackTrace();
				}	
			}
		}
	}

	void searchFile() {
		String searchTerm = JOptionPane.showInputDialog(frame, "Enter search term (case sensitive): ");
		String searchText = editorPane.getText();

		if (searchTerm == null || searchTerm == "" || searchText == "") {
			return;
		}

		int i = searchText.indexOf(searchTerm);
		if (i == -1) {
			return;
		} else {
			editorPane.setCaretPosition(i);
		}
		editorPane.select(i, (i + searchTerm.length()));
	}

	void printFile() throws PrinterException, PrintException {
		PrinterJob pj = PrinterJob.getPrinterJob();
		pj.setPrintable(editorPane.getPrintable(null, null));
		boolean doPrint = pj.printDialog();
		if (doPrint) {
			pj.print();
		}
	}

}

