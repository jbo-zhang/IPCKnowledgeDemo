package com.example.ipcknowledgedemo.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.ipcknowledgedemo.Book;
import com.example.ipcknowledgedemo.IBookManager;
import com.example.ipcknowledgedemo.IOnNewBookArrivedListener;
import com.example.ipcknowledgedemo.R;

import java.util.List;

public class BookManagerActivity extends AppCompatActivity {

    private static final String TAG = "BookManagerActivity";

    private static final int MESSAGE_NEW_BOOK_ARRIVED = 1;

    private IBookManager mRemoteBookManager;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_NEW_BOOK_ARRIVED:
                    Log.d(TAG, "new book " + msg.obj + " arrived!");
                    break;
            }
        }
    };


    private IOnNewBookArrivedListener mOnNewBookArrivedListenernew = new IOnNewBookArrivedListener.Stub() {

        @Override
        public void onNewBookArrived(Book newBook) throws RemoteException {
            //服务段回调客户端listener的方法，被调用的方法同样运行在Binder的线程池中，不过是客户端的线程池
            //所以理论上也可以在这里直接睡
            //此时服务端的线程会被挂起，所以也请不要在服务端的主线程调用该方法，会引起Service的ANR


            mHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED, newBook).sendToTarget();
        }
    };


    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IBookManager bookManager = IBookManager.Stub.asInterface(iBinder);
            try {
                mRemoteBookManager = bookManager;
                List<Book> list = bookManager.getBookList();
                //验证list为ArrayList
                Log.d(TAG, "query book list, list type : " + list.getClass().getCanonicalName());
                Log.d(TAG, "query book list: " + list.toString());

                Book newBook = new Book(3, "Android开发艺术探索");
                bookManager.addBook(newBook);
                Log.d(TAG, "add book: " + newBook);
                List<Book> newList = bookManager.getBookList();
                Log.d(TAG, "query book list :" + newList.toString());


                bookManager.registerListener(mOnNewBookArrivedListenernew);

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mRemoteBookManager = null;
            Log.e(TAG, "binder died");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_manager);

        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, mConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if(mRemoteBookManager != null && mRemoteBookManager.asBinder().isBinderAlive()) {
            try {
                Log.d(TAG, "unregister listener: " + mOnNewBookArrivedListenernew);
                mRemoteBookManager.unregisterListener(mOnNewBookArrivedListenernew);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
        unbindService(mConn);
        super.onDestroy();
    }
}

