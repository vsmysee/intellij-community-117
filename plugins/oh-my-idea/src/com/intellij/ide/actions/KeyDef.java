package com.intellij.ide.actions;


import java.util.HashMap;
import java.util.Map;

public class KeyDef {

  public static Map<App.EditorMode, Map<String, String>> modeMap = new HashMap<App.EditorMode, Map<String, String>>();

  public static Map<String, String> keys_first = new HashMap<String, String>(){{

    put("1", "FindInPath");
    put("2", "ReplaceInPath");
    put("3", "HideAllWindows");
    put("4", "$Cut");
    put("5", "EditorDuplicate");
    put("6", "IntroduceVariable");
    put("7", "EditorDeleteLine");
    put("8", "GotoFile");
    put("9", "GotoClass");
    put("0", "CompileDirty");

    put("q", "FindUsages");
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
    put("O", "StepOver");
    put("p", "$Paste");
    put("a", "RecentFiles");
    put("s", "NextSplitter");
    put("S", "PrevSplitter");
    put("d", "GotoDeclaration");
    put("F", "FindWordAtCaret");
    put("f", "ReformatCode");
    put("g", "Generate");
    put("h", "EditorLeft");
    put("H", "EditorLineStart");
    put("j", "EditorDown");
    put("J", "EditorMoveToPageBottom");
    put("k", "EditorUp");
    put("K", "EditorMoveToPageTop");
    put("l", "EditorRight");
    put("L", "EditorLineEnd");
    put(";", "RunClass");
    put("'", "GotoSuperMethod");
    put("z", "SurroundWith");
    put("x", "$Delete");
    put("c", "EvaluateExpression");
    put("v", "ToggleLineBreakpoint");
    put("b", "Back");
    put("B", "DebugClass");
    put("n", "FindNext");
    put("N", "FindPrevious");
    put("m", "MethodDown");
    put("M", "MethodUp");
    put(">", "$Copy");
    put(",", "GotoNextError");
    put("<", "GotoPreviousError");
    put(".", "EditorCompleteStatement");
    put("/", "Find");
    put("?", "Replace");
  }};


  public static Map<String, String> keys_second = new HashMap<String, String>(){{


    put("1", "JumpToLastWindow");
    put("2", "EditorDeleteToLineEnd");
    put("3", "ActivateProjectToolWindow");
    put("4", "Inline");
    put("5", "ShowIntentionActions");
    put("6", "ImplementMethods");
    put("7", "OverrideMethods");
    put("8", "JumpToLastChange");
    put("9", "EditorJoinLines");

    put("l", "CodeCompletion");
    put("/", "CommentByLineComment");
    put("o", "OptimizeImports");
    put("O", "GotoImplementation");

    put("e", "MoveLineUp");
    put("E", "MoveStatementUp");
    put("d", "MoveLineDown");
    put("D", "MoveStatementDown");

    put("v", "SplitVertically");
    put("V", "SplitHorizontally");

    put("<", "EditorCodeBlockStart");
    put(">", "EditorCodeBlockEnd");
    put("f", "SmartTypeCompletion");
    put("n", "EditorToggleShowLineNumbers");
    put("N", "GotoLine");


    put("s", "ActivateStructureToolWindow");
    put("p", "ActivateProjectToolWindow");
    put("t", "ActivateTerminalToolWindow");

  }};



  public static Map<String, String> keys_third = new HashMap<String, String>(){{

    put("h", "EditorLeftWithSelection");
    put("l", "EditorRightWithSelection");
    put("j", "EditorDownWithSelection");
    put("k", "EditorUpWithSelection");
    put("H", "EditorLineStartWithSelection");
    put("L", "EditorLineEndWithSelection");
    put("J", "EditorScrollUp");
    put("K", "EditorScrollDown");


  }};

  static {
    modeMap.put(App.EditorMode.COMMAND,keys_first);
    modeMap.put(App.EditorMode.COMMAND2,keys_second);
    modeMap.put(App.EditorMode.COMMAND3,keys_third);
  }


}
