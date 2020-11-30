package com.intellij.ide.actions;


import java.util.HashMap;

public class KeyDef {

  static void init() {

    MyTypedActionHandler.modeMap.put(App.EditorMode.COMMAND1, new HashMap<String, String>() {{

      put("a", "ShowSelectActions");


      put("1", "FindInPath");
      put("!", "RecentFiles");

      put("2", "ReplaceInPath");
      put("@", "Unwrap");

      put("3", "HideAllWindows");
      put("#", "EditorPageUp");

      put("4", "$Cut");
      put("$", "StepInto");

      put("5", "EditorDuplicate");
      put("%", "StepOver");

      put("6", "IntroduceVariable");
      put("^", "RenameElement");

      put("7", "EditorDeleteLine");
      put("&", "StepOut");

      put("8", "GotoFile");
      put("*", "EditorPageDown");

      put("9", "GotoClass");
      put("(", "GotoSymbol");

      put("0", "CompileDirty");
      put(")", "JumpToLastWindow");

      put("-", "OptimizeImports");
      put("_", "ShowIntentionActions");

      put("+", "JumpToNextChange");
      put("=", "JumpToLastChange");

      put("q", "FindUsages");
      put("Q", "ShowUsages");

      put("w", "EditorNextWord");
      put("W", "EditorPreviousWord");

      put("e", "EditorSelectWord");
      put("E", "EditorUnSelectWord");

      put("R", "CloseContent");
      put("r", "$Redo");

      put("t", "NextTab");
      put("T", "PreviousTab");

      put("u", "$Undo");
      put("U", "EditorToggleCase");

      put("o", "EditorStartNewLine");
      put("O", "EditorStartNewLineBefore");

      put("p", "$Paste");
      put("P", "ParameterInfo");

      put("[", "Inline");
      put("]", "ImplementMethods");

      put("A", "RecentChangedFiles");

      put("s", "NextSplitter");
      put("S", "PrevSplitter");

      put("d", "GotoDeclaration");
      put("D", "GotoImplementation");

      put("f", "ReformatCode");
      put("F", "FindWordAtCaret");

      put("g", "Generate");
      put("G", "EditorJoinLines");

      put("h", "EditorLeft");
      put("H", "EditorLineStart");

      put("j", "EditorDown");
      put("J", "EditorMoveToPageBottom");

      put("k", "EditorUp");
      put("K", "EditorMoveToPageTop");

      put("l", "EditorRight");
      put("L", "EditorLineEnd");

      put(";", "RunClass");
      put(":", "CodeCompletion");

      put("'", "GotoSuperMethod");
      put("\"", "OverrideMethods");

      put("z", "SurroundWith");
      put("Z", "GotoLine");

      put("x", "$Delete");
      put("X", "EditorDeleteToLineEnd");

      put("c", "SmartTypeCompletion");
      put("C", "EvaluateExpression");

      put("v", "ToggleLineBreakpoint");
      put("V", "QuickJavaDoc");

      put("b", "Back");
      put("B", "DebugClass");

      put("n", "FindNext");
      put("N", "FindPrevious");

      put("m", "MethodDown");
      put("M", "MethodUp");

      put(".", "EditorCompleteStatement");
      put(">", "$Copy");

      put(",", "GotoNextError");
      put("<", "GotoPreviousError");

      put("/", "Find");
      put("?", "Replace");

    }});


    //y
    MyTypedActionHandler.modeMap.put(App.EditorMode.COMMAND2, new HashMap<String, String>() {{

      put("a", "ShowSelectActions");


      put("1", "EditorCodeBlockStartWithSelection");
      put("2", "EditorCodeBlockEndWithSelection");
      put("3", "POEM_FORWARD");
      put("4", "ProjectViewChangeView");
      put("5", "ActivateVersionControlToolWindow");
      put("6", "ExtractMethod");
      put("7", "ActivateCommitToolWindow");
      put("8", "POEM_BACK");
      put("9", "IntroduceParameter");
      put("0", "HighlightUsagesInFile");
      put("-", "IntroduceConstant");
      put("=", "IntroduceField");

      put("q", "EditorTextEndWithSelection");
      put("w", "ClassNameCompletion");
      put("e", "MoveLineUp");
      put("E", "MoveStatementUp");
      put("u", "EditorToggleColumnMode");

      put("B", "Forward");


      put("i", "InsertLiveTemplate");

      put("/", "CommentByLineComment");
      put("?", "CommentByBlockComment");
      put("h", "CallHierarchy");


      put("P", "EditorToggleShowLineNumbers");


      put("d", "MoveLineDown");
      put("D", "MoveStatementDown");

      put("v", "SplitVertically");
      put("V", "SplitHorizontally");

      put("<", "EditorCodeBlockStart");
      put(">", "EditorCodeBlockEnd");


      put("s", "ActivateStructureToolWindow");
      put("p", "ActivateProjectToolWindow");
      put("t", "ActivateTerminalToolWindow");
      put("m", "ActivateMessagesToolWindow");
      put("o", "ActivateTODOToolWindow");
      put("f", "ActivateFavoritesToolWindow");


    }});


    //Y
    MyTypedActionHandler.modeMap.put(App.EditorMode.COMMAND3, new HashMap<String, String>() {{

      put("a", "ShowSelectActions");


      put("1", "ShowNavBar");
      put("2", "ShowSettings");
      put("3", "ShowProjectStructureSettings");
      put("4", "$Cut");
      put("6", "GotoTypeDeclaration");
      put("7", "GotoTest");
      put("8", "GotoRelated");
      put("9", "CloseActiveTab");
      put("-", "GotoAction");
      put("=", "FileStructurePopup");

      put("q", "ShowBookmarks");
      put("w", "EditorDeleteToWordStart");
      put("W", "EditorDeleteToWordEnd");
      put("e", "PreviousOccurence");
      put("r", "NextOccurence");
      put("t", "ShowPopupMenu");
      put("u", "Rerun");
      put("o", "NextParameter");
      put("O", "PrevParameter");

      put("S", "$SelectAll");
      put("s", "Switcher");
      put("f", "AddToFavoritesPopup");
      put("g", "Resume");
      put("b", "ViewBreakpoints");


      put("h", "EditorLeftWithSelection");
      put("l", "EditorRightWithSelection");
      put("j", "EditorDownWithSelection");
      put("k", "EditorUpWithSelection");
      put("H", "EditorLineStartWithSelection");
      put("L", "EditorLineEndWithSelection");
      put("J", "EditorScrollUp");
      put("K", "EditorScrollDown");


      put("m", "EditorPreviousWordWithSelection");
      put("M", "EditorNextWordWithSelection");


      put("'", "RemoveFromFavorites");

      put("z", "EditorContextInfo");
      put("x", "$Delete");


      put("n", "NextProjectWindow");
      put("N", "PreviousProjectWindow");

      put(">", "$Copy");

    }});



    //I
    MyTypedActionHandler.modeMap.put(App.EditorMode.COMMAND4, new HashMap<String, String>() {{

      put("a", "ShowSelectActions");


      put("1", "GotoBookmark0");
      put("2", "GotoBookmark1");
      put("3", "GotoBookmark2");
      put("4", "GotoBookmark3");
      put("6", "GotoBookmark4");
      put("7", "GotoBookmark5");
      put("8", "GotoBookmark6");
      put("9", "GotoBookmark7");
      put("0", "GotoBookmark8");
      put("-", "GotoBookmark9");
      put("=", "ChooseRunConfiguration");

      put("q", "ChooseDebugConfiguration");
      put("w", "ToggleBookmark");
      put("W", "ToggleBookmarkWithMnemonic");
      put("e", "AutoIndentLines");
      put("n", "EditorBackSpace");
      put("N", "EditorDelete");

      put("l", "EditorLookupUp");
      put("L", "EditorLookupDown");

      put("f", "ToggleFullScreen");
      put("z", "ToggleZenMode");
      put("c", "EditorScrollToCenter");

      put("K", "EditorPageUpWithSelection");
      put("J", "EditorPageDownWithSelection");
      put("k", "EditorMoveToPageTopWithSelection");
      put("j", "EditorMoveToPageBottomWithSelection");


      put("r", "Synchronize");


    }});


  }

}
