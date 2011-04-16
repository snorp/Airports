/*
 * Airports for Android
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

import android.database.Cursor;
import android.view.View;
import android.widget.TextView;

import com.nadmm.airports.DatabaseManager.Airports;
import com.nadmm.airports.DatabaseManager.States;

public class GuiUtils {

    public static void showAirportTitle( View root, Cursor c ) {
        TextView tv = (TextView) root.findViewById( R.id.airport_title );
        String code = c.getString( c.getColumnIndex( Airports.ICAO_CODE ) );
        if ( code == null || code.length() == 0 ) {
            code = c.getString( c.getColumnIndex( Airports.FAA_CODE ) );
        }
        String name = c.getString( c.getColumnIndex( Airports.FACILITY_NAME ) );
        String title = code + " - " + name;
        tv.setText( title );
        tv = (TextView) root.findViewById( R.id.airport_info );
        String type = c.getString( c.getColumnIndex( Airports.FACILITY_TYPE ) );
        String city = c.getString( c.getColumnIndex( Airports.ASSOC_CITY ) );
        String state = c.getString( c.getColumnIndex( States.STATE_NAME ) );
        String info = type+", "+city+", "+state;
        tv.setText( info );
        tv = (TextView) root.findViewById( R.id.airport_info2 );
        int distance = c.getInt( c.getColumnIndex( Airports.DISTANCE_FROM_CITY_NM ) );
        String dir = c.getString( c.getColumnIndex( Airports.DIRECTION_FROM_CITY ) );
        String status = c.getString( c.getColumnIndex( Airports.STATUS_CODE ) );
        tv.setText( DataUtils.decodeStatus( status )+", "
                +String.valueOf( distance )+" miles "+dir+" of city center" );
        tv = (TextView) root.findViewById( R.id.airport_info3 );
        int elev_msl = c.getInt( c.getColumnIndex( Airports.ELEVATION_MSL ) );
        String info2 = String.valueOf( elev_msl )+"' MSL elevation, ";
        int tpa_agl = c.getInt( c.getColumnIndex( Airports.PATTERN_ALTITUDE_AGL ) );
        String est = "";
        if ( tpa_agl == 0 ) {
            tpa_agl = 1000;
            est = " (est.)";
        }
        info2 += String.valueOf( elev_msl+tpa_agl)+"' MSL TPA"+est;
        tv.setText( info2 );
    }

}
