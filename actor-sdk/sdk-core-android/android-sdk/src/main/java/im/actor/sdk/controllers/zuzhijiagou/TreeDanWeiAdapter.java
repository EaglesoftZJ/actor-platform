package im.actor.sdk.controllers.zuzhijiagou;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import im.actor.sdk.R;
import im.actor.sdk.controllers.root.Node;
import im.actor.sdk.util.Screen;

import static im.actor.sdk.R.drawable.answer_background;

public class TreeDanWeiAdapter extends BaseAdapter {
    private Context con;
    private LayoutInflater lif;
    private List<Node> allsCache = new ArrayList<Node>();
    private List<Node> alls = new ArrayList<Node>();
    private TreeDanWeiAdapter oThis = this;
    private int expandedIcon = -1;
    private int collapsedIcon = -1;
    private Node root;

    private int radioIndex = 0;
    private boolean isRadioBut = false;

    private HashMap<String, Boolean> isViewShow;// 根据id，来确认该id下的node是否已经显示

    private float counter = 1;

    private boolean hasCheckBox = false;// 是否拥有复选框
    private boolean isCheckNode = false;// 是否联动
    private int isQuanXuan = 0;// 是否全部选中(0:不做操作 1:选中 -1:不选中)
    private boolean isIcon = false;// 是否要有左侧的图标
    private int expandLevel;// 展开级别

    public void setCounter(float counter) {
        this.counter = counter;
    }

    public boolean isIcon() {
        return isIcon;
    }

    public void setIcon(boolean isIcon) {
        this.isIcon = isIcon;
    }

    /**
     * TreeAdapter构造函数
     *
     * @param context
     * @param rootNode 根节点
     */
    public TreeDanWeiAdapter(Context context, Node rootNode) {
        this.con = context;
        this.lif = (LayoutInflater) con
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.root = rootNode;
        isViewShow = new HashMap<String, Boolean>();
        addNode(rootNode);
    }

    private void addNode(Node node) {
        if (!"-1".equals(node.getValue())) {
            alls.add(node);
            allsCache.add(node);
            if (isViewShow.get(node.getValue()) == null) {
                isViewShow.put(node.getValue(), false);
            }
            node.setIndex(allsCache.size() - 1);
        }
        if ("-1".equals(node.getFid())) {
            node.setParent(null);
        }
        if (node.getChildren() != null) {
            alls = node.getChildren();
            allsCache = node.getChildren();
//            if (node.isLeaf())
//                return;
//            for (int i = 0; i < node.getChildren().size(); i++) {
//                addNode(node.getChildren().get(i));
//            }
        }
    }

    /**
     * 设置展开级别
     *
     * @param level
     */
    public void setExpandLevel(int level) {
        alls.clear();
        this.expandLevel = level;
        for (int i = 0; i < allsCache.size(); i++) {
            Node n = allsCache.get(i);
            if (n.getLevel() <= level) {
                if (n.getLevel() < level) {// 上层都设置展开状态
                    n.setExpanded(true);
                } else {// 最后一层都设置折叠状态
                    n.setExpanded(false);
                }
                alls.add(n);
            }
        }
        this.notifyDataSetChanged();
    }

    /**
     * 控制节点的展开和收缩
     *
     * @param position
     */
    public void ExpandOrCollapse(int position) {
        Node n = alls.get(position);
        if (n != null) {
            if (!n.isLeaf()) {
                n.setExpanded(!n.isExpanded());
                filterNode();
                this.notifyDataSetChanged();
            }
        }
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
        alls.clear();
        allsCache.clear();
        addNode(root);
    }

    /**
     * 获得选中节点
     *
     * @return
     */
    public List<Node> getSeletedNodes() {
        List<Node> nodes = new ArrayList<Node>();
        for (int i = 0; i < allsCache.size(); i++) {
            Node n = allsCache.get(i);
            if (n.isChecked()) {
                nodes.add(n);
            }
        }
        return nodes;
    }

    // 控制节点的展开和折叠
    private void filterNode() {
        alls.clear();
        for (int i = 0; i < allsCache.size(); i++) {
            Node n = allsCache.get(i);
            if (!n.isParentCollapsed() || n.isRoot()) {
                alls.add(n);
            }
        }
    }

    public void setRadioIndex(int radioIndex) {
        this.radioIndex = radioIndex;
    }

    public void setRadioBut(boolean isRadioBut) {
        this.isRadioBut = isRadioBut;
    }

    /**
     * 设置展开和折叠状态图标
     *
     * @param expandedIcon  展开时图标
     * @param collapsedIcon 折叠时图标
     */
    public void setExpandedCollapsedIcon(int expandedIcon, int collapsedIcon) {
        this.expandedIcon = expandedIcon;
        this.collapsedIcon = collapsedIcon;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return alls.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return alls.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // TODO Auto-generated method stub
        Node n = alls.get(position);
        ViewHolder holder = null;
        if (view == null) {
            view = this.lif.inflate(
                    R.layout.fragment_base_zuzhijiagou_tree,
                    null);
            holder = new ViewHolder();
            holder.ivExEc = (ImageView) view.findViewById(R.id.ivExEc);
            holder.chbSelect = (CheckBox) view.findViewById(R.id.chbSelect);
            holder.tvText = (TextView) view.findViewById(R.id.danWeiDialogText);
            holder.dividerView = (TextView) view.findViewById(R.id.zuzhijiagou_divider);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // 得到当前节点
        if (n != null) {
            // 显示文本
            holder.chbSelect.setTag(n);
            holder.tvText.setTag(n);
            holder.chbSelect.setChecked(n.isChecked());
            holder.dividerView.setVisibility(View.VISIBLE);
            if (position < getCount() - 1) {
                Node nNext = alls.get(position + 1);
                if (nNext != null && nNext.getValue().equals("-9999")) {
                    holder.dividerView.setVisibility(View.GONE);
                }
            }
            //显示部门和人员之间的间隔
            if (n.getValue().equals("-9999")) {
                holder.tvText.setBackgroundColor(Color.parseColor("#dddddd"));
                holder.tvText.setHeight(Screen.dp(10));
                holder.tvText.setText("");
                holder.dividerView.setHeight(Screen.dp(10));
//                holder.ivExEc.setImageDrawable(con.getResources().getDrawable(R.drawable.department));
                holder.chbSelect.setVisibility(View.GONE);
//                holder.ivExEc.setVisibility(View.VISIBLE);
                holder.dividerView.setVisibility(View.GONE);
                return view;
            } else {
                holder.tvText.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }

            holder.tvText.setMinHeight(Screen.dp(50));
            int childrenSize = n.getChildrenSize();
//            System.out.println("iGem:size:"+n.getText()+":"+childrenSize);
            if (childrenSize == 0) {
                holder.tvText.setText(n.getText() + "(无)");
            } else {
                holder.tvText.setText(n.getText());
            }

//			view.setBackgroundColor(Color.parseColor("#0A59A8"));
//			holder.tvText.setTextColor(Color.parseColor("#FFFFFF"));

            holder.chbSelect.setOnClickListener(null);
            // 复选框单击事件
//			holder.chbSelect.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					Node n = (Node) v.getTag();
//					if (isCheckNode)
//						checkNode(n, ((CheckBox) v).isChecked());
//					else
//						n.setChecked(((CheckBox) v).isChecked());
//					isQuanXuan = 0;
//					oThis.notifyDataSetChanged();
//
//				}
//			});
            // 文本选中
//			holder.tvText.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					Node n = (Node) v.getTag();
//					boolean isCheck = n.isChecked();
//					if (isCheckNode)
//						checkNode(n, !isCheck);
//					else
//						n.setChecked(!isCheck);
//					isQuanXuan = 0;
//					oThis.notifyDataSetChanged();
//
//				}
//			});

            // 控制缩进
//            view.setPadding(35 * n.getLevel(), 3, 3, 3);
            view.setPadding(0, 3, 3, 3);

            if (hasCheckBox) {
                holder.chbSelect.setVisibility(View.VISIBLE);
                if (isQuanXuan > 0) {
                    holder.chbSelect.setChecked(true);
                    n.setChecked(true);
                    checkNode(n, true);// 联动
                } else if (isQuanXuan < 0) {
                    holder.chbSelect.setChecked(false);
                    n.setChecked(false);
                    checkNode(n, false);
                }
            } else {
                holder.chbSelect.setVisibility(View.GONE);
            }

            if (isIcon) {
                final int id = position;
                holder.ivExEc.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ExpandOrCollapse(id);
                    }
                });
                if (n.isLeaf()) {
                    // 是叶节点 显示展开和折叠状态图标
                    holder.ivExEc.setVisibility(View.VISIBLE);
                    holder.ivExEc.setImageResource(expandedIcon);
                } else {
                    // 单击时控制子节点展开和折叠,状态图标改变
                    holder.ivExEc.setVisibility(View.VISIBLE);
                    if (n.isExpanded()) {
                        if (expandedIcon != -1)
                            holder.ivExEc.setImageResource(expandedIcon);
                    } else {
                        if (collapsedIcon != -1)
                            holder.ivExEc.setImageResource(collapsedIcon);
                    }
                }
            } else {
                holder.ivExEc.setVisibility(View.GONE);
            }
        }

        return view;
    }

    // 复选框联动
    private void checkNode(Node node, boolean isChecked) {
        node.setChecked(isChecked);
        for (int i = 0; i < node.getChildren().size(); i++) {
            checkNode(node.getChildren().get(i), isChecked);
        }
    }

    public void setisQuanXuan(int isQuanXuan) {
        this.isQuanXuan = isQuanXuan;
        if (isQuanXuan == 1) {
            quanXuan(this.root, true);
        } else if (isQuanXuan == -1) {
            quanXuan(this.root, false);
        }
    }

    private void quanXuan(Node root, boolean bol) {
        List<Node> list = root.getChildren();
        for (Node node : list) {
            node.setChecked(bol);
            if (node.getChildren() != null && node.getChildren().size() > 0) {
                quanXuan(node, bol);
            }
        }
    }

    /**
     * 列表项控件集合
     */
    public class ViewHolder {
        TextView tvText;// 文本〉〉〉
        ImageView ivExEc;// 展开或折叠标记">"或"v"
        CheckBox chbSelect;// 选项框
        TextView dividerView;
    }

    public boolean isHasCheckBox() {
        return hasCheckBox;
    }

    public void setHasCheckBox(boolean hasCheckBox) {
        this.hasCheckBox = hasCheckBox;
    }

    public boolean isCheckNode() {
        return isCheckNode;
    }

    public void setCheckNode(boolean isCheckNode) {
        this.isCheckNode = isCheckNode;
    }

    public int getIsQuanXuan() {
        return isQuanXuan;
    }

    public void setIsQuanXuan(int isQuanXuan) {
        this.isQuanXuan = isQuanXuan;
    }

}
