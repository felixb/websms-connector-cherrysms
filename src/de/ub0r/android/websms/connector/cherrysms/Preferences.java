/*
 * Copyright (C) 2010 Felix Bechstein
 * 
 * This file is part of WebSMS.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/>.
 */
package de.ub0r.android.websms.connector.cherrysms;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;
import de.ub0r.android.websms.connector.common.Log;

/**
 * Preferences.
 * 
 * @author flx
 */
public final class Preferences extends PreferenceActivity implements
		OnPreferenceClickListener {
	/** Tag for output. */
	private static final String TAG = "cherry.pref";

	/** Preference key: enabled. */
	static final String PREFS_ENABLED = "enable_cherrysms";
	/** Preference's name: user's password. */
	static final String PREFS_PASSWORD = "password_cherrysms";

	/** Base referral URL. */
	private static final String REF_URL = "http://www.cherry-sms.com/?ref=";
	// "http://www.cherry-sms.com/index_iPhone.php?action=Register&ref=";
	/** Ids of referrals. */
	private static final String[] REF_IDS = new String[] { "DWNWAAAU",
			"ZXAQDHJW", "JJAZACFZ" };

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.xml.connector_cherrysms_prefs);
		this.findPreference("new_account").setOnPreferenceClickListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onPreferenceClick(final Preference preference) {
		final int i = (int) Math.floor(Math.random() * REF_IDS.length);
		final String url = REF_URL + REF_IDS[i];
		final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		Log.i(TAG, "Referral URL: " + url);
		this.startActivity(intent);
		return true;
	}
}
