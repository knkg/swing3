package websquare.hybrid.android.signature;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import org.json.JSONException;

import websquare.hybrid.android.mfilechooser.Constants;

import java.io.File;

public class SignatureActivity extends Activity {
	private static final String TAG = "SignatureActivity";
	public static final String SIGNATURE = "signature";
	public static final String IS_SIGNATURE = "is_signature";
	public static final int EDIT_SIGNATURE = 0;

	private Button mClear;
	private Button mSave;
	private SignatureEditor mSignatureEditor;
	private boolean mIsSignature;
	private MenuItem mDoneMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		int main = this.getResources().getIdentifier("signature_main", "layout", this.getPackageName());
		int welcome_username = this.getResources().getIdentifier("welcome_username", "id", this.getPackageName());
		int phrase = this.getResources().getIdentifier("phrase", "id", this.getPackageName());
		int clear = this.getResources().getIdentifier("clear", "id", this.getPackageName());
		int save = this.getResources().getIdentifier("save", "id", this.getPackageName());
		int signature = this.getResources().getIdentifier("signature", "id", this.getPackageName());
			
		int signature_phrase = this.getResources().getIdentifier("signature_phrase", "string", this.getPackageName());
		int appname = this.getResources().getIdentifier("app_name", "string", this.getPackageName());
		
		
		setContentView(main);

		((TextView) findViewById(welcome_username))
				.setVisibility(View.GONE);
		((TextView) findViewById(phrase)).setText(getString(
				signature_phrase,
				getString(appname)));

		mClear = (Button) findViewById(clear);
		mSave = (Button) findViewById(save);
		mSignatureEditor = (SignatureEditor) findViewById(signature);
		mSignatureEditor.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE
						&& mSignatureEditor.isDrawed()) {
					setGhostedText(false);
				}
				return false;
			}

		});

		mClear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mSignatureEditor.erase();
				setGhostedText(true);
			}
		});

		mSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveSignature();
				
			}
		});
		
		//setResult(RESULT_CANCELED);
		//finish();

		/*Intent intent = getIntent();
		if (intent != null) {
			boolean showSig = intent.getBooleanExtra(ArtistFields.SHOW_SIG,
					true);
			String userSignId = intent.getStringExtra(ArtistFields.USER_SIGNID);
			mAttachSignature.setChecked(showSig);
			mSignatureEditor.setUserSigUrl(userSignId);
			mSignatureEditor.loadSignature();
			if (userSignId != null && !"".equals(userSignId))
				setGhostedText(false);
		}*/
		String userSignId = "http://swing.websquare.co.kr/upload/signature.png";
		mSignatureEditor.setUserSigUrl(userSignId);
		mSignatureEditor.loadSignature();
		if (userSignId != null && !"".equals(userSignId))
			setGhostedText(false);
	}

	/**
	 * Signature에 GhostedText를 보여주거나 숨긴다.
	 * 
	 * @param isShow
	 *            GhostedText의 존재 유무
	 */
	private void setGhostedText(boolean isText) {
		int border_with_ghosted_text = this.getResources().getIdentifier("border_with_ghosted_text", "drawable", this.getPackageName());
		int border_without_ghosted_text = this.getResources().getIdentifier("border_without_ghosted_text", "drawable", this.getPackageName());
		
		if (isText)
			mSignatureEditor
					.setBackgroundResource(border_with_ghosted_text);
		else
			mSignatureEditor
					.setBackgroundResource(border_without_ghosted_text);
		mIsSignature = !isText;
	}

	/**
	 * Signature를 저장한다.
	 */
	private void saveSignature() {
		String path[] = mSignatureEditor.saveSignature(this);
		if (path == null || path.length < 2)
			throw new IllegalArgumentException("Illegal Signature");
		
		Uri signatureUri = Uri.fromFile(new File(
				path[SignatureEditor.SIGNATURE]));
		if (mIsSignature){
			Intent intent = new Intent();
			intent.putExtra(Constants.KEY_FILE_SELECTED,
					signatureUri.toString());
			setResult(Activity.RESULT_OK, intent);
			finish();
		}else{
			Intent intent = new Intent();
			intent.putExtra(Constants.KEY_FILE_SELECTED,"");
			setResult(Activity.RESULT_OK, intent);
			finish();
		}
		
		/*AccountManager manager = new AccountManager(this);
		manager.setRequestListener(this);
		Uri signatureUri = Uri.fromFile(new File(
				path[SignatureEditor.SIGNATURE]));
		if (mIsSignature)
			manager.registerSignature(EDIT_SIGNATURE, signatureUri,
					mAttachSignature.isChecked());
		else
			manager.deleteSignature(EDIT_SIGNATURE,
					mAttachSignature.isChecked());*/
		
	}
}
