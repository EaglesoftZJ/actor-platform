package im.actor.sdk.controllers.root;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import im.actor.runtime.crypto.primitives.digest.MD5;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.controllers.BaseFragment;
import im.actor.sdk.intents.WebForV8Util;
import im.actor.sdk.intents.WebServiceLogionUtil;
import im.actor.sdk.util.MD5Util;
import webviewjavascriptbridge.BridgeInterface;
import webviewjavascriptbridge.JSResult;
import webviewjavascriptbridge.RequestHandler;
import webviewjavascriptbridge.WebViewJavaScriptBridge;
import webviewjavascriptbridge.WebViewJavaScriptBridgeBase;

import static im.actor.sdk.util.ActorSDKMessenger.messenger;
import static im.actor.sdk.util.ActorSDKMessenger.myUid;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("JavascriptInterface")
public class MainMoaFragment extends BaseFragment implements BridgeInterface {
    private static final String TAG = "iGem:" + MainMoaFragment.class.getSimpleName();

    WebView webViewNR;
    WebViewJavaScriptBridge mBridge;
    RequestHandler mRequestHandler;

    public MainMoaFragment() {
        setShowTitle(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_moa, container, false);
        webViewNR = (WebView) view.findViewById(R.id.v8_main_webView);
        webViewNR.getSettings().setLayoutAlgorithm(
                WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        // webViewNR.setInitialScale(100);

        // webViewNR.getSettings().setUseWideViewPort(true);
        // webViewNR.getSettings().setLoadWithOverviewMode(true);
        // LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
        // LOAD_DEFAULT: 根据cache-control决定是否从网络上取数据。
        // LOAD_CACHE_NORMAL: API level 17中已经废弃, 从API level
        // 11开始作用同LOAD_DEFAULT模式
        // LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
        // LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。

        webViewNR.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                // 监听下载功能，当用户点击下载链接的时候，直接调用系统的浏览器来下载
//                Intent i = new Intent(GongGaoTZFormInfoActivity.this,
//                        FileDownLoadActivity.class);
//                i.putExtra("url", Uri.decode(url));
//                startActivity(i);
            }
        });

        WebViewJavaScriptBridge.enableLogging();//打印Log
        WebSettings settings = webViewNR.getSettings();
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 设置
        // 缓存模式
        // webViewNR.getSettings().setDefaultFontSize(800);
        // 仅支持双击缩放
        settings.setSupportZoom(false);
        // 设置是否可缩放
//        settings.setBuiltInZoomControls(true);
        // 设置此属性，可任意比例缩放。
        // webViewNR.getSettings().setUseWideViewPort(true);
        // 解除数据阻止（image）
        settings.setBlockNetworkImage(false);

        // webViewNR.getSettings().setLayoutAlgorithm(
        // LayoutAlgorithm.SINGLE_COLUMN);
//        webViewNR.setVerticalScrollBarEnabled(false); //垂直不显示

        webViewNR.getSettings().setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);// 打开本地缓存提供JS调用,至关重要
        settings.setJavaScriptEnabled(true);//很关键
        settings.setAppCacheMaxSize(1024 * 1024 * 8);// 实现8倍缓存
        settings.setAllowFileAccess(true);
        settings.setUseWideViewPort(true);
        settings.setAppCacheEnabled(true);
        String appCachePath = getActivity().getCacheDir().getAbsolutePath();
        settings.setAppCachePath(appCachePath);
        settings.setDatabaseEnabled(true);

        mBridge = WebViewJavaScriptBridge.bridgeForWebView(this.getContext(), webViewNR);
        mBridge.setWebViewDelegate(new MyWebViewClient());//设置WebViewClient
        webViewNR.setWebChromeClient(new MyChromeClient());//设置ChromeClient
        //End
        //注册一个 处理 js端发来消息的 handler
        mBridge.registerHandler("zh", new WebViewJavaScriptBridgeBase.WVJBHandler() {
            @Override
            public void handle(JSONObject data, WebViewJavaScriptBridgeBase.WVJBResponseCallback responseCallback) {
                HashMap<String, String> par = new HashMap<>();
                par.put("zh", MD5Util.MD5(messenger().getUser(myUid()).getNick().get()));
                String baseUrl = ActorSDK.getV8WebUrl(getContext()) + "/rest/phone/JcYhglManage/login";
                WebForV8Util.webPost(getActivity(), baseUrl, par,
                        new Handler(new Handler.Callback() {
                            @Override
                            public boolean handleMessage(Message message) {
                                Bundle b = message.getData();
                                String datasource = b.getString("datasource");
                                try {
                                    responseCallback.callback(datasource);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return false;
                            }
                        }));
            }
        });

        webViewNR.loadUrl("http://192.168.2.136:8086/m/main");
        return view;
    }


    class MyChromeClient extends WebChromeClient {

        // Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            //choose image or take photo
        }

        // Android > 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            openFileChooser(uploadMsg);
        }

        // Android  > 4.1.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            openFileChooser(uploadMsg);
        }

        //Android >= 5.0
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            //choose image or take photo
            return true;
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }
    }

    class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.i(TAG, "onPageStarted");
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.i(TAG, "onPageFinished");
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i(TAG, "shouldOverrideUrlLoading");
            return super.shouldOverrideUrlLoading(view, url);
        }
    }


    @Override
    public void startActivityForResult(RequestHandler command, Intent intent, int requestCode) {
        setActivityResultCallback(command);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void setActivityResultCallback(RequestHandler plugin) {
        mRequestHandler = plugin;
    }


    @Override
    public ExecutorService getThreadPool() {
        return Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable r) {
                return new Thread(new Runnable() {
                    @Override
                    public void run() {
                        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                        r.run();
                    }
                }, "Test");
            }
        });
    }


    @Override
    public boolean onBackPressed() {
        if (webViewNR.canGoBack()) {
            webViewNR.goBack();
            return true;
        }
        return super.onBackPressed();
    }
}
