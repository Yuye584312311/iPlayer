# **iPlayer**

#### 一、SDK基础功能
* 支持网络地址、直播流、本地Assets和Raw等音视频资源文件播放</br>
* 支持播放倍速、缩放模式、静音、镜像等功能设置</br>
* 支持自定义视频解码器、控制器、UI交互组件、视频画面渲染器</br>
* 支持对SDK默认控制器进行局部交互组件自定义</br>
* 支持重力感应横竖屏旋转及开关设置</br>
* 支持多播放器同时播放、任意位置直接启动全屏播放</br>
* 支持任意位置启动Window悬浮和全局悬浮窗口播放、支持靠边吸附悬停</br>
* Demo：MediaPlayer、IjkPlayer、ExoPlayer三种解码器切换</br>
* Demo：列表或组件之间无缝转场播放、全局悬浮窗口转场播放</br>
* Demo：仿抖音，支持视频缓存、秒播</br>
* Demo：简单的弹幕交互</br>
* Demo：Android8.0+画中画示例</br>

#### 二、[历史版本][1]
#### 三、SDK集成
* 建议集成前先[下载apk][2]体验，找到自己想要实现的功能模块，后续集成可参考demo源码。
##### 1、项目根build.gradle配置</br>
```
    allprojects {
        repositories {
            maven { url 'https://jitpack.io' }
        }
    }
```
##### 2、模块build.gradle配置</br>
```
    dependencies {
        //播放器(无UI交互)
        implementation 'com.github.hty527.iPlayer:iplayer:2.1.26.1'
        //SDK默认UI交互组件
        implementation 'com.github.hty527.iPlayer:widget:2.1.26.1'
    }
```
* 更多解码器、缓存等全量SDK请阅读[全量SDK][6]
##### 3、在需要播放视频的xml中添加如下代码,或在适合的位置new VideoPlayer()</br>
```
    <com.android.iplayer.widget.VideoPlayer
        android:id="@+id/video_player"
        android:layout_width="match_parent"
        android:layout_height="200dp"/>
```
##### 4、播放器准备及开始播放</br>
```
    mVideoPlayer = (VideoPlayer) findViewById(R.id.video_player);
    mVideoPlayer.getLayoutParams().height= getResources().getDisplayMetrics().widthPixels * 9 /16;//固定播放器高度，或高度设置为:match_parent
    //使用SDK自带控制器+各UI交互组件
    VideoController controller = VideoPlayer.initController();//初始化SDK默认控制器
    //将UI交互组件绑定到控制器(需集成：implementation 'com.github.hty527.iPlayer:widget:lastversion')
    WidgetFactory.bindDefaultControls(controller);
    controller.setTitle("测试地址播放");//视频标题(仅横屏状态可见)
    //设置播放源
    mVideoPlayer.setDataSource("https://upload.dongfeng-nissan.com.cn/nissan/video/202204/4cfde6f0-bf80-11ec-95c3-214c38efbbc8.mp4");
    //异步开始准备播放
    mVideoPlayer.prepareAsync();
```
* 更多控制器及播放器设置请阅读[常用控制器及播放器API][7]
##### 5、生命周期处理</br>
```
    @Override
    protected void onResume() {
        super.onResume();
        mVideoPlayer.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoPlayer.onPause();
    }

    @Override
    public void onBackPressed() {
        if(mVideoPlayer.isBackPressed()){
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoPlayer.onDestroy();
    }
```
##### 6、常用API、更换解码器、自定义解码器、UI交互组件和悬浮窗口播放等功能请阅读[wiki][3]</br>
#### 四、遇到问题
* 1、阅读接入文档[wiki][3]
* 2、提交[issues][4]
* 3、联系作者：584312311@qq.com
* 4、项目无法下载或下载缓慢请至[码云][5]下载
* 5、[播放器框架结构图][8]
#### 五、SDK及Demo部分功能预览
<div align="center">
    <img src="https://amuse-1259486925.cos.ap-hongkong.myqcloud.com/image/doc1.jpg?q=1" width="270",height="585">
    <img src="https://amuse-1259486925.cos.ap-hongkong.myqcloud.com/image/doc2.jpg?q=2" width="270",height="585">
</div>
<div align="center">
    <img src="https://amuse-1259486925.cos.ap-hongkong.myqcloud.com/image/doc3.jpg?q=3" width="270",height="585">
    <img src="https://amuse-1259486925.cos.ap-hongkong.myqcloud.com/image/doc4.jpg?q=4" width="270",height="585">
</div>
<div align="center">
    <img src="https://amuse-1259486925.cos.ap-hongkong.myqcloud.com/image/doc5.jpg?q=5" width="270",height="585">
    <img src="https://amuse-1259486925.cos.ap-hongkong.myqcloud.com/image/doc6.jpg?q=6" width="270",height="585">
</div>
<div align="center">
    <img src="https://amuse-1259486925.cos.ap-hongkong.myqcloud.com/image/doc7.jpg?q=7" width="270",height="585">
    <img src="https://amuse-1259486925.cos.ap-hongkong.myqcloud.com/image/doc8.jpg?q=8" width="270",height="585">
</div>

#### 六、鸣谢
* [ijkplayer][9]
* [ExoPlayer][10]
* [AndroidVideoCache][11]
* Demo演示视频列表模块api使用的是开眼api，本项目只做学习使用。禁止任何人应用于任何商业用途，由此带来的法律风险由应用于商业用途的一方承担！

[1]:https://github.com/hty527/iPlayer/wiki/Version "历史版本"
[2]:https://amuse-1259486925.cos.ap-hongkong.myqcloud.com/apk/iPlayer-2.1.26.apk?version=2.1.26 "下载apk"
[3]:https://github.com/hty527/iPlayer/wiki "wiki"
[4]:https://github.com/hty527/iPlayer/issues "issues"
[5]:https://gitee.com/hty527/iPlayer "码云"
[6]:https://github.com/hty527/iPlayer/wiki#全量SDK "全量SDK"
[7]:https://github.com/hty527/iPlayer/wiki#一常用api使用说明 "常用控制器及播放器API"
[8]:https://github.com/hty527/iPlayer/wiki#播放器框架结构图 "播放器框架结构图"
[9]:https://github.com/bilibili/ijkplayer "ijkplayer"
[10]:https://github.com/google/ExoPlayer "ExoPlayer"
[11]:https://github.com/danikula/AndroidVideoCache "AndroidVideoCache"