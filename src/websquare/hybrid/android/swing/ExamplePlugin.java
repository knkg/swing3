package websquare.hybrid.android.swing;

import java.util.Random;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ExamplePlugin extends CordovaPlugin
{
    public static final String TAG                = "ExamplePlugin";
    public CallbackContext mProcessDataCallbackId; 
    
    @Override
    public boolean execute(final String action, JSONArray args, final CallbackContext callbackContext){
        Log.d(TAG, "execute() called. Action: " + action);
        PluginResult result = null;
        if (action.equals("processData")) {
        	double dataToProcess = 0;
        	try {
        		dataToProcess = args.getDouble(0);
        		
        		startProcessingData(dataToProcess);
            	mProcessDataCallbackId = callbackContext;
            	result = new PluginResult(PluginResult.Status.NO_RESULT);
            	result.setKeepCallback(true);
            	callbackContext.sendPluginResult(result);
            } catch (JSONException jsonEx) {
                Log.e(TAG, "Got JSON Exception " + jsonEx.getMessage());
                jsonEx.printStackTrace();
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION));
            }
            return true;
        } else {
			callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
            Log.e(TAG, "Invalid action: " + action);
			return false;
        }
    }

    private void startProcessingData(final double inputNumber) {
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(15000);  //sleep 15 seconds
                } catch (InterruptedException e) {
                    Log.e(TAG, "InterruptedException in thread. " + e.getMessage());
                    e.printStackTrace();
                }
                Random r = new Random();
                double processedData = r.nextDouble() + inputNumber;
                onProcessDataReadSuccess(processedData);
            }
        }.start();
    }

    private void onProcessDataReadSuccess(double processedData) {
        Log.d(TAG, "onCardDataReadSuccess() called. Processed data: " + processedData);
        PluginResult result;
        try
        {
            JSONObject resultJSON = new JSONObject();
            resultJSON.put("processedData", processedData);
            result = new PluginResult(PluginResult.Status.OK, resultJSON);
        }
        catch (JSONException jsonEx)
        {
            Log.e(TAG, "Got JSON Exception " + jsonEx.getMessage());
            jsonEx.printStackTrace();
            result = new PluginResult(PluginResult.Status.JSON_EXCEPTION);
        }
        //callback to the javascript layer with our result
        //using the held callbackId via success(PluginResult result, String callbackId)
        result.setKeepCallback(false);
	    if (this.mProcessDataCallbackId != null) {
	        this.mProcessDataCallbackId.sendPluginResult(result);
	        this.mProcessDataCallbackId = null;
	    }
    }
}