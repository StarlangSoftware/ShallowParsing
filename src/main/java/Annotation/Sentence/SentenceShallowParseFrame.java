package Annotation.Sentence;

import AnnotatedSentence.AnnotatedCorpus;
import DataCollector.ParseTree.TreeEditorPanel;
import DataCollector.Sentence.SentenceAnnotatorFrame;
import DataCollector.Sentence.SentenceAnnotatorPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

public class SentenceShallowParseFrame extends SentenceAnnotatorFrame {

    public SentenceShallowParseFrame(){
        super();
        AnnotatedCorpus corpus;
        corpus = new AnnotatedCorpus(new File(TreeEditorPanel.phrasePath));
        JMenuItem itemViewAnnotated = addMenuItem(projectMenu, "View Annotations", KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        itemViewAnnotated.addActionListener(e -> {
            new ViewSentenceShallowParseAnnotationFrame(corpus, this);
        });
        JOptionPane.showMessageDialog(this, "Annotated corpus is loaded!", "Shallow Parse Annotation", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected SentenceAnnotatorPanel generatePanel(String currentPath, String rawFileName) {
        return new SentenceShallowParsePanel(currentPath, rawFileName);
    }
}
