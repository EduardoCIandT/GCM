package com.example.rebelo.gcm_example;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.rebelo.gcm_example.backend.registration.Registration;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

/**
 * Created by rebelo on 13/05/2016.
 */
public class GCMRegistrationIntentService extends IntentService {

    public static final String REGISTRATION_SUCCESS = "RegistrationSuccess";
    public static final String REGISTRATION_ERROR = "RegistrationError";

    public GCMRegistrationIntentService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        registerGCM();
    }

    private void registerGCM() {
        Intent registrationComplete = null;
        String token = null;
        try {
            InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.w("GCMRegIntentService", "token:" + token);
            //notify to UI that registration complete success
            registrationComplete = new Intent(REGISTRATION_SUCCESS);
            registrationComplete.putExtra("token", token);

            sendRegistrationToServer(token);
        } catch (Exception e) {
            Log.w("GCMRegIntentService", "Registration error");
            registrationComplete = new Intent(REGISTRATION_ERROR);
        }
        //Send broadcast
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(String token) throws IOException {
        /*Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null)
                // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
                // otherwise they can be skipped
                .setRootUrl("http://gcm-example-c5203.appspot.com/_ah/api/registration/v1/register")
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                            throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                });*/
        Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null)
                .setRootUrl("https://gcm-example-c5203.appspot.com/_ah/api/");
        Registration regService = builder.build();
        regService.reigster(token).execute();
        Log.w("**** MainActivity", "REGISTER LOG IN BACKEND");
    }
}
