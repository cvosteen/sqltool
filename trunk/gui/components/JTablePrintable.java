/**
 * JTablePrintable wraps a JTable and implements the Printable
 * interface.
 * This class prints the table as a plain grid with bold headings
 * and automatically splits pages horizontally and vertically.
 */

package gui.components;

import java.awt.*;
import java.awt.print.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

public class JTablePrintable implements Printable {

	JTable table;
	ArrayList<TextEntity> entities = null;
	String title = "";
	TextEntity titleEntity = null;
	TextEntity timestampEntity = null;
	
	int maxPageX = 0;
	int maxPageY = 0;
	final Font plainFont = new Font("Arial", Font.PLAIN, 8);
	final Font boldFont = new Font("Arial", Font.BOLD, 8);
	final int yPadding = 0;
	final int xPadding = 3;

	
	public JTablePrintable(JTable table) {
		this.table = table;
	}

	/**
	 * Sets a title to be printed at the top of each page
	 * in the margins.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Implement the printable interface.
	 * Most of the calculations must take place here because we won't know
	 * the size of the page etc. until print time.
	 */
	public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
		// Translate so it fits within the imageable area
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(pf.getImageableX(), pf.getImageableY());
		
		// Set the font, default is too big
		g.setFont(plainFont);
		
		// Standard height of a line of text
		int lineheight = g.getFontMetrics().getHeight() + (2 * yPadding);
		
		// The area within the page that the table will take up
		// leaving room for headers and footers
		Rectangle tableRect = new Rectangle(0, 2 * lineheight, (int) pf.getImageableWidth(), (int) pf.getImageableHeight() - (4 * lineheight));

		// Number of table rows that will fit in a page
		int rowsPerPage = tableRect.height / lineheight;

		// All work should be done here to list all entities and their coordinates
		// and page numbers.  Ensure that these calculations are done only for the first
		// page request.  After that use the already calculated data.
		if(entities == null) {
			entities = new ArrayList<TextEntity>();
			TableModel model = table.getModel();
		
			// Generate the title entity
			g.setFont(boldFont);
			int titleWidth = g.getFontMetrics().stringWidth(title);
			int titleX = (int)(pf.getImageableWidth() / 2) - (titleWidth / 2);
			titleEntity = new TextEntity(title, true, false, titleX, 0, titleWidth, lineheight, -1, -1);
			
			// Generate the timestamp entity
			g.setFont(plainFont);
			String timestamp = new SimpleDateFormat("EEE, MMM d, yyyy   h:mm a").format(new Date());
			int timestampWidth = g.getFontMetrics().stringWidth(timestamp);
			int timestampY = (int) pf.getImageableHeight() - lineheight;
			timestampEntity = new TextEntity(timestamp, false, false, 0, timestampY, timestampWidth, lineheight, -1, -1);

			// Collect table entities one column at a time
			// Keep track of the x coordinate as we traverse each column
			// The y coordinate is just a multiple of lineheight
			// The vertical page number is also easly calculated
			int currentX = tableRect.x;
			int pageX = 0;
			for(int col = 0; col < model.getColumnCount(); col++) {
				int colWidth = getColumnWidth(model, g, col);
				colWidth += (2 * xPadding);

				// Advance pageX if needed
				if(currentX + colWidth > tableRect.width && currentX > 0) {
					currentX = 0;
					pageX++;
				}

				// Now create the column's entities
				String thisString = model.getColumnName(col);
				entities.add(new TextEntity(thisString, true, true, currentX, tableRect.y, colWidth, lineheight, pageX, 0));
				for(int row = 0; row < model.getRowCount(); row++) {
					Object value = model.getValueAt(row, col);
					if(value == null)
						value = "";
					thisString = value.toString();
					int pageY = (row + 1) / rowsPerPage; // Add 1 for the headers
					int y = ((row + 1) - (pageY * rowsPerPage)) * lineheight + tableRect.y;
					entities.add(new TextEntity(thisString, false, true, currentX, y, colWidth, lineheight, pageX, pageY));
				}
				currentX += colWidth;
			}
			maxPageX = pageX;
			maxPageY = (model.getRowCount() + 1) / rowsPerPage;

			// Generate the Page x of y footers
			g.setFont(plainFont);
			int totalPages = ((maxPageY + 1) * (maxPageX + 1));
			for(int x = 0; x <= maxPageX; x++) {
				for(int y = 0; y <= maxPageX; y++) {
					int pageNum = (x * (maxPageY + 1)) + y + 1;
					String pageFooter = "Page " + pageNum + " of " + totalPages;
					int pageFooterY = (int) pf.getImageableHeight() - lineheight;
					int pageFooterWidth = g.getFontMetrics().stringWidth(pageFooter);
					int pageFooterX = (int) pf.getImageableWidth() - pageFooterWidth - xPadding;
					entities.add(new TextEntity(pageFooter, false, false, pageFooterX, pageFooterY, pageFooterWidth, lineheight, x, y));
				}
			}

		}

		// For now let's only print one page
		if(page > ((maxPageY + 1) * (maxPageX + 1)) - 1)
			return NO_SUCH_PAGE;

		// This shouldn't happen, but better safe than sorry :P
		if(page < 0)
			return NO_SUCH_PAGE;

		// Now print the text entities
		titleEntity.draw(g);
		timestampEntity.draw(g);
		for(TextEntity e : entities) {
			if(page == (e.pageX * (maxPageY + 1)) + e.pageY) {
				e.draw(g);
			}
		}
		
		return PAGE_EXISTS;
	}

	/**
	 * Calculates how wide a column needs to be to fit all of the
	 * data for that column.
	 */
	private int getColumnWidth(TableModel model, Graphics g, int column) {
		int maxWidth = 0;
		// First get the column heading width
		String thisString = model.getColumnName(column);
		int thisWidth = g.getFontMetrics(boldFont).stringWidth(thisString);
		maxWidth = thisWidth;

		// Go row by row measuring each item's width
		for(int row = 0; row < model.getRowCount(); row++) {
			Object value = model.getValueAt(row, column);
			if(value == null)
				value = "";
			thisString = value.toString();
			thisWidth = g.getFontMetrics(plainFont).stringWidth(thisString);
			if(thisWidth > maxWidth)
				maxWidth = thisWidth;
		}
		return maxWidth;
	}

	public class TextEntity {
		public String text;
		public boolean bold;
		public boolean boxed;
		public int x;
		public int y;
		public int width;
		public int height;
		public int pageX;
		public int pageY;

		public TextEntity(String text, boolean bold, boolean boxed, int x, int y, int width, int height,
				int pageX, int pageY) {
			this.text = text;
			this.bold = bold;
			this.boxed = boxed;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.pageX = pageX;
			this.pageY = pageY;
		}

		public void draw(Graphics g) {
			if(bold)
				g.setFont(boldFont);
			else
				g.setFont(plainFont);

			FontMetrics fm = g.getFontMetrics();
			int ypad = yPadding + fm.getLeading() + fm.getAscent();
			g.drawString(text, x + xPadding, y + ypad);

			if(boxed)
				g.drawRect(x, y, width, height);
		}
	}

}
	
