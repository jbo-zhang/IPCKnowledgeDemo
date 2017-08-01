package com.example.ipcknowledgedemo.messenger;

import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.ipcknowledgedemo.Book;
import com.example.ipcknowledgedemo.R;

public class ParcelableReceiveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcelable_receive);

        Bundle data = getIntent().getBundleExtra("data");
        Book book = data.getParcelable("obj");
        TextView tvMsg = (TextView) findViewById(R.id.tv_msg);
        tvMsg.setText("book : " + book.bookId + " " + book.bookName);
    }
}
