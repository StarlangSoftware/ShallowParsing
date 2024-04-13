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

    /**
     * Constructor for the Shallow parse panel for a parse tree.
     * @param path The absolute path of the annotated parse tree.
     * @param fileName The raw file name of the annotated parse tree.
     */
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

    /**
     * Overloaded function that displays the next tree according to the index of the parse tree. For example, if the current
     * tree fileName is 0123.train, after the call of nextTree(3), ViewerPanel will display 0126.train. If the next tree
     * does not exist, nothing will happen.
     * @param count Number of trees to go forward
     */
    protected void nextTree(int count){
        super.nextTree(count);
        updateChunks();
    }

    /**
     * Overloaded function that displays the previous tree according to the index of the parse tree. For example, if the
     * current tree fileName is 0123.train, after the call of nextTree(3), ViewerPanel will display 0126.train. If the
     * previous tree does not exist, nothing will happen.
     * @param count Number of trees to go backward
     */
    protected void previousTree(int count){
        super.previousTree(count);
        updateChunks();
    }

    /**
     * The size of the string displayed. If it is a leaf node, it returns the maximum size of the shallow parse tags of
     * word(s) in the node. Otherwise, it returns the size of the symbol in the node.
     * @param parseNode Parse node
     * @param g Graphics on which tree will be drawn.
     * @return Size of the string displayed.
     */
    protected int getStringSize(ParseNodeDrawable parseNode, Graphics g) {
        int i, stringSize = 0;
        if (parseNode.numberOfChildren() == 0) {
            try {
                for (i = 0; i < parseNode.getLayerInfo().getNumberOfWords(); i++)
                    if (g.getFontMetrics().stringWidth(parseNode.getLayerInfo().getShallowParseAt(i)) > stringSize){
                        stringSize = g.getFontMetrics().stringWidth(parseNode.getLayerInfo().getShallowParseAt(i));
                    }
            } catch (LayerNotExistsException | WordNotExistsException ignored) {
            }
            return stringSize;
        } else {
            return g.getFontMetrics().stringWidth(parseNode.getData().getName());
        }
    }

    /**
     * If the node is a leaf node, it draws the shallow parse tag(s) of the word(s). Otherwise, it draws the node symbol.
     * @param parseNode Parse Node
     * @param g Graphics on which symbol is drawn.
     * @param x x coordinate
     * @param y y coordinate
     */
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
                    } catch (LayerNotExistsException | WordNotExistsException ignored) {
                    }
                }
            } catch (LayerNotExistsException ignored) {
            }
        } else {
            g.drawString(parseNode.getData().getName(), x, y);
        }
    }

    /**
     * Sets the size of the enclosing area of the parse node (for selecting, editing etc.).
     * @param parseNode Parse Node
     * @param x x coordinate of the center of the node.
     * @param y y coordinate of the center of the node.
     * @param stringSize Size of the string in terms of pixels.
     */
    protected void setArea(ParseNodeDrawable parseNode, int x, int y, int stringSize){
        if (parseNode.numberOfChildren() == 0){
            try {
                parseNode.setArea(x - 5, y - 15, stringSize + 10, 20 * (parseNode.getLayerInfo().getNumberOfWords() + 1));
            } catch (LayerNotExistsException ignored) {
            }
        } else {
            parseNode.setArea(x - 5, y - 15, stringSize + 10, 20);
        }
    }
}
