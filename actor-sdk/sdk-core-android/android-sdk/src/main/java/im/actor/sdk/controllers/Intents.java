package im.actor.sdk.controllers;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;

import im.actor.core.entity.FileReference;
import im.actor.core.entity.Peer;
import im.actor.core.utils.IOUtils;
import im.actor.runtime.files.FileSystemReference;
import im.actor.sdk.controllers.contacts.AddContactActivity;
import im.actor.sdk.controllers.pickers.TakePhotoActivity;
import im.actor.sdk.controllers.conversation.ChatActivity;
import im.actor.sdk.controllers.group.GroupInfoActivity;
import im.actor.sdk.controllers.group.InviteLinkActivity;
import im.actor.sdk.controllers.fragment.preview.PictureActivity;
import im.actor.sdk.controllers.settings.EditAboutActivity;
import im.actor.sdk.controllers.settings.EditNameActivity;

public class Intents {

    public static final String EXTRA_FORWARD_TEXT = "forward_text";

    public static final String EXTRA_FORWARD_TEXT_RAW = "forward_text_raw";

    public static final String EXTRA_FORWARD_CONTENT = "forward_content";

    public static final String EXTRA_SHARE_USER = "share_user";

    public static final String EXTRA_UID = "uid";

    public static final String EXTRA_GROUP_ID = "group_id";

    public static final String EXTRA_CHAT_PEER = "chat_peer";
    public static final String EXTRA_CHAT_COMPOSE = "compose";

    public static final String EXTRA_EDIT_TYPE = "edit_type";
    public static final String EXTRA_EDIT_ID = "edit_id";

    public static final int RESULT_DELETE = 0;
    public static final int RESULT_IMAGE = 1;

    public static final String EXTRA_ALLOW_DELETE = "allow_delete";
    public static final String EXTRA_RESULT = "result";
    public static final String EXTRA_IMAGE = "image";

    public static Intent pickAvatar(boolean isAllowDelete, Context context) {
        return new Intent(context, TakePhotoActivity.class)
                .putExtra(EXTRA_ALLOW_DELETE, isAllowDelete);
    }

    public static Intent editMyName(Context context) {
        return new Intent(context, EditNameActivity.class)
                .putExtra(EXTRA_EDIT_TYPE, EditNameActivity.TYPE_ME)
                .putExtra(EXTRA_EDIT_ID, 0);
    }

    public static Intent editUserName(int uid, Context context) {
        return new Intent(context, EditNameActivity.class)
                .putExtra(EXTRA_EDIT_TYPE, EditNameActivity.TYPE_USER)
                .putExtra(EXTRA_EDIT_ID, uid);
    }

    public static Intent editGroupTitle(int groupId, Context context) {
        return new Intent(context, EditNameActivity.class)
                .putExtra(EXTRA_EDIT_TYPE, EditNameActivity.TYPE_GROUP)
                .putExtra(EXTRA_EDIT_ID, groupId);
    }

    public static Intent editGroupTheme(int groupId, Context context) {
        return new Intent(context, EditNameActivity.class)
                .putExtra(EXTRA_EDIT_TYPE, EditNameActivity.TYPE_GROUP_THEME)
                .putExtra(EXTRA_EDIT_ID, groupId);
    }

    public static Intent editUserAbout(Context context) {
        return new Intent(context, EditAboutActivity.class)
                .putExtra(EXTRA_EDIT_TYPE, EditAboutActivity.TYPE_ME)
                .putExtra(EXTRA_EDIT_ID, 0);
    }

    public static Intent editUserNick(Context context) {
        return new Intent(context, EditNameActivity.class)
                .putExtra(EXTRA_EDIT_TYPE, EditNameActivity.TYPE_NICK);
    }

    public static Intent editGroupAbout(int groupId, Context context) {
        return new Intent(context, EditAboutActivity.class)
                .putExtra(EXTRA_EDIT_TYPE, EditAboutActivity.TYPE_GROUP)
                .putExtra(EXTRA_EDIT_ID, groupId);
    }

    public static Intent openGroup(int chatId, Context context) {
        Intent res = new Intent(context, GroupInfoActivity.class);
        res.putExtra(EXTRA_GROUP_ID, chatId);
        return res;
    }

    public static Intent inviteLink(int chatId, Context context) {
        Intent res = new Intent(context, InviteLinkActivity.class);
        res.putExtra(EXTRA_GROUP_ID, chatId);
        return res;
    }

    public static Intent openDialog(Peer peer, boolean compose, Context context) {
        final Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EXTRA_CHAT_PEER, peer.getUnuqueId());
        intent.putExtra(EXTRA_CHAT_COMPOSE, compose);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

    public static Intent openPrivateDialog(int uid, boolean compose, Context context) {
        return openDialog(Peer.user(uid), compose, context);
    }

    public static Intent openGroupDialog(int chatId, boolean compose, Context context) {
        return openDialog(Peer.group(chatId), compose, context);
    }

    public static Intent call(long phone) {
        return call(phone + "");
    }

    public static Intent call(String phone) {
        return new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:+" + phone));
    }

    public static Intent findContacts(Context context) {
        return new Intent(context, AddContactActivity.class);
    }

    // External intents

    private static Uri getAvatarUri(FileReference location) {
        return Uri.parse("content://im.actor.avatar/" + location.getFileId());
    }

    private static Uri getAvatarUri(Activity activity, String downloadFileName) {
        File file = new File(downloadFileName);
        Uri uri = Uri.fromFile(file);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//如果是7.0android系统
//            ContentValues contentValues = new ContentValues(1);
//            contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            uri = FileProvider.getUriForFile(activity, "im.actor.develop.myFileProvider", file);
        }

        return uri;
    }

    public static Intent openDoc(Activity activity, String fileName, String downloadFileName) {
        String mimeType = MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(IOUtils.getFileExtension(fileName));
        if (mimeType == null) {
            mimeType = "*/*";
        }
//        System.out.println("iGem:openDoc:mimeType=" + mimeType);
//        File file = new File(downloadFileName);
//        System.out.println("iGem:openDoc:downloadFileName=" + downloadFileName);
        Uri uri = getAvatarUri(activity, downloadFileName);
//        System.out.println("iGem:openVideo:uri=" + uri.toString());
//        Uri uri = Uri.fromFile(file);
        return new Intent(Intent.ACTION_VIEW)
                .setDataAndType(uri, mimeType)
                .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

    }

    public static Intent openVideo(Activity activity, String fileName, String downloadFileName) {
        String mimeType = MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(IOUtils.getFileExtension(fileName));
        if (mimeType == null) {
            mimeType = "*/*";
        }
//        System.out.println("iGem:openVideo:downloadFileName=" + downloadFileName);
        Uri uri = getAvatarUri(activity, downloadFileName);
//        System.out.println("iGem:openVideo:uri=" + uri.toString());
//        String path = getPath(activity.getApplicationContext(),uri);
//        System.out.println("iGem:uriPath:"+path);
        return new Intent(Intent.ACTION_VIEW)
                .setDataAndType(uri, mimeType)
                .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

    }


    public static Intent shareDoc(Activity activity, String fileName, String downloadFileName) {
        String mimeType = MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(IOUtils.getFileExtension(fileName));
        if (mimeType == null) {
            mimeType = "*/*";
        }
        Uri uri = getAvatarUri(activity, downloadFileName);
        Intent intent = new Intent(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_STREAM, uri)
                .setType(mimeType)
                .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        return Intent.createChooser(intent,"分享");
    }

    public static Intent shareAvatar(FileReference location) {
        return new Intent(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_STREAM, getAvatarUri(location))
                .setType("image/jpeg")
                .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }

    public static Intent openAvatar(FileReference location) {
        return new Intent(Intent.ACTION_VIEW)
                .setDataAndType(getAvatarUri(location), "image/jpeg")
                .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }

    public static Intent setAsAvatar(FileReference location) {
        Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
        intent.setDataAndType(getAvatarUri(location), "image/jpg");
        intent.putExtra("mimeType", "image/jpg");
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intent;
    }

    public static Intent pickFile(Context context) {
        return im.actor.sdk.controllers.pickers.Intents.pickFile(context);
    }

    public static Intent pickLocation(Context context) {
        return im.actor.sdk.controllers.pickers.Intents.pickLocation(context);
    }

    public static void openMedia(Activity activity, View photoView, String path, int senderId) {
        PictureActivity.launchPhoto(activity, photoView, path, senderId);
    }

    public static void savePicture(Context context, Bitmap bitmap) {

        File actorPicturesFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        actorPicturesFolder = new File(actorPicturesFolder, "Actor");
        actorPicturesFolder.mkdirs();
        try {
            File pictureFile = new File(actorPicturesFolder, System.currentTimeMillis() + ".jpg");
            pictureFile.createNewFile();


            FileOutputStream ostream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
            ostream.close();


            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(pictureFile);
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);
            Log.d("Picture saving", "Saved as " + pictureFile.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
