package com.swelab.attendanceapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TeacherAllCoursesFragment extends Fragment {

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

        CollectionReference allCourses = FirebaseFirestore.getInstance().collection("Classes");

        allCourses.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                listViewItems.clear();
                DocID.clear();
                for (DocumentSnapshot i : value.getDocuments()) {
                    listViewItems.add(i.get("courseID").toString());
                    DocID.add(i.getId());
                }
                adapter.notifyDataSetChanged();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCourse.setCourseId(DocID.get(i));
                TeacherAllCoursesDialog dialog = new TeacherAllCoursesDialog();
                dialog.show(getParentFragmentManager(), "");
            }
        });
        return view;
    }
}