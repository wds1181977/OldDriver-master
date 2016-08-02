package com.olddriver.data;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.search.AVSearchQuery;
import com.olddriver.data.api.dribbble.model.Images;
import com.olddriver.data.api.dribbble.model.Shot;
import com.olddriver.util.ImageUtils;

import java.util.Collections;
import java.util.List;


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

    public static void createOrUpdateShot(final String title,final String  author,final String  description, Uri uri, final SaveCallback saveCallback) {


        String name = System.currentTimeMillis()+"";
        if (uri != null) {
            byte[] data = ImageUtils.readFile(mContext,uri);
            final AVFile file = new AVFile(name, data);
            file.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e != null) {
                        Log.d("wds",e.toString());
                        saveCallback.done(e);
                    } else {
                        String url = file.getUrl();
                        saveShot(title,author,description, url, saveCallback);
                    }
                }
            });
        } else {
            saveShot(title,author,description, "", saveCallback);
        }
    }

    public static void saveShot(final String title,final String author,final String description,final String url, final SaveCallback saveCallback) {
        final Shot shot = new Shot();

        shot.setTitle(title);
        shot.setAuthor(author);
        shot.setDescription(description);
        shot.setImageURL(url);

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




}
