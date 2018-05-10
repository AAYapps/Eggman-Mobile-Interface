package e.thejo.eggmanosmobileinterface;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class DesktopActivity extends Activity {

    private GridLayout grid;
    private int screencheck = 0;

    public void startBtnPress(View view)
    {
        Intent i = new Intent(this, EggmanAppsListActivity.class);
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent data)
    {
        String tit = data.getStringExtra("appTitle");
        String pac = data.getStringExtra("appPackage");
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
        Drawable imgtemp;
        for (int k = 0; k < availableActivities.size(); k++)
        {
            if (tit.contains(availableActivities.get(k).loadLabel(manager)) && pac.contains(availableActivities.get(k).activityInfo.packageName))
            {
                imgtemp = availableActivities.get(k).activityInfo.loadIcon(manager);
                favApps.addListItems(tit, pac, imgtemp);
                SetGrid();
            }
        }
    }

    public class listItems
    {
        private final ArrayList<String> Title = new ArrayList<String>();
        private final ArrayList<String> packages = new ArrayList<String>();
        private final ArrayList<Drawable> icon = new ArrayList<Drawable>();

        public void addListItems(String title, String link, Drawable appIcon)
        {
            Title.add(title);
            packages.add(link);
            icon.add(appIcon);
        }

        public void remove(int index)
        {
            Title.remove(index);
            packages.remove(index);
            icon.remove(index);
        }

        public int getSyncedIndex()
        {
            if (Title.size() == packages.size() && Title.size() == icon.size())
                return Title.size();
            else
                return -1;
        }

        public ArrayList<String> toArrayString()
        {
            return Title;
        }

        public ArrayList<String> toArrayLabel()
        {
            return packages;
        }

        public ArrayList<Drawable> toArrayIcon()
        {
            return icon;
        }
    }

    int width;
    int height;
    int col2;
    private void SetGrid()
    {
        handler.postDelayed(update, 50);
        grid.removeAllViews();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        col2 = (width / (width / 375));
        int col = width / 375;

        screencheck = favApps.getSyncedIndex();

        if (screencheck > 0)
        {
            grid.setColumnCount(col);
            favAppIndex = 0;
            limit = screencheck;
            favAppUpdate = true;
        }
        else if (screencheck == -1)
        {
            Toast.makeText(this, "Favorites encountered a listing mismatch.", Toast.LENGTH_LONG).show();
        }
    }

    private Handler handler;
    private Update update;
    private PackageManager manager;
    SharedPreferences p;
    listItems favApps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desktop);
        grid = findViewById(R.id.icongrid);
        handler = new Handler();
        update = new Update();
        favApps = new listItems();
        manager = getPackageManager();
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
        p = getPreferences(Context.MODE_PRIVATE);
        Map<String, String> map = (Map<String, String>) p.getAll();

        if (!map.isEmpty())
        {
            Collection<String> s = map.keySet();
            String[] str = s.toArray(new String[0]);
            for (int j = 0; j < map.size(); j++)
            {
                Drawable imgtemp = getResources().getDrawable(R.drawable.eggman_icon);
                for (int k = 0; k < availableActivities.size(); k++)
                {
                    if (str[j].contains(availableActivities.get(k).loadLabel(manager)) && map.get(str[j]).contains(availableActivities.get(k).activityInfo.packageName))
                    {
                        imgtemp = availableActivities.get(k).activityInfo.loadIcon(manager);
                    }
                }
                favApps.addListItems(str[j], map.get(str[j]), imgtemp);
            }
        }
        else
        {
            Drawable imgtemp = getResources().getDrawable(R.drawable.eggman_icon);
            for (int k = 0; k < availableActivities.size(); k++)
            {
                if ("Phone".contains(availableActivities.get(k).loadLabel(manager)) && (availableActivities.get(k).activityInfo.packageName).contains(".dialer"))
                {
                    imgtemp = availableActivities.get(k).activityInfo.loadIcon(manager);
                    favApps.addListItems("Phone", availableActivities.get(k).activityInfo.packageName, imgtemp);
                }
                else
                {
                    //favApps.addListItems(availableActivities.get(k).activityInfo.packageName, "Error", imgtemp);
                }
            }

        }

        SetGrid();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        SharedPreferences.Editor e = p.edit();
        e.clear();
        for (int i = 0; i < favApps.toArrayString().size(); i++)
        {
            e.putString(favApps.toArrayString().get(i), favApps.toArrayLabel().get(i));
        }
        e.apply();
    }

    @Override
    protected void onSaveInstanceState(Bundle saveInstanceState)
    {
        super.onSaveInstanceState(saveInstanceState);
    }

    boolean favAppUpdate = false;
    private int favAppIndex = 0;
    private int limit = 0;
    private class Update implements Runnable
    {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run()
        {
            if (favAppUpdate)
            {
                if (favAppIndex < limit)
                {
                    RelativeLayout griditem = (RelativeLayout) getLayoutInflater().inflate(R.layout.file_icon, grid, false);
                    Drawable image = getResources().getDrawable(R.drawable.eggman_icon);
                    ImageView itemImage = griditem.findViewById(R.id.image_icon);
                    itemImage.getLayoutParams().width = col2;
                    itemImage.getLayoutParams().height = col2;
                    itemImage.setImageDrawable(image);
                    TextView itemText = griditem.findViewById(R.id.icon_text);

                    try
                    {
                        itemImage.setImageDrawable(favApps.toArrayIcon().get(favAppIndex));
                    }
                    catch (Exception e)
                    {

                    }
                    try
                    {
                        itemText.setText(favApps.toArrayString().get(favAppIndex));
                    }
                    catch (Exception e)
                    {
                        itemText.setText(e.getMessage());
                    }
                    grid.addView(griditem);
                    favAppIndex++;
                    handler.postDelayed(update, 50);
                }
                else
                {
                    int children = grid.getChildCount();

                    for (int i = 0; i < children; i++)
                    {
                        RelativeLayout ltemp = (RelativeLayout) grid.getChildAt(i);
                        final TextView temp = ltemp.findViewById(R.id.icon_text);
                        final ImageView imgtemp = ltemp.findViewById(R.id.image_icon);
                        final int index = i;

                        temp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try
                                {
                                    Intent in = manager.getLaunchIntentForPackage(favApps.toArrayLabel().get(index));
                                    startActivity(in);
                                }
                                catch (Exception e)
                                {

                                }

                                //Toast.makeText(DesktopActivity.this, "Name: " + temp.getText().toString(), Toast.LENGTH_LONG).show();
                            }
                        });

                        temp.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                AlertDialog checkRemoveChoice = new AlertDialog.Builder(new ContextThemeWrapper(DesktopActivity.this, R.style.myDialog)).create();
                                checkRemoveChoice.setMessage("Are you sure you want to delete this Favorite?");
                                checkRemoveChoice.setTitle("Delete Favorite");
                                checkRemoveChoice.setButton(AlertDialog.BUTTON_POSITIVE, "Delete " + favApps.toArrayString().get(index), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int j) {
                                        favApps.remove(index);
                                        SetGrid();
                                    }
                                });
                                checkRemoveChoice.setButton(AlertDialog.BUTTON_NEGATIVE, "Never Mind", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int j) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                checkRemoveChoice.show();

                                return true;
                            }
                        });

                        imgtemp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try
                                {
                                    Intent in = manager.getLaunchIntentForPackage(favApps.toArrayLabel().get(index));
                                    startActivity(in);
                                }
                                catch (Exception e)
                                {

                                }
                                //Toast.makeText(DesktopActivity.this, "Name: " + temp.getText().toString(), Toast.LENGTH_LONG).show();
                            }
                        });

                        imgtemp.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                AlertDialog checkRemoveChoice = new AlertDialog.Builder(new ContextThemeWrapper(DesktopActivity.this, R.style.myDialog)).create();
                                checkRemoveChoice.setMessage("Are you sure you want to delete this Favorite?");
                                checkRemoveChoice.setTitle("Delete Favorite");
                                checkRemoveChoice.setButton(AlertDialog.BUTTON_POSITIVE, "Delete " + favApps.toArrayString().get(index), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int j) {
                                        favApps.remove(index);
                                        SetGrid();
                                    }
                                });
                                checkRemoveChoice.setButton(AlertDialog.BUTTON_NEGATIVE, "Never Mind", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int j) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                checkRemoveChoice.show();

                                return true;
                            }
                        });
                    }
                    favAppUpdate = false;
                }
            }
        }
    }
}
