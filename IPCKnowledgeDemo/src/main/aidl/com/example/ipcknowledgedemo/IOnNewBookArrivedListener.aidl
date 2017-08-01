// IOnNewBookArrivedListener.aidl
package com.example.ipcknowledgedemo;

import com.example.ipcknowledgedemo.Book;
//新书提醒功能
interface IOnNewBookArrivedListener {
    void onNewBookArrived(in Book newBook);
}
