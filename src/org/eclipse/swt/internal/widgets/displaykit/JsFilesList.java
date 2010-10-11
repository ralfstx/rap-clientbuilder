package org.eclipse.swt.internal.widgets.displaykit;

import java.lang.reflect.Field;

public final class JsFilesList {

  public static String[] getFiles()
    throws SecurityException, NoSuchFieldException, IllegalAccessException
  {
    Class clazz = QooxdooResourcesUtil.class;
    Field field = clazz.getDeclaredField( "JAVASCRIPT_FILES" );
    field.setAccessible( true );
    String[] files = ( String[] )field.get( null );
    for( int i = 0; i < files.length; i++ ) {
      if( "debug-settings.js".equals( files[ i ] ) ) {
        files[ i ] = "settings.js";
      }
    }
    return files;
  }
}
