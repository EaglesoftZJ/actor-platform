package im.actor.sdk.controllers.zuzhijiagou.topBar;

import java.io.Serializable;
import java.util.List;

import im.actor.sdk.controllers.root.Node;

/**
 * Created by huchengjie on 2017/11/10.
 */

public class TreeBarBean implements Serializable{
    private String text;
    private List<Node> nodes;
    private int treeSize;

    public TreeBarBean() {

    }

    public TreeBarBean(String text,List<Node> nodes,int treeSize) {
        this.text = text;
        this.nodes = nodes;
        this.treeSize = treeSize;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public int getTreeSize() {
        return treeSize;
    }

    public void setTreeSize(int treeSize) {
        this.treeSize = treeSize;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
}
