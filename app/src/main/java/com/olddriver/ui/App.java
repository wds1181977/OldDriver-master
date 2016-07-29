package com.olddriver.ui;

import android.app.Application;

import com.olddriver.data.AVService;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

/**
 * @author Danny
 * @ClassName: XXXX
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date
 */
public class App extends Application {
    public static final String DETAIL_ID = "detailId";
    @Override
    public void onCreate() {
        super.onCreate();
        Logger.init("OD");
        AVService.AVInit(this);
    }
}
