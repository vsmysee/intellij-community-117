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
package com.intellij.psi.impl.source;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 *  @author dsl
 */
public class PsiImmediateClassType extends PsiClassType {
  private static final Logger LOG = Logger.getInstance("#com.intellij.psi.impl.source.PsiImmediateClassType");
  private final PsiClass myClass;
  private final PsiSubstitutor mySubstitutor;
  private final PsiManager myManager;
  private String myCanonicalText;
  private String myPresentableText;
  private String myInternalCanonicalText;

  private final ClassResolveResult myClassResolveResult = new ClassResolveResult() {
    @Override
    public PsiClass getElement() {
      return myClass;
    }

    @Override
    public PsiSubstitutor getSubstitutor() {
      return mySubstitutor;
    }

    @Override
    public boolean isValidResult() {
      return true;
    }

    @Override
    public boolean isAccessible() {
      return true;
    }

    @Override
    public boolean isStaticsScopeCorrect() {
      return true;
    }

    @Override
    public PsiElement getCurrentFileResolveScope() {
      return null;
    }

    @Override
    public boolean isPackagePrefixPackageReference() {
      return false;
    }
  };

  public PsiImmediateClassType(PsiClass aClass, PsiSubstitutor substitutor) {
    this (aClass, substitutor, null);
  }

  public PsiImmediateClassType(final PsiClass aClass, final PsiSubstitutor substitutor, final LanguageLevel languageLevel) {
    this(aClass, substitutor,languageLevel, PsiAnnotation.EMPTY_ARRAY);
  }

  public PsiImmediateClassType(PsiClass aClass, PsiSubstitutor substitutor, LanguageLevel languageLevel, PsiAnnotation[] annotations) {
    super(languageLevel,annotations);
    myClass = aClass;
    myManager = aClass.getManager();
    mySubstitutor = substitutor;
    LOG.assertTrue(mySubstitutor != null);
  }

  @Override
  public PsiClass resolve() {
    return myClass;
  }

  @Override
  public String getClassName() {
    return myClass.getName();
  }
  @Override
  @NotNull
  public PsiType[] getParameters() {
    final PsiTypeParameter[] parameters = myClass.getTypeParameters();
    if (parameters.length == 0) {
      return PsiType.EMPTY_ARRAY;
    }

    List<PsiType> lst = new ArrayList<PsiType>();
    for (PsiTypeParameter parameter : parameters) {
      PsiType substituted = mySubstitutor.substitute(parameter);
      if (substituted != null) {
        lst.add(substituted);
      }
    }
    return lst.toArray(new PsiType[lst.size()]);
  }

  @Override
  @NotNull
  public ClassResolveResult resolveGenerics() {
    return myClassResolveResult;
  }

  @Override
  @NotNull
  public PsiClassType rawType() {
    return JavaPsiFacade.getInstance(myClass.getProject()).getElementFactory().createType(myClass);
  }

  @Override
  public String getPresentableText() {
    if (myPresentableText == null) {
      final StringBuilder buffer = new StringBuilder();
      buildText(myClass, buffer, false, false);
      myPresentableText = buffer.toString();
    }
    return myPresentableText;
  }

  @Override
  public String getCanonicalText() {
    if (myCanonicalText == null) {
      final StringBuilder buffer = new StringBuilder();
      buildText(myClass, buffer, true, false);
      myCanonicalText = buffer.toString();
    }
    return myCanonicalText;
  }

  @Override
  public String getInternalCanonicalText() {
    if (myInternalCanonicalText == null) {
      final StringBuilder buffer = new StringBuilder();
      buildText(myClass, buffer, true, true);
      myInternalCanonicalText = buffer.toString();
    }
    return myInternalCanonicalText;
  }

  private void buildText(PsiClass aClass, StringBuilder buffer, boolean canonical, boolean internal) {
    PsiSubstitutor substitutor = mySubstitutor;
    if (aClass instanceof PsiAnonymousClass) {
      ClassResolveResult baseResolveResult = ((PsiAnonymousClass) aClass).getBaseClassType().resolveGenerics();
      aClass = baseResolveResult.getElement();
      substitutor = baseResolveResult.getSubstitutor();
      if (aClass == null) return;
    }
    PsiClass parentClass = null;
    if (!aClass.hasModifierProperty(PsiModifier.STATIC)) {
      final PsiElement parent = aClass.getParent();
      if (parent instanceof PsiClass && !(parent instanceof PsiAnonymousClass)) {
        parentClass = (PsiClass)parent;
      }
    }
    buffer.append(getAnnotationsTextPrefix());
    if (parentClass != null) {
      buildText(parentClass, buffer, canonical, false);
      buffer.append('.');
      buffer.append(aClass.getName());
    }
    else {
      final String name;
      if (!canonical) {
        name = aClass.getName();
      }
      else {
        final String qualifiedName = aClass.getQualifiedName();
        if (qualifiedName != null) {
          name = qualifiedName;
        }
        else {
          name = aClass.getName();
        }
      }
      buffer.append(name);
    }

    final PsiTypeParameter[] typeParameters = aClass.getTypeParameters();
    if (typeParameters.length > 0) {
      StringBuilder pineBuffer = new StringBuilder();
      pineBuffer.append('<');
      for (int i = 0; i < typeParameters.length; i++) {
        PsiTypeParameter typeParameter = typeParameters[i];
        if (i > 0) pineBuffer.append(',');
        final PsiType substitutionResult = substitutor.substitute(typeParameter);
        if (substitutionResult == null) {
          pineBuffer = null;
          break;
        }
        if (!canonical) {
          pineBuffer.append(substitutionResult.getPresentableText());
        }
        else {
          if (internal) {
            pineBuffer.append(substitutionResult.getInternalCanonicalText());
          }
          else {
            pineBuffer.append(substitutionResult.getCanonicalText());
          }
        }
      }
      if (pineBuffer != null) {
        buffer.append(pineBuffer);
        buffer.append('>');
      }
    }
  }

  @Override
  public boolean isValid() {
    return myClass.isValid() && mySubstitutor.isValid();
  }

  @Override
  public boolean equalsToText(String text) {
    PsiElementFactory factory = JavaPsiFacade.getInstance(myManager.getProject()).getElementFactory();
    final PsiType patternType;
    try {
      patternType = factory.createTypeFromText(text, myClass);
    }
    catch (IncorrectOperationException e) {
      return false;
    }
    return equals(patternType);

  }

  @Override
  @NotNull
  public GlobalSearchScope getResolveScope() {
    return myClass.getResolveScope();
  }

  @Override
  @NotNull
  public LanguageLevel getLanguageLevel() {
    if (myLanguageLevel != null) return myLanguageLevel;
    return PsiUtil.getLanguageLevel(myClass);
  }

  @Override
  public PsiClassType setLanguageLevel(final LanguageLevel languageLevel) {
    if (languageLevel.equals(myLanguageLevel)) return this;
    return new PsiImmediateClassType(myClass, mySubstitutor, languageLevel,getAnnotations());
  }
}
