/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.lth.cs.semantics;

import se.lth.cs.semparser.corpus.Predicate;
import se.lth.cs.semparser.corpus.Word;

/**
 *
 * @author pierre
 */
public class PredObject {

    Predicate predicate;
    Word word;

    PredObject(Predicate predicate, Word word) {
        this.predicate = predicate;
        this.word = word;
    }

    public String getPredicate() {
        return predicate.getForm();
    }

    public String getWord() {
        return word.getForm();
    }
}
