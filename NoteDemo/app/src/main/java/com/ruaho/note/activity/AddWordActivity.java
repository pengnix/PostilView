package com.ruaho.note.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class AddWordActivity extends AppCompatActivity {

    private EditText mEditText;
    private TextView mCloseTxt;
    private TextView mTitleTxt;
    private static int REQUEST_ADD_TEXT_RESULT = 3;
    private int x = -1;
    private int y = -1;
    private int offsetY = -1;
    private int offsetX = -1;
    private float scale = -1.0f;
    private boolean canEdit;
    private boolean fromManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        canEdit = true;
        fromManager = false;
        setContentView(R.layout.activity_addwords);
        initView();
        hideBar();
        initData();
        if(fromManager){
            mEditText.setFocusable(true);
            mEditText.setFocusableInTouchMode(true);
            mEditText.requestFocus();
            mTitleTxt.setText("编辑");
        } else {
            if(canEdit){
                mEditText.setFocusable(true);
                mEditText.setFocusableInTouchMode(true);
                mEditText.requestFocus();
//            callKeyboard();
            } else {
                mEditText.setEnabled(false);
                mEditText.setFocusable(false);
                mEditText.setKeyListener(null);
                mTitleTxt.setText(R.string.preview_word_look);
            }
        }
    }

    void initView(){
        mEditText = findViewById(R.id.preview_add_words);
        mCloseTxt = findViewById(R.id.preview_word_close_txt);
        mTitleTxt = findViewById(R.id.preview_word_close_title_txt);
        mCloseTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                String result = mEditText.getText().toString();
                Log.i("getResult!","result = " + result);
                intent.putExtra("result", result);
                intent.putExtra("x", x);
                intent.putExtra("y", y);
                intent.putExtra("offsetX",offsetX);
                intent.putExtra("offsetY", offsetY);
                intent.putExtra("scale",scale);
                intent.putExtra("fromManager",fromManager);
                AddWordActivity.this.setResult(REQUEST_ADD_TEXT_RESULT, intent);
                AddWordActivity.this.finish();
            }
        });
    }

    void initData(){
        Intent intent = getIntent();
        if(intent != null){
            String content = intent.getStringExtra("content");
            if(content != null){
                canEdit = false;
                mEditText.setText(content);
                x = intent.getIntExtra("x",-1);
                y = intent.getIntExtra("y",-1);
                offsetX = intent.getIntExtra("offsetX",-1);
                offsetY = intent.getIntExtra("offsetY",-1);
                scale = intent.getFloatExtra("scale",-1.0f);
                fromManager = intent.getBooleanExtra("fromManager",false);
            }
        }
    }

    void hideBar(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    void callKeyboard(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {

                Log.i("callKeyboard","1111111");
                InputMethodManager inputManager = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }

        }, 200);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
