package com.example.thu_helper.ui.setting;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SettingFragment extends Fragment {
    private LoginRepository loginRepository = LoginRepository.getInstance();
    private QMUIGroupListView mGroupListView;
    private ProgressBar mProgressBar;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_setting, container, false);
        mGroupListView = root.findViewById(R.id.groupListView);
        mProgressBar = root.findViewById(R.id.progressBar);
        initSettingGroup();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initSettingGroup(){
        LoggedInUser loggedInUser = loginRepository.getUser();
        int paddingVer = QMUIDisplayHelper.dp2px(getContext(), 12);

        // Section 1
        QMUICommonListItemView itemUsername = mGroupListView.createItemView(
                null,
                "用户名",
                loggedInUser.username,
                QMUICommonListItemView.VERTICAL,
                QMUICommonListItemView.ACCESSORY_TYPE_NONE,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        itemUsername.setPadding(itemUsername.getPaddingLeft(), paddingVer,
                itemUsername.getPaddingRight(), paddingVer);

        QMUICommonListItemView itemEmail = mGroupListView.createItemView(
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
        QMUICommonListItemView itemNickname = mGroupListView.createItemView(
                null,
                "昵称",
                loggedInUser.nickname,
                QMUICommonListItemView.VERTICAL,
                QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        itemNickname.setPadding(itemNickname.getPaddingLeft(), paddingVer,
                itemNickname.getPaddingRight(), paddingVer);

        QMUICommonListItemView itemPhone = mGroupListView.createItemView(
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
        View.OnClickListener onClickListenerAvater = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "头像", Toast.LENGTH_SHORT).show();
            }
        };

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
            builder.setDefaultText(loginRepository.getUser().nickname);
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
            builder.setDefaultText(loginRepository.getUser().phone);
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

    private class EditTask extends AsyncTask<Pair<String, String>, Integer, Result<Boolean>> {

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
        protected Result<Boolean> doInBackground(Pair<String, String>... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                for(Pair<String, String> param : params){
                    formBodyBuilder = formBodyBuilder.add(param.first, param.second);
                }
                FormBody formBody = formBodyBuilder.build();
                Request request = new Request.Builder()
                        .url(Global.url_prefix + "/user/editinfo")
                        .addHeader("Authorization","Token " + loginRepository.getUser().token)
                        .post(formBody)
                        .build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return new Result.Success<>(true);
                }
                return new Result.Error(new IOException("Login Error"));
            } catch (Exception e) {
                return new Result.Error(new IOException("Error logging in", e));
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
        protected void onPostExecute(Result<Boolean> result) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (result instanceof Result.Success) {
                Toast.makeText(getActivity().getApplicationContext(), "修改成功", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), ((Result.Error) result).toString(), Toast.LENGTH_SHORT).show();
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