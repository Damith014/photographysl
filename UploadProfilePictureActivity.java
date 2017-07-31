package com.example.echonlabs.photographysrilanka;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.echonlabs.photographysrilanka.Constant.Constant;
import com.example.echonlabs.photographysrilanka.Service.UploadProfileImageService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UploadProfilePictureActivity extends AppCompatActivity {


    private Toolbar mToolbar;

    //ImageView
    private ImageView profileImage;

    //Button
    private Button btnSelectImage;
    //String
    private String icon, id;
    String image;
    Constant constant = new Constant();
    ProgressDialog loading = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_picture);

        if (checkInternetConenction()) {
            //Method Calling
            setActionBar();
            initial();
            clickEvent();
        } else {
            Intent intent = new Intent(getApplicationContext(), OfflineActivity.class);
            startActivity(intent);
        }
    }
    /*
    *
    * Click Button
    *
    * */

    private void clickEvent() {
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), SelectGalleryActivity.class);
//                startActivity(intent);
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, 0);
                loader(v);
            }
        });
    }

    /*
     *
     * Add waiting loader
     *
     * */
    private void loader(View v) {
        loading = new ProgressDialog(v.getContext());
        loading.setCancelable(true);
        loading.setMessage("Waiting...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.show();
    }

    /*
    *
    * Image Uploading
    *
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && requestCode == 0) {


            if (resultCode == RESULT_OK) {

                Uri targetUri = data.getData();

                Bitmap bitmap;
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
                    profileImage.setImageBitmap(resizedBitmap);
                    icon = ConvertBitmapToString(resizedBitmap);
                    Upload();

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     *
     * Convert Bitmap To String
     *
     * */
    public static String ConvertBitmapToString(Bitmap bitmap) {
        String encodedImage = "";

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        try {
            encodedImage = URLEncoder.encode(Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return encodedImage;
    }

    /*
     *
     * Upload Image
     *
     * */
    private void Upload() {

        System.out.println(id);
        UploadProfileImageService uploadProfileImageService = new UploadProfileImageService(id, icon) {
            @Override
            public void displayResult(JSONObject jsonObject) {
                loading.dismiss();
                displayRe(jsonObject);
            }

            @Override
            public void displayError() {
                displayErr();
            }
        };
        uploadProfileImageService.execute();
        System.out.println("Image String in function" + icon);
    }

    /*
    *
    * Display Result
    *
    * */
    private void displayRe(JSONObject jsonObject) {

        try {
            if (jsonObject.getInt("status") == constant.ERR_BAD_REQUEST) {
                Toast.makeText(getApplicationContext(), "BAD REQUEST", Toast.LENGTH_SHORT).show();

            } else if (jsonObject.getInt("status") == constant.ERR_SERVER_ERROR) {
                Toast.makeText(getApplicationContext(), "SERVER ERROR", Toast.LENGTH_SHORT).show();

            } else if (jsonObject.getInt("status") == constant.ERR_NOT_FOUND) {
                Toast.makeText(getApplicationContext(), "NOT FOUND", Toast.LENGTH_SHORT).show();

            } else if (jsonObject.getInt("status") == constant.ERR_ALREADY_EXISTS) {
                Toast.makeText(getApplicationContext(), "ALREADY EXISTS", Toast.LENGTH_SHORT).show();

            } else if (jsonObject.getInt("status") == constant.ERR_INCORRECT_ENDPOINT) {
                Toast.makeText(getApplicationContext(), "INCORRECT ENDPOINT", Toast.LENGTH_SHORT).show();

            } else if (jsonObject.getInt("status") == constant.ERR_NOT_AUTHENTICATED) {
                Toast.makeText(getApplicationContext(), "NOT AUTHENTICATED", Toast.LENGTH_SHORT).show();

            } else if (jsonObject.getInt("status") == constant.SUCCESS) {
                Toast.makeText(getApplicationContext(), "Upload success", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), SelectGalleryActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
    *
    * Display Request error
    *
    * */
    private void displayErr() {
        Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();

    }


    /*
    *
    * Initialized imageview button and layout
    *
    *
    * */
    private void initial() {
        //Image view
        profileImage = (ImageView) findViewById(R.id.imageProfile);

        //Button
        btnSelectImage = (Button) findViewById(R.id.btnSelect);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
    }

    /*
    *
    * Set Actionbar with toolbar
    * */
    private void setActionBar() {
        //Set Actionbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //Enable HomeUp
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select Profile Image");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace_white_24dp);

    }

    /*
    *
    *Back button and animations
    * @return menuItem
    *
    * */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /*
     *
     * Check Internet Connections
     *
     * */
    private boolean checkInternetConenction() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec
                = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() ==
                android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
            // Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();

            return true;
        } else if (
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() ==
                                android.net.NetworkInfo.State.DISCONNECTED) {

            return false;
        }
        return false;
    }
}
