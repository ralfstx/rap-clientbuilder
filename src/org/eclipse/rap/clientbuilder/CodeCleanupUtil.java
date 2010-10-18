package org.eclipse.rap.clientbuilder;

import java.util.List;

import org.mozilla.javascript.Token;

import com.yahoo.platform.yui.compressor.JavaScriptToken;


public class CodeCleanupUtil {

  public static void removeDebugCode( List tokens ) {
    int length = tokens.size();
    int pos = 0;
    int start = 0;
    int level = 0;
    while( pos < length ) {
      if( level == 0 ) {
        if( pos < length - 15 && isBeginDebugBlock( tokens, pos ) ) {
          start = pos;
          level = 1;
          pos += 15;
        }
      } else if( tokenMatches( tokens, pos, Token.LC, null ) ) {
        level++;
      } else if( tokenMatches( tokens, pos, Token.RC, null ) ) {
        level--;
        if( level == 0 ) {
          pos++;
          if( pos < 0 || pos >= length || !tokenMatches( tokens, pos, Token.ELSE, null ) ) {
            pos--;
          }
          int removed = removeTokens( tokens, start, pos );
          length -= removed;
          pos -= removed;
        }
      }
      pos++;
    }
  }

  private static int removeTokens( List tokens, int first, int last ) {
    String code = printTokensToRemove( tokens, first, last );
    System.out.println( code );
    for( int i = first; i <= last; i++ ) {
      tokens.remove( first );
    }
    return last - first + 1;
  }

  private static String printTokensToRemove( List tokens, int first, int last )
  {
    int start = Math.max( 0, first - 3 );
    int end = Math.min( tokens.size() - 1, last + 4 );
    StringBuffer code = new StringBuffer();
    code.append( "----------\n" );
    for( int i = start; i < first; i++ ) {
      JavaScriptToken token = ( JavaScriptToken )tokens.get( i );
      code.append( token.getValue() + " " );
    }
    code.append( "\n>>>\n" );
    String indent = "";
    String nextPrefix = "";
    for( int i = first; i <= last; i++ ) {
      JavaScriptToken token = ( JavaScriptToken )tokens.get( i );
      int type = token.getType();
      if( type == Token.RC ) {
        code.append( "\n" );
        if( indent.length() >= 2 ) {
          indent = indent.substring( 2 );
          code.append( indent );
        }
        code.append( token.getValue() );
        nextPrefix = "\n" + indent;
      } else if( type == Token.LC ) {
        code.append( nextPrefix );
        code.append( token.getValue() );
        indent += "  ";
        nextPrefix = "\n" + indent;
      } else if( type == Token.SEMI ) {
        code.append( token.getValue() );
        nextPrefix = "\n" + indent;
      } else if( type == Token.DOT ) {
        code.append( token.getValue() );
        nextPrefix = "";
      } else {
        code.append( nextPrefix + token.getValue() );
        nextPrefix = " ";
      }
    }
    code.append( "\n<<<\n" );
    for( int i = last + 1; i < end; i++ ) {
      JavaScriptToken token = ( JavaScriptToken )tokens.get( i );
      code.append( token.getValue() + " " );
    }
    code.append( "\n----------\n" );
    return code.toString();
  }

  public static boolean isBeginDebugBlock( List tokens, int offset ) {
    return tokenMatches( tokens, offset, Token.IF, null )
           && tokenMatches( tokens, offset + 1, Token.LP, null )
           && tokenMatches( tokens, offset + 2, Token.NAME, "qx" )
           && tokenMatches( tokens, offset + 3, Token.DOT, null )
           && tokenMatches( tokens, offset + 4, Token.NAME, "core" )
           && tokenMatches( tokens, offset + 5, Token.DOT, null )
           && tokenMatches( tokens, offset + 6, Token.NAME, "Variant" )
           && tokenMatches( tokens, offset + 7, Token.DOT, null )
           && tokenMatches( tokens, offset + 8, Token.NAME, "isSet" )
           && tokenMatches( tokens, offset + 9, Token.LP, null )
           && tokenMatches( tokens, offset + 10, Token.STRING, "qx.debug" )
           && tokenMatches( tokens, offset + 11, Token.COMMA, null )
           && tokenMatches( tokens, offset + 12, Token.STRING, "on" )
           && tokenMatches( tokens, offset + 13, Token.RP, null )
           && tokenMatches( tokens, offset + 14, Token.RP, null )
           && tokenMatches( tokens, offset + 15, Token.LC, null );
  }

  private static boolean tokenMatches( List tokens,
                                       int offset,
                                       int type,
                                       String value )
  {
    JavaScriptToken token = ( JavaScriptToken )tokens.get( offset );
    return token.getType() == type
           && ( value == null || value.equals( token.getValue() ) );
  }
}
