package com.example.newsapphw3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.sql.Timestamp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static Context context;
    RecyclerView recyclerView;
    Spinner spinner;
    ArrayList<NewsItem> allNews = new ArrayList<NewsItem>();
    boolean taskDone = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.action_bar);

        setSupportActionBar(toolbar);

        context = getApplicationContext();
        spinner = (Spinner) findViewById(R.id.spinner);
        recyclerView = findViewById(R.id.recycler_view_main);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = parent.getItemAtPosition(position).toString();
                ArrayList<NewsItem> filteredNews = new ArrayList<>();

                if (category == "All") {
                    RecyclerViewAdapter adapter2 = new RecyclerViewAdapter(parent.getContext(), allNews);
                    recyclerView.setAdapter(adapter2);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                }
                else {

                    for (int i=0;i<allNews.size();i++) {
                        NewsItem item = allNews.get(i);
                        if (item.category_name.equals(category)) {
                            filteredNews.add(allNews.get(i));
                        }
                    }

                    // MODIFY
                    RecyclerViewAdapter adapter2 = new RecyclerViewAdapter(parent.getContext(), filteredNews);
                    recyclerView.setAdapter(adapter2);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        NewsTask myTask = new NewsTask();
        myTask.execute(0); // get categories and fill the spinner
        // when the first task finishes, i call my task again to get all news


    }


    class NewsTask extends AsyncTask<Integer, Void, String> {
        ProgressDialog progDailog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDailog = new ProgressDialog(MainActivity.this);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();
        }

        @Override
        protected String doInBackground(Integer... ıntegers) {
            int mode = ıntegers[0];
            String url_str="";
            StringBuilder strBuilder = new StringBuilder();

            if (mode == 0) {         // mode 0 is for getting categories
                url_str= "http://94.138.207.51:8080/NewsApp/service/news/getallnewscategories";
            }

            else if (mode == 1 ) {  // mode 1 is for getting all news
                url_str= "http://94.138.207.51:8080/NewsApp/service/news/getall";
            }


            try {
                strBuilder.append(Integer.toString(mode)); // this will be used on post execute to eliminate code duplication
                URL url = new URL(url_str);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line ="";

                    while((line = reader.readLine())!=null){
                        strBuilder.append(line);
                    }
                }
                else {
                    System.out.println("IN ELSE IN DOINBACKGROUND. SOMETHING WENT WRONG!");
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String result = strBuilder.toString();
            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            int mode = Character.getNumericValue(s.charAt(0));
            s = s.substring(1);
            if (mode == 0) {  // ON CALL, GETS THE CATEGORIES AND FILLS THE SPINNER
                try {
                    ArrayList<String> categories=new ArrayList<String>();
                    categories.add("All"); // server does not return a category for all news
                    JSONObject inputObj = new JSONObject(s);
                    JSONArray array = inputObj.getJSONArray("items");

                    for (int i=0; i<array.length(); i++) {
                        String id = array.getJSONObject(i).getString("id");
                        String name = array.getJSONObject(i).getString("name");
                        //NewsCategory cat = new NewsCategory(Integer.parseInt(id),name);7
                        categories.add(name);
                    }
                    progDailog.dismiss();
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(context,R.layout.support_simple_spinner_dropdown_item, categories);
                    spinner.setAdapter(adapter1);
                    NewsTask myTask = new NewsTask();
                    myTask.execute(1);
                    taskDone = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (mode == 1 ) {  // ON CALL, GETS ALL THE NEWS AND FILLS THE RECYCLER VIEW
                try {
                    JSONObject inputObj = new JSONObject(s);
                    JSONArray array = inputObj.getJSONArray("items");
                    for (int i=0; i<array.length(); i++) {
                        int id = Integer.parseInt(array.getJSONObject(i).getString("id"));
                        String title = array.getJSONObject(i).getString("title");
                        String text = array.getJSONObject(i).getString("text");
                        String date_epoch = array.getJSONObject(i).getString("date");
                        Timestamp ts = new Timestamp(Long.parseLong(date_epoch));
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        String date = sdf.format(ts);
                        String image_url = array.getJSONObject(i).getString("image");
                        String category = array.getJSONObject(i).getString("categoryName");
                        NewsItem item = new NewsItem(id, title, text, date, image_url, category);
                        allNews.add(item);
                    }

                    progDailog.dismiss();
                    // NOW NEWS ARE IN ALLNEWS ARRAY READY TO FILL THE RECYCLER VIEW
                    RecyclerViewAdapter adapter1 = new RecyclerViewAdapter(context, allNews);
                    recyclerView.setAdapter(adapter1);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }  else {  // something wrong
                System.out.println("IN THE LAST ELSE IN POST EXECUTE. SOMETHING WENT WRONG!");
            }


            //
        }
    }
}
