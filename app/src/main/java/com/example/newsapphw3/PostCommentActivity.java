package com.example.newsapphw3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PostCommentActivity extends AppCompatActivity {
    int news_id;
    Context context;
    Toolbar  toolbar;
    EditText name_editText;
    EditText comment_editText;
    Button   button;
    String name;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment);
        context = getApplicationContext();
        name_editText = findViewById(R.id.editText2);
        comment_editText = findViewById(R.id.editText4);
        button = findViewById(R.id.button);
        toolbar = findViewById(R.id.action_bar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Post Comment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        news_id = intent.getIntExtra("news_id", 0);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                name = name_editText.getText().toString();
                text = comment_editText.getText().toString();
                // NOW USE NEWS API TO POST A COMMENT
                NewsTask task = new NewsTask();
                task.execute(name, text);
            }
        });

        /*
        ----- ON POST EXECUTEvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
        Intent new_intent = new Intent(this, CommentsActivity.class);
        intent.putExtra("news_id", news_id);
        startActivity(intent);

         */
    }

    class NewsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {


            StringBuilder strBuilder = new StringBuilder();
            String urlStr = "http://94.138.207.51:8080/NewsApp/service/news/savecomment";
            String name = strings[0];
            String text = strings[1];
            JSONObject obj = new JSONObject();
            try {
                obj.put("name", name);
                obj.put("text", text);
                obj.put("news_id", news_id);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type","application/json");
                conn.connect();

                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.writeBytes(obj.toString());


                if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line ="";

                    while((line = reader.readLine())!=null){
                        strBuilder.append(line);
                    }
                }
                else {
                    Toast.makeText(context, "Error posting the comment.",Toast.LENGTH_SHORT);
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
            Intent new_intent = new Intent(context, CommentsActivity.class);
            new_intent.putExtra("news_id", news_id);
            startActivity(new_intent);
        }
    }
}
