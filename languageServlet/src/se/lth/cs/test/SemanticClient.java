package se.lth.cs.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 *
 * @author pierre
 */
public class SemanticClient {

    /**
     * @param args the command line arguments
     */
    private static final long serialVersionUID = 1L;
    private static String host = "http://localhost:8080";
//    private static String host = "http://vm25.cs.lth.se";


    public static void main(String[] args) throws Exception {
        String narrative = "You should calibrate the pen.";
    

       // System.out.println("HELOOSSS");

        narrative = URLEncoder.encode(narrative, "UTF-8");
        URL semanticServer = new URL(host + "/languageServlet/badaboum?text=" + narrative);
        InputStream is = semanticServer.openStream();
        BufferedReader bReader =
                new BufferedReader(new InputStreamReader(is));
       // System.out.println("HELOsasfasda");
        String line;
        while ((line = bReader.readLine()) != null) {
            System.out.println(URLDecoder.decode(line, "UTF-8"));
        }
      //  System.out.println("HEssssss");

    }
}
