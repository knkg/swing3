/*
    Open Manager, an open source file manager for the Android system
    Copyright (C) 2009, 2010, 2011  Joe Berria nexesdevelopment@gmail.com

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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;

public class WirelessManager extends Activity {
	//index values to access the elements in the TextView array.
	private final int SSTRENGTH = 0;
	private final int WIFISTATE = 1;
	private final int IPADD 	= 2;
	private final int MACADD 	= 3;
	private final int SSID 		= 4;
	private final int LINKSPD 	= 5;	
	
	private TextView[] data_labels;
	private TextView name_label;
	private TextView enable_label;
	private Button state_button;
	private Button back_button;
	private WifiManager wifi;
	
	int backbutton;
	int zipbutton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		int main = this.getResources().getIdentifier("manager_info_layout", "layout", this.getPackageName());
		int first_title = this.getResources().getIdentifier("first_title", "id", this.getPackageName());
		int second_title = this.getResources().getIdentifier("second_title", "id", this.getPackageName());
		int third_title = this.getResources().getIdentifier("third_title", "id", this.getPackageName());
		int fourth_title = this.getResources().getIdentifier("fourth_title", "id", this.getPackageName());
		int fifth_title = this.getResources().getIdentifier("fifth_title", "id", this.getPackageName());

		int dirs_label = this.getResources().getIdentifier("dirs_label", "id", this.getPackageName());
		int files_label = this.getResources().getIdentifier("files_label", "id", this.getPackageName());
		int time_stamp = this.getResources().getIdentifier("time_stamp", "id", this.getPackageName());
		int total_size = this.getResources().getIdentifier("total_size", "id", this.getPackageName());
		int free_space = this.getResources().getIdentifier("free_space", "id", this.getPackageName());

		setContentView(main);


		wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		TextView[] titles = new TextView[6];
		data_labels = new TextView[6];
		
		int[] left_views = {first_title, second_title, third_title,
					   fourth_title, fifth_title};
		
		/*R.layout.info_layout is the same layout used for directory info.
		 *Re-using the layout for this activity, so id tag names may not make sense,
		 *but are in the correct order.
		 */
		int[] right_views = {dirs_label, files_label, time_stamp,
							 total_size, free_space};
		String[] labels = {"Signal strength", "WIFI State", "ip address",
						  "mac address", "SSID", "link speed"};
		
		for (int i = 0; i < 5; i++) {
			titles[i] = (TextView)findViewById(left_views[i]);
			titles[i].setText(labels[i]);
			
			data_labels[i] = (TextView)findViewById(right_views[i]);
			data_labels[i].setText("N/A");
		}
		
		int namelabel = this.getResources().getIdentifier("name_label", "id", this.getPackageName());
		int pathlabel = this.getResources().getIdentifier("path_label", "id", this.getPackageName());
		backbutton = this.getResources().getIdentifier("back_button", "id", this.getPackageName());
		zipbutton = this.getResources().getIdentifier("zip_button", "id", this.getPackageName());

		name_label = (TextView)findViewById(namelabel);
		enable_label = (TextView)findViewById(pathlabel);
		state_button = (Button)findViewById(backbutton);
		back_button = (Button)findViewById(zipbutton);
		back_button.setText(" Back ");
		
		state_button.setOnClickListener(new ButtonHandler());
		back_button.setOnClickListener(new ButtonHandler());
		
		int info_icon = this.getResources().getIdentifier("info_icon", "id", this.getPackageName());
		int wireless = this.getResources().getIdentifier("wireless", "drawable", this.getPackageName());
		ImageView icon = (ImageView)findViewById(info_icon);
		icon.setImageResource(wireless);
				
		get_wifi();
	}
	
	private void get_wifi() {
		WifiInfo info = wifi.getConnectionInfo();
		int state = wifi.getWifiState();
		int strength = WifiManager.calculateSignalLevel(info.getRssi(), 5);
		boolean enabled = wifi.isWifiEnabled();
		
		name_label.setText(info.getSSID());
		enable_label.setText(enabled ?"Your wifi is enabled" :"Your wifi is not enabled");
		state_button.setText(enabled ?"Disable wifi" : "Enable wifi");
		
		switch(state) {
			case WifiManager.WIFI_STATE_ENABLED:
				data_labels[WIFISTATE].setText(" Enabled");
				break;
			case WifiManager.WIFI_STATE_DISABLED:
				data_labels[WIFISTATE].setText(" Disabled");
				break;
			case WifiManager.WIFI_STATE_DISABLING:
				data_labels[WIFISTATE].setText(" Being Disabled");
				break;
			case WifiManager.WIFI_STATE_ENABLING:
				data_labels[WIFISTATE].setText(" Being Enabled");
				break;
			case WifiManager.WIFI_STATE_UNKNOWN:
				data_labels[WIFISTATE].setText(" Unknown");
				break;
		}
		if(enabled) {
			data_labels[IPADD].setText(FileManager.integerToIPAddress(info.getIpAddress()));
			data_labels[MACADD].setText(info.getMacAddress());
			data_labels[SSID].setText(info.getSSID());
			data_labels[LINKSPD].setText(info.getLinkSpeed() + " Mbps");
			data_labels[SSTRENGTH].setText("strength " + strength);
		}else {
			data_labels[IPADD].setText("N/A");
			data_labels[MACADD].setText(info.getMacAddress());
			data_labels[SSID].setText("N/A");
			data_labels[LINKSPD].setText("N/A");
			data_labels[SSTRENGTH].setText("N/A");
		}
	}
	
	private class ButtonHandler implements OnClickListener {

		public void onClick(View v) {
			
			if(v.getId() == backbutton) {
				if(wifi.isWifiEnabled()){
					wifi.setWifiEnabled(false);
					state_button.setText("Enable wifi");
				}else {
					wifi.setWifiEnabled(true);
					state_button.setText("Disable wifi");
					get_wifi();
				}	
			}else if(v.getId() == zipbutton)
				finish();
		}
	}

}
