package Annotation.Sentence;

import AnnotatedSentence.*;
import DataCollector.ParseTree.TreeEditorPanel;
import DataCollector.Sentence.ViewSentenceAnnotationFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ViewSentenceShallowParseAnnotationFrame extends ViewSentenceAnnotationFrame implements ActionListener {

    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        if (PASTE.equals(e.getActionCommand())) {
            if (selectedRow != -1) {
                for (int rowNo : dataTable.getSelectedRows()) {
                    updateShallowParse(rowNo, data.get(selectedRow).get(TAG_INDEX));
                }
            }
        }
        dataTable.invalidate();
    }

    public class ShallowParseTableDataModel extends TableDataModel {

        public String getColumnName(int col) {
            switch (col) {
                case FILENAME_INDEX:
                    return "FileName";
                case WORD_POS_INDEX:
                    return "Index";
                case WORD_INDEX:
                    return "Word";
                case 3:
                    return "Shallow Parse";
                case 4:
                    return "Sentence";
                default:
                    return "";
            }
        }

        public void setValueAt(Object value, int row, int col) {
            if (col == TAG_INDEX && !data.get(row).get(TAG_INDEX).equals(value)) {
                updateShallowParse(row, (String) value);
            }
        }
    }

    private void updateShallowParse(int row, String newValue){
        data.get(row).set(TAG_INDEX, newValue);
        AnnotatedSentence sentence = (AnnotatedSentence) corpus.getSentence(Integer.parseInt(data.get(row).get(COLOR_COLUMN_INDEX - 1)));
        AnnotatedWord word = (AnnotatedWord) sentence.getWord(Integer.parseInt(data.get(row).get(WORD_POS_INDEX)) - 1);
        word.setShallowParse(newValue);
        sentence.save();
    }

    protected void prepareData(AnnotatedCorpus corpus){
        data = new ArrayList<>();
        for (int i = 0; i < corpus.sentenceCount(); i++){
            AnnotatedSentence sentence = (AnnotatedSentence) corpus.getSentence(i);
            for (int j = 0; j < corpus.getSentence(i).wordCount(); j++){
                AnnotatedWord word = (AnnotatedWord) sentence.getWord(j);
                ArrayList<String> row = new ArrayList<>();
                row.add(sentence.getFileName());
                row.add("" + (j + 1));
                row.add(word.getName());
                if (word.getShallowParse() != null){
                    row.add(word.getShallowParse());
                    row.add("<html>" + sentence.toShallowParseString(j) + "</html>");
                } else {
                    row.add("-");
                    row.add(sentence.toWords());
                }
                row.add("" + i);
                row.add("0");
                data.add(row);
            }
        }
    }

    public ViewSentenceShallowParseAnnotationFrame(AnnotatedCorpus corpus, SentenceShallowParseFrame sentenceShallowParseFrame){
        super(corpus);
        COLOR_COLUMN_INDEX = 6;
        TAG_INDEX = 3;
        prepareData(corpus);
        dataTable = new JTable(new ShallowParseTableDataModel());
        dataTable.getColumnModel().getColumn(FILENAME_INDEX).setMinWidth(150);
        dataTable.getColumnModel().getColumn(FILENAME_INDEX).setMaxWidth(150);
        dataTable.getColumnModel().getColumn(WORD_POS_INDEX).setMinWidth(60);
        dataTable.getColumnModel().getColumn(WORD_POS_INDEX).setMaxWidth(60);
        dataTable.getColumnModel().getColumn(WORD_INDEX).setWidth(200);
        dataTable.getColumnModel().getColumn(TAG_INDEX).setMinWidth(150);
        dataTable.getColumnModel().getColumn(TAG_INDEX).setMaxWidth(150);
        dataTable.setDefaultRenderer(Object.class, new CellRenderer());
        dataTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2){
                    int row = dataTable.rowAtPoint(evt.getPoint());
                    if (row >= 0) {
                        String fileName = data.get(row).get(0);
                        AnnotatedSentence sentence = (AnnotatedSentence) corpus.getSentence(Integer.parseInt(data.get(row).get(COLOR_COLUMN_INDEX - 1)));
                        sentenceShallowParseFrame.addPanelToFrame(sentenceShallowParseFrame.generatePanel(sentence.getFile().getParent(), fileName), fileName);
                    }
                }
            }
        });
        JScrollPane tablePane = new JScrollPane(dataTable);
        add(tablePane, BorderLayout.CENTER);
    }

}
