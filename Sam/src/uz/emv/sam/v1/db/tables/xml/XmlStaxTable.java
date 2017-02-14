package uz.emv.sam.v1.db.tables.xml;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.emv.sam.v1.db.tables.Table;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * User: simbre1
 * Date: 11/10/13
 */
public class XmlStaxTable implements Table {

    private final InputStreamProvider inputStreamProvider;

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlStaxTable.class);

    private static final String COLUMN_INFOS_TAG = "column-infos";
    private static final String COLUMN_NAME_TAG = "column-name";
    private static final String ROW_TAG = "r";
    private static final String COLUMN_TAG = "c";
    private static final String TABLE_TAG = "rows";
    private final List<String> columnNames;


    public XmlStaxTable(@NotNull final InputStreamProvider inputStreamProvider) throws IOException, XMLStreamException {
        this.inputStreamProvider = inputStreamProvider;
        this.columnNames = parseColumnNames();
    }

    @NotNull
    private List<String> parseColumnNames() throws IOException, XMLStreamException {
        final InputStream in = inputStreamProvider.getInputStream();
        try {
            final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            final XMLEventReader er = inputFactory.createXMLEventReader(in);

            seekTo(er, COLUMN_INFOS_TAG);
            final List<String> values = getAllColumnsInRow(er, COLUMN_INFOS_TAG, COLUMN_NAME_TAG);
            er.close();

            return values;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    @Override
    @NotNull
    public List<String> getColumns() {
        return columnNames;
    }

    @Override
    public void cleanUp() {

        inputStreamProvider.cleanUp();

    }

    @Override
    public String getVersion() {
        return inputStreamProvider.getVersion();
    }

    public int findColumn(@NotNull final String name) {
        for (int i = 0; i < columnNames.size(); i++) {
            if (name.equals(columnNames.get(i))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    @NotNull
    public Iterator<Map<String, Object>> iterator() {
        try {
            return new RowIterator();
        } catch (Exception e) {
            LOGGER.error("Could not create iterator for table: {}", inputStreamProvider.getName(), e);
            return emptyIterator();
        }
    }

    @NotNull
    private List<String> getAllColumnsInRow(@NotNull final XMLEventReader er,
                                            @NotNull final String rowTag,
                                            @NotNull final String columnTag) throws XMLStreamException {
        final List<String> values = new ArrayList<String>();

        while (er.hasNext()) {
            final XMLEvent e = er.nextEvent();
            if (e.isStartElement() && e.asStartElement().getName().getLocalPart().equalsIgnoreCase(columnTag)) {
                final StringBuilder b = new StringBuilder();
                while (er.hasNext()) {
                    final XMLEvent ee = er.nextEvent();
                    if (ee.isCharacters()) {
                        b.append(ee.asCharacters().getData());
                    } else if (ee.isEndDocument()
                            || (ee.isEndElement() && ee.asEndElement().getName().getLocalPart().equalsIgnoreCase(columnTag))) {
                        break;
                    }
                }

                values.add(b.toString().trim());
            } else if (e.isEndDocument() ||
                    (e.isEndElement() && e.asEndElement().getName().getLocalPart().equalsIgnoreCase(rowTag))) {
                return values;
            }
        }

        return values;
    }


    @Nullable
    private XMLEvent seekTo(@NotNull final XMLEventReader eventReader,
                            @NotNull final String tagName) throws XMLStreamException {
        XMLEvent event = null;
        while (eventReader.hasNext()
                        && !((event = eventReader.nextEvent()).isStartElement()
                        && event.asStartElement().getName().getLocalPart().equalsIgnoreCase(tagName))) {
            // Logic in while statement
        }
        return event;
    }

    @NotNull
    protected Map<String, Object> createRow(@NotNull final List<Object> values) {
        if (getColumns().size() != values.size()) {
            throw new IllegalArgumentException(
                    "Size differs: columns[" + columnNames.size() + "] values[" + values.size() + "]");
        }

        final Map<String, Object> map = new TreeMap<String, Object>();
        Iterator<String> ik = getColumns().iterator();
        Iterator<Object> iv = values.iterator();
        while (ik.hasNext()) {
            map.put(ik.next(), iv.next());
        }

        return map;
    }

    public class RowIterator implements Iterator<Map<String, Object>> {

        private InputStream in = null;
        private XMLEventReader er = null;
        private Map<String, Object> row = null;

        public RowIterator() {
            try {
                this.in = inputStreamProvider.getInputStream();
                this.er = XMLInputFactory.newInstance().createXMLEventReader(in);

                final XMLEvent e = seekTo(er, TABLE_TAG);
                if (e != null) {
                    row = getNextRow();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Nullable
        private Map<String, Object> getNextRow() {
            try {
                final XMLEvent e = er.nextTag();
                if (e.isEndDocument() ||
                        (e.isEndElement() && e.asEndElement().getName().getLocalPart().equalsIgnoreCase(TABLE_TAG))) {
                    return null;
                } else {
                    return createRow(new ArrayList<Object>(getAllColumnsInRow(er, ROW_TAG, COLUMN_TAG)));
                }
            } catch (XMLStreamException ignored) {
            }
            return null;
        }

        @Override
        public boolean hasNext() {
            return row != null;
        }

        @Override
        @NotNull
        public Map<String, Object> next() {
            if (row == null) {
                throw new NoSuchElementException();
            }
            Map<String, Object> r = row;
            row = getNextRow();
            return r;
        }

        @Override
        public void remove() {
        }

        public void close() {
            if (er != null) {
                try {
                    er.close();
                } catch (XMLStreamException ignored) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private static <T> Iterator<T> emptyIterator() {
        return new Iterator<T>() {

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            @Nullable
            public T next() {
                throw new NoSuchElementException();
            }

            @Override
            public void remove() {
            }
        };
    }
}
