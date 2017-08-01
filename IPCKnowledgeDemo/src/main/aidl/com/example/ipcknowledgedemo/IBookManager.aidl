// IBookManager.aidl
package com.example.ipcknowledgedemo;

import com.example.ipcknowledgedemo.Book;
import com.example.ipcknowledgedemo.IOnNewBookArrivedListener;
// AIDL文件支持的数据类型：
// 基本数据类型： int, long, char, boolean, double等
// String 和 CharSequence
// List： 只支持ArrayList, 里面每个元素都必须能够被AIDL支持
// Map： 只支持HashMap， 里面每个元素必须被AIDL支持，包括key和value
// Parcelable：所有实现了Parcelable接口的对象
// AIDL：所有的AIDL接口本身也可以在AIDL文件中使用
interface IBookManager {
    List<Book> getBookList();
    //AIDL除了基本数据类型，其他类型的参数必须标上方向
    //in表示输入型参数，out表示输出型参数，inout表示输入输出型参数
    void addBook(in Book book);

    //添加新书提醒功能
    void registerListener(IOnNewBookArrivedListener listener);
    void unregisterListener(IOnNewBookArrivedListener listener);

}
