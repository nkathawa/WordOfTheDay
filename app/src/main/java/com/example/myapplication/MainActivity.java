package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.method.ScrollingMovementMethod;
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
import static org.apache.commons.lang3.StringUtils.countMatches;
import static org.apache.commons.lang3.StringUtils.ordinalIndexOf;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;

import org.apache.commons.lang3.StringUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SpiceManager spiceManager = new SpiceManager(UncachedSpiceService.class);
    private String the_word;
    private String the_result;

    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    private void performRequest(String searchQuery) {
        ReverseStringRequest request = new ReverseStringRequest(searchQuery);
        spiceManager.execute(request, new ReverseStringRequestListener());
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
            Log.d("THE MSG:", result);
            TextView tv = (TextView)findViewById(R.id.textView5);
            tv.setMovementMethod(new ScrollingMovementMethod());

            int numDefs1 = StringUtils.countMatches(result, "<dt>");
            if(numDefs1 == 0){
                result = "There are no definitions available for this word. Better luck next time!";
                the_result = result;
                tv.setText(result);
                return;
            }
            int numDefs = StringUtils.countMatches(result, "</dt>");
            result = result.substring(ordinalIndexOf(result, "<dt>", 1), ordinalIndexOf(result, "</dt>", numDefs));
//            result = result.replaceAll("[</>]", " ");

            result = result.replace("<dt>", "Definition: ");
            result = result.replaceAll("<[^>]+>", "");
//            result = result.replaceAll("[</>]", "");
            result = result.replaceAll(":", "");
            result = result.replace("Definition ", "\nDefinition: ");
            result = result.replaceAll("[1234567890]", "");

            the_result = result;

            tv.setText(result);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        performRequest(the_word);
//        performRequest("percival");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);
        if (isFirstRun) {
            startActivity(new Intent(MainActivity.this, StartupActivity.class));
            Toast.makeText(MainActivity.this, "First Run", Toast.LENGTH_LONG)
                    .show();
        }

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).commit();

        Log.d("HELLO","MAIN RUNNING???");
        super.onCreate(savedInstanceState);

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

        DataBaseHelper dbh = null;
        try {
            dbh = new DataBaseHelper(this);
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

        String word_orig = word;
        word = word.replaceAll("\'","\\\\'");
        final String share_word = word;
        the_word = word;

        performRequest(word);

        final String inner_word = word;
        dbh.myDataBase.execSQL("Update wordsTable Set seen = 1 Where word = '" + word + "';");
        TextView the_word = (TextView) findViewById(R.id.the_word);
        the_word.setText(word_orig);

        FloatingActionButton Fab = (FloatingActionButton) findViewById(R.id.fab);
        Fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "The word I learned today is " + '"' + share_word + '"' +
                    " and the definition(s) are: " + the_result);
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
        } else if (id == R.id.history) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
