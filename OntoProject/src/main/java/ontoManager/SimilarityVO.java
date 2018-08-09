/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ontoManager;

import com.hp.hpl.jena.ontology.OntResource;
import java.util.ArrayList;

/**
 *
 * @author nicolasferranti
 */
public class SimilarityVO {

    private OntResource elementA;
    private OntResource elementB;
    private ArrayList<Float> similarity;

    public SimilarityVO() {
        this.similarity = new ArrayList<>();
    }

    public SimilarityVO(OntResource elementA, OntResource elementB, float similarity) {
        this.elementA = elementA;
        this.elementB = elementB;
        this.similarity = new ArrayList<>();
        this.similarity.add(similarity);
    }

    public void setElementA(OntResource elementA) {
        this.elementA = elementA;
    }

    public void setElementB(OntResource elementB) {
        this.elementB = elementB;
    }

    public void addSimilarity(float similarity) {
        this.similarity.add(similarity);
    }

    public OntResource getElementA() {
        return elementA;
    }

    public OntResource getElementB() {
        return elementB;
    }

    public float getSimilarity() {
        float sum = 0;
        for (int i = 0 ; i < this.similarity.size(); i++) {
            sum += this.similarity.get(i);
        }
        return (sum / this.similarity.size());
    }

    @Override
    public String toString() {
        return "(" + elementA.getLocalName() + ", " + elementB.getLocalName() + ", " + getSimilarity() + ")";
    }

}
