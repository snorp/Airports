/*
 * FlightIntel for Pilots
 *
 * Copyright 2011 Nadeem Hasan <nhasan@nadmm.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */

package com.nadmm.airports;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.nadmm.airports.donate.DonateDatabase;

public class FlightIntel extends ActivityBase {

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        PreferenceManager.setDefaultValues( this, R.xml.preferences, false );
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( this );

        DonateDatabase db = new DonateDatabase( this );
        Cursor c = db.queryAlldonations();
        Application.sDonationDone = c.moveToFirst();

        boolean agreed = prefs.getBoolean( PreferencesActivity.KEY_DISCLAIMER_AGREED, false );
        if ( !agreed ) {
            // User has not yet agreed to the disclaimer, show it now
            Intent disclaimer = new Intent( this, DisclaimerActivity.class );
            startActivity( disclaimer );
            finish();
            return;
        }

        Intent intent = checkData();
        if ( intent != null ) {
            startActivity( intent );
            finish();
            return;
        }

        startHomeActivity();
        finish();
    }

}
