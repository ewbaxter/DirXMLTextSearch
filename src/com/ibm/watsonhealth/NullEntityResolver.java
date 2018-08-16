package com.ibm.watsonhealth;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

class NullEntityResolver implements EntityResolver {

    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException {
        // Message only for debugging / if you care
    //    System.out.println("I'm asked to resolve: " + publicId + " / " + systemId);
        return new InputSource(new ByteArrayInputStream(new byte[0]));
    }

}
