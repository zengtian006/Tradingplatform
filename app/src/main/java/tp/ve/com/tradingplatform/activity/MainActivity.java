package tp.ve.com.tradingplatform.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import tp.ve.com.tradingplatform.R;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    public final static int HIGHTLIGHT_COLOR = R.color.white1;
    public final static int SUB_MENU_WIDTH = 120;
    public final static int SUB_MENU_HEIGHT = 120;
    public final static int SUB_MENU_FONT_SIZE = 8;


    /*TEXT + res id*/
    public final static HashMap BSS_HORIZONTAL_SCROLL_MENU = new LinkedHashMap();

    static {
        BSS_HORIZONTAL_SCROLL_MENU.put(R.string.bssmenu_buy, null);
        BSS_HORIZONTAL_SCROLL_MENU.put(R.string.bssmenu_share, null);
        BSS_HORIZONTAL_SCROLL_MENU.put(R.string.bssmenu_sell, null);
    }

    public final static HashMap<Integer, Integer> BUY_HORIZONTAL_SCROLL_MENU = new LinkedHashMap<Integer, Integer>();

    static {
        BUY_HORIZONTAL_SCROLL_MENU.put(R.string.buymenu_display, R.drawable.ic_style_black);
        BUY_HORIZONTAL_SCROLL_MENU.put(R.string.buymenu_groupbuy, R.drawable.ic_people_black);
        BUY_HORIZONTAL_SCROLL_MENU.put(R.string.buymenu_bulk, R.drawable.ic_shopping_basket_black);
        BUY_HORIZONTAL_SCROLL_MENU.put(R.string.buymenu_watch_list, R.drawable.ic_visibility_black);
    }

    public final static HashMap<Integer, Integer> SELL_HORIZONTAL_SCROLL_MENU = new LinkedHashMap<Integer, Integer>();

    static {
        SELL_HORIZONTAL_SCROLL_MENU.put(R.string.sellmenu_selling, R.drawable.ic_storage_black);
        SELL_HORIZONTAL_SCROLL_MENU.put(R.string.sellmenu_scheduled, R.drawable.ic_alarm_on_black);
        SELL_HORIZONTAL_SCROLL_MENU.put(R.string.sellmenu_add, R.drawable.ic_camera_enhance_black);
        SELL_HORIZONTAL_SCROLL_MENU.put(R.string.sellmenu_bulk, R.drawable.ic_shopping_basket_black);
    }

    public final static HashMap<Integer, Integer> SHARE_HORIZONTAL_SCROLL_MENU = new LinkedHashMap<Integer, Integer>();

    static {
        SHARE_HORIZONTAL_SCROLL_MENU.put(R.string.sharemenu_share_item, R.drawable.ic_share_black);
        SHARE_HORIZONTAL_SCROLL_MENU.put(R.string.sharemenu_customize, R.drawable.ic_public_black);
        SHARE_HORIZONTAL_SCROLL_MENU.put(R.string.sharemenu_history, R.drawable.ic_storage_black);
        SHARE_HORIZONTAL_SCROLL_MENU.put(R.string.sharemenu_statistics, R.drawable.ic_event_note_black);
    }

    private LinearLayout scroll_linear_bss_menu;
    private LinearLayout scroll_linear_sub_menu;

    SharedPreferences appPreferences;
    boolean isAppInstalled = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isAppInstalled = appPreferences.getBoolean("isAppInstalled", false);
        if (isAppInstalled == false) {
            addShortcut();
            /**
             * Make preference true
             */
            SharedPreferences.Editor editor = appPreferences.edit();
            editor.putBoolean("isAppInstalled", true);
            editor.commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().hide();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        /*BUY SHARE SELL Menu*/
       /* WheelMenu wheelMenu = (WheelMenu) findViewById(R.id.wheelMenu);
        wheelMenu.setDivCount(3);
        wheelMenu.setWheelImage(R.drawable.wheel_menu_bg);

        //final TextView selectedPositionText = (TextView) findViewById(R.id.selected_position_text);
        //selectedPositionText.setText("selected: " + (wheelMenu.getSelectedPosition() + 1));

        wheelMenu.setWheelChangeListener(new WheelMenu.WheelChangeListener() {
            @Override
            public void onSelectionChange(int selectedPosition) {
                removeAllChildMenuButton(scroll_linear_sub_menu);
                if (selectedPosition == 0) {//BUY
                    createMenuButtonsFromMap(BUY_HORIZONTAL_SCROLL_MENU, scroll_linear_sub_menu);
                    //add action go to home page of buy menu (first item in buy menu)
                } else if (selectedPosition == 1) {//SELL
                    createMenuButtonsFromMap(SELL_HORIZONTAL_SCROLL_MENU, scroll_linear_sub_menu);
                    //add action go to home page of sell menu (first item in buy menu)
                } else if (selectedPosition == 2) {//SHARE
                    createMenuButtonsFromMap(SHARE_HORIZONTAL_SCROLL_MENU, scroll_linear_sub_menu);
                    //add action go to home page of share menu (first item in buy menu)
                }
                //selectedPositionText.setText("selected: " + (selectedPosition + 1));

            }
        });*/



        /*Right bottom corner menu*/
        /*FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(createCustomFloatingMainActionView(this, android.R.drawable.ic_menu_manage, Color.BLUE))
                //.setBackgroundDrawable(R.drawable.custom_buttom)
                .build();

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(createCustomFloatingSubActionButton(itemBuilder, this, android.R.drawable.ic_menu_view, Color.BLUE), 100, 100)
                .addSubActionView(createCustomFloatingSubActionButton(itemBuilder, this, android.R.drawable.ic_menu_info_details, Color.BLUE), 100, 100)
                .addSubActionView(createCustomFloatingSubActionButton(itemBuilder, this, android.R.drawable.ic_menu_help, Color.BLUE), 100, 100)
                .attachTo(actionButton)
                .build();*/


        /*Scroll horizontal main menu for BUY / SELL / SHARE*/
        scroll_linear_bss_menu = (LinearLayout) this.findViewById(R.id.linear_BSS_menu);
        createMenuButtonsFromMap(BSS_HORIZONTAL_SCROLL_MENU, scroll_linear_bss_menu, 20, 100, 100);


        /*Scroll horizontal sub menu for BUY / SELL / SHARE*/
        scroll_linear_sub_menu = (LinearLayout) this.findViewById(R.id.linear_BSS_sub_menu);
        createMenuButtonsFromMap(BUY_HORIZONTAL_SCROLL_MENU, scroll_linear_sub_menu, SUB_MENU_FONT_SIZE, SUB_MENU_WIDTH, SUB_MENU_HEIGHT);
        /*Main body of app*/
        WebView webView = (WebView) this.findViewById(R.id.web_view);
        webView.getSettings().setDomStorageEnabled(true);
        File dir = getCacheDir();

        if (!dir.exists()) {
            dir.mkdirs();
        }

        webView.getSettings().setAppCachePath(dir.getPath());

        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl("http://" + getString(R.string.app_url));
        //webView.clearCache(true);
       /* if (Build.VERSION.SDK_INT >= 19) {
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }*/


    }


    private ImageView createCustomFloatingMainActionView(Context context, int drawable_id, int color) {
        ImageView imageView = new ImageView(context); // Create an icon
        Drawable drawable = ContextCompat.getDrawable(context, drawable_id);
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }

        imageView.setImageDrawable(drawable);
        return imageView;
    }

    private SubActionButton createCustomFloatingSubActionButton(SubActionButton.Builder itemBuilder, Context context, int drawable_id, int color) {
        ImageView itemIcon = new ImageView(context);
        Drawable drawable = ContextCompat.getDrawable(this, drawable_id);
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
        itemIcon.setImageDrawable(drawable);
        SubActionButton button = itemBuilder.setContentView(itemIcon).build();
        return button;
    }

    private void createMenuButtonsFromMap(HashMap<Integer, Integer> from_map, LinearLayout linear, int font_size, int width, int height) {
        for (Map.Entry<Integer, Integer> entry : from_map.entrySet()) {
            addChildMenuButton(linear, entry.getKey(), entry.getValue(), font_size, width, height);
        }
    }

    private void addChildMenuButton(LinearLayout linear, int button_text_id, Integer drawable_id, int font_size, int width, int height) {
        LinearLayout.LayoutParams linearLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout myLinear = new LinearLayout(this);
        linearLp.setMargins(40, 1, 40, 1);
        myLinear.setOrientation(LinearLayout.VERTICAL);
        myLinear.setTag(getString(button_text_id));
        linear.addView(myLinear, linearLp);

        /*LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);*/

        if (drawable_id != null) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(drawable_id);
            myLinear.addView(imageView, lp);
        }


        LinearLayout.LayoutParams textViewLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(this);
        textView.setText(getString(button_text_id));
        textView.setTextSize(font_size);
        textView.setGravity(Gravity.CENTER);


        //only for main BSS menu
        if (getString(button_text_id) == getString(R.string.bssmenu_buy) || getString(button_text_id) == getString(R.string.bssmenu_sell) || getString(button_text_id) == getString(R.string.bssmenu_share)) {
            if (getString(button_text_id) == getString(R.string.bssmenu_buy)) {
                textView.setBackgroundResource(HIGHTLIGHT_COLOR);
                textView.setTextColor(Color.BLACK);
            } else {
                textView.setTextColor(Color.WHITE);
            }
            textView.setTypeface(null, Typeface.BOLD);
            /*Shader shader = new LinearGradient(0, 0, 0, textView.getTextSize(), Color.WHITE, Color.LTGRAY,
                    Shader.TileMode.CLAMP);
            textView.getPaint().setShader(shader);*/
        }


        myLinear.addView(textView, textViewLp);

        myLinear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //Log.d("Mylog", "clcik "+v.getTag().toString());
                if (v.getTag().toString().equals(getString(R.string.bssmenu_buy))) {
                    removeAllChildMenuButtonColor(scroll_linear_bss_menu);
                    textView.setBackgroundResource(HIGHTLIGHT_COLOR);
                    textView.setTextColor(Color.BLACK);
                    removeAllChildMenuButton(scroll_linear_sub_menu);
                    createMenuButtonsFromMap(BUY_HORIZONTAL_SCROLL_MENU, scroll_linear_sub_menu, SUB_MENU_FONT_SIZE, SUB_MENU_WIDTH, SUB_MENU_HEIGHT);
                    //add action go to home page of buy menu (first item in buy menu)
                } else if (v.getTag().toString().equals(getString(R.string.bssmenu_share))) {
                    removeAllChildMenuButtonColor(scroll_linear_bss_menu);
                    textView.setBackgroundResource(HIGHTLIGHT_COLOR);
                    textView.setTextColor(Color.BLACK);
                    removeAllChildMenuButton(scroll_linear_sub_menu);

                    createMenuButtonsFromMap(SHARE_HORIZONTAL_SCROLL_MENU, scroll_linear_sub_menu, SUB_MENU_FONT_SIZE, SUB_MENU_WIDTH, SUB_MENU_HEIGHT);
                } else if (v.getTag().toString().equals(getString(R.string.bssmenu_sell))) {
                    removeAllChildMenuButtonColor(scroll_linear_bss_menu);
                    textView.setBackgroundResource(HIGHTLIGHT_COLOR);
                    textView.setTextColor(Color.BLACK);
                    removeAllChildMenuButton(scroll_linear_sub_menu);
                    createMenuButtonsFromMap(SELL_HORIZONTAL_SCROLL_MENU, scroll_linear_sub_menu, SUB_MENU_FONT_SIZE, SUB_MENU_WIDTH, SUB_MENU_HEIGHT);

                } else if (v.getTag().toString().equals(getString(R.string.buymenu_groupbuy))) {
                    //action..........
                } else {
                }

            }
        });
    }

    private void removeAllChildMenuButton(LinearLayout linear) {
        if (linear.getChildCount() > 0)
            linear.removeAllViews();
    }

    /*
    structure : linear -> 3views -> textView in each
     */
    private void removeAllChildMenuButtonColor(LinearLayout linear) {
        if (linear.getChildCount() > 0) {
            int childcount = linear.getChildCount();
            for (int i = 0; i < childcount; i++) {
                View v = linear.getChildAt(i);//3
                for (int j = 0; j < ((ViewGroup) v).getChildCount(); j++) {
                    View nextChild = ((ViewGroup) v).getChildAt(j);
                    if (nextChild instanceof TextView) {
                        nextChild.setBackgroundColor(Color.TRANSPARENT);
                        ((TextView) nextChild).setTextColor(Color.WHITE);
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        switch (id) {
            case R.id.sign_up:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SignupActivity.class);
                startActivity(intent);
                // 设置切换动画，从右边进入，左边退出
//                activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.login:
                break;
            default:
                item.setChecked(true);
                break;
        }

       /* if (id == R.id.nav_address) {
            // Handle the address action
        } else if (id == R.id.nav_message) {

        }
*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void addShortcut() {
        //Adding shortcut for MainActivity
        //on Home screen
        Intent shortcutIntent = new Intent(getApplicationContext(),
                MainActivity.class);

        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent
                .putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, R.string.app_name);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                        R.mipmap.ic_launcher));

        addIntent
                .setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(addIntent);
    }
}