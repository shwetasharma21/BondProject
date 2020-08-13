package com.mask.bond;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginScreenTeacher extends AppCompatActivity {
    private ConstraintLayout loginConstLayout;
    private ConstraintLayout signupConstLayout;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen_teacher);
        loginConstLayout = findViewById(R.id.loginTeacherConstLayout);
        signupConstLayout = findViewById(R.id.signupTeacherConstLayout);
    }

    public void showSignup(View view) {
        loginConstLayout.setVisibility(View.GONE);
        signupConstLayout.setVisibility(View.VISIBLE);
    }

    public void showLogin(View view) {
        signupConstLayout.setVisibility(View.GONE);
        loginConstLayout.setVisibility(View.VISIBLE);
    }

    public void signupValidate(View view) {
        EditText nameEdTxt, emailEdTxt, orgNameEdTxt, orgLinkEdTxt, passEdTxt, cpassEdTxt;
        TextView errorMsgTxtView = findViewById(R.id.signupTeacherErrorMsgTxtView);
        errorMsgTxtView.setText("");
        nameEdTxt = findViewById(R.id.signupTeacherNameEdTxt);
        emailEdTxt = findViewById(R.id.signupTeacherEmailEdTxt);
        orgNameEdTxt = findViewById(R.id.signupTeacherOrgNameEdTxt);
        orgLinkEdTxt = findViewById(R.id.signupTeacherOrgWebsiteEdTxt);
        passEdTxt = findViewById(R.id.signupTeacherPassEdTxt);
        cpassEdTxt = findViewById(R.id.signupTeacherConfirmPassEdTxt);
        final String strName = nameEdTxt.getText().toString();
        final String strEmail = emailEdTxt.getText().toString();
        final String strOrgName = orgNameEdTxt.getText().toString();
        final String strOrgLink = orgLinkEdTxt.getText().toString();
        final String strPass = passEdTxt.getText().toString();
        String strCpass = cpassEdTxt.getText().toString();
        if (strName.length() < 3) {
            Toast.makeText(this, "Name must be of atleast 3 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        if (strEmail.isEmpty() || (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches())) {
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (strOrgName.isEmpty()) {
            Toast.makeText(this, "Organization name can not be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (strOrgLink.isEmpty() || (!Patterns.WEB_URL.matcher(strOrgLink).matches())) {
            Toast.makeText(this, "Invalid URL", Toast.LENGTH_SHORT).show();
            return;
        }
        if (strPass.length() < 6) {
            Toast.makeText(this, "Invalid Password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!strCpass.equals(strPass)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        requestQueue = Volley.newRequestQueue(this);
        final String url = "https://project-bond.herokuapp.com/register";
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(LoginScreenTeacher.this, "" + response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginScreenTeacher.this, "error:" + error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap();
                map.put("name", strName);
                map.put("email", strEmail);
                map.put("pass", strPass);
                map.put("orgName", strOrgName);
                map.put("orgWebsiteLink", strOrgLink);
                map.put("status_code", "1");
                return map;
            }
        };
        requestQueue.add(request);
    }

    public void loginValidate(View view) {
        EditText loginEmailEdTxt,loginPassEdTxt;
        loginEmailEdTxt = findViewById(R.id.loginTeacherEmailEdTxt);
        loginPassEdTxt = findViewById(R.id.loginTeacherPassEdTxt);
        final String strLoginEmail = loginEmailEdTxt.getText().toString().trim();
        final String strLoginPass = loginPassEdTxt.getText().toString();
        if(strLoginEmail.isEmpty()||strLoginPass.isEmpty()){
            Toast.makeText(this, "Insufficient data", Toast.LENGTH_SHORT).show();
        }

        final String url = "https://project-bond.herokuapp.com/login";
        requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(LoginScreenTeacher.this, response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String name = jsonObject.getString("name");
                            int teacherId = jsonObject.getInt("user_id");
                            SharedPreferences sharedPreferences = getSharedPreferences("loginData", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("email",strLoginEmail);
                            editor.putString("pass",strLoginPass);
                            editor.putString("status_code","1");
                            editor.commit();
                            Intent intent = new Intent(LoginScreenTeacher.this,TeacherHomePage.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("name",name);
                            bundle.putInt("teacher_id",teacherId);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                        catch (JSONException e) {
                            Toast.makeText(LoginScreenTeacher.this, response, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginScreenTeacher.this, "error:"+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> map = new HashMap<>();
                map.put("email",strLoginEmail);
                map.put("pass",strLoginPass);
                map.put("status_code","1");
                return map;
            }
        };
        requestQueue.add(request);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(requestQueue != null){
            requestQueue.cancelAll(null);
        }
    }
}