package com.ruaho.note.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ruaho.note.adapter.PreviewWordsAdapter;
import com.ruaho.note.bean.Picture;
import com.ruaho.note.bean.PostilRecordList;
import com.ruaho.note.bean.PostilWordsList;
import com.ruaho.note.bean.PostilWord;
import com.ruaho.note.util.BitmapCache;
import com.ruaho.note.util.DialogUtils;
import com.ruaho.note.util.FileUtils;
import com.ruaho.note.util.MD5Utils;
import com.ruaho.note.util.NoteSharePreferenceUtils;
import com.ruaho.note.util.StringUtils;
import com.ruaho.note.util.VersionUtils;
import com.ruaho.note.view.ChooseColorLayout;
import com.ruaho.note.view.ObservableWebView;
import com.ruaho.note.view.PostilView;
import com.ruaho.note.util.ScreenUtils;
import com.ruaho.note.view.SwipeItemLayout;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;

import static com.ruaho.note.view.PostilView.SCALE_BASE;

public class PreviewWebviewActivity extends AppCompatActivity {

    private ObservableWebView mWebView;
    private LinearLayout mWaterMarkLayout;
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
    private ImageView mTuyaCurve;
    private ImageView mTuyaLine;
    private ImageView mTuyaRect;
    private ImageView mTuyaOval;
    private ImageView mTuyaConfig;
    private ImageView mTuyaCancel;
    private ImageView mTuyaNext;
    private ImageView mTuyaPreview;
    private ImageView mTuyaDelete;
    private TextView mWordsManagerCloseBtn;
    LinearLayout mLeftWordsBar;
    private SeekBar mSeekBar;
    PopupWindow popupWindow;
    PopupWindow tuyaDeletePopupWindow;
    LinearLayout mTagTopBar;
    RelativeLayout mCommonToolBar;
    LinearLayout mTuYaContainer;
    LinearLayout mTuYaControlTopBar;
    ChooseColorLayout mChooseColorBar;
    PostilRecordList mPostRecord;
    PostilWordsList mPostilWordList;
    ImageView mTuyaControlClose;
    RecyclerView mWordsRecycleView;
    PreviewWordsAdapter mPreviewWordsAdapter;
    TextView mTitleTxt;
    int mEditIndex;//编辑文字index
    static int QUALITY = 20;

    public final static int REQUEST_ADD_TEXT = 1;
    private static int REQUEST_ADD_TEXT_RESULT = 3;
    private Handler mHandler;
    private String url = "";
    private String title = "";
    private String waterMark="";
    private int markNum =0;

    private static final int TUYA_SAVE_SUCCESS = 1;
    private static final int TUYA_SAVE_FAILED = 2;
    private static final int WORD_SAVE_SUCCESS = 3;
    private static final int WORD_SAVE_FAILED = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_content_webview);
        Intent intent = getIntent();
        if(intent != null){
            url = intent.getStringExtra("previewurl");
            title = intent.getStringExtra("previewtitle");
            waterMark = intent.getStringExtra("watermark");
            markNum = intent.getIntExtra("marknum",0);
        }
        mPostRecord = new PostilRecordList();
        mPostilWordList = new PostilWordsList();
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
        initMainPopupWindow();
        initTuYaDeletePopupWindow();
        initWordsList();
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case TUYA_SAVE_SUCCESS:
                        mPostilView.setMode(PostilView.Mode.NOT_EDIT);
                        removeColorBar();
                        mBottomToolbar.setVisibility(View.GONE);
                        showCommonToolBar();
                        mPostilView.clear();
                        mPostilView.setHistoryPictureRecord(mPostRecord);
                        Toast.makeText(PreviewWebviewActivity.this,"保存成功",Toast.LENGTH_LONG).show();
                        break;
                    case TUYA_SAVE_FAILED:
                        Toast.makeText(PreviewWebviewActivity.this,"保存失败",Toast.LENGTH_LONG).show();
                        break;
                    case WORD_SAVE_SUCCESS:
                        showCommonToolBar();
                        mPostilView.clearCurrentPostilTag();
                        enableZoom();
                        mPostilView.setPostilTags(mPostilWordList);
                        Toast.makeText(PreviewWebviewActivity.this,"保存成功",Toast.LENGTH_LONG).show();
                        mPreviewWordsAdapter.notifyDataSetChanged();
                        break;
                    case WORD_SAVE_FAILED:
                        Toast.makeText(PreviewWebviewActivity.this,"保存失败",Toast.LENGTH_LONG).show();
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    void loadHistory(){
        loadRecord();
        mPostilView.setHistoryPictureRecord(mPostRecord);
        loadTags();
        mPostilView.setPostilTags(mPostilWordList);
//        Log.i("calSize","SHeight"+ ScreenUtils.getScreebHeight(this) + "SWidth"+ScreenUtils.getScreenWidth(this));
    }

    void hideBar(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    void initView(){
        mRoot = findViewById(R.id.preview_root_view);
        mWaterMarkLayout = findViewById(R.id.preview_ll_water_mark);
        mPostilView = findViewById(R.id.mypostilview);
        mBottomToolbar = findViewById(R.id.note_bottom_toolbar);
        mTuYaControlTopBar = findViewById(R.id.preview_tuya_control_container);
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
        mTuyaCurve = findViewById(R.id.tuya_curve);
        mTuyaLine = findViewById(R.id.tuya_line);
        mTuyaRect = findViewById(R.id.tuya_rect);
        mTuyaOval = findViewById(R.id.tuya_oval);
        mTuyaConfig = findViewById(R.id.tuya_shezhi);
        mTuyaCancel = findViewById(R.id.tuya_cancel);
        mSeekBar = findViewById(R.id.preview_seekbar);
        mBrushTxt = findViewById(R.id.preview_brush);
        mTuyaControlClose = findViewById(R.id.tuya_control_close);
        mTuyaNext = findViewById(R.id.tuya_control_down);
        mTuyaPreview = findViewById(R.id.tuya_control_up);
        mTuyaDelete = findViewById(R.id.tuya_control_delete);
        mLeftWordsBar = findViewById(R.id.preview_left_menu);
        mWordsManagerCloseBtn = findViewById(R.id.preview_word_manager_close);
        mWordsRecycleView = findViewById(R.id.preview_words_list);
        mTitleTxt = findViewById(R.id.preview_view_title);
        mTitleTxt.setText(title);

        mPenTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeToPenState();
            }
        });
        mEraseTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPostilView.setPenAlpha(255);
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
                if(mLeftWordsBar != null && mLeftWordsBar.getVisibility() == View.VISIBLE){
                    hideWordsManager();
                    return;
                }
                if(mPostilView.getMode() == PostilView.Mode.NOT_EDIT){
                    mPostilView.setMode(PostilView.Mode.DRAW);
                    showTuyaTopToolBar();
                    mBottomToolbar.setVisibility(View.VISIBLE);
                    addColorBar();
                    usePenState();
                }
            }
        });
        mBackTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllBitmap();
                PreviewWebviewActivity.this.finish();
            }
        });
        mEraseAllTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.createDialog(PreviewWebviewActivity.this,
                        R.string.preview_tuya_erase_all_title,
                        R.string.preview_tuya_close_ok,
                        R.string.preview_tuya_close_cancel,
                        new DialogUtils.NoteDialogInterface() {
                            @Override
                            public void Ok() {
                                mPostilView.clear();
                            }

                            @Override
                            public void cancel() {

                            }
                        });
            }
        });
        mWordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mLeftWordsBar != null && mLeftWordsBar.getVisibility() == View.VISIBLE){
                    hideWordsManager();
                    return;
                }
                jumpToAddWordsActivity();
            }
        });


        mPostilView.setCallback(new PostilView.Callback() {
            @Override
            public void scrollTo(int x, int y) {
                scrollToYforWebView(y);
            }

            @Override
            public void onUndoRedoStatusChanged() {

            }

            @Override
            public void openTag(PostilWord tag) {
                Log.i("getResult!","tag = " + tag.toString());
                Intent intent = new Intent(PreviewWebviewActivity.this,AddWordActivity.class);
                intent.putExtra("content", tag.getContent());
                intent.putExtra("x",tag.getxPos());
                intent.putExtra("y",tag.getyPos());
                intent.putExtra("offsetX", tag.getOffsetX());
                intent.putExtra("offsetY", tag.getOffsetY());
                intent.putExtra("scale", tag.getScale());
                startActivityForResult(intent,REQUEST_ADD_TEXT);
            }
        });
        mSaveTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        mControlTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mLeftWordsBar != null && mLeftWordsBar.getVisibility() == View.VISIBLE){
                    hideWordsManager();
                    return;
                }
                showMainPopWindow();
            }
        });
        mTagSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveWordsTodo();

            }
        });
        mTagCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.createDialog(PreviewWebviewActivity.this,
                        R.string.preview_word_cancel_all_title,
                        R.string.preview_tuya_close_ok,
                        R.string.preview_tuya_close_cancel,
                        new DialogUtils.NoteDialogInterface() {
                            @Override
                            public void Ok() {
                                showCommonToolBar();
                                mPostilView.clearCurrentPostilTag();
                                enableZoom();
                            }

                            @Override
                            public void cancel() {

                            }
                        });

            }
        });
        mTuyaSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.createDialog(PreviewWebviewActivity.this,
                        R.string.preview_tuya_save_title,
                        R.string.preview_tuya_close_ok,
                        R.string.preview_tuya_close_cancel,
                        new DialogUtils.NoteDialogInterface() {
                            @Override
                            public void Ok() {
                                saveTuyaToDo();
                            }

                            @Override
                            public void cancel() {

                            }
                        });
            }
        });
        mTuyaConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBottomToolbar.getVisibility() == View.VISIBLE){
                    mTuyaConfig.setImageResource(R.drawable.pizhu_shezhi);
                    removeColorBar();
                    mBottomToolbar.setVisibility(View.GONE);
                } else {
                    mTuyaConfig.setImageResource(R.drawable.pizhu_shezhi_selected);
                    addColorBar();
                    mBottomToolbar.setVisibility(View.VISIBLE);
                }
            }
        });
        mTuyaCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogUtils.createDialog(PreviewWebviewActivity.this,
                        R.string.preview_tuya_close_title,
                        R.string.preview_tuya_close_ok,
                        R.string.preview_tuya_close_cancel,
                        new DialogUtils.NoteDialogInterface() {
                            @Override
                            public void Ok() {
                                mPostilView.setMode(PostilView.Mode.NOT_EDIT);
                                removeColorBar();
                                mBottomToolbar.setVisibility(View.GONE);
                                showCommonToolBar();
                                mPostilView.clear();
                            }

                            @Override
                            public void cancel() {

                            }
                        });
            }
        });

        mBrushTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPostilView.setMode(PostilView.Mode.DRAW);
                mSeekBar.setProgress(100);
                mPostilView.setPenRawSize(100*50/100);
                mPostilView.setPenAlpha(150);
                useBrushState();
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    Log.i("progresschange","i = " + i);
                mPostilView.setPenRawSize(i*50/100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mTuyaCurve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeToPenState();
                mPostilView.setDrawMode(PostilView.DRAWMode.CURVE);
                resetAllPenTypeIcon();
                mTuyaCurve.setImageResource(R.drawable.pizhu_quxian_selected);
            }
        });
        mTuyaLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeToPenState();
                mPostilView.setDrawMode(PostilView.DRAWMode.LINE);
                resetAllPenTypeIcon();
                mTuyaLine.setImageResource(R.drawable.pizhu_zhixian_selected);
            }
        });
        mTuyaRect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeToPenState();
                mPostilView.setDrawMode(PostilView.DRAWMode.RECT);
                resetAllPenTypeIcon();
                mTuyaRect.setImageResource(R.drawable.pizhu_juxing_selected);
            }
        });
        mTuyaOval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeToPenState();
                mPostilView.setDrawMode(PostilView.DRAWMode.OVAL);
                resetAllPenTypeIcon();
                mTuyaOval.setImageResource(R.drawable.pizhu_tuoyuan_selected);
            }
        });
        mTuyaControlClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeTuyaManager();
            }
        });
        mTuyaNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextTuya();
            }
        });
        mTuyaPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previewTuya();
            }
        });
        mTuyaDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTuYaDeletePopWindow();
            }
        });
        mWordsManagerCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideWordsManager();
            }
        });
        renderWaterMarker();
    }

    void initWordsList(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mWordsRecycleView.setLayoutManager(linearLayoutManager);
        mPreviewWordsAdapter = new PreviewWordsAdapter(PreviewWebviewActivity.this,mPostilWordList);
        mWordsRecycleView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(PreviewWebviewActivity.this));
        mWordsRecycleView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mWordsRecycleView.setAdapter(mPreviewWordsAdapter);
    }

    void saveTuyaToDo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                saveTuYaImage();
                mHandler.sendEmptyMessage(TUYA_SAVE_SUCCESS);
            }
        }).start();
    }

    void resetAllPenTypeIcon(){
        mTuyaCurve.setImageResource(R.drawable.pizhu_quxian);
        mTuyaLine.setImageResource(R.drawable.pizhu_zhixian);
        mTuyaRect.setImageResource(R.drawable.pizhu_juxing);
        mTuyaOval.setImageResource(R.drawable.pizhu_tuoyuan);
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

    public void jumpToEditWordsActivity(PostilWord tag,int editIndex){
        mEditIndex = editIndex;
        Log.i("getResult!","tag = " + tag.toString());
        Intent intent = new Intent(PreviewWebviewActivity.this,AddWordActivity.class);
        intent.putExtra("content", tag.getContent());
        intent.putExtra("x",tag.getxPos());
        intent.putExtra("y",tag.getyPos());
        intent.putExtra("offsetX", tag.getOffsetX());
        intent.putExtra("offsetY", tag.getOffsetY());
        intent.putExtra("scale", tag.getScale());
        intent.putExtra("fromManager", true);//从编辑页来可点击
        startActivityForResult(intent,REQUEST_ADD_TEXT);
    }

    void initWebView(){
        mWebView = findViewById(R.id.mywebview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
//        mWebView.getSettings().setPluginsEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.getSettings().setBuiltInZoomControls(true);
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
                view.setWebViewClient(new PreviewWebClient());
                return true;
            }
        });
        mWebView.setOnScrollChangedCallback(new ObservableWebView.OnScrollChangedCallback() {
            @Override
            public void onScroll(int dx, int dy, float scale) {
                Log.i("onScaleChanged","dx = " + dx + "dy = " +dy);
                mPostilView.updatePositionInfo(dx,dy,scale);
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
                float scale = data.getExtras().getFloat("scale");
                int offsetX = data.getExtras().getInt("offsetX");
                int offsetY = data.getExtras().getInt("offsetY");
                int height = ScreenUtils.getScreebHeight(getApplicationContext());
                int width = ScreenUtils.getScreenWidth(getApplicationContext());
                boolean fromManager = data.getExtras().getBoolean("fromManager",false);
                Log.i("activityR","" + x + ":" + y +":" + scale + ":" + offsetX +":"
                        + offsetY + ":" + fromManager);
                Log.i("activityR","height=" + height + "width=" + width);
                if(fromManager){
                    //从管理页编辑文字
                    if(mPostilWordList != null && mPostilWordList.getList() != null){
                        mPostilWordList.getList().get(mEditIndex).setContent(result);
                        mPreviewWordsAdapter.notifyDataSetChanged();
                        saveTags();
                    }
                } else {
                    if(x != -1 && y != -1){
                        Log.i("activityR","update" + offsetY + ":" + x + ":" + y);
                        mPostilView.updatePostilTag(new PostilWord(offsetX,offsetY,x,y,scale,result));
                    } else {
                        offsetX = (int)mPostilView.getOffsetX();
                        offsetY = (int)mPostilView.getOffsetY();
                        scale = mPostilView.getCurrentNewScale();
                        Log.i("activityR","add" + offsetY+ ":" + width/2 + ":" + height/2);
                        mPostilView.addPostilTag(new PostilWord(offsetX,offsetY,width/2,height/2,scale,result));
                        showTagToolBar();
                        disableZoom();
                        Toast.makeText(PreviewWebviewActivity.this,R.string.preview_move_toast,Toast.LENGTH_LONG).show();
                    }
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

    private class PreviewWebClient extends WebViewClient {

        @Override
        public void onScaleChanged(WebView view, float oldScale, float newScale) {
            super.onScaleChanged(view, oldScale, newScale);
            Log.i("onScaleChanged","oldScale = " + oldScale + ":newScale = " + newScale);
            onScaleChange(view.getScrollX(),view.getScrollY(),oldScale, newScale);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if(mPostilView != null && mPostilView.getVisibility() == View.INVISIBLE){
                Log.i("WebChromeClient","Finish2");
                mPostilView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void onScaleChange(int dx,int dy,float oldScale, float newScale){
        mPostilView.updatePositionInfo(dx,dy,newScale);
    }

    private void saveTuYaImage(){
        Bitmap bm = mPostilView.buildBitmap();
        String savedFile = FileUtils.saveImage(bm, QUALITY);
        if (savedFile != null) {
            float offsetX = mPostilView.getOffsetX();
            float offsetY = mPostilView.getOffsetY();
            Picture picture = new Picture((int)offsetX,(int)offsetY,savedFile,mPostilView.getCurrentNewScale());
            mPostRecord.getPicList().add(picture);
            saveRecord();
        }else{
        }
    }

    private void saveWordsTodo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                saveTagImage();
                mHandler.sendEmptyMessage(WORD_SAVE_SUCCESS);
            }
        }).start();
    }

    private void saveTagImage(){
        Bitmap bm = mPostilView.buildBitmap();
        String savedFile = FileUtils.saveImage(bm, QUALITY);
        if (savedFile != null) {
            PostilWord currentTag = mPostilView.getCurrentPostilTag();
            currentTag.setCanMove(false);
            Log.i("activityR","currentTag = " + currentTag.toString());
            PostilWord tag = new PostilWord(currentTag.getOffsetX(),currentTag.getOffsetY()
                    ,currentTag.getxPos(),currentTag.getyPos(),currentTag.getScale()
                    ,currentTag.getContent(),savedFile);
            tag.setCanMove(currentTag.isCanMove());
            Log.i("activityR","tag = " + tag.toString());
            mPostilWordList.getList().add(tag);
            saveTags();
        }else{
        }
    }

    private void saveRecord(){
        Gson gson = new Gson();
        String recordString = gson.toJson(mPostRecord);
        Log.i("saveImage","saveRecord = " + recordString);
        String key = MD5Utils.stringToMD5("NoteRecords"+ VersionUtils.getVersion() + url);
        Log.i("saveImage","saveRecord key = " + key);
        NoteSharePreferenceUtils.setPrefString(key,recordString);
    }

    private void loadRecord(){
        String key = MD5Utils.stringToMD5("NoteRecords"+ VersionUtils.getVersion() + url);
        String recordString = NoteSharePreferenceUtils.getPrefString(key,null);
        Gson gson = new Gson();
        Log.i("loadRecord","loadRecord = " + recordString);
        Log.i("loadRecord","loadRecord key = " + key);
        if(null != recordString){
            mPostRecord = gson.fromJson(recordString, PostilRecordList.class);
        }
    }

    private void saveTags(){
        Gson gson = new Gson();
        String recordString = gson.toJson(mPostilWordList);
        Log.i("saveImage","saveTag = " + recordString);
        String key = MD5Utils.stringToMD5("NoteTags"+ VersionUtils.getVersion() + url);
        Log.i("saveImage","saveTag key = " + key);
        NoteSharePreferenceUtils.setPrefString(key,recordString);
    }

    private void loadTags(){
        String key = MD5Utils.stringToMD5("NoteTags"+ VersionUtils.getVersion() + url);
        String recordString = NoteSharePreferenceUtils.getPrefString(key,null);
        Gson gson = new Gson();
        Log.i("saveImage","loadTags = " + recordString);
        Log.i("saveImage","loadTags key = " + key);
        if(null != recordString){
            mPostilWordList = gson.fromJson(recordString, PostilWordsList.class);
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

    private void changeToPenState(){
        mPostilView.setMode(PostilView.Mode.DRAW);
        mPostilView.setPenAlpha(255);
        usePenState();
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
    private void clearAllBitmap(){
        if(mPostilView != null){
            mPostRecord = new PostilRecordList();
            mPostilWordList = new PostilWordsList();
            mPostilView.clearAllBitmap();
        }
    }

    private void initMainPopupWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.preview_control_popview_layout, null, false);

        popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true); //设置可以点击
        popupWindow.setTouchable(true);
        contentView.findViewById(R.id.preview_control_tuya).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                toTuyaManager();
            }
        });
        contentView.findViewById(R.id.preview_control_tag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                toWordsManager();
            }
        });
        contentView.findViewById(R.id.preview_control_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
    }
    private void showMainPopWindow() {
        View rootview = LayoutInflater.from(PreviewWebviewActivity.this).inflate(R.layout.pop_main,
                null);
        popupWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
    }

    private void initTuYaDeletePopupWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.preview_tuya_delete_popview_layout, null, false);

        tuyaDeletePopupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        tuyaDeletePopupWindow.setBackgroundDrawable(new BitmapDrawable());
        tuyaDeletePopupWindow.setOutsideTouchable(true); //设置可以点击
        tuyaDeletePopupWindow.setTouchable(true);
        contentView.findViewById(R.id.preview_tuya_delete_current).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.createDialog(PreviewWebviewActivity.this,
                        R.string.preview_delete_current_txt,
                        R.string.preview_tuya_close_ok,
                        R.string.preview_tuya_close_cancel,
                        new DialogUtils.NoteDialogInterface() {
                            @Override
                            public void Ok() {
                                tuyaDeletePopupWindow.dismiss();
                                deleteCurrentTuya();
                            }

                            @Override
                            public void cancel() {

                            }
                        });
            }
        });
        contentView.findViewById(R.id.preview_tuya_delete_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.createDialog(PreviewWebviewActivity.this,
                        R.string.preview_delete_all_txt,
                        R.string.preview_tuya_close_ok,
                        R.string.preview_tuya_close_cancel,
                        new DialogUtils.NoteDialogInterface() {
                            @Override
                            public void Ok() {
                                tuyaDeletePopupWindow.dismiss();
                                deleteAllTuya();
                            }

                            @Override
                            public void cancel() {

                            }
                        });

            }
        });
        contentView.findViewById(R.id.preview_tag_delete_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tuyaDeletePopupWindow.dismiss();
            }
        });
    }
    private void showTuYaDeletePopWindow() {
        View rootview = LayoutInflater.from(PreviewWebviewActivity.this).inflate(R.layout.pop_main,
                null);
        tuyaDeletePopupWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
    }

    private void toWordsManager(){
        mLeftWordsBar.setVisibility(View.VISIBLE);
    }

    private void hideWordsManager(){
        mLeftWordsBar.setVisibility(View.GONE);
    }

    private void toTuyaManager(){
        mPostilView.setCurrentTuYaIndex(0);
        mTuYaControlTopBar.setVisibility(View.VISIBLE);
        mCommonToolBar.setVisibility(View.GONE);
        mPostilView.setMode(PostilView.Mode.MANAGE_TUYA);
        mPostilView.invalidate();
    }

    private void closeTuyaManager(){
        mTuYaControlTopBar.setVisibility(View.GONE);
        mCommonToolBar.setVisibility(View.VISIBLE);
        mPostilView.setMode(PostilView.Mode.NOT_EDIT);
        mPostilView.invalidate();
    }

    public void scrollToYforWebView(int y){
        mWebView.scrollTo(0,y);
    }

    public void nextTuya(){
        mPostilView.nextTuya();
    }

    public void previewTuya(){
        mPostilView.previewTuya();
    }

    public void deleteAllTuya(){
        mPostRecord.setPicList(new ArrayList<Picture>());
        saveRecord();
        mPostilView.invalidate();
    }

    public void deleteCurrentTuya(){
        mPostilView.deleteCurrentTuya();
        saveRecord();
        mPostilView.invalidate();
    }

    public void deleteWordTag(){
//        saveTagImage();
        saveTags();
        mPostilView.setPostilTags(mPostilWordList);
        mPreviewWordsAdapter.notifyDataSetChanged();
    }

     public void enableZoom(){
         if(mWebView != null){
             mWebView.getSettings().setSupportZoom(true);
             mWebView.getSettings().setBuiltInZoomControls(true);
             mPostilView.setEnableMovePage(true);
         }
     }

     public void disableZoom(){
        if(mWebView != null){
            mWebView.getSettings().setSupportZoom(false);
            mWebView.getSettings().setBuiltInZoomControls(false);
            mPostilView.setEnableMovePage(false);
            mPostilView.invalidate();
        }
     }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BitmapCache.getInstance().clear();
    }

    /**
     * 渲染水印
     */

    private void renderWaterMarker() {
        if (StringUtils.isNotEmpty(waterMark) && markNum > 0) {
            for (int i = 0; i < markNum; i++) {
                View.inflate(this, R.layout.row_warter_mark, mWaterMarkLayout);
            }
            for (int i = 0; i < mWaterMarkLayout.getChildCount(); i++) {
                View view = mWaterMarkLayout.getChildAt(i);
                if (view instanceof AutofitTextView) {
                    AutofitTextView autofitTextView = (AutofitTextView) view;
                    autofitTextView.setText(waterMark);
                }
            }

            float screenHeight = ScreenUtils.getScreebHeight(getApplicationContext());
            float screenWidth = ScreenUtils.getScreenWidth(getApplicationContext());

            ViewGroup.LayoutParams params = mWaterMarkLayout.getLayoutParams();
            params.width = (int) Math.max(screenWidth, screenHeight);
            mWaterMarkLayout.setLayoutParams(params);
            int degree = Math.round(
                    (float) (Math.atan(screenHeight / screenWidth) / Math.PI * 180));//最终角度
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                mWaterMarkLayout.setTranslationX(-(screenHeight - screenWidth) / 2);
            }
            mWaterMarkLayout.setRotation(-degree);
        }
    }
}
