package ogp.com.gpstoggler3.widgets;


import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;

import ogp.com.gpstoggler3.ITogglerService;
import ogp.com.gpstoggler3.servlets.ExecuteOnServiceWithTimeout;
import ogp.com.gpstoggler3.R;
import ogp.com.gpstoggler3.apps.AppStore;
import ogp.com.gpstoggler3.apps.ListWatched;
import ogp.com.gpstoggler3.global.Constants;
import ogp.com.gpstoggler3.servlets.ExecuteWithTimeout;


public class AppListProvider implements RemoteViewsFactory {
    private static final String RPC_METHOD = "listActivatedApps";
    private static final int RPC_TIMEOUT = 5000;            // 5 seconds

    private static ArrayList<String> listItemList = new ArrayList<>();
    private Context context = null;
    private ExecuteOnServiceWithTimeout executor;


    public AppListProvider(Context context) {
        this.context = context;
    }


    @Override
    public void onCreate() {
        Log.v(Constants.TAG, "AppListProvider::onCreate. Invoked.");

        executor = new ExecuteOnServiceWithTimeout(context);
    }


    @Override
    public void onDataSetChanged() {
        Log.v(Constants.TAG, "AppListProvider::onDataSetChanged. Entry...");

        listItemList.clear();

        ListWatched list = null;
        boolean success = false;

        try {
            ExecuteOnServiceWithTimeout.ExecuteOnService executeMethod = new ExecuteOnServiceWithTimeout.ExecuteOnService(ITogglerService.class.getMethod(RPC_METHOD));

            list = (ListWatched)executor.execute(executeMethod, RPC_TIMEOUT);
            success = true;
        } catch (Exception e) {
            Log.e(Constants.TAG, "AppListProvider::onDataSetChanged. Error with 'ExecuteOnServiceWithTimeout::execute' returning not expected type [1].");
        }

        if (success) {
            for (int i = 0; null != list && i < list.size(); i++) {
                try {
                    AppStore app = list.get(i);
                    listItemList.add(app.friendlyName);
                } catch (Exception e) {
                    Log.e(Constants.TAG, "AppListProvider::onDataSetChanged. Error with 'ExecuteOnServiceWithTimeout::execute' returning not expected type [2].");
                    success = false;
                }
            }
        }

        if (success) {
            if (0 == listItemList.size()) {
                String noActiveApps = context.getResources().getString(R.string.no_active_apps_found);
                listItemList.add(noActiveApps);

                Log.w(Constants.TAG, "AppListProvider::onDataSetChanged. No apps loaded.");
            } else {
                Log.w(Constants.TAG, String.format("AppListProvider::onDataSetChanged. Loaded %d app(s).", listItemList.size()));
            }
        }

        Log.v(Constants.TAG, "AppListProvider::onDataSetChanged. Exit.");
    }


    @Override
    public void onDestroy() {
        Log.v(Constants.TAG, "AppListProvider::onDestroy. Invoked.");

        executor.kill();
    }


    @Override
    public int getCount() {
        return listItemList.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public boolean hasStableIds() {
        return false;
    }


    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.list_row);
        String listItem = listItemList.get(position);
        remoteView.setTextViewText(R.id.appName, listItem);
        return remoteView;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }


    @Override
    public int getViewTypeCount() {
        return 1;
    }
}
