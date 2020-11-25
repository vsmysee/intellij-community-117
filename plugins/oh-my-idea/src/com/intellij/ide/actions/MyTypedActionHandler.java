package com.intellij.ide.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class MyTypedActionHandler implements TypedActionHandler {

  private TypedActionHandler origHandler;

  public MyTypedActionHandler(TypedActionHandler handler) {
    this.origHandler = handler;
  }


  @Override
  public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {

    if (App.editorMode == App.EditorMode.COMMAND) {
      if (charTyped == 'i') {
        editor.getSettings().setBlockCursor(false);
        App.editorMode = App.EditorMode.INSERT;
        return;
      }
      if (charTyped == 'I') {
        App.editorMode = App.EditorMode.COMMAND3;
        return;
      }
      if (charTyped == 'y') {
        App.editorMode = App.EditorMode.COMMAND2;
        return;
      }

    }

    if (App.editorMode == App.EditorMode.INSERT) {
      origHandler.execute(editor, charTyped, dataContext);
      return;
    }

    Map<String,String> keyMapping = KeyDef.modeMap.get(App.editorMode);
    if (keyMapping.containsKey(String.valueOf(charTyped))) {
      doAction(dataContext, keyMapping.get(String.valueOf(charTyped)));
    }

  }


  private void doAction(DataContext dataContext, String actionName) {
    final AnAction acton = ActionManager.getInstance().getAction(actionName);
    if (acton != null) {
      acton.actionPerformed(new AnActionEvent(null, dataContext, "", new Presentation(), ActionManager.getInstance(), 0));
    }
  }


  public TypedActionHandler getOriginalTypedHandler() {
    return origHandler;
  }


}