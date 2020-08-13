package com.mask.bond;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StudentTeachersFragment extends Fragment {

    private ListView contactsListView;
    private SimpleAdapter simpleAdapter;
    private List<Map<String, String>> contactList;
    private  int student_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_student_teachers, container, false);
        Bundle bundle = getActivity().getIntent().getExtras();
        student_id = bundle.getInt("user_id");
        contactsListView = view.findViewById(R.id.studentContactsListView);
        contactList = new ArrayList<>();
        simpleAdapter = new SimpleAdapter(
                getActivity(),
                contactList,
                R.layout.layout_contacts,
                new String[]{"teacherName"},
                new int[]{R.id.layoutStudentContactsTxtView}
        );
        contactsListView.setAdapter(simpleAdapter);
        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String,String> individualItem = contactList.get(position);
                String teacherName = individualItem.get("teacherName");
                int teacher_id = Integer.parseInt(individualItem.get("teacher_id"));
                Intent intent = new Intent(getActivity(),IndividualMessage.class);
                Bundle bundle1 = new Bundle();
                bundle1.putInt("teacher_id",teacher_id);
                bundle1.putString("teacherName",teacherName);
                bundle1.putInt("student_id",student_id);
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });
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
                                String teacherName = jsonObject.getString("name");
                                int teacher_id = jsonObject.getInt("teacher_id");
                                Map<String,String> item = new HashMap<>();
                                item.put("teacherName",teacherName);
                                item.put("teacher_id",""+teacher_id);
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
                map.put("user_id",""+student_id);
                return map;
            }
        };
        requestQueue.add(request);
    }
}