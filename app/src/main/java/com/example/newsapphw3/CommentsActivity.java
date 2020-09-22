package com.example.newsapphw3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CommentsActivity extends AppCompatActivity {
    boolean postedComment =false;
    int news_id;
    Toolbar toolbar;
    Context context;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        context = getApplicationContext();
        recyclerView = findViewById(R.id.recycler_view_comments);
        toolbar = findViewById(R.id.action_bar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent new_intent = new Intent(context, NewsDetailsActivity.class);
                new_intent.putExtra("news_id", news_id);
                startActivity(new_intent);
            }
        });

        Intent intent = getIntent();
        news_id = intent.getIntExtra("news_id", 0);
        NewsTask task = new NewsTask();
        task.execute(news_id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.second_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, PostCommentActivity.class);
        intent.putExtra("news_id", news_id);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    class NewsTask extends AsyncTask<Integer, Void, String> {

        ProgressDialog progDailog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDailog = new ProgressDialog(CommentsActivity.this);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();
        }

        @Override
        protected String doInBackground(Integer... ıntegers) {
            int id = ıntegers[0];
            StringBuilder strBuilder = new StringBuilder();
            String urlStr = "http://94.138.207.51:8080/NewsApp/service/news/getcommentsbynewsid/" + Integer.toString(id);

            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line ="";

                    while((line = reader.readLine())!=null){
                        strBuilder.append(line);
                    }
                }
                else {
                    System.out.println("IN ELSE IN COMMENTS ACTIVITY NEWSTASK. SOMETHING WENT WRONG");
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return strBuilder.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            ArrayList<CommentItem> comments = new ArrayList<>();
            try {
                JSONObject inputObj = new JSONObject(s);
                JSONArray array = inputObj.getJSONArray("items");

                for (int i=0; i<array.length(); i++) {
                    int comment_id = array.getJSONObject(i).getInt("id");
                    int news_id = array.getJSONObject(i).getInt("news_id");
                    String text = array.getJSONObject(i).getString("text");
                    String name = array.getJSONObject(i).getString("name");

                    CommentItem item = new CommentItem(comment_id, news_id, text, name);
                    comments.add(item);
                }

                // HERE ALL THE COMMENTS ARE IN COMMENTS ARRAY
                CommentsRecyclerViewAdapter adapter = new CommentsRecyclerViewAdapter(context, comments);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                progDailog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
