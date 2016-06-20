package uk.appinvent.lunchfinder;

import com.activeandroid.ActiveAndroid;

/**
 * Created by mudasar on 20/06/16.
 */
public class LunchFinderApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
}
