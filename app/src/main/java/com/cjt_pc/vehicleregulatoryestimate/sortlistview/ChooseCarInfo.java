package com.cjt_pc.vehicleregulatoryestimate.sortlistview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.cjt_pc.vehicleregulatoryestimate.R;

public class ChooseCarInfo extends Activity {

    private ListView sortListView;
    SideBar sideBar;
    TextView dialog;
    private SortAdapter adapter;
    ClearEditText mClearEditText;

    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;

    private PinyinComparator pinyinComparator;

    private List<String> names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_car_info_layout);
        names = getIntent().getStringArrayListExtra("names");
        init();
    }

    private void init() {
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();

        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setmTextDialog(dialog);

        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int positionForSection = adapter.getPositionForSection(s
                        .charAt(0));
                if (positionForSection != -1) {
                    sortListView.setSelection(positionForSection);
                }
            }
        });
        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        sortListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String name = ((SortModel) adapter.getItem(position)).getName();
                Intent intent = new Intent();
                intent.putExtra("name", name);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        SourceDateList = filldeData(names);

        Collections.sort(SourceDateList, pinyinComparator);
        adapter = new SortAdapter(SourceDateList, this);
        sortListView.setAdapter(adapter);

        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

        mClearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
    }

    private List<SortModel> filldeData(List<String> names) {
        List<SortModel> models = new ArrayList<>();
        for (String name : names) {
            SortModel sortModel = new SortModel();
            sortModel.setName(name);
            String pinyin = characterParser.getSelling(name);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }
            models.add(sortModel);
        }
        return models;
    }

    private void filterData(String str) {
        List<SortModel> filterDateList = new ArrayList<>();

        if (TextUtils.isEmpty(str)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (SortModel sortModel : SourceDateList) {
                String name = sortModel.getName();
                if (name.contains(str) || characterParser.getSelling(name).startsWith(str)) {
                    filterDateList.add(sortModel);
                }
            }
        }
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }

}
