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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.rap.clientbuilder.TokenList.TokenMatcher;

public class CodeCleaner {

  private final TokenList reader;

  private final Set tokensToRemove;

  public CodeCleaner( TokenList tokens ) {
    this.reader = tokens;
    tokensToRemove = new HashSet();
  }

  public void removeVariantsCode() {
    int pos = 0;
    while( pos < reader.size() ) {
      int nextPos = removeConditional( pos );
      if( nextPos == pos ) {
        nextPos = replaceSelection( pos );
      }
      if( nextPos == pos ) {
        nextPos++;
      }
      pos = nextPos;
    }
    removeMarkedTokens();
  }

  private int removeConditional( int offset ) {
    int nextPos = offset;
    VariantConditional conditional = readVariantConditional( offset );
    if( conditional != null ) {
      if( canRemoveVariant( conditional.variant ) ) {
        int endExpr = reader.readExpression( conditional.end + 1 );
        if( endExpr != -1 ) {
          markTokensForRemoval( conditional.begin, endExpr );
          nextPos = endExpr + 1;
          if( TokenMatcher.ELSE.matches( reader.getToken( nextPos ) ) ) {
            markTokensForRemoval( nextPos, nextPos );
            nextPos++;
            if( TokenMatcher.LEFT_BRACE.matches( reader.getToken( nextPos ) ) ) {
              int closingBrace = reader.findClosing( nextPos );
              if( closingBrace != -1 ) {
                markTokensForRemoval( nextPos, nextPos );
                markTokensForRemoval( closingBrace, closingBrace );
                nextPos++;
              }
            }
          }
        }
      }
    }
    return nextPos;
  }

  private int replaceSelection( int offset ) {
    int nextPos = offset;
    VariantSelection selection = readVariantSelection( offset );
    if( selection != null ) {
      int closingBrace = reader.findClosing( selection.end );
      if( closingBrace != -1 ) {
        if( TokenMatcher.RIGHT_PAREN.matches( reader.getToken( closingBrace + 1 ) ) ) {
          int closingParen = closingBrace + 1;
          nextPos = selection.end + 1;
          if( canRemoveVariant( selection.variant ) ) {
            int selectedExpression
            = reader.findInObjectLiteral( "off", selection.end );
            if( selectedExpression != -1 ) {
              int endExpression = reader.readExpression( selectedExpression );
              if( endExpression != -1 ) {
                markTokensForRemoval( offset, selectedExpression - 1 );
                markTokensForRemoval( endExpression + 1, closingParen );
                nextPos = closingParen + 1;
              }
            }
          }
        }
      }
    }
    return nextPos;
  }

  private int markTokensForRemoval( int first, int last ) {
    for( int i = first; i <= last; i++ ) {
      tokensToRemove.add( new Integer( i ) );
    }
    return last - first + 1;
  }

  private void removeMarkedTokens() {
    ArrayList removeList = new ArrayList( tokensToRemove );
    Collections.sort( removeList );
    Collections.reverse( removeList );
    for( Iterator iterator = removeList.iterator(); iterator.hasNext(); ) {
      Integer index = ( Integer )iterator.next();
      reader.removeToken( index.intValue() );
    }
  }

  private static boolean canRemoveVariant( String variantName ) {
    return "qx.debug".equals( variantName )
           || "qx.compatibility".equals( variantName )
           || "qx.aspects".equals( variantName );
  }

  VariantConditional readVariantConditional( int offset ) {
    VariantConditional result = null;
    int pos = offset;
    boolean matched = true;
    TokenMatcher nameMatcher = TokenMatcher.string();
    matched &= TokenMatcher.IF.matches( reader.getToken( pos++ ) );
    matched &= TokenMatcher.LEFT_PAREN.matches( reader.getToken( pos++ ) );
    matched &= TokenMatcher.name( "qx" ).matches( reader.getToken( pos++ ) );
    matched &= TokenMatcher.DOT.matches( reader.getToken( pos++ ) );
    matched &= TokenMatcher.name( "core" ).matches( reader.getToken( pos++ ) );
    matched &= TokenMatcher.DOT.matches( reader.getToken( pos++ ) );
    matched &= TokenMatcher.name( "Variant" ).matches( reader.getToken( pos++ ) );
    matched &= TokenMatcher.DOT.matches( reader.getToken( pos++ ) );
    matched &= TokenMatcher.name( "isSet" ).matches( reader.getToken( pos++ ) );
    matched &= TokenMatcher.LEFT_PAREN.matches( reader.getToken( pos++ ) );
    matched &= nameMatcher.matches( reader.getToken( pos++ ) );
    matched &= TokenMatcher.COMMA.matches( reader.getToken( pos++ ) );
    matched &= TokenMatcher.string( "on" ).matches( reader.getToken( pos++ ) );
    matched &= TokenMatcher.RIGHT_PAREN.matches( reader.getToken( pos++ ) );
    matched &= TokenMatcher.RIGHT_PAREN.matches( reader.getToken( pos++ ) );
    if( matched ) {
      result = new VariantConditional( offset, pos - 1, nameMatcher.matchedValue );
    }
    return result;
  }

  VariantSelection readVariantSelection( int offset ) {
    VariantSelection result = null;
    int pos = offset;
    boolean matched = true;
    TokenMatcher nameMatcher = TokenMatcher.string();
    matched &= TokenMatcher.name( "qx" ).matches( reader.getToken( pos++ ) );
    matched &= TokenMatcher.DOT.matches( reader.getToken( pos++ ) );
    matched &= TokenMatcher.name( "core" ).matches( reader.getToken( pos++ ) );
    matched &= TokenMatcher.DOT.matches( reader.getToken( pos++ ) );
    matched &= TokenMatcher.name( "Variant" ).matches( reader.getToken( pos++ ) );
    matched &= TokenMatcher.DOT.matches( reader.getToken( pos++ ) );
    matched &= TokenMatcher.name( "select" ).matches( reader.getToken( pos++ ) );
    matched &= TokenMatcher.LEFT_PAREN.matches( reader.getToken( pos++ ) );
    matched &= nameMatcher.matches( reader.getToken( pos++ ) );
    matched &= TokenMatcher.COMMA.matches( reader.getToken( pos++ ) );
    matched &= TokenMatcher.LEFT_BRACE.matches( reader.getToken( pos++ ) );
    if( matched ) {
      result = new VariantSelection( offset, pos - 1, nameMatcher.matchedValue );
    }
    return result;
  };

  public static class Range {
    public final int begin;
    public final int end;
    
    public Range( int begin, int end ) {
      this.begin = begin;
      this.end = end;
    }
  }

  static class VariantConditional extends Range {
    public final String variant;
    
    public VariantConditional( int begin, int end, String variant ) {
      super( begin, end );
      this.variant = variant;
    }
  }

  static class VariantSelection extends Range {
    public final String variant;
    
    public VariantSelection( int begin, int end, String variant ) {
      super( begin, end );
      this.variant = variant;
    }
  }
}
