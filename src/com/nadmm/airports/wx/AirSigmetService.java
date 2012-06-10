/*
 * FlightIntel for Pilots
 *
 * Copyright 2011-2012 Nadeem Hasan <nhasan@nadmm.com>
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

import java.io.File;
import java.net.URI;

import org.apache.http.client.utils.URIUtils;

import android.content.Intent;
import android.text.format.DateUtils;

import com.nadmm.airports.R;
import com.nadmm.airports.utils.UiUtils;

public class AirSigmetService extends NoaaService {

    private final String AIRSIGMET_IMAGE_NAME = "airmets_%s.gif";
    private final String AIRSIGMET_IMAGE_ZOOM_NAME = "zoom_airmets_%s.gif";
    private final String AIRSIGMET_TEXT_QUERY =
            "datasource=airsigmets&requesttype=retrieve&format=xml&compression=gzip"
            + "&hoursBeforeNow=%d&minLat=%.2f&maxLat=%.2f&minLon=%.2f&maxLon=%.2f";
    private final String AIRSIGMET_IMAGE_QUERY = "/data/airmets/";
    private final String AIRSIGMET_IMAGE_ZOOM_QUERY = "/data/airmets/zoom/";
    private final long AIRSIGMET_CACHE_MAX_AGE = 60*DateUtils.MINUTE_IN_MILLIS;

    AirSigmetParser mParser;

    public AirSigmetService() {
        super( "airsigmet" );
        mParser = new AirSigmetParser();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Remove any old files from cache first
        cleanupCache( DATA_DIR, AIRSIGMET_CACHE_MAX_AGE );
    }

    @Override
    protected void onHandleIntent( Intent intent ) {
        String action = intent.getAction();
        if ( action.equals( ACTION_GET_AIRSIGMET ) ) {
            String type = intent.getStringExtra( TYPE );
            if ( type.equals( TYPE_TEXT ) ) {
                String stationId = intent.getStringExtra( STATION_ID );
                double[] box = intent.getDoubleArrayExtra( COORDS_BOX );
                int hours = intent.getIntExtra( HOURS_BEFORE, 3 );
                boolean cacheOnly = intent.getBooleanExtra( CACHE_ONLY, false );
                boolean forceRefresh = intent.getBooleanExtra( FORCE_REFRESH, false );

                File xml = new File( DATA_DIR, "AIRSIGMET_"+stationId+".xml" );

                if ( forceRefresh || ( !cacheOnly && !xml.exists() ) ) {
                    try {
                        String query = String.format( AIRSIGMET_TEXT_QUERY,
                                hours, box[ 0 ], box[ 1 ], box[ 2 ], box[ 3 ] );
                        fetchFromNoaa( query, xml, true );
                    } catch ( Exception e ) {
                        UiUtils.showToast( this, "Unable to fetch AirSigmet: "+e.getMessage() );
                    }
                }

                AirSigmet airSigmet = new AirSigmet();

                if ( xml.exists() ) {
                    mParser.parse( xml, airSigmet );
                }

                // Broadcast the result
                Intent result = new Intent();
                result.setAction( action );
                result.putExtra( STATION_ID, stationId );
                result.putExtra( TYPE, TYPE_TEXT );
                result.putExtra( RESULT, airSigmet );
                sendBroadcast( result );
            } else if ( type.equals( TYPE_IMAGE ) ) {
                boolean hiRes = getResources().getBoolean( R.bool.WxHiResImages );
                String code = intent.getStringExtra( IMAGE_CODE );
                String imageName = String.format(
                        hiRes? AIRSIGMET_IMAGE_ZOOM_NAME : AIRSIGMET_IMAGE_NAME, code );
                File image = new File( DATA_DIR, imageName );
                if ( !image.exists() ) {
                    try {
                        String query = hiRes? AIRSIGMET_IMAGE_ZOOM_QUERY : AIRSIGMET_IMAGE_QUERY;
                        query += imageName;
                        URI uri = URIUtils.createURI( "http", NOAA_HOST, 80, query, null, null );
                        fetchFromNoaa( uri, image, false );
                    } catch ( Exception e ) {
                        UiUtils.showToast( this, "Unable to fetch image: "+e.getMessage() );
                    }
                }

                // Broadcast the result
                Intent result = new Intent();
                result.setAction( action );
                result.putExtra( TYPE, TYPE_IMAGE );
                result.putExtra( IMAGE_CODE, code );
                if ( image.exists() ) {
                    result.putExtra( RESULT, image.getAbsolutePath() );
                }
                sendBroadcast( result );
            }
        }
    }

}
