package tp.ve.com.tradingplatform.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tp.ve.com.tradingplatform.R;
import tp.ve.com.tradingplatform.app.AppConfig;
import tp.ve.com.tradingplatform.component.MultiSelectionSpinner;

/**
 * Created by Zeng on 2015/11/10.
 */
public class AccountSettingActivity extends AppCompatActivity {
    private static final int CAPTURE_IMAGE_REQUEST_CODE_ID = 100;
    private static final int CAPTURE_IMAGE_REQUEST_CODE_BIZ = 200;
    private static final String TAG = AccountSettingActivity.class.getSimpleName();
    private MultiSelectionSpinner multiSelectionSpinner;
    public static LinearLayout linearLayout_advanced;
    public static ScrollView scrollView;
    Button btnUploadID, btnUploadeBizLicense;
    ImageView idImgPreview, BizLicenseImgPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsetting);

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.roleSpinner);
        linearLayout_advanced = (LinearLayout) findViewById(R.id.advanced_view);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        btnUploadID = (Button) findViewById(R.id.btn_uploadID);
        btnUploadeBizLicense = (Button) findViewById(R.id.btn_uploadBizLicense);
        idImgPreview = (ImageView) findViewById(R.id.idImgPreview);
        BizLicenseImgPreview = (ImageView) findViewById(R.id.BizLicenseImgPreview);

        btnUploadID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent(CAPTURE_IMAGE_REQUEST_CODE_ID);
//                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//                photoPickerIntent.setType("image/*");
//                startActivityForResult(photoPickerIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
            }
        });

        btnUploadeBizLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent(CAPTURE_IMAGE_REQUEST_CODE_BIZ);
            }
        });

        String[] array = {"Buyer", "ITP", "Supplier"};
        multiSelectionSpinner.setItems(array);
        multiSelectionSpinner.setSelection(new int[]{0});

        linearLayout_advanced.setVisibility(LinearLayout.GONE);
        Log.v("test", "allChechedPositon:" + multiSelectionSpinner.checkItemPostion.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_account_setting_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE_REQUEST_CODE_ID) {
                Uri selectedImageUri;
                Log.v(TAG, "getData" + String.valueOf(data.getData()));
                if (data.getData() == null) {
                    selectedImageUri = outputFileUri;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    final Bitmap bitmap = BitmapFactory.decodeFile(selectedImageUri.getPath(), options);
                    idImgPreview.setVisibility(ImageView.VISIBLE);
                    idImgPreview.setImageBitmap(bitmap);
                } else {
                    selectedImageUri = data.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    idImgPreview.setVisibility(ImageView.VISIBLE);
                    idImgPreview.setImageBitmap(selectedImage);
                }
                Log.v(TAG, "url!!!!!:" + String.valueOf(selectedImageUri.getPath()));
            } else if (requestCode == CAPTURE_IMAGE_REQUEST_CODE_BIZ) {
                Uri selectedImageUri;
                Log.v(TAG, "getData" + String.valueOf(data.getData()));
                if (data.getData() == null) {
                    selectedImageUri = outputFileUri;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    final Bitmap bitmap = BitmapFactory.decodeFile(selectedImageUri.getPath(), options);
                    BizLicenseImgPreview.setVisibility(ImageView.VISIBLE);
                    BizLicenseImgPreview.setImageBitmap(bitmap);
                } else {
                    selectedImageUri = data.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    BizLicenseImgPreview.setVisibility(ImageView.VISIBLE);
                    BizLicenseImgPreview.setImageBitmap(selectedImage);
                }
                Log.v(TAG, "url!!!!!:" + String.valueOf(selectedImageUri.getPath()));
            }
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    AccountSettingActivity.scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }
    }

    private Uri outputFileUri;

    private void openImageIntent(int requestcode) {

// Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
        root.mkdirs();
        final String fname = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, requestcode);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", outputFileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        outputFileUri = savedInstanceState.getParcelable("file_uri");
    }


}
