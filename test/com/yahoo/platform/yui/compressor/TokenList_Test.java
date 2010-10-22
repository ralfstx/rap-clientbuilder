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

import org.eclipse.rap.clientbuilder.TokenList;
import org.mozilla.javascript.Token;

public class TokenList_Test extends TestCase {

  public void testCreate() {
    List tokens = new ArrayList();
    tokens.add( new JavaScriptToken( Token.STRING, "Test" ) );
    tokens.add( new JavaScriptToken( Token.LB, "[" ) );
    TokenList tokenList = new TokenList( tokens );
    assertEquals( 2, tokenList.size() );
    assertEquals( tokens.get( 0 ), tokenList.getToken( 0 ) );
  }

  public void testRemove() {
    List tokens = new ArrayList();
    tokens.add( new JavaScriptToken( Token.STRING, "First" ) );
    tokens.add( new JavaScriptToken( Token.STRING, "Second" ) );
    TokenList tokenList = new TokenList( tokens );
    tokens.remove( 0 );
    assertEquals( 1, tokenList.size() );
    assertEquals( 1, tokens.size() );
    assertEquals( "Second", tokenList.getToken( 0 ).getValue() );
  }

  public void testFindClosingFailsIfNotOnOpeningBrace() throws Exception {
    String input = "a, b, c";
    List tokens = parse( input );
    TokenList reader = new TokenList( tokens );
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
    TokenList reader = new TokenList( tokens );
    int closingBrace = reader.findClosing( 2 );
    assertEquals( 24, closingBrace );
  }

  public void testFindClosingBracket() throws Exception {
    String input = "a = [ \"foo\", 23, { x : 7, y : [ 1, 2, 3 ] } ]";
    List tokens = parse( input );
    TokenList reader = new TokenList( tokens );
    int closingBrace = reader.findClosing( 2 );
    assertEquals( 22, closingBrace );
  }

  public void testFindInObjectLiteral() throws Exception {
    String input = "a = { \"foo\" : {}, \"bar\" : {} }";
    List tokens = parse( input );
    TestUtil.printTokens( tokens );
    TokenList reader = new TokenList( tokens );
    int startSelectedExpr = reader.findInObjectLiteral( "foo", 2 );
    assertEquals( 5, startSelectedExpr );
  }

  public void testFindInObjectLiteralUnquoted() throws Exception {
    String input = "a = { foo : {}, bar : {} }";
    List tokens = parse( input );
    TestUtil.printTokens( tokens );
    TokenList reader = new TokenList( tokens );
    int startSelectedExpr = reader.findInObjectLiteral( "bar", 2 );
    assertEquals( 10, startSelectedExpr );
  }

  public void testFindInObjectLiteralDefault() throws Exception {
    String input = "a = { \"foo\" : {}, \"default\" : {} }";
    List tokens = parse( input );
    TestUtil.printTokens( tokens );
    TokenList reader = new TokenList( tokens );
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
