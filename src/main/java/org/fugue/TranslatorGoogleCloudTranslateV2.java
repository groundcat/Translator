package org.fugue;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class TranslatorGoogleCloudTranslateV2 {
    private static final String API_URL = "https://translation.googleapis.com/language/translate/v2";
    private final String apiKey;

    public TranslatorGoogleCloudTranslateV2() {
        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(Paths.get("env.properties"))) {
            properties.load(inputStream);
            apiKey = properties.getProperty("GOOGLE_API_KEY");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load API key from env.properties", e);
        }
    }

    public String translate(String originalText, String fromLanguage, String toLanguage) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonBody = buildRequestBody(originalText, fromLanguage, toLanguage);
        RequestBody requestBody = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(API_URL + "?key=" + apiKey)
                .header("Content-Type", "application/json")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String jsonData = response.body().string();
            JSONObject jsonResponse = new JSONObject(jsonData);
            return extractTranslation(jsonResponse);
        } catch (IOException e) {
            throw new RuntimeException("Failed to call the Google Cloud Translate API", e);
        }
    }

    private JSONObject buildRequestBody(String originalText, String fromLanguage, String toLanguage) {
        JSONObject requestBody = new JSONObject();
        JSONArray sourceTextArray = new JSONArray().put(originalText);
        requestBody.put("q", sourceTextArray);
        requestBody.put("source", fromLanguage);
        requestBody.put("target", toLanguage);
        return requestBody;
    }

    private String extractTranslation(JSONObject jsonResponse) {
        JSONObject data = jsonResponse.getJSONObject("data");
        JSONArray translations = data.getJSONArray("translations");
        JSONObject firstTranslation = translations.getJSONObject(0);
        return firstTranslation.getString("translatedText");
    }
}