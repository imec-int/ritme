//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package uz.ehealth.ritme.addressbook;


import org.apache.log4j.Logger;
import uz.ehealth.ritme.be.fgov.ehealth.addressbook.protocol.v1.SearchOrganizationsRequestType;

import javax.xml.bind.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class JaxContextCentralizer {
    private static final Logger LOG = Logger.getLogger(JaxContextCentralizer.class);
    private static JaxContextCentralizer instance = new JaxContextCentralizer();
    private Map<Class<?>, JAXBContext> contextStore;
    private XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

    private JaxContextCentralizer() {
        if(this.contextStore == null) {
            this.contextStore = new HashMap();

            try {
                this.getContext(SearchOrganizationsRequestType.class);
            } catch (Exception var2) {
                LOG.warn("Can not cache SearchOrganizationsRequestType JaxbContext", var2);
            }
        }

    }

    public static JaxContextCentralizer getInstance() {
        return instance;
    }

    public synchronized void addContext(Class<?> clazz) {
        try {
            this.getContext(clazz);
        } catch (Exception var3) {
            LOG.warn("Jaxb context not cached : " + clazz, var3);
        }

    }

    public JAXBContext getContext(Class<?> clazz) throws Exception {
        if(!this.contextStore.containsKey(clazz)) {
            try {
                this.contextStore.put(clazz, JAXBContext.newInstance(clazz));
            } catch (JAXBException var4) {
                String message = this.processJAXBException(var4);
                throw new Exception(message,var4);
            }

            LOG.info("Jaxbcontext for " + clazz + " cached");
        }

        return this.contextStore.get(clazz);
    }

    public Unmarshaller getUnmarshaller(Class<?> clazz) throws Exception {
        try {
            return this.getContext(clazz).createUnmarshaller();
        } catch (JAXBException var4) {
            LOG.error("", var4);
            String message = this.processJAXBException(var4);
            throw new Exception(message,var4);
        }
    }

    public Marshaller getMarshaller(Class<?> clazz) throws Exception {
        try {
            return this.getContext(clazz).createMarshaller();
        } catch (JAXBException var4) {
            LOG.error("", var4);
            String message = this.processJAXBException(var4);
            throw new Exception(message, var4);
        }
    }

    public <X> X toObject(Class<X> clazz, String data) throws Exception {
        try {
            return this.toObject(clazz, data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException var4) {
            throw new Exception(var4.getMessage(),var4);
        }
    }

    public <X> X toObject(Class<X> clazz, byte[] data) throws Exception {
        try {
            ByteArrayInputStream e = new ByteArrayInputStream(data);
            Object message1;
            if(clazz.getAnnotation(XmlRootElement.class) != null) {
                message1 = this.getUnmarshaller(clazz).unmarshal(e);
            } else {
                try {
                    JAXBElement es = this.getUnmarshaller(clazz).unmarshal(this.xmlInputFactory.createXMLStreamReader(e, "UTF-8"), clazz);
                    message1 = es.getValue();
                } catch (XMLStreamException var6) {
                    LOG.error("Incorrect xml : " + var6);
                    throw new Exception(var6.getMessage(),var6);
                }
            }

            return (X)message1;
        } catch (JAXBException var7) {
            LOG.error("", var7);
            String message = this.processJAXBException(var7);
            throw new Exception(message, var7);
        }
    }

    private String processJAXBException(JAXBException e) {
        String message = null;
        if(e.getLinkedException() != null) {
            message = e.getLinkedException().getMessage();
        } else {
            message = e.getLocalizedMessage();
        }

        return message;
    }

    public String toXml(Class<?> clazz, Object obj) throws Exception {
        StringWriter sw = new StringWriter();
        Marshaller marshaller = this.getMarshaller(clazz);

        try {
            marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
            if(clazz.getAnnotation(XmlRootElement.class) != null) {
                marshaller.marshal(obj, sw);
            } else {
                JAXB.marshal(obj, sw);
            }
        } catch (JAXBException var7) {
            LOG.error("", var7);
            String message = this.processJAXBException(var7);
            throw new Exception(message, var7);
        }

        return sw.toString();
    }
}
