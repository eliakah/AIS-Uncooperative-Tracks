/**
 * Created by Bob S on 6/29/2016.
 */
import java.net.*;
import java.io.*;
import java.awt.Desktop;
import java.util.ArrayList;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class URLConnDemo {

    private static URL base;
    private static URL url;

    public static void main(String[] args) {

        try {
            base = new URL("http://tidesandcurrents.noaa.gov/");
            url = new URL(base, "stations.html");

            //Local resource to store html file
            File htmlSource = new File(System.getProperty("user.dir") + "\\out\\source.html");

            // Open url connection to arg
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection connection = (HttpURLConnection) urlConnection;

            // Read in html file for local storage
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            //Extract string text from body of html
            String filetext = buildBodyString(in);

            //Store to local source
            writeToFile(filetext, htmlSource);

            /*//Open local resource for parsing
            openHtmlSource(htmlSource);*/

            //Extract each href with ID. NOTE these are all ID's not just the necessary ones
           writeToFile(getUrls(htmlSource), new File(System.getProperty("user.dir") + "\\resources\\station_urls.csv"));

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String buildBodyString(BufferedReader in) {
        String urlString = "";
        try {

            String current;
            while ((current = in.readLine()) != null) {
                urlString += current;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return urlString;
    }

    private static void writeToFile(String text, File f) {
        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(f));
            br.write(text);
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        print("Successfully wrote to, " + f.getPath());
    }

    private static void writeToFile(ArrayList<String> ar, File f) {
        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(f));
            StringBuilder sb = new StringBuilder();
            for (String element : ar) {
                sb.append(element);
                sb.append(",");
            }

            br.write(sb.toString());
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void openHtmlSource(File f) {
        try {
            Desktop.getDesktop().browse(f.toURI());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        print("Successfully opening, " + f.getPath());
    }

    private static void openHtmlURL(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        print("Successfully opening, " + url);
    }

    private static ArrayList<String> getUrls(File f) {
        Document doc = null;
        Elements hrefs = null;
        ArrayList<String> ret = new ArrayList<>();
        try {
            doc = Jsoup.parse(f, "UTF-8");
            hrefs = doc.select("span:matchesOwn(present)");
            for(Element link: hrefs) {
                String href = link.firstElementSibling().attr("href");
                ret.add(new URL(base, href).toString());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        print("Successfully extracted ID's ");
        return ret;
    }

    public static void print(Object o) {
        System.out.println(o.toString());
    }
}