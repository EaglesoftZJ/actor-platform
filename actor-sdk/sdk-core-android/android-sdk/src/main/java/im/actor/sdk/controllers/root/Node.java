package im.actor.sdk.controllers.root;

import android.support.annotation.NonNull;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import im.actor.core.viewmodel.UserVM;
import im.actor.runtime.mvvm.BaseValueModel;

/**
 * Created by huchengjie on 2017/9/21.
 */

//                            "IGIMID":"895636497",
//                                    "id":"yh0000002",
//                                    "xm":"系统管理员",
//                                    "zh":"xmgs",
//                                    "bmid":"bm002 ",
//                                    "fid":"",
//                                    "dwid":"dw002",
//                                    "bmmc":"系统管理",
//                                    "dwmc":"舟山港兴港工程建设项目管理有限公司",
//                                    "wzh":"999"

//                            {
//                                "id":"dw018",
//                                    "mc":"宁波舟山港舟山港务有限公司",
//                                    "wzh":"70"
//                            }

//                           {
//                            "id":"bm010",
//                                    "mc":"系统管理",
//                                    "wzh":"0",
//                                    "fid":"",
//                                    "dwid":"dw006"
//                        }
public class Node  implements Serializable,
        Comparable<Node> {

    private Node parent;// 父节点
    private List<Node> children = new ArrayList<Node>();// 子节点
    private int childrenSize =-1;
    private String text;// 节点显示的文字
    private String value;// 节点的值
    private String fid;// 父节点标识
    private String wzh;// 位置号
    private String dwid;
    private String bmid;
    private String szk;
    private int index;// 在listView里的位置，从0开始
    private boolean isChecked = false;// 是否处于选中状态
    private boolean isExpanded = false;// 是否处于展开状态
    private boolean isRy = false; //是否是人员

    private JSONObject json;
    /**
     * Node构造函数
     *
     * @param text  节点显示的文字
     * @param value 节点的值
     */
    public Node(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 设置父节点
     *
     * @param node
     */
    public void setParent(Node node) {
        this.parent = node;
    }

    /**
     * 获得父节点
     *
     * @return
     */
    public Node getParent() {
        return this.parent;
    }

    /**
     * 设置节点文本
     *
     * @param text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * 获得节点文本
     *
     * @return
     */
    public String getText() {
        return this.text;
    }

    /**
     * 设置节点值
     *
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 获得节点值
     *
     * @return
     */
    public String getValue() {
        return this.value;
    }

    /**
     * 是否根节点
     *
     * @return
     */
    public boolean isRoot() {
        if (parent == null) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 获得子节点
     *
     * @return
     */
    public List<Node> getChildren() {
        return this.children;
    }

    /**
     * 添加子节点
     *
     * @param node
     */
    public void add(Node node) {
        if (!children.contains(node)) {
            children.add(node);
        }
    }

    /**
     * 清除所有子节点
     */
    public void clear() {
        children.clear();
    }

    /**
     * 删除一个子节点
     *
     * @param node
     */
    public void remove(Node node) {
        if (children.contains(node)) {
            children.remove(node);
        }
    }

    /**
     * 删除指定位置的子节点
     *
     * @param location
     */
    public void remove(int location) {
        children.remove(location);
    }

    /**
     * 获得节点的级数,根节点为0
     *
     * @return
     */
    public int getLevel() {
        return parent == null ? 0 : parent.getLevel() + 1;
    }

    /**
     * 设置节点选中状态
     *
     * @param isChecked
     */
    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    /**
     * 获得节点选中状态
     *
     * @return
     */
    public boolean isChecked() {
        return isChecked;
    }

    /**
     * 设置是否拥有复选框
     *
     * @param hasCheckBox
     */

    /**
     * 是否叶节点,即没有子节点的节点
     *
     * @return
     */
    public boolean isLeaf() {
        return children.size() < 1 ? true : false;
    }

    /**
     * 当前节点是否处于展开状态
     *
     * @return
     */
    public boolean isExpanded() {
        return isExpanded;
    }

    /**
     * 设置节点展开状态
     *
     * @return
     */
    public void setExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }

    /**
     * 递归判断父节点是否处于折叠状态,有一个父节点折叠则认为是折叠状态
     *
     * @return
     */
    public boolean isParentCollapsed() {
        if (parent == null)
            return !isExpanded;
        if (!parent.isExpanded())
            return true;
        return parent.isParentCollapsed();
    }

    /**
     * 递归判断所给的节点是否当前节点的父节点
     *
     * @param node 所给节点
     * @return
     */
    public boolean isParent(Node node) {
        if (parent == null)
            return false;
        if (node.equals(parent))
            return true;
        return parent.isParent(node);
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getWzh() {
        return wzh;
    }

    public void setWzh(String wzh) {
        this.wzh = wzh;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    public String getDwid() {
        return dwid;
    }

    public void setDwid(String dwid) {
        this.dwid = dwid;
    }

    public String getBmid() {
        return bmid;
    }

    public void setBmid(String bmid) {
        this.bmid = bmid;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }


    public String getSzk() {
        return szk;
    }

    public void setSzk(String szk) {
        this.szk = szk;
    }

    public int getChildrenSize() {
        return childrenSize;
    }

    public void setChildrenSize(int childrenSize) {
        this.childrenSize = childrenSize;
    }

    @Override
    public int compareTo(@NonNull Node another) {
        int sx = 0;
        try {
            int a = Integer.parseInt(this.getWzh());
            int b = Integer.parseInt(another.getWzh());
            if (a >= b)
                sx = 1;
            else
                sx = -1;
        } catch (Exception e) {

        }

        return sx;
    }

    public boolean isRy() {
        return isRy;
    }

    public void setRy(boolean ry) {
        isRy = ry;
    }
}
