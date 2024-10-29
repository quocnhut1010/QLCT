package com.example.ckqlct;

import android.app.Activity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {
    public static final String DATABASE_NAME = "QLCT.db";
    SQLiteDatabase db;
    EditText edtusername,edtpassword;
    Button eregister,elogin;
    boolean isAllFields = false;
    private void initDB() {
        db = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        String sql;
        try {
            if (!isTableExists(db, "tbluser")) {
                sql = "CREATE TABLE tbluser (id_user INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,";
                sql += " username TEXT NOT NULL,";
                sql += " password TEXT NOT NULL,";
                sql += " email TEXT NOT NULL UNIQUE,";
                sql += " fullname TEXT NULL,";
                sql += " registration_date DATETIME DEFAULT (DATETIME('now')))";
                db.execSQL(sql);

                // Insert default admin user
                sql = "INSERT INTO tbluser (username, password, email, fullname, registration_date) " +
                        "VALUES ('admin', 'admin', 'admin@gmail.com', '', '2024-10-23')";
                db.execSQL(sql);
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Khởi tạo cơ sở dữ liệu không thành công", Toast.LENGTH_LONG).show();
        }
    }

    private boolean CheckAllField (String username, String password)
    {
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(),"Please enter name",Toast.LENGTH_LONG).show();
            return false;

        }
        else if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),"please enter proper password",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        // Initialize the database
        initDB();  // Add this line to initialize database

        eregister = findViewById(R.id.register1);
        elogin= findViewById(R.id.login);
        edtusername = findViewById(R.id.username);
        edtpassword = findViewById(R.id.password);


        eregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in = new Intent(Login.this,RegisterActivity.class);

                startActivity(in);


            }
        });

        elogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtusername.getText().toString();
                String password = edtpassword.getText().toString();

                if (username.isEmpty()) {
                    Toast.makeText(getApplication(), "Vui long nhap tai khoan", Toast.LENGTH_LONG).show();
                    edtusername.requestFocus();
                } else if (password.isEmpty()) {
                    Toast.makeText(getApplication(), "Vui long nhap mat khau", Toast.LENGTH_LONG).show();
                    edtpassword.requestFocus();
                } else if (isUser(username, password)) {
//                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("username", username);  // Save the username
//                    editor.apply();
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplication(), "Tai khoan hoac mat khau bi sai", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    private boolean isUser(String username, String password) {
        try {
            db = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
            // Update the query to fetch id_user, fullname, and email
            Cursor c = db.rawQuery("SELECT id_user, fullname, email FROM tbluser WHERE username = ? AND password = ?", new String[]{username, password});

            if (c.moveToFirst()) {
                // Get the indices for the columns
                int userIdIndex = c.getColumnIndex("id_user");
                int fullnameIndex = c.getColumnIndex("fullname");
                int emailIndex = c.getColumnIndex("email");

                if (userIdIndex == -1 || fullnameIndex == -1 || emailIndex == -1) {
                    Toast.makeText(this, "Error retrieving user information.", Toast.LENGTH_LONG).show();
                    return false;
                }

                // User is valid, retrieve user ID and other details
                int userId = c.getInt(userIdIndex);
                String fullname = c.getString(fullnameIndex);
                String email = c.getString(emailIndex);

                // Store user information in SharedPreferences
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("id_user", userId);
                editor.putString("fullname", fullname);
                editor.putString("email", email);
                editor.apply();

                return true;  // User is valid
            } else {
                Toast.makeText(this, "Invalid username or password.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Login error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("LoginError", ex.getMessage());
        }
        return false;  // User is not valid
    }




//    private boolean isUser(String username, String password) {
//        try {
//            db = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
//            // Truy vấn chỉ kiểm tra username và password
//            Cursor c = db.rawQuery("SELECT * FROM tbluser WHERE username = ? AND password = ?", new String[]{username, password});
//            if (c.moveToFirst()) {  // Chỉ cần kiểm tra nếu có kết quả
//                return true;  // Người dùng hợp lệ
//            }
//        } catch (Exception ex) {
//            Toast.makeText(this, "Lỗi đăng nhập", Toast.LENGTH_LONG).show();
//            Log.e("LoginError", ex.getMessage());  // In lỗi ra log để dễ theo dõi
//        }
//        return false;  // Người dùng không hợp lệ
//    }


    private boolean isTableExists(SQLiteDatabase database, String tableName) {
        Cursor cursor = database.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name" + "= '" + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

}