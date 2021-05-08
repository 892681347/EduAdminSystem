package com.zyh.recyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kongzue.dialog.v2.SelectDialog;
import com.zyh.activities.NoteActivity;
import com.zyh.activities.NoticeActivity;
import com.zyh.beans.Messages;
import com.zyh.fragment.R;

import org.litepal.LitePal;

import java.util.Collections;
import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {
    private final String TAG = "NoticeAdapter";
    private List<Messages> messagesList;
    private NoticeActivity activity;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView headImg;
        ImageView redDot;
        TextView title;
        TextView read;
        TextView context;
        TextView time;
        LinearLayout layout;
        public ViewHolder(View view){
            super(view);
            headImg = view.findViewById(R.id.notice_head);
            redDot = view.findViewById(R.id.notice_red_dot);
            title = view.findViewById(R.id.note_title);
            read = view.findViewById(R.id.notice_read);
            context = view.findViewById(R.id.notice_context);
            time = view.findViewById(R.id.notice_time);
            layout = view.findViewById(R.id.notice_layout);
        }
    }
    public NoticeAdapter(List<Messages> list,NoticeActivity activity){
        // 反转lists
        Collections.reverse(list);
        messagesList = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.notice_item,viewGroup,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Messages msg = messagesList.get(i);
        viewHolder.context.setText(msg.getContent());
        viewHolder.time.setText(msg.getTime());
        if(!msg.isRead()){
            viewHolder.redDot.setVisibility(View.VISIBLE);
            viewHolder.read.setVisibility(View.VISIBLE);
        }else{
            viewHolder.redDot.setVisibility(View.INVISIBLE);
            viewHolder.read.setVisibility(View.INVISIBLE);
        }
        viewHolder.read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.redDot.setVisibility(View.INVISIBLE);
                viewHolder.read.setVisibility(View.INVISIBLE);
                msg.setRead(true);
                Messages messages = new Messages();
                messages.setRead(true);
                messages.updateAll("content = ? and time = ?",msg.getContent(),msg.getTime());
            }
        });
        viewHolder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteDialog(i);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (messagesList==null){
            return 0;
        }
        return messagesList.size();
    }
    private void showDeleteDialog(int postion){
        Messages msg = messagesList.get(postion);
        SelectDialog.show(activity, "提示", "确定刪除此消息", "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LitePal.deleteAll(Messages.class,"content = ? and time = ?",msg.getContent(),msg.getTime());
                messagesList.remove(postion);
                activity.setNnm(messagesList.size()+"");
                notifyDataSetChanged();
            }
        }, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).setCanCancel(true);

    }



}
