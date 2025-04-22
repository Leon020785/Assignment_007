import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Main {
    public static void main(String[] args) {
        try {
            // Din rigtige API-nøgle fra OpenRouteService
            String apiKey = "5b3ce3597851110001cf62480a6634c782fb497896e3c42f962a005f";

            // Start- og slutkoordinater (longitude,latitude)
            String start = "12.568337,55.676098"; // København
            String end = "12.080346,55.641519";   // Roskilde

            // Bygger URL uden api_key som parameter
            String urlStr = "https://api.openrouteservice.org/v2/directions/driving-car"
                    + "?start=" + start
                    + "&end=" + end;

            // Opretter HTTP-forbindelse
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", apiKey);
            conn.setRequestProperty("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8");

            // Læser svaret fra API
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            // Udskriver hele svaret (til fejlfinding)
            System.out.println("Svar fra API:");
            System.out.println(response.toString());

            // Parser JSON-svar
            JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();

            // Tjekker om der findes ruter
            if (!json.has("features")) {
                System.out.println("Ingen rute fundet i API-svaret.");
                return;
            }

            JsonObject summary = json
                    .getAsJsonArray("features").get(0).getAsJsonObject()
                    .getAsJsonObject("properties")
                    .getAsJsonArray("segments").get(0).getAsJsonObject()
                    .getAsJsonObject("summary");


            double distanceMeters = summary.get("distance").getAsDouble();
            double durationSeconds = summary.get("duration").getAsDouble();

            double distanceKm = distanceMeters / 1000.0;
            double durationMinutes = durationSeconds / 60.0;

            // Beregner pris (grundpris + pris pr. km)
            double price = 40 + (distanceKm * 10);

            // Udskriver resultat
            System.out.printf("Afstand: %.2f km%n", distanceKm);
            System.out.printf("Tid: %.1f minutter%n", durationMinutes);
            System.out.printf("Pris: %.2f kr%n", price);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
