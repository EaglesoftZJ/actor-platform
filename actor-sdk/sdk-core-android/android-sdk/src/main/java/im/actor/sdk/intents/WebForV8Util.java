package im.actor.sdk.intents;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import im.actor.sdk.ActorSDK;


/**
 * @作者：胡成杰
 * @时间：2013-12-3下午2:03:54
 * @功能：webService工具类，连接服务器，获取内容
 */
public class WebForV8Util {
//    // 连接服务器，获取内容
//    public static String URL;

    public static void webPost(Context con, final String baseURL, final HashMap<String, String> par
            , final Handler handler) {
        ConnectivityManager mConnectivity = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mTelephony = (TelephonyManager) con.getSystemService(con.TELEPHONY_SERVICE);
        // 检查网络连接，如果无网络可用，就不需要进行连网操作等
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null || !mConnectivity.getBackgroundDataSetting()) {
            Message msg = new Message();
            Bundle b = new Bundle();
            b.putString("datasource", "isNoNet");
            msg.setData(b);
            handler.sendMessage(msg);
        } else {
            new Thread() {
                @Override
                public void run() {
                    try {
                        Message msg = new Message();
                        Bundle b = new Bundle();
                        String data = "";

                        List<NameValuePair> pairList = new ArrayList<NameValuePair>();
                        // pairList.add(pair1);
                        if (par != null) {
                            for (Iterator<String> it = par.keySet().iterator(); it
                                    .hasNext(); ) {
                                String name = it.next();
                                String value = par.get(name);
                                pairList.add(new BasicNameValuePair(Uri
                                        .encode(name), Uri.encode(value)));
                            }

                        }
                        HttpEntity requestHttpEntity = new UrlEncodedFormEntity(
                                pairList);
                        // URL使用基本URL即可，其中不需要加参数
                        HttpPost httpPost = new HttpPost(baseURL);
                        // 将请求体内容加入请求中
                        httpPost.setEntity(requestHttpEntity);
                        // 需要客户端对象来发送请求
                        HttpClient httpClient = new DefaultHttpClient();
                        // 发送请求
                        HttpResponse response = httpClient.execute(httpPost);
                        // 显示响应
                        if (response.getStatusLine().getStatusCode() == 200) {
                            /* 读返回数据 */
                            data = EntityUtils.toString(response.getEntity());
                            // mTextView1.setText(strResult);

                            Header[] headers = response
                                    .getHeaders("Set-Cookie");
                            String headerstr = headers.toString();
                            if (headers == null)
                                return;
                            String str = "";
                            SerializableMap map = new SerializableMap();
                            Map<String, Object> mapVal = new HashMap<String, Object>();
                            for (int i = 0; i < headers.length; i++) {
                                String cookie = headers[i].getValue();
                                String[] cookievalues = cookie.split(";");
                                for (int j = 0; j < cookievalues.length; j++) {
                                    String[] keyPair = cookievalues[j]
                                            .split("=");
                                    String key = keyPair[0].trim();
                                    String value = keyPair.length > 1 ? keyPair[1]
                                            .trim() : "";
                                    String valueUTF = new String(
                                            value.getBytes("ISO-8859-1"),
                                            "utf-8");
                                    // CookieContiner.put(key, value);
                                    str += "key=" + key + ",value=" + valueUTF
                                            + "---";
                                    mapVal.put(key, valueUTF);
                                    // if("ASP.NET_SessionId".equals(key)){
                                    // b.put("SessionId", value);
                                    // }
                                }
                            }
                            map.setMap(mapVal);
                            b.putSerializable("cookie", map);
                            System.out.println(str);
                        } else {
                            data = "Error Response: "
                                    + response.getStatusLine().toString();
                        }
                        if (data == null
                                || "java.lang.NullPointerException".equals(data
                                .trim())) {
                            b.putString("datasource", "false");
                        } else {
                            b.putString("datasource", data);
                        }
                        msg.setData(b);
                        handler.sendMessage(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        // dialog.dismiss();
                    }
                }
            }.start();

        }
    }


    public static void webPostUrl(Context con, final String baseURL, final HashMap<String, String> par
            , final Handler handler) {
        ConnectivityManager mConnectivity = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mTelephony = (TelephonyManager) con.getSystemService(con.TELEPHONY_SERVICE);
        // 检查网络连接，如果无网络可用，就不需要进行连网操作等
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null || !mConnectivity.getBackgroundDataSetting()) {
            Message msg = new Message();
            Bundle b = new Bundle();
            b.putString("datasource", "isNoNet");
            msg.setData(b);
            handler.sendMessage(msg);
        } else {
            new Thread() {
                @Override
                public void run() {
                    try {
                        Message msg = new Message();
                        Bundle b = new Bundle();
                        String data = "";
                        String urlPath = new String(baseURL);
                        //建立连接
                        URL url = new URL(urlPath);
                        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                        //设置参数
                        httpConn.setDoOutput(true);     //需要输出
                        httpConn.setDoInput(true);      //需要输入
                        httpConn.setUseCaches(false);   //不允许缓存
                        httpConn.setRequestMethod("POST");      //设置POST方式连接
                        //设置请求属性
                        httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
                        httpConn.setRequestProperty("Charset", "UTF-8");
                        //连接,也可以不用明文connect，使用下面的httpConn.getOutputStream()会自动connect
                        httpConn.connect();
                        //建立输入流，向指向的URL传入参数
                        DataOutputStream dos = new DataOutputStream(httpConn.getOutputStream());
//                        dos.writeBytes(param);
                        dos.flush();
                        dos.close();
                        //获得响应状态
                        int resultCode = httpConn.getResponseCode();

                        StringBuffer sb = new StringBuffer();
                        String readLine = new String();
                        BufferedReader responseReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
                        while ((readLine = responseReader.readLine()) != null) {
                            sb.append(readLine).append("\n");
                        }
                        responseReader.close();

                        if (HttpURLConnection.HTTP_OK == resultCode) {
                            // 显示响应
                            /* 读返回数据 */

                            data = sb.toString();


                        } else {

                            data = "Error Response: "
                                    + sb.toString();
                        }
                        if (data == null
                                || "java.lang.NullPointerException".equals(data
                                .trim())) {
                            b.putString("datasource", "false");
                        } else {
                            b.putString("datasource", data);
                        }
                        msg.setData(b);
                        handler.sendMessage(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        // dialog.dismiss();
                    }
                }
            }.start();

        }
    }

}