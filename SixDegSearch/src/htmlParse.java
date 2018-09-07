/*Mary Fatima Menges s1012284*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class htmlParse {
	
	
	ArrayList<String> strs = new ArrayList<String>();
	ArrayList<String> refs = new ArrayList<String>();
	
	public htmlParse(String whatToSearch) throws IOException{

		int index = 0;
		Elements links;

		do{		
			//wget
			String url = "http://localhost:8080/en.wikipedia.org/wiki/Special:Search/" +  whatToSearch.replaceAll(" ", "_") + "?fulltext=y&xowa_page_index=" + index;
			String input = "";
			try {
				input = wget(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			Document doc = Jsoup.parse(input, "UTF-8");
			links = doc.select("a[href][title]:has(strong)"); // a with href and strong
			parseOneHtml(links);
			
			index++;
			
		} while(links.size() != 0);

	}
	
	public ArrayList<String> getStrs(){
		return strs;
	}
	
	public ArrayList<String> getRefs(){
		return refs;
	}
	
	public void parseOneHtml(Elements el){	
		for(int x = 0; x < el.size(); x++){
			strs.add(el.get(x).text());
			refs.add(el.get(x).attr("href"));
		}
		
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
}