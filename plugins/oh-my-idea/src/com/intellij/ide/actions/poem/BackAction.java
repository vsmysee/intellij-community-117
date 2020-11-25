/*
 * Copyright 2000-2020 JetBrains s.r.o.
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
package com.intellij.ide.actions.poem;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class BackAction extends AnAction {

    private static Stack<String> stack = new Stack<String>();

    public static void add(String poem) {
        stack.push(poem);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        JPanel holder = PoemToolWindowFactory.holder;
        holder.remove(0);
        String item = PoemToolWindowFactory.random();
        try {
            item = stack.pop();
            if (item.equals(ForwardAction.current)) {
                item = stack.pop();
            }
        } catch (Exception exception) {
        }

        List<String> poems = Arrays.asList(item.split(";"));

        holder.add(PoemBuilder.build(poems).getPoem());
        holder.updateUI();
    }
}
