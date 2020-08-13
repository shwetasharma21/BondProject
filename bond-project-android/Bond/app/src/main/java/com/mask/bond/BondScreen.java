package com.mask.bond;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class BondScreen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bond_screen);
        SharedPreferences sharedPreferences = getSharedPreferences("loginData", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String pass = sharedPreferences.getString("pass", "");
        String statusCode = sharedPreferences.getString("status_code", "");
        login(email, pass, statusCode);
    }

    private void login(final String email, final String pass, final String statusCode) {
        if (email.isEmpty() || pass.isEmpty() || statusCode.isEmpty()) {
            Intent intent = new Intent(BondScreen.this, ChooseUser.class);
            startActivity(intent);
            finish();
        }
        final String url = "https://project-bond.herokuapp.com/login";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String name = jsonObject.getString("name");
                            int userId = jsonObject.getInt("user_id");
                            if (statusCode.equals("0")) {
                                Intent intent = new Intent(BondScreen.this, StudentHomePage.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("name", name);
                                bundle.putInt("user_id", userId);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                            } else if (statusCode.equals("1")) {
                                Intent intent = new Intent(BondScreen.this, TeacherHomePage.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("name", name);
                                bundle.putInt("teacher_id", userId);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            Intent intent = new Intent(BondScreen.this, ChooseUser.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Intent intent = new Intent(BondScreen.this, ChooseUser.class);
                        startActivity(intent);
                        finish();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("email", email);
                map.put("pass", pass);
                map.put("status_code", statusCode);
                return map;
            }
        };
        requestQueue.add(request);
    }
}