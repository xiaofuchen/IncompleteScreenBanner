package com.xiaofu.incompletescreenbannerdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.xiaofu.incompletescreenbannerdemo.base.MyRecyclerViewBannerBase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    MyRecyclerViewBanner banner, banner1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        banner = (MyRecyclerViewBanner) findViewById(R.id.recycle);
        banner1 = (MyRecyclerViewBanner) findViewById(R.id.recycle1);

        List<String> list = new ArrayList<>();
        list.add("http://img0.imgtn.bdimg.com/it/u=1060586016,167727750&fm=26&gp=0.jpg");
        list.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=3336937407,1706111605&fm=26&gp=0.jpg");
        list.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=4233767024,491915230&fm=26&gp=0.jpg");
        list.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2960231721,1241541633&fm=26&gp=0.jpg");
        list.add("http://img0.imgtn.bdimg.com/it/u=1515174754,211221812&fm=26&gp=0.jpg");

        banner.initBannerImageView(list, new MyRecyclerViewBannerBase.OnBannerItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(MainActivity.this, "clicked:" + position, Toast.LENGTH_SHORT).show();
            }
        });

        banner1.initBannerImageView(list, new MyRecyclerViewBannerBase.OnBannerItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(MainActivity.this, "clicked:" + position, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
