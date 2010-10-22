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

final class CodeCleanupRunner {

  private File directoryForDebugFiles;

  void createDebugFilesIn( File parentDirectory ) {
    this.directoryForDebugFiles = parentDirectory;
  }

  void cleanupFile( List tokens, String fileName ) {
    TokenList tokenList = new TokenList( tokens );
    String originalCode = getCodeForDebugFile( tokenList );
    doCleanup( tokenList );
    String cleanedCode = getCodeForDebugFile( tokenList );
    createDebugFiles( originalCode, cleanedCode, fileName );
  }

  private String getCodeForDebugFile( TokenList tokens ) {
    String code = null;
    if( directoryForDebugFiles != null ) {
      code = JavaScriptPrinter.printTokens( tokens );
    }
    return code;
  }

  private void doCleanup( TokenList tokens ) {
    CodeCleaner codeCleaner = new CodeCleaner( tokens );
    codeCleaner.removeVariantsCode();
  }

  private void createDebugFiles( String origCode,
                                 String cleanedCode,
                                 String fileName )
  {
    if( directoryForDebugFiles != null
        && origCode != null
        && !origCode.equals( cleanedCode ) )
    {
      createDebugFile( "orig", fileName, origCode );
      createDebugFile( "clean", fileName, cleanedCode );
    }
  }

  private void createDebugFile( String dirName, String fileName, String code ) {
    File subDir = new File( directoryForDebugFiles, dirName );
    subDir.mkdirs();
    File file = new File( subDir, fileName );
    try {
      writeToFile( code, file );
    } catch( IOException e ) {
      System.err.println( "Failed to write to file " + file.getAbsolutePath() );
      e.printStackTrace();
    }
  }

  private static void writeToFile( String Code, File file ) throws IOException {
    FileWriter fileWriter = new FileWriter( file );
    try {
      fileWriter.write( Code );
    } finally {
      fileWriter.close();
    }
  }
}
