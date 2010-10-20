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
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CodeCleanupRunner {

  private File parentDirectory;

  public void createDebugFilesIn( File parentDirectory ) {
    this.parentDirectory = parentDirectory;
  }

  public void cleanupFile( List tokens, String fileName ) {
    String origCode = null;
    if( parentDirectory != null ) {
      origCode = DebugTokenPrinter.printTokens( tokens );
    }
    CodeCleaner codeCleaner = new CodeCleaner( tokens );
    codeCleaner.removeVariantsCode();
    if( parentDirectory != null ) {
      String cleanedCode = DebugTokenPrinter.printTokens( tokens );
      logDifferences( origCode, cleanedCode, fileName );
    }
  }

  private void logDifferences( String origCode,
                               String cleanedCode,
                               String fileName )
  {
    if( !origCode.equals( cleanedCode ) ) {
      try {
        File origDir = new File( parentDirectory, "orig" );
        File cleanDir = new File( parentDirectory, "clean" );
        origDir.mkdirs();
        cleanDir.mkdirs();
        File origFile = new File( origDir, fileName );
        File cleanedFile = new File( cleanDir, fileName );
        writeToFile( origCode, origFile );
        writeToFile( cleanedCode, cleanedFile );
      } catch( IOException e ) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  private static void writeToFile( String Code, File file ) throws IOException {
    FileWriter fileWriter = new FileWriter( file );
    try {
      fileWriter.write( Code );
    } catch( IOException e ) {
      System.err.println( "Failed to write to file " + file.getAbsolutePath() );
      e.printStackTrace();
    } finally {
      fileWriter.close();
    }
  }
}
