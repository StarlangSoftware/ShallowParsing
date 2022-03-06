package Annotation.ParseTree;

import AnnotatedSentence.LayerNotExistsException;
import AnnotatedSentence.ViewLayerType;
import AnnotatedTree.ChunkType;
import AnnotatedTree.ParseNodeDrawable;
import AnnotatedTree.WordNotExistsException;
import DataCollector.ParseTree.TreeEditorPanel;

import java.awt.*;

public class TreeChunkViewPanel extends TreeEditorPanel {
    private ChunkType chunkType;

    public TreeChunkViewPanel(String path, String fileName) {
        super(path, fileName, ViewLayerType.SHALLOW_PARSE);
        chunkType = ChunkType.NORMAL;
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private void updateChunks(){
        currentTree.reload();
        currentTree.extractVerbal();
        currentTree.setShallowParseLayer(chunkType);
        repaint();
    }

    public void setChunkType(ChunkType chunkType){
        this.chunkType = chunkType;
        updateChunks();
    }

    protected void nextTree(int count){
        super.nextTree(count);
        updateChunks();
    }

    protected void previousTree(int count){
        super.previousTree(count);
        updateChunks();
    }

    protected int getStringSize(ParseNodeDrawable parseNode, Graphics g) {
        int i, stringSize = 0;
        if (parseNode.numberOfChildren() == 0) {
            try {
                for (i = 0; i < parseNode.getLayerInfo().getNumberOfWords(); i++)
                    if (g.getFontMetrics().stringWidth(parseNode.getLayerInfo().getShallowParseAt(i)) > stringSize){
                        stringSize = g.getFontMetrics().stringWidth(parseNode.getLayerInfo().getShallowParseAt(i));
                    }
            } catch (LayerNotExistsException | WordNotExistsException e) {
                e.printStackTrace();
            }
            return stringSize;
        } else {
            return g.getFontMetrics().stringWidth(parseNode.getData().getName());
        }
    }

    protected void drawString(ParseNodeDrawable parseNode, Graphics g, int x, int y){
        int i;
        if (parseNode.numberOfChildren() == 0){
            g.drawString(parseNode.getLayerData(ViewLayerType.TURKISH_WORD), x, y);
            g.setColor(Color.RED);
            try {
                for (i = 0; i < parseNode.getLayerInfo().getNumberOfWords(); i++){
                    try {
                        y += 20;
                        g.drawString(parseNode.getLayerInfo().getShallowParseAt(i), x, y);
                    } catch (LayerNotExistsException | WordNotExistsException e) {
                        e.printStackTrace();
                    }
                }
            } catch (LayerNotExistsException e) {
                e.printStackTrace();
            }
        } else {
            g.drawString(parseNode.getData().getName(), x, y);
        }
    }

    protected void setArea(ParseNodeDrawable parseNode, int x, int y, int stringSize){
        if (parseNode.numberOfChildren() == 0){
            try {
                parseNode.setArea(x - 5, y - 15, stringSize + 10, 20 * (parseNode.getLayerInfo().getNumberOfWords() + 1));
            } catch (LayerNotExistsException e) {
                e.printStackTrace();
            }
        } else {
            parseNode.setArea(x - 5, y - 15, stringSize + 10, 20);
        }
    }
}
