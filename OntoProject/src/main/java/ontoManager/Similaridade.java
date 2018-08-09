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
public class Similaridade {

    private String elementA;
    private String elementB;
    private ArrayList<Float> similarity;
    private boolean simNotInserted;

    public Similaridade() {
        this.similarity = new ArrayList<>();
    }

    public Similaridade(String elementA, String elementB, float similarity) {
        this.elementA = elementA;
        this.elementB = elementB;
        this.similarity = new ArrayList<>();
        this.similarity.add(similarity);
        simNotInserted = false;
    }

    public Similaridade(String elementA, String elementB) {
        this.elementA = elementA;
        this.elementB = elementB;
        this.similarity = new ArrayList<>();
        simNotInserted = true;
    }

    public void setElementA(String elementA) {
        this.elementA = elementA;
    }

    public void setElementB(String elementB) {
        this.elementB = elementB;
    }

    public void addSimilarity(float similarity) {
        this.similarity.add(similarity);
        simNotInserted = false;
    }

    public String getElementA() {
        return elementA;
    }

    public String getElementB() {
        return elementB;
    }

    public float getSimilarity() {
        if (simNotInserted) {
            return 0;
        }
        float sum = 0;
        for (int i = 0; i < this.similarity.size(); i++) {
            sum += this.similarity.get(i);
        }
        return (sum / this.similarity.size());
    }

    @Override
    public String toString() {
        return "(" + elementA + ", " + elementB + ", " + getSimilarity() + ")";
    }
}
