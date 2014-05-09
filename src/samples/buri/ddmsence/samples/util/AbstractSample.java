/* Copyright 2010 - 2013 by Brian Uri!
   
   This file is part of DDMSence.
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 3.0 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
   GNU Lesser General Public License for more details.
   
   You should have received a copy of the GNU Lesser General Public 
   License along with DDMSence. If not, see <http://www.gnu.org/licenses/>.

   You can contact the author at ddmsence@urizone.net. The DDMSence
   home page is located at http://ddmsence.urizone.net/
 */
package buri.ddmsence.samples.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import nu.xom.Document;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.UnsupportedVersionException;
import buri.ddmsence.util.DDMSReader;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * Abstract base class for sample applications.
 * 
 * <p>
 * This class contains components that are reused in the sample applications, and tries
 * to keep the main applications free of Swing-related clutter.
 * </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public abstract class AbstractSample implements ActionListener {
	private JFrame _frame;

	protected static final String FILE = "File";
	protected static final String OPEN = "Open...";
	protected static final String EXIT = "Exit";

	/**
	 * Initializes shared components
	 */
	public AbstractSample(String title, Dimension size, boolean hasFileMenu) throws SAXException {
		_frame = buildFrame(title, size, hasFileMenu);
	}


	/**
	 * Returns a Reader for a specific version of DDMS
	 */
	protected DDMSReader getReader(DDMSVersion version) throws SAXException {
		return (new DDMSReader(version));
	}
	
	/**
	 * Helper method to attempt to guess which version of DDMS to use, based
	 * upon the namespace URI of the root element, via a non-validating builder.
	 * 
	 * @param potentialResource a File containing the resource
	 * @return the version
	 * @throws UnsupportedVersionException if the version could not be guessed.
	 * @throws InvalidDDMSException if the file could not be parsed.
	 */
	protected DDMSVersion guessVersion(File potentialResource) throws InvalidDDMSException {
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader(PropertyReader.getProperty("xml.reader.class"));
			nu.xom.Builder builder = new nu.xom.Builder(reader, false);
			Document doc = builder.build(new FileReader(potentialResource));
			String namespace = doc.getRootElement().getNamespaceURI();
			return (DDMSVersion.getVersionForNamespace(namespace));
		}
		catch (Exception e) {
			throw new InvalidDDMSException("Could not create a valid element from potential resource: " + e.getMessage());
		}
	}
	
	/**
	 * Returns a set of default instructions to display on startup.
	 */
	protected abstract String getDefaultInstructions();

	/**
	 * The base action listener just catches EXIT events.
	 * 
	 * @see ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (EXIT.equals(e.getActionCommand())) {
			getFrame().setVisible(false);
			System.exit(0);
		}
	}

	/**
	 * Builds the outer frame.
	 * 
	 * @param title the simple title of the app
	 * @param size the size of the app frame
	 * @param hasFileMenu whether to build a menubar
	 */
	private JFrame buildFrame(String title, Dimension size, boolean hasFileMenu) {
		JPanel contentPane = new JPanel(new BorderLayout());
		if (!Util.isEmpty(getDefaultInstructions()))
			contentPane.add(new JLabel(getDefaultInstructions(), JLabel.CENTER), BorderLayout.CENTER);
		JFrame frame = new JFrame(title + ": a DDMSence Sample");
		frame.setSize(size);
		frame.setPreferredSize(size);
		if (hasFileMenu)
			frame.setJMenuBar(buildMenuBar());
		frame.setContentPane(contentPane);
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		centerFrame(frame);
		return (frame);
	}

	/**
	 * Builds the menu bar for the sample application.
	 * 
	 * @return JMenuBar
	 */
	private JMenuBar buildMenuBar() {
		JMenuItem openItem = new JMenuItem(OPEN);
		openItem.setActionCommand(OPEN);
		openItem.setMnemonic('O');
		openItem.addActionListener(this);

		JMenuItem exitItem = new JMenuItem(EXIT);
		exitItem.setActionCommand(EXIT);
		exitItem.setMnemonic('x');
		exitItem.addActionListener(this);

		JMenu fileMenu = new JMenu(FILE);
		fileMenu.add(openItem);
		fileMenu.add(exitItem);
		fileMenu.setMnemonic('F');

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);

		return (menuBar);
	}

	/**
	 * Builds a labelled panel containing some text.
	 * 
	 * @param name the name of this panel
	 * @param text the text that will go in this panel
	 * @return a panel
	 */
	protected JPanel buildLabelledPanel(String name, String text) {
		JTextArea textArea = new JTextArea(text);
		textArea.setEditable(false);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));

		JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		JPanel outerPanel = new JPanel(new BorderLayout());
		outerPanel.add(new JLabel(name, JLabel.LEFT), BorderLayout.NORTH);
		outerPanel.add(scrollPane, BorderLayout.CENTER);
		outerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		return (outerPanel);
	}

	/**
	 * Creates a panel to show any error messages.
	 * 
	 * @param messagePrefix custom text to display before the actual error
	 * @param e the error to show
	 * @return a panel
	 */
	protected JPanel buildErrorPanel(String messagePrefix, Exception e) {
		JTextArea textArea = new JTextArea(messagePrefix + e.getMessage());
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setPreferredSize(new Dimension(800, 500));
		textArea.setEditable(false);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));

		JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		JPanel resultPanel = new JPanel();
		resultPanel.add(scrollPane);
		return (resultPanel);
	}

	/**
	 * Creates a panel containing a Google visualization
	 * 
	 * @param url the url of the data image
	 * @return JPanel
	 */
	protected JPanel buildVisualizationPanel(URL url) throws IOException {
		ImageIcon image = new ImageIcon(url);
		JTextField field = new JTextField(url.toString());
		field.setEditable(false);
		field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20),
			BorderFactory.createLineBorder(Color.BLACK)));
		field.setBackground(Color.WHITE);
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.add(new JLabel(image), BorderLayout.CENTER);
		panel.add(field, BorderLayout.SOUTH);
		return (panel);
	}

	/**
	 * Adds the new panel to the UI and repaints.
	 * 
	 * @param panel a new panel to replace the existing contents of the contentPane
	 */
	protected void refreshUI(JPanel panel) {
		getFrame().getContentPane().removeAll();
		getFrame().getContentPane().add(panel, BorderLayout.CENTER);
		getFrame().pack();
		getFrame().repaint();
	}

	/**
	 * Centers a frame on the screen
	 * 
	 * @param frame the frame
	 */
	public void centerFrame(JFrame frame) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((screenSize.width - frame.getWidth()) / 2, (screenSize.height - frame.getHeight()) / 2);
	}

	/**
	 * Exposes visibility of the frame
	 * 
	 * @param b true for showing, false for hiding
	 */
	public void setVisible(boolean b) {
		getFrame().setVisible(b);
	}

	/**
	 * Accessor for the frame
	 */
	protected JFrame getFrame() {
		return (_frame);
	}
}
