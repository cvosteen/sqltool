/**
 * Persists a collection of Databases to, and
 * retrieves a collection of Databases from an .xml file
 */
package cvosteen.sqltool.database;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

public class DatabaseXMLEncoder {

	private String filename = null;

	public DatabaseXMLEncoder() {
	}

	public DatabaseXMLEncoder(String filename) {
		this.filename = filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	/**
	 * Reads this instance's associated filename and recreates a collection
	 * of Databases.
	 */
	public Collection<Database> readXMLFile() throws IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document doc;
		List<Database> databaseList = new ArrayList<Database>();

		// Parse the XML file
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(filename);
		} catch(IOException ie) {
			throw ie;
		} catch(Exception e) {
			throw new IOException("XML file not well formed.");
		}

		// Get the root element <databases>
		Element root = doc.getDocumentElement();

		// Get all of the children nodes <database> tags
		NodeList nl = root.getElementsByTagName("database");

		// Read each database
		for(int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			Database d = readDatabase(e);
			if(d != null)
				databaseList.add(d);

		}

		return databaseList;
	}



	/**
	 * Reads a database by analyizing a <database> element.
	 * The database element should be of the form:
	 * <database name="Name">
	 *   <driver>foo.bar.DriverName</driver>
	 *   <url>foo:bar:connection_string</url>
	 *   <query name="Query One">SELECT * FROM Foo</query>
	 *   <query name="Query Two">SELECT * FROM Bar</query>
	 *   ...
	 * </database>
	 */
	private Database readDatabase(Element elem) {
		String name = elem.getAttribute("name");
		String cls = getTextChild(elem, "driver");
		String conn = getTextChild(elem, "url");

		Database d;
		if(name == null || cls == null || conn == null)
			return null;
		else
			d = new Database(name, cls, conn);

		NodeList nl = elem.getElementsByTagName("query");
		for(int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			String qname = e.getAttribute("name");
			String qsql = e.getFirstChild().getNodeValue();
			d.saveQuery(qname, qsql);
		}
		return d;
	}

	/**
	 * Convenience function to retrieve the text from a child element.
	 */
	private String getTextChild(Element elem, String name) {
		NodeList nl = elem.getElementsByTagName(name);
		if(nl.getLength() == 0)
			return null;

		return nl.item(0).getFirstChild().getNodeValue();
	}

	/**
	 * Writes a collection of Databases to the associated file.
	 */
	public void writeXMLFile(Collection<Database> dl) throws IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document doc;

		try {
			// Create the DOM
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.newDocument();
			Element root = doc.createElement("databases");
			doc.appendChild(root);
			for(Database d : dl) {
				writeDatabase(root, d);
			}

			// Write the file
			DOMSource ds = new DOMSource(doc);
			StreamResult sr = new StreamResult(new File(filename));
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.METHOD, "xml");
			t.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.setOutputProperty(OutputKeys.STANDALONE, "yes");
			t.transform(ds, sr);

		} catch(Exception e) {
			IOException ie = new IOException("Unable to save changes.");
			ie.initCause(e);
			throw ie;
		}
	}

	/**
	 * Constructs a <database> element in the XML DOM.
	 */
	private void writeDatabase(Element elem, Database db) {
				Element e = elem.getOwnerDocument().createElement("database");
				e.setAttribute("name", db.getName());
				setTextChild(e, "driver", db.getDriver());
				setTextChild(e, "url", db.getConnectionUrl());

				for(String query : db.getAllQueries()) {
					setTextChild(e, "query", db.getQuerySql(query));
					((Element) e.getLastChild()).setAttribute("name", query);
				}

				elem.appendChild(e);
	}

	/**
	 * Convenience function to set the text of a child element.
	 */
	private void setTextChild(Element elem, String name, String value) {
		Document doc = elem.getOwnerDocument();
		Element e = doc.createElement(name);
		e.appendChild(doc.createTextNode(value));
		elem.appendChild(e);
	}
}
