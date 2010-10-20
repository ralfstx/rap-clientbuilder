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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.eclipse.swt.internal.widgets.displaykit.JsFilesList;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

public class JsCompressor {

  private static final String JS_SOURCE_DIR = "js";
  private static final String TARGET_JS_FILE = "resources/client.js";
  private static final String CHARSET = "UTF-8";
  private static final boolean PRESERVE_ALL_SEMICOLONS = false;
  private static final boolean DISABLE_OPTIMIZATIONS = false;
  private static final boolean VERBOSE = false;
  private static final boolean CREATE_DEBUG_FILES
    = "true".equals( System.getProperty( "jscompressor.debug" ) );
  private final static ErrorReporter REPORTER = new SystemErrorReporter();
  
  private static CodeCleanupRunner runner = new CodeCleanupRunner();

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
      runner.createDebugFilesIn( debugDir );
    }
    File inputDir = new File( projectDir, JS_SOURCE_DIR );
    if( !inputDir.exists() ) {
      String message = "Javascript source directory not found: " + inputDir;
      throw new IllegalArgumentException( message );
    }
    File[] inputFiles = getJsFilesList( inputDir );
    File outputFile = new File( projectDir, TARGET_JS_FILE );
    try {
      long start = System.currentTimeMillis();
      String compressed = compressFiles( inputFiles );
      long time = System.currentTimeMillis() - start;
      writeToFile( outputFile, compressed );
      int count = inputFiles.length;
      System.out.println( "Compressed " + count + " files in " + time + " ms" );
      System.out.println( "Result size: " + compressed.length() + " bytes" );
    } catch( IOException e ) {
      throw new RuntimeException( "Failed to compress Javascript files", e );
    }
  }

  private static File[] getJsFilesList( File inputDir ) {
    File[] inputFiles;
    try {
      String[] fileNames = JsFilesList.getFiles();
      inputFiles = new File[ fileNames.length ];
      for( int i = 0; i < inputFiles.length; i++ ) {
        inputFiles[ i ] = new File( inputDir, fileNames[ i ] );
      }
    } catch( Exception e ) {
      String message = "Failed to get JS files list from rwt.q07 project";
      throw new RuntimeException( message, e );
    }
    return inputFiles;
  }

  private static String compressFiles( File[] inputFiles ) throws IOException {
    StringBuffer buffer = new StringBuffer();
    for( int i = 0; i < inputFiles.length; i++ ) {
      File inputFile = inputFiles[ i ];
      String result = compressFile( inputFile );
      buffer.append( result );
      buffer.append( "\n" );
      System.out.println( inputFile.getAbsolutePath() + "\t" + result.length() );
    }
    return buffer.toString();
  }

  private static String compressFile( final File inputFile ) throws IOException {
    String result;
    InputStream inputStream = new FileInputStream( inputFile );
    Reader inputReader = new InputStreamReader( inputStream, CHARSET );
    StringWriter stringWriter = new StringWriter();
    try {
      JavaScriptCompressor compressor = new JavaScriptCompressor( inputReader,
                                                                  REPORTER );
      compressor.setCleanupCallback( new ICleanupCallback() {

        public void cleanup( List tokens ) {
          runner.cleanupFile( tokens, inputFile.getName() );
        };
      } );
      compressor.compress( stringWriter ,
                           -1,
                           true,
                           VERBOSE,
                           PRESERVE_ALL_SEMICOLONS,
                           DISABLE_OPTIMIZATIONS );
      stringWriter.flush();
      result = stringWriter.getBuffer().toString();
      stringWriter.close();
    } finally {
      inputReader.close();
    }
    return result;
  }

  private static void writeToFile( File outputFile, String copmressed )
    throws IOException
  {
    FileOutputStream outputStream = new FileOutputStream( outputFile );
    Writer outputWriter = new OutputStreamWriter( outputStream, CHARSET );
    try {
      outputWriter.write( copmressed );
    } finally {
      outputWriter.close();
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
