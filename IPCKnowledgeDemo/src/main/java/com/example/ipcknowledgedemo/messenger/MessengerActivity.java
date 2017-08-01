package com.example.ipcknowledgedemo.messenger;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.ipcknowledgedemo.Book;
import com.example.ipcknowledgedemo.R;

import static android.R.attr.data;

public class MessengerActivity extends AppCompatActivity {
    private static final String TAG = "MessengerActivity";

    private Messenger mService;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyConstants.MSG_FROM_SERVER:
                    Log.d(TAG, "receiver msg from server : " + msg.getData().getString("msg"));
                    break;
            }
        }
    };

    private Messenger mMessenger = new Messenger(handler);

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = new Messenger(iBinder);
            Message msg = Message.obtain(null, MyConstants.MSG_FROM_CLIENT);
            msg.replyTo = mMessenger;
            Bundle data = new Bundle();

            //跨进程时无法传递非系统Parcelable对象，所以下面的对象Book在Service接收时会出错，
            //如果去掉Service的process=":remote"，则传递可以成功
            //data.putParcelable("obj", new Book(123,"sherlock"));

            data.putString("msg", "hello ,this is client.");
            msg.setData(data);
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

//        Intent intent = new Intent(this, ParcelableReceiveActivity.class);
//        Bundle data = new Bundle();
//        data.putParcelable("obj", new Book(123,"sherlock"));
//        intent.putExtra("data", data);
//        startActivity(intent);

        Intent intent = new Intent(this, MessengerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
       // unbindService(mConnection);
        super.onDestroy();
    }
}
