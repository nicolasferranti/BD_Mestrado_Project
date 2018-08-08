/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ontologia.controller;

import Database.Blazegraph;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author nicolasferranti
 */
public class CurlExec {

    //curl -X POST http://localhost:9999/blazegraph/namespace/Onto1_DB_Nicolas/sparql --data-urlencode 'update=DROP ALL; LOAD <file:///home/nicolasferranti/Documentos/Dissertacao/heuristicontologymatching/TCC-PPOA/xml_2011/101/onturtle.ttl>;'
    public void ArmazenarTtl(Blazegraph bz, String pathToTtl) throws IOException {
        //Runtime.getRuntime().exec("curl -X POST http://localhost:9999/blazegraph/namespace/Onto1_DB_Nicolas/sparql --data-urlencode 'update=DROP ALL; LOAD <file:///home/nicolasferranti/Documentos/Dissertacao/heuristicontologymatching/TCC-PPOA/xml_2011/101/onturtle.ttl>;'");

        String url = "http://localhost:9999/blazegraph/namespace/" + bz.getDBname() + "/sparql";

        URL obj;
        String message = "DROP ALL; LOAD <file://" + pathToTtl + ">;";
        try {
            obj = new URL(url);

            HttpURLConnection con;
            try {
                con = (HttpURLConnection) obj.openConnection();
                con.setRequestProperty("Accept", "application/json");
                con.setRequestMethod("POST");

                String urlParameters = "update=" + URLEncoder.encode(message);

                // Send post request
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();

                BufferedReader br = new BufferedReader(new InputStreamReader((con.getInputStream())));
                StringBuilder x = new StringBuilder();

                String output;
                while ((output = br.readLine()) != null) {
                    x.append(output);

                }

                System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Post parameters : " + urlParameters);
                System.out.println("Response Code : " + responseCode);
                System.out.println("Output Message : " + x); // comparar x com o id

            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        } catch (MalformedURLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
