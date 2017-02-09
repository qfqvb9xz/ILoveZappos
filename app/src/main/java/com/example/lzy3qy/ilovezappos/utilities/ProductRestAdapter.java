package com.example.lzy3qy.ilovezappos.utilities;

import android.util.Log;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by LZY3QY on 2/7/17.
 */

public class ProductRestAdapter {
    private final String TAG = getClass().getSimpleName();
    private ProductAPI mApi;
    static final String PRODUCT_URL="https://api.zappos.com/";
    static final String OPEN_WEATHER_API = "b743e26728e16b81da139182bb2094357c31d331";

    public ProductRestAdapter() {
        Retrofit mRestAdapter = new Retrofit.Builder()
                .baseUrl(PRODUCT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mApi = mRestAdapter.create(ProductAPI.class); // create the interface
        Log.d(TAG, "Adapter -- created");
    }

    public void testProductApi(String product, Callback<ProductInfo> callback){
        Log.d(TAG, "testProductApi: for product:" + product);
        mApi.getProductFromApi(product, OPEN_WEATHER_API).enqueue(callback);
    }

}
