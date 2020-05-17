package com.example.popularmovies1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

//import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MainAdapter mainAdapter;
    private int sortBy=1;
    String launch_saved="";

    public JSONObject jsonresponse;

    public static final String API_KEY="ba1cbd940b7f3b9597fa89a52e81f3a8";
    public static final String BASE_URL_POPULAR="https://api.themoviedb.org/3/movie/popular";
    public static final String BASE_URL_RATED="https://api.themoviedb.org/3/movie/top_rated";
    public static final String PARAMETER1="api_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("PopularMovies");

        recyclerView =findViewById(R.id.recycler_view);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setHasFixedSize(true);

        Bundle extras=getIntent().getExtras();

        if(isOnline()) {
            if(savedInstanceState!=null)
            {
                if(savedInstanceState.getString("launch_saved").equals("rated"))
                {
                    launch_saved="rated";
                    new FetchInfo().execute("rated");
                }
                else
                {
                    launch_saved="popular";
                    new FetchInfo().execute("popular");
                }
                return;
            }
            if(extras!=null)
            {
                String s=extras.getString("launch");

                if(s.equals("rated"))
                {
                    launch_saved="rated";
                    new FetchInfo().execute("rated");
                }
                else
                {
                    launch_saved="popular";
                    new FetchInfo().execute("popular");
                }
            }
            else
            {
                launch_saved="popular";
                new FetchInfo().execute("popular");
            }
        }
        else
        {
            NoInternet();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isOnline())
        {
            NoInternet();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            alertDialog();
            return true;
        }
        else if(id==R.id.view_favorites)
        {
            Intent i = new Intent(this, Favorites.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    public void NoInternet()
    {
        try {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();

            alertDialog.setTitle("Info");
            alertDialog.setMessage("Internet not available, Cross check your internet connectivity and try again");
            alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();

                }
            });

            alertDialog.show();
        } catch (Exception e) {
//                Log.d(Constants.TAG, "Show Dialog: " + e.getMessage());
        }
    }

    void alertDialog()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Sort By");
        String[] items = {"popular","ratings"};
        int checkedItem = 0;
        sortBy=1;
        alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        sortBy=1;
                        break;
                    case 1:
                        sortBy=2;
                        break;
                }
            }
        });

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //sortBy=radioGroup.getCheckedRadioButtonId();
                if(sortBy==1)
                {
                    viewByPopularity();
                }
                else
                {
                    viewByRatings();
                }
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });


        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();

//        if(sortBy==1)
//        {
//            viewByPopularity();
//        }
//        else
//        {
//            viewByRatings();
//        }
    }

    void viewByPopularity()
    {
        if(isOnline()) {
            launch_saved="popular";
            new FetchInfo().execute("popular");
        }
        else
            NoInternet();
    }

    void viewByRatings()
    {
        if(isOnline()) {
            launch_saved="rated";
            new FetchInfo().execute("rated");
        }
        else
            NoInternet();
    }


    public static URL buildUrl(String sort)
    {
        Uri builtUri;
        if(sort.equals("rated")) {
            builtUri = Uri.parse(BASE_URL_RATED).buildUpon()
                    .appendQueryParameter(PARAMETER1, API_KEY).build();
        }
        else
        {
            builtUri = Uri.parse(BASE_URL_POPULAR).buildUpon()
                    .appendQueryParameter(PARAMETER1, API_KEY).build();
        }

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }



        return url;
    }

    public static String getResponseFromHTTPurl(URL movielisturl) throws IOException {

//        URL movielisturl=new URL(movielisturl1);
        Log.d("main_url", "Built URI " + movielisturl);

        HttpURLConnection urlConnection = (HttpURLConnection) movielisturl.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }

    }

//    @Override
//    public void onClick(String weatherForDay) {
//        Log.d("adapter","clicked_main");
//    }

    public class FetchInfo extends AsyncTask<String,Void,List<JSONObject>>
    {

        @Override
        protected List<JSONObject> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String sort = params[0];
            List<JSONObject> jsonObjects;

            URL movielisturl = buildUrl(sort);
            JSONObject json=null;



            String response_string=null;
            try {
                //response_string=getResponseFromHTTPurl("https://andfun-weather.udacity.com/staticweather?q=94043%2CUSA&mode=json&units=metric&cnt=14");

                response_string=getResponseFromHTTPurl(movielisturl);
                 json= new JSONObject(response_string);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            Log.d("main_json", response_string);
            JSONArray jsonResults= null;
            try {
                jsonResults = (JSONArray)json.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("main_json_array", String.valueOf(jsonResults));

            jsonObjects=new ArrayList<>();
            for(int i=0;i<jsonResults.length();i++)
            {
                try {
                    jsonObjects.add(jsonResults.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            List<String> imageIds=new ArrayList<>();
            for(int i=0;i<jsonObjects.size();i++)
            {
                try {
                    imageIds.add(jsonObjects.get(i).getString("backdrop_path"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("image_ids",imageIds.get(i));
            }

            return jsonObjects;
        }

        @Override
        protected void onPostExecute(List<JSONObject> jsonObjects) {

            mainAdapter = new MainAdapter(jsonObjects);

            recyclerView.setAdapter(mainAdapter);

        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("launch_saved",launch_saved);
    }
}
