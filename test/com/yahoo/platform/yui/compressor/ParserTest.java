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

import junit.framework.TestCase;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.Token;

public class ParserTest extends TestCase {

  static final ErrorReporter REPORTER = new TestErrorReporter();

  public void testParseNumber() throws Exception {
    JavaScriptToken[] result = TestUtil.parse( "23.0" );
    assertEquals( 2, result.length );
    assertEquals( Token.NUMBER, result[ 0 ].getType() );
    assertEquals( Token.SEMI, result[ 1 ].getType() );
  }

  public void testParseVar() throws Exception {
    JavaScriptToken[] tokens = TestUtil.parse( "var x = 12;" );
    assertEquals( 5, tokens.length );
    assertEquals( Token.VAR, tokens[ 0 ].getType() );
    assertEquals( Token.NAME, tokens[ 1 ].getType() );
    assertEquals( Token.ASSIGN, tokens[ 2 ].getType() );
    assertEquals( Token.NUMBER, tokens[ 3 ].getType() );
    assertEquals( Token.SEMI, tokens[ 4 ].getType() );
  }

  public void testParseAssignment() throws Exception {
    JavaScriptToken[] tokens = TestUtil.parse( "a = 1;" );
    assertEquals( 4, tokens.length );
    assertEquals( Token.NAME, tokens[ 0 ].getType() );
    assertEquals( Token.ASSIGN, tokens[ 1 ].getType() );
    assertEquals( Token.NUMBER, tokens[ 2 ].getType() );
    assertEquals( Token.SEMI, tokens[ 3 ].getType() );
  }
}
