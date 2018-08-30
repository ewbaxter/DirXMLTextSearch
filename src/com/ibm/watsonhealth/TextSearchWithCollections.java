package com.ibm.watsonhealth;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.File;

import java.io.FileNotFoundException;
import java.util.*;


public class TextSearchWithCollections {
    public static void main(String[] args) {
        addStringsToCollections(args[0], args[1]);
    }

    private static void addStringsToCollections(String innerFileName, String policyOuterFile) {
        try {
            HashSet<String> searchList = new HashSet<>() ;
            List<String> targetList;
            if(!innerFileName.endsWith("properties")){
                System.out.println("Agent File is not a property file");
                System.exit(-1);
            }
            if (policyOuterFile.endsWith("xml")) {
                searchList = addXMLTagsOuterList(policyOuterFile);
            } else {
                File outFile = new File(policyOuterFile);
                final Scanner scanner1 = new Scanner(outFile);
                while (scanner1.hasNextLine()) {
                    final String lineFromPolicyFile = scanner1.nextLine();
                    String searchString = lineFromPolicyFile.substring(lineFromPolicyFile.lastIndexOf("://") + 3);
                    String finalSearchStr;
                    if (searchString.contains("/")) {
                        finalSearchStr = searchString.substring(0, searchString.indexOf("/"));
                    } else {
                        finalSearchStr = searchString;
                    }
                    searchList.add(finalSearchStr);
                }
            }
                targetList = innerFileList(innerFileName);
                compareTwoLists(targetList, searchList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<String> innerFileList(String innerFileName) throws FileNotFoundException {
        List<String> targetList = new ArrayList<>();
        File inFile = new File(innerFileName);
        final Scanner scanner = new Scanner(inFile);
        String finalStr;
        while (scanner.hasNextLine()) {
            final String lineFromFile = scanner.nextLine();
            if (!lineFromFile.startsWith("#") && (lineFromFile.contains("url"))) {
                String searchString = lineFromFile.substring(lineFromFile.indexOf("://") + 3);
                if (searchString.contains("/")) {
                    finalStr = searchString.substring(0, searchString.indexOf("/"));
                } else {
                    finalStr = searchString;
                }
                targetList.add(finalStr);
            }
        }
        return targetList;
    }

    private static HashSet<String> addXMLTagsOuterList(String policyOuterFile) {
        HashSet<String> searchList = new HashSet<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setIgnoringComments(true);
            factory.setSchema(null);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(new NullEntityResolver());
            System.out.println("\nFile Name: " + policyOuterFile);
            File fXmlFile = new File(policyOuterFile);
            Document doc = builder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression resourceName = xpath.compile("/Policies/Policy/Rule/ResourceName[@name]");

            NodeList nl = (NodeList) resourceName.evaluate(doc, XPathConstants.NODESET);

            for (int i = 0; i < nl.getLength(); i++) {
                Node resourceNameNode = nl.item(i);

                String key = resourceNameNode.getAttributes().getNamedItem("name").getNodeValue();

                String searchString = key.substring(key.lastIndexOf("://") + 3);
                String finalSearchStr;
                if (searchString.contains("/")) {
                    finalSearchStr = searchString.substring(0, searchString.indexOf("/"));
                } else {
                    finalSearchStr = searchString;
                }
                searchList.add(finalSearchStr);
//                System.out.println("this is the value: " +key);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return searchList;
    }

    private static void compareTwoLists(List<String> agentFile, HashSet<String> policyList) {
        int count = 1;
        int notCount = 1;
        try {
            for (String agentStr : policyList) {
                if (agentFile.contains(agentStr)) {
                    System.out.println(count + " Found " + agentStr + "    ");
                    count++;
                } else {
                    System.out.println(notCount + " ** NOT Found in agent.property file " + agentStr);
                    notCount++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}