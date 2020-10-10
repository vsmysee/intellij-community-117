
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
package com.intellij.psi.impl.compiled;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.impl.PsiImplUtil;
import com.intellij.psi.impl.source.tree.ElementType;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocTagValue;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

class ClsDocTagImpl extends ClsElementImpl implements PsiDocTag {
  private static final Logger LOG = Logger.getInstance("#com.intellij.psi.impl.compiled.ClsDocTagImpl");

  private final ClsDocCommentImpl myDocComment;
  private final PsiElement myNameElement;

  public ClsDocTagImpl(ClsDocCommentImpl docComment, @NonNls String name) {
    myDocComment = docComment;
    myNameElement = new NameElement(name);
  }

  @Override
  public void appendMirrorText(final int indentLevel, final StringBuilder buffer) {
    buffer.append(myNameElement.getText());
  }

  @Override
  public void setMirror(@NotNull TreeElement element) {
    setMirrorCheckingType(element, ElementType.DOC_TAG);
  }

  @Override
  public String getText() {
    return myNameElement.getText();
  }

  @Override
  @NotNull
  public char[] textToCharArray(){
    return myNameElement.textToCharArray();
  }

  @Override
  public String getName() {
    return getNameElement().getText().substring(1);
  }

  @Override
  public boolean textMatches(@NotNull CharSequence text) {
    return myNameElement.textMatches(text);
  }

  @Override
  public boolean textMatches(@NotNull PsiElement element) {
    return myNameElement.textMatches(element);
  }

  @Override
  public int getTextLength(){
    return myNameElement.getTextLength();
  }

  @Override
  @NotNull
  public PsiElement[] getChildren() {
    return new PsiElement[]{myNameElement};
  }

  @Override
  public PsiElement getParent() {
    return getContainingComment();
  }

  @Override
  public PsiDocComment getContainingComment() {
    return myDocComment;
  }

  @Override
  public PsiElement getNameElement() {
    return myNameElement;
  }

  @Override
  public PsiElement[] getDataElements() {
    return PsiElement.EMPTY_ARRAY;
  }

  @Override
  public PsiDocTagValue getValueElement() {
    return null;
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JavaElementVisitor) {
      ((JavaElementVisitor)visitor).visitDocTag(this);
    }
    else {
      visitor.visitElement(this);
    }
  }

  private class NameElement extends ClsElementImpl {
    private final String myText;

    public NameElement(String text) {
      myText = text;
    }

    @Override
    public String getText() {
      return myText;
    }

    @Override
    @NotNull
    public char[] textToCharArray(){
      return myText.toCharArray();
    }

    @Override
    @NotNull
    public PsiElement[] getChildren(){
      return PsiElement.EMPTY_ARRAY;
    }

    @Override
    public void appendMirrorText(final int indentLevel, final StringBuilder buffer) {
    }

    @Override
    public void setMirror(@NotNull TreeElement element) {
      setMirrorCheckingType(element, null);
    }

    @Override
    public PsiElement getParent() {
      return ClsDocTagImpl.this;
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
      visitor.visitElement(this);
    }
  }
  @Override
  public PsiElement setName(@NotNull String name) throws IncorrectOperationException{
    PsiImplUtil.setName(getNameElement(), name);
    return this;
  }
}
