package com.example.ipcknowledgedemo;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UserManager.sUserId = 2;
        Log.d("UserManager", "MainActivity sUserId = " + UserManager.sUserId);
        findViewById(R.id.bt_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
            }
        });

        User user = new User(5566, "wrf", true);
        Admin admin = new Admin(7788, "zyh", true);
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("/mnt/sdcard/cache.txt"));
            out.writeObject(user);
            out.close();

            ObjectInputStream in = new ObjectInputStream(new FileInputStream("/mnt/sdcard/cache.txt"));
            User newUser = (User) in.readObject();
            in.close();
            Log.d("UserManager", newUser.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    /**
     * Serializable是Java中的序列化接口，使用起来简单但是开销很大
     * Parcelable是Android中的序列化接口，使用稍微麻烦点，但是效率很高
     */


    /**
     * 非静态内部类拥有对外部类的所有成员的完全访问权限，包括实例字段和方法。
     * 为实现这一行为，非静态内部类存储着对外部类的实例的一个隐式引用。
     * 序列化时要求所有的成员变量是Serializable,现在外部的类并没有implements Serializable,
     * 所以就抛出java.io.NotSerializableException异常。
     解决办法：
     1.将内部类写成静态的
     2.将内部类单独写一个.java文件 implements Serializable
     */
    /**
     * 内部类要序列化必须采用静态修饰，原因如上说明
     */
    static class User implements Serializable {

        private static final long serialVersionUID = 305740234209347807L;

        int userId;
        String name;
        boolean isMale;


        public User(int userId, String name, boolean isMale) {
            this.userId = userId;
            this.name = name;
            this.isMale = isMale;
        }
        @Override
        public String toString() {
            return userId + "  " + name + "  " + isMale;
        }
    }

    static class Admin implements Parcelable {
        public int adminId;
        public String adminName;
        public boolean isMale;

        public Admin(int adminId, String adminName, boolean isMale) {
            this.adminId = adminId;
            this.adminName = adminName;
            this.isMale = isMale;
        }

        @Override
        public String toString() {
            return adminId + "  " + adminName + "  " + isMale;
        }


        protected Admin(Parcel in) {
            adminId = in.readInt();
            adminName = in.readString();
            isMale = in.readInt() == 1;
        }

        /**
         * 反序列化
         */
        public static final Creator<Admin> CREATOR = new Creator<Admin>() {

            @Override
            public Admin createFromParcel(Parcel in) {
                return new Admin(in);
            }

            @Override
            public Admin[] newArray(int size) {
                return new Admin[size];
            }
        };

        /**
         * 内容描述
         * @return
         */
        @Override
        public int describeContents() {
            return 0;
        }

        /**
         * 序列化
         * @param out
         * @param flags
         */
        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(adminId);
            out.writeString(adminName);
            out.writeInt(isMale ? 1 : 0);
        }
    }

}
