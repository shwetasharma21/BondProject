package com.mask.bond.teacher.teacherScreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestsFragment extends Fragment {

    private int teacherId;
    private List<Map<String,String>> reqData;
    private ListView listView;
    private SimpleAdapter simpleAdapter;
    private boolean canSendReq = true;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_teacher_requests, container, false);
        Bundle bundle = getActivity().getIntent().getExtras();
        teacherId = bundle.getInt("teacher_id");
        listView = root.findViewById(R.id.reqListView);
        reqData = new ArrayList<>();
        simpleAdapter = new SimpleAdapter(
                getActivity(),
                reqData,
                R.layout.layout_request_teacher,
                new String[]{"name"},
                new int[]{R.id.layoutReqNameTxtView}
        ){
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Button acptBtn = view.findViewById(R.id.layoutReqAcptBtn);
                Button rjctBtn = view.findViewById(R.id.layoutReqRjctBtn);
                acptBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        processRequest(position,"1");
                    }
                });
                rjctBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        processRequest(position,"0");
                    }
                });
                return view;
            }
        };
        listView.setAdapter(simpleAdapter);
        loadRequests();
        return root;
    }

    private void processRequest(final int position, final String req_status) {
             if(canSendReq == false){
                 Toast.makeText(getActivity(), "Send only one request", Toast.LENGTH_SHORT).show();
                 return;
             }
             canSendReq = false;
             Map<String,String> map = reqData.get(position);
             final String req_id = map.get("req_id");
             String url = "https://project-bond.herokuapp.com/processRequest";
             RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
             StringRequest request = new StringRequest(
                     Request.Method.POST,
                     url,
                     new Response.Listener<String>() {
                         @Override
                         public void onResponse(String response) {
                             Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                             reqData.remove(position);
                             simpleAdapter.notifyDataSetChanged();
                             canSendReq = true;
                         }
                     },
                     new Response.ErrorListener() {
                         @Override
                         public void onErrorResponse(VolleyError error) {
                             Toast.makeText(getActivity(), "Error:"+error.getMessage(), Toast.LENGTH_SHORT).show();
                             canSendReq = true;
                         }
                     }
             ){
                 @Override
                 protected Map<String, String> getParams()  {
                     Map<String,String> map = new HashMap<>();
                     map.put("req_id",req_id);
                     map.put("req_status",req_status);
                     return map;
                 }
             };
             requestQueue.add(request);
    }


    private void loadRequests() {
        String url = "https://project-bond.herokuapp.com/receiveRequest";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i =0;i<jsonArray.length();i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int connId = jsonObject.getInt("connection_id");
                                String studentName = jsonObject.getString("name");
                                Map<String,String> item = new HashMap<>();
                                item.put("name",studentName);
                                item.put("req_id",""+connId);
                                reqData.add(item);
                            }
                            simpleAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "Error :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Error:"+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> map = new HashMap<>();
                map.put("teacher_id",""+teacherId);
                return map;
            }
        };
        requestQueue.add(request);
    }
}
