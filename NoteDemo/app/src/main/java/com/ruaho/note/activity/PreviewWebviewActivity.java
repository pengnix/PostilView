package com.ruaho.note.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ruaho.note.bean.Picture;
import com.ruaho.note.bean.PostilRecord;
import com.ruaho.note.bean.PostilTagList;
import com.ruaho.note.util.FileUtils;
import com.ruaho.note.util.MD5Utils;
import com.ruaho.note.util.NoteSharePreferenceUtils;
import com.ruaho.note.view.ChooseColorLayout;
import com.ruaho.note.view.ObservableWebView;
import com.ruaho.note.bean.PostilTag;
import com.ruaho.note.view.PostilView;
import com.ruaho.note.util.ScreenUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PreviewWebviewActivity extends AppCompatActivity {

    private ObservableWebView mWebView;
    private FrameLayout mRoot;
    private PostilView mPostilView;
    private TextView mTagTxt;
    private TextView mBackTxt;
    private LinearLayout mBottomToolbar;
    private ImageView mPenTxt;
    private ImageView mBrushTxt;
    private ImageView mEraseTxt;
    private ImageView mToBackTxt;
    private ImageView mToFrontTxt;
    private ImageView mEraseAllTxt;
    private TextView mWordTxt;
    private TextView mSaveTxt;
    private TextView mControlTxt;
    private ImageView mTagSave;
    private ImageView mTagCancel;
    private ImageView mTuyaSave;
    private ImageView mTuyaCancel;
    private SeekBar mSeekBar;
    LinearLayout mTagTopBar;
    RelativeLayout mCommonToolBar;
    LinearLayout mTuYaContainer;
    ChooseColorLayout mChooseColorBar;
    PostilRecord mPostRecord;
    PostilTagList mPostilTagList;
    public final static int REQUEST_ADD_TEXT = 1;
    private static int REQUEST_ADD_TEXT_RESULT = 3;
    private Handler mHandler;
    private String url = "";

    private static final int MSG_SAVE_SUCCESS = 1;
    private static final int MSG_SAVE_FAILED = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_content_webview);
        Intent intent = getIntent();
        if(intent != null){
            url = intent.getStringExtra("previewurl");
        }
        mPostRecord = new PostilRecord();
        mPostilTagList = new PostilTagList();
        hideBar();
        initView();
        initWebView();
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
        loadHistory();
    }

    void loadHistory(){
        loadRecord();
        mPostilView.setHistoryPictureRecord(mPostRecord);
        loadTags();
        mPostilView.setPostilTags(mPostilTagList);
    }

    void hideBar(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    void initView(){
        mRoot = findViewById(R.id.preview_root_view);
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
        mControlTxt = findViewById(R.id.mguanli);
        mTagSave = findViewById(R.id.tag_save);
        mTagCancel = findViewById(R.id.tag_cancel);
        mTagTopBar  = findViewById(R.id.tag_container);
        mCommonToolBar = findViewById(R.id.not_edit_container);
        mTuYaContainer = findViewById(R.id.tuya_container);
        mTuyaSave = findViewById(R.id.tuya_save);
        mTuyaCancel = findViewById(R.id.tuya_cancel);
        mSeekBar = findViewById(R.id.preview_seekbar);
        mBrushTxt = findViewById(R.id.preview_brush);

        mPenTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPostilView.setMode(PostilView.Mode.DRAW);
                usePenState();
            }
        });
        mEraseTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPostilView.setMode(PostilView.Mode.ERASER);
                useEraserState();
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
                    showTuyaTopToolBar();
                    mBottomToolbar.setVisibility(View.VISIBLE);
                    addColorBar();
                    usePenState();
                } else {
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
                intent.putExtra("offsetY", tag.getOffsetY());
                startActivityForResult(intent,REQUEST_ADD_TEXT);
            }
        });
        mSaveTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //saveTags();
            }
        });
        mControlTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        mTagSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommonToolBar();
                saveTagImage();
                mPostilView.clearCurrentPostilTag();
                mPostilView.setPostilTags(mPostilTagList);
                //saveTags();
            }
        });
        mTagCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommonToolBar();
                mPostilView.clearCurrentPostilTag();
            }
        });
        mTuyaSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPostilView.setMode(PostilView.Mode.NOT_EDIT);
                removeColorBar();
                mBottomToolbar.setVisibility(View.GONE);
                saveTuYaImage();
                showCommonToolBar();
                mPostilView.clear();
                mPostilView.setHistoryPictureRecord(mPostRecord);
            }
        });
        mTuyaCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPostilView.setMode(PostilView.Mode.NOT_EDIT);
                removeColorBar();
                mBottomToolbar.setVisibility(View.GONE);
                showCommonToolBar();
                mPostilView.clear();
            }
        });

        mBrushTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSeekBar.setProgress(100);
                mPostilView.setPenRawSize(100);
                useBrushState();
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    Log.i("progresschange","i = " + i);
                mPostilView.setPenRawSize(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    void showTagToolBar(){
        mTagTopBar.setVisibility(View.VISIBLE);
        mCommonToolBar.setVisibility(View.GONE);
        mTuYaContainer.setVisibility(View.GONE);
    }

    void showCommonToolBar(){
        mTagTopBar.setVisibility(View.GONE);
        mCommonToolBar.setVisibility(View.VISIBLE);
        mTuYaContainer.setVisibility(View.GONE);
    }

    void showTuyaTopToolBar(){
        mTagTopBar.setVisibility(View.GONE);
        mCommonToolBar.setVisibility(View.GONE);
        mTuYaContainer.setVisibility(View.VISIBLE);
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
        mWebView.loadUrl(url);
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
                int offset = data.getExtras().getInt("offsetY");
                Log.i("getResult!",result);
                int height = ScreenUtils.getScreebHeight(getApplicationContext());
                int width = ScreenUtils.getScreenWidth(getApplicationContext());
                Log.i("getResult!","height = " + height + "width = " + width);
                if(x != -1 && y != -1){
                    Log.i("SSSSSSSS","update" + offset + ":" + x + ":" + y);
                    mPostilView.updatePostilTag(new PostilTag(offset,x,y,result));
                } else {
                    offset = (int)mPostilView.getOffsetY();
                    Log.i("SSSSSSSS","add" + offset+ ":" + width/2 + ":" + height/2);
                    mPostilView.addPostilTag(new PostilTag(offset,width/2,height/2,result));
                    showTagToolBar();
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

    private void saveTuYaImage(){
        Bitmap bm = mPostilView.buildBitmap();
        String savedFile = FileUtils.saveImage(bm, 100);
        if (savedFile != null) {
            Picture picture = new Picture((int)(mPostilView.getOffsetY()),savedFile);
            mPostRecord.getPicList().add(picture);
            saveRecord();
        }else{
        }
    }

    private void saveTagImage(){
        Bitmap bm = mPostilView.buildBitmap();
        String savedFile = FileUtils.saveImage(bm, 100);
        if (savedFile != null) {
            PostilTag currentTag = mPostilView.getCurrentPostilTag();
            currentTag.setCanMove(false);
            PostilTag tag = new PostilTag(currentTag.getOffsetY(),currentTag.getxPos(),currentTag.getyPos(),currentTag.getContent(),savedFile);
            mPostilTagList.getList().add(tag);
            saveTags();
        }else{
        }
    }

    private void saveRecord(){
        Gson gson = new Gson();
        String recordString = gson.toJson(mPostRecord);
        Log.i("saveImage","saveRecord = " + recordString);
        String key = MD5Utils.stringToMD5("NoteRecords" + url);
        Log.i("saveImage","saveRecord key = " + key);
        NoteSharePreferenceUtils.setPrefString(key,recordString);
    }

    private void loadRecord(){
        String key = MD5Utils.stringToMD5("NoteRecords" + url);
        String recordString = NoteSharePreferenceUtils.getPrefString(key,null);
        Gson gson = new Gson();
        Log.i("saveImage","loadRecord = " + recordString);
        Log.i("saveImage","loadRecord key = " + key);
        if(null != recordString){
            mPostRecord = gson.fromJson(recordString, PostilRecord.class);
        }
    }

    private void saveTags(){
        Gson gson = new Gson();
        String recordString = gson.toJson(mPostilTagList);
        Log.i("saveImage","saveTag = " + recordString);
        String key = MD5Utils.stringToMD5("NoteTags" + url);
        Log.i("saveImage","saveTag key = " + key);
        NoteSharePreferenceUtils.setPrefString(key,recordString);
    }

    private void loadTags(){
        String key = MD5Utils.stringToMD5("NoteTags" + url);
        String recordString = NoteSharePreferenceUtils.getPrefString(key,null);
        Gson gson = new Gson();
        Log.i("saveImage","loadTags = " + recordString);
        Log.i("saveImage","loadTags key = " + key);
        if(null != recordString){
            mPostilTagList = gson.fromJson(recordString, PostilTagList.class);
        }
    }

    private void addColorBar(){
        if(mChooseColorBar == null){
            mChooseColorBar = (ChooseColorLayout) LayoutInflater.from(this).inflate(R.layout.preview_change_color_layout, null);
            mChooseColorBar.lazyInit();
            mChooseColorBar.setCallback(new ChooseColorLayout.Callback() {
                @Override
                public void changePenColor(int color) {
                    mPostilView.setPenColor(color);
                }
            });
        }
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM;
        mRoot.addView(mChooseColorBar,lp);
    }

    private void removeColorBar(){
        if(mChooseColorBar != null){
            mRoot.removeView(mChooseColorBar);
        }
    }

    private void usePenState(){
        mPenTxt.setImageResource(R.drawable.shixinbi_selected);
        mBrushTxt.setImageResource(R.drawable.caibi);
        mEraseTxt.setImageResource(R.drawable.xiangpica);
    }

    private void useBrushState(){
        mPenTxt.setImageResource(R.drawable.shixinbi);
        mBrushTxt.setImageResource(R.drawable.caibi_selected);
        mEraseTxt.setImageResource(R.drawable.xiangpica);
    }

    private void useEraserState(){
        mPenTxt.setImageResource(R.drawable.shixinbi);
        mBrushTxt.setImageResource(R.drawable.caibi);
        mEraseTxt.setImageResource(R.drawable.xiangpica_selected);
    }
}
