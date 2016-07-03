package uk.appinvent.lunchfinder.notify;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by mudasar on 03/07/16.
 */
public class LunchFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = LunchFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);
    }

    /**
     * this method will be used to store the token on server
     * @param token
     * @throws IOException
     */
    private void registerToken(String token) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder().add("token", token).build();
        Request request = new Request.Builder().url("").build();

        client.newCall(request).execute();

    }
}
