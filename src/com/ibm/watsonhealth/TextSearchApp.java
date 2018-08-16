package com.ibm.watsonhealth;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TextSearchApp {

    public static void main(String[] args) {
        try {
            searchForName(args[0], args[1]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void searchForName(String innerFileName, String policyOuterFile) throws FileNotFoundException {

        File outFile = new File(policyOuterFile);
        final Scanner scanner1 = new Scanner(outFile);
        int count = 1;
        try {
            while (scanner1.hasNextLine()) {
                final String lineFromPolicyFile = scanner1.nextLine();
                String searchString = lineFromPolicyFile.substring(lineFromPolicyFile.lastIndexOf("://"));
                String finalSearchStr = searchString.substring(0, searchString.lastIndexOf("/"));
                File inFile = new File(innerFileName);
                final Scanner scanner = new Scanner(inFile);
                while (scanner.hasNextLine()) {
                    final String lineFromFile = scanner.nextLine();
                    if (lineFromFile.contains(finalSearchStr)) {
                        // a match!
                        System.out.println(+count + " " + lineFromPolicyFile + "    " + lineFromFile + " ");
                        count++;
                    }
                    else{
                        System.out.println("*****  NOT FOUND **** "+finalSearchStr);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
