
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class Scraper_10_11_2018 {

    private String url;
    private Document doc;

    public Scraper_10_11_2018(String u) {
        url = u;
        String input = "";
        try {
            input = wget(u);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        doc = Jsoup.parse(input, "UTF-8");
    }

    public Scraper_10_11_2018() {
        url = null;
        doc = null;
    }

    public void usePageSource(String pageSource) {
        url = null;
        doc = Jsoup.parse(pageSource);
    }

    public Elements getElementsOfType(String type) {
        Elements links = doc.select(type);
        return links;
    }

    public void removeElementsByType(String type) {
        doc.select(type).remove();
    }

    private String getHeader() {
        return doc.getElementById("firstHeading").text();
    }

    public void removeElementsByClass(String type) {
        doc.getElementsByClass(type).remove();
    }

    public void removeElementById(String type) {
        doc.getElementById(type).remove();
    }

    public void replaceTagName(String type1, String type2) {
        Elements elements = doc.select(type1);
        elements.tagName(type2);
    }

    public Element getBody() {

        Element body = doc.body();
        return body;
    }

    public void getInfoBox() {
        Elements infoBox = doc.getElementsByClass("infobox vcard");
        //Elements infoBoxBody = doc.getAllElements();
        Element infoBoxBody = infoBox.get(0);
        Elements myTest = infoBoxBody.getAllElements();
        
        for(Element e : myTest){
            System.out.println(e.text());
        }
    }

    public void traditionalScraper() {

    }

    public static void main(String[] args) throws IOException {
        String inputFilePath = null;
        String outputFolderPath = null;

        if (args.length == 2) {
            // Two arguments were provided
            inputFilePath = args[0];
            outputFolderPath = args[1];
        } else {
            // Get file locations as input
            Scanner scan1 = new Scanner(System.in);
            System.out.println("Enter the location path of the input file.");
            inputFilePath = scan1.nextLine();
            System.out.println("Enter the location path of the ouput folder.");
            outputFolderPath = scan1.nextLine();
            scan1.close();
        }

        String listFolderName = outputFolderPath + "/Lists";
        File listFolder = new File(listFolderName);
        if (!listFolder.exists()) {
            listFolder.mkdirs();
        }

        PrintWriter out = null;

        BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
        String line;

        while ((line = reader.readLine()) != null) {
            int pNumber = 0;

            try {
                Scraper_10_11_2018 scraper = new Scraper_10_11_2018(line);
                scraper.getInfoBox();
                String head = scraper.getHeader();// get the name of the person
                scraper.removeElementsByType("sup");// remove references
                scraper.removeElementsByType("table");// remove table
                scraper.removeElementsByType("div.refbegin.columns.references-column-width");
                scraper.removeElementsByType("cite");// remove cite
                scraper.removeElementsByClass("toc");// remove toc
                scraper.removeElementsByClass("mw-editsection");
                scraper.replaceTagName("h1", "p");// replace <h1> tag with <p>
                scraper.replaceTagName("h2", "p");// replace <h2> tag with <p>
                scraper.replaceTagName("h3", "p");// replace <h3> tag with <p>
                // scraper.replaceTagName("li", "p");// replace <ul> tag with <p>
                // scraper.removeElementsByType("ul>li:has(a.external.text)");
                // scraper.removeElementById("mw-panel");// remove panel
                // scraper.removeElementById("mw-head");// remove head
                // scraper.removeElementById("mw-navigation");// remove
                // navigation header
                // scraper.removeElementById("catlinks");
                // scraper.removeElementById("footer");
                Elements paragraphs = scraper.getElementsOfType("p");
                String textFolderName = outputFolderPath + "/Paragraphs/" + head.replaceAll(" ", "_") + "/text_paragraphs";
                new File(textFolderName).mkdirs();//create folder for each person
                String xmlFolderName = outputFolderPath + "/Paragraphs/" + head.replaceAll(" ", "_") + "/xml_paragraphs";
                new File(xmlFolderName).mkdirs();//create folder for each person
                for (Element p : paragraphs) {
                    if (p.text().equals("See also")) {
                        break;
                    }
                    String filePath = outputFolderPath + "/Paragraphs/" + head.replaceAll(" ", "_") + "/text_paragraphs" + "/" + head.replaceAll(" ", "_") + "-P" + ++pNumber + ".txt";
                    out = new PrintWriter(filePath);
                    out.println(p.text());
                    out.flush();
                    out.close();
                }

            } catch (IOException e) {
                System.err.println("Caught IOException: " + e.getMessage());
            }

        }
        reader.close();
    }

    public static String wget(String url) throws MalformedURLException, IOException {

        URL u = new URL(url);
        HttpURLConnection urlConn = (HttpURLConnection) u.openConnection();
        String line = null;
        StringBuilder tmp = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
        while ((line = in.readLine()) != null) {
            tmp.append(line);
        }
        return tmp.toString();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
