package com.ruaho.note.notedemo;

import android.content.Context;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addwords);
        initView();
        hideBar();
        callKeyboard();
    }

    void initView(){
        mEditText = findViewById(R.id.preview_add_words);
        mCloseTxt = findViewById(R.id.preview_word_close_txt);
        mCloseTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddWordActivity.this.finish();
            }
        });
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

        }, 1000);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
