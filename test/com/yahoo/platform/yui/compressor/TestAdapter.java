package com.yahoo.platform.yui.compressor;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import org.mozilla.javascript.EvaluatorException;

/**
 * Adapter to access package private fields and methods of the YUI Compressor.
 */
public final class TestAdapter {

  public static JavaScriptToken createJavaScriptToken( int type, String value )
  {
    return new JavaScriptToken( type, value );
  }

  public static ArrayList parseString( String input )
    throws EvaluatorException, IOException
  {
    Reader inputReader = new StringReader( input );
    TestErrorReporter reporter = new TestErrorReporter();
    ArrayList tokens = JavaScriptCompressor.parse( inputReader, reporter );
    return tokens;
  }

  public static String getLiteralString( int type ) {
    return ( String )JavaScriptCompressor.literals.get( new Integer( type ) );
  }
}
