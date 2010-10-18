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
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

public class CompressorTest extends TestCase {

  public void testCompressEmpty() throws IOException {
    assertEquals( "", compress( "" ) );
  }

  public void testCompressNumbers() throws IOException {
    assertEquals( "23;", compress( "23" ) );
    assertEquals( "23;", compress( " 23.0 " ) );
  }

  public void testCompressStrings() throws IOException {
    assertEquals( "\"\";", compress( "''" ) );
    assertEquals( "\"a\";", compress( "'a'" ) );
  }

  public void testCompressExpressions() throws IOException {
    assertEquals( "23+\"\";", compress( " 23 + ''" ) );
  }

  public void testCompressEscapes() throws IOException {
    assertEquals( "\"\";", compress( "\"\"" ) );
    assertEquals( "\"\\\\\";", compress( "\"\\\\\"" ) );
    // Unicode characters are not escaped as the output file is in UTF-8
    assertEquals( "\"\u0416\";", compress( "\"\u0416\"" ) );
    // Unicode escapes are transformed into Unicode characters
    assertEquals( "\"\u0416\";", compress( "\"\\u0416\"" ) );
    assertEquals( "\"\u00CF\";", compress( "\"\\xCF\"" ) );
  }

  private static String compress( String input )
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
