package com.cjt_pc.vehicleregulatoryestimate.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.graphics.BitmapCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjt_pc.vehicleregulatoryestimate.R;
import com.cjt_pc.vehicleregulatoryestimate.adapter.ImgInfoListAdapter;
import com.cjt_pc.vehicleregulatoryestimate.entity.DisImgItem;
import com.cjt_pc.vehicleregulatoryestimate.entity.UploadImageEntity;
import com.cjt_pc.vehicleregulatoryestimate.entity.UploadPgrwInfo;
import com.cjt_pc.vehicleregulatoryestimate.my_view.MyTitleView;
import com.cjt_pc.vehicleregulatoryestimate.utils.ImageCompressUtil;
import com.cjt_pc.vehicleregulatoryestimate.utils.SystemUtil;
import com.squareup.picasso.Picasso;

import org.litepal.crud.DataSupport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cjt-pc on 2015/8/11.
 * Email:879309896@qq.com
 */
public class DisImgActivity extends Activity implements AdapterView.OnItemClickListener {

    // 调用系统相机
    public final static int START_SYSTEM_CAMERA = 0;
    // 本地相册
    public final static int LOCAL_PIC = 1;
    // 自定义标题栏
    private MyTitleView titleView;
    // 是否开启拍照辅助
    private boolean isShowHelper = true;
    // 是否正在编辑
    private boolean isEditing;
    // 显示图片的网格布局
    private GridView gvDisImg;
    // 启动此activity的ll项
    private int llIndex;
    // gridView的item项
    private int gvIndex;
    // 该评估任务的taskId，只要点击进来就存入了本地数据，从而自动生成了唯一的taskId
    private int taskId;
    // 该评估任务的UploadImageEntity集合，可以为空，由taskId决定
    List<UploadImageEntity> uploadImgList = new ArrayList<>();
    // GridView的数据源list集合
    private List<DisImgItem> itemList = new ArrayList<>();
    // GridView的适配器
    private ImgInfoListAdapter adapter;
    // gridView固有的项数目
    private int itemCount;

    // 拍照辅助页面
    private ScrollView svCameraHelper;
    private ImageView ivImg_1, ivImg_2, ivImg_3;
    private TextView tvInfo_1, tvInfo_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dis_img_layout);
        getEnterData();
        initTitleView();
        initGridView();
    }

    private void initGridView() {
        gvDisImg = (GridView) findViewById(R.id.gv_add_img);

        // 初始化固定的item
        String[] imgTips = null;
        try {
            Field field = R.array.class.getDeclaredField("img_tips_" + llIndex);
            int id = field.getInt(R.array.class);
            imgTips = getResources().getStringArray(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (imgTips != null) {
            itemCount = imgTips.length;
            for (String temp : imgTips) {
                DisImgItem item = new DisImgItem();
                item.setImgId(R.mipmap.add_img);
                item.setImgTips(temp);
                itemList.add(item);
            }
        }
        updateItemList();
        adapter = new ImgInfoListAdapter(this, R.layout.img_grid_item, itemList);
        if (isEditing) {
            // 在编辑状态下如果开启了拍照辅助就初始化scrollView及其子view
            if (isShowHelper) {
                initScrollView();
            }
            // 如果正在编辑的话，item是可点击的，而且最后面还有一个“补充”的项
            addAddItem();
            gvDisImg.setOnItemClickListener(this);
        }
        gvDisImg.setAdapter(adapter);
    }

    private void addAddItem() {
        DisImgItem item = new DisImgItem();
        item.setImgId(R.mipmap.add_img);
        item.setImgTips("补充图片");
        itemList.add(item);
    }

    private void updateItemList() {
        // 获取到当前数据库存储图片的集合（存在filePath）
        // 值得注意的是任务表和图片表是一对多，uploadpgrwinfo_id在图片实体添加到任务实体中自动生成
        uploadImgList = DataSupport.where("uploadpgrwinfo_id = ? and filefl = ? ", taskId + "", getFileFl(llIndex) + "")
                .find(UploadImageEntity.class);
        // 遍历当前当前数据库存储的图片集合，实例化为gridView的item
        for (UploadImageEntity info : uploadImgList) {
            int index = Integer.parseInt(info.getFilewz());
            if (index <= itemList.size()) {
                itemList.get(index - 1).setImgPath(info.getFilerealpath());
            } else {
                // 如果超出固定Item数目，则new一个item出来
                DisImgItem item = new DisImgItem();
                item.setImgPath(info.getFilerealpath());
                itemList.add(item);
            }
        }
    }

    // 根据传入的ll项index，获取分类的字符串
    private String getFileFl(int llIndex) {
        switch (llIndex) {
            case 1:
                return "djz";
            case 2:
                return "xsz";
            case 3:
                return "mp";
            case 4:
                return "cswg";
            case 5:
                return "ctgj";
            case 6:
                return "clns";
            case 7:
                return "ycbx";
            case 8:
                return "cybc";
        }
        return null;
    }

    public void getEnterData() {
        isEditing = getIntent().getBooleanExtra("isEditing", true);
        llIndex = getIntent().getIntExtra("llIndex", 0);
        taskId = getIntent().getIntExtra("taskId", 0);
        isShowHelper = getSharedPreferences("data", MODE_PRIVATE).getBoolean("isShowHelper", true);
    }

    private void initTitleView() {
        titleView = (MyTitleView) findViewById(R.id.title_view);
        titleView.setLeftButton(R.mipmap.back_btn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        titleView.setTitleName(isEditing ? "添加照片" : "查看照片");
        // 如果是编辑状态并且开启了照相辅助才初始化标题栏右侧相机按钮并隐藏
        if (isEditing && isShowHelper) {
            titleView.setRightButton(R.mipmap.camera_nobg, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCameraDialog();
                }
            });
        }
        titleView.getIbtRight().setVisibility(View.GONE);
        titleView.setBg("#303F9F");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        gvIndex = position + 1;
        if (isShowHelper) {
            // 开启了照相辅助后如果点击的index超出了原有的项，直接拍照
            if (gvIndex > itemCount) {
                showCameraDialog();
            } else {
                titleView.setTitleName("拍照辅助");
                titleView.getIbtRight().setVisibility(View.VISIBLE);
                gvDisImg.setVisibility(View.GONE);
                svCameraHelper.setVisibility(View.VISIBLE);
                setSv();
            }
        } else {
            showCameraDialog();
        }
    }

    @Override
    public void onBackPressed() {
        if (gvDisImg.getVisibility() == View.GONE) {
            hideSv();
        } else {
            super.onBackPressed();
        }
    }

    // 开启了照相辅助后从scrollView跳转到gridView
    private void hideSv() {
        gvDisImg.setVisibility(View.VISIBLE);
        svCameraHelper.setVisibility(View.GONE);
        titleView.setTitleName("添加照片");
        titleView.getIbtRight().setVisibility(View.GONE);
    }

    private void initScrollView() {
        svCameraHelper = (ScrollView) findViewById(R.id.sv_camera_help);
        ivImg_1 = (ImageView) findViewById(R.id.img_1);
        ivImg_2 = (ImageView) findViewById(R.id.img_2);
        ivImg_3 = (ImageView) findViewById(R.id.img_3);
        tvInfo_1 = (TextView) findViewById(R.id.info_1);
        tvInfo_2 = (TextView) findViewById(R.id.info_2);
    }

    // 动态获取id从而设置资源
    private void setSv() {
        String strIv_1 = "img_info_" + llIndex + "_" + gvIndex + "_" + 1;
        int ivId_1 = getResources().getIdentifier(strIv_1, "mipmap", getPackageName());
        ivImg_1.setImageResource(ivId_1);
        String strIv_2 = "img_info_" + llIndex + "_" + gvIndex + "_" + 2;
        int ivId_2 = getResources().getIdentifier(strIv_2, "mipmap", getPackageName());
        ivImg_2.setImageResource(ivId_2);
        String strTv_1 = "img_info_text_" + llIndex + "_" + gvIndex + "_" + 1;
        int tvId_1 = getResources().getIdentifier(strTv_1, "string", getPackageName());
        tvInfo_1.setText(getResources().getString(tvId_1));
        if ((llIndex == 5) && (gvIndex == 12 || gvIndex == 4 || gvIndex == 5 || gvIndex == 9)) {
            String strIv_3 = "img_info_" + llIndex + "_" + gvIndex + "_" + 3;
            int ivId_3 = getResources().getIdentifier(strIv_3, "mipmap", getPackageName());
            ivImg_3.setImageResource(ivId_3);
        } else {
            String strTv_2 = "img_info_text_" + llIndex + "_" + gvIndex + "_" + 2;
            int tvId_2 = getResources().getIdentifier(strTv_2, "string", getPackageName());
            tvInfo_2.setText(getResources().getString(tvId_2));
        }
    }

    private void showCameraDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("选择拍照方式...");
        builder.setPositiveButton("拍照", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (SystemUtil.isHasSdCard()) {
                    //指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                    Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "image.jpg"));
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, START_SYSTEM_CAMERA);
                } else {
                    Toast.makeText(DisImgActivity.this, "未检查到sd卡！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("相册", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, LOCAL_PIC);
            }
        });
        builder.setNeutralButton("取消", null).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            File pic = getPicFile();
            if (pic == null) {
                Toast.makeText(this, "创建图片文件时出现错误！", Toast.LENGTH_SHORT).show();
                return;
            }
            switch (requestCode) {
                case START_SYSTEM_CAMERA:
                    savePicWithSysCamera(pic);
                    savePicToDB(pic);
                    updateItemList();
                    break;
                case LOCAL_PIC:
                    savePicWithAlbum(pic, data);
                    savePicToDB(pic);
                    updateItemList();
                    break;
            }
            if (gvIndex == itemList.size()) {
                itemList.get(gvIndex - 1).setImgTips("");
                addAddItem();
            }
            // 每次拍完照片清楚该图片缓存
            Picasso.with(this).invalidate(pic);
            adapter.notifyDataSetChanged();
            // 在拍完照片回来时如果gv是隐藏的要执行hideSv方法
            if (gvDisImg.getVisibility() == View.GONE) {
                hideSv();
            }
        }
    }

    private void savePicWithAlbum(File pic, Intent data) {
        Uri uri = data.getData();
        Cursor cursor = getContentResolver().query(uri, null,
                null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            String imgPath = cursor.getString(1); // 图片文件路径
            cursor.close();
            // 在获取到所选相册图片路径后复制图片到指定路径
            ImageCompressUtil.compressPic(new File(imgPath), pic);
        }
    }

    // 采用比缓冲更加高效的文件复制方式
    public void fileChannelCopy(File curFile, File tarFile) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fis = new FileInputStream(curFile);
            fos = new FileOutputStream(tarFile);
            in = fis.getChannel();//得到对应的文件通道
            out = fos.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null)
                    fis.close();
                if (in != null)
                    in.close();
                if (fos != null)
                    fos.close();
                if (out != null)
                    out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void
    savePicWithSysCamera(File pic) {
        File imgCache = new File(Environment.getExternalStorageDirectory() + "/image.jpg");
        if (imgCache.exists()) {
            ImageCompressUtil.compressPic(imgCache, pic);
        } else {
            Toast.makeText(this, "保存图片出现未知错误！", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePicToDB(File pic) {
        // 获取当前任务实体类
        UploadPgrwInfo uploadPgrwInfo = DataSupport.find(UploadPgrwInfo.class, taskId);
        // 查找当前任务当前位置的图片集合
        List<UploadImageEntity> infos = DataSupport.where("djh = ? and filefl = ? and filewz = ?",
                uploadPgrwInfo.getPgdh(),
                getFileFl(llIndex) + "",
                gvIndex + "")
                .find(UploadImageEntity.class);
        // 集合不会空说明之前已经有图了，要删掉再保存
        if (!infos.isEmpty()) {
            for (UploadImageEntity entity : infos) {
                DataSupport.delete(UploadImageEntity.class, entity.getId());
                Picasso.with(this).invalidate(entity.getFilerealpath());
            }
        }
        UploadImageEntity uploadImageEntity = new UploadImageEntity();
        uploadImageEntity.setDjh(uploadPgrwInfo.getPgdh());
        uploadImageEntity.setFilefl(getFileFl(llIndex));
        uploadImageEntity.setExfile(".jpg");
        uploadImageEntity.setFilewz(gvIndex + "");
        uploadImageEntity.setFilerealpath(pic.getAbsolutePath());
        uploadImageEntity.setFilenames(pic.getName());
        uploadImageEntity.save();

        uploadPgrwInfo.getImageEntityList().add(uploadImageEntity);
        uploadPgrwInfo.save();
    }

    private File getPicFile() {
        // 创建该任务图片缓存的文件夹
        String imgListPath = Environment.getExternalStorageDirectory().getPath()
                + "/jiuche" + "/imgDir" + taskId;
        File imgListDir = new File(imgListPath);
        if (!imgListDir.exists()) {
            if (!imgListDir.mkdirs()) {
                Log.d("cjt-pc", "创建文件夹失败！");
                return null;
            } else {
                Log.d("cjt-pc", "创建文件夹成功！");
            }
        }
        // 创建本次照片文件
        String picName = "img_" + llIndex + "_" + gvIndex + ".jpg";
        File pic = new File(imgListDir, picName);
        // 倘若文件存在createNewFile会自动覆盖
        try {
            if (pic.exists()) {
                if (!pic.delete())
                    return null;
            }
            if (!pic.createNewFile()) {
                Log.d("cjt-pc", "创建新图片失败！");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pic;
    }
}
