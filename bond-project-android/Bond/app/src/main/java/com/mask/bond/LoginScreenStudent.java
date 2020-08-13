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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LoginScreenStudent extends AppCompatActivity {

    private ConstraintLayout loginConstLayout;
    private ConstraintLayout signupConstLayout;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen_student);
        loginConstLayout = findViewById(R.id.loginConstLayout);
        signupConstLayout = findViewById(R.id.signupConstLayout);
    }

    public void showSignup(View view) {
        loginConstLayout.setVisibility(View.GONE);
        signupConstLayout.setVisibility(View.VISIBLE);
    }

    public void showLogin(View view) {
        loginConstLayout.setVisibility(View.VISIBLE);
        signupConstLayout.setVisibility(View.GONE);
    }

    public void signupValidate(View view) {
        EditText nameEdTxt, emailEdTxt, passEdTxt, cpassEdTxt;
        nameEdTxt = findViewById(R.id.signupStudentNameEdTxt);
        emailEdTxt = findViewById(R.id.signupStudentEmailEdTxt);
        passEdTxt = findViewById(R.id.signupStudentPassEdTxt);
        cpassEdTxt = findViewById(R.id.signupStudentConfirmPassEdTxt);
        final String strName = nameEdTxt.getText().toString().trim();
        final String strEmail = emailEdTxt.getText().toString().trim();
        final String strPass = passEdTxt.getText().toString().trim();
        String strCpass = cpassEdTxt.getText().toString().trim();
        if (!strName.isEmpty() && strName.length() < 3) {
            Toast.makeText(this, "Name must be of atleast 3 character", Toast.LENGTH_LONG).show();
            return;
        }
        if (strEmail.isEmpty() || (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches())) {
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_LONG).show();
            return;
        }
        if (strPass.length() < 6) {
            Toast.makeText(this, "Password too weak", Toast.LENGTH_LONG).show();
            return;
        }
        if (!strCpass.equals(strPass)) {
            Toast.makeText(this, "Password do not match", Toast.LENGTH_LONG).show();
            return;
        }
        final String url = "https://project-bond.herokuapp.com/register";
        requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(LoginScreenStudent.this,  response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginScreenStudent.this,  "Error occured", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("name", strName);
                map.put("email", strEmail);
                map.put("pass", strPass);
                map.put("status_code","0");
                return map;
            }
        };
        requestQueue.add(request);
    }

    public void login(View view) {
        EditText  emailEdTxt, passEdTxt;
        emailEdTxt = findViewById(R.id.loginEmailEdTxt);
        passEdTxt = findViewById(R.id.loginPassEdTxt);
        final String strEmail = emailEdTxt.getText().toString().trim();
        final String strPass = passEdTxt.getText().toString().trim();
        final String url = "https://project-bond.herokuapp.com/login";
        requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(LoginScreenStudent.this, response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject userJson = new JSONObject(response);
                            String name = userJson.getString("name");
                            int user_id = userJson.getInt("user_id");
                            SharedPreferences sharedPreferences = getSharedPreferences("loginData", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("email",strEmail);
                            editor.putString("pass",strPass);
                            editor.putString("status_code","0");
                            editor.commit();
                            Intent intent = new Intent(LoginScreenStudent.this,StudentHomePage.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("name",name);
                            bundle.putInt("user_id",user_id);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            Toast.makeText(LoginScreenStudent.this, response, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginScreenStudent.this, "Error occured", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> map = new HashMap<>();
                map.put("email",strEmail);
                map.put("pass",strPass);
                map.put("status_code","0");
                return map;
            }
        };
        requestQueue.add(request);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (requestQueue != null)
            requestQueue.cancelAll(null);
    }
}
