package org.yanzi.activity;

import org.yanzi.camera.CameraInterface;
import org.yanzi.camera.preview.CameraSurfaceView;
import org.yanzi.mode.GoogleFaceDetect;
import org.yanzi.playcamera.R;
import org.yanzi.ui.FaceView;
import org.yanzi.util.DisplayUtil;
import org.yanzi.util.EventUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.TextView;

public class CameraActivity extends Activity{
	private static final String TAG = "yanzi";
	CameraSurfaceView surfaceView = null;
	ImageButton shutterBtn;
	ImageButton switchBtn;
	FaceView faceView;
	float previewRate = -1f;
	private MainHandler mMainHandler = null;
	GoogleFaceDetect googleFaceDetect = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_camera);
		initUI();
		initViewParams();
		mMainHandler = new MainHandler();
		googleFaceDetect = new GoogleFaceDetect(getApplicationContext(), mMainHandler);


		shutterBtn.setOnClickListener(new BtnListeners());
		switchBtn.setOnClickListener(new BtnListeners());
		mMainHandler.sendEmptyMessageDelayed(EventUtil.CAMERA_HAS_STARTED_PREVIEW, 1500);

		//
		//需要监听左右滑动事件的view
		TextView text = (TextView) this.findViewById(R.id.textview);

		//setLongClickable是必须的
		text.setLongClickable(true);
		text.setOnTouchListener(new MyGestureListener(this));

		//text.setBackgroundColor(0xC8C8C8C8);
		//text.setTextColor(0x00000000);
		//text.setText("123456789adfasdfasdfasdfads");

		/*
		shutterBtn.setVisibility(View.INVISIBLE);
		switchBtn.setVisibility(View.INVISIBLE);
		*/
	}

	private class MyGestureListener extends GestureListener {
		public MyGestureListener(Context context) {
			super(context);
		}
		@Override
		public boolean left() {
			Log.e("test", "向左滑");
			showIcon(false);
			return super.left();
		}

		@Override
		public boolean right() {
			Log.e("test", "向右滑");
			showIcon(false);
			return super.right();
		}

		@Override
		public boolean up() {
			Log.e("test", "向上滑");
			showIcon(true);
			return super.up();
		}

		@Override
		public boolean down() {
			Log.e("test", "向下滑");
			showIcon(false);
			return super.down();
		}
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			Log.d("##@@##", "onTouch: takepicture");
			takePicture();
			return false;
		}
		/*
		@Override
		public boolean onMultiTouch(View v, MotionEvent event, int touchCount){
			Log.d("##@@##", "onMultiTouch: touchCount="+touchCount);
			if(touchCount >=2 ){
				Log.d("##@@##", "onClick: threadinfo=" + Thread.currentThread() + " mainthread="+getMainLooper());
				takePicture();
			}
			return true;
		}
		*/

	}
	private void showIcon(boolean enable){
		if (enable){
			shutterBtn.setVisibility(View.VISIBLE);
			switchBtn.setVisibility(View.VISIBLE);
			surfaceView.setVisibility(View.VISIBLE);
		}
		else {
			shutterBtn.setVisibility(View.INVISIBLE);
			switchBtn.setVisibility(View.INVISIBLE);
			surfaceView.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera, menu);
		return true;
	}

	private void initUI(){
		surfaceView = (CameraSurfaceView)findViewById(R.id.camera_surfaceview);
		shutterBtn = (ImageButton)findViewById(R.id.btn_shutter);
		switchBtn = (ImageButton)findViewById(R.id.btn_switch);
		faceView = (FaceView)findViewById(R.id.face_view);
	}
	private void initViewParams(){
		LayoutParams params = surfaceView.getLayoutParams();
		Point p = DisplayUtil.getScreenMetrics(this);
		params.width = p.x/10/3;
		params.height = p.y/10/3;
		previewRate = DisplayUtil.getScreenRate(this);
		surfaceView.setLayoutParams(params);
	}

	private class BtnListeners implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.btn_shutter:
				Log.d("##@@##", "onClick: threadinfo=" + Thread.currentThread() + " mainthread="+getMainLooper());
				takePicture();
				break;
			case R.id.btn_switch:
				switchCamera();
				break;
			default:break;
			}
		}

	}
	private  class MainHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what){
			case EventUtil.UPDATE_FACE_RECT:
				Face[] faces = (Face[]) msg.obj;
				faceView.setFaces(faces);
				break;
			case EventUtil.CAMERA_HAS_STARTED_PREVIEW:
				startGoogleFaceDetect();
				break;
			}
			super.handleMessage(msg);
		}

	}

	private void takePicture(){
		CameraInterface.getInstance().doTakePicture();
		mMainHandler.sendEmptyMessageDelayed(EventUtil.CAMERA_HAS_STARTED_PREVIEW, 1500);
	}
	private void switchCamera(){
		stopGoogleFaceDetect();
		int newId = (CameraInterface.getInstance().getCameraId() + 1)%2;
		CameraInterface.getInstance().doStopCamera();
		CameraInterface.getInstance().doOpenCamera(null, newId);
		CameraInterface.getInstance().doStartPreview(surfaceView.getSurfaceHolder(), previewRate);
		mMainHandler.sendEmptyMessageDelayed(EventUtil.CAMERA_HAS_STARTED_PREVIEW, 1500);
//		startGoogleFaceDetect();

	}
	private void startGoogleFaceDetect(){
		Camera.Parameters params = CameraInterface.getInstance().getCameraParams();
		if(params.getMaxNumDetectedFaces() > 0){
			if(faceView != null){
				faceView.clearFaces();
				faceView.setVisibility(View.VISIBLE);
			}
			CameraInterface.getInstance().getCameraDevice().setFaceDetectionListener(googleFaceDetect);
			//CameraInterface.getInstance().getCameraDevice().startFaceDetection();
		}
	}
	private void stopGoogleFaceDetect(){
		Camera.Parameters params = CameraInterface.getInstance().getCameraParams();
		if(params.getMaxNumDetectedFaces() > 0){
			CameraInterface.getInstance().getCameraDevice().setFaceDetectionListener(null);
			//CameraInterface.getInstance().getCameraDevice().stopFaceDetection();
			//faceView.clearFaces();
		}
	}

}
