package com.mask.bond;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ChooseUser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);
    }

    public void showStudentLogin(View view) {
        Intent intent=new Intent(this,LoginScreenStudent.class);
        startActivity(intent);
        finish();
    }

    public void showTeacherLogin(View view) {
        Intent intent=new Intent(this, LoginScreenTeacher.class);
        startActivity(intent);
        finish();
    }
}
