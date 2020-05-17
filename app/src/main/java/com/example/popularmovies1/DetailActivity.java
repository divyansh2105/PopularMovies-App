package com.example.popularmovies1;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static com.example.popularmovies1.MainActivity.API_KEY;
import static com.example.popularmovies1.MainActivity.getResponseFromHTTPurl;
import static com.example.popularmovies1.MainAdapter.BASE_IMAGE_URL;


public class DetailActivity extends AppCompatActivity
        implements YouTubePlayer.OnInitializedListener,
         LoaderManager.LoaderCallbacks<String>
    {


    ImageView imageView;
    TextView title_tv,overview_tv,release_date_tv,rating_tv,review_tv;
    ImageButton starButton;
    String imageUrl,title,overview,rating,release_date,id;
    String youtube_url;
    String youtube_video_key="";
    YouTubePlayer youTubePlayer;
    JSONObject json = null;

    int flag=1;

    private static final String SEARCH_QUERY_URL_EXTRA = "query";
    private static final int LOADER_ID=14;
    private static final int LOADER_ID2=15;

    public static final String INSTANCE_TASK_ID = "instanceTaskId";
    private static final int DEFAULT_TASK_ID = -1;
    private int mTaskId = DEFAULT_TASK_ID;
    private AppDatabase mDb;

    List<TaskEntry> mtaskEntry;

    private final static String youtubeAPI="AIzaSyBcRem94SClEDSU2Z3qodFH3DfpHgA7bqo";
    public final String BASE_URL_MOVIE="https://api.themoviedb.org/3/movie/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(!isOnline())
        {
            NoInternet();
        }

        Log.d("created","created again");
        setTitle("PopularMovies");
        imageView=findViewById(R.id.image_view_detail);
        title_tv=findViewById(R.id.movie_title);
        overview_tv=findViewById(R.id.overview);
        release_date_tv=findViewById(R.id.release_date);
        rating_tv=findViewById(R.id.user_rating);
        review_tv=findViewById(R.id.review_tv);
        starButton=findViewById(R.id.star_button);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                imageUrl= null;
                title=null;
                rating=null;
                release_date=null;
                overview=null;
                id=null;
            } else {
                imageUrl= extras.getString("imageUrl");
                title= extras.getString("title");
                rating= extras.getString("rating");
                overview= extras.getString("overview");
                release_date= extras.getString("release_date");
                id=extras.getString("id");
            }
        } else {
            imageUrl= (String) savedInstanceState.getSerializable("imageUrl");
            title= (String) savedInstanceState.getSerializable("title");
            rating= (String) savedInstanceState.getSerializable("rating");
            overview= (String) savedInstanceState.getSerializable("overview");
            release_date= (String) savedInstanceState.getSerializable("release_date");
            id=(String) savedInstanceState.getSerializable("id");
        }

        title_tv.setText(title);


        YouTubePlayerFragment youtubePlayerFragment = new YouTubePlayerFragment();
        youtubePlayerFragment.initialize(youtubeAPI, this);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.youtube_fragment, youtubePlayerFragment);
        transaction.commit();


        Log.d("detail_imageurl",BASE_IMAGE_URL+imageUrl);


        Picasso.get().load(BASE_IMAGE_URL+imageUrl).into(imageView);


        overview_tv.setText(overview);
        release_date_tv.setText(release_date);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            rating_tv.setText(Html.fromHtml("<h2>"+rating+"</h2>/10", Html.FROM_HTML_MODE_COMPACT));
        } else {
            rating_tv.setText(Html.fromHtml("<h2>"+rating+"</h2>/10"));
        }

        String review_url_string=BASE_URL_MOVIE+String.valueOf(id)+"/reviews?api_key="+API_KEY;
        getReview(review_url_string);

        mDb = AppDatabase.getInstance(getApplicationContext());




        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getTasks().observe(this, new Observer<List<TaskEntry>>() {
            @Override
            public void onChanged(@Nullable List<TaskEntry> taskEntries) {
                Log.d("TAG2", "Updating list of tasks from LiveData in ViewModel");
                mtaskEntry=taskEntries;


                    for(int i=0;i<mtaskEntry.size();i++)
                    {
                        Log.d("detail_db_id",mtaskEntry.get(i).getId()+mtaskEntry.get(i).getTitle());
                        //mtaskEntry.add(taskEntries.get(i));
                    }


                if(mtaskEntry==null)
                {
                    Log.d("detail_curid","null");
                    flag=1;
                }
                else
                {
                    Log.d("detail_curid",id);
                    for(int i=0;i<mtaskEntry.size();i++)
                    {
                        if(mtaskEntry.get(i).getId().equals(id))
                        {
                            Log.d("detail_db_id2",mtaskEntry.get(i).getId());
                            flag=0;
                            break;
                        }
                    }
                }
                if(flag==1)
                {
                    starButton.setImageResource(R.drawable.ic_star_not_favorite);
                }
                else
                {
                    starButton.setImageResource(R.drawable.ic_star_favorite);
                }

            }
        });


    }


        private void getReview(String review_url_string) {

            URL url;
            try {
                url=new URL(review_url_string);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }



            new ReviewLoader(review_url_string);

        }



        class ReviewLoader implements LoaderManager.LoaderCallbacks<String>
        {

            ReviewLoader(String review_url_string)
            {
                Bundle queryBundle = new Bundle();
                // COMPLETED (20) Use putString with SEARCH_QUERY_URL_EXTRA as the key and the String value of the URL as the value

                queryBundle.putString("review_url", review_url_string);

                LoaderManager loaderManager = getSupportLoaderManager();
                // COMPLETED (22) Get our Loader by calling getLoader and passing the ID we specified
                Loader<String> githubSearchLoader = loaderManager.getLoader(LOADER_ID2);
                // COMPLETED (23) If the Loader was null, initialize it. Else, restart it.
                if (githubSearchLoader == null) {
                    loaderManager.initLoader(LOADER_ID2, queryBundle, this);
                } else {
                    loaderManager.restartLoader(LOADER_ID2, queryBundle, this);
                }

                getSupportLoaderManager().initLoader(LOADER_ID2, null, this);
            }

            @NonNull
            @Override
            public Loader<String> onCreateLoader(int id, @Nullable final Bundle args) {
                return new AsyncTaskLoader<String>(getBaseContext()) {

                    // COMPLETED (5) Override onStartLoading
                    @Override
                    protected void onStartLoading() {

                        if(!isOnline())
                        {
                            NoInternet();
                        }
                        // COMPLETED (6) If args is null, return.
                        /* If no arguments were passed, we don't have a query to perform. Simply return. */
                        if (args == null) {
                            return;
                        }

                        forceLoad();
                    }

                    // COMPLETED (9) Override loadInBackground
                    @Override
                    public String loadInBackground() {

                        boolean b=isOnline();
                        Log.d("boolean", String.valueOf(b));
                        if(!b)
                        {
                            NoInternet();
                        }
                        String url_string = args.getString("review_url");

                        List<JSONObject> jsonObjects;
                        Log.d("detail_url", url_string);

                        URL url = null;
                        try {
                            url = new URL(url_string);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
//
                        Log.d("detail_url", String.valueOf(url));

                        String response_string=null;
                        try {

                            response_string=getResponseFromHTTPurl(url);
                            if(response_string==null)
                            {
                                return "";
                            }
                            json= new JSONObject(response_string);

                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            if(response_string==null)
                            {
                                return "";
                            }
                        }
                       if(!b)
                       {
                           NoInternet();
                       }
                       else {
                           Log.d("main_json 2", response_string);
                           JSONArray jsonResults = null;
                           try {
                               jsonResults = (JSONArray) json.getJSONArray("results");
                           } catch (JSONException e) {
                               e.printStackTrace();
                               jsonError();
                           }
                           Log.d("main_json_arra 2y", String.valueOf(jsonResults));

                           JSONObject object2 = null;
                           try {
                               if(jsonResults==null)
                               {
                                   jsonError();
                               }
                               else {
                                   object2 = jsonResults.getJSONObject(0);
                               }
                           } catch (JSONException e) {
                               e.printStackTrace();
                           }
                           try {
                                if(object2==null)
                                {
                                    jsonError();
                                }
                                else {
                                    Log.d("detail_keyyyyy", object2.getString("content"));
                                    return object2.getString("content");
                                }
                           } catch (JSONException e) {

                               e.printStackTrace();
                               jsonError();
                           }
                       }
                        return null;
                    }
                };
            }

            @Override
            public void onLoadFinished(@NonNull Loader<String> loader, String data) {

                if(data==null || data.equals(""))
                {
                    return;
                }

                review_tv.setText(data);;
            }

            @Override
            public void onLoaderReset(@NonNull Loader<String> loader) {

            }
        }

        public void star_button_clicked(View view) {


            if(flag==1)
            {
                Toast.makeText(this,"Added to Favorites",Toast.LENGTH_SHORT).show();
                starButton.setImageResource(R.drawable.ic_star_favorite);

                final TaskEntry task = new TaskEntry(id,title);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mTaskId == DEFAULT_TASK_ID) {
                            // insert new task
                            mDb.taskDao().insertTask(task);
                        }
                    }
                });
                flag=0;
            }
            else
            {
                Toast.makeText(this,"Removed from Favorites",Toast.LENGTH_SHORT).show();
                flag=1;
                starButton.setImageResource(R.drawable.ic_star_not_favorite);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        TaskEntry taskEntry=new TaskEntry(id,title);
                        mDb.taskDao().deleteTask(taskEntry);
                    }
                });

            }

        }

        public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
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

        public void jsonError()
        {
            try {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();

                alertDialog.setTitle("Info");
                alertDialog.setMessage("Error ocurred while retrieving the data");
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("imageUrl",imageUrl);
        outState.putString("title",title);
        outState.putString("rating",rating);
        outState.putString("overview",overview);
        outState.putString("release_date",release_date);
        outState.putString("id",id);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youtube_url=BASE_URL_MOVIE+String.valueOf(id)+"/videos?api_key="+API_KEY;



        this.youTubePlayer=youTubePlayer;

        Bundle queryBundle = new Bundle();
        // COMPLETED (20) Use putString with SEARCH_QUERY_URL_EXTRA as the key and the String value of the URL as the value

        queryBundle.putString(SEARCH_QUERY_URL_EXTRA, youtube_url);

        LoaderManager loaderManager = getSupportLoaderManager();
        // COMPLETED (22) Get our Loader by calling getLoader and passing the ID we specified
        Loader<String> githubSearchLoader = loaderManager.getLoader(LOADER_ID);
        // COMPLETED (23) If the Loader was null, initialize it. Else, restart it.
        if (githubSearchLoader == null) {
            loaderManager.initLoader(LOADER_ID, queryBundle, this);
        } else {
            loaderManager.restartLoader(LOADER_ID, queryBundle, this);
        }

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);


    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this,"fail", Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            // COMPLETED (5) Override onStartLoading
            @Override
            protected void onStartLoading() {

                // COMPLETED (6) If args is null, return.
                /* If no arguments were passed, we don't have a query to perform. Simply return. */
                if (args == null) {
                    return;
                }

                forceLoad();
            }

            @Override
            public String loadInBackground() {

                String url_string = args.getString(SEARCH_QUERY_URL_EXTRA);

                List<JSONObject> jsonObjects;
                Log.d("detail_url", url_string);

                URL url = null;
                try {
                    url = new URL(url_string);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
//
                Log.d("detail_url", String.valueOf(url));

                String response_string=null;
                try {

                    response_string=getResponseFromHTTPurl(url);
                    if(response_string==null)
                    {
                        return "";
                    }
                    json= new JSONObject(response_string);

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    if(response_string==null)
                    {
                        return "";
                    }
                }
                if(response_string==null)
                {
                    return "";
                }
                try {
                    json= new JSONObject(response_string);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                boolean b=isOnline();
                if(!b)
                {
                    NoInternet();

                }
                else {
                    JSONArray jsonResults = null;
                    try {
                        jsonResults = (JSONArray) json.getJSONArray("results");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("main_json", String.valueOf(jsonResults));

                    JSONObject object2 = null;
                    try {
                        if(jsonResults.length()==0)
                        {
                            return "";
                        }
                        object2 = jsonResults.getJSONObject(0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        Log.d("detail_key", object2.getString("key"));
                        return object2.getString("key");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };

    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {

        if(data==null || data.equals(""))
        {
            return;
        }
        youtube_video_key=data;
        boolean b=isOnline();
        if(!b)
        {
            NoInternet();

        }
        else {
            youTubePlayer.cueVideo(youtube_video_key);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }



}
