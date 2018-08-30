package com.ibm.watsonhealth;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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
            String envName = "prod";
            try (Stream<Path> paths = Files.walk(Paths.get("C:\\Users\\IBM_ADMIN\\workspace\\siam\\dataprobe-install\\src\\main\\resources\\dataprobe\\" + envName + "\\openamSecurityPolicy"))) {
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

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(false);
                factory.setIgnoringComments(true);
                factory.setSchema(null);
                DocumentBuilder builder = factory.newDocumentBuilder();
                builder.setEntityResolver(new NullEntityResolver());
                System.out.println("\nFile Name: " + path);
                File fXmlFile = new File(path);
                Document doc = builder.parse(fXmlFile);
                XPathFactory xPathfactory = XPathFactory.newInstance();
                XPath xpath = xPathfactory.newXPath();
                XPathExpression creationExpr = xpath.compile("//Policies/Policy[@creationdate]");
                XPathExpression lastModExpr = xpath.compile("//Policies/Policy[@lastmodifieddate]");
                XPathExpression policyNameExpr = xpath.compile("//Policies/Policy[@name]");

                NodeList nl = (NodeList) lastModExpr.evaluate(doc, XPathConstants.NODESET);
                NodeList nodeList = (NodeList) policyNameExpr.evaluate(doc, XPathConstants.NODESET);
                NodeList nodeLi = (NodeList) creationExpr.evaluate(doc, XPathConstants.NODESET);


                for (int i = 0; i < nl.getLength(); i++) {
                    Node lastModNode = nl.item(i);
                    Node nameNode = nodeList.item(i);
                    Node createDateNode = nodeLi.item(i);

                    String created = createDateNode.getAttributes().getNamedItem("creationdate").getNodeValue();
                    String key = lastModNode.getAttributes().getNamedItem("lastmodifieddate").getNodeValue();
                    String name = nameNode.getAttributes().getNamedItem("name").getNodeValue();
                    dateTimeNameOutput(created, name, key);
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

/**
    public static void searchTags(String path) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setIgnoringComments(true);
            factory.setSchema(null);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(new NullEntityResolver());
            System.out.println("\nFile Name: " + path);
            File fXmlFile = new File(path);
            Document doc = builder.parse(fXmlFile);
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression creationExpr = xpath.compile("//Policies/Policy[@creationdate]");
            XPathExpression lastModExpr = xpath.compile("//Policies/Policy[@lastmodifieddate]");
            XPathExpression policyNameExpr = xpath.compile("//Policies/Policy[@name]");

            NodeList nl = (NodeList) lastModExpr.evaluate(doc, XPathConstants.NODESET);
            NodeList nodeList = (NodeList) policyNameExpr.evaluate(doc, XPathConstants.NODESET);
            NodeList nodeLi = (NodeList) creationExpr.evaluate(doc, XPathConstants.NODESET);


            for (int i = 0; i < nl.getLength(); i++) {
                Node lastModNode = nl.item(i);
                Node nameNode = nodeList.item(i);
                Node createDateNode = nodeLi.item(i);

                String created = createDateNode.getAttributes().getNamedItem("creationdate").getNodeValue();
                String key = lastModNode.getAttributes().getNamedItem("lastmodifieddate").getNodeValue();
                String name = nameNode.getAttributes().getNamedItem("name").getNodeValue();
                dateTimeNameOutput(created, name, key);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
**/

    private static void dateTimeNameOutput(String creation, String name, String key) {
        Long dateL = Long.valueOf(key);
        Long dateC = Long.valueOf(creation);
        try {
            DateTimeFormatter dtFormat = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
            LocalDateTime date =
                    Instant.ofEpochMilli(dateL).atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime dateLC =
                    Instant.ofEpochMilli(dateC).atZone(ZoneId.systemDefault()).toLocalDateTime();
            System.out.println("Policy Name: " + name + " Created: " + dateLC.format(dtFormat) +
                    " - Last Modified Date: " + date.format(dtFormat));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
