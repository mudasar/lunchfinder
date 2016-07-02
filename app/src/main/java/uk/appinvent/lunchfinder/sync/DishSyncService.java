package uk.appinvent.lunchfinder.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by mudasar on 24/06/16.
 */
public class DishSyncService extends Service {

    private static LunchFinderSyncAdapter mLunchfinderSyncFinder = null;
    private static final Object sSyncAdapterLock = new Object();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SunshineSyncService", "onCreate - SunshineSyncService");
        synchronized (sSyncAdapterLock) {
            if (mLunchfinderSyncFinder == null) {
                mLunchfinderSyncFinder = new LunchFinderSyncAdapter(getApplicationContext(), true);
            }
        }
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mLunchfinderSyncFinder.getSyncAdapterBinder();
    }
}
