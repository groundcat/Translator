package org.fugue;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new TranslatorFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}

class TranslatorFrame extends JFrame {
    private JComboBox<String> fromLanguageComboBox;
    private JComboBox<String> toLanguageComboBox;
    private JComboBox<String> translationEngineComboBox;
    private JTextArea sourceTextArea;
    private JTextArea outputTextArea;
    private JButton translateButton;
    private JButton copyButton;

    public TranslatorFrame() {
        setTitle("Language Translation App");
        setSize(900, 600);
        setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);

        // Language selectors
        JLabel fromLanguageLabel = new JLabel("Translate from:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(fromLanguageLabel, constraints);

        fromLanguageComboBox = new JComboBox<>(new String[]{"EN", "ZH"});
        constraints.gridx = 1;
        constraints.gridy = 0;
        add(fromLanguageComboBox, constraints);

        JLabel toLanguageLabel = new JLabel("Translate to:");
        constraints.gridx = 2;
        constraints.gridy = 0;
        add(toLanguageLabel, constraints);

        toLanguageComboBox = new JComboBox<>(new String[]{"ZH", "EN"});
        constraints.gridx = 3;
        constraints.gridy = 0;
        add(toLanguageComboBox, constraints);

        // Translation engine selector
        JLabel translationEngineLabel = new JLabel("Translation Engine:");
        constraints.gridx = 4;
        constraints.gridy = 0;
        add(translationEngineLabel, constraints);

        translationEngineComboBox = new JComboBox<>(new String[]{"DeepL", "Google Cloud v2", "Google Cloud v3", "GPT-3.5"});
        constraints.gridx = 5;
        constraints.gridy = 0;
        add(translationEngineComboBox, constraints);

        // Source text area
        JLabel sourceTextLabel = new JLabel("Source Text:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 3;
        constraints.anchor = GridBagConstraints.WEST;
        add(sourceTextLabel, constraints);

        sourceTextArea = new JTextArea(10, 30);
        sourceTextArea.setLineWrap(true); // Enable soft wrapping
        sourceTextArea.setWrapStyleWord(true); // Wrap at word boundaries
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 3;
        constraints.fill = GridBagConstraints.BOTH;
        JScrollPane sourceScrollPane = new JScrollPane(sourceTextArea);
        sourceScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Disable horizontal scrollbar
        add(sourceScrollPane, constraints);

        // Output text area
        JLabel outputTextLabel = new JLabel("Translated Text:");
        constraints.gridx = 3;
        constraints.gridy = 1;
        constraints.gridwidth = 3;
        constraints.anchor = GridBagConstraints.WEST;
        add(outputTextLabel, constraints);

        outputTextArea = new JTextArea(10, 30);
        outputTextArea.setLineWrap(true); // Enable soft wrapping
        outputTextArea.setWrapStyleWord(true); // Wrap at word boundaries
        constraints.gridx = 3;
        constraints.gridy = 2;
        constraints.gridwidth = 3;
        constraints.fill = GridBagConstraints.BOTH;
        JScrollPane outputScrollPane = new JScrollPane(outputTextArea);
        outputScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Disable horizontal scrollbar
        add(outputScrollPane, constraints);

        // Translate button
        translateButton = new JButton("Translate");
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 3;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        add(translateButton, constraints);

        // Copy button
        copyButton = new JButton("Copy");
        constraints.gridx = 3;
        constraints.gridy = 3;
        constraints.gridwidth = 3;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        add(copyButton, constraints);

        // Event listeners
        translateButton.addActionListener(new TranslateButtonListener());
        copyButton.addActionListener(new CopyButtonListener());
    }

    private class TranslateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String fromLanguage = (String) fromLanguageComboBox.getSelectedItem();
            String toLanguage = (String) toLanguageComboBox.getSelectedItem();
            String translationEngine = (String) translationEngineComboBox.getSelectedItem();
            String sourceText = sourceTextArea.getText();

            // Call the respective translation class and method here.
            String translatedText = ""; // Placeholder for the translated text

            if (translationEngine.equals("DeepL")) {
                TranslatorDeepL translatorDeepL = new TranslatorDeepL();
                translatedText = translatorDeepL.translate(sourceText, fromLanguage, toLanguage);
            } else if (translationEngine.equals("Google Cloud v2")) {
                TranslatorGoogleCloudTranslateV2 translatorGoogleCloudTranslate = new TranslatorGoogleCloudTranslateV2();
                translatedText = translatorGoogleCloudTranslate.translate(sourceText, fromLanguage, toLanguage);
            } else if (translationEngine.equals("Google Cloud v3")) {
                TranslatorGoogleCloudTranslateV3 translatorGoogleCloudTranslateV3 = new TranslatorGoogleCloudTranslateV3();
                translatedText = translatorGoogleCloudTranslateV3.translate(sourceText, fromLanguage, toLanguage);
            } else if (translationEngine.equals("GPT-3.5")) {
                TranslatorGPT3 translatorGPT3 = new TranslatorGPT3();
                translatedText = translatorGPT3.translate(fromLanguage, toLanguage, sourceText);
            } // Add other translation engines' implementation here.

            outputTextArea.setText(translatedText);
        }
    }

    private class CopyButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String outputText = outputTextArea.getText();
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(outputText), null);
        }
    }
}