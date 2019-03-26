package com.example.library;

import java.io.IOException;
import java.util.Vector;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.CameraManager;
import com.google.zxing.CaptureActivityHandler;
import com.google.zxing.InactivityTimer;
import com.google.zxing.Result;
import com.google.zxing.ViewfinderView;
import com.example.library.R;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class CaptureActivity extends Activity implements Callback{
    private ViewfinderView viewfinderView;
    private Button cancelScanButton;
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    private CaptureActivityHandler handler;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    //private MediaPlayer mediaPlayer;

    private int first_bookcase = 0;
    private int second_row = 0;
    private int third_col = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);

        //getActionBar().hide();
        CameraManager.init(getApplication());

        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null){
            int t = 0;
            if((t = bundle.getInt("first_bookcase")) != 0)
                first_bookcase = t;
            if((t = bundle.getInt("second_row")) != 0)
                second_row = t;
            if((t = bundle.getInt("third_col")) != 0)
                third_col = t;
        }

        findView();
        addListener();
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }
    //Press the back button in mobile phone
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.base_slide_right_out);
    }
    /**
     * 查找图形组件
     */
    private void findView(){
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        cancelScanButton = (Button) this.findViewById(R.id.btn_cancel_scan);

    }
    /**
     * 为组件添加Listener
     */
    private void addListener(){
        cancelScanButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CaptureActivity.this, MainActivity.class );
                startActivity(intent);
                CaptureActivity.this.finish();
                overridePendingTransition(0, R.anim.base_slide_right_out);
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;
        Log.d("onResume()","CaptureActivity");
        //initBeepSound();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        Log.d("onPause()","CaptureActivity");
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        Log.d("onDestory()","CaptureActivity");
        super.onDestroy();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }



    /**
     * 初始化相机
     * @param surfaceHolder
     */
    private void initCamera(SurfaceHolder surfaceHolder) {

        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }
    /*
    /**
     * 准备beep音乐
     */

    /*
    private void initBeepSound() {
        if (mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }
    */

    /**
     * 处理扫描结果
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        //playBeepSound();
        String isbn = result.getText();
        //FIXME
        if (isbn.equals("")) {
            Toast.makeText(CaptureActivity.this, "扫描失败", Toast.LENGTH_SHORT).show();
        }else {
//			System.out.println("Result:"+resultString);
            Intent resultIntent = new Intent(this, ScanConfirmActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("isbn", isbn.trim());

            bundle.putInt("first_bookcase", first_bookcase);
            bundle.putInt("second_row", second_row);
            bundle.putInt("third_col", third_col);

            //bundle.putString("ISBN", isbn);
            resultIntent.putExtras(bundle);
//			this.setResult(RESULT_OK, resultIntent);
            startActivity(resultIntent);
            overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);

        }
        CaptureActivity.this.finish();
    }
    /*
    private void playBeepSound() {
        if (mediaPlayer != null &&
                ((AudioManager)getSystemService(AUDIO_SERVICE)).getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            mediaPlayer.start();
        }
    }
    */

    public Handler getHandler() {
        return handler;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }


    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };
}

