package app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class StartWindow {
	private JFrame frame;
	private JMenuBar mainMenuBar;
	private JMenu fileMenu;
	private JMenuItem newItem;
	private JEditorPane editorPane;

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

	}

}