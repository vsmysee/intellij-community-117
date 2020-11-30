package com.intellij.ide.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.ui.components.JBList;
import com.intellij.util.ArrayUtil;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

import static com.intellij.ide.actions.MyTypedActionHandler.modeMap;

public class ActionPop extends EditorAction {


  private static class Handler extends EditorActionHandler {
    @Override
    public void execute(Editor editor, final DataContext dataContext) {
      java.util.List<String> actions = new ArrayList<String>();
      Map<String, String> keyMapping = modeMap.get(App.editorMode);
      for (String key : keyMapping.keySet()) {
        actions.add(key + " => " + keyMapping.get(key));
      }
      final JList list = new JBList(ArrayUtil.toObjectArray(actions));
      list.setCellRenderer(new DefaultListCellRenderer() {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
          super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
          String pane = (String)value;
          setText(pane);
          return this;
        }
      });

      Runnable runnable = new Runnable() {
        @Override
        public void run() {
          if (list.getSelectedIndex() < 0) return;
          String actionKey = (String)list.getSelectedValue();
          String actionName = actionKey.split("=>")[1];

          final AnAction action = ActionManager.getInstance().getAction(actionName.trim());
          if (action != null) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {

              @Override
              public void run() {
                try {
                  action.actionPerformed(new AnActionEvent(null, dataContext, "", new Presentation(), ActionManager.getInstance(), 0));
                }
                catch (Exception e) {
                  e.printStackTrace();
                }
              }

            });
          }

        }
      };

      new PopupChooserBuilder(list).
        setTitle(App.editorMode.name()).
        setItemChoosenCallback(runnable).
        createPopup().showInBestPositionFor(editor);
    }
  }


  public ActionPop() {
    super(new Handler());
  }


}
