package org.apache.lucene.spatial.search.grid;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.index.Term;
import org.apache.lucene.search.AutomatonQuery;
import org.apache.lucene.spatial.core.grid.SpatialGrid;
import org.apache.lucene.util.ToStringUtils;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.BasicAutomata;
import org.apache.lucene.util.automaton.BasicOperations;

import java.util.ArrayList;
import java.util.List;

/** 
 * 
 * @see AutomatonQuery, WildcardQuery
 */
public class SpatialGridQuery extends AutomatonQuery 
{  
  /**
   * Constructs a query for terms matching <code>term</code>. 
   */
  public SpatialGridQuery(Term term) {
    super(term, toAutomaton(term));
  }
  
  /**
   * Convert Grid syntax into an automaton.
   */
  public static Automaton toAutomaton(Term wildcardquery) {
    List<Automaton> automata = new ArrayList<Automaton>();
    
    String wildcardText = wildcardquery.text();
    
    for (int i = 0; i < wildcardText.length();) {
      final int c = wildcardText.codePointAt(i);
      int length = Character.charCount(c);
      switch(c) {
        case SpatialGrid.COVER: 
          automata.add(BasicAutomata.makeAnyString());
          break;

        case SpatialGrid.INTERSECTS: 
          automata.add(BasicAutomata.makeAnyString()); // same as cover?
          break;
          
        default:
          automata.add(BasicAutomata.makeChar(c));
      }
      i += length;
    }
    
    return BasicOperations.concatenate(automata);
  }
  
  /**
   * Returns the pattern term.
   */
  public Term getTerm() {
    return term;
  }
  
  /** Prints a user-readable version of this query. */
  @Override
  public String toString(String field) {
    StringBuilder buffer = new StringBuilder();
    if (!getField().equals(field)) {
      buffer.append(getField());
      buffer.append(":");
    }
    buffer.append(term.text());
    buffer.append(ToStringUtils.boost(getBoost()));
    return buffer.toString();
  }
}