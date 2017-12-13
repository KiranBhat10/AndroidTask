package com.example.kiran.androidtask.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.kiran.androidtask.R;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

/**
 * Created by Kiran on 13-12-2017.
 */

@SuppressLint("ValidFragment")
public class GalleryFragment extends Fragment {
    Context context;
    ImageView galleryImage;
    private int PICK_IMAGE = 100;

    @SuppressLint("ValidFragment")
    public GalleryFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.gallery_fragment, container, false);

        galleryImage = (ImageView) view.findViewById(R.id.galleryImage);

        chooseImage();

        return view;
    }

    /* Choose an image from Gallery */
    void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    String path = getImagePathFromURI(selectedImageUri);
                    Log.i(TAG, "Image Path : " + path);
                    galleryImage.setImageURI(selectedImageUri);
                }
            }
        }
    }

    /* Get the real path from the URI */
    public String getImagePathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}
