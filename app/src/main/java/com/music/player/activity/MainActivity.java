package com.music.player.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.music.player.APPContext;
import com.music.player.Config.Constants;
import com.music.player.R;
import com.music.player.adapter.MainMusicAdapter;
import com.music.player.base.BaseActivity;
import com.music.player.bean.Song;
import com.music.player.db.CollectDBHelper;
import com.music.player.musicUtils.MusicUtils;
import com.music.player.server.MusicServer;

import java.net.URI;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.music.player.R.id.activity_main_model_ll;

public class MainActivity extends BaseActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener,
        MainMusicAdapter.OnMusicItemListener {
    private ImageView mMainIconIv;
    private ImageView mExitIv;
    private TextView mMusicName;
    private TextView mMusicAuthor;
    private SeekBar mSeekBar;
    private TextView mTimeTv;
    private ImageView mLastIv;
    private ImageView mNextIv;
    private CheckBox mStateCb;
    private LinearLayout mCollectLl;
    private RecyclerView mRecycler;
    private MainMusicAdapter mAdapter;
    private List<Song> mSongs;
    private MediaPlayer mPlayer;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private MainHandler mHandler;
    private MainReceiver mMainReceiver;
    private LinearLayout mRefreshLl;
    private ImageView mRefreshIv;
    private LinearLayout mModelLl;
    private TextView mModelTv;
    private boolean isRefresh = false;
    private final int NEXT_SONG = 1;
    private final int UP_TIME = 2;
//    private Button button_login;
    private ImageView userimage;

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void findView() {
        userimage=(ImageView)findViewById(R.id.user_image) ;//用户头像
//        button_login=(Button)findViewById(R.id.set) ;//设置按钮
        mMainIconIv = (ImageView) findViewById(R.id.activity_main_iv);//黑胶音乐转动
        mExitIv = (ImageView) findViewById(R.id.activity_main_exit_iv);//退出
        mMusicName = (TextView) findViewById(R.id.activity_main_music_name);
        mMusicAuthor = (TextView) findViewById(R.id.activity_main_music_author);
        mSeekBar = (SeekBar) findViewById(R.id.activity_main_music_seek);
        mTimeTv = (TextView) findViewById(R.id.activity_main_music_time);

        mLastIv = (ImageView) findViewById(R.id.activity_main_last_iv);//上一曲
        mNextIv = (ImageView) findViewById(R.id.activity_main_next_iv);//下一曲
        mStateCb = (CheckBox) findViewById(R.id.activity_main_state_iv);//播放暂停
        mCollectLl = (LinearLayout) findViewById(R.id.activity_main_collect_ll);//收藏
        mRecycler = (RecyclerView) findViewById(R.id.activity_main_recycler);//列表
        mRefreshLl = (LinearLayout) findViewById(R.id.activity_main_refresh);
        mRefreshIv = (ImageView) findViewById(R.id.activity_main_refresh_iv);//刷新图标
        mModelLl = (LinearLayout) findViewById(activity_main_model_ll);
        mModelTv = (TextView) findViewById(R.id.activity_main_model_tv);//播放模式
    }

    @Override
    public void initView() {
        mPlayer = APPContext.getPlager();
        mStateCb.setChecked(mPlayer.isPlaying());//点击播放/暂停
    }

    private void setSongView(Song song) {
        //获取列表里的各项属性
        if (song != null) {
            mMusicName.setText(song.song);
            mMusicAuthor.setText(song.singer);
            mTimeTv.setText(MusicUtils.formatTime(song.duration));
        }
    }

    @Override
    public void initData() {
        mMainIconIv.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.rotate));//旋转
        mMainReceiver = new MainReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.CHANGE_MUSIC_UP_MUSIC_DATA);
        filter.addAction(Constants.CHANGE_PLAYED);
        registerReceiver(mMainReceiver, filter);//注册广播接收器
        mHandler = new MainHandler();

        mSongs = MusicUtils.getMusicData(mContext);
        Song song = APPContext.getInstance().getSong();

        mAdapter = new MainMusicAdapter(this, mSongs, this);
        mRecycler.setAdapter(mAdapter);
        mRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        setSongView(song);
        if (mPlayer.isPlaying()) {
            mSeekBar.setMax(song.duration);
            mSeekBar.setProgress(mPlayer.getCurrentPosition());
            recordProgress();
        }
    }
//添加动作监听器
    @Override
    public void initListener() {
        userimage.setOnClickListener(this);
//        button_login.setOnClickListener(this);
        mExitIv.setOnClickListener(this);
        mLastIv.setOnClickListener(this);
        mNextIv.setOnClickListener(this);
        mCollectLl.setOnClickListener(this);
        mRefreshLl.setOnClickListener(this);
        mModelLl.setOnClickListener(this);
        mStateCb.setOnCheckedChangeListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
    }

//上一首，下一首，暂停等动作的事件处理
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_main_last_iv:
                lastSong();//调用函数
                Log.i("msg","last_song");
                break;
            case R.id.activity_main_next_iv:
                nextSong();//调用函数
                Log.i("msg","next_song");
                break;
            case R.id.activity_main_exit_iv:
                stopService(new Intent(this, MusicServer.class));
                Log.i("msg","exit");
                System.exit(0);
                break;
            case R.id.activity_main_refresh:
                if (isRefresh) {
                    return;
                }
                isRefresh = true;
                mRefreshIv.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.rotate_500));//刷新图标旋转
                mHandler.postDelayed(new Runnable() {
                    @Override
                    //更新列表
                    public void run() {
                        mRefreshIv.clearAnimation();//
                        sendBroadcast(new Intent(Constants.CHANGE_ALL_LIST));
                        mSongs.clear();
                        mSongs.addAll(MusicUtils.getMusicData(mContext));
                        mAdapter.notifyDataSetChanged();
                        isRefresh = false;
                        Toast.makeText(mContext, "刷新成功", Toast.LENGTH_SHORT).show();
                    }
                }, 2000);
                break;
            case R.id.activity_main_collect_ll:
                startActivityForResult(new Intent(this, CollectActivity.class), 1);
                break;
            case R.id.activity_main_model_ll:
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://music.163.com/"));
                startActivity(intent);
                break;
//            case R.id.set:
//                Intent intent1=new Intent(MainActivity.this,Login.class);
//                startActivity(intent1);
            case R.id.user_image:
                Intent intent2 =new Intent(MainActivity.this,User_Data.class);
                startActivity(intent2);
        }
    }

    /**
     * 上一首
     */
    private void lastSong() {
        if (mSongs.size() > 0) {
            Song song = APPContext.getInstance().getSong();
            int lastPosition = -1;
            if (song != null) {
                for (Song so : mSongs) {
                    lastPosition++;
                    if (so.path.equals(song.path)) {
                        if (lastPosition == 0) {
                            lastPosition = mSongs.size() - 1;
                        } else {
                            lastPosition--;
                        }
                        break;
                    }
                }
            } else {
                lastPosition++;
            }
            Song lastSong = mSongs.get(lastPosition);
            sendBroadcastSong(lastSong);
        }
    }

    /**
     * 下一首
     */
    private void nextSong() {
        if (mSongs.size() > 0) {
            Song song = APPContext.getInstance().getSong();
            int lastPosition = -1;
            if (song != null) {
                for (Song so : mSongs) {
                    lastPosition++;
                    if (so.path.equals(song.path)) {
                        if (lastPosition == mSongs.size() - 1) {
                            lastPosition = 0;
                        } else {
                            lastPosition++;
                        }
                        break;
                    }
                }
            } else {
                lastPosition++;
            }
            Song lastSong = mSongs.get(lastPosition);
            sendBroadcastSong(lastSong);
        }
    }
//更改播放信息
    private void sendBroadcastSong(Song song) {
        sendBroadcast(new Intent(Constants.CHANGE_ALL_LIST));
        Intent intent = new Intent(Constants.CHANGE_MUSIC_SONG);
        intent.putExtra("song", song);
        sendBroadcast(intent);
        APPContext.getInstance().setSong(song);
        setSongView(song);
        mSeekBar.setMax(song.duration);
        mSeekBar.setProgress(0);
        mAdapter.notifyDataSetChanged();
        mStateCb.setChecked(true);
        recordProgress();
    }
//播放暂停
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (mSongs.size() > 0) {
                Song song = APPContext.getInstance().getSong();
                if (song != null) {
                    mPlayer.start();
                    recordProgress();
                } else {
                    song = mSongs.get(0);
                    sendBroadcastSong(song);
                }
            }
        } else {
            cancelTimer();
            mPlayer.pause();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mPlayer.seekTo(seekBar.getProgress());
        mHandler.sendEmptyMessage(UP_TIME);
    }
//音乐点击
    @Override
    public void onMusicClick(int position, Song song) {
        Song oldSong = APPContext.getInstance().getSong();
        if (oldSong == null || !oldSong.path.equals(song.path)) {
            sendBroadcastSong(song);
        }
    }
//收藏点击
    @Override
    public void onCollectClick(int position, Song song, boolean isCheck) {
        if (isCheck) {
            CollectDBHelper.getInstance(mContext).insertSong(song);//添加到收藏解表
        } else {
            CollectDBHelper.getInstance(mContext).deleteSong(song);//在列表里删除掉
        }
    }

    private void recordProgress() {
        cancelTimer();
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(UP_TIME);
            }
        };
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    private void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
        }
    }

    private class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UP_TIME:
                    Song song = APPContext.getInstance().getSong();
                    mSeekBar.setProgress(mPlayer.getCurrentPosition());
//                    mTimeTv.setText(MusicUtils.formatTime(song.duration - mPlayer.getCurrentPosition()));//设置进度条显示剩余时间
                    mTimeTv.setText(MusicUtils.formatTime(mPlayer.getCurrentPosition()));//设置进度条显示时间
                    break;
            }

        }
    }
//更新音乐列表
    private class MainReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.CHANGE_MUSIC_UP_MUSIC_DATA.equals(intent.getAction())) {
                Song song = APPContext.getInstance().getSong();
                setSongView(song);
                mStateCb.setChecked(true);
                mAdapter.notifyDataSetChanged();
                recordProgress();
            } else if (Constants.CHANGE_PLAYED.equals(intent.getAction())) {
                mStateCb.setChecked(false);
            }
        }
    }
//当子Activity返回时回返回一些子模块的数据交给主activity处理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Song song = APPContext.getInstance().getSong();
        setSongView(song);
        if (mPlayer.isPlaying()) {
            mStateCb.setChecked(true);
            mSeekBar.setMax(mPlayer.getDuration());
            mSeekBar.setProgress(mPlayer.getCurrentPosition());
            recordProgress();
        }
        mAdapter.notifyDataSetChanged();
    }
//注册掉接收器
    @Override
    protected void onDestroy() {
        unregisterReceiver(mMainReceiver);
        super.onDestroy();
    }

}
