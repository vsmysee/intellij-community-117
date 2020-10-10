/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Created by IntelliJ IDEA.
 * User: max
 * Date: Feb 3, 2002
 * Time: 9:49:29 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.intellij.codeInspection.dataFlow;

import com.intellij.codeInsight.NullableNotNullManager;
import com.intellij.codeInspection.dataFlow.value.DfaTypeValue;
import com.intellij.codeInspection.dataFlow.value.DfaValue;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiVariable;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DfaVariableState implements Cloneable {
  private final Set<DfaTypeValue> myInstanceofValues;
  private final Set<DfaTypeValue> myNotInstanceofValues;
  private boolean myNullable = false;
  private final boolean myVariableIsDeclaredNotNull;
  private final PsiVariable myVar;

  public DfaVariableState(@Nullable PsiVariable var) {
    myVar = var;
    myInstanceofValues = new HashSet<DfaTypeValue>();
    myNotInstanceofValues = new HashSet<DfaTypeValue>();
    myNullable = var != null && NullableNotNullManager.isNullable(var);
    myVariableIsDeclaredNotNull = var != null && NullableNotNullManager.isNotNull(var);
  }

  protected DfaVariableState(final DfaVariableState toClone) {
    myVar = toClone.myVar;
    myInstanceofValues = new THashSet<DfaTypeValue>(toClone.myInstanceofValues);
    myNotInstanceofValues = new THashSet<DfaTypeValue>(toClone.myNotInstanceofValues);
    myNullable = toClone.myNullable;
    myVariableIsDeclaredNotNull = toClone.myVariableIsDeclaredNotNull;
  }

  public boolean isNullable() {
    return myNullable;
  }

  private boolean checkInstanceofValue(DfaTypeValue dfaType) {
    if (myInstanceofValues.contains(dfaType)) return true;

    for (DfaTypeValue dfaTypeValue : myNotInstanceofValues) {
      if (dfaTypeValue.isAssignableFrom(dfaType)) return false;
    }

    for (DfaTypeValue dfaTypeValue : myInstanceofValues) {
      if (!dfaType.isConvertibleFrom(dfaTypeValue)) return false;
    }

    return true;
  }

  public boolean setInstanceofValue(DfaTypeValue dfaType) {
    myNullable |= dfaType.isNullable();

    if (dfaType.getType() instanceof PsiPrimitiveType) return true;

    if (checkInstanceofValue(dfaType)) {
      myInstanceofValues.add(dfaType);
      return true;
    }

    return false;
  }

  public boolean addNotInstanceofValue(DfaTypeValue dfaType) {
    if (myNotInstanceofValues.contains(dfaType)) return true;

    for (DfaTypeValue dfaTypeValue : myInstanceofValues) {
      if (dfaType.isAssignableFrom(dfaTypeValue)) return false;
    }

    myNotInstanceofValues.add(dfaType);
    return true;
  }

  public int hashCode() {
    return myInstanceofValues.hashCode() + myNotInstanceofValues.hashCode();
  }

  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (!(obj instanceof DfaVariableState)) return false;
    DfaVariableState aState = (DfaVariableState) obj;
    return myInstanceofValues.equals(aState.myInstanceofValues) &&
           myNotInstanceofValues.equals(aState.myNotInstanceofValues) &&
           myNullable == aState.myNullable;
  }

  protected Object clone() throws CloneNotSupportedException {
    return new DfaVariableState(this);
  }

  public String toString() {
    @NonNls StringBuilder buf = new StringBuilder();

    buf.append("instanceof {");
    for (Iterator<DfaTypeValue> iterator = myInstanceofValues.iterator(); iterator.hasNext();) {
      DfaTypeValue dfaTypeValue = iterator.next();
      buf.append(dfaTypeValue);
      if (iterator.hasNext()) buf.append(", ");
    }
    buf.append("} ");

    buf.append("not instanceof {");
    for (Iterator<DfaTypeValue> iterator = myNotInstanceofValues.iterator(); iterator.hasNext();) {
      DfaTypeValue dfaTypeValue = iterator.next();
      buf.append(dfaTypeValue);
      if (iterator.hasNext()) buf.append(", ");
    }
    buf.append("}");
    buf.append(", nullable=").append(myNullable);
    return buf.toString();
  }

  public boolean isNotNull() {
    return myVariableIsDeclaredNotNull;
  }

  public void setNullable(final boolean nullable) {
    myNullable = nullable;
  }

  public PsiVariable getVariable() {
    return myVar;
  }

  public void setValue(DfaValue value) {
  }

  @Nullable
  public DfaValue getValue() {
    return null;
  }
}
