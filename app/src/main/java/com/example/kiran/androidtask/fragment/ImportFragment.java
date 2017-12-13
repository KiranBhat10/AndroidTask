package com.example.kiran.androidtask.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kiran.androidtask.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Kiran on 13-12-2017.
 */

@SuppressLint("ValidFragment")
public class ImportFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.camera_button)
    Button captureImage;
    @BindView(R.id.captureImageView)
    ImageView captureImageView;
    Context context;
    private int STORAGE_PERMISSIONS_CODE = 100;
    private int REQUEST_CAMERA = 200;
    private String mCurrentPhotoPath;
    private Uri contentUri;

    @SuppressLint("ValidFragment")
    public ImportFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.import_fragment, container, false);
        ButterKnife.bind(this, view);
        captureImage.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.camera_button:
                checkPermission();
                break;
        }
    }

    /* Check Permissions */
    private void checkPermission() {
        if ((ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
            callCameraApi();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, STORAGE_PERMISSIONS_CODE);
            }
        }
    }

    /* Request Camera to Open */
    void callCameraApi() {
        Intent cameraIntent = new Intent();
        cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoUri = null;
        photoUri = createImageFile();
        String authorities = getActivity().getPackageName() + ".fileprovider";
        Uri imageUri = FileProvider.getUriForFile(context, authorities, photoUri);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, REQUEST_CAMERA);

    }

    /* Create Image File Name */
    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,
                    ".JPEG",
                    storageDir
            );
            Log.d("image name 476", image.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCurrentPhotoPath = image.getAbsolutePath();
        galleryAddPic();

        return image;
    }

    /* Add Image to Gallery */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {

            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().managedQuery(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
            int column_index_data = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToLast();

            String imagePath = cursor.getString(column_index_data);
            captureImageView.setVisibility(View.VISIBLE);
            captureImageView.setImageURI(contentUri);

        } else {
            Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
        }

    }

}
