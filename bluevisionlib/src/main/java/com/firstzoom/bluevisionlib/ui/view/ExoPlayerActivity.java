package com.firstzoom.bluevisionlib.ui.view;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

import static com.firstzoom.bluevisionlib.util.AppConstants.TAG;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.firstzoom.bluevisionlib.R;
import com.firstzoom.bluevisionlib.databinding.ActivityExoPlayerBinding;
import com.firstzoom.bluevisionlib.repository.Repository;
import com.firstzoom.bluevisionlib.util.AppConstants;
import com.firstzoom.bluevisionlib.util.AppUtil;
import com.firstzoom.bluevisionlib.util.BlueVisionUtil;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.util.Util;

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
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            if (playerView != null) {
                playerView.onPause();
            }
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
        binding.fullScreen.setOnClickListener(view -> openFullScreen(view));
        playerView.setControllerVisibilityListener((StyledPlayerView.ControllerVisibilityListener) visibility -> {
            if (visibility == 0)
                binding.fullScreen.setVisibility(View.VISIBLE);
            else
                binding.fullScreen.setVisibility(View.INVISIBLE);

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
                playerView.onResume();
            }
        }
    }

    private void initializePlayer() {
        String filePath = item;
        player = BlueVisionUtil.getExoPlayer(this);
        playerView.setPlayer(player);
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                if (playbackState == ExoPlayer.STATE_READY)
                    hideLoader();
                //hide progress bar
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                hideLoader();
                AppUtil.showSnackbarLong(playerView, "Unable to play  " + error.getLocalizedMessage());

            }
        });

        // Build the media item.
        try {
            Uri uri = Uri.parse(filePath);
           /* MediaSource mediaSource =
                    new RtspMediaSource.Factory()
                            .createMediaSource(MediaItem.fromUri(filePath));
                             player.setMediaSource(mediaSource);
            */

            MediaItem mediaItem = MediaItem.fromUri(uri);
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
            displayLoader();
        } catch (Exception e) {
            Log.d(TAG, "Exo player err " + e.getLocalizedMessage());
        }
    }

    @Override
    public void onVisibilityChange(int visibility) {

    }

    void openFullScreen(View view) {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(SCREEN_ORIENTATION_LANDSCAPE);
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        }
    }

    private void hideLoader() {
        binding.viewLoader.rootView.setVisibility(View.GONE);
    }

    private void displayLoader() {
        binding.viewLoader.rootView.setVisibility(View.VISIBLE);
    }

}