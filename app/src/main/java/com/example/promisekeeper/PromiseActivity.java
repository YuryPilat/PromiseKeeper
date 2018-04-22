package com.example.promisekeeper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class PromiseActivity extends MainActivity {
    private static final String CHOSEN_TITLE = "ChosenTitle";
    private static final String POSITION = "Position";
    private TextView titlePromise;
    private TextView descriptionPromise;
    private TextView addingDate;
    private TextView status_text;
    private TextView promiseDoneDate;
    private TextView counterText;
    private Button btnPlus1;
    private Button btnPromiseDone;
    final String[] columns = {Root.getStringRes(R.string.COLUMN_TITLE), Root.getStringRes(R.string.COLUMN_DESCRIPTION)
            , Root.getStringRes(R.string.COLUMN_COUNTER)
            , Root.getStringRes(R.string.COLUMN_START_DATE)
            , Root.getStringRes(R.string.COLUMN_END_DATE)
            , Root.getStringRes(R.string.COLUMN_DONE)
    };

    ConstraintLayout myLay;
    Cursor titleCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promise);

        myLay = findViewById(R.id.promiseLayout);
        myLay.setBackgroundColor(getResources().getColor(R.color.colorBackgroundRed));
        titlePromise = findViewById(R.id.titlePromise);
        addingDate = findViewById(R.id.adding_date);
        status_text = findViewById(R.id.status_text);
        promiseDoneDate = findViewById(R.id.promiseDoneDate);
        descriptionPromise = findViewById(R.id.descriptionPromise);
        counterText = findViewById(R.id.counterText);
        btnPlus1 = findViewById(R.id.addPlus1);
        btnPromiseDone = findViewById(R.id.promiseDone);

        if (isPromiseDone()) {
            setData();
           showWindowAsDone();
            if (descriptionPromise.length()==0){
                descriptionPromise.setHint("");
            }
        } else {
            setData();
            makeEditable();
        }
    }

    public  void onCickPlus (View view) {
        switch (view.getId()){
            case R.id.addPlus1:
                titleCursor =dbManager.findEntry(Root.getString(TABLE_NAME, DEFAULT_TABLE),columns, Root.getString(CHOSEN_TITLE, ""));
                int counter = titleCursor.getInt(titleCursor.getColumnIndex(Root.getStringRes(R.string.COLUMN_COUNTER)));
                counterText.setText(String.valueOf(counter+1));
                dbManager.updateCounter(Root.getString(CHOSEN_TITLE, ""), counter+1);
                titleCursor.close();
                break;

            case R.id.promiseDone:
                showWindowAsDone();
                dbManager.updateData(Root.getStringRes(R.string.COLUMN_END_DATE), Root.getString(CHOSEN_TITLE, ""), getCurrentDateTime());
                dbManager.updateData(Root.getStringRes(R.string.COLUMN_DONE), Root.getString(CHOSEN_TITLE, ""), Root.getStringRes(R.string.STRING_DONE));
                titleCursor =dbManager.findEntry(Root.getString(TABLE_NAME, DEFAULT_TABLE),columns, Root.getString(CHOSEN_TITLE, ""));
                promiseDoneDate.setText(titleCursor.getString(titleCursor.getColumnIndex(Root.getStringRes(R.string.COLUMN_END_DATE))));
                status_text.setText(titleCursor.getString(titleCursor.getColumnIndex(Root.getStringRes(R.string.COLUMN_DONE))));
                break;

            case R.id.btnDelete:
                showDeleteDialog();
                break;
        }
    }

    private void showWindowAsDone() {
        btnPromiseDone.setEnabled(false);
        btnPromiseDone.setText("");
        btnPromiseDone.setBackgroundResource(R.drawable.galochka);
        btnPlus1.setVisibility(View.INVISIBLE);
        myLay.setBackgroundColor(getResources().getColor(R.color.colorBackgroundGreen));
    }

    private boolean isPromiseDone() {
        titleCursor =dbManager.findEntry(Root.getString(TABLE_NAME, DEFAULT_TABLE),columns, Root.getString(CHOSEN_TITLE, ""));
        String promiseDone = titleCursor.getString(titleCursor.getColumnIndex(Root.getStringRes(R.string.COLUMN_DONE)));
        return !promiseDone.equals(Root.getStringRes(R.string.STRING_PROCESSING));

    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TEMPLATE, Locale.getDefault());
        return sdf.format(new Date());
    }

    void deleteData() {
        dbManager.deleteData(Root.getString(CHOSEN_TITLE, ""));
        myList.remove(Root.getInteger(POSITION, 0));
        this.finish();
        Toast.makeText(getApplicationContext(), R.string.ENTRY_DELETED, Toast.LENGTH_SHORT).show();
    }

    private void showDeleteDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.ARE_YOU_SURE);

        dialog.setPositiveButton(R.string.DELETE, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                deleteData();
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

    void setData(){
        Cursor titleCursor =dbManager.findEntry(Root.getString(TABLE_NAME, DEFAULT_TABLE),columns, Root.getString(CHOSEN_TITLE, ""));
        titlePromise.setText(titleCursor.getString(titleCursor.getColumnIndex(Root.getStringRes(R.string.COLUMN_TITLE))));
        descriptionPromise.setText(titleCursor.getString(titleCursor.getColumnIndex(Root.getStringRes(R.string.COLUMN_DESCRIPTION))));
        counterText.setText(String.valueOf(titleCursor.getInt(titleCursor.getColumnIndex(Root.getStringRes(R.string.COLUMN_COUNTER)))));
        addingDate.setText(titleCursor.getString(titleCursor.getColumnIndex(Root.getStringRes(R.string.COLUMN_START_DATE))));
        promiseDoneDate.setText(titleCursor.getString(titleCursor.getColumnIndex(Root.getStringRes(R.string.COLUMN_END_DATE))));
        status_text.setText(titleCursor.getString(titleCursor.getColumnIndex(Root.getStringRes(R.string.COLUMN_DONE))));
    }

    private void makeEditable() {
        descriptionPromise.setHintTextColor(Color.parseColor("#828282"));
        descriptionPromise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                final EditText titleBox = new EditText(view.getContext());
                titleBox.setText("");
                titleBox.setHint(R.string.ENTER_DESCRIPTION);
                LinearLayout layout = new LinearLayout(view.getContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(titleBox);
                dialog.setTitle(R.string.NEW_DESCRIPTION);
                dialog.setView(layout);

                if (descriptionPromise.length()>0){
                    titleBox.setText(descriptionPromise.getText().toString());
                }

                dialog.setPositiveButton(R.string.DONE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        editDescription(titleBox.getText().toString());

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
        });
    }

    private void editDescription(String descriptionNew) {
        dbManager.updateData(Root.getStringRes(R.string.COLUMN_DESCRIPTION), Root.getString(CHOSEN_TITLE, ""), descriptionNew);
        Cursor titleCursor =dbManager.findEntry(Root.getString(TABLE_NAME, DEFAULT_TABLE),columns, Root.getString(CHOSEN_TITLE, ""));
        String description = titleCursor.getString(titleCursor.getColumnIndex(Root.getStringRes(R.string.COLUMN_DESCRIPTION)));
        descriptionPromise.setText(description);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
