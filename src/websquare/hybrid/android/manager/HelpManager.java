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

import websquare.hybrid.android.swing.R;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HelpManager extends Activity implements OnClickListener {
	private static final String[] EMAIL = {"nexesdevelopment@gmail.com"};
	private static final String WEB = "http://nexesdevelopment.webs.com";
	
	int help_email_bt, help_website_bt;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		int main = this.getResources().getIdentifier("manager_help_layout", "layout", this.getPackageName());
		int help_top_label = this.getResources().getIdentifier("help_top_label", "id", this.getPackageName());
		help_email_bt = this.getResources().getIdentifier("help_email_bt", "id", this.getPackageName());
		help_website_bt = this.getResources().getIdentifier("help_website_bt", "id", this.getPackageName());
		int icon64 = this.getResources().getIdentifier("icon64", "id", this.getPackageName());
	
		setContentView(R.layout.manager_help_layout);
		
		String text = "Open Manager: If you have any questions or "
						+"comments, please email the developer or visit "
						+"the Open Manager web page.\n\nThank you\n\n";
		
		TextView label = (TextView)findViewById(help_top_label);
		label.setText(text);
		
		ImageView image = (ImageView)findViewById(icon64);
		image.setImageResource(getResources().getIdentifier("icon64", "drawable", getPackageName()));
		
		Button email = (Button)findViewById(help_email_bt);
		Button web = (Button)findViewById(help_website_bt);
		email.setOnClickListener(this);
		web.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		Intent i = new Intent();

		if (id == help_email_bt) {
			i.setAction(android.content.Intent.ACTION_SEND);
			i.setType("message/rfc822");
			i.putExtra(Intent.EXTRA_EMAIL, EMAIL);
			try {
				startActivity(Intent.createChooser(i, "Email using..."));
				
			} catch(ActivityNotFoundException e) {
				Toast.makeText(this, "Sorry, could not start the email", Toast.LENGTH_SHORT).show();
			}
		} else if (id == help_website_bt) {
			i.setAction(android.content.Intent.ACTION_VIEW);
			i.setData(Uri.parse(WEB));
			try {
				startActivity(i);
				
			} catch(ActivityNotFoundException e) {
				Toast.makeText(this, "Sorry, could not open the website", Toast.LENGTH_SHORT).show();
			}
		}
	}

}
