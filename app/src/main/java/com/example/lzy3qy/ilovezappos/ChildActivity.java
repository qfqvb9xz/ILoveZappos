/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lzy3qy.ilovezappos;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.dk.animation.circle.CircleAnimationUtil;
import com.example.lzy3qy.ilovezappos.databinding.ActivityChildBinding;

import java.io.InputStream;

public class ChildActivity extends AppCompatActivity {

    ActivityChildBinding mBinding;

    public void AddCart(String productId, String productNum) {
        //do something for cart!
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_child);

        //spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.planetsSpinner.setAdapter(adapter);
        //FAB
        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChildActivity.this, "Add to cart", Toast.LENGTH_LONG).show();
                new CircleAnimationUtil().attachActivity(ChildActivity.this)
                        .setTargetView(mBinding.planetsSpinner).setDestView(mBinding.fab).startAnimation();
                mBinding.planetsSpinner.setVisibility(View.VISIBLE);
                AddCart(ProductId,mBinding.planetsSpinner.getSelectedItem().toString());
            }
        });
        mBinding.Detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebPage(detailPage);
            }
        });
        //Get Info from Main_activity
        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            String[] productData = intentThatStartedThisActivity.getStringArrayExtra(Intent.EXTRA_TEXT);
            showData(productData);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.action_logout) {
            startActivity(new Intent(ChildActivity.this, RegisterActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //Get info from URL
    private void openWebPage(String url) {

        Uri webpage = Uri.parse(url);

        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void showData(String[] productData){
        mBinding.BNAME.setText(productData[0]);

        mBinding.PNAME.setText(productData[1]);

        mBinding.PRICE.setText(productData[2]);

        String tmp;
        if (! productData[3].equals("0%") ) {
            tmp = productData[3] + " Off";
        }
        else{
            tmp = "";
        }
        mBinding.DISCOUNT.setText(tmp);
        new DownloadImageTask(mBinding.searchResultsImg).execute(productData[4]);

        detailPage = productData[5];

        ProductId = productData[6];
    }

    private String detailPage = "";

    private String ProductId = "";

    //Info section end
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}