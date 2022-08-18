package app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class StartWindow {
	private JFrame frame;
	private JMenuBar mainMenuBar;
	private JMenu fileMenu;
	private JMenuItem newItem;
	private JEditorPane editorPane;
	private JMenuItem openItem;
	private JFileChooser chooser;
	private Container container;
	private int dialogValue;
	private String filename;

	public StartWindow() {
// Frame
		frame = new JFrame("Text Editor");
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setBounds(200,170,500,500);

// Menu bar
		mainMenuBar = new JMenuBar();
		frame.setJMenuBar(mainMenuBar);

// Menu bar item: File
		fileMenu = new JMenu("File Menu");
		mainMenuBar.add(fileMenu);

// File menu item: New File
		newItem = new JMenuItem(new AbstractAction("New") {
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(frame, "Save changes?\n"+ "Unsaved changes will be lost.");
				switch(result) {
					case JOptionPane.YES_OPTION:
						// Save changes
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

// Initialize Editor pane
		editorPane = new JEditorPane();
		editorPane.setBounds(frame.getBounds());
		frame.add(editorPane, BorderLayout.CENTER);

// Show all elements
		frame.setContentPane(editorPane);
		frame.setVisible(true);

// File menu item: Open File
		openItem = new JMenuItem("Open");
		container = frame.getContentPane();
		chooser = new JFileChooser();
		//container = null;
		dialogValue = 0;
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialogValue = chooser.showOpenDialog(container);
				if (dialogValue == JFileChooser.APPROVE_OPTION) {
					String filePath = chooser.getSelectedFile().getPath();
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
		});
		fileMenu.add(openItem);
	}


}