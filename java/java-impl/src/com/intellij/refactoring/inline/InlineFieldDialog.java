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
package com.intellij.refactoring.inline;

import com.intellij.openapi.help.HelpManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.util.PsiFormatUtil;
import com.intellij.refactoring.HelpID;
import com.intellij.refactoring.JavaRefactoringSettings;
import com.intellij.refactoring.RefactoringBundle;

public class InlineFieldDialog extends InlineOptionsDialog {
  public static final String REFACTORING_NAME = RefactoringBundle.message("inline.field.title");
  private final PsiReferenceExpression myReferenceExpression;

  private final PsiField myField;
  protected final int myOccurrencesNumber;

  public InlineFieldDialog(Project project, PsiField field, PsiReferenceExpression ref) {
    super(project, true, field);
    myField = field;
    myReferenceExpression = ref;
    myInvokedOnReference = myReferenceExpression != null;

    setTitle(REFACTORING_NAME);
    myOccurrencesNumber = initOccurrencesNumber(myField);
    init();
  }

  protected String getNameLabelText() {
    String fieldText = PsiFormatUtil.formatVariable(myField, PsiFormatUtil.SHOW_NAME | PsiFormatUtil.SHOW_TYPE,PsiSubstitutor.EMPTY);
    return RefactoringBundle.message("inline.field.field.name.label", fieldText);
  }

  protected String getBorderTitle() {
    return RefactoringBundle.message("inline.field.border.title");
  }

  protected String getInlineThisText() {
    return RefactoringBundle.message("this.reference.only.and.keep.the.field");
  }

  protected String getInlineAllText() {
    final String occurrencesString = myOccurrencesNumber > -1 ? " (" + myOccurrencesNumber + " occurrence" + (myOccurrencesNumber == 1 ? ")" : "s)") : "";
    return RefactoringBundle.message("all.references.and.remove.the.field") + occurrencesString;
  }

  protected boolean isInlineThis() {
    return JavaRefactoringSettings.getInstance().INLINE_FIELD_THIS;
  }

  protected void doAction() {
    invokeRefactoring(new InlineConstantFieldProcessor(myField, getProject(), myReferenceExpression, isInlineThisOnly()));
    JavaRefactoringSettings settings = JavaRefactoringSettings.getInstance();
    if(myRbInlineThisOnly.isEnabled() && myRbInlineAll.isEnabled()) {
      settings.INLINE_FIELD_THIS = isInlineThisOnly();
    }
  }

  protected void doHelpAction() {
    HelpManager.getInstance().invokeHelp(HelpID.INLINE_FIELD);
  }
}
