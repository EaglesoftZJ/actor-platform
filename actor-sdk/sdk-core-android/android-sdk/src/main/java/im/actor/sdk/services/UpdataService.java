package im.actor.sdk.services;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;

import android.widget.Toast;

import java.io.File;

/**
 * 检测安装更新文件的助手类
 *
 * @author G.Y.Y
 */

public class UpdataService extends Service {

    /**
     * 安卓系统下载类
     **/
    DownloadManager manager;

    /**
     * 接收下载完的广播
     **/
    DownloadCompleteReceiver receiver;

    String Directory = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/iGem/DownLoad/apk";

    long downloadId;

    private SharedPreferences prefs;
    private static final String DL_ID = "downloadId";

    /**
     * 初始化下载器
     **/
    private void initDownManager(String url) {

        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        receiver = new DownloadCompleteReceiver();
        // downloadManagerPro = new DownloadManagerPro(manager);
        // 设置下载地址
        if (!url.startsWith("http://")) {
            url = "http://" + url;
        }
        Uri uri = Uri.parse(url);
        DownloadManager.Request down = new DownloadManager.Request(uri);

        File downFileD = new File(Directory);
        if (!downFileD.exists()) {
            downFileD.mkdirs();
        }

        if (isHasSdcard()) {
            File downFile = new File(Directory + "/flyChat.apk");
            down.setDestinationUri(Uri.fromFile(downFile));
            // down.setDestinationInExternalPublicDir("",
            // "OfficeFly.apk");

            // down.setDestinationInExternalFilesDir(this,
            // Environment.DIRECTORY_DOWNLOADS, "OfficeFly.apk");
        } else {

        }

        down.setTitle("flyChat下载更新");
        // down.setDescription("da");
        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                | DownloadManager.Request.NETWORK_WIFI);
        // 下载时，通知栏显示途中
        // VISIBILITY_VISIBLE_NOTIFY_COMPLETED一直显示
//        VISIBILITY_VISIBLE
        down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // 显示下载界面
        down.setVisibleInDownloadsUi(true);
        // 将下载请求放入队列
        downloadId = manager.enqueue(down);
        prefs.edit().putLong(DL_ID, downloadId);
        // 设置下载后文件存放的位置

        // 注册下载广播
        registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        //
        // DownloadChangeObserver downloadObserver = new DownloadChangeObserver(
        // null);
        // getContentResolver().registerContentObserver(uri, true,
        // downloadObserver);

        // updateView();
    }

    // public void updateView() {
    // int[] bytesAndStatus = downloadManagerPro.getBytesAndStatus(downloadId);
    // handler.sendMessage(handler.obtainMessage(0, bytesAndStatus[0],
    // bytesAndStatus[1], bytesAndStatus[2]));
    // }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        String url = bundle.getString("url");
        System.out.println(url + "--service url--");
        // 调用下载
        Toast.makeText(this, "flyChat下载更新", Toast.LENGTH_SHORT).show();
        initDownManager(url);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // 注销下载广播
        if (receiver != null)
            unregisterReceiver(receiver);
        super.onDestroy();
    }

    // class DownloadChangeObserver extends ContentObserver {
    //
    // public DownloadChangeObserver(Handler handler) {
    // super(handler);
    // }
    //
    // @Override
    // public void onChange(boolean selfChange) {
    // queryDownloadStatus();
    // }
    // }
    //
    // private void queryDownloadStatus() {
    // DownloadManager.Query query = new DownloadManager.Query();
    // query.setFilterById(downloadId);
    // Cursor c = manager.query(query);
    // if (c != null && c.moveToFirst()) {
    // int status = c.getInt(c
    // .getColumnIndex(DownloadManager.COLUMN_STATUS));
    // int reasonIdx = c.getColumnIndex(DownloadManager.COLUMN_REASON);
    // int titleIdx = c.getColumnIndex(DownloadManager.COLUMN_TITLE);
    // int fileSizeIdx = c
    // .getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
    // int bytesDLIdx = c
    // .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
    // String title = c.getString(titleIdx);
    // int fileSize = c.getInt(fileSizeIdx);
    // int bytesDL = c.getInt(bytesDLIdx);
    //
    // int reason = c.getInt(reasonIdx);
    // StringBuilder sb = new StringBuilder();
    // sb.append(title).append("\n");
    // sb.append("Downloaded ").append(bytesDL).append(" / ")
    // .append(fileSize);
    //
    // switch (status) {
    // case DownloadManager.STATUS_PAUSED:
    // Log.v("down", "STATUS_PAUSED");
    // case DownloadManager.STATUS_PENDING:
    // Log.v("down", "STATUS_PENDING");
    // case DownloadManager.STATUS_RUNNING:
    // // 正在下载，不做任何事情
    // Log.v("down", "STATUS_RUNNING");
    // break;
    // case DownloadManager.STATUS_SUCCESSFUL:
    // // 完成
    // Log.v("down", "下载完成");
    //
    // // 获取下载的文件id
    // // long downId = intent.getLongExtra(
    // // DownloadManager.EXTRA_DOWNLOAD_ID, -1);
    // // 自动安装apk
    // // installAPK(manager.getUriForDownloadedFile(downloadId));
    // // manager.remove(downloadId);
    //
    // // 停止服务并关闭广播
    // // UpdataService.this.stopSelf();
    // // 在DownloadCompleteReceiver中执行
    // break;
    // case DownloadManager.STATUS_FAILED:
    // // 清除已下载的内容，重新下载
    // Log.v("down", "STATUS_FAILED");
    // // manager.remove(downloadId);
    // // prefs.edit().clear().commit();
    // break;
    // }
    // }
    // }

    /**
     * 安装apk文件
     */
    private void installAPK(Uri apk) {

        // 通过Intent安装APK文件
//        Intent intents = new Intent();
//        intents.setAction(Intent.ACTION_VIEW);
//        intents.setData(apk);
////        intents.setDataAndType(apk, "application/vnd.android.package-archive");
//        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        // 如果不加上这句的话在apk安装完成之后点击打开会崩溃
//        startActivity(intents);
        if (apk == null) {
            return;
        }
        File file = new File(getPath(this, apk));
        if (file.exists()) {
            openFile(file);
        } else {
            Toast.makeText(this, "下载失败", Toast.LENGTH_SHORT).show();
        }
    }


    public void openFile(File var0) {
        Intent var2 = new Intent();
        var2.setAction(Intent.ACTION_VIEW);
        String var3 = getMIMEType(var0);

        Uri uri = Uri.fromFile(var0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//如果是7.0android系统
//            ContentValues contentValues = new ContentValues(1);
//            contentValues.put(MediaStore.Images.Media.DATA, videoFile.getAbsolutePath());
//            videoUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            uri = FileProvider.getUriForFile(this, "im.actor.develop.myFileProvider", var0);
        }
        var2.setDataAndType(uri, var3);
        var2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        var2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
            Toast.makeText(this, "没有找到打开此类文件的程序", Toast.LENGTH_SHORT).show();
        }

    }

    public String getMIMEType(File var0) {
        String var1 = "";
        String var2 = var0.getName();
        String var3 = var2.substring(var2.lastIndexOf(".") + 1, var2.length()).toLowerCase();
        var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3);
        return var1;
    }

    // 接受下载完成后的intent
    class DownloadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // queryDownloadStatus();
            // 判断是否下载完成的广播
            if (intent.getAction().equals(
                    DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                // 获取下载的文件id
                // long downId = intent.getLongExtra(
                // DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                // 自动安装apk
                installAPK(manager.getUriForDownloadedFile(downloadId));
                // manager.remove(downloadId);
                prefs.edit().clear().commit();
                // 停止服务并关闭广播
                UpdataService.this.stopSelf();
//				android.os.Process.killProcess(android.os.Process.myPid());
            }

        }
    }

    private boolean isHasSdcard() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= 19) {
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return uri.getPath();
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
