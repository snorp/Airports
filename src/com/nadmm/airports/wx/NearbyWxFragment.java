/*
 * FlightIntel for Pilots
 *
 * Copyright 2012 Nadeem Hasan <nhasan@nadmm.com>
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

package com.nadmm.airports.wx;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import com.nadmm.airports.DatabaseManager;
import com.nadmm.airports.DatabaseManager.LocationColumns;

public class NearbyWxFragment extends WxListFragmentBase {

    private final class NearbyWxTask extends AsyncTask<Location, Void, Cursor> {

        @Override
        protected Cursor doInBackground( Location... params ) {
            if ( getActivity() == null ) {
                cancel( false );
                return null;
            }

            Location location = params[ 0 ];
            Bundle args = getArguments();
            int radius = args.getInt( LocationColumns.RADIUS );
            SQLiteDatabase db = getDatabase( DatabaseManager.DB_FADDS );

            return new NearbyWxCursor( db, location, radius );
        }

        @Override
        protected void onPostExecute( Cursor c ) {
            setCursor( c );
        }

    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        setEmptyText( "No wx stations found nearby." );
        super.onCreate( savedInstanceState );
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState ) {
        Bundle args = getArguments();
        Location location = (Location) args.getParcelable( LocationColumns.LOCATION );
        if ( location != null ) {
            onLocationChanged( location );
        }

        super.onActivityCreated( savedInstanceState );
    }

    @Override
    public void onLocationChanged( Location location ) {
        new NearbyWxTask().execute( location );
    }

}
