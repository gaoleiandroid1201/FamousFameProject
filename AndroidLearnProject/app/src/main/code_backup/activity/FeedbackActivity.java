package com.android.learn.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.android.learn.R;
import com.android.learn.base.activity.BaseMvpActivity;
import com.android.learn.base.email.MailSender;
import com.android.learn.base.mpresenter.BasePresenter;
import com.android.learn.base.utils.LanguageUtil;
import com.android.learn.base.utils.Utils;

import butterknife.BindView;
import butterknife.OnClick;


public class FeedbackActivity extends BaseMvpActivity {

    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.send_btn)
    TextView send_btn;
    @BindView(R.id.add_attachment)
    TextView add_attachment;
    @BindView(R.id.file_dir)
    TextView file_dir;

    @BindView(R.id.et_email_title)
    EditText et_email_title;
    @BindView(R.id.et_email_content)
    EditText et_email_content;
    private final int FILECHOOSER_RESULTCODE = 1;
    private String sendEmail = "gaoleiemail@163.com";//发送方邮件
    private String sendEmaiPassword = "gl1201";//发送方邮箱密码(或授权码)
    private String receiveEmail = "gaoleiandroid@163.com";//接收方邮件
    private String file_path = null;


    public static void startActivity(Context context) {
        Intent intent = new Intent(context, FeedbackActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_help_feedback;
    }

    @Override
    protected void initData(Bundle bundle) {
        title.setText(getString(R.string.help_feedback));
        iv_back.setVisibility(View.VISIBLE);
    }

    @Override
    public BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void loadData() {

    }

    @OnClick({R.id.send_btn, R.id.add_attachment})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.send_btn:
                SenderRunnable senderRunnable = new SenderRunnable(sendEmail, sendEmaiPassword);
                String sendTitle = et_email_title.getText().toString();
                String sendContent = et_email_content.getText().toString();
                if ("".equals(sendTitle.trim())) {
                    Utils.showToast(getString(R.string.please_input_contact), true);
                    return;
                }
                if ("".equals(sendContent.trim())) {
                    Utils.showToast(getString(R.string.please_input_content), true);
                    return;
                }
                senderRunnable.setMail(sendTitle, sendContent,
                        receiveEmail, file_path);
                new Thread(senderRunnable).start();
//                sendEmail();
                break;
            case R.id.add_attachment:
                showFileChooser();
                break;
        }
    }


    private void showFileChooser() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"),
                FILECHOOSER_RESULTCODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();

            file_path = getPathByUri4kitkat(this, uri);
            Log.d("gaolei", "file_path---------------" + file_path);
            file_dir.setText(file_path);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class SenderRunnable implements Runnable {

        private String user;
        private String password;
        private String subject;
        private String body;
        private String receiver;
        private MailSender sender;
        private String attachment;

        public SenderRunnable(String user, String password) {
            this.user = user;
            this.password = password;
            sender = new MailSender(user, password);
            String mailhost = user.substring(user.lastIndexOf("@") + 1,
                    user.lastIndexOf("."));
            if (!mailhost.equals("gmail")) {
                mailhost = "smtp." + mailhost + ".com";
                Log.i("hello", mailhost);
                sender.setMailhost(mailhost);
            }
        }

        public void setMail(String subject, String body, String receiver,
                            String attachment) {
            this.subject = subject;
            this.body = body;
            this.receiver = receiver;
            this.attachment = attachment;
        }

        public void run() {
            // TODO Auto-generated method stub
            try {
                sender.sendMail(subject, body, user, receiver, attachment);
                Utils.showToast(getString(R.string.feedback_send_success), false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            } catch (Exception e) {
                // TODO Auto-generated catch block
                if (e.getMessage() != null)
                    Utils.showToast(getString(R.string.feedback_send_fail), false);
                e.printStackTrace();
            } finally {
                finish();
            }
        }
    }

    // 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使了。targetSdkVersion 22；如果targetSdkVersion>=23怎需要动态获取WRITE_EXTERNAL_STORAGE权限；如果targetSdkVersion>=24 则可能需要用到FileProvider
    @SuppressLint("NewApi")
    public static String getPathByUri4kitkat(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {// ExternalStorageProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {// DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {// MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private void sendEmail() {
        Intent email = new Intent(Intent.ACTION_SENDTO);
        /*不带附件发送邮件*/
//        email.setType("plain/text");
        email.setData(Uri.parse(receiveEmail));

        /*设置邮件默认地址，多个收件人，String数组*/
        //    email.putExtra(android.content.Intent.EXTRA_EMAIL, (String[])mMailReceivers.toArray(new String[mMailReceivers.size()]));
        /*多个抄送人，String数组*/
//        email.putExtra(android.content.Intent.EXTRA_CC, (String[])mMailCopyTos.toArray(new String[mMailCopyTos.size()]));
        /*邮件标题*/
        email.putExtra(Intent.EXTRA_SUBJECT, et_email_title.getText().toString());
        /*邮件正文*/
        email.putExtra(Intent.EXTRA_TEXT, et_email_content.getText().toString());
//调用系统的邮件系统
        startActivity(Intent.createChooser(email, "请选择邮件发送软件"));

    }

}
