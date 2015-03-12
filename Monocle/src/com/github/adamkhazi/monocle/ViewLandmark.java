package com.github.adamkhazi.monocle;
import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.model.LatLng;

@SuppressWarnings("deprecation")
public class ViewLandmark extends Activity implements OnTouchListener {

	//the images to display for gallery view
	 Integer[] imageIDs = {
	 R.drawable.pic1,
	 R.drawable.pic2,
	 R.drawable.pic3,
	 };
	 
	 int gallery_grid_Images[]={R.drawable.pic1,R.drawable.pic2,R.drawable.pic3};
	 ViewFlipper viewFlipper;
	 
	 // view flipper related 
	 float downXValue;
	 private static final String TAG = "Touch";
	 // These matrices will be used to move and zoom image
	 Matrix matrix = new Matrix();
	 Matrix savedMatrix = new Matrix();

	 // We can be in one of these 3 states
	 static final int NONE = 0;
	 static final int DRAG = 1;
	 static final int ZOOM = 2;
	 int mode = NONE;                                

	 // Remember some things for zooming
	 PointF start = new PointF();
	 PointF mid = new PointF();
	 float oldDist = 1f;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_landmark);
		
		//landmark information population related
		TextView landmarkTitle = (TextView) findViewById(R.id.landmarkTitle);
		TextView hours = (TextView) findViewById(R.id.openingTimes);
		TextView landmarkWeather = (TextView) findViewById(R.id.landmarkWeather);
		TextView landmarkDescription = (TextView) findViewById(R.id.landmarkDescription);

		Intent intent = getIntent();
		int requestedLandmarkId = intent.getIntExtra(Browse.EXTRA_MESSAGE, -1);
		//Toast.makeText(this,String.valueOf(pos), Toast.LENGTH_SHORT).show();

		if (requestedLandmarkId == 1){
			showBigBen(landmarkTitle, hours, landmarkWeather, landmarkDescription);
		}
		
		// Add these two lines
        RelativeLayout lay = (RelativeLayout) findViewById(R.id.layz);
        lay.setOnTouchListener((OnTouchListener) this);
		
		viewFlipper = (ViewFlipper) findViewById(R.id.flipper);
		for(int i=0;i<gallery_grid_Images.length;i++)
		{
			//  This will create dynamic image view and add them to ViewFlipper
			setFlipperImage(gallery_grid_Images[i]);
		}
		
	}
		private void showBigBen(TextView landmarkTitle, TextView hours,
			TextView landmarkWeather, TextView landmarkDescription) {
		landmarkTitle.setText("Big Ben");
		hours.setText("Hours: 10am-5pm");
		landmarkWeather.setText("Mostly cloudy currently. It's 9 degrees at location");
		landmarkDescription.setText("16-storey Gothic clocktower and national symbol at the "
				+ "Eastern end of the Houses of Parliament.");

		
	}
		
	public void estimatedArrival(View view){
		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
		Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		double longitude = location.getLongitude();
		double latitude = location.getLatitude();
		
		LatLng currentLocation = new LatLng(latitude,longitude);
		LatLng landmark = new LatLng(51.500922, -0.124545);
		
		Location locationA = new Location("pointA");
		locationA.setLatitude(currentLocation.latitude);
		locationA.setLongitude(currentLocation.longitude);
		
		Location locationB = new Location("pointB");
		locationB.setLatitude(landmark.latitude);
		locationB.setLongitude(landmark.longitude);
		
		float distance = locationA.distanceTo(locationB);
		
		Log.i("DISTANCE...", String.valueOf(distance));
		
		distance*=0.000621371192; //times radius of earth
		
		Log.i("DISTANCE...", String.valueOf(distance));
		
		TextView estimatedArrival = (TextView) findViewById(R.id.estimatedArrival);

		DecimalFormat formattedString = new DecimalFormat("##.##");
		
		
		estimatedArrival.setText("Estimated time by walking is " + formattedString.format((distance/4)) + " hours");
		
		
	}
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {

			// Get the action that was done 
			switch (arg1.getAction())
			{
			case MotionEvent.ACTION_DOWN:
			{
				// store the X value when the user's finger was pressed down
				downXValue = arg1.getX();
				break;
			}
			case MotionEvent.ACTION_UP:
			{
				// Get the X value when the user released finger
				float currentX = arg1.getX();            

				// going backwards: pushing stuff to the right
				if (downXValue < currentX)
				{
					ViewFlipper vf = (ViewFlipper) findViewById(R.id.flipper); 
					vf.setAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out)); // Set the animation
					vf.showPrevious(); // move using animation
				}

				// going forwards: pushing stuff to the left
				if (downXValue > currentX)
				{
					ViewFlipper vf = (ViewFlipper) findViewById(R.id.flipper); 
					vf.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in)); // Set the animation
					vf.showNext(); // move using animation
				}
				break;
			}
			case MotionEvent.ACTION_MOVE:
				if (mode == DRAG) {
					matrix.set(savedMatrix);
					matrix.postTranslate(arg1.getX() - start.x,
							arg1.getY() - start.y);
				}
				else if (mode == ZOOM) {
					float newDist = spacing(arg1);
					Log.d(TAG, "newDist=" + newDist);
					if (newDist > 10f) {
						matrix.set(savedMatrix);
						float scale = newDist / oldDist;
						matrix.postScale(scale, scale, mid.x, mid.y);
					}
				}
				break;
			}
			// view.setImageMatrix(matrix);
			return true; // indicate event was handled
		}



		/** Show an event in the LogCat view, for debugging */
		private void dumpEvent(MotionEvent arg1) {
			String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
					"POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
			StringBuilder sb = new StringBuilder();
			int action = arg1.getAction();
			int actionCode = action & MotionEvent.ACTION_MASK;
			sb.append("event ACTION_").append(names[actionCode]);
			if (actionCode == MotionEvent.ACTION_POINTER_DOWN
					|| actionCode == MotionEvent.ACTION_POINTER_UP) {
				sb.append("(pid ").append(
						action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
				sb.append(")");
			}
			sb.append("[");
			for (int i = 0; i < arg1.getPointerCount(); i++) {
				sb.append("#").append(i);
				sb.append("(pid ").append(arg1.getPointerId(i));
				sb.append(")=").append((int) arg1.getX(i));
				sb.append(",").append((int) arg1.getY(i));
				if (i + 1 < arg1.getPointerCount())
					sb.append(";");
			}
			sb.append("]");
			Log.d(TAG, sb.toString());
		}

		/** Determine the space between the first two fingers */
		private float spacing(MotionEvent event) {
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return FloatMath.sqrt(x * x + y * y);
		}

		/** Calculate the mid point of the first two fingers */
		private void midPoint(PointF point, MotionEvent event) {
			float x = event.getX(0) + event.getX(1);
			float y = event.getY(0) + event.getY(1);
			point.set(x / 2, y / 2);
		}
		//		// gallery related 
		//		@SuppressWarnings("deprecation")
		//		Gallery gallery = (Gallery) findViewById(R.id.gallery1);
		//		gallery.setAdapter(new ImageAdapter(this));
		//
		//		gallery.setOnItemClickListener(new OnItemClickListener() {
		//			public void onItemClick(AdapterView<?> parent, View v, int position,long id)
		//			{
		//				Toast.makeText(getBaseContext(),"pic" + (position + 1) + " selected",
		//						Toast.LENGTH_SHORT).show();
		//				// display the images selected
		//				ImageView imageView = (ImageView) findViewById(R.id.image1);
		//				imageView.setImageResource(imageIDs[position]);
		//			}
		//		});
		//	}

	public class ImageAdapter extends BaseAdapter {
		private Context context;
		private int itemBackground;
		public ImageAdapter(Context c)
		{
			context = c;
			// sets a grey background; wraps around the images
			TypedArray a =obtainStyledAttributes(R.styleable.MyGallery);
			itemBackground = a.getResourceId(R.styleable.MyGallery_android_galleryItemBackground, 0);
			a.recycle();
		}
		// returns the number of images
		public int getCount() {
			return imageIDs.length;
		}
		// returns the ID of an item
		public Object getItem(int position) {
			return position;
		}
		// returns the ID of an item
		public long getItemId(int position) {
			return position;
		}
		// returns an ImageView view
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = new ImageView(context);
			imageView.setImageResource(imageIDs[position]);
			imageView.setLayoutParams(new Gallery.LayoutParams(150, 150));
			imageView.setBackgroundResource(itemBackground);
			return imageView;
		}
	}

	private void setFlipperImage(int res) {
	    Log.i("Set Filpper Called", res+"");
	    ImageView image = new ImageView(getApplicationContext());
	    image.setBackgroundResource(res);
	    viewFlipper.addView(image);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_landmark, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
