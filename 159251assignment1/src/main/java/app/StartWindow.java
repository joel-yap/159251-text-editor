package app;

import javax.swing.*;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class StartWindow {

	public StartWindow() {
		JFrame frame = new JFrame("Text Editor");
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setBounds(200,170,500,500);
		JMenuBar mainMenuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File Menu");
		JMenuItem newItem = new JMenuItem("New");
		fileMenu.add(newItem);
		mainMenuBar.add(fileMenu);
		frame.setJMenuBar(mainMenuBar);
		
		frame.setVisible(true);

	}

}