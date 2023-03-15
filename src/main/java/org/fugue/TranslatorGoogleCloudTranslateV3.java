package org.fugue;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.v3.LocationName;
import com.google.cloud.translate.v3.TranslateTextRequest;
import com.google.cloud.translate.v3.TranslateTextResponse;
import com.google.cloud.translate.v3.Translation;
import com.google.cloud.translate.v3.TranslationServiceClient;
import com.google.cloud.translate.v3.TranslationServiceSettings;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class TranslatorGoogleCloudTranslateV3 {
    private final String credentialsPath;
    private final String projectId;

    public TranslatorGoogleCloudTranslateV3() {
        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(Paths.get("env.properties"))) {
            properties.load(inputStream);
            credentialsPath = properties.getProperty("GOOGLE_APPLICATION_CREDENTIALS");
            projectId = properties.getProperty("GOOGLE_PROJECT_ID");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Google Cloud credentials path and project ID from env.properties", e);
        }
    }

    public String translate(String originalText, String fromLanguage, String toLanguage) {
        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath));
            TranslationServiceSettings settings = TranslationServiceSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build();

            try (TranslationServiceClient client = TranslationServiceClient.create(settings)) {
                String location = "global";
                LocationName parent = LocationName.of(projectId, location);

                TranslateTextRequest request = TranslateTextRequest.newBuilder()
                        .setParent(parent.toString())
                        .setMimeType("text/plain")
                        .setSourceLanguageCode(fromLanguage)
                        .setTargetLanguageCode(toLanguage)
                        .addContents(originalText)
                        .build();

                TranslateTextResponse response = client.translateText(request);
                System.out.println(response);
                Translation translation = response.getTranslationsList().get(0);
                return translation.getTranslatedText();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to call the Google Cloud Translate Advanced API", e);
        }
    }
}
