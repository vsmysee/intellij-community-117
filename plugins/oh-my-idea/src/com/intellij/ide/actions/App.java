package com.intellij.ide.actions;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.intellij.openapi.editor.event.EditorFactoryAdapter;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import org.jetbrains.annotations.NotNull;


public class App implements ApplicationComponent {

  private static final String MODEACTION = "ChangeModeAction";
  private static final String ACTION_KEY = "ctrl I";

  private MyTypedActionHandler handler;

  public static EditorMode editorMode = EditorMode.COMMAND1;


  public static enum EditorMode {

    INSERT,

    COMMAND1,

    COMMAND2,

    COMMAND3,

    COMMAND4

  }


  @Override
  public void initComponent() {

    EditorActionManager manager = EditorActionManager.getInstance();
    TypedAction action = manager.getTypedAction();

    handler = new MyTypedActionHandler(action.getHandler());
    action.setupHandler(handler);


    Disposable parentDisposable = new Disposable() {
      @Override
      public void dispose() {
      }
    };

    EditorFactory.getInstance().addEditorFactoryListener(new EditorFactoryAdapter() {

      public void editorCreated(EditorFactoryEvent event) {
        final Editor editor = event.getEditor();
        editor.getSettings().setBlockCursor(true);

        AnAction acton = ActionManager.getInstance().getAction(MODEACTION);

        if (acton == null) {
          acton = new AnAction() {
            @Override
            public void actionPerformed(AnActionEvent e) {
              App.editorMode = EditorMode.COMMAND1;
              for (Editor editor : EditorFactory.getInstance().getAllEditors()) {
                editor.getSettings().setBlockCursor(true);
              }
            }
          };
          ActionManager.getInstance().registerAction(MODEACTION, acton);
        }

        acton.registerCustomShortcutSet(CustomShortcutSet.fromString(ACTION_KEY), editor.getComponent());

      }

    }, parentDisposable);
  }

  @Override
  public void disposeComponent() {
    EditorActionManager manager = EditorActionManager.getInstance();
    TypedAction action = manager.getTypedAction();
    action.setupHandler(handler.getOriginalTypedHandler());
  }

  @NotNull
  @Override
  public String getComponentName() {
    return "OhMyIDEA";
  }


}
