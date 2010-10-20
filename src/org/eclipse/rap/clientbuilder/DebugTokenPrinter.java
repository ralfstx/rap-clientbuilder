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

import java.util.List;

import org.mozilla.javascript.Token;

import com.yahoo.platform.yui.compressor.JavaScriptToken;

public class DebugTokenPrinter {

  private static final String INDENT = "  ";
  private static final String NEWLINE = "\n";
  private final StringBuffer code = new StringBuffer();
  private String indent = "";
  private String nextPrefix = "";

  public static String printTokens( List tokens ) {
    return printTokens( tokens, 0, tokens.size() - 1 );
  }

  public static String printTokens( List tokens, int first, int last ) {
    DebugTokenPrinter printer = new DebugTokenPrinter();
    for( int i = first; i <= last; i++ ) {
      printer.printToken( ( JavaScriptToken )tokens.get( i ) );
    }
    return printer.toString();
  }

  public void printToken( JavaScriptToken token ) {
    int type = token.getType();
    if( type == Token.RC ) {
      code.append( NEWLINE );
      if( indent.length() >= INDENT.length() ) {
        indent = indent.substring( INDENT.length() );
        code.append( indent );
      }
      code.append( token.getValue() );
      nextPrefix = NEWLINE + indent;
    } else if( type == Token.LC ) {
      code.append( nextPrefix );
      code.append( token.getValue() );
      indent += INDENT;
      nextPrefix = NEWLINE + indent;
    } else if( type == Token.COMMA ) {
      code.append( token.getValue() );
    } else if( type == Token.SEMI ) {
      code.append( token.getValue() );
      nextPrefix = NEWLINE + indent;
    } else if( type == Token.DOT ) {
      code.append( token.getValue() );
      nextPrefix = "";
    } else if( type == Token.STRING ) {
      code.append( nextPrefix + "\"" + escapeString( token.getValue() ) + "\"" );
      nextPrefix = " ";
    } else {
      code.append( nextPrefix + token.getValue() );
      nextPrefix = " ";
    }
  }

  private String escapeString( String value ) {
    StringBuffer result = new StringBuffer();
    int length = value.length();
    for( int i = 0; i < length; i++ ) {
      char ch = value.charAt( i );
      if( ch == '"' ) {
        result.append( "\\\"" );
      } else if( ch == '\n' ) {
        result.append( "\\n" );
      } else if( ch == '\r' ) {
        result.append( "\\r" );
      } else if( ch == '\t' ) {
        result.append( "\\t" );
      } else if( ch == '\\' ) {
        result.append( "\\\\" );
      } else {
        result.append( ch );
      }
    }
    return result.toString();
  }

  public String toString() {
    return code.toString();
  }
}
