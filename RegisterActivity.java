package com.example.echonlabs.photographysrilanka;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.example.echonlabs.photographysrilanka.Constant.Constant;
import com.example.echonlabs.photographysrilanka.Service.RegisterServices;
import com.example.echonlabs.photographysrilanka.until.MyPreference;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private String session;
    private MyPreference myPreference;

    //EditText
    private EditText edName;
    private EditText edConactnumebr;
    private EditText edEmail;
    private EditText edAddress;
    private EditText edWebsite;
    private EditText edFacebookProfile;
    private EditText edInstagramProfile;
    private EditText edDescription;

    //ImageView
    private ImageView imageView;

    //TextView
    private TextView txtDec;
    private TextView txtDecMid;

    //String
    private String name;
    private String contactNumber;
    private String email;
    private String address;
    private String website;
    private String fbLink;
    private String insLink;
    private String description;
    private String id;

    //defining AwesomeValidation object
    private AwesomeValidation awesomeValidation;

    //Button
    private Button btnRegister;

    ProgressDialog loading = null;
    Constant constant = new Constant();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        if (checkInternetConenction()) {
            //Method Calling
            setActionBar();
            initial();
            checkSection();
            clickEvent();
        } else {
            Intent intent = new Intent(getApplicationContext(), OfflineActivity.class);
            startActivity(intent);
        }

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
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace_white_24dp);

    }

    /*
    *
    * Initial activity property
    *
    * */
    private void initial() {

        //Preference
        myPreference = MyPreference.getInstance(getApplicationContext());
        //Section
        session = myPreference.getData("Session");
        //Button
        btnRegister = (Button) findViewById(R.id.btnRegister);

        //Edit Text
        edName = (EditText) findViewById(R.id.txtName);
        edConactnumebr = (EditText) findViewById(R.id.txtConatct);
        edEmail = (EditText) findViewById(R.id.txtEmail);
        edAddress = (EditText) findViewById(R.id.txtAddress);
        edWebsite = (EditText) findViewById(R.id.txtWebsite);
        edFacebookProfile = (EditText) findViewById(R.id.txtFbLink);
        edInstagramProfile = (EditText) findViewById(R.id.txtInsLink);
        edDescription = (EditText) findViewById(R.id.txtFeture);

        //ImageView
        imageView = (ImageView) findViewById(R.id.imageView);

        //TextView
        txtDec = (TextView) findViewById(R.id.txtDesc);
        txtDecMid = (TextView) findViewById(R.id.txtDescMid);
    }

    /*
    *
    * Get EditText Values
    *
    * */
    private void getValues() {


        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        name = edName.getText().toString();
        contactNumber = edConactnumebr.getText().toString();
        email = edEmail.getText().toString();
        address = edAddress.getText().toString();
        website = edWebsite.getText().toString();
        fbLink = edFacebookProfile.getText().toString();
        insLink = edInstagramProfile.getText().toString();
        description = edDescription.getText().toString();
        //adding validation to edit  texts
        awesomeValidation.addValidation(this, R.id.txtName, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.txtEmail, Patterns.EMAIL_ADDRESS, R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.txtConatct, "^[2-9]{2}[0-9]{8}$", R.string.nameerror);
//      awesomeValidation.addValidation(this, R.id.editTextDob, "^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$", R.string.nameerror);
//      awesomeValidation.addValidation(this, R.id.editTextAge, Range.closed(13, 60), R.string.ageerror);

    }

    /*
    *
    * Set value in header
    * */
    private void setValue(String dec, String decMid) {
        txtDec.setText(dec);
        //txtDecMid.setText(decMid);
        imageView.setImageResource(R.drawable.reg);
    }

    /*
    *
    * Button Click event
    *
    *
    * */
    private void clickEvent() {

        btnRegister.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), UploadProfilePictureActivity.class);
//                startActivity(intent);
                                               if (checkInternetConenction()) {
                                                   getValues();
                                                   loader(v);
                                                   RegisterServices registerServices = new RegisterServices(name, email, contactNumber,
                                                           address, website, fbLink, insLink, description) {
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
                                                   registerServices.execute();
                                               } else {
                                                   Intent intent = new Intent(getApplicationContext(), OfflineActivity.class);
                                                   startActivity(intent);
                                               }
                                           }
                                       }
        );

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

                JSONObject data = jsonObject.getJSONObject("data");
                id = data.getString("id");
//              Toast.makeText(getApplicationContext(), "Id : " + id, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), UploadProfilePictureActivity.class);
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
    * Check Section and load toolbar name
    * Section ==0 ->"Join as a Lab"
    * Section ==1 ->"Join as a Photography"
    *
    * */
    private void checkSection() {

        if (session.equals("0")) {
            myPreference.saveData("Session", "");
            //Set Toolbar Label
            getSupportActionBar().setTitle("Register");

            setValue("Publish your shop selling Cameras and Accessories",
                    "Publish your camera that's for sale, rent or get it repaired");


        } else {
            //Set Toolbar Label
            getSupportActionBar().setTitle("Register");
            setValue("Join as a Photographer or Studio",
                    "Find more customers by registering your Color Lab with us");

        }
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
