package com.swelab.attendanceapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class TeacherViewStatsDialog extends DialogFragment {

    private ListView listView;
    private ArrayList<String> listViewItems;
    private ArrayList<String> selectedItem;
    private Map<String, Object> studentReferences;
    private Map<String, Object> attendanceCodes;
    private TextView exit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_teacher_view_stats, container, false);

        listViewItems = new ArrayList<>();
        selectedItem = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_items, listViewItems);
        listView = view.findViewById(R.id.list_view);
        exit = view.findViewById(R.id.exit);
        listView.setAdapter(adapter);
        FirebaseFirestore.getInstance().collection("Classes").document(selectedCourse.getCourseId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                studentReferences = (Map<String, Object>) task.getResult().getData().get("Students");
                int totalStudents = studentReferences.size();
                FirebaseFirestore.getInstance().collection("attendanceCodes").document(selectedCourse.getCourseId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        attendanceCodes = value.getData();
                        Map<Timestamp, Integer> totalPresent = new TreeMap<>();
                        for (String code : attendanceCodes.keySet()) {
                            Map<String, Object> currCode = (Map<String, Object>) attendanceCodes.get(code);
                            totalPresent.put((Timestamp) currCode.get("startTime"), 0);
                        }
                        for (Object i : studentReferences.values()) {
                            DocumentReference student = (DocumentReference) i;
                            student.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    Map<String, Object> allClasses = (Map<String, Object>) task.getResult().get("Classes");
                                    Map<String, Object> currClass = (Map<String, Object>) allClasses.get(selectedCourse.getCourseId());
                                    listViewItems.clear();
                                    selectedItem.clear();
                                    listViewItems.add("Total Number Of Students : " + totalStudents);
                                    for (String code : attendanceCodes.keySet()) {
                                        Map<String, Object> currCode = (Map<String, Object>) attendanceCodes.get(code);
                                        Timestamp startTime = (Timestamp) currCode.get("startTime");
                                        if (currClass.containsKey(code)) {
                                            int k = totalPresent.get(startTime);
                                            totalPresent.put(startTime, k + 1);
                                        }
                                        listViewItems.add(startTime.toDate() + " : " + totalPresent.get(startTime));
                                        selectedItem.add(code);
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCode.setCode(selectedItem.get(i - 1));
                TeacherProxiesDialog dialog = new TeacherProxiesDialog();
                dialog.show(getParentFragmentManager(), "");
            }
        });
        return view;
    }
}
