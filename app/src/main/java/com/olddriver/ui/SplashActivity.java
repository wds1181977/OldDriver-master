package com.olddriver.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.olddriver.R;

import com.olddriver.ui.widget.LoginView;
import com.olddriver.ui.widget.SplashVideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.BindView;


public class SplashActivity extends Activity  {

    public static final String VIDEO_NAME = "welcome_video.mp4";


    @BindView(R.id.title) TextView title;
    @BindView(R.id.videoView) SplashVideoView mVideoView;
    @BindView(R.id.iv_avatar) ImageView ivAvatar;
    @BindView(R.id.btn_change_avatar) Button btnChangeAvatar;
//    @BindView(R.id.nick_input) TextInputLayout nickInput;
//    @BindView(R.id.sex_input) TextInputLayout sexInput;
//    @BindView(R.id.city_input) TextInputLayout cityInput;
//    @BindView(R.id.lover_input) TextInputLayout loverInput;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Window window = getWindow();
                window.setFlags(
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            setContentView(R.layout.activity_splash);
            ButterKnife.bind(this);

            playVideo();
            playAnim();

        }

        private void findView() {
        //    mVideoView = (VideoView) findViewById(R.id.videoView);
        //    buttonLeft = (Button) findViewById(R.id.buttonLeft);
         //   buttonRight = (Button) findViewById(R.id.buttonRight);
//            contianer = (ViewGroup) findViewById(R.id.container);
//            formView = (LoginView) findViewById(R.id.formView);
            title = (TextView) findViewById(R.id.title);
//            buttonLeft.setVisibility(View.GONE);
//            buttonRight.setVisibility(View.GONE);
//            formView.setVisibility(View.GONE);
//            formView.post(new Runnable() {
//                @Override
//                public void run() {
//                    int delta = formView.getTop()+formView.getHeight();
//                    formView.setTranslationY(-1 * delta);
//                }
//            });
        }

        private void initView() {

         //   buttonRight.setOnClickListener(this);
        //    buttonLeft.setOnClickListener(this);
        }

        private void playVideo() {

            File videoFile = getFileStreamPath(VIDEO_NAME);
            if (!videoFile.exists()) {
                videoFile = copyVideoFile();
            }
            mVideoView.setVideoPath(videoFile.getPath());
            mVideoView.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
            });
        }

        private void playAnim() {
            ObjectAnimator anim = ObjectAnimator.ofFloat(title, "alpha", 0,1);
            anim.setDuration(4000);
            anim.setRepeatCount(1);
            anim.setRepeatMode(ObjectAnimator.REVERSE);
            anim.start();
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    title.setVisibility(View.INVISIBLE);
                }
            });
        }

        @NonNull
        private File copyVideoFile() {
            File videoFile;
            try {
                FileOutputStream fos = openFileOutput(VIDEO_NAME, MODE_PRIVATE);
                InputStream in = getResources().openRawResource(R.raw.welcome_video);
                byte[] buff = new byte[1024];
                int len = 0;
                while ((len = in.read(buff)) != -1) {
                    fos.write(buff, 0, len);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            videoFile = getFileStreamPath(VIDEO_NAME);
            if (!videoFile.exists())
                throw new RuntimeException("video file has problem, are you sure you have welcome_video.mp4 in res/raw folder?");
            return videoFile;
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            mVideoView.stopPlayback();
        }

//        @Override
//        public void onClick(View view) {
//            int delta = formView.getTop()+formView.getHeight();
//            switch (inputType) {
//                case NONE:
//
//                    formView.animate().translationY(0).alpha(1).setDuration(500).start();
//                    if (view == buttonLeft) {
//                        inputType = InputType.LOGIN;
//                        buttonLeft.setText(R.string.button_confirm_login);
//                        buttonRight.setText(R.string.button_cancel_login);
//                    } else if (view == buttonRight) {
//                        inputType = InputType.SIGN_UP;
//                        buttonLeft.setText(R.string.button_confirm_signup);
//                        buttonRight.setText(R.string.button_cancel_signup);
//                    }
//
//                    break;
//                case LOGIN:
//
//                    formView.animate().translationY(-1 * delta).alpha(0).setDuration(500).start();
//                    if (view == buttonLeft) {
//
//                    } else if (view == buttonRight) {
//
//                    }
//                    inputType = InputType.NONE;
//                    buttonLeft.setText(R.string.button_login);
//                    buttonRight.setText(R.string.button_signup);
//                    break;
//                case SIGN_UP:
//
//                    formView.animate().translationY(-1 * delta).alpha(0).setDuration(500).start();
//                    if (view == buttonLeft) {
//
//                    } else if (view == buttonRight) {
//
//                    }
//                    inputType = InputType.NONE;
//                    buttonLeft.setText(R.string.button_login);
//                    buttonRight.setText(R.string.button_signup);
//                    break;
//            }
//        }
//
//        enum InputType {
//            NONE, LOGIN, SIGN_UP;
//        }
    }