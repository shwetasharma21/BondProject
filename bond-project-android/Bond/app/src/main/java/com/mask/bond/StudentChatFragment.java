package com.mask.bond;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentChatFragment extends Fragment {
    Map<String,String> item;
    List<Map<String,String>> msgList;
    SimpleAdapter simpleAdapter;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_chat, container, false);
        loadUser();
        msgList = new ArrayList<>();
        listView = view.findViewById(R.id.fragmentStudentChatListView);
        simpleAdapter = new SimpleAdapter(
                getActivity(),
                msgList,R.layout.layout_chat_history,
                new String[]{"msgSender","msgTxt","msgTime"},
                new int[]{R.id.layoutChatHistorySenderTxtView,R.id.layoutChatHistoryMsgTxtView,
                             R.id.layoutChatHistoryMsgTimeView});
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                item = msgList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("msgSender",item.get("msgSender"));
                bundle.putString("msgText",item.get("msgTxt"));
                bundle.putString("msgTime",item.get("msgTime"));
                Intent i = new Intent(getActivity(),IndividualMessage.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
        items();
        return view;
    }
    private void loadUser(){
        Bundle bundle = getActivity().getIntent().getExtras();
        String name = bundle.getString("name");
        int userId = bundle.getInt("user_id");
        Toast.makeText(getActivity(),""+userId, Toast.LENGTH_SHORT).show();
    }
    private void items() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        item = new HashMap<>();
        simpleAdapter.notifyDataSetChanged();
    }
}