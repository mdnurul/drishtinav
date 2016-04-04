package aitlindia.com.drishtinav;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    protected static final int RESULT_SPEECH = 1;
    private static final int PERMISSION_REQUEST_CODE = 1;

    private Context context;
    private Activity activity;

    private TextToSpeech engineTTS;

    private ImageButton btnSpeak;
    private Button locationButton, speakButton;
    private TextView txtText, locationTextV;
    private TextToSpeech tts;

    private LocationManager locManager;
    private LocationListener locListener;
    private Location mobileLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtText = (TextView) findViewById(R.id.txtText);
        locationTextV = (TextView)findViewById(R.id.lcoationtextView);

        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

        locationButton = (Button)findViewById(R.id.getLocationBtn);
        speakButton = (Button)findViewById(R.id.speechbutton);

        engineTTS = new TextToSpeech(this, this);




        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    txtText.setText("");
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Ops! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                buttonGetLocationClick();


            }
        });

        speakButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                speakOut();
            }

        });
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    txtText.setText(text.get(0));
                }
                break;
            }

        }
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;

        }
    }


    private void requestPermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)){

            Toast.makeText(context,"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Snackbar.make(view,"Permission Granted, Now you can access location data.",Snackbar.LENGTH_LONG).show();

                } else {

                    //Snackbar.make(view,"Permission Denied, You cannot access location data.",Snackbar.LENGTH_LONG).show();

                }
                break;
        }
    }



    /** Gets the current location and update the mobileLocation variable*/
    private void getCurrentLocation() {
        locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locListener = new LocationListener() {
            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onLocationChanged(Location location) {
                // TODO Auto-generated method stub
                mobileLocation = location;
            }
        };

        if (locManager != null) {

            //checkPermission
            if(android.os.Build.VERSION.SDK_INT <= 22){
                locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
            }else{
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //locManager.removeUpdates(GPSListener.this);
                    locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
                }
            }

        }

    }

    private void buttonGetLocationClick() {
        getCurrentLocation(); // gets the current location and update mobileLocation variable

        if (mobileLocation != null) {

            if (locManager != null) {
                if(android.os.Build.VERSION.SDK_INT <= 22){
                    locManager.removeUpdates(locListener);
                }else{
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locManager.removeUpdates(locListener);
                        //locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
                    }
                }

            }
             // This needs to stop getting the location data and save the battery power.

            String londitude = "Londitude: " + mobileLocation.getLongitude();
            String latitude = "Latitude: " + mobileLocation.getLatitude();
            String altitiude = "Altitiude: " + mobileLocation.getAltitude();
            String accuracy = "Accuracy: " + mobileLocation.getAccuracy();
            String time = "Time: " + mobileLocation.getTime();

            locationTextV.setText(londitude + "\n" + latitude + "\n"
                    + altitiude + "\n" + accuracy + "\n" + time);
        } else {
            locationTextV.setText("Sorry, location is not determined");
        }
    }


    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = engineTTS.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                btnSpeak.setEnabled(true);
                speakOut();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    private void speakOut() {

        String text = txtText.getText().toString();

        //engineTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        engineTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }


}
