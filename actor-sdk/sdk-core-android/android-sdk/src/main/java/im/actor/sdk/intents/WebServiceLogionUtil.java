package im.actor.sdk.intents;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.io.KXmlSerializer;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayOutputStream;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;

import im.actor.sdk.ActorSDK;


/**
 * @作者：胡成杰
 * @时间：2013-12-3下午2:03:54
 * @功能：webService工具类，连接服务器，获取内容
 */
public class WebServiceLogionUtil {
    // 连接服务器，获取内容
    public static String URL;

    public static String DoWebService(Context con, String methodName,
                                      HashMap<String, Object> Params) {
        return DoWebService(null, con, methodName, Params);
    }

    // 检查更新的时候会有个专门的ip
    public static String DoWebService(String ip, Context con,
                                      String methodName, HashMap<String, Object> Params) {

        String serviceurl;
        if (ip != null && ip.length() > 0) {
            if (!ip.startsWith("http")) {
                ip = "http://" + ip;
            }
            serviceurl = ip;
        } else {
            SharedPreferences sp2 = con.getSharedPreferences("MOASetting",
                    con.MODE_PRIVATE);
            String url = sp2.getString("url", null);
            if (url == null || url.length() == 0) {
                serviceurl = ActorSDK.getWebServiceUri(con)+":8004";
                sp2.edit().putString("url", serviceurl).commit();
            } else {
                serviceurl = url;
            }
            if (!serviceurl.startsWith("http")) {
                serviceurl = "http://" + serviceurl;
            }
        }

        URL = serviceurl;
        String nameSpace = "http://eaglesoft";

        String soapAction = nameSpace + "/" + methodName;
        SoapObject request = new SoapObject(nameSpace, methodName);
//        /ActorServices-Maven
//        if (!serviceurl.endsWith("/ActorServices-Maven/services/ActorService")) {
//            serviceurl += "/ActorServices-Maven/services/ActorService";
//        }

        if (!serviceurl.endsWith("/services/ActorService")) {
            serviceurl += "/services/ActorService";
        }


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.bodyOut = request;
//        envelope.dotNet = true;
//        envelope.setOutputSoapObject(request);
        envelope.encodingStyle="UTF-8";

        for (Iterator<String> it = Params.keySet().iterator(); it.hasNext(); ) {
            String name = it.next();
            Object value = Params.get(name);

            // if("allowePushInNight".equals(name)){
            //
            // }else{
            request.addProperty(name, value);
            // }
            // String gbkValue="";
            // try {
            // gbkValue = new String(value.getBytes(), "GBK");
            // } catch (UnsupportedEncodingException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }

        }

        HttpTransportSE ht = new HttpTransportSE(serviceurl);
        ht.debug = true;
        try {
            ht.call(soapAction, envelope);
            if (null == envelope.getResponse()) {
                return null;
            } else {
                return envelope.getResponse().toString();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        } catch (ConnectException e) {
            e.printStackTrace();
            return "ConnectException";
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return "faceSmile";
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "ipError";
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            return "timeOut";
        } catch (SocketException e) {
            e.printStackTrace();
            return "timeOut";
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return "ipError";
        } catch (SoapFault e) {
            e.printStackTrace();
            return "SoapFault";
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    public static String getURL(Context con) {
        if (URL == null) {
            SharedPreferences sp2 = con.getSharedPreferences("MOASetting",
                    con.MODE_PRIVATE);
            URL = sp2.getString("url", "");
        }
        if (!URL.startsWith("http")) {
            URL = "http://" + URL;
        }
        return URL;
    }

    public static void setURL(String uRL) {
        URL = uRL;
    }

    public static void webServiceRun(final HashMap<String, Object> par,
                                     final String methodName, final Context con, final Handler handler) {
        // TODO Auto-generated method stub
        webServiceRun(null, par, methodName, con, handler);
    }

    // 检查更新的时候会有个专门的ip
    public static void webServiceRun(final String ip,
                                     final HashMap<String, Object> par, final String methodName,
                                     final Context con, final Handler handler) {
        // TODO Auto-generated method stub
        ConnectivityManager mConnectivity = (ConnectivityManager) con
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mTelephony = (TelephonyManager) con
                .getSystemService(con.TELEPHONY_SERVICE);
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
                        String data = DoWebService(ip, con, methodName, par);
                        if (data == null) {
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