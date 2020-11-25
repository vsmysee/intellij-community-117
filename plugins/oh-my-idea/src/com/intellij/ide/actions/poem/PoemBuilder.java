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

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PoemBuilder {

    private static final int titleSize = 25;
    private static final int bodySize = 35;


    private static JComponent title(List<String> poems) {

        Box horizontalBox = Box.createHorizontalBox();

        horizontalBox.add(Box.createHorizontalGlue());

        JPanel title = new JPanel();

        String text = poems.get(1);
        int endIndex = text.indexOf("《");
        String author = text.substring(0, endIndex);
        JLabel comp = buildJLabel(text, titleSize);
        comp.setText("<html><font color=rgb(72,72,72)>" + author + "</font><font color='blue'>" + text.substring(endIndex) + "</font></html>");

        horizontalBox.add(title);

        horizontalBox.add(Box.createHorizontalGlue());

        title.add(comp);
        return horizontalBox;
    }

    public static PoemPanel build(List<String> poems) {

        JComponent poemRoot = Box.createVerticalBox();

        PoemPanel poemPanel = new PoemPanel(poemRoot);

        poemRoot.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        poemRoot.add(title(poems));

        poemRoot.add(new JSeparator());
        poemRoot.add(Box.createVerticalStrut(10));

        int index = 2;

        while (index < poems.size()) {

            JPanel panel = new JPanel();

            String left = poems.get(index).trim();
            panel.add(buildJLabel(left, bodySize));

            index++;

            poemPanel.add(panel);

        }

        Box poemContent = Box.createVerticalBox();
        for (JPanel poemLabel : poemPanel.getPoemLabels()) {
            poemContent.add(poemLabel);
        }


        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(new CirclePanel(poems.get(0), 20, 25));
        poemContent.add(bottom);

        poemPanel.add(bottom);

        poemRoot.add(poemContent);

        return poemPanel;
    }

    private static JLabel buildJLabel(String text, int size) {
        JLabel item = new JLabel(text);
        item.setFont(new Font(Setting.FONT, Font.BOLD, size));
        return item;
    }


}
