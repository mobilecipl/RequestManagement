package com.cipl.ciplapimanagementlibrary;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request.Method;


public class MainActivity extends Activity 
{
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        makeRequest();
    }

	private void makeRequest() 
	{
		RequestData requestData = new RequestData(this);
		requestData.shouldCache = true;
		requestData.requestUrl= "http://mibc.demos.classicinformatics.com/api/mobile/login";
		requestData.data = getRequestMap();
		requestData.requestType = Method.POST;
		requestData.onSuccessCallback = "onSuccess";
		requestData.targetObject = this;
		
		requestData.makeRequestForJsonObjResponse("json_obj_request");
	}

	private Map<String, String> getRequestMap()
	{
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", "cipl");
		map.put("password", "Classic123");
		map.put("usertype", "1");
		
		return map;
	}
	
	public void onSuccess(Object test)
	{
		Log.d("","In success");
	}
}
