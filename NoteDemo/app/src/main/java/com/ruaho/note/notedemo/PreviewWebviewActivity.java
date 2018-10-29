package com.ruaho.note.notedemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ruaho.note.view.ObservableWebView;
import com.ruaho.note.view.PostilTag;
import com.ruaho.note.view.PostilView;
import com.ruaho.note.view.ScreenUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PreviewWebviewActivity extends AppCompatActivity {

    private ObservableWebView mWebView;
    private PostilView mPostilView;
    private TextView mTagTxt;
    private TextView mBackTxt;
    private LinearLayout mBottomToolbar;
    private TextView mPenTxt;
    private TextView mEraseTxt;
    private TextView mToBackTxt;
    private TextView mToFrontTxt;
    private TextView mEraseAllTxt;
    private TextView mWordTxt;
    private TextView mSaveTxt;
    public final static int REQUEST_ADD_TEXT = 1;
    private static int REQUEST_ADD_TEXT_RESULT = 3;
    List<PostilTag> mPostilTagList;
    private Handler mHandler;

    private static final int MSG_SAVE_SUCCESS = 1;
    private static final int MSG_SAVE_FAILED = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_content_webview);
        hideBar();
        initView();
        initWebView();
        mPostilTagList = new ArrayList<PostilTag>();
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
    }

    void hideBar(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    void initView(){
        mPostilView = findViewById(R.id.mypostilview);
        mBottomToolbar = findViewById(R.id.note_bottom_toolbar);
        mPenTxt = findViewById(R.id.preview_pen);
        mEraseTxt = findViewById(R.id.preview_erase);
        mToBackTxt = findViewById(R.id.preview_back);
        mToFrontTxt = findViewById(R.id.preview_front);
        mEraseAllTxt = findViewById(R.id.preview_cancel_all);
        mTagTxt = findViewById(R.id.mbiaoji);
        mBackTxt = findViewById(R.id.note_back);
        mWordTxt = findViewById(R.id.preview_wenzi);
        mSaveTxt = findViewById(R.id.note_save);

        mPenTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPostilView.setMode(PostilView.Mode.DRAW);
            }
        });
        mEraseTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPostilView.setMode(PostilView.Mode.ERASER);
            }
        });
        mToBackTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPostilView.undo();
            }
        });
        mToFrontTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPostilView.redo();
            }
        });

        mTagTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mPostilView.getMode() == PostilView.Mode.NOT_EDIT){
                    mPostilView.setMode(PostilView.Mode.DRAW);
                    mBottomToolbar.setVisibility(View.VISIBLE);
                } else {
                    mPostilView.setMode(PostilView.Mode.NOT_EDIT);
                    mBottomToolbar.setVisibility(View.GONE);
                }
            }
        });
        mBackTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreviewWebviewActivity.this.finish();
            }
        });
        mEraseAllTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPostilView.clear();
            }
        });
        mWordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToAddWordsActivity();
            }
        });

        mPostilView.setCallback(new PostilView.Callback() {
            @Override
            public void onUndoRedoStatusChanged() {

            }

            @Override
            public void openTag(PostilTag tag) {
                Log.i("getResult!","tag = " + tag.toString());
                Intent intent = new Intent(PreviewWebviewActivity.this,AddWordActivity.class);
                intent.putExtra("content", tag.getContent());
                intent.putExtra("x",tag.getxPos());
                intent.putExtra("y",tag.getyPos());
                startActivityForResult(intent,REQUEST_ADD_TEXT);
            }
        });
        mSaveTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage();
            }
        });
    }

    private void jumpToAddWordsActivity(){
        Intent intent = new Intent(PreviewWebviewActivity.this,AddWordActivity.class);
        startActivityForResult(intent,REQUEST_ADD_TEXT);
    }

    void initWebView(){
        mWebView = findViewById(R.id.mywebview);
        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
//        mWebView.getSettings().setPluginsEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setVerticalScrollBarEnabled(false);
//        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.requestFocus();
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        String pdfUrl = "http://www8.cao.go.jp/okinawa/8/2012/0409-1-1.pdf";
        String pdfUrl = "https://source.android.com/security/reports/Google_Android_Security_2017_Report_Final.pdf";
//        mWebView.loadUrl(pdfUrl);
//        mWebView.loadUrl("http://docs.google.com/gview?embedded=true&url=" +pdfUrl);
        mWebView.loadUrl("http://www.baidu.com");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                //设置加载进度条
                view.setWebChromeClient(new WebChromeClientProgress());
                return true;
            }

        });
        mWebView.setOnScrollChangedCallback(new ObservableWebView.OnScrollChangedCallback() {
            @Override
            public void onScroll(int dx, int dy) {
                Log.i("dxdy","dx = " + dx + "dy = " +dy);
                mPostilView.updateOffsetY(dy);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.i("getResult!","requestCode="+ requestCode +"resultCode=" + resultCode);
        if(REQUEST_ADD_TEXT_RESULT == resultCode){
            if (REQUEST_ADD_TEXT == requestCode){
                String result = data.getExtras().getString("result");
                int x = data.getExtras().getInt("x");
                int y = data.getExtras().getInt("y");
                Log.i("getResult!",result);
                int height = ScreenUtils.getScreebHeight(getApplicationContext());
                int width = ScreenUtils.getScreenWidth(getApplicationContext());
                Log.i("getResult!","height = " + height + "width = " + width);
                if(x != -1 && y != -1){
                    mPostilView.addPostilTag(new PostilTag(x,y,result));
                } else {
                    mPostilView.addPostilTag(new PostilTag(width/2,height/2,result));
                }
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class WebChromeClientProgress extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int progress) {
            super.onProgressChanged(view, progress);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void saveImage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bm = mPostilView.buildBitmap();
                String savedFile = saveImage(bm, 100);
                if (savedFile != null) {
                    scanFile(PreviewWebviewActivity.this, savedFile);
                    mHandler.obtainMessage(MSG_SAVE_SUCCESS).sendToTarget();
                }else{
                    mHandler.obtainMessage(MSG_SAVE_FAILED).sendToTarget();
                }
            }
        }).start();
    }

    private static String saveImage(Bitmap bmp, int quality) {
        if (bmp == null) {
            return null;
        }
        File appDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (appDir == null) {
            return null;
        }
        String fileName = "preview_demo" + ".png";
        File file = new File(appDir, fileName);
        Log.i("saveImage","file is" + file.getAbsolutePath() + ":" +file.getName());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, quality, fos);
            fos.flush();
            return file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            Log.i("saveImage","FileNotFoundException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("saveImage","IOException");
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static void scanFile(Context context, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(scanIntent);
    }

}
