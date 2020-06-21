package com.example.thu_helper.ui.chatting;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.thu_helper.R;
import com.example.thu_helper.data.LoginRepository;
import com.example.thu_helper.data.Result;
import com.example.thu_helper.data.model.LoggedInUser;
import com.example.thu_helper.utils.Global;

import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Intent.getIntent;

public class ChatListViewAdapter extends ArrayAdapter<ChatMsgEntity> {

    private int resourceId;
    private LayoutInflater mInflater;
    private String head_portrait_right_name;
    private String head_portrait_left_name;
    private String other_id;
    ViewHolder viewHolder;
    LoggedInUser loggedInUser = LoginRepository.getInstance().getUser();
    public ChatListViewAdapter(Context context,int textViewresourceId, List<ChatMsgEntity> datas,String other_id){
        super(context, textViewresourceId, datas);
        resourceId = textViewresourceId;
        mInflater = LayoutInflater.from(context);
        this.other_id = other_id;
        new headLeftTask().execute();
        new headRightTask().execute();
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ChatMsgEntity msg = getItem(position);
        View view;

        if(convertView == null){
            view = mInflater.inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.leftLayout = view.findViewById(R.id.left_layout);
            viewHolder.rightLayout = view.findViewById(R.id.right_layout);
            viewHolder.msg_left = view.findViewById(R.id.chatContent_left);
            viewHolder.msg_right = view.findViewById(R.id.chatContent_right);
            viewHolder.sendTime_left = view.findViewById(R.id.sendTime_left);
            viewHolder.sendTime_right = view.findViewById(R.id.sendTime_right);
            viewHolder.username_left = view.findViewById(R.id.chatUsername_left);
            viewHolder.username_right = view.findViewById(R.id.chatUsername_right);
            viewHolder.userhead_left = view.findViewById(R.id.userhead_left);
            viewHolder.userhead_right = view.findViewById(R.id.userhead_right);
            view.setTag(viewHolder);
        }
        else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        switch (msg.getType()){
            case ChatMsgEntity.MSG_RECEIVED:
                viewHolder.leftLayout.setVisibility(View.VISIBLE);
                viewHolder.rightLayout.setVisibility(View.GONE);
                viewHolder.username_left.setText(msg.getName());
                viewHolder.sendTime_left.setText(msg.getDate());
                viewHolder.msg_left.setText(msg.getText());
                if(head_portrait_left_name != null && !head_portrait_left_name.equals("null")){
                    Glide.with(getContext()).load(Global.url_prefix + "/static/images/"+head_portrait_left_name)
                            .apply(RequestOptions.circleCropTransform())
                            .into(viewHolder.userhead_left);
                }
//                    new DownloadImageLeftTask().execute(Global.url_prefix + "/static/images/"+head_portrait_left_name);
                break;
            case ChatMsgEntity.MSG_SEND:
                viewHolder.leftLayout.setVisibility(View.GONE);
                viewHolder.rightLayout.setVisibility(View.VISIBLE);
                viewHolder.username_right.setText(msg.getName());
                viewHolder.sendTime_right.setText(msg.getDate());
                viewHolder.msg_right.setText(msg.getText());
                if(head_portrait_right_name != null && !head_portrait_right_name.equals("null")){
                    Glide.with(getContext()).load(loggedInUser.avater)
                            .apply(RequestOptions.circleCropTransform())
                            .into(viewHolder.userhead_right);
                }
//                    new DownloadImageRightTask().execute(Global.url_prefix + "/static/images/"+head_portrait_right_name);
                break;
        }

        return view;
    }

    static class ViewHolder{
        ImageView userhead_left;
        ImageView userhead_right;
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView sendTime_left;
        TextView sendTime_right;
        TextView username_left;
        TextView username_right;
        TextView msg_left;
        TextView msg_right;
    }

    private class headRightTask extends AsyncTask<Void,Void, Result<Boolean>>{

        @Override
        protected Result<Boolean> doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(Global.url_prefix + "/user/info")
                        .addHeader("Authorization", "Token " + loggedInUser.token)
                        .build();
                Response response = null;
                response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONObject res = null;
                    res = new JSONObject(response.body().string());
                    head_portrait_right_name = res.getString("head_portrait");
                    System.out.println(head_portrait_right_name);
                    System.out.println(res);
                    return new Result.Success<>(true);
                }
                return new Result.Error(new Exception("请求失败，请联系网站管理员"));
            } catch (Exception e) {
                return new Result.Error(new Exception("网络请求失败，请稍后重试", e));
            }
        }
    }

    private class headLeftTask extends AsyncTask<Void,Void, Result<Boolean>>{

        @Override
        protected Result<Boolean> doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(Global.url_prefix + "/user/other?ID=" + other_id)
                        .addHeader("Authorization", "Token " + loggedInUser.token)
                        .build();
                Response response = null;
                response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONObject res = null;
                    res = new JSONObject(response.body().string());
                    head_portrait_left_name = res.getString("head_portrait");
                    return new Result.Success<>(true);
                }
                return new Result.Error(new Exception("请求失败，请联系网站管理员"));
            } catch (Exception e) {
                return new Result.Error(new Exception("网络请求失败，请稍后重试", e));
            }
        }
    }

    private class DownloadImageRightTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            URL picUrl = null;
            try {
                picUrl = new URL(url);
                Bitmap pngBM = null;
                pngBM = BitmapFactory.decodeStream(picUrl.openStream());
                return pngBM;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println(e.getClass());
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Bitmap result) {
            viewHolder.userhead_right.setImageBitmap(result);
        }
    }

    private class DownloadImageLeftTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            URL picUrl = null;
            try {
                picUrl = new URL(url);
                Bitmap pngBM = null;
                pngBM = BitmapFactory.decodeStream(picUrl.openStream());
                return pngBM;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println(e.getClass());
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Bitmap result) {
            viewHolder.userhead_left.setImageBitmap(result);
        }
    }
}
