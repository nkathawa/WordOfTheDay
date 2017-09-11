package com.example.myapplication;

import android.net.Uri;
import android.os.Build;

import com.octo.android.robospice.request.SpiceRequest;

import org.apache.commons.io.IOUtils;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Navin on 8/26/2017.
 */

public class ReverseStringRequest extends SpiceRequest<String> {

    private String word;

    public ReverseStringRequest(String word) {
        super(String.class);
        this.word = word;
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        String url = "http://www.dictionaryapi.com/api/v1/references/collegiate/xml/" + word
                + "?key=254cc331-d202-4e64-97cb-7e6f6d543fce";

        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url)
                .openConnection();
        String result = IOUtils.toString(urlConnection.getInputStream());
        urlConnection.disconnect();

        return result;
    }
}
