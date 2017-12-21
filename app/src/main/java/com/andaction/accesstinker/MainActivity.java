package com.andaction.accesstinker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.andaction.accesstinker.tinker.TinkerManager;

public class MainActivity extends AppCompatActivity {

    TextView tvBug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvBug = findViewById(R.id.tv_bug);
    }

    public void fixBug(View view) {
        String patchPath = getExternalCacheDir().getAbsolutePath() + "/tinker-patch/at.apk";
        TinkerManager.loadPatch(patchPath);
    }

}
