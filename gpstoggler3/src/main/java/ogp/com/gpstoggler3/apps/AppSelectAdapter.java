package ogp.com.gpstoggler3.apps;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ogp.com.gpstoggler3.R;
import ogp.com.gpstoggler3.global.Constants;
import ogp.com.gpstoggler3.interfaces.AppAdapterInterface;
import ogp.com.gpstoggler3.resources.IconStorage;


public class AppSelectAdapter extends ArrayAdapter<AppStore> {
    private AppAdapterInterface appAdapterInterface;


    public AppSelectAdapter(Context context, AppAdapterInterface appAdapterInterface) {
        super(context, 0, new ArrayList<AppStore>());

        this.appAdapterInterface = appAdapterInterface;

        IconStorage.getInstance(context);
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final AppStore appStore = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.app_list, parent, false);
        }

        convertView.setTag(R.string.position, position);
        convertView.setTag(R.string.package_name, appStore.packageName);

        final TextView appName = convertView.findViewById(R.id.appName);
        final TextView appPackage = convertView.findViewById(R.id.appPackage);
        final ImageView appIcon = convertView.findViewById(R.id.appIcon);
        final ImageView appStateIcon = convertView.findViewById(R.id.appImage);
        final LinearLayout appIds = convertView.findViewById(R.id.appIds);

        appIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AppSelectAdapter.this.onLongClick((View)view.getParent());
                return true;
            }
        });

        appStateIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AppSelectAdapter.this.onLongClick((View)view.getParent());
                return true;
            }
        });

        appIds.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AppSelectAdapter.this.onLongClick((View)view.getParent());
                return true;
            }
        });

        assert appStore != null;
        appName.setText(appStore.friendlyName);
        appPackage.setText(appStore.packageName);
        setAppImageDrawable(appIcon, appStateIcon, appStore);
        return convertView;
    }


    public boolean updateCollection(ListAppStore appList) {
        Log.v(Constants.TAG, "AppAdapter::updateCollection. Entry...");

        boolean updateRequired = false;

        if (getCount() != appList.size()) {
            updateRequired = true;
            Log.i(Constants.TAG, "AppAdapter::updateCollection. Update required: different length of apps.");
        } else {
            for (int i = 0; i < getCount(); i++) {
                AppStore existedApp = getItem(i);
                AppStore newApp = appList.get(i);
                assert existedApp != null;
                if (!existedApp.packageName.equals(newApp.packageName)) {
                    updateRequired = true;
                    Log.i(Constants.TAG, "AppAdapter::updateCollection. Update required: different list of apps.");
                    break;
                }
            }
        }

        if (updateRequired) {
            clear();
            addAll(appList);
        } else {
            Log.i(Constants.TAG, "AppAdapter::updateCollection. Update not required: same list of apps.");
        }

        Log.v(Constants.TAG, "AppAdapter::updateCollection. Exit.");
        return updateRequired;
    }


    private void setAppImageDrawable(ImageView appIcon, ImageView appStateIcon, AppStore app) {
        appIcon.setImageDrawable(app.getAppIcon());
        appStateIcon.setImageResource(app.getInWidgets() > 0 ? R.drawable.exists : R.drawable.not_exists);
    }


    private void onLongClick(View view) {
        int position = (int)view.getTag(R.string.position);
        String packageName = (String)view.getTag(R.string.package_name);
        Log.v(Constants.TAG, String.format("AppAdapter::onLongClick. Entry [%d] for [%s]...", position, packageName));

        appAdapterInterface.onClickAppLookup(getItem(position), AppStore.AppState.STARTABLE);

        Log.v(Constants.TAG, "AppAdapter::onLongClick. Exit.");
    }
}
