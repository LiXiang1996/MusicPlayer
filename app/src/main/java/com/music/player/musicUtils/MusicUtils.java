package com.music.player.musicUtils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import com.music.player.bean.Song;

import static android.support.v4.app.ActivityCompat.requestPermissions;
import static android.support.v4.content.ContextCompat.checkSelfPermission;

/**
 * 音乐工具类,
 */
public class MusicUtils {

    /**
     * 扫描系统里面的音频文件，返回一个list集合
     */

    public static List<Song> getMusicData(Context context) {
        List<Song> list = new ArrayList<>();
        final  int REQUEST_CODE_ASK_PERMISSIONS = 123;

//        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
//
//            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                int hasWriteContactsPermission = checkSelfPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE);
//                if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
//
//                    requestPermissions((Activity) context,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE_ASK_PERMISSIONS);
//                    return list;
//                }
//            }

            // 媒体库查询语句（写一个工具类MusicUtils）
//            Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
//                    null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, null, null,
                null,  MediaStore.Audio.AudioColumns.IS_MUSIC);
            //读取手机上的音乐
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Song Song = new Song();
                    Song.song = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                    Song.singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    Song.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    Song.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    Song.size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                    if (Song.size > 1000 * 800) {
                        // 注释部分是切割标题，分离出歌曲名和歌手 （本地媒体库读取的歌曲信息不规范）
                        if (Song.song.contains("---")) {
                            String[] str = Song.song.split(".");
                            Song.singer = str[0];
                            Song.song = str[1];
                        }
                        list.add(Song);
                    }
                }
                // 释放资源
                cursor.close();
            }

            return list;
        }
//        return list;
//    }

    /**
     * 定义一个方法用来格式化获取到的时间
     */
    public static String formatTime(long time) {
        if (time / 1000 % 60 < 10) {
            return time / 1000 / 60 + ":0" + time / 1000 % 60;
 
        } else {
            return time / 1000 / 60 + ":" + time / 1000 % 60;
        }
 
    }
}