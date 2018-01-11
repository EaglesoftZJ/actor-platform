package im.actor.sdk.controllers.root;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

import im.actor.core.entity.SearchEntity;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.controllers.BaseFragment;
import im.actor.sdk.controllers.Intents;
import im.actor.sdk.controllers.zuzhijiagou.TreeDanWeiAdapter;

import static im.actor.sdk.util.ActorSDKMessenger.groups;
import static im.actor.sdk.util.ActorSDKMessenger.messenger;
import static java.util.Collections.*;

/**
 * Created by huchengjie on 2017/9/21.
 */

public class RootZzjgFragment extends BaseFragment {

    private Node rootNode;
    private View emptyView;

    ListView dwcollection;
    //    ListView bmcollection;
//    ListView rycollection;
//    TextView zuZhiTitleText;
//    ImageView backImage;
    LinearLayout zuzhijiagou_lay;

    HashMap<String, HashMap<String, List<Node>>> bmMap = new HashMap<String, HashMap<String, List<Node>>>();
    HashMap<String, HashMap<String, HashMap<String, List<Node>>>> ryMap = new HashMap<>();

    public RootZzjgFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return onCreateContactsView(R.layout.fragment_base_zuzhijiagou, inflater, container, savedInstanceState);
    }

    public RecyclerView.Adapter adapter;

    protected View onCreateContactsView(int layoutId, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View res = inflater.inflate(layoutId, container, false);
        dwcollection = (ListView) res.findViewById(R.id.zuzhijiagou_dw);
//        bmcollection = (ListView) res.findViewById(R.id.zuzhijiagou_bm);
//        rycollection = (ListView) res.findViewById(R.id.zuzhijiagou_ry);
//        zuZhiTitleText = (TextView) res.findViewById(R.id.zuzhijiagou_text);
//        backImage = (ImageView) res.findViewById(R.id.zuzhijiagou_back);
        zuzhijiagou_lay = (LinearLayout) res.findViewById(R.id.zuzhijiagou_title_lay);
//        if (rootNode == null) {
        ZzjgData(ActorSDK.getZjjgData());
//        }
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

//        setAnimationsEnabled(false);

//        View headerPadding = new View(getActivity());
//        headerPadding.setBackgroundColor(ActorSDK.sharedActor().style.getMainBackgroundColor());
//        headerPadding.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, useCompactVersion ? 0 : ActorSDK.sharedActor().style.getContactsMainPaddingTop()));
//        addHeaderView(headerPadding);
//
//        addFootersAndHeaders();

        if (emptyView != null) {
            if (messenger().getAppState().getIsContactsEmpty().get()) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
        }
//        bind(messenger().getAppState().getIsContactsEmpty(), new ValueChangedListener<Boolean>() {
//            @Override
//            public void onChanged(Boolean val, Value<Boolean> Value) {
//                if (emptyView != null) {
//                    if (val) {
//                        emptyView.setVisibility(View.VISIBLE);
//                    } else {
//                        emptyView.setVisibility(View.GONE);
//                    }
//                }
//            }
//        });
        emptyView.setVisibility(View.GONE);
        res.setBackgroundColor(ActorSDK.sharedActor().style.getMainBackgroundColor());

        return res;
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
//            DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
//            // CustomItemAnimator itemAnimator = new CustomItemAnimator();
//            itemAnimator.setSupportsChangeAnimations(false);
//            itemAnimator.setMoveDuration(200);
//            itemAnimator.setAddDuration(150);
//            itemAnimator.setRemoveDuration(200);
//            dwcollection.setItemAnimator(itemAnimator);
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
            zuzhijiagou_lay.setVisibility(View.GONE);
//            backImage.setVisibility(View.GONE);
            yh_array = json.getJSONArray("yh_data");
            List<Node> yhList = getListNode(yh_array, new String[]{"IGIMID", "xm", "wzh", "fid", "dwid", "bmid"});
//            yhList = getAdapterRoot(yhList).getChildren();
            ryMap = new HashMap<>();
            for (int i = 0; i < yhList.size(); i++) {
                Node node = yhList.get(i);
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
                        node.setChildrenSize(0);
                    }
                } else {
                    node.setChildrenSize(0);
                }

                bmNodes.add(node);
                mapSzks.put(dwid, bmNodes);
                bmMap.put(szk, mapSzks);
            }
//            for (int i = 0; i < dwList.size(); i++) {
//                Node node = dwList.get(i);
//                String dwid = node.getValue();
//                node.setChildren(bmMap.get(dwid));
//            }
            rootNode = new Node("", "-1");
            rootNode.setChildren(dwList);

            if (rootNode == null || rootNode.getChildren() == null || rootNode.getChildren().size() == 0) {
                dwcollection.setVisibility(View.INVISIBLE);
            } else {
                dwcollection.setVisibility(View.VISIBLE);
            }
//            setAnimationsEnabled(true);
//            configureRecyclerView(dwcollection);
//            adapter = new CommonAdapter
//            adapter = onCreateAdapter(rootNode, getActivity());
            TreeDanWeiAdapter danWeiAdapter = (TreeDanWeiAdapter) dwcollection.getAdapter();
            Node dwNode = new Node("", "-1");
            dwNode.setChildren(dwList);
            if (danWeiAdapter == null) {
                danWeiAdapter = new TreeDanWeiAdapter(getContext(), dwNode);
                dwcollection.setAdapter(danWeiAdapter);
            } else {
                danWeiAdapter.setRoot(dwNode);
                danWeiAdapter.notifyDataSetChanged();
            }
//            dwcollection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                    TreeDanWeiAdapter danWeiAdapter = (TreeDanWeiAdapter) dwcollection.getAdapter();
//                    Node node = (Node) danWeiAdapter.getItem(i);
//                    dwcollection.setVisibility(View.GONE);
////                    zuZhiTitleText.setText(node.getText());
//                    Node bmNode = new Node("", "-1");
//                    List<Node> bmNiodes = bmMap.get(node.getSzk()).get(node.getValue());
//                    if (bmNiodes != null) {
//                        Collections.sort(bmNiodes);
//                        bmNiodes = getAdapterRoot(bmNiodes).getChildren();
//                    }
//                    bmNode.setChildren(bmNiodes);
//                    TreeDanWeiAdapter bmAdapter = (TreeDanWeiAdapter) bmcollection.getAdapter();
//                    if (bmAdapter == null) {
//                        bmAdapter = new TreeDanWeiAdapter(getContext(), bmNode);
//                        bmcollection.setAdapter(bmAdapter);
//                    } else {
//                        bmAdapter.setRoot(bmNode);
//                        bmAdapter.notifyDataSetChanged();
//                    }
////                    backImage.setVisibility(View.VISIBLE);
//                    bmcollection.setVisibility(View.VISIBLE);
//                    zuzhijiagou_lay.setVisibility(View.VISIBLE);
//                }
//            });
            dwcollection.setVisibility(View.VISIBLE);
//            bmcollection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                    TreeDanWeiAdapter danWeiAdapter = (TreeDanWeiAdapter) bmcollection.getAdapter();
//                    Node node = (Node) danWeiAdapter.getItem(i);
////                    String dwText = zuZhiTitleText.getText().toString();
////                    zuZhiTitleText.setText(dwText + "-" + node.getText());
//                    List<Node> ryNodes = ryMap.get(node.getSzk()).get(node.getDwid()).get(node.getValue());
//                    if (ryNodes != null) {
//                        Collections.sort(ryNodes);
////                        ryNodes = getAdapterRoot(ryNodes).getChildren();
//                    }
////                    bmcollection.setVisibility(View.GONE);
//                    Node ryNode = new Node("", "-1");
//                    ryNode.setChildren(ryNodes);
//                    TreeDanWeiAdapter ryAdapter = (TreeDanWeiAdapter) rycollection.getAdapter();
//                    if (ryAdapter == null) {
//                        ryAdapter = new TreeDanWeiAdapter(getContext(), ryNode);
////                        rycollection.setAdapter(ryAdapter);
//                    } else {
//                        ryAdapter.setRoot(ryNode);
//                        ryAdapter.notifyDataSetChanged();
//                    }
////                    rycollection.setVisibility(View.VISIBLE);
//                }
//            });

//            rycollection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                    TreeDanWeiAdapter danWeiAdapter = (TreeDanWeiAdapter) rycollection.getAdapter();
//                    Node node = (Node) danWeiAdapter.getItem(i);
//                    onItemClicked(Integer.parseInt(node.getValue()));
//                }
//            });


//            backImage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if(bmcollection.isShown()){
//                        bmcollection.setVisibility(View.GONE);
//                        dwcollection.setVisibility(View.VISIBLE);
//                        zuzhijiagou_lay.setVisibility(View.GONE);
//                    }else if(rycollection.isShown()){
//                        rycollection.setVisibility(View.GONE);
//                        bmcollection.setVisibility(View.VISIBLE);
////                        String title = zuZhiTitleText.getText().toString();
////                        zuZhiTitleText.setText(title.split("-")[0]);
//                    }
//                }
//            });
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
                node.setSzk(jo.getString("szk").trim());
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
    public static Node getAdapterRoot(List<Node> nodes) {
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
        }
        return root;
    }


    public void onItemClicked(int uid) {
        getActivity().startActivity(Intents.openPrivateDialog(uid, true, getActivity()));
    }

//    public boolean onItemLongClicked(final Contact contact) {
//        new AlertDialog.Builder(getActivity())
//                .setItems(new CharSequence[]{
//                        getString(R.string.contacts_menu_remove).replace("{0}", contact.getName()),
//                        getString(R.string.contacts_menu_edit),
//                }, (dialog, which) -> {
//                    if (which == 0) {
//                        new AlertDialog.Builder(getActivity())
//                                .setMessage(getString(R.string.alert_remove_contact_text).replace("{0}", contact.getName()))
//                                .setPositiveButton(R.string.alert_remove_contact_yes, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
////                                        execute(messenger().removeContact(contact.getUid()), R.string.contacts_menu_remove_progress, new CommandCallback<Boolean>() {
////                                            @Override
////                                            public void onResult(Boolean res) {
////
////                                            }
////
////                                            @Override
////                                            public void onError(Exception e) {
////
////                                            }
////                                        });
//                                    }
//                                })
//                                .setNegativeButton(R.string.dialog_cancel, null)
//                                .show()
//                                .setCanceledOnTouchOutside(true);
//                    } else if (which == 1) {
//                        startActivity(Intents.editUserName(contact.getUid(), getActivity()));
//                    }
//                })
//                .show()
//                .setCanceledOnTouchOutside(true);
//        return true;
//    }


//    public class DWAdapter extends RecyclerView.Adapter<DWAdapter.ViewHolder> {
//        public List<Node> datas = null;
//        int flag;//0表示单位，1表示部门，2表示人员
//
//        public DWAdapter(List<Node> datas, int flag) {
//            this.datas = datas;
//            this.flag = flag;
//        }
//
//        //创建新View，被LayoutManager所调用
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//            View view = LayoutInflater.from(viewGroup.getContext()).inflate(android.R.layout.simple_list_item_activated_1, viewGroup, false);
//            ViewHolder vh = new ViewHolder(view);
//            return vh;
//        }
//
//        //将数据与界面进行绑定的操作
//        @Override
//        public void onBindViewHolder(ViewHolder viewHolder, int position) {
//            final String text = datas.get(position).getText();
//            viewHolder.mTextView.setText(text);
//            final String dwid = datas.get(position).getValue();
//            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dwcollection.setVisibility(View.GONE);
//                    Node bmNode = new Node("", "-1");
//                    List<Node> bmNiodes = bmMap.get(dwid);
//                    if (bmNiodes != null) {
//                        Collections.sort(bmNiodes);
//                        bmNiodes = getAdapterRoot(bmNiodes).getChildren();
//                    }
//                    bmNode.setChildren(bmNiodes);
//                    TreeDanWeiAdapter bmAdapter = (TreeDanWeiAdapter) bmcollection.getAdapter();
//                    if (bmAdapter == null) {
//                        bmAdapter = new TreeDanWeiAdapter(getContext(), bmNode);
//                        bmcollection.setAdapter(bmAdapter);
//                    } else {
//                        bmAdapter.setRoot(bmNode);
//                        bmAdapter.notifyDataSetChanged();
//                    }
//                    bmcollection.setVisibility(View.VISIBLE);
//                    Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//
//        //获取数据的数量
//        @Override
//        public int getItemCount() {
//            return datas.size();
//        }
//
//        public int getFlag() {
//            return flag;
//        }
//
//        public void setFlag(int flag) {
//            this.flag = flag;
//        }
//
//        //自定义的ViewHolder，持有每个Item的的所有界面元素
//        public class ViewHolder extends RecyclerView.ViewHolder {
//            public TextView mTextView;
//
//            public ViewHolder(View view) {
//                super(view);
//                mTextView = (TextView) view.findViewById(android.R.id.text1);
//            }
//        }
//    }


}
