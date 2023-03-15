package org.fugue;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class TranslatorGPT3 {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-3.5-turbo";
    private final String apiKey;

    public TranslatorGPT3() {
        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(Paths.get("env.properties"))) {
            properties.load(inputStream);
            apiKey = properties.getProperty("OPENAI_API_KEY");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load API key from env.properties", e);
        }
    }

    public String translate(String fromLanguage, String toLanguage, String originalText) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonBody = buildRequestBody(fromLanguage, toLanguage, originalText);
        RequestBody requestBody = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(API_URL)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
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
            throw new RuntimeException("Failed to call the OpenAI API", e);
        }
    }

    private JSONObject buildRequestBody(String fromLanguage, String toLanguage, String originalText) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", MODEL);
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "system").put("content", "You are a translator."));
        messages.put(new JSONObject().put("role", "user").put("content", "Translate this text from "+fromLanguage+" to "+toLanguage+" accurately and only provide output: \"" + originalText + "\""));
        requestBody.put("messages", messages);
        return requestBody;
    }

    private String extractTranslation(JSONObject jsonResponse) {
        JSONArray choices = jsonResponse.getJSONArray("choices");
        JSONObject firstChoice = choices.getJSONObject(0);
        JSONObject message = firstChoice.getJSONObject("message");
        String content = message.getString("content");
        if (content.startsWith("\"") && content.endsWith("\"")) { // Remove surrounding double quotes if there is any
            content = content.substring(1, content.length() - 1);
        }
        return content;
    }
}