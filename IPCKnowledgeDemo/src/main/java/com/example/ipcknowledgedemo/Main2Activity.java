package com.example.ipcknowledgedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //每个进程拥有各自独立的虚拟机，不同虚拟机在内存分配上有不同的地址空间，静态值也无法共享
        Log.d("UserManager", "Main2Activity sUserId = " + UserManager.sUserId);
        findViewById(R.id.bt_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2Activity.this, Main3Activity.class);
                startActivity(intent);

            }
        });
    }
}
