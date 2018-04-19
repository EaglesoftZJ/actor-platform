package im.actor.sdk.controllers.preview;

import android.app.Activity;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;

import im.actor.sdk.R;
import im.actor.sdk.controllers.Intents;

public class TengXunX5PreviewActivity extends AppCompatActivity {

    private FrameLayout frameLayout;

    TbsReaderView readerView;
    String path;

    String fileName;
    String downloadFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teng_xun_x5_preview);

        getSupportActionBar().setTitle("文件查看");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        frameLayout = (FrameLayout) findViewById(R.id.preview_lay);
        path = getIntent().getStringExtra("path");
        fileName = getIntent().getStringExtra("fileName");
        downloadFileName = getIntent().getStringExtra("downloadFileName");
        openFile();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.preview_x5, menu);
        getMenuInflater().inflate(R.menu.doc_popup, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        Activity activity = TengXunX5PreviewActivity.this;
        if (i == android.R.id.home) {
            finish();
            return true;
        } else if (i == R.id.app_open) {

            try {
                activity.startActivity(Intents.openDoc(activity, fileName, downloadFileName));
            } catch (Exception e) {
                Toast.makeText(activity, R.string.toast_unable_open, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return true;
        } else if (i == R.id.share) {
            activity.startActivity(Intents.shareDoc(activity, fileName,
                    downloadFileName));
        }
        return false;
    }

    /**
     * 打开文件
     */
    private void openFile() {
        if (readerView != null) {
            readerView.onStop();
            readerView.destroyDrawingCache();
            readerView = null;
        }
        readerView = new TbsReaderView(this, new TbsReaderView.ReaderCallback() {
            @Override
            public void onCallBackAction(Integer integer, Object o, Object o1) {
                System.out.println("-----");
            }
        });
//        通过bundle把文件传给x5,打开的事情交由x5处理
        Bundle bundle = new Bundle();
        //传递文件路径
        bundle.putString("filePath", path);
        //加载插件保存的路径
        bundle.putString("tempPath", Environment.getExternalStorageDirectory() + File.separator + "X5temp");
        //加载文件前的初始化工作,加载支持不同格式的插件
        boolean b = readerView.preOpen(getFileType(path), false);
        if (b) {
            readerView.openFile(bundle);
        }
        frameLayout.addView(readerView);
    }

    /***
     * 获取文件类型
     *
     * @param path 文件路径
     * @return 文件的格式
     */
    private String getFileType(String path) {
        String str = "";

        if (TextUtils.isEmpty(path)) {
            return str;
        }
        int i = path.lastIndexOf('.');
        if (i <= -1) {
            return str;
        }
        str = path.substring(i + 1);
        return str;
    }

    /**
     * 确保注销配置能够被释放
     */
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
//        if (this.webView != null) {
//            webView.destroy();
//        }
        readerView.onStop();
        super.onDestroy();
    }
}
