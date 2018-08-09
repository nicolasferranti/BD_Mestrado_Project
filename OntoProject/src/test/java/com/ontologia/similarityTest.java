/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ontologia;

import PPAHelp.Analyser;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import ontoManager.TabelaSimilaridade;

/**
 *
 * @author nicolas
 */
public class similarityTest {

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
        Analyser analyser = new Analyser("/home/nicolas/Documentos/bench101.xml");
        TabelaSimilaridade ts = analyser.process();
        ts.imprimeTabela(new PrintWriter("/tmp/out.txt"));
    }
}
