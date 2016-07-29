package com.olddriver.data;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVStatus;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.search.AVSearchQuery;
import com.olddriver.data.api.dribbble.model.Images;
import com.olddriver.data.api.dribbble.model.Shot;
import com.olddriver.data.api.dribbble.model.Todo;
import com.olddriver.ui.App;
import com.olddriver.util.ImageUtils;

import org.jsoup.nodes.Element;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        AVObject.registerSubclass(Todo.class);
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

    public static void fetchTodoById(String objectId,GetCallback<AVObject> getCallback) {
        Todo todo = new Todo();
        todo.setObjectId(objectId);
        // 通过Fetch获取content内容
        todo.fetchInBackground(getCallback);
    }

    public static void createOrUpdateTodo(final String text, Uri uri, final SaveCallback saveCallback) {


 //       if (bitmap != null) {
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
//            byte[] bs = out.toByteArray();
//
//            String name = System.currentTimeMillis()+"";
//            final AVFile file = new AVFile(name, bs);
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
                        sendStatus(text, url, saveCallback);
                    }
                }
            });
        } else {
            sendStatus(text, "", saveCallback);
        }
    }

    public static void sendStatus(final String text, final String url, final SaveCallback saveCallback) {
        final Shot todo = new Shot();

        todo.setContent(text);
        todo.setImageURL(url);

//        Map<String, Object> datas = new HashMap<String, Object>();
//        datas.put(App.DETAIL_ID, todo.getObjectId());
//        todo.setData(datas);
        // 异步保存
        todo.saveInBackground(saveCallback);
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


//    private static Shot parseShot(final String text, final String imgUrl) {
//
//
//        return new Shot.Builder()
//                .setId(1)
//                .setHtmlUrl(imgUrl)
//                .setTitle(text)
//                .setDescription(text)
//                .setImages(new Images(null, imgUrl, null))
//                .setAnimated(true)
//                .setCreatedAt(null)
//                .setLikesCount(1)
//                .setCommentsCount(1)
//                .setViewsCount(1)
//                .setUser(null)
//                .build();
//    }


}
