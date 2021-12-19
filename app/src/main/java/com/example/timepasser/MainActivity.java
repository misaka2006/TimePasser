package com.example.timepasser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timepasser.util.SharedHelper;
import com.example.timepasser.util.ToastUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView tvClock;
    private TextView tvInfo;
    private Button btnSetting;
    private boolean exit=false;
    private boolean isSetting=true;
    private int infoFlag=0;
    private String SurplusDay="";
    private GestureDetector mDetector;
    private final static int MIN_MOVE = 200;   //最小距离
    private MyGestureListener mgListener;
    private MediaPlayer mp;
    private String DateEvent="周末";
    private SharedHelper sh;
    private String date="";
    private ToastUtil toastUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sh=new SharedHelper(getApplicationContext());

        hideUIMenu();
        GetConfig();
        tvClock=findViewById(R.id.tv_clock);
        tvInfo=findViewById(R.id.tv_info);
        btnSetting=findViewById(R.id.btn_setting);
        mp=MediaPlayer.create(this,R.raw.homo);
        new ClockThread().start();
        toastUtil=new ToastUtil(this);
        //new AlarmThread().start();
        tvInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoFlag++;
                infoFlag%=3;
            }
        });
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,SettingActivity.class);
                try {
                    startActivityForResult(intent,0x10);
                }
                catch (Exception e)
                {
                    toastUtil.showToast(e.toString());
                }
            }
        });
        mgListener = new MyGestureListener();
        mDetector = new GestureDetector(this, mgListener);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==0x10&&resultCode==0x10)
        {
            assert data != null;
            Bundle bundle=data.getExtras();
            date=bundle.getString("Date");
            DateEvent=bundle.getString("Name");
            toastUtil.showToast("设置成功！");
            isSetting=true;
        }
    }

    public class ClockThread extends Thread
    {
        @Override
        public void run() {
            super.run();
            try {
                do {
                    Thread.sleep(100);
                    Message msg=new Message();
                    msg.what=114514;
                    handler.sendMessage(msg);
                }while (!exit);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
//    public class AlarmThread extends Thread
//    {
//        @SuppressLint("SimpleDateFormat")
//        @Override
//        public void run() {
//            super.run();
//            try {
//                do {
//                    if(new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis())).equals("07:00:00"))
//                    {
//                        mp.start();
//                    }
//
//                }while (!exit);
//            }catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }
    private Handler handler=new Handler(new Handler.Callback() {
        @SuppressLint("SimpleDateFormat")
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what)
            {
                case 114514:
                    tvClock.setText(new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis())));
                    switch (infoFlag)
                    {
                        case 0:
                            tvInfo.setText(getSurplusDay());
                            break;
                        case 1:
                            tvInfo.setText(getDate());
                            break;
                        case 2:
                            tvInfo.setText(getPower());
                    }
                    break;
                case 0x01:
                    tvInfo.setText(SurplusDay);
                    break;
            }
            return false;
        }
    });

    protected void hideUIMenu() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getSupportActionBar().hide();
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.release();
        exit=true;
    }
    private String getSurplusDay() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        /*天数差*/
        Date fromDate1 = new Date(System.currentTimeMillis());
        Date toDate1 = new Date();
        try {
            toDate1=simpleFormat.parse(date);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        long from1 = fromDate1.getTime();

        long to1 = toDate1.getTime();
        int days = (int) ((to1 - from1-8*3600) / (1000 * 60 * 60 * 24));
        //System.out.println("两个时间之间的天数差为：" + days);

        /*小时差*/
        /*Date fromDate2 = simpleFormat.parse("2018-03-01 12:00");
        Date toDate2 = simpleFormat.parse("2018-03-12 12:00");
        long from2 = fromDate2.getTime();
        long to2 = toDate2.getTime();
        int hours = (int) ((to2 - from2) / (1000 * 60 * 60));
        System.out.println("两个时间之间的小时差为：" + hours);

        *//*分钟差*//*
        Date fromDate3 = simpleFormat.parse("2018-03-01 12:00");
        Date toDate3 = simpleFormat.parse("2018-03-12 12:00");
        long from3 = fromDate3.getTime();
        long to3 = toDate3.getTime();
        int minutes = (int) ((to3 - from3) / (1000 * 60));
        System.out.println("两个时间之间的分钟差为：" + minutes);*/
        return (isSetting?"距离"+DateEvent+"还有"+(days+1)+"天":"暂无事件");
    }
    private String getWeather()
    {
        return "不知所云";
    }
    private String getDate()
    {
        return new SimpleDateFormat("yyyy年MM月dd日 E").format(new Date(System.currentTimeMillis()));
    }

    private String getPower()
    {
        BatteryManager manager = (BatteryManager)getApplicationContext().getSystemService(getApplicationContext().BATTERY_SERVICE);
        int currentLevel = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        return "电池电量："+currentLevel;
    }
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float v, float v1) {
            if(e1.getY() - e2.getY() > MIN_MOVE){
                //startActivity(new Intent(MainActivity.this, CountDownActivity.class));
                Toast.makeText(MainActivity.this, "没事划拉屏幕干啥", Toast.LENGTH_SHORT).show();
            }else if(e1.getY() - e2.getY()  < MIN_MOVE){
                //finish();
                //startActivity(new Intent(MainActivity.this, CountDownActivity.class));
                Toast.makeText(MainActivity.this,"好好学习", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    }


    private void GetConfig() {
        super.onStart();
        Map<String,String> data=sh.read();
        if(data.get("D").equals(""))
        {
            isSetting=false;
        }
        date=data.get("Y")+"-"+data.get("M")+"-"+data.get("D")+" 00:00";
        DateEvent=data.get("N");
    }
}