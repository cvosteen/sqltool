/**
 * A dialog that allows a user to specify the printed table
 * will fit along several pages.
 */

package cvosteen.sqltool.gui;

import cvosteen.sqltool.database.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class PageSetupDialog extends JDialog implements ResponseGetter<PageSetup> {

	private PageSetup pageSetup;
	private final JCheckBox zoomCheck = new JCheckBox();
	private final JFormattedTextField zoomField = new JFormattedTextField(new Integer(100));
	private final JCheckBox pagesWideCheck = new JCheckBox();
	private final JFormattedTextField pagesWideField = new JFormattedTextField(new Integer(1));
	private final JCheckBox pagesHighCheck = new JCheckBox();
	private final JFormattedTextField pagesHighField = new JFormattedTextField(new Integer(1));

	/**
	 * Creates a PageSetupDialog that will return a new
	 * PageSetup instance.
	 */
	public PageSetupDialog(Frame owner) {
		super(owner, "Page Setup", true);

		// Create this dialog to create a new Database instance
		pageSetup = new PageSetup();
		createComponents();

		checkZoom(pageSetup.isZoom());
		checkPagesWide(pageSetup.isFitWidth());
		checkPagesHigh(pageSetup.isFitHeight());
		setZoom(pageSetup.getZoom());
		setPagesWide(pageSetup.getFitWidthPages());
		setPagesHigh(pageSetup.getFitHeightPages());
		setVisible(true);
	}

	/**
	 * Creates a PageSetupDialog that will edit an existing
	 * Database instance.
	 */
	public PageSetupDialog(Frame owner, PageSetup pageSetup) {
		super(owner, "Page Setup", true);

		this.pageSetup = pageSetup;
		createComponents();
		
		checkZoom(pageSetup.isZoom());
		checkPagesWide(pageSetup.isFitWidth());
		checkPagesHigh(pageSetup.isFitHeight());
		setZoom(pageSetup.getZoom());
		setPagesWide(pageSetup.getFitWidthPages());
		setPagesHigh(pageSetup.getFitHeightPages());
		setVisible(true);
	}

	private void createComponents() {
		// We want to hide this object when we click on the 'X'
		// That way we can retreive the PageSetup instance if necessary
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

		// Set the layout (GridBag)
		JPanel panel = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		panel.setLayout(gridbag);
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,5,5,5);
	
		// Zoom option
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		c.gridheight = 1;
		zoomCheck.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					checkZoom(e.getStateChange() == ItemEvent.SELECTED);
				}
			});
		gridbag.setConstraints(zoomCheck, c);
		panel.add(zoomCheck);
		JLabel label = new JLabel("Zoom to");
		gridbag.setConstraints(label, c);
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		zoomField.setMaximumSize(new Dimension(2000, zoomField.getPreferredSize().height));
		gridbag.setConstraints(zoomField, c);
		panel.add(zoomField);
		
		// Fit Width option
		c.weightx = 0.0;
		c.gridwidth = 1;
		pagesWideCheck.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					checkPagesWide(e.getStateChange() == ItemEvent.SELECTED);
				}
			});
		gridbag.setConstraints(pagesWideCheck, c);
		panel.add(pagesWideCheck);
		label = new JLabel("Fit to");
		gridbag.setConstraints(label, c);
		panel.add(label);
		c.weightx = 1.0;
		pagesWideField.setMaximumSize(new Dimension(2000, pagesWideField.getPreferredSize().height));
		gridbag.setConstraints(pagesWideField, c);
		panel.add(pagesWideField);
		c.weightx = 0.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		label = new JLabel("pages wide.");
		gridbag.setConstraints(label, c);
		panel.add(label);
		
		// Fit Height option
		c.gridwidth = 1;
		pagesHighCheck.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					checkPagesHigh(e.getStateChange() == ItemEvent.SELECTED);
				}
			});
		gridbag.setConstraints(pagesHighCheck, c);
		panel.add(pagesHighCheck);
		label = new JLabel("Fit to");
		gridbag.setConstraints(label, c);
		panel.add(label);
		c.weightx = 1.0;
		pagesHighField.setMaximumSize(new Dimension(2000, pagesHighField.getPreferredSize().height));
		gridbag.setConstraints(pagesHighField, c);
		panel.add(pagesHighField);
		c.weightx = 0.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		label = new JLabel("pages high.");
		gridbag.setConstraints(label, c);
		panel.add(label);

		// Save and Cancel buttons on bottom right
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridx = 2;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.SOUTHEAST;
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					savePageSetup();
				}
			});
		gridbag.setConstraints(saveButton, c);
		panel.add(saveButton);
		c.gridx = GridBagConstraints.RELATIVE;
		c.weightx = 0.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		JButton cancelButton = new JButton("Cancel");
		cancelButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "doCancel");
		cancelButton.getActionMap().put("doCancel", new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});
		cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});
		gridbag.setConstraints(cancelButton, c);
		panel.add(cancelButton);


		getContentPane().add(panel);
		setMinimumSize(new Dimension(300,200));
		pack();
		zoomCheck.requestFocusInWindow();
		getRootPane().setDefaultButton(saveButton);

		// center the window on the screen
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenDim.width-getWidth())/2,
			(screenDim.height-getHeight())/2);

	}
	
	/**
	 * Called when the user presses 'Enter' or clicks 'Save'
	 */
	private void savePageSetup() {
		// Plain page setup object if nothing is checked.
		pageSetup = new PageSetup();
		
		// Set a zoomed page setup if zoom is checked
		if(zoomCheck.isSelected())
		{
			double zoom = 1.0;
			try {
				zoom = ((Integer)zoomField.getValue()).doubleValue() / 100.0;
			} catch(NumberFormatException e) { }
			pageSetup = new PageSetup(zoom);
		}
		
		// Set a fitted page setup if either fit check box is checked
		if(pagesWideCheck.isSelected() || pagesHighCheck.isSelected())
		{
			int fitWidth = 1;
			int fitHeight = 1;
			try {
				fitWidth = (Integer)pagesWideField.getValue();
			} catch(NumberFormatException e) { }
			try {
				fitHeight = (Integer)pagesHighField.getValue();
			} catch(NumberFormatException e) { }
			pageSetup = new PageSetup(pagesWideCheck.isSelected(), fitWidth, pagesHighCheck.isSelected(), fitHeight);
		}

		setVisible(false);
	}

	/**
	 * Called by parent window or other client to get the
	 * user's choice or response from this dialog.
	 */
	public PageSetup getResponse() {
		return pageSetup;
	}

	private void checkZoom(boolean checked) {
		if(checked) {
			zoomField.setEnabled(true);
			pagesWideField.setEnabled(false);
			pagesHighField.setEnabled(false);
			zoomCheck.setSelected(true);
			pagesWideCheck.setSelected(false);
			pagesHighCheck.setSelected(false);
		} else {
			zoomField.setEnabled(false);
		}
	}

	private void checkPagesWide(boolean checked) {
		if(checked) {
			zoomField.setEnabled(false);
			pagesWideField.setEnabled(true);
			zoomCheck.setSelected(false);
			pagesWideCheck.setSelected(true);
		} else {
			pagesWideField.setEnabled(false);
		}
	}

	private void checkPagesHigh(boolean checked) {
		if(checked) {
			zoomField.setEnabled(false);
			pagesHighField.setEnabled(true);
			zoomCheck.setSelected(false);
			pagesHighCheck.setSelected(true);
		} else {
			pagesHighField.setEnabled(false);
		}
	}

	private void setZoom(double zoom) {
		zoomField.setValue(Integer.valueOf((int)(100 * zoom)));
	}

	private void setPagesWide(int pages) {
		pagesWideField.setValue(Integer.valueOf(pages));
	}

	private void setPagesHigh(int pages) {
		pagesHighField.setValue(Integer.valueOf(pages));
	}
}
