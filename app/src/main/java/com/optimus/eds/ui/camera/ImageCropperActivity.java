package com.optimus.eds.ui.camera;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.optimus.eds.BaseActivity;
import com.optimus.eds.Constant;
import com.optimus.eds.R;
import com.optimus.eds.utils.PermissionUtil;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.Closeable;
import java.io.File;

import java.io.IOException;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImageCropperActivity extends BaseActivity {

    public static final String TAG = "ImageCropperActivity";
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";

    private final Bitmap.CompressFormat mOutputFormat = Bitmap.CompressFormat.JPEG;
    @BindView(R.id.cropImageView)
    CropImageView mImageView;
    private ContentResolver mContentResolver;

    //Temp file to save cropped image
    private String mImagePath;
    private Uri mImageUri = null;
    //File for capturing camera images
    private File mFileTemp;

    private boolean isIdentity;


    @Override
    public int getID() {
        return R.layout.activity_image_cropper;
    }

    @Override
    public void created(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setToolbar("Take Picture");
        mContentResolver = getContentResolver();
        isIdentity=getIntent().getBooleanExtra("IDENTITY",false);
        mImageView.setCropShape(isIdentity? CropImageView.CropShape.RECTANGLE:CropImageView.CropShape.OVAL);
        mImageView.setFixedAspectRatio(!isIdentity);
        PermissionUtil.requestPermission(this, Manifest.permission.CAMERA, new PermissionUtil.PermissionCallback() {
            @Override
            public void onPermissionsGranted(String permission) {
                proceedToCrop(savedInstanceState);
            }

            @Override
            public void onPermissionsGranted() {

            }

            @Override
            public void onPermissionDenied() {

            }
        });
    }



    /**
     * Get passed action as extras and call intent accordingly
     * @param savedInstanceState
     */
    private void proceedToCrop(Bundle savedInstanceState) {
        mFileTemp = createImageFile();
        if (savedInstanceState == null || !savedInstanceState.getBoolean("restoreState")) {
            String action = getIntent().getStringExtra("ACTION");

            if (null != action) {
                switch (action) {
                    case Constant.IntentExtras.ACTION_CAMERA:
                        getIntent().removeExtra("ACTION");
                        takePic();
                        return;
                }
            }
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
    }



    /**
     * Save cropped image and send image path back to calling activity
     */
    private void saveCroppedImage() {
        boolean saved = saveOutput();
        if (saved) {
            Intent intent = new Intent();

           // intent.putExtra(isIdentity? IntentExtras.IMAGE_PATH_ID:Constant.IntentExtras.IMAGE_PATH, mImagePath);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(this, "Unable to save Image into your device.", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Create temp image which will be write by camera api
     * @return
     */
    private File createImageFile() {
        try {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File  image = File.createTempFile(imageFileName, ".jpg", storageDir);
            mImagePath = image.getAbsolutePath();
            return image;
        } catch (IOException ex) {
            // Error occurred while creating the File
            errored();
        }
        return null;
    }

    /**
     * Capture pic with camera
     */
    private void takePic() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Continue only if the File was successfully created
            if (mFileTemp != null) {

                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.optimus.eds.fileprovider",
                        mFileTemp);
                mImageUri = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.putExtra("return-data",true);

                startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PICTURE);
            }else{
                mFileTemp = createImageFile();
            }
        }
    }



    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("restoreState", true);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode == REQUEST_CODE_TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                mImageView.setImageUriAsync(mImageUri);
            } else if (resultCode == RESULT_CANCELED) {
                userCancelled();
            } else {
                errored();
            }

        }
    }


    /**
     * Save cropped image
     * @return
     */
    private boolean saveOutput() {

        Bitmap croppedImage = mImageView.getCroppedImage();
        if (mImageUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = mContentResolver.openOutputStream(mImageUri);
                if (outputStream != null) {
                    croppedImage.compress(mOutputFormat, 90, outputStream);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            } finally {
                closeSilently(outputStream);
            }
        } else {
            Log.e(TAG, "not defined image url");
            return false;
        }
        croppedImage.recycle();
        return true;

    }


    public void closeSilently(Closeable c) {
        if (c == null) return;
        try {
            c.close();
        } catch (Throwable t) {
            // do nothing
        }
    }

    public void userCancelled() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    public void errored() {
        Intent intent = new Intent();
        intent.putExtra(ERROR, true);
        intent.putExtra(ERROR_MSG, "Error while opening the image file. Please try again.");
        finish();
    }

    @OnClick(R.id.btnRetake)
    public void retakeImage(){
        userCancelled();
    }


    @OnClick(R.id.btnSave)
    public void saveImage(){
        saveCroppedImage();
    }

}
