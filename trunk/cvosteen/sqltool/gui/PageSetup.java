/**
 * PageSetup class stores options for printing across multiple
 * pages.
 */

package cvosteen.sqltool.gui;

public class PageSetup {

	private double zoom = 1.0;
	private boolean fitWidth = false;
	private int fitWidthPages = 1;
	private boolean fitHeight = false;
	private int fitHeightPages = 1;
	
	public PageSetup() { }

	public PageSetup(double zoom) {
		this.zoom = zoom;
	}

	public PageSetup(boolean fitWidth, int fitWidthPages, boolean fitHeight, int fitHeightPages) {
		if(fitWidth)
			setFitWidth(fitWidthPages);
		if(fitHeight)
			setFitHeight(fitHeightPages);
	}


	/**
	 * Explicitly set the zoom for printing this table
	 * The default is 100%
	 */
	public void setZoom(double zoom) {
		this.zoom = zoom;
		fitWidth = false;
		fitWidthPages = 0;
		fitHeight = false;
		fitHeightPages = 0;
	}

	public boolean isZoom() {
		return !fitWidth && !fitHeight;
	}

	public double getZoom() {
		return zoom;
	}

	/**
	 * Sets the number of pages that the table must
	 * fit into width-wise
	 */
	public void setFitWidth(int pages) {
		this.zoom = 1.0;
		fitWidth = true;
		fitWidthPages = pages < 1 ? 1 : pages;
	}

	/**
	 * Returns wether or not to fit to a specific number
	 * of pages wide.
	 */
	public boolean isFitWidth() {
		return fitWidth;
	}

	/**
	 * Returns the number of pages wide to fit to.
	 */
	public int getFitWidthPages() {
		return fitWidthPages;
	}

	/**
	 * Sets the number of pages that the table must
	 * fit into height-wise
	 */
	public void setFitHeight(int pages) {
		this.zoom = 1.0;
		fitHeight = true;
		fitHeightPages = pages < 1 ? 1 : pages;
	}

	/**
	 * Returns wether or not to fit to a specific number
	 * of pages high.
	 */
	public boolean isFitHeight() {
		return fitHeight;
	}

	/**
	 * Returns the number of pages high to fit to.
	 */
	public int getFitHeightPages() {
		return fitHeightPages;
	}
}

