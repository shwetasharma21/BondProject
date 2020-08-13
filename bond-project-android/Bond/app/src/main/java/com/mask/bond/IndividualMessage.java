package com.mask.bond;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndividualMessage extends AppCompatActivity {
    private Map<String,String> msgItem;
    private List<Map<String,String>> msgList;
    private SimpleAdapter simpleAdapter;
    private ListView listView;
    private TextView contactTxtView;
    private EditText typeMsgEdTxt;
    SimpleDateFormat simpleDateFormat;
    RequestQueue requestQueue;
    private int sender_id;
    private int receiver_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_message);
        contactTxtView = findViewById(R.id.individualReceiverNameTxtView);
        typeMsgEdTxt = findViewById(R.id.individualTypeMsgEdTxt);
        simpleDateFormat = new SimpleDateFormat("hh:mm a");

        msgList = new ArrayList<>();
        listView = findViewById(R.id.listViewMsgList);
        simpleAdapter = new SimpleAdapter(
                this,
                msgList,
                R.layout.layout_individual_message,
                new String[]{"msgText","msgTime"},
                new int[]{R.id.individualMsgTxtView,R.id.individualMsgTimeTxtView}
        );
        listView.setAdapter(simpleAdapter);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
         sender_id = b.getInt("student_id");
         receiver_id = b.getInt("teacher_id");
        String teacherName = b.getString("teacherName");
        contactTxtView.setText(teacherName);
        receive();

    }
    private void populateMsgList(String msg,String time) {
        msgItem = new HashMap<>();
        msgItem.put("msgText",msg);
        msgItem.put("msgTime",time);
        msgList.add(msgItem);
        simpleAdapter.notifyDataSetChanged();
    }
    public void receive(){
        final String url = "https://project-bond.herokuapp.com/messageReceive";
        requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(IndividualMessage.this, "" + response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            int size = jsonArray.length();
                            for(int i=0;i<size;i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int sender_id = jsonObject.getInt("sender_id");
                                int receiver_id = jsonObject.getInt("receiver_id");
                                String msg_txt = jsonObject.getString("msg_txt");
                                String msg_time = jsonObject.getString("msg_time");
                                populateMsgList(msg_txt,msg_time);
                            }
                        } catch (JSONException jsonexe) {
                            Toast.makeText(IndividualMessage.this, "Incorrect Json Format:"+jsonexe, Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(IndividualMessage.this, "error:"+error, Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> map = new HashMap<>();
                map.put("sender_id",""+sender_id);
                map.put("receiver_id",""+receiver_id);
                return map;
            }
        };
        requestQueue.add(request);
    }
    public void send(View view) {

        final String date = simpleDateFormat.format(new Date());
        final String strTypeMsgEdTxt = typeMsgEdTxt.getText().toString().trim();
        if(strTypeMsgEdTxt.isEmpty()){
            return;
        }
        final String url = "https://project-bond.herokuapp.com/messageSend";
        requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                    Toast.makeText(IndividualMessage.this, ""+response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(IndividualMessage.this, "error", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> map = new HashMap<>();
                map.put("sender_id",""+sender_id);
                map.put("receiver_id",""+receiver_id);
                map.put("msg_txt",strTypeMsgEdTxt);
                map.put("msg_time",new Date().toString());
                return map;
            }
        };
        requestQueue.add(request);
        populateMsgList(strTypeMsgEdTxt,date);
        typeMsgEdTxt.setText("");

    }
}
