package com.zyh.activities;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.zyh.beans.Messages;
import com.zyh.fragment.R;
import com.zyh.recyclerView.NoticeAdapter;

import org.litepal.LitePal;

import java.util.List;

public class NoticeActivity extends AppCompatActivity {
    private final String TAG = "NoticeActivity";
    private RecyclerView recyclerView;
    private ImageView returnBack;
    private TextView num;
    private TextView readALL;
    private List<Messages> messagesList;
    private NoticeAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_notice);
        num = findViewById(R.id.notice_num);
        readALL = findViewById(R.id.notice_read_all);
        recyclerView = findViewById(R.id.notice_recycler_view);
        returnBack = findViewById(R.id.notice_return);
        messagesList = LitePal.findAll(Messages.class);
        showRecyclerView();
        num.setText(messagesList.size()+"");
        readALL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Messages messages = new Messages();
                messages.setRead(true);
                for(Messages msg : messagesList){
                    msg.setRead(true);
                    messages.updateAll("content = ? and time = ?",msg.getContent(),msg.getTime());
                }
                adapter.notifyDataSetChanged();
            }
        });
        returnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void showRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(NoticeActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NoticeAdapter(messagesList,NoticeActivity.this);
        recyclerView.setAdapter(adapter);
    }
    public void setNnm(String count){
        num.setText(count);
    }
}
