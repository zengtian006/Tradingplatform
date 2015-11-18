package tp.ve.com.tradingplatform.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
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
import tp.ve.com.tradingplatform.entity.Member;
import tp.ve.com.tradingplatform.helper.SessionManager;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private final static int SCANNED_GREENEST_CODE = 1;

    public final static int HIGHTLIGHT_COLOR = R.color.white1;
    public final static int SUB_MENU_WIDTH = 120;
    public final static int SUB_MENU_HEIGHT = 120;
    public final static int SUB_MENU_FONT_SIZE = 8;

    boolean doubleBackToExitPressedOnce = false;


    /*TEXT + res id*/
    public final static HashMap BSS_HORIZONTAL_SCROLL_MENU = new LinkedHashMap();

    static {
        BSS_HORIZONTAL_SCROLL_MENU.put(R.string.bssmenu_buy, null);
//        BSS_HORIZONTAL_SCROLL_MENU.put(R.string.bssmenu_share, null);
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

    private LinearLayout scroll_linear_bss_menu;
    private LinearLayout scroll_linear_sub_menu;
    public static DrawerLayout drawer;
    static NavigationView navigationView;

    private static SessionManager session;

    private static LinearLayout layout_loggedin, layout_noLoggedin;
    Button btn_signup, btn_login, btn_account, btn_logout;
    View headerView;
    static TextView icontext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().hide();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);


        navigationView.setNavigationItemSelectedListener(this);

        session = new SessionManager(getApplicationContext());

        headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        icontext = (TextView) headerView.findViewById(R.id.nav_user_nickname);
//        View header = LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);
//        navigationView.addHeaderView(header);


        layout_noLoggedin = (LinearLayout) headerView.findViewById(R.id.layout_noLoggedin);
        layout_loggedin = (LinearLayout) headerView.findViewById(R.id.layout_Loggedin);

        btn_signup = (Button) headerView.findViewById(R.id.nav_sign_up);
        btn_login = (Button) headerView.findViewById(R.id.nav_login);
        btn_logout = (Button) headerView.findViewById(R.id.nav_logout);
        btn_account = (Button) headerView.findViewById(R.id.nav_account);
        btn_signup.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        btn_account.setOnClickListener(this);

        if (!session.isLoggedIn()) {
            logoutUser();
            showSignupMenu();
            icontext.setText("GUEST");
//            Toast.makeText(MainActivity.this, "no logged in", Toast.LENGTH_SHORT).show();
        } else {
            session.verifyToken();
        }


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
        scroll_linear_bss_menu.setVisibility(LinearLayout.GONE);


        /*Scroll horizontal sub menu for BUY / SELL / SHARE*/
        scroll_linear_sub_menu = (LinearLayout) this.findViewById(R.id.linear_BSS_sub_menu);
        createMenuButtonsFromMap(BUY_HORIZONTAL_SCROLL_MENU, scroll_linear_sub_menu, SUB_MENU_FONT_SIZE, SUB_MENU_WIDTH, SUB_MENU_HEIGHT);
        scroll_linear_sub_menu.setVisibility(LinearLayout.GONE);
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

    public static void showSignupMenu() {
        MenuItem deal = navigationView.getMenu().findItem(R.id.nav_deal);
        deal.setVisible(false);
        MenuItem opportunity = navigationView.getMenu().findItem(R.id.nav_opportunity);
        opportunity.setVisible(false);
        MenuItem me = navigationView.getMenu().findItem(R.id.nav_me);
        me.setVisible(false);
        layout_loggedin.setVisibility(LinearLayout.GONE);
        layout_noLoggedin.setVisibility(LinearLayout.VISIBLE);
    }

    public static void showLogoutMenu() {
        MenuItem deal = navigationView.getMenu().findItem(R.id.nav_deal);
        deal.setVisible(true);
        MenuItem opportunity = navigationView.getMenu().findItem(R.id.nav_opportunity);
        opportunity.setVisible(true);
        MenuItem me = navigationView.getMenu().findItem(R.id.nav_me);
        me.setVisible(true);
        layout_loggedin.setVisibility(LinearLayout.VISIBLE);
        layout_noLoggedin.setVisibility(LinearLayout.GONE);

        icontext.setText(session.currMember.getMember_name());

    }


    public static void logoutUser() {
        session.setLogin(false, "", session.getUserName(), "");

        // Launching the login activity
//        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//        startActivity(intent);
//        finish();
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
//        if (getString(button_text_id) == getString(R.string.bssmenu_buy) || getString(button_text_id) == getString(R.string.bssmenu_sell) || getString(button_text_id) == getString(R.string.bssmenu_share)) {
        if (getString(button_text_id) == getString(R.string.bssmenu_buy) || getString(button_text_id) == getString(R.string.bssmenu_sell)) {
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
//                } else if (v.getTag().toString().equals(getString(R.string.bssmenu_share))) {
//                    removeAllChildMenuButtonColor(scroll_linear_bss_menu);
//                    textView.setBackgroundResource(HIGHTLIGHT_COLOR);
//                    textView.setTextColor(Color.BLACK);
//                    removeAllChildMenuButton(scroll_linear_sub_menu);
//
//                    createMenuButtonsFromMap(SHARE_HORIZONTAL_SCROLL_MENU, scroll_linear_sub_menu, SUB_MENU_FONT_SIZE, SUB_MENU_WIDTH, SUB_MENU_HEIGHT);
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
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
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
        Intent intent = new Intent();
        switch (id) {
            case R.id.ic_menu_qrcode:

                intent.setClass(MainActivity.this, MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNED_GREENEST_CODE);
                break;
            case R.id.ic_menu_share:
//                intent.setAction(Intent.ACTION_SEND);
//                intent.setType("image/*");
//                intent.putExtra(Intent.EXTRA_TEXT, "www.baidu.com");
//                intent.putExtra(Intent.EXTRA_STREAM, "http://www.joomlaworks.net/images/demos/galleries/abstract/7.jpg");
//                startActivity(Intent.createChooser(intent, "Share"));
                intent.setClass(MainActivity.this, ShareActivity.class);
                startActivity(intent);
                break;
        }

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
        setTitle(item.getTitle());
        switch (id) {
//            case R.id.nav_sign_up:
//                Intent intent_sign = new Intent();
//                intent_sign.setClass(MainActivity.this, SignupActivity.class);
//                startActivity(intent_sign);
//                break;
//            case R.id.nav_login:
//                Intent intent_login = new Intent();
//                intent_login.setClass(MainActivity.this, LoginActivity.class);
//                startActivity(intent_login);
////                finish();
//                break;
//            case R.id.nav_logout:
//                logoutUser();
//                showSignupMenu();
//                Snackbar snackbar = Snackbar
//                        .make(drawer, "You have successfully logged out", Snackbar.LENGTH_SHORT);
//
//                snackbar.show();
//                break;
//            case R.id.nav_account:
//                Intent intent = new Intent(
//                        MainActivity.this, AccountSettingActivity.class);
//                startActivity(intent);
//                String userid = session.getUserID();
//                Log.v(TAG, "userid!: " + userid);
//                break;
            default:
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_sign_up:
                Intent intent_sign = new Intent();
                intent_sign.setClass(MainActivity.this, SignupActivity.class);
                startActivity(intent_sign);
                break;
            case R.id.nav_login:
                Intent intent_login = new Intent();
                intent_login.setClass(MainActivity.this, LoginActivity.class);
                startActivity(intent_login);
//                finish();
                break;
            case R.id.nav_logout:
                logoutUser();
                showSignupMenu();
                Snackbar snackbar = Snackbar
                        .make(drawer, "You have successfully logged out", Snackbar.LENGTH_SHORT);

                snackbar.show();
                break;
            case R.id.nav_account:
                Intent intent = new Intent(
                        MainActivity.this, AccountSettingActivity.class);
                startActivity(intent);
                String userid = session.getUserID();
                Log.v(TAG, "userid!: " + userid);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
    }

}