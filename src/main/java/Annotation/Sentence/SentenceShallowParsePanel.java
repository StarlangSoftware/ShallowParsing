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
    private final HashMap<String, ArrayList<AnnotatedWord>> mappedWords;
    private final HashMap<String, ArrayList<AnnotatedSentence>> mappedSentences;
    private final String[] shallowParseList = {"YÜKLEM", "ÖZNE", "NESNE", "ZARF_TÜMLECİ", "DOLAYLI_TÜMLEÇ", "HİÇBİRİ"};

    /**
     * Constructor for the NER panel for an annotated sentence. Sets the attributes.
     * @param currentPath The absolute path of the annotated file.
     * @param fileName The raw file name of the annotated file.
     * @param mappedWords Enlists other annotated words that has the same word in the key.
     * @param mappedSentences Enlists other annotated sentence that contains the same word in the key.
     */
    public SentenceShallowParsePanel(String currentPath, String fileName, HashMap<String, ArrayList<AnnotatedWord>> mappedWords, HashMap<String, ArrayList<AnnotatedSentence>> mappedSentences){
        super(currentPath, fileName, ViewLayerType.SHALLOW_PARSE);
        this.mappedWords = mappedWords;
        this.mappedSentences = mappedSentences;
        list.setCellRenderer(new ListRenderer());
        setLayout(new BorderLayout());
    }

    /**
     * Updates the Shallow Parse layer of the annotated word.
     */
    @Override
    protected void setWordLayer() {
        clickedWord.setShallowParse((String) list.getSelectedValue());
    }

    /**
     * Sets the width and height of the JList that displays the Shallow parse tags.
     */
    @Override
    protected void setBounds() {
        pane.setBounds(((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().getX(), ((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().getY() + 20, 240, (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.4));
    }

    /**
     * Sets the space between displayed lines in the sentence.
     */
    @Override
    protected void setLineSpace() {
        lineSpace = 80;
    }

    /**
     * Draws the shallow parse tag of the word.
     * @param word Annotated word itself.
     * @param g Graphics on which shallow parse tag is drawn.
     * @param currentLeft Current position on the x-axis, where the shallow parse tag will be aligned.
     * @param lineIndex Current line of the word, if the sentence resides in multiple lines on the screen.
     * @param wordIndex Index of the word in the annotated sentence.
     * @param maxSize Maximum size in pixels of anything drawn in the screen.
     * @param wordSize Array storing the sizes of all words in pixels in the annotated sentence.
     * @param wordTotal Array storing the total size until that word of all words in the annotated sentence.
     */
    @Override
    protected void drawLayer(AnnotatedWord word, Graphics g, int currentLeft, int lineIndex, int wordIndex, int maxSize, ArrayList<Integer> wordSize, ArrayList<Integer> wordTotal) {
        if (word.getShallowParse() != null){
            String correct = word.getShallowParse();
            g.drawString(correct, currentLeft, (lineIndex + 1) * lineSpace + 30);
        }
    }

    /**
     * Compares the size of the word and the size of the shallow parse tag in pixels and returns the maximum of them.
     * @param word Word annotated.
     * @param g Graphics on which shallow parse is drawn.
     * @return Maximum of the graphic sizes of word and its shallow parse tag.
     */
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

    /**
     * Construct the tooltip text for every shallow parse tag for selectedWord using the mappedSentences. The tooltip
     * enlists the example sentences that contains the selectedWord annotated with that tag. If the number of example
     * sentence are more than 20, it only displays the first 20 of them.
     */
    private class ListRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component cell = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            AnnotatedWord selectedWord = ((AnnotatedWord)sentence.getWord(selectedWordIndex));
            StringBuilder examples = new StringBuilder("<html>");
            int count = 0;
            if (mappedSentences.containsKey(selectedWord.getName())){
                for (AnnotatedSentence annotatedSentence : mappedSentences.get(selectedWord.getName())){
                    for (int i = 0; i < annotatedSentence.wordCount(); i++){
                        AnnotatedWord word = (AnnotatedWord) annotatedSentence.getWord(i);
                        if (word.getName().equals(selectedWord.getName()) && word.getShallowParse() != null){
                            if (word.getShallowParse().equals(value)){
                                examples.append(annotatedSentence.toShallowParseString(i)).append("<br>");
                                count++;
                            }
                        }
                    }
                    if (count >= 20){
                        break;
                    }
                }
            }
            examples.append("</html>");
            ((JComponent) cell).setToolTipText(examples.toString());
            return this;
        }
    }

    /**
     * Sorts the Shallow parse tags according to usage frequency (how many times they are used to tag that word) in
     * decreasing order.
     * @param word The selected word
     * @return Shallow parse tags sorted in decreasing order of usage frequency.
     */
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

    /**
     * Fills the JList that contains all possible shallow parse tags. Shallow parse tags are sorted in decreasing order
     * of usage frequency of those tags for that word.
     * @param sentence Sentence used to populate for the current word.
     * @param wordIndex Index of the selected word.
     * @return The index of the selected tag, -1 if nothing selected.
     */
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
