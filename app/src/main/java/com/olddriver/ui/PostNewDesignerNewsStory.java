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

package com.olddriver.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.olddriver.R;
import com.olddriver.data.AVService;
import com.olddriver.data.api.designernews.PostStoryService;
import com.olddriver.data.prefs.DesignerNewsPrefs;
import com.olddriver.ui.transitions.FabTransform;
import com.olddriver.ui.transitions.MorphTransform;
import com.olddriver.ui.widget.BottomSheet;
import android.widget.ImageView;
import com.olddriver.ui.widget.ObservableScrollView;
import com.olddriver.util.AnimUtils;
import com.olddriver.util.ImeUtils;

import java.io.IOException;

public class PostNewDesignerNewsStory extends Activity {

    private static final int IMAGE_PICK_REQUEST = 0;
    public static final int RESULT_DRAG_DISMISSED = 3;
    public static final int RESULT_POSTING = 4;
    private boolean haveImage = false;
    private Bitmap bitmap;
    private Uri imageUri;


    @BindView(R.id.bottom_sheet) BottomSheet bottomSheet;
    @BindView(R.id.bottom_sheet_content) ViewGroup bottomSheetContent;
    @BindView(R.id.title) TextView sheetTitle;
    @BindView(R.id.scroll_container) ObservableScrollView scrollContainer;
    @BindView(R.id.new_story_title) EditText title;
    @BindView(R.id.new_story_url_label) TextInputLayout urlLabel;
    @BindView(R.id.new_story_url) EditText url;
    @BindView(R.id.new_story_comment_label) TextInputLayout commentLabel;
    @BindView(R.id.new_story_comment) EditText comment;
    @BindView(R.id.imageAction) Button imageAction;
    @BindView(R.id.image) ImageView imageView;
    @BindView(R.id.new_story_post) Button post;
    @BindDimen(R.dimen.z_app_bar) float appBarElevation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_new_designer_news_story);
        ButterKnife.bind(this);
        if (!FabTransform.setup(this, bottomSheetContent)) {
            MorphTransform.setup(this, bottomSheetContent,
                    ContextCompat.getColor(this, R.color.background_light), 0);
        }

        bottomSheet.registerCallback(new BottomSheet.Callbacks() {
            @Override
            public void onSheetDismissed() {
                // After a drag dismiss, finish without the shared element return transition as
                // it no longer makes sense.  Let the launching window know it's a drag dismiss so
                // that it can restore any UI used as an entering shared element
                setResult(RESULT_DRAG_DISMISSED);
                finish();
            }
        });

        scrollContainer.setListener(new ObservableScrollView.OnScrollListener() {
            @Override
            public void onScrolled(int scrollY) {
                if (scrollY != 0
                        && sheetTitle.getTranslationZ() != appBarElevation) {
                    sheetTitle.animate()
                            .translationZ(appBarElevation)
                            .setStartDelay(0L)
                            .setDuration(80L)
                            .setInterpolator(AnimUtils.getFastOutSlowInInterpolator
                                    (PostNewDesignerNewsStory.this))
                            .start();
                } else if (scrollY == 0 && sheetTitle.getTranslationZ() == appBarElevation) {
                    sheetTitle.animate()
                            .translationZ(0f)
                            .setStartDelay(0L)
                            .setDuration(80L)
                            .setInterpolator(AnimUtils.getFastOutSlowInInterpolator
                                    (PostNewDesignerNewsStory.this))
                            .start();
                }
            }
        });

        // check for share intent
        if (isShareIntent()) {
            ShareCompat.IntentReader intentReader = ShareCompat.IntentReader.from(this);
            url.setText(intentReader.getText());
            title.setText(intentReader.getSubject());

            // when receiving a share there is no shared element transition so animate up the
            // bottom sheet to establish the spatial model i.e. that it can be dismissed downward
            overridePendingTransition(R.anim.post_story_enter, R.anim.fade_out_rapidly);
            bottomSheetContent.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    bottomSheetContent.getViewTreeObserver().removeOnPreDrawListener(this);
                    bottomSheetContent.setTranslationY(bottomSheetContent.getHeight());
                    bottomSheetContent.animate()
                            .translationY(0f)
                            .setStartDelay(120L)
                            .setDuration(240L)
                            .setInterpolator(AnimUtils.getLinearOutSlowInInterpolator
                                    (PostNewDesignerNewsStory.this));
                    return false;
                }
            });
        }
    }

    @Override
    protected void onPause() {
        // customize window animations
        overridePendingTransition(0, R.anim.fade_out_rapidly);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (isShareIntent()) {
            bottomSheetContent.animate()
                    .translationY(bottomSheetContent.getHeight())
                    .setDuration(160L)
                    .setInterpolator(AnimUtils.getFastOutLinearInInterpolator
                            (PostNewDesignerNewsStory.this))
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            finishAfterTransition();
                        }
                    });
        } else {
            super.onBackPressed();
        }
    }


   private  void setButtonAndImage() {
        imageView.setImageBitmap(bitmap);
        if (haveImage) {
            imageAction.setText(R.string.status_cancelImage);
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageAction.setText(R.string.status_addImage);
            imageView.setVisibility(View.INVISIBLE);
        }
    }
    private  void pickImage(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent, requestCode);
    }



    @OnClick(R.id.imageAction)
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

    @OnClick(R.id.bottom_sheet)
    protected void dismiss() {
        finishAfterTransition();
    }

    @OnTextChanged(R.id.new_story_title)
    protected void titleTextChanged(CharSequence text) {
        setPostButtonState();
    }

    @OnTextChanged(R.id.new_story_url)
    protected void urlTextChanged(CharSequence text) {
//        final boolean emptyUrl = TextUtils.isEmpty(text);
//        comment.setEnabled(emptyUrl);
//        commentLabel.setEnabled(emptyUrl);
//        comment.setFocusableInTouchMode(emptyUrl);
        setPostButtonState();
    }

    @OnTextChanged(R.id.new_story_comment)
    protected void commentTextChanged(CharSequence text) {
//        final boolean emptyComment = TextUtils.isEmpty(text);
//        url.setEnabled(emptyComment);
//        urlLabel.setEnabled(emptyComment);
//        url.setFocusableInTouchMode(emptyComment);
        setPostButtonState();
    }

    @OnClick(R.id.new_story_post)
    protected void postNewStory() {
        if (DesignerNewsPrefs.get(this).isLoggedIn()) {
            ImeUtils.hideIme(title);
            Intent postIntent = new Intent(PostStoryService.ACTION_POST_NEW_STORY, null,
                    this, PostStoryService.class);
            postIntent.putExtra(PostStoryService.EXTRA_STORY_TITLE, title.getText().toString());
            postIntent.putExtra(PostStoryService.EXTRA_STORY_GITHUB, url.getText().toString());
            postIntent.putExtra(PostStoryService.EXTRA_STORY_COMMENT, comment.getText().toString());
            postIntent.putExtra(PostStoryService.EXTRA_STORY_IMAGEURL, imageUri.toString());
            postIntent.putExtra(PostStoryService.EXTRA_BROADCAST_RESULT,
                    getIntent().getBooleanExtra(PostStoryService.EXTRA_BROADCAST_RESULT, false));
            startService(postIntent);
            setResult(RESULT_POSTING);
            finishAfterTransition();
        } else {
            Intent login = new Intent(this, DesignerNewsLogin.class);
            MorphTransform.addExtras(login, ContextCompat.getColor(this, R.color.designer_news), 0);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                    this, post, getString(R.string.transition_designer_news_login));
            startActivity(login, options.toBundle());
        }
    }

    private boolean isShareIntent() {
        return getIntent() != null && Intent.ACTION_SEND.equals(getIntent().getAction());
    }

    private void setPostButtonState() {
        post.setEnabled(!TextUtils.isEmpty(title.getText())
                );
    }
//监听软键盘回车
    @OnEditorAction({ R.id.new_story_url, R.id.new_story_comment })
    protected boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            postNewStory();
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_REQUEST) {
                imageUri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    haveImage = true;
                    setButtonAndImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
