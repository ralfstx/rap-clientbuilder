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
    return files;
  }
}
