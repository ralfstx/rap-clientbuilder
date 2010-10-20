/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package com.yahoo.platform.yui.compressor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rap.clientbuilder.TokenReader;

public class TokenReaderTest extends TestCase {

  public void testFindClosingFailsIfNotOnOpeningBrace() throws Exception {
    String input = "a, b, c";
    List tokens = parse( input );
    TokenReader reader = new TokenReader( tokens );
    try {
      reader.findClosing( 0 );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testFindClosingBrace() throws Exception {
    String input = "a = { foo : 23, bar : { x : 7, y : [ 1, 2, 3 ] } }";
    List tokens = parse( input );
    TokenReader reader = new TokenReader( tokens );
    int closingBrace = reader.findClosing( 2 );
    assertEquals( 24, closingBrace );
  }

  public void testFindClosingBracket() throws Exception {
    String input = "a = [ \"foo\", 23, { x : 7, y : [ 1, 2, 3 ] } ]";
    List tokens = parse( input );
    TokenReader reader = new TokenReader( tokens );
    int closingBrace = reader.findClosing( 2 );
    assertEquals( 22, closingBrace );
  }

  public void testFindInObjectLiteral() throws Exception {
    String input = "a = { \"foo\" : {}, \"bar\" : {} }";
    List tokens = parse( input );
    TestUtil.printTokens( tokens );
    TokenReader reader = new TokenReader( tokens );
    int startSelectedExpr = reader.findInObjectLiteral( "foo", 2 );
    assertEquals( 5, startSelectedExpr );
  }

  public void testFindInObjectLiteralUnquoted() throws Exception {
    String input = "a = { foo : {}, bar : {} }";
    List tokens = parse( input );
    TestUtil.printTokens( tokens );
    TokenReader reader = new TokenReader( tokens );
    int startSelectedExpr = reader.findInObjectLiteral( "bar", 2 );
    assertEquals( 10, startSelectedExpr );
  }

  public void testFindInObjectLiteralDefault() throws Exception {
    String input = "a = { \"foo\" : {}, \"default\" : {} }";
    List tokens = parse( input );
    TestUtil.printTokens( tokens );
    TokenReader reader = new TokenReader( tokens );
    int startSelectedExpr = reader.findInObjectLiteral( "bar", 2 );
    assertEquals( 10, startSelectedExpr );
  }

  private static List parse( String input ) throws IOException {
    JavaScriptToken[] tokens = TestUtil.parse( input );
    return createList( tokens );
  }
  
  private static List createList( JavaScriptToken[] tokens ) {
    List tokenList = new ArrayList();
    for( int i = 0; i < tokens.length; i++ ) {
      tokenList.add( tokens[ i ] );
    }
    return tokenList;
  }
}
