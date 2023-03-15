Java Language Translation Application
=====================================

This is a simple Java application that uses Swing for its GUI and allows users to translate text between different languages using DeepL, Google Translate, and OpenAI GPT-3.5 APIs.

Features
--------

-   User-friendly Swing-based GUI
-   Select from different translation engines: DeepL, Google Translate, and OpenAI GPT-3.5
-   Choose source and target languages for translation
-   Enter the text to be translated in a textarea
-   Display the translated text in another textarea
-   Copy the translated text to the clipboard

Prerequisites
-------------

-   Java JDK 11 or later
-   Maven

Setup
-----

1.  Clone this repository to your local machine.
2.  Obtain API keys for the DeepL, Google Translate, and OpenAI GPT-3.5 services, and download the Google Cloud credentials JSON file for your Google Cloud project.
3.  Create an `env.properties` file in the project root directory with the following content, replacing the placeholders with the appropriate values:

```
DEEPL_API_KEY=your_deepl_api_key
GOOGLE_API_KEY=your_google_api_key
OPENAI_API_KEY=your_openai_api_key
GOOGLE_APPLICATION_CREDENTIALS=path_to_your_credentials_file.json
GOOGLE_PROJECT_ID=your_project_id
```

4.  Build the application using Maven:

```bash
mvn clean compile
```

5.  Run the application:

```bash
mvn exec:java -Dexec.mainClass="Main"
```

Usage
-----

1.  Launch the application by following the setup steps above.
2.  Select the translation engine you want to use from the dropdown menu.
3.  Choose the source and target languages from the language selectors.
4.  Enter the text you want to translate in the textarea on the left.
5.  Click the "Translate" button to perform the translation.
6.  The translated text will be displayed in the textarea on the right.
7.  Click the "Copy" button to copy the translated text to the clipboard.

License
-------

This project is open-source and available under the [MIT License](https://chat.openai.com/chat/LICENSE).