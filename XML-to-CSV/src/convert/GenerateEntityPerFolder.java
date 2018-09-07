/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package convert;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.stream.Collectors;
import entity.NamedEntity;
import xml.Token;
import xml.XMLParser;

/**
 * This class contains the main() method of the research package. To run this
 * program a user must input a path to a folder of XML files that were output by
 * Stanford's CoreNLP and a path to a folder to store the CSV output files. The
 * program uses an XML parser to scan each XML file in the inputFolder for
 * Tokens that have a NER value of either PERSON, LOCATION, or ORGANIZATION.
 * Once recognized the tokens are combined to a single entity if applicable,
 * properly formatted, and written to one of three CSV output files. Output
 * files are divided in categories to separate PERSON, LOCATION, and
 * ORGANIZATION.
 *
 * @author Todd Qualiano
 */
public class GenerateEntityPerFolder {

    private final File inputFolder;
    private final List<File> xmlFiles;
    private File outputFolder;
    private File personOut;
    private File locationOut;
    private File organizationOut;
    private FileWriter personWriter;
    private FileWriter locationWriter;
    private FileWriter organizationWriter;

    /**
     * Only constructor so far.
     *
     * @param inputFolder the folder containing the XML output files of the
     * CoreNLP
     * @param outputFolder the folder to contain the CSV output files of this
     * program
     * @throws IOException
     */
    public GenerateEntityPerFolder(String inputFolder, String outputFolder) throws IOException {
        this.inputFolder = new File(inputFolder);
        this.xmlFiles = new LinkedList<>();
        if (!this.inputFolder.isDirectory()) {
            throw new IOException("Input path is not a directory");
        }

        for (File f : this.inputFolder.listFiles()) {
            if (f.getName().endsWith(".xml")) {
                xmlFiles.add(f);
            }
        }

        this.outputFolder = new File(outputFolder);
        if (!this.outputFolder.isDirectory()) {
            throw new IOException("Output path is not a directory");
        }

        this.personOut = new File(outputFolder, "PERSON.csv");
        this.locationOut = new File(outputFolder, "LOCATION.csv");
        this.organizationOut = new File(outputFolder, "ORGANIZATION.csv");

        this.personOut.createNewFile();
        this.locationOut.createNewFile();
        this.organizationOut.createNewFile();

        this.personWriter = new FileWriter(this.personOut);
        this.locationWriter = new FileWriter(this.locationOut);
        this.organizationWriter = new FileWriter(this.organizationOut);

        //The following three lines create a header in each CSV output file. 
        personWriter.write("\"NER\",\"Name\"\n");
        locationWriter.write("\"NER\",\"Name\"\n");
        organizationWriter.write("\"NER\",\"Name\"\n");
    }

    /**
     * This method uses an XMLParser object to scan a file for valid Tokens to
     * become a NamedEntity object and uses the method combineEntityTokens to
     * create the new NamedEntity objects.
     *
     * @param f the file to parse for valid Token objects to become a
     * NamedEntity
     * @throws IOException
     */
    private List<NamedEntity> getEntitiesFromFile(File f) throws IOException {
        XMLParser parse = new XMLParser(f);

        List<Token> fTokens = parse.getTokens();

        //Uses Java 8 stream objects to obtain only the tokens with the specified
        //values for their NER parameter.
        List<Token> namedEntities = fTokens.stream()
                .filter(t -> t.getNer().equals("PERSON")
                || t.getNer().equals("LOCATION")
                || t.getNer().equals("ORGANIZATION"))
                .collect(Collectors.toList());

        return combineEntityTokens(namedEntities);
    }

    /**
     * Writes a List of NamedEntity object to the proper CSV files.
     *
     * @param entities the entities to be written to one of the three CSV output
     * files.
     */
    private void writeToOutput(List<NamedEntity> entities) throws IOException {
        for (NamedEntity ne : entities) {
            String temp = ne.formatAsCSVLine();
            switch (ne.getNER()) {
                case "PERSON":
                    this.personWriter.write(temp);
                    break;
                case "LOCATION":
                    this.locationWriter.write(temp);
                    break;
                case "ORGANIZATION":
                    this.organizationWriter.write(temp);
                    break;
            }
        }
    }

    private void closeOutput() throws IOException {
        personWriter.close();
        locationWriter.close();
        organizationWriter.close();
    }

    /**
     * Combines consecutive Tokens which have the same NER value (if
     * applicable), and creates new NamedEntity objects based on the groupings
     * of the Token objects.
     *
     * @param tokens the tokens to combine should be filtered to have an
     * approved value for the NER parameter (PERSON, LOCATION, or ORGANIZATION)
     * @return the NamedEntity objects that were created.
     */
    private List<NamedEntity> combineEntityTokens(List<Token> tokens) {
        List<NamedEntity> ret = new LinkedList<>();

        ListIterator<Token> it = tokens.listIterator();
        while (it.hasNext()) {
            Token head = it.next();
            String ner = head.getNer();
            String name = combineEntityTokensHelp(head, it);

            NamedEntity temp = new NamedEntity(name, ner);
            ret.add(temp);
            System.out.println(name);
        }
        return ret;
    }

    /*
    * Helper method to above.
    */
    private String combineEntityTokensHelp(Token t, ListIterator<Token> it) {
        if (it.hasNext()) {
            Token next = it.next();
            if (t.getSentID() == next.getSentID()) {
                if (t.getId() == next.getId() - 1) {
                    return t.getWord() + " " + combineEntityTokensHelp(next, it);
                } else {
                    it.previous();
                    return t.getWord();
                }
            } else {
                it.previous();
                return t.getWord();
            }
        }
        return t.getWord();
    }

    public static void main(String[] args) throws IOException {
        String input, inputOut;
        if (args.length != 2) {
            Scanner scan = new Scanner(System.in);
            System.out.println("Enter the absolute path of an XML output file to the CoreNLP: ");
            input = scan.nextLine();
            System.out.println("Enter the absolute path for the output csv files: ");
            inputOut = scan.nextLine();
        } else {
            input = args[0];
            inputOut = args[1];
        }

        GenerateEntityPerFolder converter = new GenerateEntityPerFolder(input, inputOut);

        for (File f : converter.xmlFiles) {
            List<NamedEntity> entities = converter.getEntitiesFromFile(f);
            converter.writeToOutput(entities);
        }
        converter.closeOutput();
    }
}
