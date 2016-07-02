package uk.appinvent.lunchfinder.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by mudasar on 24/06/16.
 */
public class AuthenticatorService extends Service {

    private LunchFinderAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        mAuthenticator = new LunchFinderAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
