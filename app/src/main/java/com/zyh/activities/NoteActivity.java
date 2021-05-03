package com.zyh.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kongzue.dialog.v2.DialogSettings;
import com.kongzue.dialog.v2.SelectDialog;
import com.zyh.beans.LoginBean;
import com.zyh.beans.Note;
import com.zyh.fragment.R;
import com.zyh.utills.CalendarUtil;

import org.litepal.LitePal;

import static com.kongzue.dialog.v2.DialogSettings.STYLE_IOS;

public class NoteActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "NoteActivity";
    private Context context = NoteActivity.this;
    private boolean isNew;
    private Note note;
    private EditText nameEdit;
    private TextView semesterText;
    private TextView weekText;
    private TextView timeText;

    private ImageView back;
    private TextView save;
    private LinearLayout deleteLinear;
    private TextView deleteText;
    private TextView noteTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_note);
        initData();
    }
    private void initData(){
        note = new Note();
        nameEdit = findViewById(R.id.note_name);
        semesterText = findViewById(R.id.note_semester);
        weekText = findViewById(R.id.note_week);
        timeText = findViewById(R.id.note_time);
        back = findViewById(R.id.back);
        save = findViewById(R.id.save);
        deleteLinear = findViewById(R.id.delete_linear);
        deleteText = findViewById(R.id.delete_text);
        noteTitle = findViewById(R.id.note_title);

        Intent intent = getIntent();
        note.setUsername(intent.getStringExtra("username"));
        note.setSemester(intent.getStringExtra("semester"));
        note.setWeek(intent.getStringExtra("week"));
        note.setDayInWeek(intent.getIntExtra("dayInWeek",1));
        note.setTime(intent.getIntExtra("time",1));
        isNew = intent.getBooleanExtra("isNew",true);

        semesterText.setText(note.getSemester());
        weekText.setText("第"+note.getWeek()+"周");
        timeText.setText(CalendarUtil.getWeekday(note.getDayInWeek())+"  第"+note.getTime()+"节");
        if(!isNew){
            Note noteTemp = LitePal.where("semester = ? and week = ? and " +
                            "dayInWeek = ? and time = ?",note.getSemester(),note.getWeek(),
                    note.getDayInWeek()+"",note.getTime()+"").findFirst(Note.class);
            nameEdit.setText(noteTemp.getName());
            noteTitle.setText("修改备注");
            deleteLinear.setVisibility(View.VISIBLE);
        }
        deleteText.setOnClickListener(this);
        back.setOnClickListener(this);
        save.setOnClickListener(this);
    }
    public static void actionStart(Context context, String username, String semester, String week, int dayInWeek, int time, boolean isNew){
        Intent intent = new Intent(context,NoteActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("semester", semester);
        intent.putExtra("week", week);
        intent.putExtra("dayInWeek", dayInWeek);
        intent.putExtra("time", time);
        intent.putExtra("isNew", isNew);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                onBackPressed();
                break;
            case R.id.save:
                String name = nameEdit.getText().toString();
                if(name.equals("")) {
                    Toast.makeText(NoteActivity.this, "事件不可为空", Toast.LENGTH_SHORT).show();
                }else{
                    LitePal.deleteAll(Note.class,"username = ? and semester = ? and week = ? and " +
                                    "dayInWeek = ? and time = ?",note.getUsername(),note.getSemester(),note.getWeek(),
                            note.getDayInWeek()+"",note.getTime()+"");
                    note.setName(nameEdit.getText().toString());
                    note.save();
                    Log.i(TAG,"username:  "+note.getUsername());
                    NoteActivity.this.finish();
                }
                break;
            case R.id.delete_text:
                showDeleteDialog();
            default:
                break;

        }
    }
    private void showReturnDialog(){
        SelectDialog.show(context, "提示", "确定放弃添加此备注", "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NoteActivity.this.finish();
            }
        }, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

    }
    private void showDeleteDialog(){
        SelectDialog.show(context, "提示", "确定删除此备注", "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LitePal.deleteAll(Note.class,"username = ? and semester = ? and week = ? and " +
                                "dayInWeek = ? and time = ?",note.getUsername(),note.getSemester(),note.getWeek(),
                        note.getDayInWeek()+"",note.getTime()+"");
                NoteActivity.this.finish();
            }
        }, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(isNew){
            showReturnDialog();
        }else{
            NoteActivity.this.finish();
        }

    }
}
