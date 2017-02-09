package com.example.lzy3qy.ilovezappos;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lzy3qy.ilovezappos.utilities.ProductInfo;
import com.example.lzy3qy.ilovezappos.utilities.ProductRestAdapter;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private EditText mSearchBoxEditText;
    protected final String TAG = getClass().getSimpleName();
    private RetainedAppData mRetainedAppData;

    //Check network connection, works fine on phone but not studio
    public boolean isInternetAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSearchBoxEditText = (EditText) findViewById(R.id.et_search_box);

        mRetainedAppData = new RetainedAppData();

        mRetainedAppData.setAppContext(this);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            if (isInternetAvailable(MainActivity.this)){
                String prodQuery = mSearchBoxEditText.getText().toString();
                if (!prodQuery.isEmpty()) {
                    mRetainedAppData.runRetrofitTestAsync(prodQuery);
                } else {
                    Toast.makeText(this, "Please enter a query", Toast.LENGTH_LONG).show();
                }
                return true;
            }
            else{
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
            }
        }
        if (id == R.id.action_logout) {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class RetainedAppData {
        private WeakReference<MainActivity> mActivityRef;
        protected final String TAG = "RTD";
        private ProductInfo mData; // ProductInfo data received
        private AtomicBoolean mInProgress = new AtomicBoolean(false); // Is a download in progress
        private ProductRestAdapter mProductInfoRestAdapter; // REST Adapter
        private Callback<ProductInfo> mProductInfoCallback = new Callback<ProductInfo>() {
            @Override
            public void onResponse(Call<ProductInfo> call, Response<ProductInfo> response) {
                // response.isSuccessful() is true if the response code is 2xx
                if (response.isSuccessful()) {
                    ProductInfo data = response.body();
                    Log.d(TAG, "Load Data");
                    mData = data;
                    if (mActivityRef.get() != null) {
                        mActivityRef.get().updateProductInfo(mData);
                        mActivityRef.get().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //do sth
                            }
                        });
                    }
                    mInProgress.set(false);
                } else {
                    int statusCode = response.code();

                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                    Log.d(TAG,"Error code:" + statusCode + ", Error:" + errorBody);
                }
            }

            @Override
            public void onFailure(Call<ProductInfo> call, Throwable t) {
                mInProgress.set(false);
                if (mActivityRef.get() != null) {
                    mActivityRef.get().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //do sth
                        }
                    });
                }
            }
        };
        // Method to test Async. call
        public void runRetrofitTestAsync (final String productInfo) {
            if ( (mActivityRef.get() != null) && (mInProgress.get())) {
                Toast.makeText(mActivityRef.get(),"ProductInfo fetch in progress.",Toast.LENGTH_LONG).show();
                return;
            }
            // Get the Adapter
            if (mProductInfoRestAdapter == null)
                mProductInfoRestAdapter = new ProductRestAdapter();

            // Test delay
            try {
                mInProgress.set(true);
                mProductInfoRestAdapter.testProductApi(productInfo, mProductInfoCallback); // Call Async API
            } catch (Exception e) {
                Log.d(TAG, "Thread sleep error" + e);
            }
        }

        void setAppContext (MainActivity ref) {
            mActivityRef = new WeakReference<>(ref);
        }
    }
    //Pass REST Results to Child Activity
    private void updateProductInfo (ProductInfo productData){
        if (!productData.getTotalResultCount().equals("0")) {

            Context context = MainActivity.this;

            Class destinationActivity = ChildActivity.class;

            Intent startChildActivityIntent = new Intent(context, destinationActivity);

            startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, productData.getResults());

            startActivity(startChildActivityIntent);

        }
        else{
            Toast toast = Toast.makeText(this, "No Search Result", Toast.LENGTH_LONG);
            toast.show();
        }
    }

}
