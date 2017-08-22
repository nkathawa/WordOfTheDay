package com.example.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class DisplayWordActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_word);
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

        Cursor resultSet = dbh.myDataBase.rawQuery("Select * from wordsTable",null);
        resultSet.moveToFirst();
        String word = resultSet.getString(0);
        System.out.print(word);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_word, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
