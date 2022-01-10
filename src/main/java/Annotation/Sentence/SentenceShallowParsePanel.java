package Annotation.Sentence;

import AnnotatedSentence.*;
import DataCollector.Sentence.SentenceAnnotatorPanel;
import DataStructure.CounterHashMap;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SentenceShallowParsePanel extends SentenceAnnotatorPanel {
    private HashMap<String, ArrayList<AnnotatedWord>> mappedWords;
    private HashMap<String, ArrayList<AnnotatedSentence>> mappedSentences;
    private String[] shallowParseList = {"YÜKLEM", "ÖZNE", "NESNE", "ZARF_TÜMLECİ", "DOLAYLI_TÜMLEÇ", "HİÇBİRİ"};

    public SentenceShallowParsePanel(String currentPath, String fileName, HashMap<String, ArrayList<AnnotatedWord>> mappedWords, HashMap<String, ArrayList<AnnotatedSentence>> mappedSentences){
        super(currentPath, fileName, ViewLayerType.SHALLOW_PARSE);
        this.mappedWords = mappedWords;
        this.mappedSentences = mappedSentences;
        list.setCellRenderer(new ListRenderer());
        setLayout(new BorderLayout());
    }

    @Override
    protected void setWordLayer() {
        clickedWord.setShallowParse((String) list.getSelectedValue());
    }

    @Override
    protected void setBounds() {
        pane.setBounds(((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().x, ((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().y + 20, 240, (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.4));
    }

    @Override
    protected void drawLayer(AnnotatedWord word, Graphics g, int currentLeft, int lineIndex, int wordIndex, int maxSize, ArrayList<Integer> wordSize, ArrayList<Integer> wordTotal) {
        if (word.getShallowParse() != null){
            String correct = word.getShallowParse();
            g.drawString(correct, currentLeft, (lineIndex + 1) * lineSpace + 30);
        }
    }

    @Override
    protected int getMaxLayerLength(AnnotatedWord word, Graphics g) {
        int maxSize = g.getFontMetrics().stringWidth(word.getName());
        if (word.getShallowParse() != null){
            int size = g.getFontMetrics().stringWidth(word.getShallowParse());
            if (size > maxSize){
                maxSize = size;
            }
        }
        return maxSize;
    }

    private class ListRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component cell = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            AnnotatedWord selectedWord = ((AnnotatedWord)sentence.getWord(selectedWordIndex));
            String examples = "<html>";
            int count = 0;
            if (mappedSentences.containsKey(selectedWord.getName())){
                for (AnnotatedSentence annotatedSentence : mappedSentences.get(selectedWord.getName())){
                    for (int i = 0; i < annotatedSentence.wordCount(); i++){
                        AnnotatedWord word = (AnnotatedWord) annotatedSentence.getWord(i);
                        if (word.getName().equals(selectedWord.getName()) && word.getShallowParse() != null){
                            if (word.getShallowParse().equals(value)){
                                examples += annotatedSentence.toShallowParseString(i) + "<br>";
                                count++;
                            }
                        }
                    }
                    if (count >= 20){
                        break;
                    }
                }
            }
            examples += "</html>";
            ((JComponent) cell).setToolTipText(examples);
            return this;
        }
    }

    private String[] possibleValues(String word){
        if (!mappedWords.containsKey(word)){
            return shallowParseList;
        }
        ArrayList<AnnotatedWord> words = mappedWords.get(word);
        CounterHashMap<String> counts = new CounterHashMap<>();
        for (String shallowParse : shallowParseList){
            counts.put(shallowParse);
        }
        for (AnnotatedWord annotatedWord : words){
            counts.put(annotatedWord.getShallowParse());
        }
        List<Map.Entry<String, Integer>> sortedCounts = counts.topN(counts.size());
        String[] result = new String[sortedCounts.size()];
        int i = 0;
        for (Map.Entry<String, Integer> entry : sortedCounts){
            result[i] = entry.getKey();
            i++;
        }
        return result;
    }

    public int populateLeaf(AnnotatedSentence sentence, int wordIndex){
        int selectedIndex = -1;
        AnnotatedWord word = (AnnotatedWord) sentence.getWord(wordIndex);
        listModel.clear();
        String[] parseList = possibleValues(word.getName());
        for (int i = 0; i < parseList.length; i++){
            if (word.getShallowParse() != null && word.getShallowParse().equals(parseList[i])){
                selectedIndex = i;
            }
            listModel.addElement(parseList[i]);
        }
        return selectedIndex;
    }

}
