package com.cjt_pc.vehicleregulatoryestimate.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjt_pc.vehicleregulatoryestimate.R;
import com.cjt_pc.vehicleregulatoryestimate.entity.DisImgItem;
import com.cjt_pc.vehicleregulatoryestimate.my_view.MultiListView;
import com.cjt_pc.vehicleregulatoryestimate.utils.DensityUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.util.List;

/**
 * Created by cjt-pc on 2015/8/11.
 * Email:879309896@qq.com
 */
public class ImgInfoListAdapter extends ArrayAdapter<DisImgItem> {

    private int resourceId;

    public ImgInfoListAdapter(Context context, int resource, List<DisImgItem> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DisImgItem item = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.ivImg = (ImageView) view.findViewById(R.id.img_dis);
            viewHolder.tvTips = (TextView) view.findViewById(R.id.img_tips);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        // 如果是图片地址是空的话，默认读取mipmap下的
        if (TextUtils.isEmpty(item.getImgPath())) {
            viewHolder.ivImg.setImageResource(item.getImgId());
        } else {
            File tempFile = new File(item.getImgPath());
            if (tempFile.exists()) {
                // 本地图片读取
                int proWidth = DensityUtil.dpTopx(getContext(), 130f);
                int proHeight = DensityUtil.dpTopx(getContext(), 100f);
                Picasso.with(getContext()).load(tempFile)
                        .resize(proWidth, proHeight).centerInside().into(viewHolder.ivImg);
            } else {
                // 网络图片读取
                Picasso.with(getContext()).load(item.getImgPath()).into(viewHolder.ivImg);
            }

//            viewHolder.ivImg.measure(
//                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//            int reWidth = viewHolder.ivImg.getMeasuredWidth();
//            int reHeight = viewHolder.ivImg.getMeasuredHeight();
        }
        viewHolder.tvTips.setText(item.getImgTips());
        return view;
    }

    class ViewHolder {
        ImageView ivImg;
        TextView tvTips;
    }
}
