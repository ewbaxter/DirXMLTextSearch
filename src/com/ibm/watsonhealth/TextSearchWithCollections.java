package com.ibm.watsonhealth;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class TextSearchWithCollections {
    public static void main(String[] args) {
        try {
            addStringsToCollections(args[0], args[1]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void addStringsToCollections(String innerFileName, String policyOuterFile) throws FileNotFoundException {
        File outFile = new File(policyOuterFile);
        final Scanner scanner1 = new Scanner(outFile);
        List<String> searchList = new ArrayList<>();
        List<String> targetList = new ArrayList<>();
        try {
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
            compareTwoLists(targetList, searchList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void compareTwoLists(List<String> agentFile, List<String> policyList) {
        int count = 1;
        int notCount = 1;
        try {
            for (String agentStr : policyList) {
                if (agentFile.contains(agentStr)) {
                    System.out.println( count + " Found " + agentStr + "    ");
                    count++;
                }else{
                    System.out.println(notCount +" ** NOT Found in agent.property file "+ agentStr );
                    notCount++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}