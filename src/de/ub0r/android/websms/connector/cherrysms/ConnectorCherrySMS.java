/*
 * Copyright (C) 2010-2011 Felix Bechstein
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

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import de.ub0r.android.websms.connector.common.BasicConnector;
import de.ub0r.android.websms.connector.common.ConnectorCommand;
import de.ub0r.android.websms.connector.common.ConnectorSpec;
import de.ub0r.android.websms.connector.common.Log;
import de.ub0r.android.websms.connector.common.Utils;
import de.ub0r.android.websms.connector.common.WebSMSException;

/**
 * AsyncTask to manage IO to cherry-sms.com API.
 * 
 * @author flx
 */
public final class ConnectorCherrySMS extends BasicConnector {
	/** Tag for output. */
	private static final String TAG = "cherry";
	/** SubConnectorSpec ID: with sender. */
	private static final String ID_W_SENDER = "w_sender";
	/** SubConnectorSpec ID: without sender. */
	private static final String ID_WO_SENDER = "wo_sender";
	/** Preference's name: hide with sender subcon. */
	private static final String PREFS_HIDE_W_SENDER = "hide_withsender";
	/** Preference's name: hide without sender subcon. */
	private static final String PREFS_HIDE_WO_SENDER = "hide_nosender";

	/** CherrySMS Gateway URL. */
	private static final String URL = "https://gw.cherry-sms.com/";
	/** Ad unitid. */
	private static final String AD_UNITID = "a14dbba90186ed3";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConnectorSpec initSpec(final Context context) {
		final String name = context
				.getString(R.string.connector_cherrysms_name);
		ConnectorSpec c = new ConnectorSpec(name);
		c.setAuthor(// .
				context.getString(R.string.connector_cherrysms_author));
		c.setBalance(null);
		c.setAdUnitId(AD_UNITID);
		c.setCapabilities(ConnectorSpec.CAPABILITIES_UPDATE
				| ConnectorSpec.CAPABILITIES_SEND
				| ConnectorSpec.CAPABILITIES_PREFS);
		final SharedPreferences p = PreferenceManager
				.getDefaultSharedPreferences(context);
		if (!p.getBoolean(PREFS_HIDE_WO_SENDER, false)) {
			c.addSubConnector(ID_WO_SENDER, context
					.getString(R.string.wo_sender), 0);
		}
		if (!p.getBoolean(PREFS_HIDE_W_SENDER, false)) {
			c.addSubConnector(ID_W_SENDER,
					context.getString(R.string.w_sender), 0);
		}
		return c;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConnectorSpec updateSpec(final Context context,
			final ConnectorSpec connectorSpec) {
		final SharedPreferences p = PreferenceManager
				.getDefaultSharedPreferences(context);
		if (p.getBoolean(Preferences.PREFS_ENABLED, false)) {
			if (p.getString(Preferences.PREFS_PASSWORD, "").length() > 0) {
				connectorSpec.setReady();
			} else {
				connectorSpec.setStatus(ConnectorSpec.STATUS_ENABLED);
			}
		} else {
			connectorSpec.setStatus(ConnectorSpec.STATUS_INACTIVE);
		}
		return connectorSpec;
	}

	/**
	 * Check return code from cherry-sms.com.
	 * 
	 * @param context
	 *            {@link Context}
	 * @param ret
	 *            return code
	 * @return true if no error code
	 */
	private static boolean checkReturnCode(final Context context, final int ret) {
		Log.d(TAG, "ret=" + ret);
		switch (ret) {
		case 100:
			return true;
		case 10:
			throw new WebSMSException(context, R.string.error_cherry_10);
		case 20:
			throw new WebSMSException(context, R.string.error_cherry_20);
		case 30:
			throw new WebSMSException(context, R.string.error_cherry_30);
		case 31:
			throw new WebSMSException(context, R.string.error_cherry_31);
		case 40:
			throw new WebSMSException(context, R.string.error_cherry_40);
		case 50:
			throw new WebSMSException(context, R.string.error_cherry_50);
		case 60:
			throw new WebSMSException(context, R.string.error_cherry_60);
		case 70:
			throw new WebSMSException(context, R.string.error_cherry_70);
		case 71:
			throw new WebSMSException(context, R.string.error_cherry_71);
		case 80:
			throw new WebSMSException(context, R.string.error_cherry_80);
		case 90:
			throw new WebSMSException(context, R.string.error_cherry_90);
		default:
			throw new WebSMSException(context, R.string.error, " code: " + ret);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getParamUsername() {
		return "user";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getParamPassword() {
		return "password";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getParamRecipients() {
		return "to";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getParamSender() {
		return "xxx";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getParamText() {
		return "message";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getUsername(final Context context,
			final ConnectorCommand command, final ConnectorSpec cs) {
		return Utils.international2oldformat(Utils.getSender(context, command
				.getDefSender()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getPassword(final Context context,
			final ConnectorCommand command, final ConnectorSpec cs) {
		final SharedPreferences p = PreferenceManager
				.getDefaultSharedPreferences(context);
		return Utils.md5(p.getString(Preferences.PREFS_PASSWORD, ""));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getRecipients(final ConnectorCommand command) {
		return Utils.joinRecipientsNumbers(Utils.national2international(command
				.getDefPrefix(), command.getRecipients()), ";", true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getSender(final Context context,
			final ConnectorCommand command, final ConnectorSpec cs) {
		return "xxx";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getUrlBalance(final ArrayList<BasicNameValuePair> d) {
		d.add(new BasicNameValuePair("check", "guthaben"));
		return URL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getUrlSend(final ArrayList<BasicNameValuePair> d) {
		return URL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean usePost() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addExtraArgs(final Context context,
			final ConnectorCommand command, final ConnectorSpec cs,
			final ArrayList<BasicNameValuePair> d) {
		boolean sendWithSender = false;
		final String sub = command.getSelectedSubConnector();
		if (sub != null && sub.equals(ID_W_SENDER)) {
			sendWithSender = true;
		}
		Log.d(TAG, "send with sender = " + sendWithSender);
		if (sendWithSender) {
			d.add(new BasicNameValuePair("from", "1"));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parseResponse(final Context context,
			final ConnectorCommand command, final ConnectorSpec cs,
			final String htmlText) {
		if (htmlText == null || htmlText.length() == 0) {
			throw new WebSMSException(context, R.string.error_service);
		}
		String[] lines = htmlText.split("\n");
		int l = lines.length;
		if (command.getType() == ConnectorCommand.TYPE_SEND) {
			try {
				final int ret = Integer.parseInt(lines[0].trim());
				checkReturnCode(context, ret);
				if (l > 1) {
					cs.setBalance(lines[l - 1].trim());
				}
			} catch (NumberFormatException e) {
				Log.e(TAG, "could not parse ret", e);
				throw new WebSMSException(e.getMessage());
			}
		} else {
			cs.setBalance(lines[l - 1].trim());
		}
	}
}
