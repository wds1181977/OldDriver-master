/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.olddriver.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;

import java.io.Closeable;
import java.io.File;
import java.io.RandomAccessFile;

/**
 * Utility methods for working with images.
 */
public class ImageUtils {

    private ImageUtils() { }

    public static Bitmap vectorToBitmap(Context context, Drawable vector) {
        final Bitmap bitmap = Bitmap.createBitmap(vector.getIntrinsicWidth(),
                vector.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        vector.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vector.draw(canvas);
        return bitmap;
    }

    public static Bitmap vectorToBitmap(Context context, @DrawableRes int vectorDrawableId) {
        return vectorToBitmap(context, context.getDrawable(vectorDrawableId));
    }

    public static byte[] readFile(Context mContext,Uri uri) {
        File file=null;
        file= new File(getRealPathFromURI(mContext,uri));
        RandomAccessFile rf = null;
        byte[] data = null;
        try {
            rf = new RandomAccessFile(file, "r");
            data = new byte[(int) rf.length()];
            rf.readFully(data);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            closeQuietly(rf);
        }
        return data;
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    public static String getRealPathFromURI(Context mContext,Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor =mContext. getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

}
