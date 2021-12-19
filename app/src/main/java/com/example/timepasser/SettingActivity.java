package com.example.timepasser;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.timepasser.util.SharedHelper;
import com.example.timepasser.util.ToastUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SettingActivity extends AppCompatActivity {

    private Button btnReturn;
    private Button btnCancel;
    private EditText etName;
    private EditText etYear;
    private EditText etMonth;
    private EditText etDay;
    private ToastUtil toastUtil;
    private SharedHelper sh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        hideUIMenu();
        initView();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(0x11);
                finish();
            }
        });
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                String res=etYear.getText().toString()+"-"+etMonth.getText().toString()+"-"
                        +etDay.getText().toString()+" 00:00";
                Date date=new Date();
                try {
                    date=simpleFormat.parse(res);
                    String en=etName.getText().toString();
                    if(en.length()>0&&en.length()<=8)
                    {
                        sh.save(etYear.getText().toString(),etMonth.getText().toString(),etDay.getText().toString()
                        ,en);
                        Intent intent=getIntent();
                        Bundle bundle=new Bundle();
                        bundle.putString("Date",res);
                        bundle.putString("Name",en);
                        intent.putExtras(bundle);
                        setResult(0x10,intent);
                        finish();
                    }
                    else toastUtil.showToast("事件名太长！");
                } catch (ParseException e) {
                    toastUtil.showToast("格式错误！");
                    e.printStackTrace();
                }
            }
        });
    }
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
    private void initView()
    {
        btnReturn=findViewById(R.id.btn_return);
        btnCancel=findViewById(R.id.btn_cancel);
        etName=findViewById(R.id.et_event_name);
        etYear=findViewById(R.id.et_year);
        etMonth=findViewById(R.id.et_month);
        etDay=findViewById(R.id.et_day);
        toastUtil=new ToastUtil(this);
        sh=new SharedHelper(getApplicationContext());
    }
}