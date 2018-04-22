package com.example.promisekeeper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String CURRENT_USER = "CurrentUser";
    private static final String DONT_ASK = "DontAsk";
    public static final String TABLE_NAME = "TableName";
    public static final String DEFAULT_TABLE = "defTable";
    private static final String ALL = "all";
    private static final String SORTING = "Sorting";
    private static final String MENU_TABLE = "меню";
    public static final String DATE_TEMPLATE = "dd.MM.yyyy, HH:mm";
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mRecyclerAdapter;
    ArrayList<RecyclerData> myList = new ArrayList<>();
    ArrayList<RecyclerData> myMenuList = new ArrayList<>();
    String title = "";
    String description = "";
    private RecyclerData mLog;
    DBManager dbManager;
    int count;
    String[] columns;
    NavigationView navigationView;
    Menu nav_menu;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbManager = new DBManager(this);
        columns = new String[]{Root.getStringRes(R.string.COLUMN_TITLE), Root.getStringRes(R.string.COLUMN_DESCRIPTION)};
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView =  findViewById(R.id.recycler_view);
        mRecyclerAdapter = new RecyclerAdapter( this, dbManager, myList, title);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setAlpha((float) 0.8);
        mLog = new RecyclerData();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toolbar = findViewById(R.id.toolbar);

        refreshMenu();
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddPromiseDialog();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureToolbar() {
        toolbar.setTitle(Root.getString(CURRENT_USER, getResources().getString(R.string.MY_PROMISES)));
        if (Root.getString(CURRENT_USER, getResources().getString(R.string.MY_PROMISES)).equals(getResources().getString(R.string.MY_PROMISES))) {
            toolbar.getMenu().findItem(R.id.delete_user).setVisible(false);
        } else {
            toolbar.getMenu().findItem(R.id.delete_user).setVisible(true);
        }
    }

    private void animateRecycler() {
        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {
                        mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
                            View v = mRecyclerView.getChildAt(i);
                            v.setAlpha(0.0f);
                            v.animate().alpha(1.0f)
                                    .setDuration(300)
                                    .setStartDelay(i * 80)
                                    .start();
                        }
                        return true;
                    }
                });

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if ( Root.getBoolean(DONT_ASK, false)) {
                    String deleting = myList.get(viewHolder.getAdapterPosition()).title;
                    dbManager.deleteData(deleting);
                    myList.remove(viewHolder.getAdapterPosition());
                    mRecyclerAdapter.notifyData(myList);
                } else {
                    showDeleteDialog(viewHolder.getAdapterPosition(), dbManager, myList);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void showDeleteDialog(final int position, final DBManager dbManager, final ArrayList<RecyclerData> myList) {
        View checkBoxView = View.inflate(this, R.layout.chckbox, null);
        final CheckBox checkBox = checkBoxView.findViewById(R.id.checkbox);
        checkBox.setText(R.string.ASK_NO_MORE);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.DELETE_ENTRY);
        dialog.setCancelable(false);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (checkBox.isChecked()) {
                    Root.setBoolean(DONT_ASK, true);
                }

                if (!checkBox.isChecked()) {
                    Root.setBoolean(DONT_ASK, false);
                }
            }
        });
        dialog.setView(checkBoxView);
        dialog.setPositiveButton(R.string.DELETE, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dbManager.deleteData(myList.get(position).title);
                myList.remove(position);
                mRecyclerAdapter.notifyData(myList);
            }
        });

        dialog.setNegativeButton(R.string.CANCEL, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                mRecyclerAdapter.notifyData(myList);
            }
        });
        dialog.show();
    }

    private void showAddPromiseDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        final EditText titleBox = new EditText(this);
        titleBox.setText("");
        titleBox.setHint(R.string.PROMISE);
        titleBox.setHintTextColor(getResources().getColor(R.color.text_hint_color));


        final EditText descriptionBox = new EditText(this);
        descriptionBox.setHint(R.string.DESCRIPTION);
        descriptionBox.setHintTextColor(getResources().getColor(R.color.text_hint_color));

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(titleBox);
        layout.addView(descriptionBox);
        dialog.setTitle(R.string.NEW_PROMISE);

        dialog.setView(layout);

        dialog.setPositiveButton(R.string.ADD, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                title = titleBox.getText().toString();

                for (int i=0; i<myList.size(); i++) {
                    if( myList.get(i).title.equals(title)){
                        Toast.makeText(getApplicationContext(), R.string.NAME_EXISTS, Toast.LENGTH_SHORT).show();
                        title="";
                        dialog.dismiss();
                    }
                }

                description = descriptionBox.getText().toString();
                if (title.matches("")) {
                    Toast.makeText(getApplicationContext(), R.string.FIELD_EMPTY, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    mLog = new RecyclerData();
                    mLog.setTitle(title);
                    mLog.setDescription(description);
                    myList.add(mLog);
                    mRecyclerAdapter.notifyData(myList);
                    count = 0;
                    SimpleDateFormat sdf = new SimpleDateFormat(DATE_TEMPLATE, Locale.getDefault());
                    dbManager.setData(title, description, sdf.format(new Date()));
                }
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

    private void showAddingUserDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        final EditText titleBox = new EditText(this);

        titleBox.setText("");
        titleBox.setHint(R.string.INPUT_NAME);
        titleBox.setFilters(new InputFilter[] {
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start,
                                               int end, Spanned dest, int dstart, int dend) {
                        if(source.equals("")){
                            return source;
                        }
                        if(source.toString().matches("[a-zA-Zа-яА-Я]+")){
                            return source;
                        }
                        return "";
                    }
                }
        });

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(titleBox);
        dialog.setTitle(R.string.NEW_USER);
        dialog.setView(layout);

        dialog.setPositiveButton(R.string.ADD, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                title = titleBox.getText().toString();
                for (int i=0; i<myMenuList.size(); i++) {
                    if( myMenuList.get(i).title.equals(title)){
                        Toast.makeText(getApplicationContext(), R.string.NAME_EXISTS, Toast.LENGTH_SHORT).show();
                        title="";
                        dialog.dismiss();
                    }
                }
                if (title.matches("")) {
                    Toast.makeText(getApplicationContext(), R.string.FIELD_EMPTY, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    dbManager.setMenuData(title);
                    Root.setString(CURRENT_USER, title);
                    toolbar.setTitle(Root.getString(CURRENT_USER, ""));
                    String newTbale = title.replaceAll("\\s","");
                    Root.setString(TABLE_NAME, newTbale);
                    dbManager.createUserTable(newTbale);
                    nav_menu = navigationView.getMenu();
                    nav_menu.add(R.id.users_menu, R.id.users_menu, 1, title);
                    refreshList(columns, Root.getString(SORTING, ALL) );
                }

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

    private void refreshMenu() {
        myMenuList.clear();
        nav_menu = navigationView.getMenu();
        String[] menu_columns= {Root.getStringRes(R.string.COLUMN_TITLE)};
        Cursor titleCursor = dbManager.getMenuData(menu_columns);

        if (titleCursor.moveToFirst()) {
            do {
                count++;
                String title = titleCursor.getString(titleCursor.getColumnIndex(Root.getStringRes(R.string.COLUMN_TITLE)));
                mLog = new RecyclerData();
                mLog.setTitle(title);
                myMenuList.add(mLog);
            }
            while (titleCursor.moveToNext());
            titleCursor.close();
        }

        for (int x =0; x<myMenuList.size(); x++) {
            String item_menu = myMenuList.get(x).getTitle();
            nav_menu.add(R.id.users_menu, Menu.NONE, 1, item_menu);
        }
        count=0;
    }

    private void refreshList(String[] columns, String sorting) {
        myList.clear();
        Cursor titleCursor = dbManager.getDataByParams(columns);

        if (titleCursor.moveToFirst()) {
            do {
                count++;
                String title = titleCursor.getString(titleCursor.getColumnIndex(Root.getStringRes(R.string.COLUMN_TITLE)));
                String description = titleCursor.getString(titleCursor.getColumnIndex(Root.getStringRes(R.string.COLUMN_DESCRIPTION)));
                if (isPromiseDone(title).equals(sorting)) {
                    mLog = new RecyclerData();
                    mLog.setTitle(title);
                    mLog.setDescription(description);
                    myList.add(mLog);
                    mRecyclerAdapter.notifyData(myList);
                }
                if (ALL.equals(sorting)) {
                    mLog = new RecyclerData();
                    mLog.setTitle(title);
                    mLog.setDescription(description);
                    myList.add(mLog);
                    mRecyclerAdapter.notifyData(myList);
                }
            }
            while (titleCursor.moveToNext());
            titleCursor.close();
        }
        mRecyclerAdapter.notifyData(myList);
        count=0;
    }

    private String isPromiseDone(String title) {
        final String[] columns = {Root.getStringRes(R.string.COLUMN_DONE)};
        Cursor titleCursor = dbManager.findEntry(Root.getString(TABLE_NAME, DEFAULT_TABLE),columns,title);
        return titleCursor.getString(titleCursor.getColumnIndex(Root.getStringRes(R.string.COLUMN_DONE)));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu myMenu) {
        getMenuInflater().inflate(R.menu.main, myMenu);
        configureToolbar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.show_all:
                Root.setString(SORTING, ALL);
                break;
            case R.id.show_done:
                Root.setString(SORTING, getResources().getString(R.string.STRING_DONE));
                break;
            case R.id.show_undone:
                Root.setString(SORTING, getResources().getString(R.string.STRING_PROCESSING));
                break;
            case R.id.delete_user:
                deleteUserData();
                break;
        }
        animateRecycler();
        refreshList(columns, Root.getString(SORTING, ALL));
        return super.onOptionsItemSelected(item);
    }

    private void deleteUserData() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.DELETE_USER);
        dialog.setPositiveButton(R.string.DELETE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbManager.deleteTable(Root.getString(CURRENT_USER, "").replaceAll("\\s",""));
                dbManager.deleteData(MENU_TABLE, Root.getString(CURRENT_USER, "").replaceAll("\\s",""));
                Root.setString(CURRENT_USER, getResources().getString(R.string.MY_PROMISES));
                Root.setString(TABLE_NAME, DEFAULT_TABLE);
                refreshActivity();
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

    private void refreshActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        this.finish();
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        String selectedItem = item.toString().replaceAll("\\s","");
        if (getResources().getString(R.string.ADDNEWUSER).equals(selectedItem)) {
            showAddingUserDialog();
            toolbar.getMenu().findItem(R.id.delete_user).setVisible(true);

        } else if (getResources().getString(R.string.MYPROMISES).equals(selectedItem)) {
            Root.setString(TABLE_NAME, DEFAULT_TABLE);
            Root.setString(CURRENT_USER, item.toString());
            animateRecycler();
            toolbar.getMenu().findItem(R.id.delete_user).setVisible(false);

        } else {
            Root.setString(TABLE_NAME, selectedItem);
            Root.setString(CURRENT_USER, item.toString());
            animateRecycler();
            toolbar.getMenu().findItem(R.id.delete_user).setVisible(true);

        }
        toolbar.setTitle(Root.getString(CURRENT_USER, ""));
        refreshList(columns, ALL);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshList(columns, Root.getString(SORTING, ALL) );
        animateRecycler();
    }
}

