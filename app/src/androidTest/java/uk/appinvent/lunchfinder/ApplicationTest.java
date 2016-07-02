package uk.appinvent.lunchfinder;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import org.testng.annotations.Test;

import uk.appinvent.lunchfinder.sync.LunchFinderSyncAdapter;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {

        super(Application.class);
    }

    @Test
    public void test_sync_adapter(){
        Log.d("Test sync adapter", "Test started");
        LunchFinderSyncAdapter adapter = new LunchFinderSyncAdapter(getContext(), true);
        adapter.onPerformSync(adapter.getSyncAccount(getContext()), null, getContext().getString(R.string.content_authority), null, null);

    }
}