import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpStuff {
    // klasa testowa, w ktorej testowana jest autoryzacja w api
    public static void main(String[] args) throws IOException {
        URL url = new URL("http://smieszne-koty.herokuapp.com/oauth/token?grant_type=password&email=olabrela@wp.pl&password=SmiesznyKot");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setReadTimeout(3000);
        connection.setDoOutput(true);
        connection.setDoInput(true);

        BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuffer response = new StringBuffer();
        String inputLine;

        while ((inputLine = input.readLine()) != null) {
            response.append(inputLine);
        }

        input.close();
        System.out.println(response);

        JSONObject autoryzacja = new JSONObject(response.toString());
        String token = autoryzacja.getString("access_token");
        System.out.println("Token: " + token);

        url = new URL("http://smieszne-koty.herokuapp.com/api/kittens" + "?access_token=" + token);
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setReadTimeout(3000);

        input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        response = new StringBuffer();

        while ((inputLine = input.readLine()) != null) {
            response.append(inputLine);
        }

        input.close();
        System.out.println(response);

        JSONArray kotki = new JSONArray(response.toString());
        for (int i = 0; i < kotki.length(); i++) {
            JSONObject kotek = kotki.getJSONObject(i);
            System.out.println("Nazwa kotka: " + kotek.getString("name"));
        }


    }
}