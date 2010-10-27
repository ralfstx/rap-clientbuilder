/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.clientbuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.mozilla.javascript.Token;

import com.yahoo.platform.yui.compressor.JavaScriptToken;

public final class StringReplacer {

  private HashMap stringMap = new HashMap();
  private ArrayList strings;

  public void discoverStrings( TokenList tokens ) {
    if( strings != null ) {
      throw new IllegalStateException( "Can not add strings after computing indexes" );
    }
    int length = tokens.size();
    for( int pos = 0; pos < length; pos++ ) {
      if( isReplacableString( tokens, pos ) ) {
        String value = tokens.getToken( pos ).getValue();
        Integer count = ( Integer )stringMap.get( value );
        if( count == null ) {
          stringMap.put( value, new Integer( 1 ) );
        } else {
          stringMap.put( value, new Integer( count.intValue() + 1 ) );
        }
      }
    }
  }

  public void replaceStrings( TokenList tokens ) {
    if( strings == null ) {
      createStringList();
    }
    int length = tokens.size();
    for( int pos = length - 1; pos >= 0; pos-- ) {
      if( isReplacableString( tokens, pos ) ) {
        String value = tokens.getToken( pos ).getValue();
        int index = getIndexForString( value );
        JavaScriptToken[] replacement = createTokensForArrayAccess( "$", index );
        tokens.replaceToken( pos, replacement );
      }
    }
  }

  public String[] getStrings() {
    if( strings == null ) {
      createStringList();
    }
    String[] result = new String[ strings.size() ];
    strings.toArray( result );
    return result;
  }

  private void createStringList() {
    strings = new ArrayList( stringMap.keySet() );
    Comparator comparator = new Comparator() {

      public int compare( Object o1, Object o2 ) {
        Integer freq1 = ( Integer )stringMap.get( o1 );
        Integer freq2 = ( Integer )stringMap.get( o2 );
        return freq2.compareTo( freq1 );
      }
    };
    Collections.sort( strings, comparator );
  }

  private int getIndexForString( String string ) {
    int index = strings.indexOf( string );
    if( index == -1 ) {
      throw new IllegalArgumentException( "String not registered: " + string );
    }
    return index;
  }

  private static JavaScriptToken[] createTokensForArrayAccess( String arrayName,
                                                               int index )
  {
    JavaScriptToken[] replacement = new JavaScriptToken[] {
      new JavaScriptToken( Token.NAME, "$" ),
      new JavaScriptToken( Token.LB, "[" ),
      new JavaScriptToken( Token.NUMBER, String.valueOf( index ) ),
      new JavaScriptToken( Token.RB, "]" )
    };
    return replacement;
  }

  private boolean isReplacableString( TokenList tokens, int pos ) {
    boolean result = false;
    JavaScriptToken token = tokens.getToken( pos );
    if( isString( token ) ) {
      JavaScriptToken nextToken = tokens.getToken( pos + 1 );
      if( !isColonInObjectLiteral( nextToken ) ) {
        result = true;
      }
    }
    return result;
  }

  static boolean isString( JavaScriptToken token ) {
    return token != null && token.getType() == Token.STRING;
  }

  static boolean isColonInObjectLiteral( JavaScriptToken token ) {
    return token != null && token.getType() == Token.OBJECTLIT;
  }
}
