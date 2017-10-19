package im.actor.sdk.controllers.root;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.FrameLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import im.actor.core.entity.Contact;
import im.actor.core.viewmodel.CommandCallback;
import im.actor.runtime.android.view.BindedListAdapter;
import im.actor.runtime.mvvm.Value;
import im.actor.runtime.mvvm.ValueChangedListener;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.controllers.BaseFragment;
import im.actor.sdk.controllers.Intents;
import im.actor.sdk.controllers.compose.CreateGroupActivity;
import im.actor.sdk.controllers.contacts.BaseContactFragment;
import im.actor.sdk.controllers.root.ZzjgAdapter.ZzjgAdapter;
import im.actor.sdk.controllers.root.ZzjgAdapter.ZzjgHolder;
import im.actor.sdk.intents.WebServiceUtil;
import im.actor.sdk.view.adapters.OnItemClickedListener;

import static im.actor.sdk.util.ActorSDKMessenger.messenger;

/**
 * Created by huchengjie on 2017/9/21.
 */

public class RootZzjgFragment extends Fragment {

    private Node rootNode;
    private View emptyView;

    RecyclerView collection;

    public RootZzjgFragment() {
//        if (rootNode == null) {
//            ZzjgData();
//        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return onCreateContactsView(R.layout.fragment_base_contacts, inflater, container, savedInstanceState);
    }
    public RecyclerView.Adapter adapter;
    protected View onCreateContactsView(int layoutId, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //生成Adapter
        View res = inflater.inflate(layoutId, container, false);
        collection = (RecyclerView) res.findViewById(R.id.collection);
        if (rootNode == null) {
            ZzjgData();
        }

//        View res = inflate(inflater, container, layoutId, messenger().buildContactsDisplayList());
        res.findViewById(R.id.collection).setBackgroundColor(ActorSDK.sharedActor().style.getMainBackgroundColor());
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

        setAnimationsEnabled(false);

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
        res.setBackgroundColor(ActorSDK.sharedActor().style.getMainBackgroundColor());

        return res;
    }
    protected RecyclerView.Adapter onCreateAdapter(Node node, Activity activity) {
        return new ZzjgAdapter(rootNode, activity, false, new OnItemClickedListener<Contact>() {
            @Override
            public void onClicked(Contact item) {
                onItemClicked(item);
            }

            @Override
            public boolean onLongClicked(Contact item) {
                return onItemLongClicked(item);
            }
        });
    }
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
            DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
            // CustomItemAnimator itemAnimator = new CustomItemAnimator();
            itemAnimator.setSupportsChangeAnimations(false);
            itemAnimator.setMoveDuration(200);
            itemAnimator.setAddDuration(150);
            itemAnimator.setRemoveDuration(200);
            collection.setItemAnimator(itemAnimator);
        } else {
            collection.setItemAnimator(null);
        }
    }

    private void ZzjgData() {
        WebServiceUtil.webServiceRun("http://220.189.207.21:8776", new HashMap<String, String>(),
                "GetAllUserFullData", getContext(), new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        Bundle b = msg.getData();
                        String datasource = b.getString("datasource");
//                        System.out.println(datasource);
                        try {
                            JSONObject json = new JSONObject(datasource);

                            JSONArray yh_array = json.getJSONArray("yh_data");
                            List<Node> yhList = getListNode(yh_array, new String[]{"id", "xm", "wzh", "fid", "dwid", "bmid"});
                            yhList = getAdapterRoot(yhList).getChildren();
                            HashMap<String, HashMap<String, List<Node>>> ryMap = new HashMap<String, HashMap<String, List<Node>>>();

//                            yhList.stream().forEach(node -> {
//                                String dwid = node.getDwid();
//                                String bmid = node.getBmid();
//                                HashMap<String, List<Node>> maps = new HashMap<String, List<Node>>();
//                                if (ryMap.get(dwid) != null)
//                                    maps = ryMap.get(dwid);
//                                List<Node> ryLists = new ArrayList<Node>();
//                                if (maps.get(bmid) != null)
//                                    ryLists = maps.get(bmid);
//                                ryLists.add(node);
//                            });

                            JSONArray dw_array = json.getJSONArray("dw_data");
                            List<Node> dwList = getListNode(dw_array, new String[]{"id", "mc", "wzh", null});
                            dwList = getAdapterRoot(dwList).getChildren();

                            JSONArray bm_array = json.getJSONArray("bm_data");
                            List<Node> bmList = getListNode(bm_array, new String[]{"id", "mc", "wzh", "fid", "dwid"});
                            bmList = getAdapterRoot(bmList).getChildren();
                            HashMap<String, List<Node>> bmMap = new HashMap<String, List<Node>>();

//                            bmList.stream().forEach(node -> {
//                                String dwid = node.getDwid();
//                                List<Node> bmNodes = new ArrayList<Node>();
//                                if (bmMap.get(dwid) != null)
//                                    bmNodes = bmMap.get(dwid);
//                                if (ryMap.get(dwid) != null &&
//                                        ryMap.get(dwid).get(node.getValue()) != null) {
//                                    node.setChildren(ryMap.get(dwid).get(node.getValue()));
//                                }
//                                bmNodes.add(node);
//                                bmMap.put(node.getDwid(), bmNodes);
//                            });
//                            dwList.stream().forEach(node -> {
//                                String dwid = node.getValue();
//                                node.setChildren(bmMap.get(dwid));
//                            });
                            rootNode = new Node("", "-1");
                            rootNode.setChildren(dwList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }finally {
                            if (rootNode == null||rootNode.getChildren()==null||rootNode.getChildren().size() == 0) {
                                collection.setVisibility(View.INVISIBLE);
                            } else {
                                collection.setVisibility(View.VISIBLE);
                            }
                            setAnimationsEnabled(true);
                            configureRecyclerView(collection);
                            adapter = onCreateAdapter(rootNode, getActivity());
                            collection.setAdapter(adapter);
                        }
                        return false;
                    }
                }));
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
                    if("".equals(wzh)){
                        wzh = "0";
                    }
                    node.setWzh(Integer.parseInt(wzh));
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


    public void onItemClicked(Contact contact) {
        getActivity().startActivity(Intents.openPrivateDialog(contact.getUid(), true, getActivity()));
    }

    public boolean onItemLongClicked(final Contact contact) {
        new AlertDialog.Builder(getActivity())
                .setItems(new CharSequence[]{
                        getString(R.string.contacts_menu_remove).replace("{0}", contact.getName()),
                        getString(R.string.contacts_menu_edit),
                }, (dialog, which) -> {
                    if (which == 0) {
                        new AlertDialog.Builder(getActivity())
                                .setMessage(getString(R.string.alert_remove_contact_text).replace("{0}", contact.getName()))
                                .setPositiveButton(R.string.alert_remove_contact_yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                        execute(messenger().removeContact(contact.getUid()), R.string.contacts_menu_remove_progress, new CommandCallback<Boolean>() {
//                                            @Override
//                                            public void onResult(Boolean res) {
//
//                                            }
//
//                                            @Override
//                                            public void onError(Exception e) {
//
//                                            }
//                                        });
                                    }
                                })
                                .setNegativeButton(R.string.dialog_cancel, null)
                                .show()
                                .setCanceledOnTouchOutside(true);
                    } else if (which == 1) {
                        startActivity(Intents.editUserName(contact.getUid(), getActivity()));
                    }
                })
                .show()
                .setCanceledOnTouchOutside(true);
        return true;
    }
}
