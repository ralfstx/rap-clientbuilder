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

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.internal.widgets.displaykit.JsFilesList;

public class JsCompressor {

  private static final String JS_SOURCE_DIR = "js";
  private static final String TARGET_JS_FILE = "resources/client.js";
  private static final boolean CREATE_DEBUG_FILES
    = "true".equals( System.getProperty( "jscompressor.debug" ) );
  static DebugFileWriter debugFileWriter = new DebugFileWriter();

  public static void main( String[] args ) {
    if( args.length < 1 ) {
      String message = "Parameter missing (rwt.q07 project directory)";
      throw new IllegalArgumentException( message );
    }
    File projectDir = new File( args[ 0 ] );
    if( !projectDir.exists() ) {
      String message = "Project directory not found: " + projectDir;
      throw new IllegalArgumentException( message );
    }
    if( CREATE_DEBUG_FILES ) {
      File debugDir = new File( projectDir, "tmp" );
      debugDir.mkdir();
      System.out.println( "Creating debug files in " + debugDir );
      debugFileWriter.createDebugFilesIn( debugDir );
    }
    File inputDir = new File( projectDir, JS_SOURCE_DIR );
    if( !inputDir.exists() ) {
      String message = "Javascript source directory not found: " + inputDir;
      throw new IllegalArgumentException( message );
    }
    JSFile[] inputFiles = getJsFilesList( inputDir );
    File outputFile = new File( projectDir, TARGET_JS_FILE );
    try {
      long start = System.currentTimeMillis();
      String compressed = compressFiles( inputFiles );
      long time = System.currentTimeMillis() - start;
      JSFile.writeToFile( outputFile, compressed );
      int count = inputFiles.length;
      System.out.println( "Compressed " + count + " files in " + time + " ms" );
      System.out.println( "Result size: " + compressed.length() + " bytes" );
    } catch( IOException e ) {
      throw new RuntimeException( "Failed to compress Javascript files", e );
    }
  }

  private static JSFile[] getJsFilesList( File inputDir ) {
    JSFile[] inputFiles;
    try {
      String[] fileNames = JsFilesList.getFiles();
      inputFiles = new JSFile[ fileNames.length ];
      for( int i = 0; i < inputFiles.length; i++ ) {
        File file = new File( inputDir, fileNames[ i ] );
        inputFiles[ i ] = new JSFile( file );
      }
    } catch( Exception e ) {
      String message = "Failed to get JS files list from rwt.q07 project";
      throw new RuntimeException( message, e );
    }
    return inputFiles;
  }

  private static String compressFiles( JSFile[] inputFiles ) throws IOException
  {
    StringReplacer stringReplacer = new StringReplacer();
    for( int i = 0; i < inputFiles.length; i++ ) {
      JSFile inputFile = inputFiles[ i ];
      stringReplacer.discoverStrings( inputFile.getTokens() );
    }
    stringReplacer.optimize();
    StringBuffer buffer = new StringBuffer();
    buffer.append( "(function($){" );
    for( int i = 0; i < inputFiles.length; i++ ) {
      JSFile inputFile = inputFiles[ i ];
      stringReplacer.replaceStrings( inputFile.getTokens() );
      String result = inputFile.compress();
      buffer.append( result );
      buffer.append( "\n" );
      System.out.println( inputFile.getFile().getAbsolutePath()
                          + "\t"
                          + result.length() );
    }
    buffer.append( "})(");
    String[] strings = stringReplacer.getStrings();
    String stringArrayCode = createStringArray( strings );
    System.out.println( "array size: " + strings.length );
    System.out.println( "array code size: " + stringArrayCode.length() );
    buffer.append( stringArrayCode );
    buffer.append( ");" );
    return buffer.toString();
  }

  private static String createStringArray( String[] strings ) {
    StringBuffer buffer = new StringBuffer();
    buffer.append( "[" );
    for( int i = 0; i < strings.length; i++ ) {
      String string = strings[ i ];
      buffer.append( "\"" + JavaScriptPrinter.escapeString( string ) + "\"," );
    }
    if( buffer.charAt( buffer.length() - 1 ) == ',' ) {
      buffer.setLength( buffer.length() - 1 );
    }
    buffer.append( "]" );
    return buffer.toString();
  }
}
