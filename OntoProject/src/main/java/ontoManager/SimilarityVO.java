/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ontoManager;

import com.hp.hpl.jena.ontology.OntResource;

/**
 *
 * @author nicolasferranti
 */
public class SimilarityVO {
    private OntResource elementA;
    private OntResource elementB;
    private float similarity;

    public SimilarityVO() {
    }

    public SimilarityVO(OntResource elementA, OntResource elementB, float similarity) {
        this.elementA = elementA;
        this.elementB = elementB;
        this.similarity = similarity;
    }
    
    public void setElementA(OntResource elementA){
        this.elementA = elementA;
    }

    public void setElementB(OntResource elementB){
        this.elementB = elementB;
    }

    public void setSimilarity(float similarity){
        this.similarity = similarity;
    }

    public OntResource getElementA(){
        return elementA;
    }

    public OntResource getElementB(){
        return elementB;
    }

    public float getSimilarity(){
        return similarity;
    }

    @Override
    public String toString() {
        return "(" + elementA.getLocalName() + ", " + elementB.getLocalName() + ", " + getSimilarity() + ")";
    }

}
