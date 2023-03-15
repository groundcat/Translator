package org.fugue;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class TranslatorDeepL {
    private static final String API_URL = "https://api-free.deepl.com/v2/translate";
    private final String apiKey;

    public TranslatorDeepL() {
        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(Paths.get("env.properties"))) {
            properties.load(inputStream);
            apiKey = properties.getProperty("DEEPL_API_KEY");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load API key from env.properties", e);
        }
    }

    public String translate(String originalText, String fromLanguage, String toLanguage) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBodyBuilder = new FormBody.Builder()
                .add("text", originalText)
                .add("source_lang", fromLanguage.toUpperCase())
                .add("target_lang", toLanguage.toUpperCase());

        RequestBody requestBody = formBodyBuilder.build();
        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "DeepL-Auth-Key " + apiKey)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String jsonData = response.body().string();
            System.out.println(jsonData);
            JSONObject jsonResponse = new JSONObject(jsonData);
            return extractTranslation(jsonResponse);
        } catch (IOException e) {
            throw new RuntimeException("Failed to call the DeepL API", e);
        }
    }

    private String extractTranslation(JSONObject jsonResponse) {
        JSONArray translations = jsonResponse.getJSONArray("translations");
        JSONObject firstTranslation = translations.getJSONObject(0);
        return firstTranslation.getString("text");
    }
}
