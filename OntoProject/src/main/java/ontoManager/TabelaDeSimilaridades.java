/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ontoManager;

import java.util.ArrayList;

/**
 *
 * @author nicolasferranti
 */
public class TabelaDeSimilaridades {

    private ArrayList<Similaridade> listaConceitos;

    public TabelaDeSimilaridades() {
        listaConceitos = new ArrayList<>();
    }

    public void addSimilaridade(Similaridade sim) {
        listaConceitos.add(sim);
    }

    public float getSimilarity(String entA, String entB) {
        for (Similaridade s : listaConceitos) {
            if (s.getElementA().equals(entA)) {
                if (s.getElementB().equals(entB)) {
                    return s.getSimilarity();
                }
            }
        }
        return 0;
    }

    public void addKnownMatches(String entA, String entB, float measure) {
        for (Similaridade s : listaConceitos) {
            if (s.getElementA().equals(entA)) {
                if (s.getElementB().equals(entB)) {
                    s.addSimilarity(measure);
                    //System.out.println("similaridade encontrada!");
                }
            }
        }
    }

    public void printSimilaridades(boolean onlyDiffFrom0) {
        System.out.println("-----------------------------");
        for (int i = 0; i < listaConceitos.size(); i++) {
            if (onlyDiffFrom0) {
                if (listaConceitos.get(i).getSimilarity() > 0) {
                    System.out.println("conceitoA :" + listaConceitos.get(i).getElementA());
                    System.out.println("conceitoB :" + listaConceitos.get(i).getElementB());
                    System.out.println("similarity:" + listaConceitos.get(i).getSimilarity());
                    System.out.println();
                }
            } else {
                System.out.println("conceitoA :" + listaConceitos.get(i).getElementA());
                System.out.println("conceitoB :" + listaConceitos.get(i).getElementB());
                System.out.println("similarity:" + listaConceitos.get(i).getSimilarity());
                System.out.println();
            }
        }
        System.out.println("-----------------------------");
    }

    public String toXML() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n"
                + "<rdf:RDF xmlns=\"http://knowledgeweb.semanticweb.org/heterogeneity/alignment\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "<Alignment>");

        for (int i = 0; i < listaConceitos.size(); i++) {
            if (listaConceitos.get(i).getSimilarity() > 0) {
                sb.append("<map>\n"
                        + "    <Cell>\n"
                        + "      <entity1 rdf:resource=\"" + listaConceitos.get(i).getElementA() + "\"/>\n"
                        + "      <entity2 rdf:resource=\"" + listaConceitos.get(i).getElementB() + "\"/>\n"
                        + "      <measure rdf:datatype=\"http://www.w3.org/2001/XMLSchema#float\">" + listaConceitos.get(i).getSimilarity() + "</measure>\n"
                        + "      <relation>=</relation>\n"
                        + "    </Cell>\n"
                        + "  </map>");
            }
        }

        sb.append("</Alignment>\n"
                + "</rdf:RDF>");

        return sb.toString();
    }

}
