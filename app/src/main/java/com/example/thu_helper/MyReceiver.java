package com.example.thu_helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.thu_helper.data.LoginRepository;
import com.example.thu_helper.data.Result;
import com.example.thu_helper.data.model.LoggedInUser;
import com.example.thu_helper.ui.MainActivity;
import com.example.thu_helper.ui.detail.DetailActivity;
import com.example.thu_helper.ui.home.Record;
import com.example.thu_helper.utils.Global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.NOTIFICATION_SERVICE;
import static java.lang.System.currentTimeMillis;

public class MyReceiver extends BroadcastReceiver {
    LoggedInUser loggedInUser;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.loggedInUser = LoginRepository.getInstance().getUser();
        this.context = context;

        if(intent(context)) {
            new GetTask().execute();
        }
    }

    private boolean intent(Context context) {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            Intent localIntent = new Intent();
            //判断API，跳转到应用通知管理页面
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0及以上
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
            } else {
                localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                localIntent.putExtra("app_package", context.getPackageName());
                localIntent.putExtra("app_uid", context.getApplicationInfo().uid);
            }
            context.startActivity(localIntent);
            return false;
        }
        return true;
    }

    private void show(Context context, String title, String msg, String id) {
        if (intent(context)) {
            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            Notification notification = null;

            Intent resultIntent = new Intent(context, DetailActivity.class);
            resultIntent.putExtra("id", id);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            if (Build.VERSION.SDK_INT >= 26) {
                NotificationChannel channel = new NotificationChannel("id", "name", NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel(channel);
                notification = new Notification.Builder(context, "id")
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(resultPendingIntent)
                        .build();
            } else {//API26以下
                notification = new Notification.Builder(context)
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(resultPendingIntent)
                        .build();
            }
            manager.notify((int)System.currentTimeMillis(), notification);
        }
    }

    private class GetTask extends AsyncTask<Void, Integer, Result<List<Record>>> {

        // 方法1：onPreExecute（）
        // 作用：执行 线程任务前的操作（UI线程）
        @Override
        protected void onPreExecute() {
        }

        // 方法2：doInBackground（）
        // 作用：接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果（子线程）
        // 此处通过计算从而模拟“加载进度”的情况
        @Override
        protected Result<List<Record>> doInBackground(Void... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(Global.url_prefix + "/activity/change_list")
                        .addHeader("Authorization","Token " + loggedInUser.token)
                        .build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONArray res = new JSONArray(response.body().string());
                    List<Record> records = parseRecords(res);
                    return new Result.Success<>(records);
                }
                return new Result.Error(new Exception("请求失败，请联系网站管理员"));
            } catch (Exception e) {
                return new Result.Error(new Exception("网络请求失败", e));
            }
        }

        private List<Record> parseRecords(JSONArray res) throws JSONException, ParseException {
            ArrayList<Record> records = new ArrayList<>();

            for(int i = 0; i < res.length(); i++){
                JSONObject obj = res.getJSONObject(i);
                Record record = new Record(obj);
                records.add(record);
            }

            return records;
        }

        // 方法3：onProgressUpdate（）
        // 作用：在主线程 显示线程任务执行的进度（UI线程）
        @Override
        protected void onProgressUpdate(Integer... progresses) {

        }

        // 方法4：onPostExecute（）
        // 作用：接收线程任务执行结果、将执行结果显示到UI组件（UI线程）
        @Override
        protected void onPostExecute(Result<List<Record>> result) {
            if (result instanceof Result.Success) {
                List<Record> records = ((Result.Success<List<Record>>) result).getData();
                for (Record record : records) {
                    if (record.state == 1) {
                        show(context, "您的订单有人接单啦", record.title, record.id);
                    }
                    else if (record.state == 2) {
                        show(context, "接单任务已完成", record.title, record.id);
                    }

                }
            } else {
                Toast.makeText(context.getApplicationContext(), ((Result.Error) result).getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        // 方法5：onCancelled()
        // 作用：将异步任务设置为：取消状态（UI线程）
        @Override
        protected void onCancelled() {
        }
    }
}
