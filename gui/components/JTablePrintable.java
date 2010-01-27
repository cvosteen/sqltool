package gui.components;
import java.awt.*;
import java.awt.print.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

public class JTablePrintable implements Printable {

	JTable table;
	ArrayList<TableEntity> entities = null;
	int maxPageX = 0;
	int maxPageY = 0;
	final Font plainFont = new Font("Arial", Font.PLAIN, 8);
	final Font boldFont = new Font("Arial", Font.BOLD, 8);
	final int yPadding = 0;
	final int xPadding = 3;

	
	public JTablePrintable(JTable table) {
		this.table = table;
	}

	public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
		// Translate so it fits within the imageable area
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(pf.getImageableX(), pf.getImageableY());
		
		// Set the font, default it too big
		g.setFont(plainFont);

		// All work should be done here to list all entities and their coordinates
		// and page numbers
		if(entities == null) {
			entities = new ArrayList<TableEntity>();
			TableModel model = table.getModel();
			
			// Collect table entities one column at a time
			int lineheight = g.getFontMetrics().getHeight() + (2 * yPadding);
			int currentX = 0;
			for(int col = 0; col < model.getColumnCount(); col++) {
				// First calc the column's width
				int maxWidth = 0;
				String thisString = model.getColumnName(col);
				int thisWidth = g.getFontMetrics(boldFont).stringWidth(thisString);
				if(thisWidth > maxWidth)
					maxWidth = thisWidth;
				for(int row = 0; row < model.getRowCount(); row++) {
					Object value = model.getValueAt(row, col);
					if(value == null)
						value = "";
					thisString = value.toString();
					thisWidth = g.getFontMetrics().stringWidth(thisString);
					if(thisWidth > maxWidth)
						maxWidth = thisWidth;
				}
				maxWidth += (2 * xPadding);

				// Now create the column's entities
				thisString = model.getColumnName(col);
				entities.add(new TableEntity(thisString, true, true, currentX, 0, maxWidth, lineheight, -1, -1));
				for(int row = 0; row < model.getRowCount(); row++) {
					Object value = model.getValueAt(row, col);
					if(value == null)
						value = "";
					thisString = value.toString();
					entities.add(new TableEntity(thisString, false, true, currentX, (row + 1) * lineheight, maxWidth, lineheight, -1, -1));
				}
				currentX += maxWidth;
			}

			// Paginate the entities vertically
			int pageHeight = (int) pf.getImageableHeight();
			int pageY = 0;
			int minY = 0;
			boolean moreEntities = true;
			while(moreEntities) {
				int newMinY = 2000000000;
				moreEntities = false;
				for(TableEntity e : entities) {
					if(e.pageY == -1) {
						// Adjust height by previous page's content's height
						e.y -= minY;
						// Find all items on current page
						if(e.y + e.height < pageHeight || e.y == 0) {
							e.pageY = pageY;
						} else {
							moreEntities = true;
							if(e.y < newMinY)
								newMinY = e.y;
						}
					}
				}
				minY = newMinY;
				pageY++;
			}
			maxPageY = pageY - 1;

			// Paginate the entities horizontally
			int pageWidth = (int) pf.getImageableWidth();
			int pageX = 0;
			int minX = 0;
			moreEntities = true;
			while(moreEntities) {
				int newMinX = 2000000000;
				moreEntities = false;
				for(TableEntity e : entities) {
					if(e.pageX == -1) {
						// Adjust height by previous page's content's height
						e.x -= minX;
						// Find all items on current page
						if(e.x + e.width < pageWidth || e.x == 0) {
							e.pageX = pageX;
						} else {
							moreEntities = true;
							if(e.x < newMinX)
								newMinX = e.x;
						}
					}
				}
				minX = newMinX;
				pageX++;
			}
			maxPageX = pageX - 1;
			// Calc total height in pages (easy)
			// maxPageX = (int) (pf.getImageableHeight() / (lineheight * model.getRowCount())) + 1;

		}

		// For now let's only print one page
		if(page > ((maxPageY + 1) * (maxPageX + 1)) - 1)
			return NO_SUCH_PAGE;

		// Now print the table entities
		for(TableEntity e : entities) {
			if(page == (e.pageX * (maxPageY + 1)) + e.pageY) {
				if(e.bold)
					g.setFont(boldFont);

				FontMetrics fm = g.getFontMetrics();
				int ypad = yPadding + fm.getLeading() + fm.getAscent();
				g.drawString(e.text, e.x + xPadding, e.y + ypad);

				if(e.boxed)
					g.drawRect(e.x, e.y, e.width, e.height);

				if(e.bold)
					g.setFont(plainFont);
			}
		}
		
		return PAGE_EXISTS;
	}

	public class TableEntity {
		public String text;
		public boolean bold;
		public boolean boxed;
		public int x;
		public int y;
		public int width;
		public int height;
		public int pageX;
		public int pageY;

		public TableEntity(String text, boolean bold, boolean boxed, int x, int y, int width, int height,
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
	}

}
	
