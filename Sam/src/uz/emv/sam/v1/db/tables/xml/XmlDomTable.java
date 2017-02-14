package uz.emv.sam.v1.db.tables.xml;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import uz.emv.sam.v1.db.tables.Table;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;

/**
 * User: simbre1
 * Date: 27/09/13
 */
public class XmlDomTable implements Table {
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlDomTable.class);
    private static final String COLUMN_NAME_TAG = "column-name";
    private static final String ROW_TAG = "r";

    private final Node xmlDocument;

    private final List<String> columnNames;
    private final List<Map<String, Object>> table;
    private final String version;

    public XmlDomTable(@NotNull final File xmlFile) throws FileNotFoundException {
        final InputStream inputStream = new BufferedInputStream(new FileInputStream(xmlFile));
        version = parseVersion(xmlFile);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document = null;
        try {
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            document = docBuilder.parse(new InputSource(inputStream));
        } catch (ParserConfigurationException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (SAXException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        xmlDocument = document;
        columnNames = new ArrayList<String>();
        table = new ArrayList<Map<String, Object>>();

        parseTableHeader();
        parseTableBody();
    }

    private void parseTableHeader(){
        final List<Node> nodes = getElementsByTagName(xmlDocument, COLUMN_NAME_TAG);
        for (final Node node : nodes) {
            columnNames.add(node.getTextContent());
        }
    }

    public static List<Node> getElementsByTagName(Node node, String tagname) {
        List<Node> result = new ArrayList<Node>();
        result = getElementsByTagName(node, tagname, result);
        return result;
    }

    private static List<Node> getElementsByTagName(Node node, String tagname, List<Node> list) {
        if (tagname.equals(node.getNodeName())) {
            list.add(node);
        }
        NodeList children = node.getChildNodes();
        if (children != null) {
            for (int i = 0; i < children.getLength(); i++) {
                list = getElementsByTagName(children.item(i), tagname, list);
            }
        }
        return list;
    }

    private void parseTableBody(){
        final List<Node> rs = getElementsByTagName(xmlDocument, ROW_TAG);
        for (Node r : rs) {
            final NodeList cs = r.getChildNodes();
            final Map<String, Object> row = new TreeMap<String, Object>();
            for (int j = 0; j < cs.getLength(); ++j) {
                row.put(columnNames.get(j), cs.item(j).getTextContent());
            }

            table.add(row);
        }
    }

    @NotNull
    public List<String> getColumnNames(){
        return columnNames;
    }

    public int findColumn(@NotNull final String name) {
        for(int i=0; i<columnNames.size(); ++i){
            if(columnNames.get(i).equals(name)){
                return i;
            }
        }
        return -1;
    }

    @Override
    @NotNull
    public List<String> getColumns() {
        return columnNames;
    }

    @Override
    public void cleanUp(){
        table.clear();
    }

    @Override
    public String getVersion() {
        return version;
    }

    @NotNull
    private String parseVersion(final File file) {
        int start = file.getName().indexOf("#");
        int stop = file.getName().indexOf("_", start);
        return file.getName().substring(start + 1, stop);
    }

    @Override
    @NotNull
    public Iterator<Map<String, Object>> iterator() {
        return table.iterator();
    }
}
