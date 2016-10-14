package com.cipl.ciplapimanagementlibrary;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

public class RequestData 
{

	private Context mContext;
	public Map<String, String> data;

	public Map<String,String> header;
	public boolean shouldCache = true;
	private Cache cache;
	public String requestUrl;
	public String onErrorCallback;
	public String onSuccessCallback;
	public Object targetObject;
	public int requestType = Method.GET;

	public boolean showLoader = true;
	public String loaderText = "Loading...";
	
	
	ProgressDialog pDialog;

	/**
	 * Constructor for class
	 * @param context - The context of the calling component
	 */
	public RequestData(Context context)
	{
		mContext = context;
		pDialog = new ProgressDialog(mContext);
		setContentHeader();
		cache = VolleyManager.getSharedInstance(mContext).getRequestQueue().getCache();
	}

	/**
	 * Set the content header for api call
	 */
	private void setContentHeader() 
	{		
		if(header == null)
			header = new HashMap<String, String>();
		header.put("Content-Type", "application/x-www-form-urlencoded");
	}

	/**
	 * Function that will make an API request and will return json response as a result of callback
	 * @param jsonRequestTag : Tag through which the request can be cancelled or cached.
	 */
	public void makeRequestForJsonObjResponse(String jsonRequestTag)
	{
		if(loaderText.equals(""))
			pDialog.setMessage("Loading...");
		pDialog.setMessage(loaderText);
		showDialog(true);     

		JsonObjectRequest jsonObjReq = new JsonObjectRequest(requestType,requestUrl, null,new Response.Listener<JSONObject>() 
		{
			@Override
			public void onResponse(JSONObject response) 
			{
				sendSuccessCallback(response);
				Log.d("TAG", response.toString());
				showDialog(false);
			}
		}
		,
		new Response.ErrorListener() 
		{
			@Override
			public void onErrorResponse(VolleyError error) 
			{
				sendErrorCallback(error);
				VolleyLog.d("TAG", "Error: " + error.getMessage());
				// hide the progress dialog
				showDialog(false);
			}

		})
		{
			@Override
			protected Map<String, String> getParams() 
			{			
				return data;
			}

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError 
			{
				return header;
			}
			
			
			@Override
			public byte[]  getBody()
			{
				if (data != null && data.size() > 0)
				{
			        return encodeParameters(data, getParamsEncoding());
				}
				
				else
					return super.getBody();
			}
		};
		
		jsonObjReq.setShouldCache(shouldCache);

		// Adding request to request queue
		VolleyManager.getSharedInstance(mContext).addToRequestQueue(jsonObjReq, jsonRequestTag);
	}

	/**
	 * Function to set call back to the class that made the API request
	 * @param response : The response of the API
	 */
	protected void sendSuccessCallback(Object response)
	{
		try 
		{
			java.lang.reflect.Method method = targetObject.getClass().getDeclaredMethod(onSuccessCallback, Object.class);
			method.invoke(targetObject, response);
		}
		catch (Exception e) 
		{		
			e.printStackTrace();
		}
	}
	
	protected void sendErrorCallback(VolleyError response)
	{
		try 
		{
			java.lang.reflect.Method method = targetObject.getClass().getDeclaredMethod(onErrorCallback, VolleyError.class);
			method.invoke(targetObject, response);
		}
		catch (Exception e) 
		{		
			e.printStackTrace();
		}
	}

	/**
	 * Function that will make an API request and will return jsonarray response as a result of callback
	 * @param jsonRequestTag : Tag through which the request can be cancelled or cached.
	 */
	public void makeRequestForJsonArrResponse(String jsonArrayReqTag)
	{
		pDialog.setMessage("Loading...");
		showDialog(true);     
		
		JsonArrayRequest req = new JsonArrayRequest(requestType,requestUrl, new Response.Listener<JSONArray>() 
		{
			@Override
			public void onResponse(JSONArray response) 
			{
				sendSuccessCallback(response);
				Log.d("TAG", response.toString());
				showDialog(false);            
			}
		}, 
		new Response.ErrorListener() 
		{
			@Override
			public void onErrorResponse(VolleyError error) 
			{
				sendErrorCallback(error);
				VolleyLog.d("TAG", "Error: " + error.getMessage());
				showDialog(false);
			}
		})
		{
			@Override
			protected Map<String, String> getParams() throws AuthFailureError 
			{
				return data;
			}
			
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError
			{
				return header;
			}
			
			@Override
			public byte[]  getBody()
			{
				if (data != null && data.size() > 0)
				{
			        return encodeParameters(data, getParamsEncoding());
				}
				
				else
					return super.getBody();
			}
		};
		
		req.setShouldCache(shouldCache);

		// Adding request to request queue
		VolleyManager.getSharedInstance(mContext).addToRequestQueue(req, jsonArrayReqTag);
	}

	/**
	 * Function that will make an API request and will return string response as a result of callback
	 * @param jsonRequestTag : Tag through which the request can be cancelled or cached.
	 */
	public void makeRequestForStringResponse(String stringReqTag)
	{
		pDialog.setMessage("Loading...");
		showDialog(true);     

		StringRequest strReq = new StringRequest(requestType,requestUrl, new Response.Listener<String>() 
				{
			@Override
			public void onResponse(String response) 
			{
				sendSuccessCallback(response);
				Log.d("TAG", response.toString());
				showDialog(false);
			}
				},
				new Response.ErrorListener() 
				{
					@Override
					public void onErrorResponse(VolleyError error) 
					{
						sendErrorCallback(error);
						VolleyLog.d("TAG", "Error: " + error.getMessage());
						showDialog(false);
					}
				})
		{
			@Override
			protected Map<String, String> getParams() throws AuthFailureError 
			{
				return data;
			}
		};;

		strReq.setShouldCache(shouldCache);
		// Adding request to request queue
		VolleyManager.getSharedInstance(mContext).addToRequestQueue(strReq, stringReqTag);
	}


	/**
	 * Function to download an image from URL.
	 * @param imgView : The imageview to which image needs to be set
	 * @param imgUrl: The URL from where the image will be downloaded
	 * @param loadingImage: The loader image to be shown on view till the time the downloaded image does not show up
	 * @param errorImage: The error image to be shown if downloading fails
	 */
	public void setBitmapInImageViewFromURL(final ImageView imgView, String imgUrl, int loadingImage, int errorImage)
	{
		ImageLoader imgLoader = VolleyManager.getSharedInstance(mContext).getImageLoader();

		imgLoader.get(imgUrl, new ImageListener() 
		{			
			@Override
			public void onErrorResponse(VolleyError volleyError) 
			{
				sendErrorCallback(volleyError);
				Log.e("TAG", "Image Load Error: " + volleyError.getMessage());
			}

			@Override
			public void onResponse(ImageContainer response, boolean arg1) 
			{
				if(response.getBitmap() != null)
					imgView.setImageBitmap(response.getBitmap());
			}
		}, R.drawable.ico_loader,R.drawable.ico_error);
	}

	/**
	 * Function to fetch the cached data for the specified URL.
	 * @param url: The url for which cache data is required
	 */
	public void getCachedData (String url)
	{
		Cache cache = VolleyManager.getSharedInstance(mContext).getRequestQueue().getCache();

		Entry entry = cache.get(url);

		if(entry != null)
		{
			try 
			{
				String data = new String(entry.data, "UTF-8");
				Log.d("",data);
				// handle data, like converting it to xml, json, bitmap etc.,
			}
			catch (UnsupportedEncodingException e) 
			{      
				e.printStackTrace();
			}
		}
		else
		{

		}
	}
	
	/**
	 * Function to invalidate cache
	 * @param url : URL for whcih the cache needs to be invalidated
	 */
	public void invalidateCache(String url)
	{
		if(cache == null)
			return;
		cache.invalidate(url, true);
	}
	
	/**
	 * Function to delete cache for the specified URL.
	 * @param url
	 */
	public void removeCache(String url)
	{
		if(cache == null)
			return;
		cache.remove(url);
	}
	
	/**
	 * Clears cache for all the URLs
	 */
	public void clearCache()
	{
		if(cache == null)
			return;
		cache.clear();
	}
	
	/**
	 * Cancels all the requests for the give tag
	 * @param tag
	 */
	public void cancelAllRequestsWithTag(String tag)
	{
		VolleyManager.getSharedInstance(mContext).getRequestQueue().cancelAll(tag);
	}
	
	/**
	 * Function to cancel all the requests
	 */
	public void cancelAllRequests()
	{
		VolleyManager.getSharedInstance(mContext).getRequestQueue().cancelAll(null);
	}
	
	private void showDialog(boolean show)
	{
		if(show)
		{
			pDialog.show();
		}
		else
			if(pDialog.isShowing())
				pDialog.hide();
	}
}
