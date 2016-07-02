package uk.appinvent.lunchfinder;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by mudasar on 20/06/16.
 */
public class LunchFinderApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)

        .build();
        ImageLoader.getInstance().init(config);
    }
}
