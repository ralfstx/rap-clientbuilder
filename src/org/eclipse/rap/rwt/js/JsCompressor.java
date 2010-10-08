package org.eclipse.rap.rwt.js;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

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
  private final static ErrorReporter REPORTER = new SystemErrorReporter();

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
    File inputDir = new File( projectDir, JS_SOURCE_DIR );
    if( !inputDir.exists() ) {
      String message = "Javascript source directory not found: " + inputDir;
      throw new IllegalArgumentException( message );
    }
    File[] inputFiles = findAllJsFiles( inputDir );
    File outputFile = new File( projectDir, TARGET_JS_FILE );
    try {
      long start = System.currentTimeMillis();
      compressFiles( inputFiles, outputFile );
      long time = System.currentTimeMillis() - start;
      int count = inputFiles.length;
      System.out.println( "Compressed " + count + " files in " + time + " ms" );
    } catch( IOException e ) {
      throw new RuntimeException( "Failed to compress Javascript files", e );
    }
  }

  private static void compressFiles( File[] inputFiles, File outputFile )
    throws IOException
  {
    FileOutputStream outputStream = new FileOutputStream( outputFile );
    Writer outputWriter = new OutputStreamWriter( outputStream, CHARSET );
    try {
      for( int i = 0; i < inputFiles.length; i++ ) {
        File inputFile = inputFiles[ i ];
        compressFile( inputFile, outputWriter );
      }
    } finally {
      outputWriter.close();
    }
  }

  private static void compressFile( File inputFile, Writer outputWriter )
    throws IOException
  {
    System.out.println( "Compressing file " + inputFile );
    InputStream inputStream = new FileInputStream( inputFile );
    Reader inputReader = new InputStreamReader( inputStream, CHARSET );
    try {
      JavaScriptCompressor compressor = new JavaScriptCompressor( inputReader,
                                                                  REPORTER );
      compressor.compress( outputWriter,
                           -1,
                           true,
                           VERBOSE,
                           PRESERVE_ALL_SEMICOLONS,
                           DISABLE_OPTIMIZATIONS );
      outputWriter.write( "\n" );
    } finally {
      inputReader.close();
    }
  }

  private static File[] findAllJsFiles( File inputDir ) {
    List collectedFiles = new ArrayList();
    File[] files = inputDir.listFiles();
    for( int i = 0; i < files.length; i++ ) {
      File file = files[ i ];
      if( file.isDirectory() ) {
        File[] foundFiles = findAllJsFiles( file );
        for( int j = 0; j < foundFiles.length; j++ ) {
          collectedFiles.add( foundFiles[ j ] );
        }
      } else if( file.getName().endsWith( ".js" ) ) {
        collectedFiles.add( file );
      }
    }
    return ( File[] )collectedFiles.toArray( new File[ collectedFiles.size() ] );
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
