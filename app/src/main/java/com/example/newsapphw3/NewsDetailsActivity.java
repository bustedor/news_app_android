package com.example.newsapphw3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class NewsDetailsActivity extends AppCompatActivity {
    int news_id;
    Context context;
    NewsItem news_item;
    Toolbar toolbar;
    TextView title_textView;
    TextView date_textView;
    TextView text_textView;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        context = getApplicationContext();
        toolbar = findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("News Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent new_intent = new Intent(context, MainActivity.class);
                startActivity(new_intent);
            }
        });

        title_textView = findViewById(R.id.textView);
        date_textView = findViewById(R.id.textView2);
        imageView = findViewById(R.id.imageView2);
        text_textView = findViewById(R.id.textView4);

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

        Intent intent = new Intent(this, CommentsActivity.class);
        int news_id = news_item.id;
        intent.putExtra("news_id", news_id);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    class NewsTask extends AsyncTask<Integer, Void, String> {

        ProgressDialog progDailog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDailog = new ProgressDialog(NewsDetailsActivity.this);
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
            String urlStr = "http://94.138.207.51:8080/NewsApp/service/news/getnewsbyid/" + Integer.toString(id);

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

            try {

                JSONObject inputObj = new JSONObject(s);
                JSONArray array = inputObj.getJSONArray("items");
                int id = array.getJSONObject(0).getInt("id");
                String title = array.getJSONObject(0).getString("title");
                String text = array.getJSONObject(0).getString("text");
                String date_epoch = array.getJSONObject(0).getString("date");
                Timestamp ts = new Timestamp(Long.parseLong(date_epoch));
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String date = sdf.format(ts);
                String image_url = array.getJSONObject(0).getString("image");
                String category = array.getJSONObject(0).getString("categoryName");
                news_item = new NewsItem(id, title, text, date, image_url, category);

                Glide.with(context).asBitmap().load(news_item.image_url).into(imageView);
                title_textView.setText(news_item.title);
                date_textView.setText(news_item.date);
                text_textView.setText(news_item.text);
                progDailog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
