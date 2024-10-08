package Annotation.ParseTree;

import AnnotatedTree.ChunkType;
import DataCollector.ParseTree.TreeEditorFrame;
import DataCollector.ParseTree.TreeEditorPanel;

import javax.swing.*;
import java.awt.*;

public class TreeChunkViewFrame extends TreeEditorFrame {
    private final JComboBox<String> chunkTypeComboBox;

    /**
     * Constructor for shallow parse viewer frame.
     */
    public TreeChunkViewFrame(){
        this.setTitle("Chunking Viewer");
        toolBar.addSeparator();
        chunkTypeComboBox = new JComboBox<>(new String[]{"Exists", "Normal", "Detailed"});
        chunkTypeComboBox.setMaximumSize(new Dimension(120, 25));
        toolBar.add(chunkTypeComboBox);
        chunkTypeComboBox.addActionListener(e -> {
            switch (chunkTypeComboBox.getSelectedIndex()){
                case 0:
                    setChunkType(ChunkType.EXISTS);
                    break;
                case 1:
                    setChunkType(ChunkType.NORMAL);
                    break;
                case 2:
                    setChunkType(ChunkType.DETAILED);
                    break;
            }
        });
    }

    @Override
    protected TreeEditorPanel generatePanel(String currentPath, String rawFileName) {
        return new TreeChunkViewPanel(currentPath, rawFileName);
    }

    private void setChunkType(ChunkType chunkType){
        TreeChunkViewPanel current = (TreeChunkViewPanel) ((JScrollPane) projectPane.getSelectedComponent()).getViewport().getView();
        current.setChunkType(chunkType);
    }

}
