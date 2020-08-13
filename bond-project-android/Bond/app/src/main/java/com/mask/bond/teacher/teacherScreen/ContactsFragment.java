package com.mask.bond.teacher.teacherScreen;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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

public class ContactsFragment extends Fragment {
    private ListView contactsListView;
    private SimpleAdapter simpleAdapter;
    private List<Map<String, String>> contactList;
    int teacher_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_contacts,container,false);
        Bundle bundle = getActivity().getIntent().getExtras();
        teacher_id = bundle.getInt("teacher_id");
        contactsListView = view.findViewById(R.id.teacherContactsListView);
        contactList = new ArrayList<>();
        simpleAdapter = new SimpleAdapter(
                getActivity(),
                contactList,
                R.layout.layout_contacts,
                new String[]{"studentName"},
                new int[]{R.id.layoutStudentContactsTxtView}
        );
        contactsListView.setAdapter(simpleAdapter);
        loadContacts();
        return view;
    }

    private void loadContacts() {
        String url = "https://project-bond.herokuapp.com/contacts";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i =0;i<jsonArray.length();i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String studentName = jsonObject.getString("name");
                                int student_id= jsonObject.getInt("student_id");
                                Map<String,String> item = new HashMap<>();
                                item.put("studentName",studentName);
                                item.put("student_id",""+student_id);
                                contactList.add(item);
                            }
                            simpleAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "Oops..! Something went wrong.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Error:"+error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> map = new HashMap<>();
                map.put("user_id",""+teacher_id);
                return map;
            }
        };
        requestQueue.add(request);
    }
    }
