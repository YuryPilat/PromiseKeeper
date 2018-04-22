package com.example.promisekeeper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerItemViewHolder> {
    private static final String CHOSEN_TITLE = "ChosenTitle";
    private static final String POSITION = "Position";
    public static final String TABLE_NAME = "TableName";
    public static final String DEFAULT_TABLE = "defTable";
    private ArrayList<RecyclerData> myList;
    private final String[] columns = {Root.getStringRes(R.string.COLUMN_DONE)};
    private final Context context;
    private DBManager dbManager;
    String title;

    RecyclerAdapter(Context context, DBManager dbManager, ArrayList<RecyclerData> myList, String title) {
        this.myList = myList;
        this.context = context;
        this.dbManager = dbManager;
        this.title = title;
    }

    @NonNull
    @Override
    public RecyclerItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new RecyclerItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerItemViewHolder holder, final int position) {
        holder.etTitleTextView.setText(myList.get(position).getTitle());
        holder.etDescriptionTextView.setText(myList.get(position).getDescription());

        Cursor titleCursor = dbManager.findEntry(Root.getString(TABLE_NAME, DEFAULT_TABLE),columns, String.valueOf(myList.get(position).getTitle()));
        String promiseDone = titleCursor.getString(titleCursor.getColumnIndex(Root.getStringRes(R.string.COLUMN_DONE)));
        if (promiseDone.equals(Root.getStringRes(R.string.STRING_DONE))){
            holder.etTitleTextView.setBackgroundColor(context.getResources().getColor(R.color.colorBackgroundGreen));
            holder.etDescriptionTextView.setBackgroundColor(context.getResources().getColor(R.color.colorBackgroundGreen));
        } else {
            holder.etTitleTextView.setBackgroundColor(context.getResources().getColor(R.color.colorCardRed));
            holder.etDescriptionTextView.setBackgroundColor(context.getResources().getColor(R.color.colorCardRed));
        }
    }

    @Override
    public int getItemCount() {
        return(null != myList?myList.size():0);
    }

    public void notifyData(ArrayList<RecyclerData> myList) {
        this.myList = myList;
        notifyDataSetChanged();
    }

    class RecyclerItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView etTitleTextView;
        private final TextView etDescriptionTextView;
        private LinearLayout mainLayout;

        RecyclerItemViewHolder(final View parent) {
            super(parent);
            etTitleTextView =  parent.findViewById(R.id.txtTitle);
            etDescriptionTextView = parent.findViewById(R.id.txtDescription);

            mainLayout = parent.findViewById(R.id.mainLayout);
            mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Root.setInteger(POSITION, getLayoutPosition());
                    String chosenTitle = myList.get(getLayoutPosition()).title;
                    Root.setString(CHOSEN_TITLE, chosenTitle);
                    Intent i = new Intent(context, PromiseActivity.class);
                    v.getContext().startActivity(i);
                }
            });

            mainLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showDeleteDialog(getLayoutPosition(), dbManager, myList);
                    return true;
                }
            });
        }
    }

    private void showDeleteDialog(final int position, final DBManager dbManager, final ArrayList<RecyclerData> myList) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(R.string.DELETE_ENTRY);

        dialog.setPositiveButton(R.string.DELETE, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dbManager.deleteData(myList.get(position).title);
                myList.remove(position);
                notifyDataSetChanged();
            }
        });

        dialog.setNegativeButton(R.string.CANCEL, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }
}