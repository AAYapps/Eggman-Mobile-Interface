package e.thejo.eggmanosmobileinterface;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class EggmanAppsListActivity extends AppCompatActivity {

    class appManager
    {
        private PackageManager manager;
        private List<AppDetail> apps;
        Intent intent;


        public appManager()
        {
            handler = new Handler();
            update = new Update();
        }

        public class AppDetail {
            String label;
            String name;
            Drawable icon;
        }

        private void loadApps(){
            manager = getPackageManager();
            apps = new ArrayList<AppDetail>();

            intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            limit = manager.queryIntentActivities(intent, 0).size();
            rl = manager.queryIntentActivities(intent, 0);
            handler.postDelayed(update, 1);
        }

        private Handler handler;
        private Update update;
        private int limit = 0;
        private int appIndex = 0;

        List<ResolveInfo> rl;
        private class Update implements Runnable
        {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run()
            {
                if (appIndex < limit)
                {
                    ResolveInfo ri = rl.get(appIndex);

                    AppDetail app = new AppDetail();
                    app.label = (String) ri.loadLabel(manager);
                    app.name = ri.activityInfo.packageName;
                    app.icon = ri.activityInfo.loadIcon(manager);
                    apps.add(app);
                    appIndex++;
                    handler.postDelayed(update, 1);
                }
                else
                {
                    appList.setAdapter(adapter);
                    load.setVisibility(View.GONE);
                }
            }
        }
    }

    private final appManager apps = new appManager();
    private ProgressBar load;
    ListView appList;
    ArrayAdapter<appManager.AppDetail> adapter;
    int appIndex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eggman_apps_list);
        appList = findViewById(R.id.appList);
        apps.loadApps();
        load = findViewById(R.id.load);

        adapter  = new ArrayAdapter<appManager.AppDetail>(this, R.layout.list_view_item, apps.apps) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.list_view_item, null);
                }

                ImageView appIcon = convertView.findViewById(R.id.appIcon);
                appIcon.setImageDrawable(apps.apps.get(position).icon);

                TextView appLabel = convertView.findViewById(R.id.applbl);
                appLabel.setText(apps.apps.get(position).label);

                TextView appName = convertView.findViewById(R.id.appName);
                appName.setText(apps.apps.get(position).name);

                return convertView;
            }
        };



        appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent in = apps.manager.getLaunchIntentForPackage(apps.apps.get(i).name.toString());
                startActivity(in);
            }
        });

        appList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                appIndex = i;

                AlertDialog checkAddChoice = new AlertDialog.Builder(EggmanAppsListActivity.this).create();
                checkAddChoice.setMessage("Add to Favorites?");
                checkAddChoice.setTitle("Add Favorite");
                checkAddChoice.setButton(AlertDialog.BUTTON_POSITIVE, "Add " + apps.apps.get(i).label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        Intent data = new Intent();
                        data.putExtra("appTitle", apps.apps.get(appIndex).label);
                        data.putExtra("appPackage", apps.apps.get(appIndex).name);
                        setResult(RESULT_OK, data);
                        finish();
                    }
                });
                checkAddChoice.setButton(AlertDialog.BUTTON_NEGATIVE, "Never Mind", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        dialogInterface.dismiss();
                    }
                });
                checkAddChoice.show();

                return true;
            }
        });
    }

    public void backToHome(View view) {
        Intent data = new Intent();
        data.putExtra("appTitle", "Not selected");
        data.putExtra("appPackage", "Not selected");
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        Intent data = new Intent();
        data.putExtra("appTitle", "Not selected");
        data.putExtra("appPackage", "Not selected");
        setResult(RESULT_OK, data);
        finish();
    }
}
