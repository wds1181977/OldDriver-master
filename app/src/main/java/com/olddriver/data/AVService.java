package com.olddriver.data;
import android.content.Context;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.search.AVSearchQuery;
import com.olddriver.data.api.dribbble.UserDAO;
import com.olddriver.data.api.dribbble.model.Images;
import com.olddriver.data.api.dribbble.model.Shot;
import com.olddriver.util.ImageUtils;

import java.util.Collections;
import java.util.List;

import butterknife.OnClick;


/**
 * @author Danny
 * @ClassName: XXXX
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2016/07/01
 */

public class AVService {
   private static Context mContext;
    public static void AVInit(Context ctx) {
        // 注册子类
        AVObject.registerSubclass(Images.class);
        AVObject.registerSubclass(Shot.class);
        AVObject.registerSubclass(PlaidItem.class);
        AVOSCloud.setDebugLogEnabled(true);
        // 初始化应用 Id 和 应用 Key，您可以在应用设置菜单里找到这些信息
        AVOSCloud.initialize(ctx, "x7H9QGolRK3CWPY78NhwNoX1-gzGzoHsz",
                "QMaBTdqwLKzetgIG21dijLNA");
        // 启用崩溃错误报告
        AVAnalytics.enableCrashReport(ctx, true);
        AVOSCloud.setLastModifyEnabled(true);
        mContext=ctx;
    }


    public static void fetchShotById(String objectId,GetCallback<AVObject> getCallback) {
        Shot shot = new Shot();
        shot.setObjectId(objectId);
        // 通过Fetch获取content内容
        shot.fetchInBackground(getCallback);
    }

    public static void createOrUpdateShot(final String title,final String  githubUrl,final String  description, final String image_uri, final SaveCallback saveCallback) {


        String name = System.currentTimeMillis()+"";
        if (image_uri != null) {
            Uri uri=Uri.parse(image_uri);
            byte[] data = ImageUtils.readFile(mContext,uri);
            final AVFile file = new AVFile(name, data);
            file.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e != null) {
                        Log.d("wds",e.toString());
                        saveCallback.done(e);
                    } else {
                        String image_url = file.getUrl();
                        saveShot(title,githubUrl,description, image_url, saveCallback);
                    }
                }
            });
        } else {
            saveShot(title,githubUrl,description, "", saveCallback);
        }
    }

    public static void saveShot(final String title,final String githubUrl,final String description,final String image_url, final SaveCallback saveCallback) {
        final Shot shot = new Shot();

        shot.setTitle(title);
        shot.setGitHubUrl(githubUrl);
        shot.setDescription(description);
        shot.setImageURL(image_url);

//        Map<String, Object> datas = new HashMap<String, Object>();
//        datas.put(App.DETAIL_ID, shot.getObjectId());
//        shot.setData(datas);
        // 异步保存
        shot.saveInBackground(saveCallback);
    }

    public static List<Shot> findShots() {
        // 查询当前Todo列表
        AVQuery<Shot> query = AVQuery.getQuery(Shot.class);
        // 按照更新时间降序排序
        query.orderByDescending("updatedAt");
        // 最大返回1000条
        query.limit(1000);
        try {
            return query.find();
        } catch (AVException exception) {
            Log.e("tag", "Query todos failed.", exception);
            return Collections.emptyList();
        }
    }

    public static void searchQuery(String inputSearch) {
        AVSearchQuery searchQuery = new AVSearchQuery(inputSearch);
        searchQuery.search();
    }



    public static   void login(String username, String password  ) {

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            AVUser.logInInBackground(username, password, new LogInCallback<AVUser>() {
                @Override
                public void done(AVUser avUser, AVException e) {
//                    if (filterException(e)) {
//                        //onSucceed();
//                    }
                }
            });
        }
    }

    public static  void register(final String username,final String password,final String cityStr,final String githubStr,final Uri avatar_uri,final SaveCallback saveCallback) {
      final  AVUser user = new AVUser();


        String name = System.currentTimeMillis()+"";
        if (avatar_uri != null) {

            byte[] data = ImageUtils.readFile(mContext,avatar_uri);
            final AVFile file = new AVFile(name, data);

            file.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e != null) {
                        Log.d("wds",e.toString());

                    } else {
                        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {

                            user.setUsername(username);
                            user.setPassword(password);
                        }
                        user.put(UserDAO.LOCATION,cityStr);
                        user.put(UserDAO.GITHUB_URL,githubStr);
                        user.put(UserDAO.AVATRR_URL, file.getUrl());
                        user.saveInBackground(saveCallback);
                    }
                }
            });
        }else {

            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {

                user.setUsername(username);
                user.setPassword(password);

            }
            user.put(UserDAO.LOCATION,cityStr);
            user.put(UserDAO.GITHUB_URL,githubStr);
            user.saveInBackground(saveCallback);

        }



    }







}
