/**
 * Very plain information dialog to show basic information about
 * this application as well as the license.
 */

package gui;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.text.*;

public class AboutDialog extends JDialog {

	public AboutDialog(Frame owner, String name, String version) {
		super(owner, "About " + name, true);

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		// Set the layout (GridBag)
		JPanel panel = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		panel.setLayout(gridbag);
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,5,5,5);
	
		// Add the title
		c.weightx = 1.0;
		JLabel label = new JLabel(name + " v" + version);
		Font font = label.getFont();
		label.setFont(new Font(font.getFontName(), Font.BOLD, font.getSize()));
		gridbag.setConstraints(label, c);
		panel.add(label);

		// Add an Ok button
		c.weightx = 0.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		JButton button = new JButton("OK");
		button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
		button.setDefaultCapable(true);
		gridbag.setConstraints(button, c);
		panel.add(button);

		c.gridwidth = GridBagConstraints.REMAINDER;
		label = new JLabel("Distributed under an open-source BSD License:");
		gridbag.setConstraints(label, c);
		panel.add(label);

		// Add the copyright/licence info
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		JEditorPane text = null;
		try {
			text = new JEditorPane(this.getClass().getResource("/license.html"));
		} catch(IOException e) {
			JOptionPane.showMessageDialog(this,
				"Unable to read licence.html", "Error",
				JOptionPane.ERROR_MESSAGE);
			text = new JEditorPane();
		}
		text.setCaretPosition(0);
		text.setEditable(false);
		Dimension size = text.getPreferredSize();
		text.setPreferredSize(new Dimension(400, size.height));
		JScrollPane scroll = new JScrollPane(text);
		scroll.setMaximumSize(new Dimension(scroll.getPreferredSize()));
		gridbag.setConstraints(scroll, c);
		panel.add(scroll);


		getRootPane().setDefaultButton(button);
		button.requestFocusInWindow();
		getContentPane().add(panel);
		setMinimumSize(new Dimension(300,250));
		pack();

		// center the window on the screen
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenDim.width-getWidth())/2,
			(screenDim.height-getHeight())/2);

		setVisible(true);
	}
	
}
