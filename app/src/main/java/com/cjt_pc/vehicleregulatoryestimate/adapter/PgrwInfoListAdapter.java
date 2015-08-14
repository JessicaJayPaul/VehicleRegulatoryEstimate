package com.cjt_pc.vehicleregulatoryestimate.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjt_pc.vehicleregulatoryestimate.R;
import com.cjt_pc.vehicleregulatoryestimate.activity.TaskInfoActivity;
import com.cjt_pc.vehicleregulatoryestimate.entity.UploadPgrwInfo;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.List;

/**
 * Created by cjt-pc on 2015/6/25.
 * Email:87930986@qq.com
 */
public class PgrwInfoListAdapter extends ArrayAdapter<UploadPgrwInfo> {

    private int resourceId;
    private List<UploadPgrwInfo> uploadPgrwInfoList;

    public PgrwInfoListAdapter(Context context, int resource, List<UploadPgrwInfo> objects) {
        super(context, resource, objects);
        resourceId = resource;
        uploadPgrwInfoList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final UploadPgrwInfo info = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_photo = (ImageView) view.findViewById(R.id.photo);
            viewHolder.tv_num = (TextView) view.findViewById(R.id.number);
            viewHolder.tv_finish = (TextView) view.findViewById(R.id.finish);
            viewHolder.tv_status = (TextView) view.findViewById(R.id.task_status);
            viewHolder.tv_estimate = (TextView) view.findViewById(R.id.pre_price);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tv_num.setText("单号：" + info.getPgdh());
        if (info.getZt().equals("-1")) {
            viewHolder.tv_finish.setText("未完成");
            viewHolder.tv_status.setText("状态：" + "未上传");
            // 如果是本地数据就加上长按监听事件
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), TaskInfoActivity.class);
                    intent.putExtra("info", info);
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setTitle("提醒")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setMessage("确定要删除这一项本地任务吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DataSupport.delete(UploadPgrwInfo.class, info.getId());
                                    String imgListPath = Environment.getExternalStorageDirectory().getPath()
                                            + "/jiuche" + "/imgDir" + info.getId();
                                    TaskInfoActivity.deleteDir(new File(imgListPath));
                                    Toast.makeText(getContext(), "删除本地文件成功！", Toast.LENGTH_SHORT).show();

                                    uploadPgrwInfoList.remove(info);
                                    notifyDataSetChanged();
                                }
                            }).setNegativeButton("取消", null).create();
                    dialog.setCancelable(false);
                    dialog.show();
                    return true;
                }
            });
        } else {
            viewHolder.tv_finish.setText("已完成");
            viewHolder.tv_status.setText("状态：" + info.getZtsm());
        }

        return view;
    }

    class ViewHolder {
        private ImageView iv_photo;
        private TextView tv_num;
        private TextView tv_finish;
        private TextView tv_status;
        private TextView tv_estimate;
    }
}
