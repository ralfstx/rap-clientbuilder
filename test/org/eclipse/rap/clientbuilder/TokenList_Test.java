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
import java.util.List;

import junit.framework.TestCase;

import org.mozilla.javascript.Token;

import com.yahoo.platform.yui.compressor.JavaScriptToken;
import com.yahoo.platform.yui.compressor.TestAdapter;

public class TokenList_Test extends TestCase {

  public void testCreate() {
    List tokens = new ArrayList();
    tokens.add( TestAdapter.createJavaScriptToken( Token.STRING, "Test" ) );
    tokens.add( TestAdapter.createJavaScriptToken( Token.LB, "[" ) );
    TokenList tokenList = new TokenList( tokens );
    assertEquals( 2, tokenList.size() );
  }

  public void testGetToken() {
    List tokens = new ArrayList();
    tokens.add( TestAdapter.createJavaScriptToken( Token.STRING, "Test" ) );
    tokens.add( TestAdapter.createJavaScriptToken( Token.LB, "[" ) );
    TokenList tokenList = new TokenList( tokens );
    assertNull( tokenList.getToken( -1 ) );
    assertEquals( tokens.get( 0 ), tokenList.getToken( 0 ) );
    assertEquals( tokens.get( 1 ), tokenList.getToken( 1 ) );
    assertNull( tokenList.getToken( 2 ) );
  }

  public void testRemoveToken() {
    List tokens = new ArrayList();
    tokens.add( TestAdapter.createJavaScriptToken( Token.STRING, "First" ) );
    tokens.add( TestAdapter.createJavaScriptToken( Token.STRING, "Second" ) );
    TokenList tokenList = new TokenList( tokens );
    tokenList.removeToken( 0 );
    assertEquals( 1, tokenList.size() );
    assertEquals( "Second", tokenList.getToken( 0 ).getValue() );
  }

  public void testRemoveManipulatesOriginalList() {
    List tokens = new ArrayList();
    tokens.add( TestAdapter.createJavaScriptToken( Token.STRING, "First" ) );
    tokens.add( TestAdapter.createJavaScriptToken( Token.STRING, "Second" ) );
    TokenList tokenList = new TokenList( tokens );
    tokenList.removeToken( 0 );
    assertEquals( 1, tokens.size() );
    assertEquals( "Second", ( ( JavaScriptToken )tokens.get( 0 ) ).getValue() );
  }

  public void testFindClosingFailsIfNotOnOpeningBrace() throws Exception {
    String input = "a, b, c";
    TokenList tokens = TestUtil.parse( input );
    try {
      tokens.findClosing( 0 );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testFindClosingBrace() throws Exception {
    String input = "a = { foo : 23, bar : { x : 7, y : [ 1, 2, 3 ] } }";
    TokenList tokens = TestUtil.parse( input );
    int closingBrace = tokens.findClosing( 2 );
    assertEquals( 24, closingBrace );
  }

  public void testFindClosingBracket() throws Exception {
    String input = "a = [ \"foo\", 23, { x : 7, y : [ 1, 2, 3 ] } ]";
    TokenList tokens = TestUtil.parse( input );
    int closingBrace = tokens.findClosing( 2 );
    assertEquals( 22, closingBrace );
  }

  public void testFindInObjectLiteral() throws Exception {
    String input = "a = { \"foo\" : {}, \"bar\" : {} }";
    TokenList tokens = TestUtil.parse( input );
    TestUtil.printTokens( tokens );
    int startSelectedExpr = tokens.findInObjectLiteral( "foo", 2 );
    assertEquals( 5, startSelectedExpr );
  }

  public void testFindInObjectLiteralUnquoted() throws Exception {
    String input = "a = { foo : {}, bar : {} }";
    TokenList tokens = TestUtil.parse( input );
    TestUtil.printTokens( tokens );
    int startSelectedExpr = tokens.findInObjectLiteral( "bar", 2 );
    assertEquals( 10, startSelectedExpr );
  }

  public void testFindInObjectLiteralDefault() throws Exception {
    String input = "a = { \"foo\" : {}, \"default\" : {} }";
    TokenList tokens = TestUtil.parse( input );
    TestUtil.printTokens( tokens );
    int startSelectedExpr = tokens.findInObjectLiteral( "bar", 2 );
    assertEquals( 10, startSelectedExpr );
  }
}
