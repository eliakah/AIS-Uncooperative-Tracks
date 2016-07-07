package gathering;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;

/**
 * Created by Research on 6/29/2016.
 */
//TODO: make tide info into JSON
public class Scraper {
    /**
     * main.
     *
     * @param args the input arguments
     * @throws IOException the io exception
     */
    public static void main(String[] args) throws IOException {
        Scraper scraper = new Scraper();
        //scraper.download("http://www.ndbc.noaa.gov/kml/marineobs_as_kml.php?sort=owner","testfile.kml");
        scraper.scrape("http://tidesandcurrents.noaa.gov/stationhome.html?id=9449639");
    }

    /**
     * This method takes an address as an input-
     * and returns the content of the page as a string.
     *
     * @param str the str
     * @return the string
     * @throws IOException the io exception
     */
    public String scrape(String str) throws IOException {
        String content = "";
        String line;
        boolean check;
        // Make a URL to the web page
        URL url = new URL(str);

        // Get the input stream through URL Connection
        URLConnection con = url.openConnection();

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

        ArrayList<String> list = new ArrayList<>();

        // read each line and write to System.out
        while ((line = br.readLine()) != null) {
            content = "";
            check = true;
            if (line.contains("<table")) {
                while (check) {
                    if (!line.contains("</table>")) {
                        content += "\n" + line;
                    } else {
                        content += "\n" + line;
                        check = false;
                    }
                    line = br.readLine();


                }
                content = content.toLowerCase();
                content = content.replace("<table>", "");
                content = content.replace("<table class=\"table table-condensed\">", "");
                content = content.replace("<i class='icon-ok'>", "");
                content = content.replace("</i>", "");
                content = content.replace("<thead>", "");
                content = content.replace("<td class=\"elevvalue\">", "");
                content = content.replace("<tbody>", "");
                content = content.replace("</tbody>", "");
                content = content.replace("<th>", "");
                content = content.replace("</th>", "");
                content = content.replace("</thead>", "");
                content = content.replace("<td>", "");
                content = content.replace("<tr>", "");
                content = content.replace("</table>", "");
                content = content.replace("</td>", "");
                content = content.replace("</tr>", "");

                list.add(content);
            }
        }

        for (int i = 0; i < list.size(); i++) {
            System.out.println("\n no " + i + ": " + list.get(i));
        }

        return content;

    }

    /**
     * This method takes in an address and a filename-
     * then downloads the page and saves that page as a file with the name specified.
     *
     * @param address the address
     * @param fName   the f name
     * @return the boolean
     * @throws IOException the io exception
     */
    boolean download(String address, String fName) throws IOException {

        URL website = new URL(address);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(fName);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        return true;
    }


}
