package com.yahoo.platform.yui.compressor;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.Token;


public class TestUtil {

  static JavaScriptToken[] parse( String input ) throws IOException {
    Reader inputReader = new StringReader( input );
    TestErrorReporter reporter = new TestErrorReporter();
    ArrayList tokens = JavaScriptCompressor.parse( inputReader, reporter );
    JavaScriptToken[] result = new JavaScriptToken[ tokens.size() ];
    tokens.toArray( result );
    return result;
  }

  public static void printTokens( List tokens ) {
    JavaScriptToken[] array = new JavaScriptToken[ tokens.size() ];
    tokens.toArray( array );
    printTokens( array );
  }

  public static void printTokens( JavaScriptToken[] tokens ) {
    for( int i = 0; i < tokens.length; i++ ) {
      JavaScriptToken token = tokens[ i ];
      int type = token.getType();
      switch( type ) {
        case Token.NAME:
          System.out.println( i + ". name: " + token.getValue() );
        break;
        case Token.REGEXP:
          System.out.println( i + ". regexp: " + token.getValue() );
        break;
        case Token.STRING:
          System.out.println( i + ". string: " + token.getValue() );
        break;
        case Token.NUMBER:
          System.out.println( i + ". number: " + token.getValue() );
        break;
        default:
          String litStr = ( String )JavaScriptCompressor.literals.get( new Integer( type ) );
          System.out.println( i + ". literal: " + litStr );
        break;
      }
    }
  }

  static String compress( String input )
    throws IOException
  {
    Reader inputReader = new StringReader( input );
    TestErrorReporter errorReporter = new TestErrorReporter();
    JavaScriptCompressor compressor = new JavaScriptCompressor( inputReader,
                                                                errorReporter );
    StringWriter outputWriter = new StringWriter();
    compressor.compress( outputWriter, -1, true, false, false, false );
    return outputWriter.toString();
  }
}
