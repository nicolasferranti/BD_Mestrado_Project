/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PPAHelp;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Filter;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.jdom.Document;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author nicolasferranti
 */
public class XMLReader {

    private Collection<List<OntResource>> listOntologies = new ArrayList<List<OntResource>>();

    public XMLReader(String xmlPath) throws ClassNotFoundException, NoSuchFieldException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, JDOMException, IOException, InvocationTargetException{
        FileInputStream f = new FileInputStream(xmlPath);

        //Criamos uma classe SAXBuilder que vai processar o XML4
        SAXBuilder sb = new SAXBuilder();
        //Este documento agora possui toda a estrutura do arquivo.
        Document d = sb.build(f);
        //Recuperamos o elemento root
        Element root = d.getRootElement();
        listOntologies = createOntologies(root);
        //Recuperamos o elemento filho do root que se trata de um Container
        Element containerPrincipal = root.getChild(XMLElements.container_XML);
        //listFunctions = createFunctions(containerPrincipal);
    }

    private Collection<List<OntResource>> createOntologies(Element root) {
        //Strings para guardar o nome e/ou caminho das duas ontologias
        String[] owlNome = new String[2];

        //Listas para guardar as classes que serÃ£o verificadas
        Collection<List<OntResource>> classesOntologias = new ArrayList<List<OntResource>>();
        List<OntResource> elementosOnt1 = new ArrayList<OntResource>();
        List<OntResource> elementosOnt2 = new ArrayList<OntResource>();

        //Recuperamos os elementos filhos (children) da Ontologia
        Element ontologia = root.getChild(XMLElements.ontologies_XML);
        List ontologias = ontologia.getChildren(XMLElements.ontology_XML);
        Iterator i = ontologias.iterator();
        Element element1 = (Element) i.next();
        Element element2 = (Element) i.next();
        owlNome[0] = element1.getAttributeValue("id");
        owlNome[1] = element2.getAttributeValue("id");

        //Cria ontologia sem associar a uma linguagem especifica.
        OntModel m1 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
        OntModel m2 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);

        //Definir se as ontologias associadas devem ou nao ser carregadas, no caso Ã© nÃ£o.
        OntDocumentManager dm1 = m1.getDocumentManager();
        dm1.setProcessImports(false);
        OntDocumentManager dm2 = m2.getDocumentManager();
        dm2.setProcessImports(false);

        //Carrega a ontologia
        //* EDITADO NICOLAS System.out.println("O1 :" +owlNome[0]);
        ///home/nicolasferranti/Documentos/TCC-Nicolas/TCC-PPOA/
        ///home/nicolas/Documentos/Dissertacao/heuristicontologymatching/TCC-PPOA/
        m1.read("file:/" + owlNome[0], null);
        m2.read("file:/" + owlNome[1], null);

        //OntologyCache.setListaIndividuosOnto1(m1.listIndividuals().toList());
        //OntologyCache.setListaIndividuosOnto2(m2.listIndividuals().toList());      
        //Recupera as classes que vamos verificar a semelhanÃ§a.
        List conceitos1 = element1.getChildren(XMLElements.concept_XML);
        preencheLista(m1, conceitos1, elementosOnt1);

        //Recupera as classes que vamos verificar a semelhanÃ§a.
        List conceitos2 = element2.getChildren(XMLElements.concept_XML);
        preencheLista(m2, conceitos2, elementosOnt2);

        classesOntologias.add(elementosOnt1);
        classesOntologias.add(elementosOnt2);

        // Verifica se usará os pre-alinhamentos
        Element prealign = ontologia.getChild(XMLElements.prealign_XML);

        return classesOntologias;
    }

    private void preencheLista(OntModel model, List conceitos, List<OntResource> elementosOnto) {

        ExtendedIterator c1 = model.listClasses().filterDrop(new Filter<OntClass>() {
            public boolean accept(OntClass t) {
                return t.hasURI("http://www.w3.org/2002/07/owl#Thing");
            }
        });

        // Caso o usuário não especificou classes da ontologia
        // Neste caso, serão acrescentadas na lista todas as classes e propriedades da ontologia
        if (conceitos.isEmpty()) {
            preencheLista(c1, model, elementosOnto);
        } // Caso o usuário especificou classes da ontologia
        // TODO permitir que o usuário possa especificar Recursos, ao invés de classes, o que significa que poderia colocar também propriedades no XML.
        else {
            for (ExtendedIterator x = c1; x.hasNext();) {
                OntClass c = (OntClass) x.next();
                for (Iterator j = conceitos.iterator(); j.hasNext();) {
                    Element e = (Element) j.next();
                    if (e.getValue().equals(c.getLocalName())) {
                        elementosOnto.add(c);
                        break;
                    }
                }
            }
        }
    }

    private void preencheLista(ExtendedIterator conceito, OntModel modelo, List<OntResource> lista) {

        // Adiciona classes
        for (ExtendedIterator x = conceito; x.hasNext();) {
            OntClass c = (OntClass) x.next();
            if (modelo.getNsPrefixURI("").equals(c.getNameSpace())) {
                lista.add(c);
            }
        }

        // Adiciona datatype properties
        for (ExtendedIterator<DatatypeProperty> x = modelo.listDatatypeProperties(); x.hasNext();) {
            OntResource resource = x.next();
            if (modelo.getNsPrefixURI("").equals(resource.getNameSpace())) {
                lista.add(resource);
            }
        }

        // Adiciona object properties
        for (ExtendedIterator<ObjectProperty> x = modelo.listObjectProperties(); x.hasNext();) {
            OntResource resource = x.next();
            if (modelo.getNsPrefixURI("").equals(resource.getNameSpace())) {
                lista.add(resource);
            }
        }
    }
    
    public Collection<List<OntResource>> getOntology() {
        return listOntologies;
    }

}
