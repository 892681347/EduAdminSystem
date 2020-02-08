package com.zyh.recyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zyh.beans.GradeBean;
import com.zyh.fragment.R;

import org.w3c.dom.Text;

import java.util.List;

public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.ViewHolder> {
    private List<GradeBean.Datas> mGrade;
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView courseNameText;
        TextView scoreText;
        TextView xuefenText;
        TextView pointText;
        TextView methodText;
        TextView natureText;
        public ViewHolder(View view){
            super(view);
            courseNameText = (TextView)view.findViewById(R.id.course_name);
            scoreText = (TextView)view.findViewById(R.id.score);
            xuefenText = (TextView)view.findViewById(R.id.xuefen);
            pointText = (TextView)view.findViewById(R.id.point);
            methodText = (TextView)view.findViewById(R.id.method);
            natureText = (TextView)view.findViewById(R.id.nature);
        }
    }
    public GradeAdapter(List<GradeBean.Datas> gradeList){
        mGrade = gradeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.grade_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView courseNameText = holder.courseNameText;
        GradeBean.Datas grade = mGrade.get(position);
        String courseName = grade.getCourseName();
        int size = courseName.length();
        if (size>=12){
            courseNameText.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) courseNameText.getLayoutParams();
            linearParams.weight *= 0.6;
            courseNameText.setLayoutParams(linearParams);
        }else if(size>=9){
            courseNameText.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        }
        courseNameText.setText(courseName);
        holder.scoreText.setText(grade.getScore());
        holder.xuefenText.setText(grade.getXuefen());
        holder.pointText.setText(grade.getPoint());
        holder.methodText.setText(grade.getMethod());
        holder.natureText.setText(grade.getNature());
    }

    @Override
    public int getItemCount() {
        if (mGrade==null){
            return 0;
        }
        return mGrade.size();
    }

}
