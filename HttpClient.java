package com.example.foodspy;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class HttpClient {
    public String getNearbyRestaurants(double latitude, double longitude, String estType) {
        HttpURLConnection con = null ;
        InputStream is = null;
        String API_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%.6f,%.6f&radius=%d&types=%s&sensor=false&key=%s";
        String API_KEY = "AIzaSyCSPX4MUceAV89e6DISWcR3kOziKH1HZ4I";
        try {
            String requestUrl = String.format(API_URL, latitude, longitude, 1000, estType, API_KEY);
            con = (HttpsURLConnection) ( new URL(requestUrl)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();
            StringBuffer buffer = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while (  (line = br.readLine()) != null )buffer.append(line + "\r\n");
            is.close();
            con.disconnect();
            return buffer.toString();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        finally {
            try { is.close(); } catch(Throwable t) {}
            try { con.disconnect(); } catch(Throwable t) {}
        }
        return null;
    }
    public String getRestaurantDetails(String placeID){
        HttpURLConnection con = null ;
        InputStream is = null;
        String API_URL = "https://maps.googleapis.com/maps/api/place/details/json?key=%s&placeid=%s";
        String API_KEY = "AIzaSyCSPX4MUceAV89e6DISWcR3kOziKH1HZ4I";
        try {
            String requestUrl = String.format(API_URL, API_KEY, placeID);
            con = (HttpsURLConnection) ( new URL(requestUrl)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();
            StringBuffer buffer = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while (  (line = br.readLine()) != null )buffer.append(line + "\r\n");
            is.close();
            con.disconnect();
            return buffer.toString();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        finally {
            try { is.close(); } catch(Throwable t) {}
            try { con.disconnect(); } catch(Throwable t) {}
        }
        return null;
    }
}