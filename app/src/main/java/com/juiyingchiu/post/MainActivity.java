package com.juiyingchiu.post;

import android.Manifest;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.File;
import java.util.List;
import java.lang.String;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_TAKE_PICTURE = 1;
    private static final int REQUEST_PICK_PICTURE = 2;
    private ImageButton addNewPhoto;
    private ImageButton photoLib;
    private ImageView imageView;
    private ImageButton send;
    private File file;
    private Uri contentUri;
    AlertDialogFragment alertFragment;
//    Intent intent;


    public static boolean isIntentAvailable(Context context, Intent intent) {

        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleView();

    }


    private void handleView() {

        imageView = findViewById(R.id.ivPhoto);
        send = findViewById(R.id.action_send);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_send) {
            Toast.makeText(this, "Your post successfully created", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onStart() {
        super.onStart();
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Common.askPermissions(this, permissions, Common.REQ_EXTERNAL_STORAGE);

    }

    public void onClick(View view) {

        alertFragment = new AlertDialogFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        alertFragment.show(fragmentManager, "alert");

//        intent = alertFragment.getIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        file = alertFragment.getFile();
        if (resultCode == RESULT_OK) {
            int newSize = 512;
            switch (requestCode) {
                case REQUEST_TAKE_PICTURE:

                    Bitmap srcPicture = BitmapFactory.decodeFile(file.getPath());
                    Bitmap downSizePicture = Common.downSize(srcPicture, newSize);
                    imageView.setImageBitmap(downSizePicture);

                    break;

                case REQUEST_PICK_PICTURE:
                    Uri uri = intent.getData();
                    if (uri != null) {
                        String[] columns = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(uri, columns, null, null, null);

                        if (cursor != null && cursor.moveToFirst()) {
                            String imagePath = cursor.getString(0);
                            cursor.close();
                            Bitmap srcImage = BitmapFactory.decodeFile(imagePath);
                            Bitmap downSizeImage = Common.downSize(srcImage, newSize);
                            imageView.setImageBitmap(downSizeImage);

                            break;


                        }
                    }
            }

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Common.REQ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addNewPhoto.setEnabled(true);
                    photoLib.setEnabled(true);
                    Toast.makeText(this, "請同意使用本機相機和讀取權限", Toast.LENGTH_SHORT);
                } else {
                    addNewPhoto.setEnabled(false);
                    photoLib.setEnabled(false);
                }
                break;
        }


    }


    public static class AlertDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
        private static final int REQUEST_TAKE_PICTURE = 1;
        private static final int REQUEST_PICK_PICTURE = 2;
        private File file;
        Intent intent;

        //        public Intent getIntent() {
//            return intent;
//        }
//
        public File getFile() {
            return file;
        }


        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())

                    .setMessage("Pick a photo from?")
                    .setPositiveButton("camera", this)
                    .setNegativeButton("gallery", this)
                    .create();

        }


        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    file = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    file = new File(file, "picture.jpg");
                    Uri contentUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                    if (MainActivity.isIntentAvailable(getActivity(), intent)) {
                        getActivity().startActivityForResult(intent, REQUEST_TAKE_PICTURE);


                    } else {

                        Toast.makeText(getActivity(), R.string.msg_NoCameraAppsFound, Toast.LENGTH_SHORT).show();
                    }

                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    getActivity().startActivityForResult(intent1, REQUEST_PICK_PICTURE);
                    break;
                default:
                    break;
            }
        }

    }

}
