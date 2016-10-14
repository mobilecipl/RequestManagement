package com.cipl.ciplapimanagementlibrary;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.cipl.ciplapimanagementlibrary.imagemanager.LruBitMapCache;

public class VolleyManager 
{
	private static VolleyManager instance;
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;
	private Context mContext;

	public static final String TAG = VolleyManager.class.getSimpleName();
	
	public static synchronized VolleyManager getSharedInstance(Context context)
	{
		if(instance == null)
			instance = new VolleyManager(context);

		return instance;
	}

	private VolleyManager(Context context) 
	{
		mContext = context;
	}

	public RequestQueue getRequestQueue()
	{
		if(mRequestQueue == null)
			mRequestQueue = Volley.newRequestQueue(mContext);

		return mRequestQueue;
	}

	public ImageLoader getImageLoader()
	{
		if(mImageLoader == null)
			mImageLoader = new ImageLoader(getRequestQueue(), new LruBitMapCache());

		return mImageLoader;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) 
	{
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) 
	{
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) 
	{
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}
	
	
}
