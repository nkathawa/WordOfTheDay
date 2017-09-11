package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

/**
 * Created by Navin on 8/21/2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    public DataBaseHelper(Context context) throws IOException {
        super(context,DB_NAME,null,1);
        this.mycontext=context;
        this.DB_PATH = "/data/data/com.example.myapplication/databases/";
//                "/data/data/"
//                + mycontext.getApplicationContext().getPackageName()
//                + "/databases/";
        boolean dbexist = checkdatabase();
        if (dbexist) {
            try {
                opendatabase();
            } catch(SQLException s) {

            }
        } else {
            System.out.println("Database doesn't exist");
            createdatabase();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void createdatabase() throws IOException {
        boolean dbexist = checkdatabase();
        if(dbexist) {

        } else {
            this.getReadableDatabase();
            try {
                copydatabase();
            } catch(IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkdatabase() {
        boolean checkdb = false;
        try {
            String myPath = "/data/data/com.example.myapplication/databases/theWordsDB.db";
//                    DB_PATH + DB_NAME;
            File dbfile = new File(myPath);
            checkdb = dbfile.exists();
        } catch(SQLiteException e) {
            System.out.println("Database doesn't exist");
        }
        return checkdb;
    }

    private void copydatabase() throws IOException {
        //Open your local db as the input stream
        InputStream myinput = mycontext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outfilename = "/data/data/com.example.myapplication/databases/theWordsDB.db";
//                DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myoutput = new FileOutputStream("/data/data/com.example.myapplication/databases/theWordsDB.db");

        // transfer byte to inputfile to outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myinput.read(buffer))>0) {
            myoutput.write(buffer,0,length);
        }

        //Close the streams
        myoutput.flush();
        myoutput.close();
        myinput.close();
    }

    public void opendatabase() throws SQLException {
        //Open the database
        String mypath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void close() {
        if(myDataBase != null) {
            myDataBase.close();
        }
        super.close();
    }

    private Context mycontext;

    //private String DB_PATH = mycontext.getApplicationContext().getPackageName()+"/databases/";
    private static String DB_NAME = "theWordsDB.db";//the extension may be .sqlite or .db
    public SQLiteDatabase myDataBase;
    private String DB_PATH;
//            = "/data/data/"
//            + mycontext.getApplicationContext().getPackageName()
//            + "/databases/";
}
