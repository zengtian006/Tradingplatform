package tp.ve.com.tradingplatform.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
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

import tp.ve.com.tradingplatform.R;
import tp.ve.com.tradingplatform.helper.SessionManager;
import tp.ve.com.tradingplatform.utils.VersionUtil;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private final static int SCANNED_GREENEST_CODE = 1;
    private static final int INTENT_REQUEST_ADD_ITEM = 12;
    public static int currentRole;
    boolean doubleBackToExitPressedOnce = false;

    private static SessionManager session;

    public static DrawerLayout drawer;
    static NavigationView navigationView;
    private static LinearLayout layout_loggedin, layout_noLoggedin;
    Button btn_signup, btn_login, btn_account, btn_logout;
    View headerView;
    static TextView icontext;
    ImageView img_header;
    WebView webView;
    SharedPreferences appPreferences;
    boolean isAppInstalled = false;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkShowTutorial();
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
        pd = new ProgressDialog(this);
        pd.setTitle(getString(R.string.app_name));
        pd.setMessage("Loading");

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
        img_header = (ImageView) headerView.findViewById(R.id.header_img);
        img_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MeActivity.class));
            }
        });


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


        /*Main body of app*/
        webView = (WebView) this.findViewById(R.id.web_view);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //mProgressbar.setProgress(newProgress);
            }
        });
        webView.setWebViewClient(new WebViewClient() {

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // mProgressbar.setVisibility(View.VISIBLE);
                pd.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // mProgressbar.setVisibility(View.INVISIBLE);
                pd.dismiss();
            }

            /*for android ver < 6*/
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //Log.d("Mylog", "onReceivedError old");
                super.onReceivedError(view, errorCode, description, failingUrl);
                view.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), getString(R.string.cannot_connect_server), Toast.LENGTH_SHORT).show();
                view.loadUrl("file:///android_asset/error.html");
            }

            /*for android ver >= 6*/
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                //Log.d("Mylog", "onReceivedError new");
                super.onReceivedError(view, request, error);
                view.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), getString(R.string.cannot_connect_server), Toast.LENGTH_SHORT).show();
                view.loadUrl("file:///android_asset/error.html");
            }
        });

        File dir = getCacheDir();

        if (!dir.exists()) {
            dir.mkdirs();
        }
        webView.getSettings().setAppCachePath(dir.getPath());
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        webView.loadUrl("http://10.0.3.99/groupbuys/mview/11e57c8149e28c618262f8bc129fbedf");

        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            if (webView.canGoBack()) {
                                webView.goBack();
                            } else {
                                new AlertDialog.Builder(webView.getContext())
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle(getString(R.string.app_name))
                                        .setMessage("Would like to exit?")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Stop the activity
                                                finish();
                                            }
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .show();
                            }
                            return true;
                    }
                }
                return false;
            }
        });
    }

    private void checkShowTutorial() {
        int oldVersionCode = VersionUtil.getAppPrefInt(this, "version_code");
        int currentVersionCode = VersionUtil.getAppVersionCode(this);
        if (currentVersionCode > oldVersionCode) {
            startActivity(new Intent(MainActivity.this, ProductTourActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            VersionUtil.putAppPrefInt(this, "version_code", currentVersionCode);
        }
    }

    public static void showSignupMenu() {
        MenuItem deal = navigationView.getMenu().findItem(R.id.nav_deal);
        deal.setVisible(false);
        MenuItem opportunity = navigationView.getMenu().findItem(R.id.nav_opportunity);
        opportunity.setVisible(false);
        layout_loggedin.setVisibility(LinearLayout.GONE);
        layout_noLoggedin.setVisibility(LinearLayout.VISIBLE);
    }

    public static void showLogoutMenu() {
        MenuItem deal = navigationView.getMenu().findItem(R.id.nav_deal);
        deal.setVisible(true);
        MenuItem opportunity = navigationView.getMenu().findItem(R.id.nav_opportunity);
        opportunity.setVisible(true);
        layout_loggedin.setVisibility(LinearLayout.VISIBLE);
        layout_noLoggedin.setVisibility(LinearLayout.GONE);
        String roleTitle = "Buyer";
        if (session.currMember.getMember_default_role().equals("B")) {
            currentRole = 0;//Buyer
        } else if (session.currMember.getMember_default_role().equals("I")) {
            currentRole = 1; //ITP
            roleTitle = "ITP";
        } else if (session.currMember.getMember_default_role().equals("S")) {
            currentRole = 2; //Supplier
            roleTitle = "Supplier";
        }
        icontext.setText(roleTitle + ": " + session.currMember.getMember_name());

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

    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
//                System.exit(0);
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
        MenuItem searchItem;
        searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        final SearchView finalSearchView = searchView;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Log.d("saerch", query);
                WebView webView = (WebView) findViewById(R.id.web_view);
                webView.loadUrl("http://10.0.3.90/items/m-index?qa=" + query);
                finalSearchView.setQuery("", false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("searchnewtext", newText);
                return false;
            }
        });
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
//                ShareActivity.urlString = webView.getUrl().toString();


                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                // Add data to the intent, the receiving app will decide
                // what to do with it.
//                share.putExtra(Intent.EXTRA_SUBJECT, "Title Of The Post");
                share.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
                startActivity(Intent.createChooser(share, "Share link!"));

                break;
            case R.id.ic_menu_share_plus:
                intent.setClass(MainActivity.this, ShareActivity.class);
                intent.putExtra("URL", webView.getUrl());
                startActivity(intent);
//                Log.v(TAG, "Member_id: " + SessionManager.currMember.getMember_id());
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
//        switch (id) {
//            case R.id.nav_add_item:
//                Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
//                startActivityForResult(intent, INTENT_REQUEST_ADD_ITEM);
//                break;
//        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "resuleCode " + resultCode);
        Log.d(TAG, "requestCode " + requestCode);

//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == INTENT_REQUEST_ADD_ITEM) {
//                Toast.makeText(this, "Your item is added", Toast.LENGTH_LONG).show();
//                Log.d(TAG, "load new url after add item");
//                webView.loadUrl("http://" + getString(R.string.app_item_seller_index_url));
//            }
//        }
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