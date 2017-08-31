package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.myapplication.R.drawable.word;
import static com.example.myapplication.R.id.fab;
import static com.example.myapplication.R.id.textView5;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SpiceManager spiceManager = new SpiceManager(UncachedSpiceService.class);

    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
//        super.onResume();
        spiceManager.shouldStop();
        super.onStop();
    }

//    @Override
//    protected void super.onResume() {
//
//    }

    private void performRequest(String searchQuery) {
//        resultTextView.setText("");

//        MainActivity.this.setProgressBarIndeterminateVisibility(true);

        ReverseStringRequest request = new ReverseStringRequest(searchQuery);
        spiceManager.execute(request, new ReverseStringRequestListener());

//        hideKeyboard();
    }

    private final class ReverseStringRequestListener implements
            RequestListener<String> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(MainActivity.this,
                    "Error: " + spiceException.getMessage(), Toast.LENGTH_SHORT)
                    .show();
        }

        @Override
        public void onRequestSuccess(String result) {
//            MainActivity.this.setProgressBarIndeterminateVisibility(false);
            Log.d("THE MSG:", result);
            TextView tv = (TextView)findViewById(R.id.textView5);
            tv.setText(result);
//            setContentView(tv);
//            tv.setText(getString(R.string.result_text, result));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("HELLO","MAIN RUNNING???");
        super.onCreate(savedInstanceState);

//        super.onResume();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        setContentView(R.layout.activity_main);
        DataBaseHelper dbh = null;
        try {
            dbh = new DataBaseHelper(this);
//            getApplicationContext()
        } catch(java.io.IOException ioe) {
            Log.d("MyApp", "Database doesn't exist");
        }
        try {
            dbh.opendatabase();
        } catch(java.sql.SQLException sqlE) {

        }

        Cursor resultSet = dbh.myDataBase.rawQuery("Select word from wordsTable Where seen = 0 order by RANDOM() LIMIT 1",null);
        resultSet.moveToFirst();
        String word = resultSet.getString(0);



//        spiceManager.start(this);
//        RequestListener requestListener = new RequestListener() {
//            @Override
//            public void onRequestFailure(SpiceException spiceException) {
//
//            }
//
//            @Override
//            public void onRequestSuccess(Object o) {
//
//            }
//        };
//        SpiceRequest spiceRequest = new SpiceRequest(("https://google.com").getClass()) {
//            @Override
//            public Object loadDataFromNetwork() throws Exception {
//                return null;
//            }
//
//            @Override
//            public int compareTo(@NonNull Object o) {
//                return 0;
//            }
//        };
//        spiceManager.execute(spiceRequest, requestListener);
//        spiceManager.shouldStop();

//        java.net.HttpURLConnection("http://www.dictionaryapi.com/api/v1/references/collegiate/xml/" + word + "?key=254cc331-d202-4e64-97cb-7e6f6d543fce");

        String word_orig = word;
        word = word.replaceAll("\'","\\\\'");

        performRequest(word);

        final String inner_word = word;
        dbh.myDataBase.execSQL("Update wordsTable Set seen = 1 Where word = '" + word + "';");
        // testing!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//        dbh.myDataBase.execSQL("Update wordsTable Set favorite = 1 Where word = '1080';");
        TextView the_word = (TextView) findViewById(R.id.the_word);
        the_word.setText(word_orig);
//        resultSet = dbh.myDataBase.rawQuery("Select seen from wordsTable Where word='" + word + "'",null);
//        String bool = resultSet.toString();
//        Log.d("D", bool);

        FloatingActionButton Fab = (FloatingActionButton) findViewById(R.id.fab);
        Fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        FloatingActionButton Fab1 = (FloatingActionButton) findViewById(R.id.fabFave);
        Fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataBaseHelper dbh1 = null;
                try {
                    dbh1 = new DataBaseHelper(getApplicationContext());
//            getApplicationContext()
                } catch(java.io.IOException ioe) {
                    Log.d("MyApp", "Database doesn't exist");
                }
                try {
                    dbh1.opendatabase();
                } catch(java.sql.SQLException sqlE) {

                }
                dbh1.myDataBase.execSQL("Update wordsTable Set favorite = 1 Where word = '" + inner_word + "';");

                Snackbar.make(view, "Word added to favorites", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        resultSet.close();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.favorites) {
            Intent intent = new Intent(this, FavoriteActivity.class);
            startActivity(intent);
//            setContentView(R.layout.activity_favorite_words);
            // Handle the camera action
        } else if (id == R.id.history) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
//            setContentView(R.layout.activity_word_history);
        } else if (id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
//            setContentView(R.layout.activity_main);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
