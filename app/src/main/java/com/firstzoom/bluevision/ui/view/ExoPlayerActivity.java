package com.firstzoom.bluevision.ui.view;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import com.firstzoom.bluevision.MainActivity;
import com.firstzoom.bluevision.R;
import com.firstzoom.bluevision.databinding.ActivityExoPlayerBinding;
import com.firstzoom.bluevision.repository.Repository;
import com.firstzoom.bluevision.util.AppConstants;
import com.firstzoom.bluevision.util.AppUtil;
import com.firstzoom.bluevision.util.BlueVisionUtil;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.util.Util;

import java.util.EventListener;
import java.util.UUID;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ExoPlayerActivity extends FragmentActivity implements StyledPlayerControlView.VisibilityListener {
    protected StyledPlayerView playerView;
    protected @Nullable
    ExoPlayer player;
    ActivityExoPlayerBinding binding;
    Boolean group;
    String item;
    @Inject
    Repository repository;

    @Override
    public void onPause() {
        super.onPause();
        if (playerView != null) {
            player.pause();
        }
        //releasePlayer();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            if (playerView != null) {

                playerView.onPause();
            }
            // releasePlayer();
        }
    }


    protected void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
            playerView.setPlayer(/* player= */ null);
        }
    }

    @Override
    public void onDestroy() {
        releasePlayer();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExoPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        item = (String) intent.getStringExtra(AppConstants.KEY_URL);
        playerView = binding.playerView;
        playerView.requestFocus();
        playerView.setControllerVisibilityListener(this);
        binding.fullScreen.setOnClickListener(view -> openFullScreen(view));
        playerView.setControllerVisibilityListener(new StyledPlayerView.ControllerVisibilityListener() {
            @Override
            public void onVisibilityChanged(int visibility) {
                if (visibility == 0)
                    binding.fullScreen.setVisibility(View.VISIBLE);
                else
                    binding.fullScreen.setVisibility(View.INVISIBLE);

            }
        });


    }

    /*@Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
            if (playerView != null) {
                playerView.onResume();
            }
        }
    }
*/
    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer();
            if (playerView != null) {
                Log.d(AppConstants.TAG, "Exo player resume");
                playerView.onResume();
            }
        }
    }

    private void initializePlayer() {
        String filePath = item;
        Log.d(AppConstants.TAG, "file path is " + item);
        // if(item!=null)
        //   filePath=item.getFile();

        player = BlueVisionUtil.getExoPlayer(this);
        playerView.setPlayer(player);
        // Build the media item.
        try {
            Uri uri = Uri.parse(filePath);
           /* MediaSource mediaSource =
                    new RtspMediaSource.Factory()
                            .createMediaSource(MediaItem.fromUri(filePath));
                             player.setMediaSource(mediaSource);
            */

            MediaItem mediaItem = MediaItem.fromUri(uri);
// Set the media item to be played.
            player.setMediaItem(mediaItem);

// Prepare the player.
            player.prepare();
// Start the playback.
            player.play();
        } catch (Exception e) {
            Log.d(AppConstants.TAG, "Exo player err " + e.getLocalizedMessage());

        }
    }

    @Override
    public void onVisibilityChange(int visibility) {

    }

    void openFullScreen(View view) {
        // startActivity(new Intent(this,ExoPlayerActivity.class));
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(SCREEN_ORIENTATION_LANDSCAPE);
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        }
        //Navigation.findNavController(view).navigate(R.id.exoPlayerActivity, BlueVisionUtil.getBundleUrl(item));

    }
}