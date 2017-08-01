package com.example.ipcknowledgedemo.contentprovider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.ipcknowledgedemo.Book;
import com.example.ipcknowledgedemo.R;

public class ProviderActivity extends AppCompatActivity {
    private static final String TAG = "ProviderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);

        Uri uri = Uri.parse("content://com.example.provider.book/book");

        ContentValues values = new ContentValues();
        values.put("_id", 6);
        values.put("name", "C语言，从入门到放弃");
        getContentResolver().insert(uri, values);

        getContentResolver().delete(uri, "_id=?", new String[]{"3"});

        Cursor cursor = getContentResolver().query(uri, new String[]{"_id", "name"}, null, null, null);
        while(cursor.moveToNext()) {
            Book book = new Book();
            book.bookId = cursor.getInt(0);
            book.bookName = cursor.getString(1);
            Log.d(TAG, "query book : " + book.toString());
        }
        cursor.close();
    }
}
