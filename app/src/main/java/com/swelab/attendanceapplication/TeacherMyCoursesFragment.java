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
import java.util.List;


public class TeacherMyCoursesFragment extends Fragment {

    private ListView listView;
    private ArrayList<String> listViewItems;
    private ArrayList<String> DocID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_courses, container, false);

        listViewItems = new ArrayList<>();
        DocID = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_items, listViewItems);
        listView = view.findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        DocumentReference teacherDetails = FirebaseFirestore.getInstance().collection("Teachers").document(currUser.getFirebaseUser().getUid());

        teacherDetails.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                listViewItems.clear();
                DocID.clear();
                List<String> teacher_courses = (List<String>) value.get("Courses");
                for (String classId : teacher_courses) {
                    FirebaseFirestore.getInstance().collection("Classes").document(classId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot courseDet = task.getResult();
                            listViewItems.add(courseDet.get("courseID").toString());
                            DocID.add(classId);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCourse.setCourseId(DocID.get(i));
                TeacherMyCoursesDialog dialog = new TeacherMyCoursesDialog();
                dialog.show(getParentFragmentManager(), "");
            }
        });
        return view;
    }
}