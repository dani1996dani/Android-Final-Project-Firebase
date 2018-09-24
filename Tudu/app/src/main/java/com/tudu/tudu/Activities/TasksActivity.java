package com.tudu.tudu.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.tudu.tudu.AlertDialogManager;
import com.tudu.tudu.Consts;
import com.tudu.tudu.Enums.ImportantColor;
import com.tudu.tudu.Interfaces.ListItemClickedListener;
import com.tudu.tudu.R;
import com.tudu.tudu.Task;
import com.tudu.tudu.TaskAdapter;

import java.util.ArrayList;
import java.util.List;

public class TasksActivity extends AppCompatActivity implements ListItemClickedListener {

    RecyclerView tasksList;
    FirebaseDatabase firebaseDatabase;
    List<Task> importantTasks;
    List<Task> regularTasks;
    TaskAdapter taskAdapter;
    int currentTaskListIndex;
    boolean isImportantTask;
    DatabaseReference reference;
    ChildEventListener childEventListener;
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        importantTasks = new ArrayList<>();
        regularTasks = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        Toast.makeText(this, "Logged in as :" + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();

        tasksList = findViewById(R.id.list_tasks);

        //Getting the color saved in the sharedPrefs and if none exists, load the default yellow color.
        SharedPreferences sharedPreferences = getSharedPreferences(Consts.PREFS, MODE_PRIVATE);
        String importantColorString = sharedPreferences.getString(Consts.IMPORTANT_COLOR_ID, "yellow");
        ImportantColor importantColor = getImportantColor(importantColorString);

        tasksList.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(importantTasks, regularTasks, this, importantColor);
        tasksList.setAdapter(taskAdapter);
        loadTaskData();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Removes the childeventlistener from the query.
        query.removeEventListener(childEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tasks_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                logout();
                break;
            case R.id.menu_settings:
                //show the settings alert dialog.
                AlertDialogManager.getAlertDialogManager().getSettingsAlertDialog(this, taskAdapter).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Loads all Tasks data from Firebase Database that belongs to the current user. The childEventListener is attached to the query at this point.
     */
    public void loadTaskData() {

        //get the reference to the Tasks node in the main database.
        reference = FirebaseDatabase.getInstance().getReference().child("Tasks");

        //prepare the query to get only tasks that belong to this specific user.
        query = reference.orderByChild("userUid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //if it in an important task, add it to the important list, if not, add to the regular list.
                //after adding to the list, scroll to the added tasks position.
                Task task = dataSnapshot.getValue(Task.class);
                if (task.getImportance() == 1) {
                    importantTasks.add(task);
                    int insertTo = importantTasks.size() - 1;
                    taskAdapter.notifyDataSetChanged();
                    tasksList.scrollToPosition(insertTo);
                } else if (task.getImportance() == 0) {
                    regularTasks.add(task);
                    int insertTo = importantTasks.size() + regularTasks.size() - 1;
                    taskAdapter.notifyDataSetChanged();
                    tasksList.scrollToPosition(insertTo);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Task task = dataSnapshot.getValue(Task.class);

                //remove the task the was changed from the relevant list.

                if (isImportantTask) {
                    importantTasks.remove(currentTaskListIndex);
                    taskAdapter.notifyDataSetChanged();
                } else if (!isImportantTask) {
                    regularTasks.remove(currentTaskListIndex);
                    taskAdapter.notifyDataSetChanged();
                }

                //insert the new task to the relevant list,and scroll to its new position.

                if (task.getImportance() == 1) {
                    importantTasks.add(task);
                    tasksList.scrollToPosition(importantTasks.size() - 1);
                } else if (task.getImportance() == 0) {
                    regularTasks.add(task);
                    tasksList.scrollToPosition(importantTasks.size() + regularTasks.size() - 1);
                }
                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Task deletedTask = dataSnapshot.getValue(Task.class);

                //delete the task from the relevant position.

                if (deletedTask.getImportance() == 1) {
                    importantTasks.remove(currentTaskListIndex);
                } else if (deletedTask.getImportance() == 0) {
                    regularTasks.remove(currentTaskListIndex);
                }
                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        query.addChildEventListener(childEventListener);

    }

    /**
     * Logs out the user and sends him back to the LoginActivity.
     */
    public void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(this, LoginActivity.class);
        i.putExtra(Consts.INTENT_FRAGMENT_TYPE, 1);
        startActivity(i);
        finish();
    }

    //Shows the add new task alert dialog. (This is an onClick function from the xml - attached to the + button).
    public void btnAddTask(View view) {
        AlertDialogManager.getAlertDialogManager().getAddTaskAlertDialog(this, firebaseDatabase).show();
    }

    /**
     * Shows the edit task alert dialog with the relevant data to display.
     * @param currentTask The Task object that the item clicked on represents.
     * @param position The index of the task in its <B>RELEVANT</B> list.
     */
    @Override
    public void onItemClick(final Task currentTask, int position) {
        isImportantTask = currentTask.getImportance() == 1 ? true : false;

        currentTaskListIndex = position;
        AlertDialog alertDialog = AlertDialogManager.getAlertDialogManager().getEditTaskAlertDialog(this, currentTask, firebaseDatabase);
        alertDialog.show();
    }

    /**
     *
     * @param savedString the string of the color.
     * @return returns an enum object based on the string that was passed in.
     */
    public ImportantColor getImportantColor(String savedString) {
        switch (savedString) {
            case "yellow":
                return ImportantColor.Yellow;
            case "green":
                return ImportantColor.Green;
            case "red":
                return ImportantColor.Red;
        }
        return ImportantColor.Yellow;
    }

    @Override
    public void onBackPressed() {
        Consts.defaultBackPressed(this);
    }
}
