package app;

import javax.print.*;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.odf.OpenDocumentParser;
import org.apache.tika.sax.BodyContentHandler;
import org.yaml.snakeyaml.Yaml;

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
	private JMenuItem getSourceItem;
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
		File yamlFile = new File("config1.yml");
		Yaml yaml = new Yaml();
		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(yamlFile);
			Map<String, Object> obj = yaml.load(inputStream);
			//System.out.println(obj);
			ArrayList<Integer> frameBounds = (ArrayList<Integer>) obj.get("size");
			//System.out.println(frameBounds);
			frame.setBounds((frameBounds.get(0)), (frameBounds.get(1)), (frameBounds.get(2)), (frameBounds.get(3)));
		} catch (FileNotFoundException e1) {
			System.err.println("Error in opening file");
			e1.printStackTrace();
		}

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

		getSourceItem = new JMenuItem("Read Source Code");
		getSourceItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				frame.setAlwaysOnTop(true);

				Object[] options = {"java", "python", "cpp"};
				Object selectionObject = JOptionPane.showInputDialog(frame, "Select language", "Style", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
				String selectionString = selectionObject.toString();
				//editorPane.setContentType("text/html");
				String data = new ConvertToStyle(false, "colorful", selectionString).hilite(editorPane.getText());
				editorPane.setContentType("text/html");
				editorPane.setText(data);
			}
		});
		manageMenu.add(getSourceItem);

// Create main menu items: File > Convert to PDF
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

	/* Parse text to code */
	public static class ConvertToStyle {
		private static final String URL = "http://hilite.me/api";
		private static final String NEWLINE = System.lineSeparator();
		private static final String ENCODING = StandardCharsets.UTF_8.name();

		private final String parameters;

		/** Constructor activating line number generation. */
		public ConvertToStyle() {
			this(true);
		}

		/** Constructor setting rendering style to "monokai". */
		public ConvertToStyle(boolean lineNumbers) {
			this(lineNumbers, "monokai");
		}

		/** Constructor setting lexer to "java", see http://pygments.org/docs/styles/. */
		public ConvertToStyle(boolean lineNumbers, String style) {
			this(lineNumbers, style, "java");
		}

		/** Constructor preparing all given parameters as URL parameters. */
		public ConvertToStyle(boolean lineNumbers, String style, String lexer) {
			this.parameters =
					"lexer="+lexer+"&"+
							"style="+style+"&"+
							(lineNumbers ? "linenos=1&" : "");
			// good styles are: "monokai", "borland", "emacs", "default", "manni", "trac" "vs"
		}

		/**
		 * Call the highlighting website with given source code and
		 * return the highlighted HTML representation.
		 * Mind that no line-formatting is done, so the given javaSourceCode
		 * should contain newlines and be indented correctly!
		 * @param sourceCode the text to highlight as HTML.
		 * @return the highlighted HTML representing given source.
		 */
		public String hilite(String SourceCode)    {
			try    {
				final java.net.URL url = new URL(URL);

				final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestProperty("Accept-Charset", ENCODING);
				connection.setDoOutput(true); // triggers POST
				// big javaSourceCode works with POST only!
				try (final OutputStream output = connection.getOutputStream()) {
					String parameters = this.parameters+"code="+ URLEncoder.encode(SourceCode, ENCODING);
					output.write(parameters.getBytes(ENCODING));
				}

				final int responseCode = connection.getResponseCode();
				if (responseCode != 200)
					throw new IllegalStateException("Response code was: "+responseCode);

				try (final BufferedReader inputReader =
							 new BufferedReader(new InputStreamReader(connection.getInputStream(), ENCODING))) {
					final StringBuilder response = new StringBuilder();
					for (String line; (line = inputReader.readLine()) != null; ) {
						response.append(line);
						response.append(NEWLINE);
					}
					return response.toString();
				}
			}
			catch (Exception e)    {
				throw new RuntimeException(e);
			}
		}
	}
}

