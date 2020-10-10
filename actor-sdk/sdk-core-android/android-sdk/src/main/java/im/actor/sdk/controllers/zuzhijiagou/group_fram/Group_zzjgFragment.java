package im.actor.sdk.controllers.zuzhijiagou.group_fram;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import im.actor.core.viewmodel.UserVM;
import im.actor.runtime.promise.Promise;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.controllers.BaseFragment;
import im.actor.sdk.controllers.Intents;
import im.actor.sdk.controllers.compose.GroupUsersFragment;
import im.actor.sdk.controllers.compose.SimpleCreateGroupActivity;
import im.actor.sdk.controllers.compose.view.UserSpan;
import im.actor.sdk.controllers.conversation.toolbar.ChatToolbarFragment;
import im.actor.sdk.controllers.root.Node;
import im.actor.sdk.controllers.zuzhijiagou.TreeDanWeiAdapter;
import im.actor.sdk.controllers.zuzhijiagou.ZzjgFragment;
import im.actor.sdk.controllers.zuzhijiagou.topBar.TopBarAdapter;
import im.actor.sdk.controllers.zuzhijiagou.topBar.TopBarItemDecoration;
import im.actor.sdk.controllers.zuzhijiagou.topBar.TreeBarBean;
import im.actor.sdk.util.BoxUtil;
import im.actor.sdk.util.KeyboardHelper;
import im.actor.sdk.util.Screen;

import static im.actor.sdk.util.ActorSDKMessenger.messenger;
import static im.actor.sdk.util.ActorSDKMessenger.users;

/**
 * Created by huchengjie on 2017/9/21.
 */

public class Group_zzjgFragment extends BaseFragment {

    private Node rootNode;
    private View emptyView;

    RecyclerView topBarRecyclerView;

    ListView dwcollection;
    //    ListView bmcollection;
//    ListView rycollection;
    //    TextView zuZhiTitleText;
//    ImageView backImage;
    LinearLayout zuzhijiagou_lay;
    int treeSize = 0;//0表示单位层级，1表示部门层级，2表示人员层级


    HashMap<String, HashMap<String, List<Node>>> bmMap = new HashMap<String, HashMap<String, List<Node>>>();
    HashMap<String, HashMap<String, HashMap<String, List<Node>>>> ryMap = new HashMap<>();


    List<Integer> userIds;

    EditText searchField;
    private TextWatcher textWatcher;

    private String title;
    private int userId;
    private String avatarPath;

    public Group_zzjgFragment() {
        setRootFragment(true);
        setHomeAsUp(true);
        setTitle("组织架构");
    }

    public static Group_zzjgFragment createGroup(String title, String avatarPath, int userId) {
        Group_zzjgFragment res = new Group_zzjgFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("avatarPath", avatarPath);
        args.putInt("userId", userId);
        res.setArguments(args);
        return res;
    }

    public static Group_zzjgFragment createGroup(String title, String avatarPath) {
        Group_zzjgFragment res = new Group_zzjgFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("avatarPath", avatarPath);
        res.setArguments(args);
        return res;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return onCreateContactsView(R.layout.fragment_base_zuzhijiagou, inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.create_group, menu);
        menu.findItem(R.id.done).setEnabled(true);
    }

    public RecyclerView.Adapter adapter;
    TopBarAdapter baradapter;

    protected View onCreateContactsView(int layoutId, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View res = inflater.inflate(layoutId, container, false);

        setTitle(R.string.create_group_title);

        title = getArguments().getString("title");
        userId = getArguments().getInt("userId");
        avatarPath = getArguments().getString("avatarPath");

        userIds = new ArrayList<>();
        topBarRecyclerView = (RecyclerView) res.findViewById(R.id.zuzhijiagou_topbar);
        topBarRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), OrientationHelper.HORIZONTAL, false));
        List<TreeBarBean> bars = new ArrayList<TreeBarBean>();
        baradapter = new TopBarAdapter(getContext(), bars);
        baradapter.setOnItemClickLitener(new TopBarAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                TreeBarBean barBean = baradapter.getBars().get(position);
                if (position == baradapter.getItemCount() - 1 && !barBean.getText().equals("单位"))
                    return;
                Node root = new Node("", "-1");
                TreeDanWeiAdapter danWeiAdapter = (TreeDanWeiAdapter) dwcollection.getAdapter();
                List<Node> nodes = new ArrayList<>();
                nodes.addAll(barBean.getNodes());
                root.setChildren(nodes);
                danWeiAdapter.setRoot(root);
                danWeiAdapter.notifyDataSetChanged();
                treeSize = barBean.getTreeSize();
                int count = baradapter.getItemCount() - 1;
                for (int i = count; i > position; i--) {
                    baradapter.getBars().remove(i);
                }
                baradapter.notifyDataSetChanged();
            }
        });
        topBarRecyclerView.setAdapter(baradapter);
        topBarRecyclerView.addItemDecoration(new TopBarItemDecoration(getContext()));

        dwcollection = (ListView) res.findViewById(R.id.zuzhijiagou_dw);
//        bmcollection = (ListView) res.findViewById(R.id.zuzhijiagou_bm);
//        rycollection = (ListView) res.findViewById(R.id.zuzhijiagou_ry);
        zuzhijiagou_lay = (LinearLayout) res.findViewById(R.id.zuzhijiagou_title_lay);
        ZzjgData(ActorSDK.getZjjgData());

        res.findViewById(R.id.zuzhijiagou_dw).setBackgroundColor(ActorSDK.sharedActor().style.getMainBackgroundColor());
        emptyView = res.findViewById(R.id.emptyCollection);
        if (emptyView != null) {
            emptyView.setBackgroundColor(ActorSDK.sharedActor().style.getMainBackgroundColor());
            emptyView.findViewById(R.id.empty_collection_bg).setBackgroundColor(ActorSDK.sharedActor().style.getMainColor());
            ((TextView) emptyView.findViewById(R.id.empty_collection_text)).setTextColor(ActorSDK.sharedActor().style.getMainColor());
        } else {
            emptyView = res.findViewById(R.id.empty_collection_text);
            if (emptyView != null && emptyView instanceof TextView) {
                ((TextView) emptyView.findViewById(R.id.empty_collection_text)).setTextColor(ActorSDK.sharedActor().style.getMainColor());
            }
        }

        if (emptyView != null) {
            dwcollection.setEmptyView(emptyView);
        }
        res.setBackgroundColor(ActorSDK.sharedActor().style.getMainBackgroundColor());


        searchField = (EditText) res.findViewById(R.id.searchField_ry);
        searchField.setVisibility(View.VISIBLE);
        searchField.setTextColor(ActorSDK.sharedActor().style.getTextPrimaryColor());
        searchField.setHintTextColor(ActorSDK.sharedActor().style.getTextHintColor());
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkForDeletions(s);
                String filter = s.toString().trim();
                while (filter.length() > 0 && filter.charAt(0) == '!') {
                    filter = filter.substring(1);
                }
                if (filter.length() > 0) {
                    org.json.JSONArray yh_array = null;
                    try {
                        yh_array = ActorSDK.getZjjgData().getJSONArray("yh_data");
                        int order = 0;
                        List<Node> yhList = getListNode(yh_array, new String[]{"iGIMID", "xm", "wzh", "fid", "dwid", "bmid"});
                        List<Node> ryNode = new ArrayList<>();
                        for (Node node : yhList) {
                            if (node.getText().contains(filter)) {
                                try {
                                    int uid = Integer.parseInt(node.getValue());
                                    UserVM userVM = users().get(uid);
                                    node.setRy(true);
                                    ryNode.add(node);
                                } catch (RuntimeException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                        nodeSelect(ryNode);
                        List<Node> nodes = new ArrayList<>();
                        TreeDanWeiAdapter adapter = (TreeDanWeiAdapter) dwcollection.getAdapter();
                        nodes.addAll(ryNode);
                        Node root = adapter.getRoot();
                        root.setChildren(nodes);
                        adapter.setRoot(root);
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    TreeBarBean barBean = baradapter.getBars().get(baradapter.getBars().size() - 1);
                    Node root = new Node("", "-1");
                    TreeDanWeiAdapter danWeiAdapter = (TreeDanWeiAdapter) dwcollection.getAdapter();
                    List<Node> nodes = new ArrayList<>();
                    nodes.addAll(barBean.getNodes());
                    root.setChildren(nodes);
                    danWeiAdapter.setRoot(root);
                    danWeiAdapter.notifyDataSetChanged();
                    treeSize = barBean.getTreeSize();
                    baradapter.notifyDataSetChanged();
                }
            }
        };
        KeyboardHelper helper = new KeyboardHelper(getActivity());
        helper.setImeVisibility(searchField, false);
        return res;
    }

    @Override
    public void onResume() {
        super.onResume();
        searchField.addTextChangedListener(textWatcher);
        if (userId != 0) {
            userIds.add(userId);
            getActivity().invalidateOptionsMenu();
            updateEditText();
        }
    }

    private void checkForDeletions(Editable editable) {
//        Integer[] selected = userIds;
        boolean hasDeletions = false;
        UserSpan[] spans = editable.getSpans(0, editable.length(), UserSpan.class);
        for (Integer u : userIds) {
            boolean founded = false;
            for (UserSpan span : spans) {
                if (span.getUser().getId() == u) {
                    if (editable.getSpanStart(span) == editable.getSpanEnd(span)) {
                        break;
                    } else {
                        founded = true;
                        break;
                    }
                }
            }

            if (!founded) {
                hasDeletions = true;
                userIds.remove(u);
                TreeDanWeiAdapter adapter = (TreeDanWeiAdapter) dwcollection.getAdapter();
                List<Node> nodes = adapter.getRoot().getChildren();

                for (int i = 0; i < nodes.size(); i++) {
                    if (nodes.get(i).isRy() && nodes.get(i).getValue().equals(u + "")) {
                        nodes.get(i).setChecked(false);
                        break;
                    }
                }
//                unselect(u);
//                userIds.remove(u);
            }
        }
        if (hasDeletions) {
            getActivity().invalidateOptionsMenu();
//            getAdapter().notifyDataSetChanged();
        }
    }

    private void updateEditText() {
        String src = "";
        for (int i = 0; i < userIds.size(); i++) {
            src += "!";
        }
        Spannable spannable = new SpannableString(src);
        for (int i = 0; i < userIds.size(); i++) {
            try {
                UserVM user = users().get(userIds.get(i).intValue());
                spannable.setSpan(new UserSpan(user, Screen.dp(200)), i, i + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        searchField.removeTextChangedListener(textWatcher);
        searchField.setText(spannable);
        searchField.setSelection(spannable.length());
        searchField.addTextChangedListener(textWatcher);
//        filter("");
//        getAdapter().notifyDataSetChanged();
    }

//    protected RecyclerView.Adapter onCreateAdapter(Node node, Activity activity) {
//        return new ZzjgAdapter(rootNode, activity, false, new OnItemClickedListener<Contact>() {
//            @Override
//            public void onClicked(Contact item) {
//                onItemClicked(item);
//            }
//
//            @Override
//            public boolean onLongClicked(Contact item) {
//                return onItemLongClicked(item);
//            }
//        });
//    }

    protected void configureRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setRecycleChildrenOnDetach(false);
        linearLayoutManager.setSmoothScrollbarEnabled(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHorizontalScrollBarEnabled(false);
        recyclerView.setVerticalScrollBarEnabled(true);
    }

    public void setAnimationsEnabled(boolean isEnabled) {
        if (isEnabled) {
            dwcollection.setVisibility(View.VISIBLE);
        } else {
            dwcollection.setVisibility(View.GONE);
        }
    }

    public void ZzjgData(JSONObject json) {
        JSONArray yh_array = null;
        try {
            if (json == null) {
                return;
            }
            dwcollection.setVisibility(View.VISIBLE);
//            bmcollection.setVisibility(View.GONE);
//            rycollection.setVisibility(View.GONE);
            zuzhijiagou_lay.setVisibility(View.VISIBLE);
            yh_array = json.getJSONArray("yh_data");
            List<Node> yhList = getListNode(yh_array, new String[]{"iGIMID", "xm", "wzh", "fid", "dwid", "bmid"});
//            yhList = getAdapterRoot(yhList).getChildren();
            ryMap = new HashMap<>();
            for (int i = 0; i < yhList.size(); i++) {
                Node node = yhList.get(i);
                node.setRy(true);
                JSONObject ryJson = node.getJson();
                String zwmc = ryJson.getString("zwmc").trim();
                if (zwmc.length() > 0) {
                    zwmc = "(" + zwmc + ")";
                }
                node.setText(node.getText() + zwmc);
                String dwid = node.getDwid();
                String bmid = node.getBmid();
                String szk = node.getSzk();
                HashMap<String, HashMap<String, List<Node>>> maps = new HashMap<>();
                if (ryMap.get(szk) != null)
                    maps = ryMap.get(szk);
                HashMap<String, List<Node>> mapDws = new HashMap<>();
                if (maps.get(dwid) != null)
                    mapDws = maps.get(dwid);
                List<Node> ryLists = new ArrayList<Node>();
                if (mapDws.get(bmid) != null)
                    ryLists = mapDws.get(bmid);
                ryLists.add(node);
                mapDws.put(bmid, ryLists);
                maps.put(dwid, mapDws);
                ryMap.put(szk, maps);
            }
            JSONArray dw_array = json.getJSONArray("dw_data");
            List<Node> dwList = getListNode(dw_array, new String[]{"id", "mc", "wzh", null});
            Collections.sort(dwList);
            dwList = getAdapterRoot(dwList).getChildren();
            JSONArray bm_array = json.getJSONArray("bm_data");
            List<Node> bmList = getListNode(bm_array, new String[]{"id", "mc", "wzh", "fid", "dwid"});
//            bmList = getAdapterRoot(bmList).getChildren();
            bmMap = new HashMap<>();
            for (int i = 0; i < bmList.size(); i++) {
                Node node = bmList.get(i);
                String dwid = node.getDwid();
                String szk = node.getSzk();
                List<Node> bmNodes = new ArrayList<Node>();
                HashMap<String, List<Node>> mapSzks = new HashMap<>();
                if (bmMap.get(szk) != null)
                    mapSzks = bmMap.get(szk);
//                if (ryMap.get(dwid) != null &&
//                        ryMap.get(dwid).get(node.getValue()) != null) {
//                    node.setChildren(ryMap.get(dwid).get(node.getValue()));
//                }
                if (mapSzks.get(dwid) != null)
                    bmNodes = mapSzks.get(dwid);
                if (ryMap.get(szk).get(dwid) != null) {
                    List<Node> ryNodes = ryMap.get(szk).get(dwid).get(node.getValue());
                    if (ryNodes != null) {
                        node.setChildrenSize(ryNodes.size());
                    } else {
//                        System.out.println("iGem:" + node.getText() + "setChildrenSize:0");
                        node.setChildrenSize(0);
                    }
                }

                bmNodes.add(node);
                mapSzks.put(dwid, bmNodes);
                bmMap.put(szk, mapSzks);
            }

            for (String szk : bmMap.keySet()) {
                HashMap<String, List<Node>> valueMap = bmMap.get(szk);
                for (String dwid : valueMap.keySet()) {
                    List<Node> list = valueMap.get(dwid);
                    if (list != null) {
                        Collections.sort(list);
                        list = getAdapterRoot(list).getChildren();
                    }
                    for (Node node : list) {
                        if (node.getChildrenSize() == 0 && node.getChildren() != null) {
                            node.setChildrenSize(node.getChildren().size());
//                            System.out.println("iGem:" + node.getText() + ":node.getChildren().size():" + node.getChildren().size());
                        }
                    }
                    bmMap.get(szk).put(dwid, list);
                }
            }
            rootNode = new Node("", "-1");
            List<Node> dwNodes = new ArrayList<>();
            dwNodes.addAll(dwList);
            rootNode.setChildren(dwNodes);

            if (rootNode == null || rootNode.getChildren() == null || rootNode.getChildren().size() == 0) {
                dwcollection.setVisibility(View.INVISIBLE);
            } else {
                dwcollection.setVisibility(View.VISIBLE);
            }

            List<Node> bars = new ArrayList<>();
            bars.addAll(dwList);
            baradapter.getBars().add(new TreeBarBean("单位", bars, treeSize));
            baradapter.notifyItemInserted(0);
            TreeDanWeiAdapter danWeiAdapter = (TreeDanWeiAdapter) dwcollection.getAdapter();
            Node dwNode = new Node("", "-1");
            dwNode.setChildren(dwList);
            if (danWeiAdapter == null) {
                danWeiAdapter = new TreeDanWeiAdapter(getContext(), dwNode);
                danWeiAdapter.setHasCheckBox(true);
                danWeiAdapter.setCheckNode(true);
                dwcollection.setAdapter(danWeiAdapter);
            } else {
                danWeiAdapter.setRoot(dwNode);
                danWeiAdapter.setHasCheckBox(true);
                danWeiAdapter.setCheckNode(true);
                danWeiAdapter.notifyDataSetChanged();
            }
            dwcollection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    TreeDanWeiAdapter danWeiAdapter = (TreeDanWeiAdapter) dwcollection.getAdapter();
                    Node node = (Node) danWeiAdapter.getItem(i);
                    Node root = new Node("", "-1");
                    TreeBarBean barBean = new TreeBarBean();
                    barBean.setText(node.getText());
                    List<Node> nodes = new ArrayList<>();
                    List<Node> bars = new ArrayList<>();
                    List<Node> nrNodes = new ArrayList<>();
                    int isRY = 0;
                    if (node.isRy()) {
                        isRY = 1;
                    } else if (node.getChildren() != null && node.getChildren().size() > 0) {
                        nrNodes = node.getChildren();
                        try {
                            List<Node> ryNodes = ryMap.get(node.getSzk()).get(node.getDwid()).get(node.getValue());
                            if (ryNodes != null && ryNodes.size() > 0) {
                                if (!nrNodes.contains(ryNodes.get(0))) {
                                    if (nrNodes != null)
                                        Collections.sort(ryNodes);
                                    nrNodes.addAll(0, ryNodes);
                                    nrNodes.add(ryNodes.size(), new Node("进入人员列表", "-9999"));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
//                            System.out.println("iGem:" + e.getMessage());
                        }
                    } else if (treeSize == 0) {
                        //从单位进入部门。
                        treeSize = 1;
                        nrNodes = bmMap.get(node.getSzk()).get(node.getValue());
//                        if (bmNodes != null) {
//                            Collections.sort(bmNodes);
//                            bmNodes = getAdapterRoot(bmNodes).getChildren();
//                        }

                    } else if (treeSize == 1) {
                        //从部门进入人员
                        treeSize = 2;
                        List<Node> allNrNodes = ryMap.get(node.getSzk()).get(node.getDwid()).get(node.getValue());
                        for (int j = 0; j < allNrNodes.size(); j++) {
                            try {
                                users().get(Integer.parseInt(allNrNodes.get(j).getValue()));
                                nrNodes.add(allNrNodes.get(j));
                            } catch (RuntimeException e) {

                            }
                        }
                        if (nrNodes != null) {
                            Collections.sort(nrNodes);
                        } else {
                            nrNodes = new ArrayList<>();
                        }
                    }

                    if (isRY == 1) {
                        int id = Integer.parseInt(node.getValue());
                        if (node.isChecked()) {
                            for (int j = 0; j < userIds.size(); j++) {
                                if (userIds.get(j) == id) {
                                    userIds.remove(j);
                                    break;
                                }
                            }
                            node.setChecked(false);
                        } else {
                            node.setChecked(true);
                            userIds.add(id);
                        }
                        danWeiAdapter.notifyDataSetChanged();
                        updateEditText();
//                        onItemClicked(Integer.parseInt(node.getValue()));
                    } else {
                        nodeSelect(nrNodes);
                        nodes.addAll(nrNodes);
                        bars.addAll(nrNodes);
                        barBean.setNodes(bars);
                        root.setChildren(nodes);
                        danWeiAdapter.setRoot(root);
                        danWeiAdapter.notifyDataSetChanged();
                        dwcollection.setSelection(0);
                        topBarChanged(barBean, treeSize);
                    }
                }
            });
            dwcollection.setVisibility(View.VISIBLE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 根据一个jsonArray生成一个无关系的list<Node>
    public static List<Node> getListNode(JSONArray jsonArray, String[] jsonName) {
        List<Node> nodes = new ArrayList<Node>();
        for (int i = 0; i < jsonArray.length(); i++)// 第一层节点数据
        {
            try {
                JSONObject jo = jsonArray.getJSONObject(i);
                Node node = new Node(jo.getString(jsonName[1]).trim(), jo
                        .getString(jsonName[0]).trim());
                // 顺序依次为id，mc，wzh，父id
                if (jsonName[2] != null) {
                    String wzh = jo.getString(jsonName[2]).trim();
                    if ("".equals(wzh)) {
                        wzh = "9999";
                    } else if (wzh == null) {
                        wzh = "9999";
                    }
                    node.setWzh(wzh);
                }
                if (jsonName[3] != null) {
                    node.setFid(jo.getString(jsonName[3]).trim());
                }
                if (jsonName.length > 4) {
                    node.setDwid(jo.getString(jsonName[4]).trim());
                }
                if (jsonName.length > 5) {
                    node.setBmid(jo.getString(jsonName[5]).trim());
                }
//                if (jsonName.length > 6) {
                node.setSzk("ZGGF");
//                }
                node.setJson(jo);
                nodes.add(node);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }
        return nodes;
    }

    // 根据得到的list<Node>，在处理父子关系
    public Node getAdapterRoot(List<Node> nodes) {
        Node root = new Node("", "-1");
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            if (node.getFid() == null || "-1".equals(node.getFid())) {
                node.setFid("-1");
                node.setParent(root);// 设置父节点
                root.add(node);
            } else {
                for (int j = 0; j < nodes.size(); j++) {
                    Node nodePar = nodes.get(j);
                    if (nodePar.getValue().equals(node.getFid())) {
                        node.setParent(nodePar);// 设置父节点
                        nodePar.add(node);
                        break;
                    }
                }
            }

//            if (node.getChildrenSize() == 0 && node.getChildren() != null) {
//                node.setChildrenSize(node.getChildren().size());
////                System.out.println("iGem:" + node.getText() + ":node.getChildren().size():" + node.getChildren().size());
//            }
        }


        return root;
    }

    private void topBarChanged(TreeBarBean bar, int treeSize) {
        bar.setTreeSize(treeSize);
        List<TreeBarBean> bars = baradapter.getBars();
        bars.add(bar);
        baradapter.notifyItemChanged(bars.size() - 2);
        baradapter.notifyItemInserted(bars.size() - 1);
        topBarRecyclerView.smoothScrollToPosition(bars.size() - 1);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.done) {
            if (userIds.size() > 0) {
                if (getActivity() instanceof SimpleCreateGroupActivity) {
                    SimpleCreateGroupActivity activity = (SimpleCreateGroupActivity) getActivity();
                    String title = activity.getGroupTitle();
                    if (title.length() > 0) {
                        execute(messenger().createGroup(title, null, BoxUtil.unbox(getSelected())).then(gid -> {
                            if (ChatToolbarFragment.chatActivity != null) {
                                ChatToolbarFragment.chatActivity.finish();
                            }
                            getActivity().startActivity(Intents.openGroupDialog(gid, true, getActivity()));
                            finishActivity();
//                                Intent intent = activity.getIntent();
//                                intent.putExtra("groupId", gid);
//                                getActivity().setResult(activity.RESULT_OK, intent);
                        }).failure(e -> {
                            Toast.makeText(getActivity(), getString(R.string.toast_unable_create_group),
                                    Toast.LENGTH_LONG).show();
                        }));
                    } else {
                        Toast.makeText(getContext(), "请输入群组名称", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    execute(messenger().createGroup(title, avatarPath, BoxUtil.unbox(getSelected())).then(gid -> {
                        getActivity().startActivity(Intents.openGroupDialog(gid, true, getActivity()));
                        getActivity().finish();
                    }).failure(e -> {
                        Toast.makeText(getActivity(), getString(R.string.toast_unable_create_group),
                                Toast.LENGTH_LONG).show();
                    }));
                }
            } else {
                Toast.makeText(getActivity(), "请选择群组人员",
                        Toast.LENGTH_LONG).show();

            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void nodeSelect(List<Node> node) {
        for (int i = 0; i < node.size(); i++) {
            if (node.get(i).isRy() && userIds.contains(Integer.parseInt(node.get(i).getValue()))) {
                node.get(i).setChecked(true);
            }
        }
    }

    private Integer[] getSelected() {
        Integer[] ids = new Integer[userIds.size()];
        return userIds.toArray(ids);
    }

}
