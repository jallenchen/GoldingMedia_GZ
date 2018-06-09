package com.goldingmedia.mvp.view.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldingmedia.BaseActivity;
import com.goldingmedia.GDApplication;
import com.goldingmedia.R;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.TruckMediaProtos;
import com.goldingmedia.utils.StreamTool;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReadyBookActivity extends BaseActivity {
    private ImageView imageView;
    private TextView textView;
    private TruckMediaProtos.CTruckMediaNode truckMediaNode;
    private String titleName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready_book);
        imageView = (ImageView) findViewById(R.id.ebook_img);
        textView = (TextView) findViewById(R.id.ebook_brief);
        initData();
    }

    private void initData() {
        String foldername ="";
        try {
            InputStream in = null;
            truckMediaNode = (TruckMediaProtos.CTruckMediaNode) getIntent().getBundleExtra("ebookB").getSerializable("ebookV");
            titleName = truckMediaNode.getMediaInfo().getTruckMeta().getTruckTitle();
            foldername = truckMediaNode.getMediaInfo().getTruckMeta().getTruckFilename();
            in = new FileInputStream(Contant.MYAPP_PATH + "ebook" + "/" + foldername+"/"+foldername+".txt");
            StreamTool.readBook(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imgPath = Contant.getTruckMetaNodePath(truckMediaNode.getMediaInfo().getCategoryId(), truckMediaNode.getMediaInfo().
                getCategorySubId(), foldername,true);
        imageView.setImageBitmap( BitmapFactory.decodeFile(imgPath+"/"+foldername+".jpg"));

        textView.setText(truckMediaNode.getMediaInfo().getTruckMeta().getTruckDesc());
    }

    public void onReadBook(View v) {
        Intent intent = new Intent(this, ReadBookActivity.class);
        intent.putExtra("ebookName", titleName);
        startActivity(intent);
    }

    public void onBack(View v){
        StreamTool.getEbookData().clear();
        finish();
    }
}
