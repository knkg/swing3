package websquare.hybrid.android.manager;

import java.util.ArrayList;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

public class MFileChooser extends CordovaPlugin {
    private static final String TAG = "MFileChooser";
    private static final String ACTION_OPEN = "open";
    private static final int PICK_FILE_REQUEST = 1;
    CallbackContext callback;
	ArrayList<String> exts;
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        exts = new ArrayList<String>();
       
        int count = args.length();
        
        for(int i = 0;i<count;i++)
        {
        	exts.add(args.getString(i).toLowerCase());
        }
  	
		if (action.equals(ACTION_OPEN)) {
            chooseFile(callbackContext,exts);
            return true;
        }	 
        
        return false;
	}
	
    public void chooseFile(CallbackContext callbackContext, ArrayList<String> ext) {

        // type and title should be configurable
    	Context context=this.cordova.getActivity().getApplicationContext();
        /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setClass(context,Main.class);
        
        if(ext.size()>0)
        {
        	intent.putStringArrayListExtra(Constants.KEY_FILTER_FILES_EXTENSIONS, ext);
        }
        cordova.startActivityForResult(this, intent, PICK_FILE_REQUEST);
        */
      
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }

        try {
        	cordova.startActivityForResult(this, Intent.createChooser(intent,"Select Picture"), PICK_FILE_REQUEST);
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
        PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        pluginResult.setKeepCallback(true);
        callback = callbackContext;
        callbackContext.sendPluginResult(pluginResult);
    }
    
    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_FILE_REQUEST && callback != null) {

            if (resultCode == Activity.RESULT_OK) {

                //Uri uri = data.getData();
                String uri = data.getStringExtra(Constants.KEY_FILE_SELECTED);
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
    
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FILE_REQUEST && callback != null) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && uri == null) {
                    ClipData clipData = data.getClipData();
                    int count = clipData.getItemCount();
                    for (int i = 0; i < count; ++i) {
                        uri = clipData.getItemAt(i).getUri();
                        if (uri != null) {
                        	callback.success(uri.toString());
                        }
                    }
                }
                else if (uri != null) {
                	callback.success(uri.toString());
                }else {

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
        super.onActivityResult(requestCode, resultCode, data);
    }

}
