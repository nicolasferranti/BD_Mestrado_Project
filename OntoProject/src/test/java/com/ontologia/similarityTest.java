/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ontologia;

import Database.Blazegraph;
import com.ontologia.controller.CurlExec;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import ontoManager.Similaridade;
import ontoManager.TabelaDeSimilaridades;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 *
 * @author nicolas
 */
public class similarityTest {

    public static void executeRules(TabelaDeSimilaridades tbs, String ent1, String ent2, Blazegraph bz1, Blazegraph bz2) {

    }

    public static void singleBrotherRule(TabelaDeSimilaridades tbs, String ent1, String ent2, Blazegraph bz1, Blazegraph bz2, float degree) {
        String bro1 = bz1.getSingleBrother(ent1);
        String bro2 = bz2.getSingleBrother(ent2);
        if (bro1 != null && bro2 != null) {
            tbs.addKnownMatches(bro1, bro2, degree * tbs.getSimilarity(ent1, ent2));
        }

    }

    public static void brothersRule(TabelaDeSimilaridades tbs, String ent1, String ent2, Blazegraph bz1, Blazegraph bz2, float degree) {
        System.out.println(ent1 + "=>" + bz1.getCountBrothers(ent1));
        System.out.println(ent2 + "=>" + bz2.getCountBrothers(ent2));

        if (bz1.getCountBrothers(ent1) > 1 && bz2.getCountBrothers(ent2) > 1) {
            System.out.println("enter bros");
            ArrayList<String> bros1 = bz1.getBrothers(ent1);
            ArrayList<String> bros2 = bz2.getBrothers(ent2);

            float avg = (bz1.getCountBrothers(ent1) + bz2.getCountBrothers(ent2)) / 2;
            System.out.println("level:" + (degree / avg));
            for (String bro1 : bros1) {
                for (String bro2 : bros2) {
                    System.out.println("adding :" + bro1 + "=" + bro2 + "(" + degree / avg + ")");
                    tbs.addKnownMatches(bro1, bro2, degree / avg);
                }
            }

        }
    }

    public static void fatherRule(TabelaDeSimilaridades tbs, String ent1, String ent2, Blazegraph bz1, Blazegraph bz2, float degree) {
        String father1 = bz1.getFather(ent1);
        String father2 = bz2.getFather(ent2);
        if (father1 != null && father2 != null) {
            tbs.addKnownMatches(father1, father2, degree * tbs.getSimilarity(ent1, ent2));
        }
    }

    public static void main(String[] args) throws IOException, Exception {
//        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><similarityCalc>"
//                + "<ontologies>"
//                + "<ontology id=\"/home/nicolas/Documentos/Dissertacao/heuristicontologymatching/TCC-PPOA/xml_2011/101/onto.rdf\"/>"
//                + "<ontology id=\"/home/nicolas/Documentos/Dissertacao/heuristicontologymatching/TCC-PPOA/xml_2011/101/onto.rdf\"/>"
//                + "</ontologies>"
//                + "</similarityCalc>";
//
//        FileWriter fw = new java.io.FileWriter("/tmp/my-file.xml");
//        fw.write(xml);
//        fw.flush();
//        fw.close();

//        Analyser analyser = new Analyser("/tmp/my-file.xml");
//        Analyser analyser = new Analyser("/home/nicolasferranti/Documentos/bench101.xml");
//        TabelaSimilaridade ts = analyser.process();
//        ts.imprimeTabela(new PrintWriter("/tmp/out.txt"));
        Blazegraph bz = new Blazegraph("Onto1_DB_Nicolas");
        Blazegraph bz2 = new Blazegraph("Onto2_DB_Nicolas");

        CurlExec ce = new CurlExec();
        ce.ArmazenarTtl(bz, "/home/nicolasferranti/Documentos/Dissertacao/heuristicontologymatching/TCC-PPOA/xml_2011/101/onturtle.ttl");
        ce.ArmazenarTtl(bz2, "/home/nicolasferranti/Documentos/Dissertacao/heuristicontologymatching/TCC-PPOA/xml_2011/201/onturtle.ttl");

        ArrayList<String> conceptsOnto1 = bz.consultaClasses();
        ArrayList<String> conceptsOnto2 = bz2.consultaClasses();
        System.out.println(conceptsOnto1.size());
        System.out.println(conceptsOnto2.size());

        TabelaDeSimilaridades tbs = new TabelaDeSimilaridades();
        for (int i = 0; i < conceptsOnto1.size(); i++) {
            for (int j = 0; j < conceptsOnto2.size(); j++) {
                tbs.addSimilaridade(new Similaridade(conceptsOnto1.get(i), conceptsOnto2.get(j)));
            }
        }

        //ler refalign
        //FileInputStream f = new FileInputStream("/home/nicolasferranti/Documentos/Dissertacao/heuristicontologymatching/TCC-PPOA/xml_2011/201/refalign_TESTEREGINA.rdf");
        File file = new File("/home/nicolasferranti/Documentos/Dissertacao/heuristicontologymatching/TCC-PPOA/xml_2011/201/refalign_TESTEREGINA.rdf");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(file);
        for (int i = 0; i < document.getElementsByTagName("Cell").getLength(); i++) {
            String ent1 = document.getElementsByTagName("entity1").item(i).getAttributes().item(0).getNodeValue();
            String ent2 = document.getElementsByTagName("entity2").item(i).getAttributes().item(0).getNodeValue();
            String meas = document.getElementsByTagName("measure").item(i).getTextContent();
            //System.out.println(ent1 + ent2 + Float.parseFloat(meas));
            tbs.addKnownMatches(ent1, ent2, Float.parseFloat(meas));
            fatherRule(tbs, ent1, ent2, bz, bz2, (float) 0.9);
            singleBrotherRule(tbs, ent1, ent2, bz, bz2, (float) 0.8);
            brothersRule(tbs, ent1, ent2, bz, bz2, (float) 0.8);

        }

        tbs.printSimilaridades(true);
    }
}
