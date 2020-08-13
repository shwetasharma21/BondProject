package com.mask.bond.teacher.teacherScreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mask.bond.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private EditText orgNameEditTxt, orgWebsiteEditTxt, teacherCodeEditTxt;
    private ImageButton orgNameEditBtn, orgWebsiteEditBtn, teacherCodeEditBtn;
    private TextView nameTxtView, emailTxtView;
    private int teacherId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_teacher_profile, container, false);
        orgNameEditTxt = root.findViewById(R.id.profileTeacherOrgNameEdTxt);
        orgWebsiteEditTxt = root.findViewById(R.id.profileTeacherOrgWebsiteEditTxt);
        teacherCodeEditTxt = root.findViewById(R.id.profileTeacherCodeEditTxt);
        orgNameEditBtn = root.findViewById(R.id.profileTeacherOrgNameEditBtn);
        orgWebsiteEditBtn = root.findViewById(R.id.profileTeacherOrgWebsiteEditBtn);
        nameTxtView = root.findViewById(R.id.profileTeacherNameTxtView);
        emailTxtView = root.findViewById(R.id.profileTeacherEmailTxtView);
        teacherCodeEditBtn = root.findViewById(R.id.profileTeacherCodeEditBtn);
        Bundle bundle = getActivity().getIntent().getExtras();
        teacherId = bundle.getInt("teacher_id");
        orgNameEditBtn.setOnClickListener(this);
        orgWebsiteEditBtn.setOnClickListener(this);
        teacherCodeEditBtn.setOnClickListener(this);
        loadProfileData();
        return root;
    }

    private void loadProfileData() {
        String url = "https://project-bond.herokuapp.com/teacherProfile";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String name = jsonObject.getString("name").trim();
                            String email = jsonObject.getString("email").trim();
                            String orgName = jsonObject.getString("orgname").trim();
                            String orgWebsiteLink = jsonObject.getString("orgwebsitelink").trim();
                            String teacherCode = jsonObject.getString("teacher_code").trim();
                            nameTxtView.setText(name);
                            emailTxtView.setText(email);
                            orgNameEditTxt.setText(orgName);
                            orgWebsiteEditTxt.setText(orgWebsiteLink);
                            if (!teacherCode.equals("null"))
                                teacherCodeEditTxt.setText(teacherCode);
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Error:" + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("teacher_id", "" + teacherId);
                return map;
            }
        };
        requestQueue.add(request);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profileTeacherOrgNameEditBtn:
                updateOrgName();
                break;
            case R.id.profileTeacherOrgWebsiteEditBtn:
                updateOrgWeb();
                break;
            case R.id.profileTeacherCodeEditBtn:
                updateTeacherCode();
                break;
        }
    }

    private void updateTeacherCode() {
        final String teacherCode = teacherCodeEditTxt.getText().toString().trim();
        if (teacherCode.length() < 6) {
            Toast.makeText(getActivity(), "Minimum length 6 required", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://project-bond.herokuapp.com/teacherUpdate";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("teacher_id", "" + teacherId);
                map.put("teacher_code", teacherCode);
                return map;
            }
        };
        requestQueue.add(request);
    }

    private void updateOrgWeb() {
        final String orgWebsiteLink = orgWebsiteEditTxt.getText().toString().trim();
        if (orgWebsiteLink.isEmpty()) {
            Toast.makeText(getActivity(), "Insufficient data", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = "https://project-bond.herokuapp.com/teacherUpdate";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Error:" + error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("teacher_id", "" + teacherId);
                map.put("orgWebsiteLink", orgWebsiteLink);
                return map;
            }
        };
        requestQueue.add(request);
    }

    private void updateOrgName() {
        final String orgName = orgNameEditTxt.getText().toString().trim();
        if (orgName.isEmpty()) {
            Toast.makeText(getActivity(), "Insufficient data", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = "https://project-bond.herokuapp.com/teacherUpdate";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Error:" + error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("teacher_id", "" + teacherId);
                map.put("orgName", orgName);
                return map;
            }
        };
        requestQueue.add(request);
    }
}
