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

    @GetMapping("/Match")
    public String computeMatch(String onto1, String onto2, String prealign, String onto1ttl, String onto2ttl) throws OWLOntologyCreationException, IOException {
        
        Blazegraph bz = new Blazegraph("Onto1_DB_Nicolas");
        
        CurlExec ce = new CurlExec();
        ce.ArmazenarTtl(bz, "/home/nicolasferranti/Documentos/Dissertacao/heuristicontologymatching/TCC-PPOA/xml_2011/101/onturtle.ttl");
        
        //onto1 = readFile("/home/nicolasferranti/Documentos/Dissertacao/heuristicontologymatching/TCC-PPOA/xml_2011/101/onto.rdf", StandardCharsets.UTF_8);
        //onto1ttl = readFile("/home/nicolasferranti/Documentos/Dissertacao/heuristicontologymatching/TCC-PPOA/xml_2011/101/onturtle.ttl", StandardCharsets.UTF_8);
        //onto2 = readFile("/home/nicolasferranti/Documentos/Dissertacao/heuristicontologymatching/TCC-PPOA/xml_2011/201/onto.rdf", StandardCharsets.UTF_8);

        // tem q arrumar esses dois
        onto2ttl = "";
        //prealign = readFile("/home/nicolasferranti/Documentos/Dissertacao/heuristicontologymatching/TCC-PPOA/xml_2011/benchmark/prealign201.rdf", StandardCharsets.UTF_8);

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
        
        
        return ("hi " + onto1);
    }

    private String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
