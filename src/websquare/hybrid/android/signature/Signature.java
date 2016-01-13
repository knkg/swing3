package websquare.hybrid.android.signature;

import java.util.ArrayList;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import websquare.hybrid.android.mfilechooser.Constants;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Signature extends CordovaPlugin {
    private static final String TAG = "Signature";
    private static final String ACTION_OPEN = "open";
    private static final int PICK_SIGNATURE_REQUEST = 1;
    private static final String KEY_FILE_SELECTED = "signature";
    private CallbackContext callbackContext;
	ArrayList<String> exts;
	
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		this.callbackContext = callbackContext;
		exts = new ArrayList<String>();
       
        int count = args.length();
        
        for(int i = 0;i<count;i++)
        {
        	exts.add(args.getString(i).toLowerCase());
        }
  	
		if (action.equals(ACTION_OPEN)) {
            showSignature(callbackContext,exts);
            //showSignature(args);
            return true;
        }	 
        
        return false;
	}
	/**
     * Starts an intent to scan and decode a barcode.
     */
    public void showSignature1(JSONArray args) {

        Context context=this.cordova.getActivity().getApplicationContext();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setClass(context, SignatureActivity.class);
     

        // avoid calling other phonegap apps
        intent.setPackage(this.cordova.getActivity().getApplicationContext().getPackageName());
        this.cordova.startActivityForResult((CordovaPlugin) this, intent, PICK_SIGNATURE_REQUEST);
    }
    
    public void showSignature(CallbackContext callbackContext, ArrayList<String> ext) {

        // type and title should be configurable
    	Context context=this.cordova.getActivity().getApplicationContext();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setClass(context,SignatureActivity.class);
        
        if(ext.size()>0)
        {
        	intent.putStringArrayListExtra(Constants.KEY_FILTER_FILES_EXTENSIONS, ext);
        }
        cordova.startActivityForResult(this, intent, PICK_SIGNATURE_REQUEST);
        
        PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        pluginResult.setKeepCallback(true);
        //callback = callbackContext;
        callbackContext.sendPluginResult(pluginResult);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == PICK_SIGNATURE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                JSONObject obj = new JSONObject();
                String uri = intent.getStringExtra(KEY_FILE_SELECTED);
                try {
                    obj.put(KEY_FILE_SELECTED, uri.toString());
                } catch (JSONException e) {
                    Log.d(TAG, "This should never happen");
                }
                //this.success(new PluginResult(PluginResult.Status.OK, obj), this.callback);
                this.callbackContext.success(obj);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put(KEY_FILE_SELECTED, "");
                } catch (JSONException e) {
                    Log.d(TAG, "This should never happen");
                }
                this.callbackContext.success(obj);
            } else {
                this.callbackContext.error("Unexpected error");
            }
        }
    }
    
    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_SIGNATURE_REQUEST && callback != null) {

            if (resultCode == Activity.RESULT_OK) {

                //Uri uri = data.getData();
                String uri = data.getStringExtra(KEY_FILE_SELECTED);
                if (uri != null) {

                    Log.w(TAG, uri.toString());
                    callback.success(uri.toString());

                } else {

                    callback.error("File uri was null");

                }

            } else if (resultCode == Activity.RESULT_CANCELED) {

                // TODO NO_RESULT or error callback?
                PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
                callback.sendPluginResult(pluginResult);

            } else {
                callback.error(resultCode);
            }
        }
    }*/

}
