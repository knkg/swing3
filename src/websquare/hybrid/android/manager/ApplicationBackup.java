/*
    Open Manager, an open source file manager for the Android system
    Copyright (C) 2009, 2010, 2011  Joe Berria <nexesdevelopment@gmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package websquare.hybrid.android.manager;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * This class is used to display an activity to the user so they can
 * view the third party applications on their phone and have a button
 * that gives them the ability to backup said applications to the SDCard
 * <br>
 * <p>
 * The location that the backup will be placed is at 
 * <br>/sdcard/open manager/AppBackup/
 * <br>
 * note: that /sdcard/open manager/ should already exists. This is check at start
 * up from the SettingsManager class.
 * 
 * @author Joe Berria <nexesdevelopment@gmail.com>
 */


public class ApplicationBackup extends ListActivity implements OnClickListener {
	private static final String BACKUP_LOC = "/sdcard/open manager/AppBackup/";
	private static final int SET_PROGRESS = 0x00;
	private static final int FINISH_PROGRESS = 0x01;
	private static final int FLAG_UPDATED_SYS_APP = 0x80;
	
	private ArrayList<ApplicationInfo> mAppList;
	private TextView mAppLabel;
	private PackageManager mPackMag;
	private ProgressDialog mDialog;
	
	/*
	 * Our handler object that will update the GUI from 
	 * our background thread. 
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			
			switch(msg.what) {
				case SET_PROGRESS:
					mDialog.setMessage((String)msg.obj);
					break;
				case FINISH_PROGRESS:
					mDialog.cancel();
					Toast.makeText(ApplicationBackup.this, 
								   "Applications have been backed up", 
								   Toast.LENGTH_SHORT).show();
					break;
			}
		}
	};

	int appicon;
	int tablerow;
	int top_view, bottom_view, multiselect_icon, row_image;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		int main = this.getResources().getIdentifier("manager_backup_layout", "layout", this.getPackageName());
		int backup_label = this.getResources().getIdentifier("backup_label", "id", this.getPackageName());
		int backup_button_all = this.getResources().getIdentifier("backup_button_all", "id", this.getPackageName());
		setContentView(main);
				
		tablerow = this.getResources().getIdentifier("manager_tablerow", "layout", this.getPackageName());
		appicon = this.getResources().getIdentifier("appicon", "drawable", this.getPackageName());
		top_view = this.getResources().getIdentifier("top_view", "id", this.getPackageName());
		bottom_view = this.getResources().getIdentifier("bottom_view", "id", this.getPackageName());
		multiselect_icon = this.getResources().getIdentifier("multiselect_icon", "id", this.getPackageName());
		row_image = this.getResources().getIdentifier("row_image", "id", this.getPackageName());
		
		mAppLabel = (TextView)findViewById(backup_label);
		Button button = (Button)findViewById(backup_button_all);
		
		button.setOnClickListener(this);
		
		mAppList = new ArrayList<ApplicationInfo>();
		mPackMag = getPackageManager();
		
		get_downloaded_apps();
		setListAdapter(new TableView());
	}
	
	
	@Override
	public void onClick(View view) {
		mDialog = ProgressDialog.show(ApplicationBackup.this, 
				  					  "Backing up applications",
				  					  "", true, false);
		
		Thread all = new Thread(new BackgroundWork(mAppList));
		all.start();
	}
	
	private void get_downloaded_apps() {
		List<ApplicationInfo> all_apps = mPackMag.getInstalledApplications(
											PackageManager.GET_UNINSTALLED_PACKAGES);
		
		for(ApplicationInfo appInfo : all_apps) {
			if((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && 
			   (appInfo.flags & FLAG_UPDATED_SYS_APP) == 0 && 
			   appInfo.flags != 0)
				
				mAppList.add(appInfo);
		}
		
		mAppLabel.setText("You have " +mAppList.size() + " downloaded apps");
	}


	/*
	 * This private inner class will perform the backup of applications
	 * on a background thread, while updating the user via a message being
	 * sent to our handler object.
	 */
	private class BackgroundWork implements Runnable {
		private static final int BUFFER = 256;
		
		private ArrayList<ApplicationInfo> mDataSource;
		private File mDir = new File(BACKUP_LOC);
		private byte[] mData;
		
		public BackgroundWork(ArrayList<ApplicationInfo> data)  {
			mDataSource = data;
			mData =  new byte[BUFFER];
						
			/*create dir if needed*/
			File d = new File("/sdcard/open manager/");
			if(!d.exists()) {
				d.mkdir();
				
				//then create this directory
				mDir.mkdir();
				
			} else {
				if(!mDir.exists())
					mDir.mkdir();
			}
		}

		public void run() {
			BufferedInputStream mBuffIn;
			BufferedOutputStream mBuffOut;
			Message msg;
			int len = mDataSource.size();
			int read = 0;
			
			for(int i = 0; i < len; i++) {
				ApplicationInfo info = mDataSource.get(i);
				String source_dir = info.sourceDir;
				String out_file = source_dir.substring(source_dir.lastIndexOf("/") + 1, source_dir.length());

				try {
					mBuffIn = new BufferedInputStream(new FileInputStream(source_dir));
					mBuffOut = new BufferedOutputStream(new FileOutputStream(BACKUP_LOC + out_file));
					
					while((read = mBuffIn.read(mData, 0, BUFFER)) != -1)
						mBuffOut.write(mData, 0, read);
					
					mBuffOut.flush();
					mBuffIn.close();
					mBuffOut.close();
					
					msg = new Message();
					msg.what = SET_PROGRESS;
					msg.obj = i + " out of " + len + " apps backed up";
					mHandler.sendMessage(msg);
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			mHandler.sendEmptyMessage(FINISH_PROGRESS);
		}
	}
	
	private static class AppViewHolder {
		TextView top_view;
		TextView bottom_view;
		ImageView icon;
		ImageView check_mark;
	}
	
	private class TableView extends ArrayAdapter<ApplicationInfo> {
		
		private TableView() {
			super(ApplicationBackup.this, tablerow, mAppList);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppViewHolder holder;
			ApplicationInfo info = mAppList.get(position);
			
			if(convertView == null) {
				LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(tablerow, parent, false);
				
				holder = new AppViewHolder();
				holder.top_view = (TextView)convertView.findViewById(top_view);
				holder.bottom_view = (TextView)convertView.findViewById(bottom_view);
				holder.check_mark = (ImageView)convertView.findViewById(multiselect_icon);
				holder.icon = (ImageView)convertView.findViewById(row_image);
				holder.icon.setMaxHeight(40);
				convertView.setTag(holder);
				
			} else {
				holder = (AppViewHolder) convertView.getTag();
			}
			
			holder.top_view.setText(info.processName);
			holder.bottom_view.setText(info.packageName);
			
			//this should not throw the exception
			try {
				holder.icon.setImageDrawable(mPackMag.getApplicationIcon(info.packageName));
			} catch (NameNotFoundException e) {
				holder.icon.setImageResource(appicon);
			}
			
			return convertView;
		}
	}
}
