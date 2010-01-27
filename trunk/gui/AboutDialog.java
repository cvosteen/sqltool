import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.text.*;

public class AboutDialog extends JDialog {

	private static final String license =
		"<font face=\"Arial\" size=\"2\">" +
		"Copyright (c) 2009, Christian Vosteen<br />" +
		"All rights reserved.<br /><br />" +
		"<p>Redistribution and use in source and binary forms, with " +
		"or without modification, are permitted provided that the " +
		"following conditions are met:</p>" +
		"<ul><li>Redistributions of source code must retain the above " +
		"copyright notice, this list of conditions and the following " +
		"disclaimer.</li>" +
		"<li>Redistributions in binary form must reproduce the above " +
		"copyright notice, this list of conditions and the following " +
		"disclaimer in the documentation and/or other materials provided " +
		"with the distribution.</li>" +
		"<li>The names of its contributors may not be used to endorse or " +
		"promote products derived from this software without specific " +
		"prior written permission.</li></ul>" +
		"<p>THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS " +
		"\"AS IS\" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT " +
		"NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND " +
		"FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT " +
		"SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY " +
		"DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL " +
		"DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE " +
		"GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS " +
		"INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, " +
		"WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING " +
		"NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF " +
		"THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH " +
		"DAMAGE.</p></font>";

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
		JEditorPane text = new JEditorPane("text/html", license);
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
		setMinimumSize(new Dimension(300,200));
		pack();

		// center the window on the screen
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenDim.width-getWidth())/2,
			(screenDim.height-getHeight())/2);

		setVisible(true);

	}
	
}
