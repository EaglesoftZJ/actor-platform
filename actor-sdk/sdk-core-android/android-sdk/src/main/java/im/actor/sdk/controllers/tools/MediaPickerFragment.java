package im.actor.sdk.controllers.tools;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.widget.Toast;


import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import im.actor.runtime.android.AndroidContext;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.controllers.BaseFragment;
import im.actor.sdk.controllers.pickers.file.FilePickerActivity;
import im.actor.sdk.permisson_interface.OnPermissionListener;
import im.actor.sdk.util.Randoms;

public class MediaPickerFragment extends BaseFragment {

    private static final int REQUEST_GALLERY = 0;
    private static final int REQUEST_PHOTO = 1;
    private static final int REQUEST_VIDEO = 2;
    private static final int REQUEST_DOC = 3;
    private static final int REQUEST_LOCATION = 4;
    private static final int REQUEST_CONTACT = 5;
    private static final int PERMISSIONS_REQUEST_CAMERA = 6;
    private static final int PERMISSIONS_REQUEST_CONTACTS = 7;

    private static final int PERMISSIONS_REQUEST_LOCATION = 8;

    private static final int PERMISSIONS_REQUEST_VIDEO = 9;

    private String pendingFile;
    private boolean pickCropped;

    public void requestPhoto() {
        requestPhoto(false);
    }

    public void requestPhoto(boolean pickCropped) {
        this.pickCropped = pickCropped;

        //
        // Checking permissions
        //
        Activity activity = getActivity();
        if (activity != null) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_CAMERA);
                    return;
                }
            }
        } else {
            return;
        }


        //
        // Generating Temporary File Name
        //
        pendingFile = generateRandomFile(".jpg");
        if (pendingFile == null) {
            return;
        }
        //
        // Requesting Photo
        //
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = new File(pendingFile);
        Uri photoUri = Uri.fromFile(photoFile);
//        System.out.println("iGem:"+photoUri.toString());
//        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//            try {
//                photoUri = createImageUri(photoFile);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        } else
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//如果是7.0android系统
//            ContentValues contentValues = new ContentValues(1);
//            contentValues.put(MediaStore.Images.Media.DATA, photoFile.getAbsolutePath());
//            photoUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            photoUri = FileProvider.getUriForFile(activity, "im.actor.develop.myFileProvider", photoFile);
        }
//        System.out.println("iGem:"+photoUri.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        startActivityForResult(intent, REQUEST_PHOTO);
    }


    public void requestVideo() {
        this.pickCropped = false;

        //
        // Generating Temporary File Name
        //
        pendingFile = generateRandomFile(".mp4");
        if (pendingFile == null) {
            return;
        }
        Activity activity = getActivity();
        if (Build.VERSION.SDK_INT >= 23) {

            String[] PERMISSIONS_CONTACT = {Manifest.permission.CAMERA};

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_VIDEO);
                return;
            }
        }
        //
        // Requesting Video
        //
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        File videoFile = new File(pendingFile);
        Uri videoUri = Uri.fromFile(videoFile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//如果是7.0android系统
//            ContentValues contentValues = new ContentValues(1);
//            contentValues.put(MediaStore.Images.Media.DATA, videoFile.getAbsolutePath());
//            videoUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            videoUri = FileProvider.getUriForFile(activity, "im.actor.develop.myFileProvider", videoFile);
        }
//        System.out.println("iGem:videoUri:"+videoUri.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        startActivityForResult(intent, REQUEST_VIDEO);
    }

    public void requestGallery() {
        requestGallery(false);
    }

    public void requestGallery(boolean pickCropped) {
        this.pickCropped = pickCropped;

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/* video/*");
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    public void requestFile() {
        this.pickCropped = false;

        Activity activity = getActivity();
        startActivityForResult(new Intent(activity, FilePickerActivity.class), REQUEST_DOC);
    }

    public void requestLocation() {
        Activity activity = getActivity();
        if (activity != null) {
            if (Build.VERSION.SDK_INT >= 23) {

                String[] PERMISSIONS_CONTACT = {
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
                };

                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(PERMISSIONS_CONTACT, PERMISSIONS_REQUEST_LOCATION);
                    return;
                }
            }
        } else {
            return;
        }
        this.pickCropped = false;

        Intent intent = new Intent("im.actor.pickLocation_" + AndroidContext.getContext().getPackageName());
        startActivityForResult(intent, REQUEST_LOCATION);
    }

    public void requestContact() {
        this.pickCropped = false;

        //
        // Checking permissions
        //
        Activity activity = getActivity();
        if (activity != null) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_CONTACTS);
                    return;
                }
            }
        } else {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQUEST_CONTACT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GALLERY) {
                if (data.getData() != null) {
                    if (pickCropped) {
                        pendingFile = generateRandomFile(".jpg");
                        Crop.of(data.getData(), Uri.fromFile(new File(pendingFile)))
                                .asSquare()
                                .start((Activity) getContext());
                    } else {
                        getCallback().onUriPicked(data.getData());
                    }
                }
            } else if (requestCode == REQUEST_PHOTO) {
                if (pendingFile != null) {

                    String sourceFileName = pendingFile;
                    Context context = getContext();
                    if (context != null) {
                        MediaScannerConnection.scanFile(context, new String[]{pendingFile},
                                new String[]{"image/jpeg"}, null);
                    }

                    if (pickCropped) {
                        pendingFile = generateRandomFile(".jpg");
                        Crop.of(Uri.fromFile(new File(sourceFileName)), Uri.fromFile(new File(pendingFile)))
                                .asSquare()
                                .start((Activity) getContext());
                    } else {
                        getCallback().onPhotoPicked(sourceFileName);
                    }
                }
            } else if (requestCode == Crop.REQUEST_CROP) {
                if (pendingFile != null) {
                    getCallback().onPhotoCropped(pendingFile);
                }
            } else if (requestCode == REQUEST_VIDEO) {
                if (pendingFile != null) {
//                    System.out.println("iGem:pendingFile:"+pendingFile);
                    getCallback().onVideoPicked(pendingFile);
                    Context context = getContext();
                    if (context != null) {
                        MediaScannerConnection.scanFile(context, new String[]{pendingFile},
                                new String[]{"video/mp4"}, null);
                    }
                    pendingFile = null;
                }
            } else if (requestCode == REQUEST_DOC) {
                if (data.getData() != null) {
                    getCallback().onUriPicked(data.getData());
                } else if (data.hasExtra("picked")) {
                    ArrayList<String> files = data.getStringArrayListExtra("picked");
                    if (files != null && files.size() > 0) {
                        getCallback().onFilesPicked(files);
                    }
                }
            } else if (requestCode == REQUEST_CONTACT) {
                Activity activity = getActivity();
                if (activity == null) {
                    return;
                }
                ArrayList<String> phones = new ArrayList<>();
                ArrayList<String> emails = new ArrayList<>();
                String name = "";
                byte[] photo = null;

                Uri contactData = data.getData();
                Cursor c = activity.managedQuery(contactData, null, null, null, null);
                if (c.moveToFirst()) {


                    String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                    String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    if (hasPhone.equalsIgnoreCase("1")) {
                        Cursor phonesCursor = activity.getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                null, null);
                        if (phonesCursor != null) {
                            try {
                                if (phonesCursor.moveToFirst()) {
                                    int phoneColumnIndex = phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);
                                    do {
                                        phones.add(phonesCursor.getString(phoneColumnIndex));
                                    } while (phonesCursor.moveToNext());
                                }
                            } finally {
                                phonesCursor.close();
                            }
                        }
                    }
                    name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));


                    Cursor emailCursor = activity.getContentResolver().query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id,
                            null, null);
                    if (emailCursor != null && emailCursor.moveToFirst()) {
                        int emailColumnIndex = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
                        do {
                            emails.add(emailCursor.getString(emailColumnIndex));
                        } while (emailCursor.moveToNext());
                        emailCursor.close();
                    }

                    Uri photoUri = Uri.withAppendedPath(contactData, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
                    Cursor photoCursor = activity.getContentResolver().query(photoUri,
                            new String[]{ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
                    if (photoCursor != null) {
                        try {
                            if (photoCursor.moveToFirst()) {
                                photo = photoCursor.getBlob(0);
                            }
                        } finally {
                            photoCursor.close();
                        }
                    }
                }

                getCallback().onContactPicked(name, phones, emails, photo);
            } else if (requestCode == REQUEST_LOCATION) {
                getCallback().onLocationPicked(data.getDoubleExtra("longitude", 0), data.getDoubleExtra("latitude", 0), data.getStringExtra("street"), data.getStringExtra("place"));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestPhoto();
            }
        }

        if (requestCode == PERMISSIONS_REQUEST_VIDEO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestVideo();
            }
        }


        if (requestCode == PERMISSIONS_REQUEST_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestContact();
            }
        }
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocation();
            }
        }

    }

    @Override
    public void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);
        if (saveInstance != null) {
            pendingFile = saveInstance.getString("pendingFile", null);
            pickCropped = saveInstance.getBoolean("pickCropped");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (pendingFile != null) {
            outState.putString("pendingFile", pendingFile);
        }
        outState.putBoolean("pickCropped", pickCropped);
    }

    private String generateRandomFile(String ext) {
        File externalFile = Environment.getExternalStorageDirectory();
        if (externalFile != null) {
            String externalPath = externalFile.getAbsolutePath();
            String exportPathBase = externalPath +
                    "/" + ActorSDK.sharedActor().getAppName() +
                    "/" + ActorSDK.sharedActor().getAppName() + " images" + "/";
            new File(exportPathBase).mkdirs();

            return exportPathBase + "capture_" + Randoms.randomId() + ext;
        } else {
            Toast.makeText(AndroidContext.getContext(), R.string.toast_no_sdcard, Toast.LENGTH_LONG).show();
            return null;
        }
    }


    /**
     * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
     * https://blog.csdn.net/mr_tony/article/details/105434719
     * https://developer.android.com/training/data-storage/shared/media?hl=zh-cn
     *
     * @return 图片的uri
     */
    private Uri createImageUri(File file) {
        //设置保存参数到ContentValues中
        ContentValues contentValues = new ContentValues();
        //设置文件名
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, file.getName());
        //兼容Android Q和以下版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //android Q中不再使用DATA字段，而用RELATIVE_PATH代替
            //TODO RELATIVE_PATH是相对路径不是绝对路径;照片存储的地方为：内部存储/Pictures/preventpro
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH,
                    "Pictures/" + ActorSDK.sharedActor().getAppName() + " images");
        }
        //设置文件类型
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/JPEG");
        //执行insert操作，向系统文件夹中添加文件
        //EXTERNAL_CONTENT_URI代表外部存储器，该值不变
        Uri uri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        return uri;

    }


    private MediaPickerCallback getCallback() {
        return new SafeCallback(getParentFragment());
    }

    private class SafeCallback implements MediaPickerCallback {

        private MediaPickerCallback callback;

        public SafeCallback(Fragment fragment) {
            if (fragment instanceof MediaPickerCallback) {
                callback = (MediaPickerCallback) fragment;
            }
        }

        @Override
        public void onUriPicked(Uri uri) {
            if (callback != null) {
                callback.onUriPicked(uri);
            }
        }

        @Override
        public void onFilesPicked(List<String> paths) {
            if (callback != null) {
                callback.onFilesPicked(paths);
            }
        }

        @Override
        public void onPhotoPicked(String path) {
            if (callback != null) {
                callback.onPhotoPicked(path);
            }
        }

        @Override
        public void onVideoPicked(String path) {
            if (callback != null) {
                callback.onVideoPicked(path);
            }
        }

        @Override
        public void onPhotoCropped(String path) {
            if (callback != null) {
                callback.onPhotoCropped(path);
            }
        }

        @Override
        public void onContactPicked(String name, List<String> phones, List<String> emails, byte[] avatar) {
            if (callback != null) {
                callback.onContactPicked(name, phones, emails, avatar);
            }
        }

        @Override
        public void onLocationPicked(double latitude, double longitude, String street, String place) {
            if (callback != null) {
                callback.onLocationPicked(latitude, longitude, street, place);
            }
        }
    }
}
