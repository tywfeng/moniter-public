package com.tyw.moniter.main.ui;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import moniter.tyw.com.moniterlibrary.Utils.DebugLog;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tyw.moniter.common.Options;
import com.tyw.moniter.main.R;
import com.tyw.moniter.utils.USBUtils;

import java.util.List;

public class OptionsFragment extends Fragment {

    private OptionsViewModel mViewModel;

    // 控件
    View mBtnChangeConfigPath;
    TextView mTextConfigPath                // 配置路径
            , mTextMaximumFractionDigits;    // 保留小数位
    private static final int REQUESTCODE_CHANGE_CONFIGPATH = 1;

    public static OptionsFragment newInstance() {
        return new OptionsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_options, container, false);
        findViews(view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(OptionsViewModel.class);
        // TODO: Use the ViewModel
    }

    private void findViews(View _rootView) {
        if (_rootView == null) return;
        // findview
        mBtnChangeConfigPath = _rootView.findViewById(R.id.btn_change_configpath);
        mTextConfigPath = _rootView.findViewById(R.id.text_configpath);
        mTextMaximumFractionDigits = _rootView.findViewById(R.id.text_options_MaximumFractionDigits);

        // init data
        String strDevicesConfigPath = Options.getDevicesConfigPath(getActivity());
        if (strDevicesConfigPath == null || strDevicesConfigPath.length() == 0)
            mTextConfigPath.setText(getString(R.string.hint_options_configpath_null));
        else
            mTextConfigPath.setText(strDevicesConfigPath);
        mTextMaximumFractionDigits.setText(Options.getMaximumFractionDigits(getActivity()) + "位");

        // click listener
        _rootView.findViewById(R.id.btn_change_MaximumFractionDigits)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        OnChangeMaximumFractionDigits();
                    }
                });
        mBtnChangeConfigPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//无类型限制
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, REQUESTCODE_CHANGE_CONFIGPATH);
            }
        });

    }


    // 点击改变小数点位数
    private void OnChangeMaximumFractionDigits() {
        // new number edittext
        final EditText editInput = new EditText(getContext());
        editInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        editInput.setText(Options.getMaximumFractionDigits(getContext()) + "");


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.title_options_MaximumFractionDigits)
                .setView(editInput).setCancelable(true)
                .setMessage(R.string.msg_options_MaximumFractionDigits)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String strInput = editInput.getText().toString();
                        int nMaximumFractionDigits = 0;
                        try {
                            nMaximumFractionDigits = Integer.valueOf(strInput).intValue();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        nMaximumFractionDigits = Options.SaveMaximumFractionDigits(getContext(), nMaximumFractionDigits);
                        mTextMaximumFractionDigits.setText(nMaximumFractionDigits + "位");
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(R.string.cancel, null)
                .show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUESTCODE_CHANGE_CONFIGPATH && resultCode == Activity.RESULT_OK) {
            String path;
            if (data == null) return;
            Uri uri = data.getData();
            if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                path = uri.getPath();
                mTextConfigPath.setText(path);
                Toast.makeText(getContext(), "GetConfigFile From TheThirdParty Tool:" + path, Toast.LENGTH_SHORT).show();
                //tv.setText(path);
                //Toast.makeText(this,path+"11111",Toast.LENGTH_SHORT).show();
                //return;
            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                path = getPath(getActivity(), uri);
                mTextConfigPath.setText(path);
                Toast.makeText(getContext(), "Android > 4.4:" + path, Toast.LENGTH_SHORT).show();
            } else {
                //4.4以下下系统调用方法
                path = getRealPathFromURI(uri);
                mTextConfigPath.setText(path);
                Toast.makeText(getContext(), "Android <= 4.4:" + path, Toast.LENGTH_SHORT).show();
            }
            if (path != null) {
                //save
                Options.SaveDevicesConfigPath(getActivity(), path);
                // 重启提示
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.hint_config_path_changesuccess_restart)
                        .setPositiveButton(R.string.ok, null)
                        .show();
            }
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {


        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;


        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];


                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {


                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));


                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
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
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (null != cursor && cursor.moveToFirst()) {
            ;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
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
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {


        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};


        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
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
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
