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

package com.nadmm.airports.utils;

import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public abstract class SectionedCursorAdapter extends ResourceCursorAdapter {

    /**
     * Id of the view that represents the section header in list item layout
     */
    private int mSectionId;
    /**
     * Map to cache the section names for each row in the cursor
     */
    private HashMap<Integer, String> mSectionNames;

    public SectionedCursorAdapter( Context context, int layout, Cursor c, int sectionId ) {
        super( context, layout, c, 0 );
        mSectionId = sectionId;
        int count = ( c != null )? c.getCount() : 0;
        mSectionNames = new HashMap<Integer, String>( count );
    }

    /**
     * Called by newView() to get the section name. If the section name is different than
     * previous section name, a new section has begun and the section header is made visible.
     * Cursor is already pointing to the correct row
     * @return Key that uniquely identifies a section
     */
    public abstract String getSectionName();

    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {
        View view = super.getView( position, convertView, parent );

        // Get the view that represents the section header
        TextView section = (TextView) view.findViewById( mSectionId );
        section.setVisibility( View.GONE );
        section.setClickable( false );
        section.setFocusable( false );

        String curSectionName = getSectionName( position );

        if ( position == 0 ) {
            if ( curSectionName != null ) {
                // First item always starts a new section
                section.setVisibility( View.VISIBLE );
                section.setText( curSectionName );
            }
        } else {
            Cursor c = getCursor();
            // Need to check if this position starts a new section by comparing the section
            // name of this row with the section name of the previous row
            c.moveToPrevious();
            String prevSectionName = getSectionName( position-1 );
            // Restore cursor position
            c.moveToNext();

            if ( curSectionName != null && !curSectionName.equals( prevSectionName ) ) {
                // A new section begins at this position, show the section header
                section.setVisibility( View.VISIBLE );
                section.setText( curSectionName );
            }
        }

        return view;
    }

    private String getSectionName( int position ) {
        String name = mSectionNames.get( position );
        if ( name == null ) {
            name = getSectionName();
            mSectionNames.put( position, name );
        }
        return name;
    }

    @Override
    public void changeCursor( Cursor c ) {
        super.changeCursor( c );
        // Reset section name cache
        mSectionNames.clear();
    }

    @Override
    protected void onContentChanged() {
        super.onContentChanged();
        // Reset section name cache
        mSectionNames.clear();
    }

}
