package org.com.track;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class CameraService extends Service implements Runnable
{	
	//public SurfaceHolder mHolder;
    Camera mCamera;
    Context mContext;
    int count=0;
    String TAG="CAMERA";
    ArrayList<String> path;
	
	public IBinder onBind(Intent arg0)
	{
        return null;
    }
	
	public void onCreate()
	{
        super.onCreate();
        System.out.println("Inside oncreate CameraService");
        Toast.makeText(this, "Inside on create CameraService", Toast.LENGTH_LONG).show();
        
	}
	
	public void onStart(Intent intent, int startid)
	{
		if(mCamera==null){
			mCamera = OpenFrontFacingCamera();
			capture();
			Toast.makeText(this, "Inside onStart CameraService", Toast.LENGTH_LONG).show();
			System.out.println("Inside Mcameraobject");
			count=0;
		}
	}
		
	

	Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] imageData, Camera c) {

			if (imageData != null) {
				Log.v("OLD preview","OLD preview");
				StoreByteImage(mContext, imageData, 20, "ImageName");
				System.out.println("Inside store image");
	    	    //mCamera.startPreview();
			}
		}
	};
	
	public void capture() 
	{
		Log.v("Inside while true","Inside while true");	
		try
		{
			Thread t1=new Thread(this);
			t1.start();
			System.out.println("Inside store image1");
		}
		catch(Exception e){Log.v("Error is this:",e.toString());}
	 }
	
	public void run()
	{
	  try{ 
		  Thread.sleep(3000);
		  for(int i=0;i<3;i++){
		  Log.v("Inside Run","--Inside Run--");
	      mCamera.takePicture(null, mPictureCallback, mPictureCallback);
	      Thread.sleep(5000);
	      System.out.println("Inside capture image image2");
		  }
		  mCamera.release();
		  mCamera=null;
		  
		  path=new ArrayList<String> ();
	      path.add(Environment.DIRECTORY_DCIM+"/image1.jpg");
	      path.add(Environment.DIRECTORY_DCIM+"/image2.jpg");	
	      path.add(Environment.DIRECTORY_DCIM+"/image3.jpg");
		  new SendAttachment("checkstudent11@gmail.com","Adndroid programe with Attachment",path);
		  //DeleteImage();
		  MainService.sendSMS(SmsReceiver.response_phno, "Mail sent Succesfully");
		  
		  
	  	}catch(Exception e){Log.v("-------------",e.toString());} 
	}

	public  boolean StoreByteImage(Context mContext, byte[] imageData, int quality, String expName) 
	{   
	    count++;
	    File sdImageMainDirectory = new File(Environment.DIRECTORY_DCIM+"/image"+count+".jpg");
	    FileOutputStream fileOutputStream = null;
	  
	    try {
	    	BitmapFactory.Options options=new BitmapFactory.Options();
	        options.inSampleSize = 5;
	        Bitmap bm = BitmapFactory.decodeByteArray(imageData, 0, imageData.length,options);
	        if(sdImageMainDirectory.exists()) {
	        	sdImageMainDirectory.delete();
	        	fileOutputStream = new FileOutputStream(sdImageMainDirectory);
	        }else if(!sdImageMainDirectory.exists()){
	        	fileOutputStream = new FileOutputStream(sdImageMainDirectory);
	          }  
	        
	        BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
	        bm.compress(CompressFormat.JPEG, quality, bos);   
	        bos.flush();  
	        bos.close();
	    } catch (Exception e) {e.printStackTrace();} 
	   
	    return true;
	}

	private Camera OpenFrontFacingCamera() {
	    Camera camera = null;
	 
	    // Look for front-facing camera, using the Gingerbread API.
	    // Java reflection is used for backwards compatibility with pre-Gingerbread APIs.
	    try {
	        Class<?> cameraClass = Class.forName("android.hardware.Camera");
	        Object cameraInfo = null;
	        Field field = null;
	        int cameraCount = 0;
	        Method getNumberOfCamerasMethod = cameraClass.getMethod( "getNumberOfCameras" );
	        if ( getNumberOfCamerasMethod != null ) {
	            cameraCount = (Integer) getNumberOfCamerasMethod.invoke( null, (Object[]) null );
	        }
	        Class<?> cameraInfoClass = Class.forName("android.hardware.Camera$CameraInfo");
	        if ( cameraInfoClass != null ) {
	            cameraInfo = cameraInfoClass.newInstance();
	        }
	        if ( cameraInfo != null ) {
	            field = cameraInfo.getClass().getField( "facing" );
	        }
	        Method getCameraInfoMethod = cameraClass.getMethod( "getCameraInfo", Integer.TYPE, cameraInfoClass );
	        if ( getCameraInfoMethod != null && cameraInfoClass != null && field != null ) {
	            for ( int camIdx = 0; camIdx < cameraCount; camIdx++ ) {
	                getCameraInfoMethod.invoke( null, camIdx, cameraInfo );
	                int facing = field.getInt( cameraInfo );
	                if ( facing == 1 ) { // Camera.CameraInfo.CAMERA_FACING_FRONT
	                    try {
	                        Method cameraOpenMethod = cameraClass.getMethod( "open", Integer.TYPE );
	                        if ( cameraOpenMethod != null ) {
	                            camera = (Camera) cameraOpenMethod.invoke( null, camIdx );
	                        }
	                    } catch (RuntimeException e) {
	                        Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
	                    }
	                }
	            }
	        }
	    }
	    // Ignore the bevy of checked exceptions the Java Reflection API throws - if it fails, who cares.
	    catch ( ClassNotFoundException e        ) {Log.e(TAG, "ClassNotFoundException" + e.getLocalizedMessage());}
	    catch ( NoSuchMethodException e         ) {Log.e(TAG, "NoSuchMethodException" + e.getLocalizedMessage());}
	    catch ( NoSuchFieldException e          ) {Log.e(TAG, "NoSuchFieldException" + e.getLocalizedMessage());}
	    catch ( IllegalAccessException e        ) {Log.e(TAG, "IllegalAccessException" + e.getLocalizedMessage());}
	    catch ( InvocationTargetException e     ) {Log.e(TAG, "InvocationTargetException" + e.getLocalizedMessage());}
	    catch ( InstantiationException e        ) {Log.e(TAG, "InstantiationException" + e.getLocalizedMessage());}
	    catch ( SecurityException e             ) {Log.e(TAG, "SecurityException" + e.getLocalizedMessage());}
	 
	    if ( camera == null ) {
	        // Try using the pre-Gingerbread APIs to open the camera.
	        try {
	            camera = Camera.open();
	        } catch (RuntimeException e) {
	            Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
	        }
	    }
	 
	    return camera;
	}

	public void show(String data)
	{
		Toast.makeText(this, data, Toast.LENGTH_LONG)
		.show();
	}
	public void DeleteImage()
	{
		try
		{
			File sdImageMainDirectory=null;
			for(int i=1;i<=3;i++)
			{
				sdImageMainDirectory = new File(Environment.DIRECTORY_DCIM+"/image"+i+".jpg");
		        if(sdImageMainDirectory.exists()) 
		        {
		        	sdImageMainDirectory.delete();	        	
		        }
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}

}
