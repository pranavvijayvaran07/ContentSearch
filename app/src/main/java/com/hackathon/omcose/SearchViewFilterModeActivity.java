package com.hackathon.omcose;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;

import androidx.core.content.FileProvider;

import com.hackathon.omcose.fileindexer.PdfIndexer;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class SearchViewFilterModeActivity extends Activity implements SearchView.OnQueryTextListener {


    private static final String TAG = "SearchViewFilterMode";
    private SearchView  mSearchView ;
    private ListView mListView ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.searchview_filter);
        mSearchView = (SearchView) findViewById(R.id.search_view);
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setTextFilterEnabled(true);
        setupSearchView();

    }


    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(false);
    }

    public boolean onQueryTextChange(String newText) {

        if (newText.length() < 3) {
            return false;
        }

        //List<HashMap<String,String>> results= PdfIndexer.getInstance().query(newText);
        List<HashMap<String, String>> results = LuceneManager.query(newText);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, results, android.R.layout.simple_list_item_2,
                new String[]{"Title", "FilePath"}, new int[]{android.R.id.text1, android.R.id.text2});
        mListView.setAdapter(simpleAdapter);

        // open pdf file on clicking the file option .
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File file = new File(results.get(position).get("FilePath"));
                Uri filePath =FileProvider.getUriForFile(mListView.getContext(),getApplicationContext().getPackageName()+".provider", file);
                Intent pdfOpenIntent = new Intent(Intent.ACTION_VIEW);
                pdfOpenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pdfOpenIntent.setDataAndType(filePath, "application/pdf");
                grantUriPermission("com.google.android.apps.docs", filePath, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    startActivity(pdfOpenIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return true;

    }

        public boolean onQueryTextSubmit(String query) {
            return false;
        }


    }




