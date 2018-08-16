package com.ibm.watsonhealth;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PolicyDateSearch {

    public static void main(String[] args) {
        try {
            searchDirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void searchDirs() {
        try {

            List<String> pathList = new ArrayList<String>();
            try (Stream<Path> paths = Files.walk(Paths.get("C:\\Users\\IBM_ADMIN\\workspace\\siam\\dataprobe-install\\src\\main\\resources\\dataprobe\\int\\openamSecurityPolicy"))) {
                pathList = paths
                        .filter(p -> p.toString().endsWith(".policies"))
                        .map(path -> Files.isDirectory(path) ? path.toString() + '/' : path.toString())
                        .collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }
            searchTags(pathList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void searchTags(List<String> pathName) {
        try {
            for (String path : pathName) {
                System.out.println("File Name: " + path);
                File fXmlFile = new File(path);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(false);
                factory.setIgnoringComments(true);
                factory.setSchema(null);
                DocumentBuilder builder = factory.newDocumentBuilder();
                builder.setEntityResolver(new NullEntityResolver());
                Document doc = builder.parse(fXmlFile);
                XPathFactory xPathfactory = XPathFactory.newInstance();
                XPath xpath = xPathfactory.newXPath();
                XPathExpression lastModExpr = xpath.compile("//Policies/Policy[@lastmodifieddate]");
                XPathExpression policyNameExpr = xpath.compile("//Policies/Policy[@name]");

                NodeList nl = (NodeList) lastModExpr.evaluate(doc, XPathConstants.NODESET);
                NodeList nodeList = (NodeList) policyNameExpr.evaluate(doc, XPathConstants.NODESET);


                for (int i = 0; i < nl.getLength(); i++) {
                    Node lastModNode = nl.item(i);
                    Node nameNode = nodeList.item(i);
                    String key = lastModNode.getAttributes().getNamedItem("lastmodifieddate").getNodeValue();
                    String name = nameNode.getAttributes().getNamedItem("name").getNodeValue();
                    dateTimeNameOutput(name, key);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void dateTimeNameOutput(String name, String key) {
        Long dateL = Long.valueOf(key);
        DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("MMM/dd/yyyy");
        LocalDate date =
                Instant.ofEpochMilli(dateL).atZone(ZoneId.systemDefault()).toLocalDate();
        System.out.println("Policy Name: " + name + "  - Last Modified Date: " + date.format(dtFormat));


    }

}
