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
 * Class LineBreakpoint
 * @author Jeka
 */
package com.intellij.debugger.ui.breakpoints;

import com.intellij.debugger.DebuggerBundle;
import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.debugger.SourcePosition;
import com.intellij.debugger.actions.ThreadDumpAction;
import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.debugger.engine.evaluation.EvaluateException;
import com.intellij.debugger.engine.evaluation.EvaluationContextImpl;
import com.intellij.debugger.impl.DebuggerUtilsEx;
import com.intellij.debugger.impl.PositionUtil;
import com.intellij.debugger.jdi.StackFrameProxyImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.jsp.JspFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.classFilter.ClassFilter;
import com.intellij.util.Processor;
import com.intellij.util.StringBuilderSpinAllocator;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.ui.DebuggerIcons;
import com.sun.jdi.*;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.request.BreakpointRequest;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LineBreakpoint extends BreakpointWithHighlighter {
  private static final Logger LOG = Logger.getInstance("#com.intellij.debugger.ui.breakpoints.LineBreakpoint");

  // icons
  public static Icon ICON = DebuggerIcons.ENABLED_BREAKPOINT_ICON;
  public static final Icon MUTED_ICON = DebuggerIcons.MUTED_BREAKPOINT_ICON;
  public static final Icon DISABLED_ICON = DebuggerIcons.DISABLED_BREAKPOINT_ICON;
  public static final Icon MUTED_DISABLED_ICON = DebuggerIcons.MUTED_DISABLED_BREAKPOINT_ICON;
  private static final Icon ourMutedVerifiedWarningsIcon = IconLoader.getIcon("/debugger/db_muted_verified_warning_breakpoint.png");

  private String myMethodName;
  public static final @NonNls Key<LineBreakpoint> CATEGORY = BreakpointCategory.lookup("line_breakpoints");

  protected LineBreakpoint(Project project) {
    super(project);
  }

  protected LineBreakpoint(Project project, RangeHighlighter highlighter) {
    super(project, highlighter);
  }

  protected Icon getDisabledIcon(boolean isMuted) {
    final Breakpoint master = DebuggerManagerEx.getInstanceEx(myProject).getBreakpointManager().findMasterBreakpoint(this);
    if (isMuted) {
      return master == null? MUTED_DISABLED_ICON : DebuggerIcons.MUTED_DISABLED_DEPENDENT_BREAKPOINT_ICON;
    }
    else {
      return master == null? DISABLED_ICON : DebuggerIcons.DISABLED_DEPENDENT_BREAKPOINT_ICON;
    }
  }

  protected Icon getSetIcon(boolean isMuted) {
    return isMuted? MUTED_ICON : ICON;
  }

  protected Icon getInvalidIcon(boolean isMuted) {
    return isMuted? DebuggerIcons.MUTED_INVALID_BREAKPOINT_ICON : DebuggerIcons.INVALID_BREAKPOINT_ICON;
  }

  protected Icon getVerifiedIcon(boolean isMuted) {
    return isMuted? DebuggerIcons.MUTED_VERIFIED_BREAKPOINT_ICON : DebuggerIcons.VERIFIED_BREAKPOINT_ICON;
  }

  protected Icon getVerifiedWarningsIcon(boolean isMuted) {
    return isMuted? ourMutedVerifiedWarningsIcon : DebuggerIcons.VERIFIED_WARNING_BREAKPOINT_ICON;
  }

  public Key<LineBreakpoint> getCategory() {
    return CATEGORY;
  }

  protected void reload(PsiFile file) {
    super.reload(file);
    myMethodName = findMethodName(file, getHighlighter().getStartOffset());
  }

  protected void createOrWaitPrepare(DebugProcessImpl debugProcess, String classToBeLoaded) {
    if (isInScopeOf(debugProcess, classToBeLoaded)) {
      super.createOrWaitPrepare(debugProcess, classToBeLoaded);
    }
  }

  protected void createRequestForPreparedClass(final DebugProcessImpl debugProcess, final ReferenceType classType) {
    if (!isInScopeOf(debugProcess, classType.name())) {
      return;
    }
    try {
      List<Location> locs = debugProcess.getPositionManager().locationsOfLine(classType, getSourcePosition());
      if (!locs.isEmpty()) {
        for (Location loc : locs) {
          if (LOG.isDebugEnabled()) {
            LOG.debug("Found location [codeIndex=" + loc.codeIndex() +"] for reference type " + classType.name() + " at line " + getLineIndex() + "; isObsolete: " + (debugProcess.getVirtualMachineProxy().versionHigher("1.4") && loc.method().isObsolete()));
          }
          BreakpointRequest request = debugProcess.getRequestsManager().createBreakpointRequest(LineBreakpoint.this, loc);
          debugProcess.getRequestsManager().enableRequest(request);
          if (LOG.isDebugEnabled()) {
            LOG.debug("Created breakpoint request for reference type " + classType.name() + " at line " + getLineIndex() + "; codeIndex=" + loc.codeIndex());
          }
        }
      }
      else {
        // there's no executable code in this class
        debugProcess.getRequestsManager().setInvalid(LineBreakpoint.this, DebuggerBundle.message(
          "error.invalid.breakpoint.no.executable.code", (getLineIndex() + 1), classType.name())
        );
        if (LOG.isDebugEnabled()) {
          LOG.debug("No locations of type " + classType.name() + " found at line " + getLineIndex());
        }
      }
    }
    catch (ClassNotPreparedException ex) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("ClassNotPreparedException: " + ex.getMessage());
      }
      // there's a chance to add a breakpoint when the class is prepared
    }
    catch (ObjectCollectedException ex) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("ObjectCollectedException: " + ex.getMessage());
      }
      // there's a chance to add a breakpoint when the class is prepared
    }
    catch (InvalidLineNumberException ex) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("InvalidLineNumberException: " + ex.getMessage());
      }
      debugProcess.getRequestsManager().setInvalid(LineBreakpoint.this, DebuggerBundle.message("error.invalid.breakpoint.bad.line.number"));
    }
    catch (InternalException ex) {
      LOG.info(ex);
    }
    catch(Exception ex) {
      LOG.info(ex);
    }
    updateUI();
  }

  private boolean isInScopeOf(DebugProcessImpl debugProcess, String className) {
    final SourcePosition position = getSourcePosition();
    if (position != null) {
      final VirtualFile breakpointFile = position.getFile().getVirtualFile();
      final ProjectFileIndex fileIndex = ProjectRootManager.getInstance(myProject).getFileIndex();
      if (breakpointFile != null && fileIndex.isInSourceContent(breakpointFile)) {
        // apply filtering to breakpoints from content sources only, not for sources attached to libraries
        final Collection<VirtualFile> candidates = findClassCandidatesInSourceContent(className, debugProcess.getSearchScope(), fileIndex);
        if (candidates == null) {
          return true;
        }
        for (VirtualFile classFile : candidates) {
          if (breakpointFile.equals(classFile)) {
            return true;
          }
        }
        return false;
      }
    }
    return true;
  }

  @Nullable
  private Collection<VirtualFile> findClassCandidatesInSourceContent(final String className, final GlobalSearchScope scope, final ProjectFileIndex fileIndex) {
    final int dollarIndex = className.indexOf("$");
    final String topLevelClassName = dollarIndex >= 0? className.substring(0, dollarIndex) : className;
    return ApplicationManager.getApplication().runReadAction(new Computable<Collection<VirtualFile>>() {
      @Nullable
      public Collection<VirtualFile> compute() {
        final PsiClass[] classes = JavaPsiFacade.getInstance(myProject).findClasses(topLevelClassName, scope);
        if (classes.length == 0) {
          return null;
        }
        final List<VirtualFile> list = new ArrayList<VirtualFile>(classes.length);
        for (PsiClass aClass : classes) {
          final PsiFile psiFile = aClass.getContainingFile();
          if (psiFile != null) {
            final VirtualFile vFile = psiFile.getVirtualFile();
            if (vFile != null && fileIndex.isInSourceContent(vFile)) {
              list.add(vFile);
            }
          }
        }
        return list;
      }
    });
  }

  public boolean evaluateCondition(EvaluationContextImpl context, LocatableEvent event) throws EvaluateException {
    if(CLASS_FILTERS_ENABLED){
      String className = null;
      final ObjectReference thisObject = (ObjectReference)context.getThisObject();
      if(thisObject != null) {
        className = thisObject.referenceType().name();
      }
      else {
        final StackFrameProxyImpl frame = context.getFrameProxy();
        if (frame != null) {
          className = frame.location().declaringType().name();
        }
      }
      if (className != null) {
        boolean matches = false;
        for (ClassFilter classFilter : getClassFilters()) {
          if (classFilter.isEnabled() && classFilter.matches(className)) {
            matches = true;
            break;
          }
        }
        if(!matches) {
          return false;
        }
        for (ClassFilter classFilter : getClassExclusionFilters()) {
          if (classFilter.isEnabled() && classFilter.matches(className)) {
            return false;
          }
        }
      }
    }
    return super.evaluateCondition(context, event);
  }

  public String toString() {
    return getDescription();
  }

  @Override
  public String getShortName() {
    return getDisplayInfoInternal(false, 30);
  }

  public String getDisplayName() {
    return getDisplayInfoInternal(true, -1);
  }

  private String getDisplayInfoInternal(boolean showPackageInfo, int totalTextLength) {
    final RangeHighlighter highlighter = getHighlighter();
    if(highlighter.isValid() && isValid()) {
      final int lineNumber = (highlighter.getDocument().getLineNumber(highlighter.getStartOffset()) + 1);
      String className = getClassName();
      final boolean hasClassInfo = className != null && className.length() > 0;
      final boolean hasMethodInfo = myMethodName != null && myMethodName.length() > 0;
      if (hasClassInfo || hasMethodInfo) {
        final StringBuilder info = StringBuilderSpinAllocator.alloc();
        try {
          boolean isFile = getSourcePosition().getFile().getName().equals(className);
          String packageName = null;
          if (hasClassInfo) {
            final int dotIndex = className.lastIndexOf(".");
            if (dotIndex >= 0 && !isFile) {
              packageName = className.substring(0, dotIndex);
              className = className.substring(dotIndex + 1); 
            }

            if (totalTextLength != -1) {
              if (className.length() + (hasMethodInfo ? myMethodName.length() : 0) > totalTextLength + 3) {
                int offset = totalTextLength - (hasMethodInfo ? myMethodName.length() : 0);
                if (offset > 0 && offset < className.length()) {
                  className = className.substring(className.length() - offset);
                  info.append("...");
                }
              }
            }
            
            info.append(className);
          }
          if(hasMethodInfo) {
            if (isFile) {
              info.append(":");
            }
            else if (hasClassInfo) {
              info.append(".");
            }
            info.append(myMethodName);
          }
          if (showPackageInfo && packageName != null) {
            info.append(" (").append(packageName).append(")");
          }
          return DebuggerBundle.message("line.breakpoint.display.name.with.class.or.method", lineNumber, info.toString());
        }
        finally {
          StringBuilderSpinAllocator.dispose(info);
        }
      }
      return DebuggerBundle.message("line.breakpoint.display.name", lineNumber);
    }
    return DebuggerBundle.message("status.breakpoint.invalid");
  }

  private static @Nullable String findMethodName(final PsiFile file, final int offset) {
    if (file instanceof JspFile) {
      return null;
    }
    if (file instanceof PsiClassOwner) {
      return ApplicationManager.getApplication().runReadAction(new Computable<String>() {
        public String compute() {
          final PsiMethod method = DebuggerUtilsEx.findPsiMethod(file, offset);
          return method != null? method.getName() + "()" : null;
        }
      });
    }
    return null;
  }

  public String getEventMessage(LocatableEvent event) {
    final Location location = event.location();
    String sourceName = "Unknown Source";
    try {
      sourceName = location.sourceName();
    }
    catch (AbsentInformationException e) {
      sourceName = getSourcePosition().getFile().getName();
    }

    final boolean printFullTrace = Registry.is("debugger.breakpoint.message.full.trace");

    StringBuilder builder = new StringBuilder();
    if (printFullTrace) {
      builder.append(DebuggerBundle.message(
        "status.line.breakpoint.reached.full.trace",
        location.declaringType().name() + "." + location.method().name())
      );
      try {
        final List<StackFrame> frames = event.thread().frames();
        renderTrace(frames, builder);
      }
      catch (IncompatibleThreadStateException e) {
        builder.append("Stacktrace not available: ").append(e.getMessage());
      }
    }
    else {
      builder.append(DebuggerBundle.message(
        "status.line.breakpoint.reached",
        location.declaringType().name() + "." + location.method().name(),
        sourceName,
        getLineIndex() + 1
      ));
    }
    return builder.toString();
  }

  private static void renderTrace(List<StackFrame> frames, StringBuilder buffer) {
    for (final StackFrame stackFrame : frames) {
      final Location location = stackFrame.location();
      buffer.append("\n\t  ").append(ThreadDumpAction.renderLocation(location));
    }
  }

  public PsiElement getEvaluationElement() {
    return PositionUtil.getContextElement(getSourcePosition());
  }

  protected static LineBreakpoint create(Project project, Document document, int lineIndex) {
    VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
    if (virtualFile == null) {
      return null;
    }

    final RangeHighlighter highlighter = createHighlighter(project, document, lineIndex);
    if (highlighter == null) {
      return null;
    }

    LineBreakpoint breakpoint = new LineBreakpoint(project, highlighter);
    return (LineBreakpoint)breakpoint.init();
  }

  public boolean canMoveTo(SourcePosition position) {
    if (!super.canMoveTo(position)) {
      return false;
    }
    final Document document = PsiDocumentManager.getInstance(getProject()).getDocument(position.getFile());
    return canAddLineBreakpoint(myProject, document, position.getLine());
  }

  public static boolean canAddLineBreakpoint(Project project, final Document document, final int lineIndex) {
    if (lineIndex < 0 || lineIndex >= document.getLineCount()) {
      return false;
    }
    final BreakpointManager breakpointManager = DebuggerManagerEx.getInstanceEx(project).getBreakpointManager();
    final LineBreakpoint breakpointAtLine = breakpointManager.findBreakpoint( document, document.getLineStartOffset(lineIndex), CATEGORY);
    if (breakpointAtLine != null) {
      // there already exists a line breakpoint at this line
      return false;
    }
    PsiDocumentManager.getInstance(project).commitDocument(document);

    final boolean[] canAdd = new boolean[]{false};
    XDebuggerUtil.getInstance().iterateLine(project, document, lineIndex, new Processor<PsiElement>() {
      public boolean process(PsiElement element) {
        if ((element instanceof PsiWhiteSpace) || (PsiTreeUtil.getParentOfType(element, PsiComment.class, false) != null)) {
          return true;
        }
        PsiElement child = element;
        while(element != null) {

          final int offset = element.getTextOffset();
          if (offset >= 0) {
            if (document.getLineNumber(offset) != lineIndex) {
              break;
            }
          }
          child = element;
          element = element.getParent();
        }

        if(child instanceof PsiMethod && child.getTextRange().getEndOffset() >= document.getLineEndOffset(lineIndex)) {
          PsiCodeBlock body = ((PsiMethod)child).getBody();
          if(body == null) {
            canAdd[0] = false;
          }
          else {
            PsiStatement[] statements = body.getStatements();
            canAdd[0] = statements.length > 0 && document.getLineNumber(statements[0].getTextOffset()) == lineIndex;
          }
        }
        else {
          canAdd[0] = true;
        }
        return false;
      }
    });

    return canAdd[0];
  }

  public @Nullable String getMethodName() {
    return myMethodName;
  }
}
