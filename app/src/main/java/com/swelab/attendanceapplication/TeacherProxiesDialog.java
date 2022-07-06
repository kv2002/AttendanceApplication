package com.swelab.attendanceapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeacherProxiesDialog extends DialogFragment {

    private Map<String, Object> IPs;
    private ArrayList<String> listViewItems;
    private ListView listView;
    private boolean dataRecieved;
    private TextView exit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_teacher_proxies, container, false);
        listViewItems = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_items, listViewItems);
        listView = view.findViewById(R.id.list_view);
        exit = view.findViewById(R.id.exit);
        listView.setAdapter(adapter);
        FirebaseFirestore.getInstance().collection("attendanceCodes").document(selectedCourse.getCourseId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                listViewItems.clear();
                listViewItems.add("PROXIES:");
                Map<String, Object> currCode = (Map<String, Object>) task.getResult().get(selectedCode.getCode());
                IPs = (Map<String, Object>) currCode.get("IP");
                for (String ip : IPs.keySet()) {
                    List<String> students = (List<String>) IPs.get(ip);
                    if (students.size() > 1) {
                        listViewItems.add("\t" + ip + " :");
                        dataRecieved = true;
                        for (String student : students) {
                            if (dataRecieved = true) {
                                dataRecieved = false;
                                FirebaseFirestore.getInstance().collection("users").document(student).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        listViewItems.add(String.format("\t\t\t%s", task.getResult().get("name").toString()));
                                        dataRecieved = true;
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    }
                }
                if (listViewItems.size() == 1) {
                    listViewItems.clear();
                    listViewItems.add("NO PROXIES FOUND");
                    adapter.notifyDataSetChanged();
                }
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        return view;
    }
}
