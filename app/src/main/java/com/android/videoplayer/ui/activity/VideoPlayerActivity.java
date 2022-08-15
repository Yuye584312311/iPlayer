package com.android.videoplayer.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.android.iplayer.base.AbstractMediaPlayer;
import com.android.iplayer.base.BaseController;
import com.android.iplayer.controller.VideoController;
import com.android.videoplayer.video.listener.OnMenuActionListener;
import com.android.iplayer.listener.OnPlayerEventListener;
import com.android.iplayer.widget.VideoPlayer;
import com.android.iplayer.model.PlayerState;
import com.android.videoplayer.R;
import com.android.videoplayer.base.BaseActivity;
import com.android.videoplayer.base.BasePresenter;
import com.android.videoplayer.controller.DanmuController;
import com.android.videoplayer.media.ExoMediaPlayer;
import com.android.videoplayer.media.JkMediaPlayer;
import com.android.videoplayer.ui.widget.TitleView;
import com.android.videoplayer.utils.DataFactory;
import com.android.videoplayer.utils.Logger;
import com.android.videoplayer.video.ui.widget.PlayerMenuView;

/**
 * created by hty
 * 2022/6/22
 * Desc:这是一个支持带弹幕控制的常规视频播放器控件封装的示例
 */
public class VideoPlayerActivity extends BaseActivity {

    private boolean mDanmu;//是否启用弹幕
    private boolean mIslive;//是否播放直播流
    private DanmuController mDanmuController;
    private PlayerMenuView mMenuView;
    private int mediaCore;//多媒体解码器 0:系统默认 1:ijk 2:exo
    private VideoController mController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        TitleView titleView = (TitleView) findViewById(R.id.title_view);
        titleView.setTitle(getIntent().getStringExtra("title"));
        titleView.setOnTitleActionListener(new TitleView.OnTitleActionListener() {
            @Override
            public void onBack() {
                onBackPressed();
            }
        });
        mDanmu = getIntent().getBooleanExtra("danmu",false);
        mIslive = getIntent().getBooleanExtra("islive",false);
        initPlayer();
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    /**
     * 播放器初始化及调用示例
     */
    private void initPlayer() {
        mVideoPlayer = (VideoPlayer) findViewById(R.id.video_player);
        findViewById(R.id.player_container).getLayoutParams().height= getResources().getDisplayMetrics().widthPixels * 9 /16;//给播放器固定一个高度
        //绑定控制器
        mController = new VideoController(mVideoPlayer.getContext());
        mController.showBackBtn(false);//竖屏下是否显示返回按钮
        mController.showMenus(true,true,true);//是否显示右上角菜单栏功能按钮
        mController.showSoundMute(true,false);//启用静音功能交互\默认不静音
        //设置交互监听
        mController.setOnControllerListener(new BaseController.OnControllerEventListener() {

            //菜单按钮交给控制器内部处理
            @Override
            public void onMenu() {
                showMenuDialog();
            }

            //竖屏的返回事件
            @Override
            public void onBack() {
                Logger.d(TAG,"onBack");
                onBackPressed();
            }

            //开启全局悬浮窗
            @Override
            public void onGobalWindow() {
                startGoableWindow(null);
            }

            @Override
            public void onCompletion() {//试播结束或播放完成
                Logger.d(TAG,"onCompletion");
            }

            //播放器是否被静音了
            @Override
            public void onMute(boolean mute) {
                if(null!=mMenuView) mMenuView.updateMute(mute);
            }
        });
        //controller.setPreViewTotalDuration("3600");//注意:设置虚拟总时长(一旦设置播放器内部走片段试看流程)
        //绑定UI控制器
        mVideoPlayer.setController(mController);

        //弹幕控制器处理
        if(mDanmu){
            mDanmuController = new DanmuController(mController.getContext());
            mController.addController(0,mDanmuController);
            Switch aSwitch = (Switch) findViewById(R.id.switch_danmu);
            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(null!=mDanmuController){
                        if(isChecked){
                            mDanmuController.openDanmu();
                            ((TextView) findViewById(R.id.tv_danmu)).setText("关闭弹幕");
                        }else{
                            mDanmuController.closeDanmu();
                            ((TextView) findViewById(R.id.tv_danmu)).setText("开启弹幕");
                        }
                    }
                }
            });
            findViewById(R.id.danmu_content).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_send_danmu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null!=mDanmuController){
                        mDanmuController.addDanmuItem("这是我发的有颜色的弹幕！",true);
                    }
                }
            });
            mDanmuController.setDanmuData(DataFactory.getInstance().getDanmus());//添加弹幕数据
        }else{
            //功能设置监听
            findViewById(R.id.controller_content).setVisibility(View.VISIBLE);
            mMenuView = (PlayerMenuView) findViewById(R.id.menu_view);
            mMenuView.setOnMenuActionListener(new OnMenuActionListener() {
                @Override
                public void onSpeed(float speed) {
                    if(null!=mVideoPlayer) mVideoPlayer.setSpeed(speed);
                }

                @Override
                public void onZoom(int zoomModel) {
                    if(null!=mVideoPlayer) mVideoPlayer.setZoomModel(zoomModel);
                }

                @Override
                public void onScale(int scale) {

                }

                @Override
                public void onMute(boolean mute) {
                    if(null!=mVideoPlayer) mVideoPlayer.setSoundMute(mute);
                    if(null!=mController) mController.updateMute();
                }

                @Override
                public void onMirror(boolean mirror) {
                    if(null!=mVideoPlayer) mVideoPlayer.setMirror(mirror);
                }
            });
            View btnCore1 = findViewById(R.id.btn_core_1);
            btnCore1.setSelected(true);
            //解码器切换监听
            btnCore1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    findViewById(R.id.btn_core_3).setSelected(false);
                    findViewById(R.id.btn_core_2).setSelected(false);
                    findViewById(R.id.btn_core_1).setSelected(true);
                    mediaCore =0;
                    rePlay(null);
                }
            });
            findViewById(R.id.btn_core_2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    findViewById(R.id.btn_core_1).setSelected(false);
                    findViewById(R.id.btn_core_3).setSelected(false);
                    findViewById(R.id.btn_core_2).setSelected(true);
                    mediaCore =1;
                    rePlay(null);
                }
            });
            findViewById(R.id.btn_core_3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    findViewById(R.id.btn_core_1).setSelected(false);
                    findViewById(R.id.btn_core_2).setSelected(false);
                    findViewById(R.id.btn_core_3).setSelected(true);
                    mediaCore =2;
                    rePlay(null);
                }
            });
            //竖屏模式下的手势交互开关监听
            if(null!=mController) mController.setCanTouchInPortrait(true);//竖屏状态下是否开启手势交互
            View touch_1 = findViewById(R.id.touch_1);
            touch_1.setSelected(true);
            touch_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    findViewById(R.id.touch_1).setSelected(true);
                    findViewById(R.id.touch_2).setSelected(false);
                    if(null!=mController) mController.setCanTouchInPortrait(true);
                }
            });
            findViewById(R.id.touch_2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    findViewById(R.id.touch_1).setSelected(false);
                    findViewById(R.id.touch_2).setSelected(true);
                    if(null!=mController) mController.setCanTouchInPortrait(false);
                }
            });

            findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText editText = (EditText) findViewById(R.id.input);
                    String url = editText.getText().toString().trim();
                    if(TextUtils.isEmpty(url)){
                        Toast.makeText(getApplicationContext(),"请粘贴或输入播放地址后再播放!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    rePlay(url);
                }
            });
        }
        //如果适用自定义解码器则必须实现setOnPlayerActionListener并返回一个多媒体解码器
        mVideoPlayer.setOnPlayerActionListener(new OnPlayerEventListener() {
            /**
             * 创建一个自定义的播放器,返回null,则内部自动创建一个默认的解码器
             * @return
             */
            @Override
            public AbstractMediaPlayer createMediaPlayer() {
                if(1==mediaCore){
                    return new JkMediaPlayer(VideoPlayerActivity.this);//IJK解码器
                }else if(2==mediaCore){
                    return new ExoMediaPlayer(VideoPlayerActivity.this);//EXO解码器
                }else{
                    return null;//播放器内部默认的
                }
            }

            @Override
            public void onPlayerState(PlayerState state, String message) {
                Logger.d(TAG,"onPlayerState-->state:"+state+",message:"+message);
                if(state==PlayerState.STATE_COMPLETION||state==PlayerState.STATE_RESET||state==PlayerState.STATE_STOP){
                    if(null!=mMenuView) mMenuView.onReset();//播放完成后重置功能设置
                    if(null!=mMenuDialog) mMenuDialog.onReset();
                }
            }
        });
        mVideoPlayer.setLoop(mDanmu);
        mVideoPlayer.setProgressCallBackSpaceMilliss(300);
        mVideoPlayer.setTitle("测试播放地址");//视频标题(默认视图控制器横屏可见)
        mVideoPlayer.setDataSource(mIslive?M3U8:URL1);//播放地址设置
        mVideoPlayer.playOrPause();//开始异步准备播放
    }

    /**
     * 重新播放
     * @param url
     */
    private void rePlay(String url) {
        if(null!=mVideoPlayer){
            mVideoPlayer.onReset();
            mVideoPlayer.setLoop(mDanmu);
            mVideoPlayer.setProgressCallBackSpaceMilliss(300);
            mVideoPlayer.setTitle("测试播放地址");//视频标题(默认视图控制器横屏可见)
            mVideoPlayer.setDataSource(!TextUtils.isEmpty(url)?url:mIslive?M3U8:URL1);//播放地址设置 URL4惊奇队长
            mVideoPlayer.playOrPause();//开始异步准备播放
        }
    }
}