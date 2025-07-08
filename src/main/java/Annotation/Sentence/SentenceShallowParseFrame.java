package Annotation.Sentence;

import AnnotatedSentence.AnnotatedCorpus;
import AnnotatedSentence.AnnotatedSentence;
import AnnotatedSentence.AnnotatedWord;
import DataCollector.Sentence.SentenceAnnotatorFrame;
import DataCollector.Sentence.SentenceAnnotatorPanel;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class SentenceShallowParseFrame extends SentenceAnnotatorFrame {

    private final HashMap<String, ArrayList<AnnotatedWord>> mappedWords = new HashMap<>();
    private final HashMap<String, ArrayList<AnnotatedSentence>> mappedSentences = new HashMap<>();

    /**
     * Constructor of the Shallow parse frame for annotated sentence. It reads the annotated sentence corpus. It also
     * creates mappedWords and mappedSentences. mappedWords will be used to show the
     * user how many times that word was annotated with different Shallow Parse tags. mappedSentences will be used to
     * show the user how other sentences with that word was annotated.
     */
    public SentenceShallowParseFrame(){
        super();
        AnnotatedCorpus annotatedCorpus;
        String subFolder = "false";
        Properties properties1 = new Properties();
        try {
            properties1.load(Files.newInputStream(new File("config.properties").toPath()));
            subFolder = properties1.getProperty("subFolder");
        } catch (IOException ignored) {
        }
        annotatedCorpus = readCorpus(subFolder);
        for (int i = 0; i < annotatedCorpus.sentenceCount(); i++){
            AnnotatedSentence sentence = (AnnotatedSentence) annotatedCorpus.getSentence(i);
            for (int j = 0; j < sentence.wordCount(); j++){
                AnnotatedWord word = (AnnotatedWord) sentence.getWord(j);
                if (word.getName() != null && word.getShallowParse() != null){
                    ArrayList<AnnotatedWord> annotatedWords;
                    if (mappedWords.containsKey(word.getName())){
                        annotatedWords = mappedWords.get(word.getName());
                    } else {
                        annotatedWords = new ArrayList<>();
                    }
                    annotatedWords.add(word);
                    mappedWords.put(word.getName(), annotatedWords);
                    ArrayList<AnnotatedSentence> annotatedSentences;
                    if (mappedSentences.containsKey(word.getName())){
                        annotatedSentences = mappedSentences.get(word.getName());
                    } else {
                        annotatedSentences = new ArrayList<>();
                    }
                    annotatedSentences.add(sentence);
                    mappedSentences.put(word.getName(), annotatedSentences);
                }
            }
        }
        JMenuItem itemViewAnnotated = addMenuItem(projectMenu, "View Annotations", KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        itemViewAnnotated.addActionListener(e -> new ViewSentenceShallowParseAnnotationFrame(annotatedCorpus, this));
        JOptionPane.showMessageDialog(this, "Annotated corpus is loaded!", "Shallow Parse Annotation", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public SentenceAnnotatorPanel generatePanel(String currentPath, String rawFileName) {
        return new SentenceShallowParsePanel(currentPath, rawFileName,mappedWords, mappedSentences);
    }
}
