package com.ontologia.controller;

import Database.Blazegraph;
import java.io.File;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.util.Version;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.semanticweb.owlapi.model.*;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import ontoManager.Similaridade;
import ontoManager.TabelaDeSimilaridades;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

@RestController
public class Controller {

    @GetMapping("/oi")
    public void main() throws OWLOntologyCreationException {
        File file = new File("/home/nicolasferranti/Documentos/Trabalhos/BD - MEstrado/onto101-PropChain.owl");

        String uri = "http://oaei.ontologymatching.org/2011/benchmarks/101/onto.rdf#";

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
        OWLReasonerFactory reasonerFactory = PelletReasonerFactory.getInstance();

        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology, new SimpleConfiguration());
        Version v = reasoner.getReasonerVersion();
        System.out.println("reasoner " + reasoner.getReasonerName() + " " + v.getMajor() + "." + v.getMinor() + "." + v.getPatch() + " build " + v.getBuild());

        OWLDataFactory factory = manager.getOWLDataFactory();

        OWLNamedIndividual ind = factory.getOWLNamedIndividual(IRI.create("http://oaei.ontologymatching.org/2011/benchmarks/101/onto.rdf#a64263824"));

        System.out.println(ind.toString());

        //get values of selected properties on the individual 
        OWLObjectProperty p = factory.getOWLObjectProperty(IRI.create("http://oaei.ontologymatching.org/2011/benchmarks/101/onto.rdf#proceedings"));

        for (Node<OWLNamedIndividual> im : reasoner.getObjectPropertyValues(ind, p)) {
            System.out.println(im.toString());
        }

    }

    @GetMapping("/hasToBeConsidered")
    public void hasToBeConsidered() throws OWLOntologyCreationException {
        File file = new File("C:/Users/andli/Desktop/Both.owl");

        String uri = "http://www.semanticweb.org/felipe/ontologies/2018/6/untitled-ontology-39#";
        String rs = "http://www.semanticweb.org/felipe/ontologies/2018/6/untitled-ontology-23#";
        String saude = "http://www.semanticweb.org/felipe/ontologies/2018/6/untitled-ontology-29#";

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
        OWLReasonerFactory reasonerFactory = PelletReasonerFactory.getInstance();

        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology, new SimpleConfiguration());
        Version v = reasoner.getReasonerVersion();
        //System.out.println("reasoner " + reasoner.getReasonerName() + " " + v.getMajor() + "." + v.getMinor() + "." + v.getPatch() + " build " + v.getBuild());

        OWLDataFactory factory = manager.getOWLDataFactory();

        OWLNamedIndividual ind = factory.getOWLNamedIndividual(IRI.create(uri + "recomendaçãoMedicamento"));

        System.out.println(ind.toString());

        //get values of selected properties on the individual 
        OWLObjectProperty p = factory.getOWLObjectProperty(IRI.create(rs + "providesInformationTo"));

        for (Node<OWLNamedIndividual> im : reasoner.getObjectPropertyValues(ind, p)) {
            System.out.println("Recebe: " + im.toString());
        }

    }

    public static void executeRules(TabelaDeSimilaridades tbs, String ent1, String ent2, Blazegraph bz1, Blazegraph bz2) {
        fatherRule(tbs, ent1, ent2, bz1, bz2, (float) 0.9);
        singleBrotherRule(tbs, ent1, ent2, bz1, bz2, (float) 0.8);
        brothersRule(tbs, ent1, ent2, bz1, bz2, (float) 0.8);
    }

    private static void singleBrotherRule(TabelaDeSimilaridades tbs, String ent1, String ent2, Blazegraph bz1, Blazegraph bz2, float degree) {
        String bro1 = bz1.getSingleBrother(ent1);
        String bro2 = bz2.getSingleBrother(ent2);
        if (bro1 != null && bro2 != null) {
            tbs.addKnownMatches(bro1, bro2, degree * tbs.getSimilarity(ent1, ent2));
        }

    }

    private static void brothersRule(TabelaDeSimilaridades tbs, String ent1, String ent2, Blazegraph bz1, Blazegraph bz2, float degree) {
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

    private static void fatherRule(TabelaDeSimilaridades tbs, String ent1, String ent2, Blazegraph bz1, Blazegraph bz2, float degree) {
        String father1 = bz1.getFather(ent1);
        String father2 = bz2.getFather(ent2);
        if (father1 != null && father2 != null) {
            tbs.addKnownMatches(father1, father2, degree * tbs.getSimilarity(ent1, ent2));
        }
    }

    @GetMapping("/Match")
    public String computeMatch(String prealign, String onto1ttl, String onto2ttl) throws OWLOntologyCreationException, IOException, ParserConfigurationException, SAXException {
        //create temp files
        /*
        String pathOnto1 = "/tmp/" + onto1 + System.nanoTime() + ".owl";
        PrintWriter writer = new PrintWriter(pathOnto1, "UTF-8");
        writer.println(onto1);
        writer.close();

        //read file and load ontology
        File file = new File(pathOnto1);
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
         */

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
            executeRules(tbs, ent1, ent2, bz, bz2);
        }

        tbs.printSimilaridades(true);

        //onto1 = readFile("/home/nicolasferranti/Documentos/Dissertacao/heuristicontologymatching/TCC-PPOA/xml_2011/101/onto.rdf", StandardCharsets.UTF_8);
        //onto1ttl = readFile("/home/nicolasferranti/Documentos/Dissertacao/heuristicontologymatching/TCC-PPOA/xml_2011/101/onturtle.ttl", StandardCharsets.UTF_8);
        //onto2 = readFile("/home/nicolasferranti/Documentos/Dissertacao/heuristicontologymatching/TCC-PPOA/xml_2011/201/onto.rdf", StandardCharsets.UTF_8);
        
        return (tbs.toXML());
    }

    private String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
