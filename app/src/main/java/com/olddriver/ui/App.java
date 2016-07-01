package com.olddriver.ui;

import android.app.Application;

import com.olddriver.data.AVService;

/**
 * @author Danny
 * @ClassName: XXXX
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AVService.AVInit(this);
    }
}
