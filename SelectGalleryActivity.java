package com.example.echonlabs.photographysrilanka;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.echonlabs.photographysrilanka.Constant.Constant;
import com.example.echonlabs.photographysrilanka.Service.UploadGalleryImagesServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SelectGalleryActivity extends AppCompatActivity {

    private static Toolbar mToolbar;
    private Button btnUploadImage;
    int PICK_IMAGE_MULTIPLE = 1;
    String imageEncoded, id, icon;
    List<String> imagesEncodedList;
    Constant constant = new Constant();
    ProgressDialog loading = null;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_gallery);
        //Method Calling
        setActionBar();
        inital();
        onclickEvent();

    }

    /*
     *
     * Set Initailzed
     * */
    private void inital() {
        btnUploadImage = (Button) findViewById(R.id.btnUploadGallery);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
    }

    /*
     *
     * Set Onclick Event
     * */
    private void onclickEvent() {
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
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
        loading.setMax(12);
        loading.setSecondaryProgress(1);
        loading.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        loading.show();
    }

     /*
      *
      * Image Uploading
      *
      * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<String>();
                if (data.getData() != null) {

                    Uri mImageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(mImageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] b = baos.toByteArray();

                    //Send to server
                    String encImage = Base64.encodeToString(b, Base64.DEFAULT);

                    //System.out.println("Image uri"+encImage);

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    cursor.close();

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();
                            //System.out.println(Base64.encodeToString(mArrayUri, Base64.DEFAULT));

                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                        for (int i = 0; i < mArrayUri.size(); i++) {
                            System.out.println("Uri : " + mArrayUri.get(i));

                            Uri mImageUri = mArrayUri.get(i);
                            Bitmap bitmap;
                            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mImageUri));
                            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
                            icon = ConvertBitmapToString(resizedBitmap);
                            Upload();

                        }

                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
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
        UploadGalleryImagesServices uploadProfileImageService = new UploadGalleryImagesServices(id, icon) {
            @Override
            public void displayResult(JSONObject jsonObject) {
                //loading.dismiss();
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

            } else if (jsonObject.getInt("status") == constant.ERR_MEMORY_FULL) {
                Toast.makeText(getApplicationContext(), "YOUR MEMORY FULL", Toast.LENGTH_SHORT).show();

            } else if (jsonObject.getInt("status") == constant.SUCCESS) {

                if (count > 11) {
                    loading.dismiss();
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                }
                loading.incrementProgressBy(1);
                count++;


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
     * Set Actionbar with toolbar
     * */
    private void setActionBar() {
        //Set Actionbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //Enable HomeUp
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select Gallery Images");
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
     * Back button Animation
     *
     * */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
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