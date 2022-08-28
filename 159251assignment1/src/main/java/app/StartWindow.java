package app;

import javax.print.*;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.*;
import java.net.MalformedURLException;
import java.time.LocalDateTime;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.odf.OpenDocumentParser;
import org.apache.tika.sax.BodyContentHandler;
import org.joda.time.LocalDate;
import org.xml.sax.SAXException;

import static javax.print.ServiceUI.printDialog;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class StartWindow {
	private JFrame frame;
	private JMenuBar mainMenuBar;
	private JMenu fileMenu;
	private JMenu viewMenu;
	private JMenu searchMenu;
	private JMenu manageMenu;
	private JMenu helpMenu;
	private JMenuItem newItem;
	private JMenuItem saveItem;
	private JMenuItem exitItem;
	private JMenuItem printItem;
	private JMenuItem zoomItem;
	private JMenuItem timeAndDate;
	private JMenuItem aboutItem;
	private JMenuItem convertToPDF;
	private JMenuItem searchCaseSensitive;
	private JEditorPane editorPane;
	private JMenuItem openItem;
	private JFileChooser chooser;
	private Container container;
	private Document pdf;
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
		mainMenuBar.add(searchMenu);

// Create main menu item: Search > Case Sensitive
		searchCaseSensitive = new JMenuItem("Case Sensitive");
		searchCaseSensitive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchFile();
				editorPane.requestFocusInWindow();
			}
		});
		searchMenu.add(searchCaseSensitive);


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

// Create main menu item: View
		viewMenu = new JMenu("View");
		mainMenuBar.add(viewMenu);
		zoomItem = new JMenuItem("Zoom");
		viewMenu.add(zoomItem);
		//change editor pane to display date and time
		timeAndDate = new JMenuItem("Display Time and Date");
		viewMenu.add(timeAndDate);
		timeAndDate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LocalDateTime date1 = LocalDateTime.now();
				editorPane.setText(date1.toString()+"\n"+editorPane.getText());
			}
		});
		
// Create main menu items: Manage and Help
		manageMenu = new JMenu("Manage");
		mainMenuBar.add(manageMenu);
		helpMenu = new JMenu("Help");
		mainMenuBar.add(helpMenu);
		aboutItem = new JMenuItem("About");
		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame, "Text Editor\nBy Andre Vrey and Joel Yap\nVersion 1.0", "About", JOptionPane.PLAIN_MESSAGE);
			}
		});
		helpMenu.add(aboutItem);

// Create main menu items: Manage > Convert to PDF
		convertToPDF = new JMenuItem("Convert to PDF");
		convertToPDF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pdf = new Document();
				try {
					PdfWriter.getInstance(pdf, new FileOutputStream("converted.pdf"));
					pdf.open();
					pdf.add(new Paragraph(editorPane.getText()));
				} catch (DocumentException de) {
					System.err.println(de.getMessage());
				} catch (IOException ioe) {
					System.err.println(ioe.getMessage());
				}
				pdf.close();
				try
				{
					File file = new File("converted.pdf");
					if(!Desktop.isDesktopSupported())//check if Desktop is supported by Platform or not
					{
						System.out.println("not supported");
						return;
					}
					Desktop desktop = Desktop.getDesktop();
					if(file.exists())
						desktop.open(file);
				}
				catch(Exception ef)
				{
					ef.printStackTrace();
				}
			}
		});
		fileMenu.add(convertToPDF);

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
				   int result = JOptionPane.showConfirmDialog(frame, "<html><b>Save changes?</b></html>\n" + "Unsaved changes will be lost.");
				   switch (result) {
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
// Create editor pane
		editorPane = new JEditorPane();
		scrPane = new JScrollPane(editorPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);


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
			//the read odt function produces console alerts due to slf4j and tika dependencies but the code runs correctly
			if (filePath.contains(".odt")) {
				System.out.println("ODT file detected");
				try {
					BodyContentHandler handler = new BodyContentHandler();
					Metadata metadata = new Metadata();
					//parse the odt file
					FileInputStream fis2 = new FileInputStream(new File(filePath));
					ParseContext parsecontent = new ParseContext();
					OpenDocumentParser opendocumentparser = new OpenDocumentParser();
					//passing InputStream, ContentHandler, Metadata and ParseContext to parse method
					opendocumentparser.parse(fis2, handler, metadata, parsecontent);
					System.out.println("Content in the document:\n"
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

