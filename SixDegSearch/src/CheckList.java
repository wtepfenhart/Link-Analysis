/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;

/**
 * Based on Example from
 * http://www.java2s.com/Code/Java/Swing-Components/CheckListExample2.htm
 *
 * @author Mary Fatima Menges
 * @author Todd Qualiano
 */
public class CheckList extends JFrame {

    StringBuilder outputString = new StringBuilder();
    int total = 0;
    ArrayList<String> strs;
    ArrayList<String> refs;
    String[] strsArray;
    String[] refsArray;
    htmlParse searchResults;
    int countScreens = 0;
    MyList list;
    CardLayout cl = new CardLayout();
    JPanel cards = new JPanel(cl);

    // OG constructor public CheckList(String[] options, String outputFile) throws IOException
    public CheckList(
            String[] options,
            String outputFile,
            String httpServerPort,
            String wiki)
            throws IOException {
        super("Choose Wisely");

        JScrollPane sp = new JScrollPane();
        final JTextArea textArea = new JTextArea(3, 10);
        JScrollPane textPanel = new JScrollPane(textArea);
        JButton printButton = new JButton("print");
        JButton nextButton = new JButton("next");

        searchResults = new htmlParse(options[0], httpServerPort, wiki);//what if empty?
        strs = searchResults.getStrs();
        refs = searchResults.getRefs();

        strsArray = new String[strs.size()];
        strsArray = strs.toArray(strsArray);

        refsArray = new String[refs.size()];
        refsArray = refs.toArray(refsArray);

        list = new MyList(strsArray);
        sp.setViewportView(list);

        printButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                writeOutput(outputFile, outputString.toString().replaceAll("\n$", ""));
                textArea.append("Printed " + total + " rows to " + outputFile);
                textArea.append(System.getProperty("line.separator"));
            }
        });

        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ListModel model = list.getModel();
                int count = 0;
                for (int i = 0; i < model.getSize(); i++) {
                    CheckableItem item = (CheckableItem) model.getElementAt(i);
                    if (item.isSelected()) {
                        outputString.append("http://localhost:" + httpServerPort + refsArray[i]);
                        outputString.append(System.getProperty("line.separator"));
                        count++;
                    }
                }
                total = total + count;
                countScreens++;
                if (countScreens < options.length) {
                    try {
                        searchResults = new htmlParse(options[countScreens], httpServerPort, wiki);
                        strs = searchResults.getStrs();
                        refs = searchResults.getRefs();

                        strsArray = new String[strs.size()];
                        strsArray = strs.toArray(strsArray);

                        refsArray = new String[refs.size()];
                        refsArray = refs.toArray(refsArray);

                        list = new MyList(strsArray);

                        if (strs.size() == 0) {
                            nextButton.doClick();
                        } else {
                            sp.setViewportView(list);
                        }

                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    cl.next(cards);
                    sp.setViewportView(new MyList());
                }
            }
        });

        JPanel panel = new JPanel(new GridLayout(2, 1));

        JPanel nextPanel = new JPanel(new GridLayout(1, 1));
        JPanel printPanel = new JPanel(new GridLayout(1, 1));

        nextPanel.add(nextButton);
        printPanel.add(printButton);

        cards.add(nextPanel);
        cards.add(printPanel);
        panel.add(cards);

        panel.add(textPanel);
        getContentPane().add(sp, BorderLayout.CENTER);
        getContentPane().add(panel, BorderLayout.SOUTH);

    }

    private void writeOutput(String fileName, String output) {
        FileWriter fWriter = null;
        BufferedWriter bWriter = null;
        try {
            fWriter = new FileWriter(fileName);
            bWriter = new BufferedWriter(fWriter);
            bWriter.write(output);
            bWriter.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void main(String args[]) {

        boolean goodFile = true;// Indicates whether or not there was a problem with the input file
        String[] tags = {"--file", "--http_server_port", "--output", "--wiki"};
        String inputFilePath = null;
        String outputFilePath = null;
        String httpServerPort = null;
        String wiki = null;
        LinkedList<String> pagesToSearch = new LinkedList<>();

        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                String currentArg = args[i];
                switch (currentArg) {
                    case "--help":
                        System.out.println("This is a test");
                        break;
                    case "--file":
                        inputFilePath = args[i + 1];
                        i++;//because i + 1 is the argument corresponding to the tag, so we don't need to look at it again
                        break;
                    case "--output":
                        outputFilePath = args[i + 1];
                        i++;
                        break;
                    case "--http_server_port":
                        httpServerPort = args[i + 1];
                        i++;
                        break;
                    case "--wiki":
                        wiki = args[i + 1];
                        i++;
                        break;
                    default:
                        System.out.println("INVALID INPUT TEST TQ");

                }
            }
        }
        //if we did not get values for these variables from args set defaults
        Scanner scan1 = new Scanner(System.in);
        if (inputFilePath == null) {
            System.out.println("Enter the location path of the input file.");
            inputFilePath = scan1.nextLine();
        }
        if (outputFilePath == null) {
            System.out.println("Enter the location path of the ouput file.");
            outputFilePath = scan1.nextLine();
        }
        if (httpServerPort == null) {
            httpServerPort = "8080";
        }
        if (wiki == null) {
            wiki = "en.wikipedia.org";
        }
        scan1.close();

        try {
            if (!(new File(outputFilePath).exists())) {
                File f = new File(outputFilePath);
                f.getParentFile().mkdirs();
                f.createNewFile();
                //throw new IOException(outputFilePath + " (No such file or directory)");
            }

            // Create LinkedList of values separated by commas
            Scanner scan2 = new Scanner(new FileInputStream(inputFilePath));
            scan2.useDelimiter(",");
            try {
                while (scan2.hasNext()) {
                    pagesToSearch.add(scan2.next().trim());
                }
            } finally {
                scan2.close();
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            goodFile = false;// There was a problem with the input file
        }

        if (goodFile == true) {
            String[] names = pagesToSearch.toArray(new String[pagesToSearch.size()]);

            CheckList frame;
            try {
                frame = new CheckList(names, outputFilePath, httpServerPort, wiki);
                frame.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                });

                frame.setSize(500, 600);
                frame.setVisible(true);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

}