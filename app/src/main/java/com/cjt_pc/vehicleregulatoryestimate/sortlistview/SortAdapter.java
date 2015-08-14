package com.cjt_pc.vehicleregulatoryestimate.sortlistview;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cjt_pc.vehicleregulatoryestimate.R;

public class SortAdapter extends BaseAdapter {

    private List<SortModel> list = null;
    private Context mContext;

    public SortAdapter(List<SortModel> list, Context mContext) {
        super();
        this.list = list;
        this.mContext = mContext;
    }

    public void updateListView(List<SortModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final SortModel mContent = list.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.car_info_item,
                    null);

            holder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.title);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        int section = getSectionForPosition(position);

        // �����ǰλ�õ��ڸ÷�������ĸ��Char��λ�� ������Ϊ�ǵ�һ�γ���
        if (position == getPositionForSection(section)) {
            holder.tvLetter.setVisibility(View.VISIBLE);
            holder.tvLetter.setText(mContent.getSortLetters());
        } else {
            holder.tvLetter.setVisibility(View.GONE);
        }

        holder.tvTitle.setText(this.list.get(position).getName());

        return convertView;
    }

    final static class ViewHolder {
        TextView tvLetter;
        TextView tvTitle;
    }

    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }

    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortLetters = list.get(i).getSortLetters();
            char charAt = sortLetters.toUpperCase().charAt(0);
            if (charAt == section) {
                return i;
            }
        }
        return -1;
    }

//    private String getAlpha(String str) {
//        String upperCase = str.trim().substring(0, 1).toUpperCase();
//        if (upperCase.matches("A-Z")) {
//            return upperCase;
//        } else {
//            return "#";
//        }
//    }

//    public Object[] getSections() {
//        return null;
//    }

}
