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
    private static int REQUEST_ADD_TEXT_RESULT = 3;
    private int x = -1;
    private int y = -1;
    private int offsetY = -1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addwords);
        initView();
        hideBar();
        initData();
        mEditText.setFocusable(true);
        mEditText.setFocusableInTouchMode(true);
        mEditText.requestFocus();
        callKeyboard();
    }

    void initView(){
        mEditText = findViewById(R.id.preview_add_words);
        mCloseTxt = findViewById(R.id.preview_word_close_txt);
        mCloseTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                String result = mEditText.getText().toString();
                Log.i("getResult!","result = " + result);
                intent.putExtra("result", result);
                intent.putExtra("x", x);
                intent.putExtra("y", y);
                intent.putExtra("offsetY", offsetY);
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
                mEditText.setText(content);
                x = intent.getIntExtra("x",-1);
                y = intent.getIntExtra("y",-1);
                offsetY = intent.getIntExtra("offsetY",-1);
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
