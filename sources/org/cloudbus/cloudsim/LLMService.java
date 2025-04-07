package org.cloudbus.cloudsim;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class LLMService {

    private static final String ENDPOINT = "http://localhost:11434/api/generate";

    public static String getLLMFeedback(String simulationOutput) {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("model", "mistral");
            json.addProperty("prompt",
                    "You are a cloud computing expert. Analyze the following CloudSim output and provide performance and optimization feedback:\n\n"
                            + simulationOutput);

            URL url = new URL(ENDPOINT);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes());
            os.flush();
            os.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder fullResponse = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                try {
                    JsonReader reader = new JsonReader(new StringReader(line));
                    reader.setLenient(true);
                    JsonObject part = JsonParser.parseReader(reader).getAsJsonObject();
                    if (part.has("response")) {
                        fullResponse.append(part.get("response").getAsString());
                    }
                } catch (Exception ignore) {
                    // skip malformed lines
                }
            }

            in.close();
            conn.disconnect();

            return fullResponse.toString().trim();

        } catch (Exception e) {
            return "⚠️ Error contacting LLM: " + e.getMessage();
        }
    }

}
