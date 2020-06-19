package com.example.thu_helper.ui.setting;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thu_helper.BR;
import com.example.thu_helper.R;
import com.example.thu_helper.data.LoginRepository;
import com.example.thu_helper.data.Result;
import com.example.thu_helper.data.model.LoggedInUser;
import com.example.thu_helper.utils.Global;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SettingFragment extends Fragment {
    private QMUIGroupListView mGroupListView;
    private ProgressBar mProgressBar;
    private LoggedInUser loggedInUser;

    private QMUICommonListItemView itemUsername;
    private QMUICommonListItemView itemEmail;
    private QMUICommonListItemView itemNickname;
    private QMUICommonListItemView itemPhone;

    private static final int RC_CHOOSE_PHOTO = 0;


    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_setting, container, false);
        loggedInUser = LoginRepository.getInstance().getUser();
        mGroupListView = root.findViewById(R.id.groupListView);
        mProgressBar = root.findViewById(R.id.progressBar);
        initSettingGroup();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void updateData(){
        itemUsername.setDetailText(loggedInUser.username);
        itemEmail.setDetailText(loggedInUser.email);
        itemNickname.setDetailText(loggedInUser.nickname);
        itemPhone.setDetailText(loggedInUser.phone);
    }

    private void initSettingGroup(){
        int paddingVer = QMUIDisplayHelper.dp2px(getContext(), 12);

        // Section 1
        itemUsername = mGroupListView.createItemView(
                null,
                "用户名",
                loggedInUser.username,
                QMUICommonListItemView.VERTICAL,
                QMUICommonListItemView.ACCESSORY_TYPE_NONE,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        itemUsername.setPadding(itemUsername.getPaddingLeft(), paddingVer,
                itemUsername.getPaddingRight(), paddingVer);

        itemEmail = mGroupListView.createItemView(
                null,
                "电子邮箱",
                loggedInUser.email,
                QMUICommonListItemView.VERTICAL,
                QMUICommonListItemView.ACCESSORY_TYPE_NONE,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        itemEmail.setPadding(itemEmail.getPaddingLeft(), paddingVer,
                itemEmail.getPaddingRight(), paddingVer);

        int size = QMUIDisplayHelper.dp2px(getContext(), 20);
        QMUIGroupListView.newSection(getContext())
                .setTitle("身份信息")
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
                .addItemView(itemUsername, null)
                .addItemView(itemEmail, null)
                .setMiddleSeparatorInset(QMUIDisplayHelper.dp2px(getContext(), 16), 0)
                .addTo(mGroupListView);


        // Section 2
        itemNickname = mGroupListView.createItemView(
                null,
                "昵称",
                loggedInUser.nickname,
                QMUICommonListItemView.VERTICAL,
                QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        itemNickname.setPadding(itemNickname.getPaddingLeft(), paddingVer,
                itemNickname.getPaddingRight(), paddingVer);

        itemPhone = mGroupListView.createItemView(
                null,
                "电话号码",
                loggedInUser.phone,
                QMUICommonListItemView.VERTICAL,
                QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        itemPhone.setPadding(itemPhone.getPaddingLeft(), paddingVer,
                itemPhone.getPaddingRight(), paddingVer);

        QMUICommonListItemView itemAvater = mGroupListView.createItemView("设置头像");
        itemAvater.setOrientation(QMUICommonListItemView.HORIZONTAL);
        itemAvater.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUIGroupListView.newSection(getContext())
                .setTitle("个人资料")
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
                .addItemView(itemNickname, onClickListenerNickname)
                .addItemView(itemPhone, onClickListenerPhone)
                .addItemView(itemAvater, onClickListenerAvater)
                .setMiddleSeparatorInset(QMUIDisplayHelper.dp2px(getContext(), 16), 0)
                .addTo(mGroupListView);
    }

    private View.OnClickListener onClickListenerNickname = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getContext());
            builder.setTitle("请输入新昵称");
            builder.setDefaultText(loggedInUser.nickname);
            builder.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.addAction("取消", new QMUIDialogAction.ActionListener() {
                @Override
                public void onClick(QMUIDialog dialog, int index) {
                    dialog.dismiss();
                }
            });

            builder.addAction("确定", new QMUIDialogAction.ActionListener() {
                @Override
                public void onClick(QMUIDialog dialog, int index) {
                    String nickname = builder.getEditText().getText().toString();
                    Pair<String, String> param = new Pair<>("nickname", nickname);
                    new EditTask().execute(param);
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    };

    private View.OnClickListener onClickListenerPhone = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getContext());
            builder.setTitle("请输入新电话号码");
            builder.setDefaultText(loggedInUser.phone);
            builder.setInputType(InputType.TYPE_CLASS_PHONE);
            builder.addAction("取消", new QMUIDialogAction.ActionListener() {
                @Override
                public void onClick(QMUIDialog dialog, int index) {
                    dialog.dismiss();
                }
            });

            builder.addAction("确定", new QMUIDialogAction.ActionListener() {
                @Override
                public void onClick(QMUIDialog dialog, int index) {
                    String phone = builder.getEditText().getText().toString();
                    Pair<String, String> param = new Pair<>("phone", phone);
                    new EditTask().execute(param);
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    };

    private View.OnClickListener onClickListenerAvater = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RC_CHOOSE_PHOTO);
            } else {
                choosePhoto();
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RC_CHOOSE_PHOTO:   //相册选择照片权限申请返回
                choosePhoto();
                break;
        }
    }

    private void choosePhoto() {
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentToPickPic, RC_CHOOSE_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_CHOOSE_PHOTO:
                if (data != null){
                    Uri uri = data.getData();
                    // 通过ContentProvider查询文件路径
                    ContentResolver resolver = getActivity().getContentResolver();
                    Cursor cursor = resolver.query(uri, null, null, null, null);
                    String path = null;
                    if (cursor == null) {
                        // 未查询到，说明为普通文件，可直接通过URI获取文件路径
                        path = uri.getPath();
                    }
                    else if (cursor.moveToFirst()) {
                        // 多媒体文件，从数据库中获取文件的真实路径
                        path = cursor.getString(cursor.getColumnIndex("_data"));
                    }
                    cursor.close();
                    new EditTask().execute(new Pair<>("head_portrait", path));
                }
                break;
        }
    }

    private class EditTask extends AsyncTask<Pair<String, String>, Integer, Result<JSONObject>> {

        // 方法1：onPreExecute（）
        // 作用：执行 线程任务前的操作（UI线程）
        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        // 方法2：doInBackground（）
        // 作用：接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果（子线程）
        // 此处通过计算从而模拟“加载进度”的情况
        @Override
        protected Result<JSONObject> doInBackground(Pair<String, String>... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);
                for(Pair<String, String> param : params){
                    if(param.first.equals("head_portrait")){
                        File file = new File(param.second);
                        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
                        multipartBodyBuilder = multipartBodyBuilder.addFormDataPart(param.first, file.getName(), fileBody);
                    }
                    else{
                        multipartBodyBuilder = multipartBodyBuilder.addFormDataPart(param.first, param.second);
                    }
                }
                MultipartBody multipartBody = multipartBodyBuilder.build();
//                FormBody.Builder formBodyBuilder = new FormBody.Builder();
//                for(Pair<String, String> param : params){
//                    formBodyBuilder = formBodyBuilder.add(param.first, param.second);
//                }
//                FormBody formBody = formBodyBuilder.build();
                Request request = new Request.Builder()
                        .url(Global.url_prefix + "/user/editinfo")
                        .addHeader("Authorization","Token " + loggedInUser.token)
                        .post(multipartBody)
                        .build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONObject res = new JSONObject(response.body().string());
                    loggedInUser.update(res);
                    return new Result.Success<>(res);
                }
                return new Result.Error(new Exception("修改失败，请检查填写内容后重试"));
            } catch (Exception e) {
                return new Result.Error(new Exception("网络请求失败，请稍后再试", e));
            }
        }

        // 方法3：onProgressUpdate（）
        // 作用：在主线程 显示线程任务执行的进度（UI线程）
        @Override
        protected void onProgressUpdate(Integer... progresses) {
        }

        // 方法4：onPostExecute（）
        // 作用：接收线程任务执行结果、将执行结果显示到UI组件（UI线程）
        @Override
        protected void onPostExecute(Result<JSONObject> result) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (result instanceof Result.Success) {
                updateData();
                Toast.makeText(getActivity().getApplicationContext(), "修改成功", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), ((Result.Error) result).getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        // 方法5：onCancelled()
        // 作用：将异步任务设置为：取消状态（UI线程）
        @Override
        protected void onCancelled() {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

}