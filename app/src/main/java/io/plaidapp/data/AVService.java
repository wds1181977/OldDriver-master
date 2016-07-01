package io.plaidapp.data;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.search.AVSearchQuery;

import java.util.Collections;
import java.util.List;



/**
 * @author Danny
 * @ClassName: XXXX
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2016/07/01
 */

public class AVService {
    public static void AVInit(Context ctx) {
        // 注册子类
        AVObject.registerSubclass(Todo.class);
        AVOSCloud.setDebugLogEnabled(true);
        // 初始化应用 Id 和 应用 Key，您可以在应用设置菜单里找到这些信息
        AVOSCloud.initialize(ctx, "x7H9QGolRK3CWPY78NhwNoX1-gzGzoHsz",
                "QMaBTdqwLKzetgIG21dijLNA");
        // 启用崩溃错误报告
        AVAnalytics.enableCrashReport(ctx, true);
        AVOSCloud.setLastModifyEnabled(true);
    }

    public static void fetchTodoById(String objectId,GetCallback<AVObject> getCallback) {
        Todo todo = new Todo();
        todo.setObjectId(objectId);
        // 通过Fetch获取content内容
        todo.fetchInBackground(getCallback);
    }

    public static void createOrUpdateTodo(String objectId, String content, SaveCallback saveCallback) {
        final Todo todo = new Todo();
        if (!TextUtils.isEmpty(objectId)) {
            // 如果存在objectId，保存会变成更新操作。
            todo.setObjectId(objectId);
        }
        todo.setContent(content);
        // 异步保存
        todo.saveInBackground(saveCallback);
    }

    public static List<Todo> findTodos() {
        // 查询当前Todo列表
        AVQuery<Todo> query = AVQuery.getQuery(Todo.class);
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
