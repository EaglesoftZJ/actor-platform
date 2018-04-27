package im.actor.core;

import com.google.j2objc.annotations.ObjectiveCName;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import im.actor.core.entity.Contact;
import im.actor.core.entity.Dialog;
import im.actor.core.entity.Message;
import im.actor.core.entity.Peer;
import im.actor.core.entity.SearchEntity;
import im.actor.core.viewmodel.GroupAllGetCallback;
import im.actor.core.viewmodel.GroupVM;
import im.actor.core.viewmodel.MessageXzrz;
import im.actor.core.viewmodel.MessageXzrzCallBack;
import im.actor.core.viewmodel.WebServiceRunCallBack;
import im.actor.runtime.Runtime;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.runtime.json.JSONArray;
import im.actor.runtime.json.JSONException;
import im.actor.runtime.json.JSONObject;
import im.actor.runtime.threading.ThreadDispatcher;

public class CocoaMessenger extends Messenger {

    private BindedDisplayList<Dialog> dialogList;
    private HashMap<Peer, BindedDisplayList<Message>> messagesLists = new HashMap<Peer, BindedDisplayList<Message>>();

    /**
     * Construct messenger
     *
     * @param configuration configuration of messenger
     */
    @ObjectiveCName("initWithConfiguration:")
    public CocoaMessenger(@NotNull Configuration configuration) {
        super(configuration);

        ThreadDispatcher.pushDispatcher(Runtime::postToMainThread);
    }

    @ObjectiveCName("getDialogsDisplayList")
    public BindedDisplayList<Dialog> getDialogsDisplayList() {
        if (dialogList == null) {
            dialogList = (BindedDisplayList<Dialog>) modules.getDisplayListsModule().getDialogsSharedList();
            dialogList.setBindHook(new BindedDisplayList.BindHook<Dialog>() {
                @Override
                public void onScrolledToEnd() {
                    modules.getMessagesModule().loadMoreDialogs();
                }

                @Override
                public void onItemTouched(Dialog item) {

                }
            });
        }

        return dialogList;
    }

    @ObjectiveCName("getMessageDisplayList:")
    public BindedDisplayList<Message> getMessageDisplayList(final Peer peer) {
        if (!messagesLists.containsKey(peer)) {
            BindedDisplayList<Message> list = (BindedDisplayList<Message>) modules.getDisplayListsModule().getMessagesSharedList(peer);
            list.setBindHook(new BindedDisplayList.BindHook<Message>() {
                @Override
                public void onScrolledToEnd() {
                    modules.getMessagesModule().loadMoreHistory(peer);
                }

                @Override
                public void onItemTouched(Message item) {

                }
            });
            messagesLists.put(peer, list);
        }

        return messagesLists.get(peer);
    }

    @ObjectiveCName("buildSearchDisplayList")
    public BindedDisplayList<SearchEntity> buildSearchDisplayList() {
        return (BindedDisplayList<SearchEntity>) modules.getDisplayListsModule().buildSearchList(false);
    }

    @ObjectiveCName("buildContactsDisplayList")
    public BindedDisplayList<Contact> buildContactsDisplayList() {
        return (BindedDisplayList<Contact>) modules.getDisplayListsModule().buildContactList(false);
    }


    /**
     * @param uid 登录人ID
     */
    @ObjectiveCName("getGroupAllWithIp:withUid:withCallback:")
    public void getGroupAll(final String ip, final long uid, GroupAllGetCallback callback) {
        webServiceRun(ip, "queryGroup", "id", uid + "", new WebServiceRunCallBack() {
            @Override
            public void webSrviceResCallBack(String str) {
                JSONArray array = null;
                List<GroupVM> groupVMS = new ArrayList<>();
                try {
                    if (str != null) {
                        array = new JSONArray(str);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject json = array.getJSONObject(i);
                            GroupVM vm = null;
                            try {
                                vm = getGroups().get(json.getInt("id"));
                                groupVMS.add(vm);
                            } catch (Exception e) {
                                String title = json.getString("title");
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    callback.responseCallBack(groupVMS);
                }
            }
        });

    }


    @ObjectiveCName("getXzrzWithIp:withMessageid:withCallback:")
    public void getXzrz(final String ip, final long messageid, MessageXzrzCallBack callback) {
        //服务的地址
        webServiceRun(ip, "selectXzrz", "messageId", messageid + "", new WebServiceRunCallBack() {
            @Override
            public void webSrviceResCallBack(String str) {
                List<MessageXzrz> xzrzs = new ArrayList<>();
                if (str != null) {
                    try {
                        JSONArray array = new JSONArray(str);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject json = array.getJSONObject(i);
                            MessageXzrz xzrz = new MessageXzrz();
                            xzrz.setMessageId(json.getLong("messageId"));
                            xzrz.setUserId(json.getLong("userId"));
                            xzrz.setUserName(json.getString("userName"));
                            xzrz.setTime(json.getString("time"));
                            xzrzs.add(xzrz);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                callback.queryResponseCallBack(xzrzs);
            }
        });
    }


    /**
     * @param ip
     * @param jsonStr  {"messageId":-7275888453393723629,"userId":2092017244,"userName":"来啊"}
     * @param callback
     */
    @ObjectiveCName("saveXzrzWithIp:withJsonStr:withCallback:")
    public void saveXzrz(final String ip, final String jsonStr, MessageXzrzCallBack callback) {
        //服务的地址
        webServiceRun(ip, "insertXzrz", "json", jsonStr, new WebServiceRunCallBack() {
            @Override
            public void webSrviceResCallBack(String str) {
                callback.saveResponseCallBack(str);
            }
        });
    }

    private void webServiceRun(final String ip, String method, String key, String value, WebServiceRunCallBack callBack) {
        HashMap<String, String> par = new HashMap<>();
        par.put(key, value);
        webServiceRun(ip, method, par, callBack);
    }

    private void webServiceRun(final String ip, String method,
                               HashMap<String, String> par, WebServiceRunCallBack callBack) {
        im.actor.runtime.Runtime.dispatch(() -> {
            String s = null;
            try {

                URL wsUrl = new URL(ip);

                HttpURLConnection conn = (HttpURLConnection) wsUrl.openConnection();

                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
                conn.setRequestProperty("SOAPAction", "http://eaglesoft/" + method);

                conn.setRequestProperty("Connection", "close");
                conn.setRequestProperty("Accept-Encoding", "gzip");
                //请求体
                String soap = "<v:Envelope xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                        "xmlns:d=\"http://www.w3.org/2001/XMLSchema\" " +
                        "xmlns:c=\"http://schemas.xmlsoap.org/soap/encoding/\" " +
                        "xmlns:v=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                        "<v:Header /><v:Body>" +
                        "<n0:" + method + " id=\"o0\" " +
                        "c:root=\"1\" xmlns:n0=\"http://eaglesoft\">";
//                    "<id i:type=\"d:string\">" + messageid + "</id>"
                Iterator iter = par.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String key = (String) entry.getKey();
                    String value = (String) entry.getValue();
                    soap += "<" + key + " i:type=\"d:string\">" + value + "</" + key + ">";
                }
                soap += "</n0:" + method + "></v:Body></v:Envelope>";

                conn.setRequestProperty("Content-Length", "" + soap.getBytes().length);
                OutputStream os = conn.getOutputStream();
                os.write(soap.getBytes(), 0, soap.getBytes().length);
                InputStream is = conn.getInputStream();
                if (conn.getResponseCode() == 200) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buf = new byte[256];

                    while (true) {
                        int rd = ((InputStream) is).read(buf, 0, 256);
                        if (rd == -1) {
                            bos.flush();
                            buf = bos.toByteArray();
                            ((InputStream) is).close();
                            is = new ByteArrayInputStream(buf);
                            break;
                        }

                        bos.write(buf, 0, rd);
                    }

                    s = new String(bos.toByteArray(), "UTF-8");
                    String resultName = "return";
                    String[] strs = s.split("<" + resultName + ">");
                    String[] strs2 = strs[1].split("</" + resultName + ">");
                    s = strs2[0];
                    os.close();
                    conn.disconnect();
                }

            } catch (Exception e) {
                System.out.println("iGem:" + e.getMessage());
                e.printStackTrace();
            } finally {
                callBack.webSrviceResCallBack(s);
            }
        });

    }
}
