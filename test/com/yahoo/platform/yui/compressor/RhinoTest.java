package com.yahoo.platform.yui.compressor;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rap.clientbuilder.CodeCleanupUtil;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Token;

public class RhinoTest extends TestCase {

  static final ErrorReporter REPORTER = new SystemErrorReporter();

  public void testParseNumber() throws Exception {
    JavaScriptToken[] result = parse( "23.0" );
    assertEquals( 2, result.length );
    assertEquals( Token.NUMBER, result[ 0 ].getType() );
    assertEquals( Token.SEMI, result[ 1 ].getType() );
  }

  public void testParseVar() throws Exception {
    JavaScriptToken[] tokens = parse( "var x = 12;" );
    assertEquals( 5, tokens.length );
    assertEquals( Token.VAR, tokens[ 0 ].getType() );
    assertEquals( Token.NAME, tokens[ 1 ].getType() );
    assertEquals( Token.ASSIGN, tokens[ 2 ].getType() );
    assertEquals( Token.NUMBER, tokens[ 3 ].getType() );
    assertEquals( Token.SEMI, tokens[ 4 ].getType() );
  }

  public void testParseAssignment() throws Exception {
    JavaScriptToken[] tokens = parse( "a = 1;" );
    assertEquals( 4, tokens.length );
    assertEquals( Token.NAME, tokens[ 0 ].getType() );
    assertEquals( Token.ASSIGN, tokens[ 1 ].getType() );
    assertEquals( Token.NUMBER, tokens[ 2 ].getType() );
    assertEquals( Token.SEMI, tokens[ 3 ].getType() );
  }

  public void testRemoveDebugCodeEmpty() throws Exception {
    String input = "if( qx.core.Variant.isSet( \"qx.debug\", \"on\" ) ) {\n" +
    "}\n";
    JavaScriptToken[] tokens = parse( input );
    List tokenList = createList( tokens );
    CodeCleanupUtil.removeDebugCode( tokenList );
    assertEquals( 0, tokenList.size() );
  }

  public void testRemoveDebugCode() throws Exception {
    String input = "a = 1;\n" +
    		"if( qx.core.Variant.isSet( \"qx.debug\", \"on\" ) ) {\n" +
    		"  if( false ) { throw \"ERROR\" }\n" +
    		"}\n" +
    		"b = 2;";
    JavaScriptToken[] tokens = parse( input );
    ArrayList tokenList = new ArrayList();
    for( int i = 0; i < tokens.length; i++ ) {
      tokenList.add( tokens[ i ] );
    }
    assertEquals( 34, tokenList.size() );
    CodeCleanupUtil.removeDebugCode( tokenList );
    assertEquals( 8, tokenList.size() );
  }

  public void testRemoveDebugCodeWithelse() throws Exception {
    String input = "if( qx.core.Variant.isSet( \"qx.debug\", \"on\" ) ) {\n" +
    "  a = 1;\n" +
    "}\n else {\n" +
    "  b = 2;\n" +
    "}";
    JavaScriptToken[] tokens = parse( input );
    List tokenList = createList( tokens );
    CodeCleanupUtil.removeDebugCode( tokenList );
    printTokens( ( JavaScriptToken[] )tokenList.toArray( new JavaScriptToken[ 0 ] ) );
    assertEquals( 6, tokenList.size() );
  }

  public void testRemoveDebugCode_Multiple() throws Exception {
    String input = "if( qx.core.Variant.isSet( \"qx.debug\", \"on\" ) ) {\n" +
    "}\n";
    JavaScriptToken[] tokens = parse( input + input );
    List tokenList = createList( tokens );
    CodeCleanupUtil.removeDebugCode( tokenList );
    assertEquals( 0, tokenList.size() );
  }
  
  private List createList( JavaScriptToken[] tokens ) {
    List tokenList = new ArrayList();
    for( int i = 0; i < tokens.length; i++ ) {
      tokenList.add( tokens[ i ] );
    }
    return tokenList;
  }

  private static JavaScriptToken[] parse( String input ) throws IOException {
    Reader inputReader = new StringReader( input );
    ArrayList tokens = JavaScriptCompressor.parse( inputReader, REPORTER );
    JavaScriptToken[] result = new JavaScriptToken[ tokens.size() ];
    tokens.toArray( result );
    return result;
  }

  private static void printTokens( JavaScriptToken[] tokens ) {
    for( int i = 0; i < tokens.length; i++ ) {
      JavaScriptToken token = tokens[ i ];
      int type = token.getType();
      switch( type ) {
        case Token.NAME:
          System.out.println( "name: " + token.getValue() );
        break;
        case Token.REGEXP:
          System.out.println( "regexp: " + token.getValue() );
        break;
        case Token.STRING:
          System.out.println( "string: " + token.getValue() );
        break;
        case Token.NUMBER:
          System.out.println( "number: " + token.getValue() );
        break;
        default:
          String litStr = ( String )JavaScriptCompressor.literals.get( new Integer( type ) );
          System.out.println( "literal: " + litStr );
        break;
      }
    }
  }

  private static final class SystemErrorReporter implements ErrorReporter {

    public void warning( String message,
                         String sourceName,
                         int line,
                         String lineSource,
                         int lineOffset )
    {
      System.out.println( getMessage( "WARNING", message ) );
    }

    public void error( String message,
                       String sourceName,
                       int line,
                       String lineSource,
                       int lineOffset )
    {
      System.out.println( getMessage( "ERROR", message ) );
    }

    public EvaluatorException runtimeError( String message,
                                            String sourceName,
                                            int line,
                                            String lineSource,
                                            int lineOffset )
    {
      error( message, sourceName, line, lineSource, lineOffset );
      return new EvaluatorException( message );
    }

    private String getMessage( String severity, String message ) {
      StringBuffer result = new StringBuffer();
      result.append( "\n[" );
      result.append( severity );
      result.append( "] " );
      result.append( message );
      return result.toString();
    }
  }
}
