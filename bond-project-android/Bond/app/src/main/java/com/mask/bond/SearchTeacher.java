package com.mask.bond;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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

public class SearchTeacher extends AppCompatActivity {

    private EditText searchEdTxt;
    private TextView nameTxtView,orgNameTxtView,orgWebTxtView;
    private ImageButton searchImageBtn;
    private Button sendReqBtn;
    private ProgressBar progressBar;
    private ConstraintLayout teacherDetailsConstLayout;
    private int teacherId, studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_teacher);
        searchEdTxt = findViewById(R.id.searchEdTxt);
        searchImageBtn = findViewById(R.id.searchImageBtn);
        teacherDetailsConstLayout = findViewById(R.id.searchTeacherConstLayout);
        nameTxtView = findViewById(R.id.searchTeacherNameTxtView);
        orgNameTxtView = findViewById(R.id.searchTeacherOrgNameTxtView);
        orgWebTxtView = findViewById(R.id.searchTeacherOrgWebTxtView);
        progressBar = findViewById(R.id.searchTeacherProgBar);
        sendReqBtn = findViewById(R.id.searchTeacherSendReqBtn);
        Bundle bundle = getIntent().getExtras();
        studentId = bundle.getInt("user_id");
        Toast.makeText(this, "User id : " + studentId, Toast.LENGTH_SHORT).show();
        searchImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTeacherList();
            }
        });
        sendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
    }

    private void loadTeacherList() {
        final String teacherCode = searchEdTxt.getText().toString().trim();
        if (teacherCode.isEmpty()) {
            Toast.makeText(this, "Teacher code empty", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        String url = "https://project-bond.herokuapp.com/teacherSearch";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       Toast.makeText(SearchTeacher.this, "Response :"+response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String name = jsonObject.getString("name");
                            String orgName = jsonObject.getString("orgname");
                            String orgWebsiteLink = jsonObject.getString("orgwebsitelink");
                            teacherId = jsonObject.getInt("teacher_id");
                            nameTxtView.setText(name);
                            orgNameTxtView.setText(orgName);
                            orgWebTxtView.setText(orgWebsiteLink);
                            progressBar.setVisibility(View.GONE);
                            teacherDetailsConstLayout.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SearchTeacher.this, "Error :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SearchTeacher.this,"Error :"+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("teacher_code", teacherCode);
                return map;
            }
        };
        requestQueue.add(request);
    }

    private void sendRequest() {
        if(teacherId==0){
            Toast.makeText(this, "Insufficient data", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = "https://project-bond.herokuapp.com/sendRequest";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(SearchTeacher.this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SearchTeacher.this, "Error :"+error, Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> map = new HashMap<>();
                map.put("student_id",""+studentId);
                map.put("teacher_id",""+teacherId);
                return map;
            }
        };
        requestQueue.add(request);
    }
}