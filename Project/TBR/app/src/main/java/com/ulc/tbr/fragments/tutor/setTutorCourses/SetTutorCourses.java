package com.ulc.tbr.fragments.tutor.setTutorCourses;

import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ulc.tbr.activities.MainActivity;
import com.ulc.tbr.R;
import com.ulc.tbr.databases.DatabaseHelper;
import com.ulc.tbr.models.util.*;
import com.ulc.tbr.models.users.*;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link com.ulc.tbr.fragments.tutor.setTutorCourses.SetTutorCourses#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetTutorCourses extends Fragment implements AdapterView.OnItemSelectedListener {

    // UPPER MENU BEGIN
    Spinner spinner_homeMenu;
    ArrayList<String> homeMenu;
    ArrayAdapter<String> adapter_homeMenu;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "ListViewExample";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView selectListView;
    private ListView myClasses;
    private MainActivity ma;
    private DatabaseHelper dbHelper;
    private User user;

    Button button_addClasses;
    Button button_removeClasses;

    ArrayList<Course> courses;
    ArrayList<Course> myCourses;

    ArrayList<String> courseString;
    ArrayList<String> myCourseString;

    ArrayAdapter<String> courseArrayAdapter;
    ArrayAdapter<String> myClassesAdapter;


    public SetTutorCourses() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TutorSetClasses.
     */
    // TODO: Rename and change types and number of parameters
    public static SetTutorCourses newInstance(String param1, String param2) {
        SetTutorCourses fragment = new SetTutorCourses();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = this.getArguments();
        user = (User) bundle.getSerializable("user");
        return inflater.inflate(R.layout.fragment_tutor_set_classes, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        spinner_homeMenu = (Spinner) view.findViewById(R.id.spinner_homeMenu);

        homeMenu = new ArrayList<String>();

        int currFragmentIndex;

        if (user.isTutor() && user.isTutee()) { // StudentTutor
            homeMenu.add("Home");
            homeMenu.add("Get A Tutor");
            homeMenu.add("My Sessions");
            homeMenu.add("Change Availability");
            homeMenu.add("Change Courses");
            homeMenu.add("Logout");

            currFragmentIndex = 4;
        }
        else if (user.isTutor()) { // Tutor
            homeMenu.add("Home");
            homeMenu.add("My Sessions");
            homeMenu.add("Change Availability");
            homeMenu.add("Change Courses");
            homeMenu.add("Logout");

            currFragmentIndex = 3;
        }
        else if (user.isTutee()) { // Student
            homeMenu.add("Home");
            homeMenu.add("Get A Tutor");
            homeMenu.add("My Sessions");
            homeMenu.add("Logout");

            currFragmentIndex = 0; // student should not be able to navigate here
        }
        else {
            // ERROR UNREACHABLE CASE
            homeMenu = null;
            currFragmentIndex = 0;
        }

        adapter_homeMenu = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, homeMenu);
        spinner_homeMenu.setAdapter(adapter_homeMenu);

        spinner_homeMenu.setOnItemSelectedListener(this);
        spinner_homeMenu.setSelection(currFragmentIndex, true);
        spinner_homeMenu.setEnabled(true);
        spinner_homeMenu.setClickable(true);




        selectListView = (ListView) view.findViewById(R.id.listView_availableClasses);
        myClasses = (ListView) view.findViewById(R.id.listView_myClasses);

        button_addClasses = view.findViewById(R.id.button_addTutorClasses);
        button_removeClasses = view.findViewById(R.id.button_removeTutorClasses);

        button_addClasses.setEnabled(true);
        button_addClasses.setClickable(true);
        button_addClasses.setBackgroundColor(Color.RED);
        button_removeClasses.setEnabled(true);
        button_removeClasses.setClickable(true);
        button_removeClasses.setBackgroundColor(Color.RED);


        // courses = dbHelper.getDataCourses();
        // TODO: get courses
        myCourses = dbHelper.getTutorCourses(user.getStudentID());

        courseString = new ArrayList<String>();
        myCourseString = new ArrayList<String>();

        for(Course c : courses) {
            courseString.add(c.toStringSubjectCourseNo());
        }
        for(Course c : myCourses){
            myCourseString.add(c.toStringSubjectCourseNo());
            courseString.remove(c.toStringSubjectCourseNo());
        }
        this.selectListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        this.myClasses.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        courseArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_multiple_choice,courseString);
        myClassesAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_multiple_choice,myCourseString);
        this.myClasses.setAdapter(myClassesAdapter);
        this.selectListView.setAdapter(courseArrayAdapter);

        button_addClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray sp = selectListView.getCheckedItemPositions();
                StringBuilder sb = new StringBuilder();

                try {

                    for (int i = 0; i < sp.size(); i++) {
                        int currIndex;
                        if (sp.valueAt(i)) {
                             currIndex = sp.keyAt(i);
                            //String currSelection = sp.toString();
                            //int currIndex = Integer.parseInt(currSelection.split("=")[0]);

                            String course = (String) selectListView.getItemAtPosition(currIndex);

                            String course_num = course.substring(course.lastIndexOf(" ") + 1);
                            String subject = course.substring(0, course.length() - course_num.length() - 1);

                            int courseNo = Integer.parseInt(course_num);

                            dbHelper.addTutorCourse(user.getStudentID(), subject, courseNo);

                            Toast.makeText(ma, "Course selection saved", Toast.LENGTH_LONG).show();
                        }



                    }
                } catch(Exception e){
                    Toast.makeText(ma, "Course selection not saved! Try again", Toast.LENGTH_LONG).show();
                }

                myCourses = dbHelper.getTutorCourses(user.getStudentID());


                courseString = new ArrayList<String>();
                myCourseString = new ArrayList<String>();
                for (Course c : courses) {
                    courseString.add(c.toStringSubjectCourseNo());
                }
                for (Course c : myCourses) {
                    myCourseString.add(c.toStringSubjectCourseNo());
                    courseString.remove(c.toStringSubjectCourseNo());
                }
                courseArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_multiple_choice, courseString);
                myClassesAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_multiple_choice, myCourseString);
                myClasses.setAdapter(myClassesAdapter);
                selectListView.setAdapter(courseArrayAdapter);




            }





        });

        button_removeClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray sp = myClasses.getCheckedItemPositions();
                StringBuilder sb = new StringBuilder();

                try {

                    for (int i = 0; i < sp.size(); i++) {
                        int currIndex;
                        if (sp.valueAt(i)) {
                            currIndex = sp.keyAt(i);
                            //String currSelection = sp.toString();
                            //int currIndex = Integer.parseInt(currSelection.split("=")[0]);

                            String course = (String) myClasses.getItemAtPosition(currIndex);

                            String course_num = course.substring(course.lastIndexOf(" ") + 1);
                            String subject = course.substring(0, course.length() - course_num.length() - 1);

                            int courseNo = Integer.parseInt(course_num);

                            dbHelper.deleteTutorCourse(user.getStudentID(), subject, courseNo);

                            Toast.makeText(ma, "Course selection saved", Toast.LENGTH_LONG).show();
                        }



                    }
                } catch(Exception e){
                    Toast.makeText(ma, "Course selection not saved! Try again", Toast.LENGTH_LONG).show();
                }

                myCourses = dbHelper.getTutorCourses(user.getStudentID());


                courseString = new ArrayList<String>();
                myCourseString = new ArrayList<String>();
                for (Course c : courses) {
                    courseString.add(c.toStringSubjectCourseNo());
                }
                for (Course c : myCourses) {
                    myCourseString.add(c.toStringSubjectCourseNo());
                    courseString.remove(c.toStringSubjectCourseNo());
                }
                courseArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_multiple_choice, courseString);
                myClassesAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_multiple_choice, myCourseString);
                myClasses.setAdapter(myClassesAdapter);
                selectListView.setAdapter(courseArrayAdapter);




            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spinner_homeMenu) {
            String menuSelection = (String) spinner_homeMenu.getSelectedItem();

            Bundle userData = new Bundle();
            userData.putSerializable("user", user);

            switch(menuSelection) {
                case "Home":
                    NavHostFragment.findNavController(SetTutorCourses.this)
                            .navigate(R.id.action_set_tutor_courses_to_home, userData);

                    break;
                case "Get A Tutor":
                    NavHostFragment.findNavController(SetTutorCourses.this)
                            .navigate(R.id.action_set_tutor_courses_to_get_a_tutor, userData);

                    break;
                case "My Sessions":
                    NavHostFragment.findNavController(SetTutorCourses.this)
                            .navigate(R.id.action_set_tutor_courses_to_my_sessions, userData);

                    break;
                case "Change Availability":
                    NavHostFragment.findNavController(SetTutorCourses.this)
                            .navigate(R.id.action_set_tutor_courses_to_set_tutor_availability, userData);

                    break;
//                case "Change Courses":
//                    NavHostFragment.findNavController(TutorSetCourses.this)
//                            .navigate(R.id.action_set_tutor_courses_to_set_tutor_courses, userData);
//
//                    break;
                case "Logout" :
                    NavHostFragment.findNavController(SetTutorCourses.this)
                            .navigate(R.id.action_set_tutor_courses_logout, userData);

                    break;
                default :
//                    NavHostFragment.findNavController(com.ulc.tbr.fragments.common.home.Home.this)
//                            .navigate(R.id.action_home_to_home, userData);
                    // do nothing
                    // refresh home?
                    break;
            }


        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if (parent.getId() == R.id.spinner_homeMenu) {

        }

    }
    // UPPER MENU END


}