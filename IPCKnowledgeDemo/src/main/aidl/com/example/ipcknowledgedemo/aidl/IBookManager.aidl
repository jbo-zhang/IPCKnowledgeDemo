// IBookManager.aidl
package com.example.ipcknowledgedemo.aidl;

import com.example.ipcknowledgedemo.Book;

interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
}