/**
 * JTablePrintable wraps a JTable and implements the Printable
 * interface.
 * This class prints the table as a plain grid with bold headings
 * and automatically splits pages horizontally and vertically.
 */

package cvosteen.sqltool.gui.components;

import java.awt.*;
import java.awt.print.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

public class JTablePrintable implements Printable {

	ArrayList<TextEntity> entities = null;
	TextEntity titleEntity = null;
	TextEntity timestampEntity = null;
	boolean initialized = false;
	
	// Table data
	private String title = "";
	private GraphicalTable table;

	// Fonts
	private Font plainFont = new Font("Arial", Font.PLAIN, 10);
	private Font boldFont = new Font("Arial", Font.BOLD, 10);

	// Zooming and Fitting options
	private double zoom = 1.0;
	private boolean fitWidth = false;
	private int fitWidthPages = 0;
	private boolean fitHeight = false;
	private int fitHeightPages = 0;
	private final double zoomReadjust = 0.95;

	
	public JTablePrintable(JTable table) {
		// Make a shallow copy of all of the data in this JTable
		// right here!  If we do it later, the data could be GC'ed by
		// the time the print is actually performed,
		// especially if the user jumps to another query in the GUI.
		extractFromJTable(table);
	}

	/**
	 * Sets a title to be printed at the top of each page
	 * in the margins.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Explicitly set the zoom for printing this table
	 * The default is 100%
	 */
	public void setZoom(double zoom) {
		this.zoom = zoom;
		fitWidth = false;
		fitHeight = false;
	}

	/**
	 * Sets the number of pages that the table must
	 * fit into width-wise
	 */
	public void setFitWidth(int pages) {
		this.zoom = 1.0;
		fitWidth = true;
		fitWidthPages = pages;
	}

	/**
	 * Sets the number of pages that the table must
	 * fit into height-wise
	 */
	public void setFitHeight(int pages) {
		this.zoom = 1.0;
		fitHeight = true;
		fitHeightPages = pages;
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
		
		// Set the font
		g.setFont(plainFont);

		// Get the standard line height
		int lineHeight = g.getFontMetrics().getHeight();
		
		// All work should be done here to list all entities and their coordinates
		// and page numbers.  Ensure that these calculations are done only for the first
		// page request.  After that use the already calculated data.
		if(!initialized) {
			initialized = true;

			// The area within the page that the table will take up
			// leaving room for headers and footers
			Rectangle tableRect = new Rectangle(0, 2 * lineHeight, (int) pf.getImageableWidth(), (int) pf.getImageableHeight() - (4 * lineHeight));
			System.out.println("Print Area: " + tableRect.x + ", " + tableRect.y + ", " + tableRect.width + ", " + tableRect.height);

			// Set these parameters so the table can make needed calculations
			table.setPageArea(tableRect);
			table.setGraphics(g);

			// Set the zoom of the table
			table.setZoom(zoom);

			// Do we have page restrictions?
			if(fitWidth)
			{
				boolean fits = table.getPagesWide() <= fitWidthPages;
				while(!fits)
				{
					zoom *= zoomReadjust;
					table.setZoom(zoom);
					fits = table.getPagesWide() <= fitWidthPages;
				}
			}
			if(fitHeight)
			{
				boolean fits = table.getPagesHigh() <= fitHeightPages;
				while(!fits)
				{
					zoom *= zoomReadjust;
					table.setZoom(zoom);
					fits = table.getPagesHigh() <= fitHeightPages;
				}
			}

			// Generate the title entity
			g.setFont(boldFont);
			int titleWidth = g.getFontMetrics().stringWidth(title);
			int titleX = (int)(pf.getImageableWidth() / 2) - (titleWidth / 2);
			titleEntity = new TextEntity(title, boldFont, null, titleX, 0, titleWidth, lineHeight, 0, 0);
			
			// Generate the timestamp entity
			g.setFont(plainFont);
			String timestamp = new SimpleDateFormat("EEE, MMM d, yyyy   h:mm a").format(new Date());
			int timestampWidth = g.getFontMetrics().stringWidth(timestamp);
			int timestampY = (int) pf.getImageableHeight() - lineHeight;
			timestampEntity = new TextEntity(timestamp, plainFont, null, 0, timestampY, timestampWidth, lineHeight, 0, 0);

		}
		
		
		java.util.List<TextEntity> entities = table.getEntities(page);

		// Generate the Page x of y footer
		g.setFont(plainFont);
		String pageFooter = "Page " + (page+1) + " of " + table.getTotalPages();
		int pageFooterWidth = g.getFontMetrics().stringWidth(pageFooter);
		int pageFooterX = (int) pf.getImageableWidth() - pageFooterWidth;
		int pageFooterY = (int) pf.getImageableHeight() - lineHeight;
		entities.add(new TextEntity(pageFooter, plainFont, null, pageFooterX, pageFooterY, pageFooterWidth, lineHeight, 0, 0));

		// For now let's only print one page
		if(page >= table.getTotalPages())
			return NO_SUCH_PAGE;

		// This shouldn't happen, but better safe than sorry :P
		if(page < 0)
			return NO_SUCH_PAGE;

		// Now print the text entities
		titleEntity.draw(g);
		timestampEntity.draw(g);
		for(TextEntity e : entities) {
			e.draw(g);
		}
		
		return PAGE_EXISTS;
	}


	/**
	 * Extracts data from a JTable as a list of lists
	 * and saves the data as well as the dimensions in this
	 * instance's private variables.
	 */
	private void extractFromJTable(JTable jtable) {
		TableModel model = jtable.getModel();

		int numColumns = model.getColumnCount();
		int numRows = model.getRowCount();
		java.util.List<String> columns = new ArrayList<String>();
		java.util.List<java.util.List<String>> data = new ArrayList<java.util.List<String>>();

		for(int col = 0; col < numColumns; col++) {
			columns.add(model.getColumnName(col));
		}

		for(int row = 0; row < numRows; row++) {
			java.util.List<String> dataRow = new ArrayList<String>();
			for(int col = 0; col < numColumns; col++) {
				Object value = model.getValueAt(row, col);
				if(value == null)
					value = "";
				dataRow.add(value.toString());
			}
			data.add(dataRow);
		}

		table = new GraphicalTable(columns, data);
	}
	

	/**
	 * A text object that knows how to draw itself.
	 */
	public class TextEntity {
		public String text;
		public Font font;
		public Stroke boxStroke;
		public int x;
		public int y;
		public int width;
		public int height;
		public int xPad;
		public int yPad;

		public TextEntity(String text, Font font, Stroke boxStroke, int x, int y, int width, int height,
				int xPad, int yPad) {
			this.text = text;
			this.font = font;
			this.boxStroke = boxStroke;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.xPad = xPad;
			this.yPad = yPad;
		}

		public void draw(Graphics g) {
			g.setFont(font);
			FontMetrics fm = g.getFontMetrics();
			int yadj = yPad + fm.getLeading() + fm.getAscent();
			g.drawString(text, x + xPad, y + yadj);

			if(boxStroke != null) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setStroke(boxStroke);
				g.drawRect(x, y, width, height);
			}
		}
	}


	/**
	 * Object which knows how to graphically split a table across multiple pages.
	 */
	public class GraphicalTable {
		// The data in this table
		private java.util.List<String> columns;
		private java.util.List<java.util.List<String>> data;

		// Extra parameters needed to calculate size and pages
		private double zoom = 1.0; // Default zoom of 100%
		private Rectangle pageArea = null;
		private Graphics graphics = null;

		// Padding within cells
		final int yPadding = 1;
		final int xPadding = 3;

		// Fonts
		private final int defaultFontSize = 10;
		private final String defaultFontFamily = "Arial";
		private Font plainFont = new Font("Arial", Font.PLAIN, defaultFontSize);
		private Font boldFont = new Font("Arial", Font.BOLD, defaultFontSize);

		// Column widths and row height at 100% zoom
		private int[] columnWidths;
		private int rowHeight;

		/**
		 * Creates a GraphicalTable instance with the specified column headings
		 * and data.
		 */		
		public GraphicalTable(java.util.List<String> columns, java.util.List<java.util.List<String>> data) {
			this.columns = columns;
			this.data = data;
		}

		/**
		 * Set the zoom factor. 1.0 is 100%
		 */
		public void setZoom(double zoom) {
			this.zoom = zoom;
		}

		/**
		 * Gets the zoom factor for this table.
		 */
		public double getZoom() {
			return zoom;
		}

		/**
		 * Sets the area this table will use to
		 * calculate page boundaries
		 */
		public void setPageArea(Rectangle pageArea) {
			this.pageArea = pageArea;
		}

		/**
		 * Sets the graphics object.  Calculates and caches the
		 * size of the rows and columns in the graphics context.
		 */
		public void setGraphics(Graphics graphics) {
			this.graphics = graphics;
			// Calculate column widths and row height at 100% zoom
			int numColumns = getColumnCount();
			columnWidths = new int[columns.size()];
			for(int c = 0; c < numColumns; c++) {
				columnWidths[c] = getColumnWidth(graphics, c) + (2 * xPadding);
				System.out.println("Column width " + c + ": " + columnWidths[c]);
			}
			rowHeight = graphics.getFontMetrics().getHeight() + (2 * yPadding);
		}

		/**
		 * For a specified graphics object, calculates the width of a column
		 * by using the width of the widest cell in the column
		 */
		private int getColumnWidth(Graphics g, int column) {
			int maxWidth = 0;
			// First get the column heading width
			String thisString = columns.get(column);
			int thisWidth = g.getFontMetrics(boldFont).stringWidth(thisString);
			maxWidth = thisWidth;

			// Go row by row measuring each item's width
			for(int row = 0; row < data.size(); row++) {
				thisString = data.get(row).get(column);
				thisWidth = g.getFontMetrics(plainFont).stringWidth(thisString);
				if(thisWidth > maxWidth)
					maxWidth = thisWidth;
			}
			return maxWidth;
		}

		/**
		 * Returns the number of columns in this table.
		 */
		public int getColumnCount() {
			return columns.size();
		}

		/**
		 * Returns the number of rows in this table.
		 */
		public int getRowCount() {
			return data.size();
		}

		/**
		 * Returns the name of the specified column.
		 */
		public String getColumnName(int column) {
			return columns.get(column);
		}

		/**
		 * Returns the contents of the cell at the specified row and column.
		 */
		public String get(int row, int column) {
			return data.get(row).get(column);
		}

		/**
		 * Calculates the first column number to appear on
		 * the specified x-axis page number.
		 */
		private int getFirstColumnOnPageX(int pageX) {
			int currentPageX = 0;
			int col = 0;
			int pos = 0;
			while(currentPageX < pageX && col < getColumnCount()) {
				int width = (int)(columnWidths[col] * zoom);
				if(width + pos > pageArea.width && pos != 0) {
					// i.e. this column belongs in the next page
					// unless the column already takes up the entire page!
					currentPageX++;
					pos = 0;
				} else {
					pos += width;
					col++;
				}
			}
			return col;
		}

		/**
		 * Calculates the first row number to appear on
		 * the specified y-axis page number.
		 */
		private int getFirstRowOnPageY(int pageY) {
			int rowsPerPage = pageArea.height / (int)(rowHeight * zoom);
			int firstRow = rowsPerPage * pageY - 1; // Subract 1 for header row
			firstRow = firstRow < 0 ? 0 : firstRow;
			firstRow = firstRow > getRowCount() ? getRowCount() : firstRow;
			return firstRow;
		}

		/**
		 * Calculates how many pages wide this table will be
		 */
		public int getPagesWide() {
			int page = 0;
			int pos = 0;
			for(int c = 0; c < getColumnCount(); c++) {
				int width = (int)(columnWidths[c] * zoom);
				if(width + pos > pageArea.width && pos != 0) {
					// i.e. this column belongs in the next page
					// unless the column already takes up the entire page!
					page++;
					pos = width;
				} else {
					pos += width;
				}
			}
			return page + 1;
		}

		/**
		 * Calculates how many pages high this table will be
		 */
		public int getPagesHigh() {
			int rowsPerPage = pageArea.height / (int)(rowHeight * zoom);
			return ((data.size() + 1) / rowsPerPage) + 1;
		}

		/**
		 * Returns the total pages that the table takes up
		 */
		public int getTotalPages() {
			return getPagesWide() * getPagesHigh();
		}

		/**
		 * Return a list of TextEntity objects that belon on the specified page number
		 */
		public java.util.List<TextEntity> getEntities(int page) {
			if(pageArea == null) {
				throw new MissingResourceException(
					"This object needs the page area in order to make the needed calculations",
					"Rectangle", "pageArea");
			}
			if(graphics == null) {
				throw new MissingResourceException(
					"This object needs a graphics object in order to make the needed calculations",
					"Graphics", "graphics");
			}
			int pagesHigh = getPagesHigh();
			int pageX = page / pagesHigh;
			int pageY = page % pagesHigh;
			
			// Which columns and rows fall on this page
			int colStart = getFirstColumnOnPageX(pageX);
			int colEnd = getFirstColumnOnPageX(pageX + 1);
			int rowStart = getFirstRowOnPageY(pageY);
			int rowEnd = getFirstRowOnPageY(pageY + 1);
			System.out.println("page " + page + ": row " + rowStart + " to " + rowEnd + ", col " + colStart + " to " + colEnd);

			// Scale fonts down (or up) to zoom
			Font scaledBold = new Font(defaultFontFamily, Font.BOLD, (int)(defaultFontSize * zoom));
			Font scaledPlain = new Font(defaultFontFamily, Font.PLAIN, (int)(defaultFontSize * zoom));
			
			// If this is a "top" page, include the column headers
			java.util.List<TextEntity> entities = new ArrayList<TextEntity>();
			Stroke stroke = new BasicStroke((float)zoom);
			int y = pageArea.y;
			int height = (int)(rowHeight * zoom);
			if(pageY == 0) {
				int x = pageArea.x;
				for(int col = colStart; col < colEnd; col++) {
					int width = (int)(columnWidths[col] * zoom);
					entities.add(new TextEntity(getColumnName(col), scaledBold, stroke, x, y, width, height,
						(int)(xPadding * zoom), (int)(yPadding * zoom)));
					x += width;
				}
				y += height;
			}

			// include table data
			for(int row = rowStart; row < rowEnd; row++) {
				int x = 0;
				for(int col = colStart; col < colEnd; col++) {
					int width = (int)(columnWidths[col] * zoom);
					entities.add(new TextEntity(get(row, col), scaledPlain, stroke, x, y, width, height,
						(int)(xPadding * zoom), (int)(yPadding * zoom)));
					x += width;
				}
				y += height;
			}

			return entities;
		}

	}
}
	
