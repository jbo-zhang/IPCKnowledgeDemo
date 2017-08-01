package com.example.ipcknowledgedemo.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.example.ipcknowledgedemo.Book;

public class MessengerService extends Service {
    private static final String TAG = "MessengerService";


    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MyConstants.MSG_FROM_CLIENT:
                    Log.d(TAG, "receive msg from client : " + msg.getData().getString("msg"));
                    //跨进程无法传递非系统Parcelable对象，以下代码报错
                    //android.os.BadParcelableException: ClassNotFoundException when unmarshalling: com.example.ipcknowledgedemo.Book
                    //Book book = (Book) msg.getData().getParcelable("obj");
                    //Log.d(TAG, "receive your book : " + book.bookId + " " + book.bookName);

                    Message msgS = Message.obtain(null, MyConstants.MSG_FROM_SERVER);
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", "I have received your messeage!");
                    msgS.setData(bundle);
                    try {
                        msg.replyTo.send(msgS);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private final Messenger mMessenger = new Messenger(new MessengerHandler());

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
