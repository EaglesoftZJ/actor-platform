package im.actor.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.display.DisplayManager;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.Display;
import android.webkit.MimeTypeMap;


import com.google.j2objc.annotations.ObjectiveCName;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import im.actor.core.entity.Contact;
import im.actor.core.entity.Dialog;
import im.actor.core.entity.Message;
import im.actor.core.entity.Peer;
import im.actor.core.entity.SearchEntity;
import im.actor.core.entity.content.FastThumb;
import im.actor.core.entity.content.JsonContent;
import im.actor.core.network.NetworkState;
import im.actor.core.utils.AppStateActor;
import im.actor.core.utils.IOUtils;
import im.actor.core.utils.ImageHelper;
import im.actor.core.viewmodel.AppStateVM;
import im.actor.core.viewmodel.Command;
import im.actor.core.viewmodel.GalleryVM;
import im.actor.core.viewmodel.GroupAllGetCallback;
import im.actor.core.viewmodel.GroupVM;
import im.actor.core.viewmodel.MessageXzrz;
import im.actor.core.viewmodel.MessageXzrzCallBack;
import im.actor.core.viewmodel.WebServiceRunCallBack;
import im.actor.runtime.Runtime;
import im.actor.runtime.actors.Actor;
import im.actor.runtime.actors.ActorCreator;
import im.actor.runtime.actors.ActorRef;
import im.actor.runtime.actors.ActorSystem;
import im.actor.runtime.actors.Props;
import im.actor.runtime.android.AndroidContext;
import im.actor.runtime.eventbus.EventBus;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.core.utils.GalleryScannerActor;
import im.actor.runtime.json.JSONArray;
import im.actor.runtime.json.JSONException;
import im.actor.runtime.json.JSONObject;
import me.leolin.shortcutbadger.ShortcutBadger;

import static im.actor.runtime.actors.ActorSystem.system;

public class AndroidMessenger extends im.actor.core.Messenger {

    private final Executor fileDownloader = Executors.newSingleThreadExecutor();

    private Context context;
    private final Random random = new Random();
    private ActorRef appStateActor;
    private BindedDisplayList<Dialog> dialogList;
    //    private BindedDisplayList<Sticker> stickersList;
//    private BindedDisplayList<StickerPack> stickerPacksList;
    private HashMap<Peer, BindedDisplayList<Message>> messagesLists = new HashMap<>();
    private HashMap<Peer, BindedDisplayList<Message>> docsLists = new HashMap<>();
    private HashMap<String, BindedDisplayList> customLists = new HashMap<>();
    private GalleryVM galleryVM;
    private ActorRef galleryScannerActor;

    public AndroidMessenger(Context context, im.actor.core.Configuration configuration) {
        super(configuration);

        this.context = context;

        this.appStateActor = system().actorOf("actor/android/state", () -> new AppStateActor(AndroidMessenger.this));

        // Catch all phone book changes
        Runtime.dispatch(() ->
                context.getContentResolver()
                        .registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true,
                                new ContentObserver(null) {
                                    @Override
                                    public void onChange(boolean selfChange) {
                                        onPhoneBookChanged();
                                    }
                                }));

        // Counters
        modules.getConductor()
                .getGlobalStateVM()
                .getGlobalCounter()
                .subscribe((val, valueModel) -> {
                    if (val != null) {
                        ShortcutBadger.with(AndroidContext.getContext()).count(val);
                    }
                });

        // Catch network change
        Runtime.dispatch(() -> context.registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        ConnectivityManager cm =
                                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                        boolean isConnected = activeNetwork != null &&
                                activeNetwork.isConnectedOrConnecting();

                        NetworkState state;
                        if (isConnected) {
                            switch (activeNetwork.getType()) {
                                case ConnectivityManager.TYPE_WIFI:
                                case ConnectivityManager.TYPE_WIMAX:
                                case ConnectivityManager.TYPE_ETHERNET:
                                    state = NetworkState.WI_FI;
                                    break;
                                case ConnectivityManager.TYPE_MOBILE:
                                    state = NetworkState.MOBILE;
                                    break;
                                default:
                                    state = NetworkState.UNKNOWN;
                            }
                        } else {
                            state = NetworkState.NO_CONNECTION;
                        }
                        onNetworkChanged(state);
                    }
                }, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)));


        // Screen change processor
        Runtime.dispatch(() -> {
            IntentFilter screenFilter = new IntentFilter();
            screenFilter.addAction(Intent.ACTION_SCREEN_OFF);
            screenFilter.addAction(Intent.ACTION_SCREEN_ON);
            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                        appStateActor.send(new AppStateActor.OnScreenOn());
                    } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                        appStateActor.send(new AppStateActor.OnScreenOff());
                    }
                }
            }, screenFilter);
            if (isScreenOn()) {
                appStateActor.send(new AppStateActor.OnScreenOn());
            } else {
                appStateActor.send(new AppStateActor.OnScreenOff());
            }
        });

    }

    public Context getContext() {
        return context;
    }

    @Override
    public void changeGroupAvatar(int gid, String descriptor) {
        try {
            Bitmap bmp = ImageHelper.loadOptimizedHQ(descriptor);
            if (bmp == null) {
                return;
            }
            String resultFileName = getExternalTempFile("image", "jpg");
            if (resultFileName == null) {
                return;
            }
            ImageHelper.save(bmp, resultFileName);

            super.changeGroupAvatar(gid, resultFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changeMyAvatar(String descriptor) {
        try {
            Bitmap bmp = ImageHelper.loadOptimizedHQ(descriptor);
            if (bmp == null) {
                return;
            }
            String resultFileName = getExternalTempFile("image", "jpg");
            if (resultFileName == null) {
                return;
            }
            ImageHelper.save(bmp, resultFileName);

            super.changeMyAvatar(resultFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDocument(Peer peer, String fullFilePath) {
        sendDocument(peer, fullFilePath, new File(fullFilePath).getName());
    }

    public void sendDocument(Peer peer, String fullFilePath, String fileName) {

        int dot = fileName.indexOf('.');
        String mimeType = null;
        if (dot >= 0) {
            String ext = fileName.substring(dot + 1);
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        }
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        Bitmap fastThumb = ImageHelper.loadOptimizedHQ(fullFilePath);
        if (fastThumb != null) {
            fastThumb = ImageHelper.scaleFit(fastThumb, 90, 90);
            byte[] fastThumbData = ImageHelper.save(fastThumb);
            sendDocument(peer, fileName, mimeType,
                    new FastThumb(fastThumb.getWidth(), fastThumb.getHeight(), fastThumbData),
                    fullFilePath);
        } else {
            sendDocument(peer, fileName, mimeType, fullFilePath);
        }
    }

    public void sendAnimation(Peer peer, String fullFilePath) {
        ImageHelper.BitmapSize size = ImageHelper.getImageSize(fullFilePath);
        if (size == null) {
            return;
        }

        Bitmap bmp = BitmapFactory.decodeFile(fullFilePath);
        if (bmp == null) {
            return;
        }
        Bitmap fastThumb = ImageHelper.scaleFit(bmp, 90, 90);

        byte[] fastThumbData = ImageHelper.save(fastThumb);

        sendAnimation(peer, fullFilePath, size.getWidth(), size.getHeight(),
                new FastThumb(fastThumb.getWidth(), fastThumb.getHeight(), fastThumbData), fullFilePath);
    }

    public void sendPhoto(Peer peer, String fullFilePath) {
        sendPhoto(peer, fullFilePath, new File(fullFilePath).getName());
    }

    public void sendPhoto(Peer peer, String fullFilePath, String fileName) {
        try {
            Bitmap bmp = ImageHelper.loadOptimizedHQ(fullFilePath);
            if (bmp == null) {
                return;
            }
            Bitmap fastThumb = ImageHelper.scaleFit(bmp, 90, 90);

            byte[] fastThumbData = ImageHelper.save(fastThumb);

            boolean isGif = fullFilePath.endsWith(".gif");

            String resultFileName = getExternalUploadTempFile("image", isGif ? "gif" : "jpg");
            if (resultFileName == null) {
                return;
            }

            if (isGif) {
                IOUtils.copy(new File(fullFilePath), new File(resultFileName));
                sendAnimation(peer, fileName, bmp.getWidth(), bmp.getHeight(), new FastThumb(fastThumb.getWidth(), fastThumb.getHeight(),
                        fastThumbData), resultFileName);
            } else {
                ImageHelper.save(bmp, resultFileName);
                sendPhoto(peer, fileName, bmp.getWidth(), bmp.getHeight(), new FastThumb(fastThumb.getWidth(), fastThumb.getHeight(),
                        fastThumbData), resultFileName);
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void sendVoice(Peer peer, int duration, String fullFilePath) {
        File f = new File(fullFilePath);
        sendAudio(peer, f.getName(), duration, fullFilePath);
    }

    public void sendVideo(Peer peer, String fullFilePath) {
        sendVideo(peer, fullFilePath, new File(fullFilePath).getName());
    }

    public void sendVideo(Peer peer, String fullFilePath, String fileName) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(fullFilePath);
            int duration = (int) (Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000L);
            Bitmap img = retriever.getFrameAtTime(0);
            int width = img.getWidth();
            int height = img.getHeight();
            Bitmap smallThumb = ImageHelper.scaleFit(img, 90, 90);
            byte[] smallThumbData = ImageHelper.save(smallThumb);

            FastThumb thumb = new FastThumb(smallThumb.getWidth(), smallThumb.getHeight(), smallThumbData);

            sendVideo(peer, fileName, width, height, duration, thumb, fullFilePath);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public Command<Boolean> sendUri(final Peer peer, final Uri uri) {
        return sendUri(peer, uri, "Actor");
    }

    public Command<Boolean> sendUri(final Peer peer, final Uri uri, String appName) {
        return callback -> fileDownloader.execute(() -> {
            String[] filePathColumn = {MediaStore.Images.Media.DATA, MediaStore.Video.Media.MIME_TYPE,
                    MediaStore.Video.Media.TITLE};
            String picturePath = null;
            String mimeType = null;
            String fileName = null;

            String ext = "";

            Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(filePathColumn[0]);
                    if (index > -1) {
                        picturePath = cursor.getString(index);
                    }
                    int minIndex = cursor.getColumnIndex(filePathColumn[1]);
                    if (minIndex > -1) {
                        mimeType = cursor.getString(minIndex);
                    }
                    int fileIndex = cursor.getColumnIndex(filePathColumn[2]);
                    if (fileIndex > -1) {
                        fileName = cursor.getString(fileIndex);
                    }
                }
                cursor.close();
                if (picturePath == null) {
//                    System.out.println("iGem:uri=" + uri.getPath());
                }
                if (fileName == null) {
                    fileName = new File(uri.getPath()).getName();
//                    System.out.println("iGem:fileName=" + fileName);
                }
                if (mimeType == null) {
                    mimeType = MimeTypeMap.getSingleton()
                            .getMimeTypeFromExtension(IOUtils.getFileExtension(fileName));
                    if (mimeType == null) {
                        mimeType = "*/*";
                    }
//                    System.out.println("iGem:mimeType=" + mimeType);
                }

            } else {
                picturePath = uri.getPath();
                fileName = new File(uri.getPath()).getName();
                int index = fileName.lastIndexOf(".");
                if (index > 0) {
                    ext = fileName.substring(index + 1);
                    mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
                } else {
                    mimeType = "?/?";
                }
            }

            if (mimeType == null) {
                mimeType = "?/?";
            }

            if (picturePath == null || !uri.getScheme().equals("file")) {
                File externalFile = context.getExternalFilesDir(null);
                if (externalFile == null) {
                    callback.onError(new NullPointerException());
                    return;
                }
                String externalPath = externalFile.getAbsolutePath();

                File dest = new File(externalPath + "/" +
                        appName +
                        "/");
                dest.mkdirs();

                if (ext.isEmpty() && picturePath != null) {
                    int index = picturePath.lastIndexOf(".");
                    ext = picturePath.substring(index + 1);
                }
                File outputFile = new File(dest, fileName);

//                File outputFile = new File(dest, "upload_" + random.nextLong() + "." + ext);
                picturePath = outputFile.getAbsolutePath();
                fileName = picturePath;
                try {
                    IOUtils.copy(context.getContentResolver().openInputStream(uri), new File(picturePath));
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.onError(e);
                    return;
                }
            }

            if (fileName == null) {
                fileName = picturePath;
            }

            if (!ext.isEmpty() && !fileName.endsWith(ext))
                fileName += "." + ext;
            if (mimeType.startsWith("video/")) {
                sendVideo(peer, picturePath, fileName);
//                            trackVideoSend(peer);
            } else if (mimeType.startsWith("image/")) {
                sendPhoto(peer, picturePath, new File(fileName).getName());
//                            trackPhotoSend(peer);
            } else {
                sendDocument(peer, picturePath, new File(fileName).getName());
//                            trackDocumentSend(peer);
            }

            callback.onResult(true);
        });
    }

    public void onActivityOpen() {
        appStateActor.send(new AppStateActor.OnActivityOpened());
    }

    public void onActivityClosed() {
        appStateActor.send(new AppStateActor.OnActivityClosed());
    }

    public BindedDisplayList<SearchEntity> buildSearchDisplayList() {
        return (BindedDisplayList<SearchEntity>) modules.getDisplayListsModule().buildSearchList(false);
    }

    public BindedDisplayList<Contact> buildContactsDisplayList() {
        return (BindedDisplayList<Contact>) modules.getDisplayListsModule().buildContactList(false);
    }

    private boolean isScreenOn() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            boolean screenOn = false;
            for (Display display : dm.getDisplays()) {
                if (display.getState() != Display.STATE_OFF) {
                    screenOn = true;
                }
            }
            return screenOn;
        } else {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            //noinspection deprecation
            return pm.isScreenOn();
        }
    }

    public String getExternalTempFile(String prefix, String postfix) {
        File externalFile = context.getExternalFilesDir(null);
        if (externalFile == null) {
            return null;
        }
        String externalPath = externalFile.getAbsolutePath();

        File dest = new File(externalPath + "/actor/tmp/");
        dest.mkdirs();

        File outputFile = new File(dest, prefix + "_" + random.nextLong() + "." + postfix);
        return outputFile.getAbsolutePath();
    }


    public String getExternalUploadTempFile(String prefix, String postfix) {
        File externalFile = context.getExternalFilesDir(null);
        if (externalFile == null) {
            return null;
        }
        String externalPath = externalFile.getAbsolutePath();

        File dest = new File(externalPath + "/actor/upload_tmp/");
        dest.mkdirs();

        File outputFile = new File(dest, prefix + "_" + random.nextLong() + "." + postfix);
        return outputFile.getAbsolutePath();
    }

    public String getInternalTempFile(String prefix, String postfix) {
        File externalFile = context.getFilesDir();
        if (externalFile == null) {
            return null;
        }
        String externalPath = externalFile.getAbsolutePath();

        File dest = new File(externalPath + "/actor/tmp/");
        dest.mkdirs();

        File outputFile = new File(dest, prefix + "_" + random.nextLong() + "." + postfix);
        return outputFile.getAbsolutePath();
    }

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

    public BindedDisplayList<Message> getDocsDisplayList(final Peer peer) {
        if (!docsLists.containsKey(peer)) {
            BindedDisplayList<Message> list = (BindedDisplayList<Message>) modules.getDisplayListsModule().getDocsSharedList(peer);
            docsLists.put(peer, list);
        }

        return docsLists.get(peer);
    }

    public GalleryVM getGalleryVM() {
        if (galleryVM == null) {
            galleryVM = new GalleryVM();
            checkGalleryScannerActor();
        }
        return galleryVM;
    }

    protected void checkGalleryScannerActor() {
        if (galleryScannerActor == null) {
            galleryScannerActor = ActorSystem.system().actorOf(Props.create(new ActorCreator() {
                @Override
                public Actor create() {
                    return new GalleryScannerActor(AndroidContext.getContext(), galleryVM);
                }
            }), "actor/gallery_scanner");
        }
    }

    public ActorRef getGalleryScannerActor() {
        checkGalleryScannerActor();
        return galleryScannerActor;
    }

    public EventBus getEvents() {
        return modules.getEvents();
    }

    public AppStateVM getAppStateVM() {
        return modules.getConductor().getAppStateVM();
    }

    public void startImport() {
        modules.getContactsModule().startImport();
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