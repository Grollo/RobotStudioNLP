/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.lth.cs.semantics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

import se.lth.cs.main.PrintToXml;
import se.lth.cs.semparser.corpus.Predicate;
import se.lth.cs.semparser.corpus.Sentence;
import se.lth.cs.semparser.corpus.Word;

/**
 *
 * @author pierre
 */
public class SemanticSubmitter {

    /**
     * @param args the command line arguments
     */
    private String semanticServer =  "vm46.cs.lth.se:8081";
    ;
 
    
    public SemanticSubmitter() {
    }

    public SemanticSubmitter(String semanticServer) {
        this.semanticServer = semanticServer;
    }

    
    /**Example*/
    public static void main(String[] args) {
    	String semanticServer =  "vm46.cs.lth.se:8081"; 
        String[] narrativeSt = {
        		"Put a fixture in front of the robot."
        };// ,	
       // 
        List<String> narrative = Arrays.asList(narrativeSt);
        List<Sentence> parsedSentences = new ArrayList<Sentence>();
        SemanticSubmitter semSubmitter = new SemanticSubmitter();

        for (String sentence : narrative) {
            String parsedOutput = semSubmitter.processSentence(sentence, semanticServer);
            parsedSentences.add(new Sentence(parsedOutput));
        }
        for (Sentence parsedSentence : parsedSentences) {
        	//TODO
        	System.out.println(parsedSentence.toString());
        }
    }

    public String processSentence(String description) {
    	
        return processSentence(description, this.semanticServer);
    }

    public String processSentence(String description, String parserServer) {
        String serverURL = "http://" + parserServer + (parserServer.endsWith("/") ? "" : "/") + "parse";
        String parseOutput;
        try {
            parseOutput = makeHTTPRequest(serverURL, description);
        } catch (IOException e) {
            System.err.println("Failed to connect to SemanticHTTPServer");
            e.printStackTrace();
            return null;
        }
        return parseOutput;

    }

    private PredArgs handlePredicate(Predicate predicate, HashSet<Predicate> handledPreds){
    	
    	
    	
    	return null;
    	
    }
    
    public List<PredArgs> mapPredicates(Sentence sentence) {
//        for (Predicate predicate : sentence.getPredicates()) {
//            if (predicate.getPOS().startsWith("VB")) {
//                System.out.println(predicate.toString());
//            }
//        }
    	HashSet<Predicate> ignorePreds = new HashSet<Predicate>();
        List<PredArgs> actions = new ArrayList<PredArgs>();
        HashSet<Predicate> handledPreds = new HashSet<Predicate>();
        
        Predicate root = null;
        for(Predicate p: sentence.getPredicates()){
        	if(p.getDeprel().equals("ROOT")){
        		root = p;
        		break;
        	}
        }
        if(root!= null) {
        	PredArgs statement = handlePredicate(root, handledPreds);
        	actions.add(statement);
        }
        
        for (Predicate predicate : sentence.getPredicates()) {
        	PredArgs stm = handlePredicate(predicate, handledPreds);
        	if(stm != null){
        		actions.add(stm);
        	}
        }
        return actions;
        
       
    }
  
  
    
    private static PredArgs createArgs(Predicate predicate, HashSet<Predicate> ignore){
    	
        
         return null;
  
    	
    }
  
   

    private String makeHTTPRequest(String url, String sentence) throws IOException {
        byte[] postData = ("sentence=" + URLDecoder.decode(sentence, "UTF-8")).getBytes();
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setFixedLengthStreamingMode(postData.length);
        connection.connect();
        OutputStream os = connection.getOutputStream(); //Do not make multiple calls to getOutputStream with fixed length streaming mode (cf. http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6472250)
        os.write(postData);
        os.close();
        BufferedReader replyReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder ret = new StringBuilder();
        String line;
        while ((line = replyReader.readLine()) != null) {
            ret.append(line).append("\n");
        }
        replyReader.close();
        connection.disconnect();
        return ret.toString().trim();
    }

   
    
    
    private static final Set<String> handledPredicates = new HashSet<String>();

    static {
        handledPredicates.add("insert.01");
        handledPredicates.add("pick.01");
        handledPredicates.add("pick.04");
        handledPredicates.add("place.01");
        handledPredicates.add("put.01");
        handledPredicates.add("take.01");
        handledPredicates.add("hit.02");
        handledPredicates.add("hit.01");
        handledPredicates.add("push.02");
        handledPredicates.add("push.01");
        handledPredicates.add("find.01");
        handledPredicates.add("locate.02");
        handledPredicates.add("destroy.01");
        handledPredicates.add("tilt.01");
        handledPredicates.add("search.01");
        handledPredicates.add("kill.01");
        handledPredicates.add("shove.01");
        handledPredicates.add("calibrate.01");
        handledPredicates.add("keep.04");
        handledPredicates.add("hold.01");
        handledPredicates.add("restart.01");
        handledPredicates.add("detect.01");
        handledPredicates.add("stop.01");
        handledPredicates.add("measure.01");
        handledPredicates.add("move.01");
        handledPredicates.add("appraoch.01");
        handledPredicates.add("retract.01");
        handledPredicates.add("assemble.02");
        handledPredicates.add("use.01");
        
    }
    
    private static final Set<String> ignoredPredicates = new HashSet<String>();

    static {
        handledPredicates.add("do.01");
       
        
        
    }
}
