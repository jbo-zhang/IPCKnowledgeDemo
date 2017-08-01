package com.example.ipcknowledgedemo.aidl;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.example.ipcknowledgedemo.Book;
import com.example.ipcknowledgedemo.IBookManager;
import com.example.ipcknowledgedemo.IOnNewBookArrivedListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class BookManagerService extends Service {

    private static final String TAG = "BMS";

    private AtomicBoolean mIsServiceDestroyed = new AtomicBoolean(false);

    //采用CopyOnWriteArrayList无法实现注销listener，需要采用RemoteCallbackList
//    private CopyOnWriteArrayList<IOnNewBookArrivedListener> mListenerList = new CopyOnWriteArrayList<IOnNewBookArrivedListener>();
    private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList = new RemoteCallbackList<IOnNewBookArrivedListener>();


    //CopyOnWriteArrayList支持并发读/写，
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<Book>();

    private Binder mBinder = new IBookManager.Stub() {


        @Override
        public List<Book> getBookList() throws RemoteException {
            //服务端的方法本身就运行在Binder的线程池里，所以在这些方法里直接睡是没有问题的
            //不过需要注意，当客户端调用这个方法，客户端线程会被挂起，所以客户端请不要在主线程调用这个耗时的方法
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerList.register(listener);
            int size = mListenerList.beginBroadcast();
            Log.d(TAG, "register size : " + size);
            mListenerList.finishBroadcast();


//            if(!mListenerList.contains(listener)) {
//                mListenerList.add(listener);
//            } else {
//                Log.d(TAG, "already exists.");
//            }
//            Log.d(TAG, "registerListener, size: " + mListenerList.size());
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerList.unregister(listener);
            int size = mListenerList.beginBroadcast();
            Log.d(TAG, "unregister size : " + size);
            mListenerList.finishBroadcast();

//            if(mListenerList.contains(listener)) {
//                mListenerList.remove(listener);
//                Log.d(TAG, "unregister listener succeed.");
//            } else {
//                Log.d(TAG, "not found, can not unregister");
//            }
//            Log.d(TAG, "unregisterListener, current size: " + mListenerList.size());
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            return super.onTransact(code, data, reply, flags);
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1, "Android"));
        mBookList.add(new Book(2, "Ios"));

        new Thread(new ServiceWorker()).start();
    }

    private class ServiceWorker implements Runnable {
        @Override
        public void run() {
            while(!mIsServiceDestroyed.get()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId = mBookList.size() + 1;
                Book newBook = new Book(bookId, "new Book#" + bookId);
                try {
                    onNewBookArrived(newBook);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void onNewBookArrived(Book newBook) throws RemoteException {
        mBookList.add(newBook);

        final int N = mListenerList.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IOnNewBookArrivedListener broadcastItem = mListenerList.getBroadcastItem(i);
            if(broadcastItem != null) {
                broadcastItem.onNewBookArrived(newBook);
            }
        }
        mListenerList.finishBroadcast();

//        Log.d(TAG, "onNewBookArrived, notify listeners: " + mListenerList.size());
//        for (int i = 0; i < mListenerList.size(); i++) {
//            IOnNewBookArrivedListener iOnNewBookArrivedListener = mListenerList.get(i);
//            Log.d(TAG, "onNewBookArrived, notify listener: " + iOnNewBookArrivedListener);
//            iOnNewBookArrivedListener.onNewBookArrived(newBook);
//        }
    }


    @Override
    public IBinder onBind(Intent intent) {
//        //权限验证，permission需要自定义，这里没有实现
//        int check = checkCallingOrSelfPermission("com.example.permission.bookmanagerservice");
//        if(check == PackageManager.PERMISSION_DENIED) {
//            //表示没有权限
//            return null;
//        }

        return mBinder;
    }
}
