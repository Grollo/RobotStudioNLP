/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.lth.cs.main;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.lth.cs.semantics.PredArgs;
import se.lth.cs.semantics.SemanticSubmitter;
import se.lth.cs.semparser.corpus.Predicate;
import se.lth.cs.semparser.corpus.Sentence;
import se.lth.cs.semparser.corpus.Word;

/**
 * 
 * @author pierre
 */
@WebServlet(name = "Submitter", urlPatterns = { "/badaboum" })
public class Submitter extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public List<String> sentenceDetector(String narrative) {
		String[] sentences = narrative.split("\\.");
		return Arrays.asList(sentences);
	}

	public String process(String narrative) throws UnsupportedEncodingException {
		// Here the semantic processing
		narrative = URLDecoder.decode(narrative, "UTF-8");
		narrative = narrative.replaceAll("!", ".");

		List<String> sentences = sentenceDetector(narrative);
		SemanticSubmitter semSubmitter = new SemanticSubmitter();

		List<Sentence> parsedSentences = new ArrayList<Sentence>();
		// SemanticSubmitter semSubmitter = new SemanticSubmitter();

		for (String sentence : sentences) {
			String parsedOutput = semSubmitter.processSentence(sentence);
			parsedSentences.add(new Sentence(parsedOutput));
		}

		for (Sentence parsedSentence : parsedSentences) {
			// sanity check?
		}

		return "";
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, UnsupportedEncodingException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		// The semantic server processes only one parameter: text
		// System.out.println("sfkhfksl");
		// out.println("fdsfksdfhksdfhkjshfjkshdfkjshd");
		String text = request.getParameter("text");
		if (text != null) {
			// out.println(text);

			out.println(process(text));
		} else {
			out.println("No text");
		}
		out.close();
	}
}