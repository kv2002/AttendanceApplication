package com.swelab.attendanceapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Map;

public class StudentMyCoursesFragment extends Fragment {
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_courses, container, false);

        ArrayList<String> listViewItems = new ArrayList<>();
        ArrayList<String> DocID = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_items, listViewItems);
        listView = view.findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        DocumentReference studentDetails = FirebaseFirestore.getInstance().collection("Students").document(currUser.getFirebaseUser().getUid());


        studentDetails.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                listViewItems.clear();
                DocID.clear();
                Map<String, Object> student_courses = (Map<String, Object>) value.get("Classes");
                for (String classId : student_courses.keySet()) {
                    FirebaseFirestore.getInstance().collection("Classes").document(classId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot courseDet = task.getResult();
                            listViewItems.add(courseDet.get("courseID").toString());
                            adapter.notifyDataSetChanged();
                            DocID.add(classId);
                        }
                    });
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCourse.setCourseId(DocID.get(i));
                StudentMyCoursesDialog dialog = new StudentMyCoursesDialog();
                dialog.show(getParentFragmentManager(), "");
            }
        });
        return view;
    }
}