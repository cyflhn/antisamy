package org.owasp.validator.html.scan;

import java.io.IOException;
import java.io.Writer;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.serialize.HTMLSerializer;
import org.apache.xml.serializer.DOM3Serializer;
import org.apache.xml.serializer.ToHTMLStream;
import org.apache.xml.serializer.dom3.DOM3SerializerImpl;
import org.w3c.dom.DocumentFragment;

/**
 * Created by chuyifan on 2018/9/21.
 */
public class HtmlExtSerializer extends HTMLSerializer {
    private DOM3Serializer serializer = null;
    private Transformer transformer;
    private Writer writer;

    public HtmlExtSerializer(Writer stringWriter) {

        initSerializer(stringWriter);

        // initTransformerFactory();
        this.writer = stringWriter;
    }

    private void initSerializer(Writer stringWriter) {
        ToHTMLStream toHtmlStream = new ToHTMLStream();
        toHtmlStream.setWriter(stringWriter);
        toHtmlStream.setEscaping(false);
        toHtmlStream.setOmitMetaTag(true);
        toHtmlStream.setWriter(stringWriter);
        toHtmlStream.setIndent(false);
        toHtmlStream.setEncoding("utf-8");
        serializer = new DOM3SerializerImpl(toHtmlStream);
    }

    private void initTransformerFactory() {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();// 转换工厂
        try {
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "true");
            transformer.setOutputProperty(OutputKeys.METHOD, "html");
        }
        catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void serialize(DocumentFragment frag) throws IOException {
        serializer.serializeDOM3(frag);
        // transform(frag);
    }

    private void transform(DocumentFragment frag) {
        DOMSource domSource = new DOMSource(frag);
        StreamResult result = new StreamResult(writer);
        try {
            transformer.transform(domSource, result);
        }
        catch (TransformerException e) {
            e.printStackTrace();
        }
    }

}
