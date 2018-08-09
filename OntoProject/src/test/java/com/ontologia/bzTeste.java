/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ontologia;

import Database.Blazegraph;

/**
 *
 * @author nicolasferranti
 */
public class bzTeste {

    public static void main(String[] args) {
        Blazegraph bz = new Blazegraph("Onto1_DB_Nicolas");
        System.out.println(bz.getBrothers("http://oaei.ontologymatching.org/2011/benchmarks/101/onto.rdf#Unpublished"));
//        System.out.println(bz.getCountBrothers("http://www.w3.org/2002/07/owl#Thing"));
//        System.out.println(bz.getCountBrothers("http://oaei.ontologymatching.org/2011/benchmarks/101/onto.rdf#MastersThesis"));
//        System.out.println(bz.getSingleBrother("http://oaei.ontologymatching.org/2011/benchmarks/101/onto.rdf#MastersThesis"));
//        System.out.println(bz.getCountBrothers("http://oaei.ontologymatching.org/2011/benchmarks/101/onto.rdf#Article"));
    }

}
