package com.example.myapplication;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class HistoryActivity extends AppCompatActivity {

    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;
    private EditText txtinput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ListView listView = (ListView) findViewById(R.id.listv);

        DataBaseHelper dbh = null;
        try {
            dbh = new DataBaseHelper(getApplicationContext());
        } catch(java.io.IOException ioe) {
            Log.d("MyApp", "Database doesn't exist");
        }
        try {
            dbh.opendatabase();
        } catch(java.sql.SQLException sqlE) {

        }

        Cursor resultSet = dbh.myDataBase.rawQuery("Select word from wordsTable Where seen = 1",null);
        if(resultSet.getCount() != 0) {
            resultSet.moveToFirst();
            String word = resultSet.getString(0);
            arrayList = new ArrayList<>(Arrays.asList(word));
            while (!resultSet.isLast()) {
                resultSet.moveToNext();
                word = resultSet.getString(0);
                arrayList.add(word);
            }
            adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.textitem, arrayList);
            listView.setAdapter(adapter);
        }

        resultSet.close();
    }
}
