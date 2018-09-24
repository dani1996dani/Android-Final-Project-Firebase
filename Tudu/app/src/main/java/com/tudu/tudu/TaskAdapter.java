package com.tudu.tudu;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tudu.tudu.Enums.ImportantColor;
import com.tudu.tudu.Interfaces.ListItemClickedListener;
import com.tudu.tudu.Interfaces.SettingsListener;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskViewHolder> implements SettingsListener {

    private List<Task> importantTasks;
    private List<Task> regularTasks;
    private ListItemClickedListener listItemClickedListener;
    private ImportantColor importantColor;


    public TaskAdapter(List<Task> importantTasks, List<Task> regularTasks, ListItemClickedListener listItemClickedListener, ImportantColor importantColor) {
        this.importantTasks = importantTasks;
        this.regularTasks = regularTasks;
        this.listItemClickedListener = listItemClickedListener;
        this.importantColor = importantColor;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, final int position) {
        final Task task;
        final int taskListIndex; // the index of the task in its SPECIFIC list.

        //calculating if the position is of an important task or a regular task, and setting the taskListIndex accordingly.
        if (position < importantTasks.size()) {
            task = importantTasks.get(position);
            taskListIndex = position;
        } else {
            taskListIndex = position - importantTasks.size();
            task = regularTasks.get(taskListIndex);

        }

        //setting the title and content according to the Task object.
        holder.title.setText(task.getTitle());
        holder.content.setText(task.getContent());

        //setting the color of the item based on the importance, stored in the Task object.
        int importance = task.getImportance();
        int regularColor = holder.linearLayout.getResources().getColor(R.color.regularTask);

        if (importance == 0) {
            holder.linearLayout.setBackgroundColor(regularColor);
        } else {
            int importantColor = holder.linearLayout.getResources().getColor(getImportantColor());
            holder.linearLayout.setBackgroundColor(importantColor);
        }

        //Handing the click of an item in the list.
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listItemClickedListener.onItemClick(task, taskListIndex);
            }
        });
    }

    @Override
    public int getItemCount() {
        return importantTasks.size() + regularTasks.size();
    }

    /**
     * Changing the important color,when it was changed.
     * @param importantColor the importantColor that was selected.
     */
    @Override
    public void onImportantColorChanged(ImportantColor importantColor) {
        setImportantColor(importantColor);
        //notifying this so the screen will redraw itself with the new colors.
        notifyDataSetChanged();
    }

    public void setImportantColor(ImportantColor importantColor) {
        this.importantColor = importantColor;
    }

    /**
     *
     * @return returns the color based on the importantColor that is currently selected.
     */
    private int getImportantColor() {
        int result = 0;
        switch (importantColor) {
            case Yellow:
                result = R.color.importantTaskYellow;
                break;
            case Green:
                result = R.color.importantTaskGreen;
                break;
            case Red:
                result = R.color.importantTaskRed;
                break;
        }

        return result;
    }
}

class TaskViewHolder extends RecyclerView.ViewHolder {

    TextView title, content;
    LinearLayout linearLayout; //The whole item that is displyed.

    public TaskViewHolder(View itemView) {
        super(itemView);
        linearLayout = (LinearLayout) itemView;
        title = itemView.findViewById(R.id.txtTaskTitle);
        content = itemView.findViewById(R.id.txtTaskContent);
    }


}
