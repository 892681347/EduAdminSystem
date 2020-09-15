package com.zyh.activities;

import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zyh.beans.Version;
import com.zyh.fragment.R;

public class About extends AppCompatActivity {
    private ImageView return_back;
    private TextView copy;
    private TextView verson_word;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about);
        return_back = findViewById(R.id.return_img);
        copy = findViewById(R.id.copy);
        verson_word = findViewById(R.id.verson_word);
        verson_word.setText(Version.getVersion());
        return_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager copy = (ClipboardManager) About.this
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                copy.setText(getResources().getString(R.string.github_website));
                Toast.makeText(About.this,"复制成功",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
