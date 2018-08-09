/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author nicolasferranti
 */
public class Blazegraph {

    private String dbName;
    private String url;

    public Blazegraph(String DBNAME) {
        //this.url = "http://138.121.71.37:9999";
        this.url = "http://localhost:9999";
        this.dbName = DBNAME;
    }

    public String getDBname() {
        return this.dbName;
    }

    public String getURL() {
        return this.url;
    }

    private JSONArray consultaBZ(String message, String tipo) {
        String url = this.url + "/blazegraph/namespace/" + this.dbName + "/sparql";

        JSONObject jsonObject;

        URL obj;
        try {
            obj = new URL(url);

            HttpURLConnection con;
            try {
                con = (HttpURLConnection) obj.openConnection();
                con.setRequestProperty("Accept", "application/json");
                con.setRequestMethod("POST");

                String urlParameters = tipo + "=" + URLEncoder.encode(message);

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

//                System.out.println("\nSending 'POST' request to URL : " + url);
//                System.out.println("Post parameters : " + urlParameters);
//                System.out.println("Response Code : " + responseCode);
//                System.out.println("Output Message : " + x); // comparar x com o id
                if ("update".equals(tipo)) {
                    return new JSONArray();
                }
                jsonObject = new JSONObject(x.toString());
                JSONArray res = (jsonObject.getJSONObject("results")).getJSONArray("bindings");

                return res;
            } catch (IOException ex) {
                Logger.getLogger(Blazegraph.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Blazegraph.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private ArrayList<String> filtroClasses(ArrayList<String> oldClasses) {
        ArrayList<String> newClasses = new ArrayList<>();
        for (String classi : oldClasses) {
            if (!classi.startsWith("t") || classi.startsWith("http://")) {
                newClasses.add(classi);
            }
        }
        return newClasses;
    }


    /* SELECIONA TODOS OS NÓS DO TIPO CLASSE */
    public ArrayList<String> consultaClasses() {
        String tipo = "query";
        String query = "select distinct ?s { ?s rdf:type owl:Class}";
        JSONArray bzReturn = this.consultaBZ(query, tipo);
        ArrayList<String> classes = new ArrayList<>();
        for (int i = 0; i < bzReturn.length(); i++) {
            classes.add(bzReturn.getJSONObject(i).getJSONObject("s").getString("value"));
        }
        return filtroClasses(classes);
    }

    private String extractLabel(String classURI) {
        String tipo = "query";
        String query = "select ?o { <" + classURI + "> <" + Vocabulario.LABEL + "> ?o }";
        JSONArray response = this.consultaBZ(query, tipo);
        if (response.length() == 0) {
            return null;
        }
        return response.getJSONObject(0).getJSONObject("o").getString("value");
    }

    public String getFather(String classURI) {
        String tipo = "query";
        String query = "select ?o { <" + classURI + "> <" + Vocabulario.SUB_CLASS_OF + "> ?o }";
        JSONArray response = this.consultaBZ(query, tipo);
        if (response.length() == 0) {
            return null;
        }
        return response.getJSONObject(0).getJSONObject("o").getString("value");
    }

    public int getCountBrothers(String classURI) {
        String tipo = "query";
        String query = "select distinct ?o {<" + classURI + "><" + Vocabulario.SUB_CLASS_OF + "> ?pai."
                + " ?o <" + Vocabulario.SUB_CLASS_OF + "> ?pai."
                + " ?o rdf:type owl:Class."
                + " <" + classURI + "> rdf:type owl:Class."
                + " FILTER (?o != <" + classURI + ">)}";
        JSONArray response = this.consultaBZ(query, tipo);
        //System.out.println(response.toString());
        return response.length();
    }

    public String getSingleBrother(String classURI) {
        if (this.getCountBrothers(classURI) == 1) {
            String tipo = "query";
            String query = "select distinct ?o {<" + classURI + "><" + Vocabulario.SUB_CLASS_OF + "> ?pai."
                    + " ?o <" + Vocabulario.SUB_CLASS_OF + "> ?pai."
                    + " ?o rdf:type owl:Class."
                    + " <" + classURI + "> rdf:type owl:Class."
                    + " FILTER (?o != <" + classURI + ">)}";
            System.out.println(query);
            JSONArray response = this.consultaBZ(query, tipo);
            return response.getJSONObject(0).getJSONObject("o").getString("value");
        }
        return null;
    }

    public ArrayList<String> getBrothers(String classURI) {
        if (this.getCountBrothers(classURI) > 0) {
            String tipo = "query";
            String query = "select distinct ?o {<" + classURI + "><" + Vocabulario.SUB_CLASS_OF + "> ?pai."
                    + " ?o <" + Vocabulario.SUB_CLASS_OF + "> ?pai."
                    + " ?o rdf:type owl:Class."
                    + " <" + classURI + "> rdf:type owl:Class."
                    + " FILTER (?o != <" + classURI + ">)}";
            JSONArray response = this.consultaBZ(query, tipo);
            ArrayList<String> brothers = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                brothers.add(response.getJSONObject(i).getJSONObject("o").getString("value"));
            }
            return brothers;
        }
        return null;
    }
}
