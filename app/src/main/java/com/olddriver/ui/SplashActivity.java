package com.olddriver.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.olddriver.R;

import com.olddriver.data.AVService;
import com.olddriver.ui.widget.LoginView;
import com.olddriver.ui.widget.SplashVideoView;
import com.olddriver.util.ImeUtils;
import com.olddriver.util.ScrimUtil;
import com.olddriver.util.glide.CircleTransform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.BindView;
import butterknife.OnTextChanged;


public class SplashActivity extends Activity  {

    public static final String VIDEO_NAME = "welcome_video.mp4";
    private static final int IMAGE_PICK_REQUEST = 0;
    @BindView(R.id.container) ViewGroup container;
    @BindView(R.id.loading) ProgressBar loading;
    @BindView(R.id.title) TextView title;
    @BindView(R.id.videoView) SplashVideoView mVideoView;
    @BindView(R.id.iv_avatar) ImageView ivAvatar;
    @BindView(R.id.btn_change_avatar) Button btnChangeAvatar;
    @BindView(R.id.sign_up_username_label) TextInputLayout usernameInput;
    @BindView(R.id.sign_up_password_label) TextInputLayout passwordInput;
    @BindView(R.id.sign_up_city_label) TextInputLayout cityInput;
    @BindView(R.id.sign_up_github_label) TextInputLayout githubInput;
    @BindView(R.id.username) EditText username;
    @BindView(R.id.password) EditText password;
    @BindView(R.id.city) EditText city;
    @BindView(R.id.github) EditText github;
    @BindView(R.id.button_sign_up) Button  post;
    private boolean haveImage = false;
    private Bitmap bitmap;
    private Uri avatar_uri;




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

    @OnClick(R.id.btn_change_avatar)
    void sendimageAction() {
        ImeUtils.hideIme(title);
        if (haveImage == false) {
            pickImage(this, IMAGE_PICK_REQUEST);
        } else {
            bitmap = null;
            haveImage = false;
            setButtonAndImage();
        }
    }

    private  void pickImage(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent, requestCode);
    }
    private  void setButtonAndImage() {
        ivAvatar.setImageBitmap(bitmap);
        if (haveImage) {
            btnChangeAvatar.setText(R.string.status_cancelImage);
           // ivAvatar.setVisibility(View.VISIBLE);
        } else {
            btnChangeAvatar.setText(R.string.status_addImage);
            ivAvatar.setImageResource(R.drawable.avatar_placeholder);
           // ivAvatar.setVisibility(View.INVISIBLE);
        }
    }


    @OnTextChanged(R.id.username)
    protected void usernameTextChanged(CharSequence text) {
        setPostButtonState();
    }

    @OnTextChanged(R.id.password)
    protected void passwordTextChanged(CharSequence text) {

        setPostButtonState();
    }


    private void setPostButtonState() {
        post.setEnabled(!TextUtils.isEmpty(username.getText())&&!TextUtils.isEmpty(password.getText()));

    }

    @OnClick(R.id.button_sign_up)
    void signUp() {
        showLoading();
        final String usernameStr = username.getText().toString().trim();
        final String passwordStr = password.getText().toString().trim();
        final String cityStr = city.getText().toString().trim();
        final String githubStr = github.getText().toString().trim();
        AVService.register(usernameStr,passwordStr,cityStr,githubStr, avatar_uri, new SaveCallback() {
            @Override
            public void done(AVException e) {

                if (e != null) {
                    showLoginFailed(e);
                    Log.e("CreateUser", "Update User failed.", e);
                }else {


                    final Toast confirmLogin = new Toast(getApplicationContext());
                    final View v = LayoutInflater.from(SplashActivity.this).inflate(R.layout
                            .toast_logged_in_confirmation, null, false);
                    ((TextView) v.findViewById(R.id.name)).setText(usernameStr);
                    // need to use app context here as the activity will be destroyed shortly
                    Glide.with(getApplicationContext())
                            .load(avatar_uri)
                            .placeholder(R.drawable.avatar_placeholder)
                            .transform(new CircleTransform(getApplicationContext()))
                            .into((ImageView) v.findViewById(R.id.avatar));
                    v.findViewById(R.id.scrim).setBackground(ScrimUtil
                            .makeCubicGradientScrimDrawable(
                                    ContextCompat.getColor(SplashActivity.this, R.color.scrim),
                                    5, Gravity.BOTTOM));
                    confirmLogin.setView(v);
                    confirmLogin.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
                    confirmLogin.setDuration(Toast.LENGTH_LONG);
                    confirmLogin.show();
                    setResult(Activity.RESULT_OK);
                    finish();

                }


            }
        }
       );

    }
    private void showLoading() {
       TransitionManager.beginDelayedTransition(container);
        container.setVisibility(View.GONE);
        post.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
    }

    private void showLoginFailed(AVException e) {
        Resources res=getResources();
        String Signupfailed= res.getString(R.string.signup_failed);
        Snackbar.make(container, Signupfailed, Snackbar.LENGTH_SHORT).show();
        showSignUp();
        password.requestFocus();
    }

    private void showSignUp() {
        TransitionManager.beginDelayedTransition(container);
        container.setVisibility(View.VISIBLE);
        post.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_REQUEST) {
                avatar_uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), avatar_uri);
                    haveImage = true;
                    setButtonAndImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
