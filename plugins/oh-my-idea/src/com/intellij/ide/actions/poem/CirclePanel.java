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


public class CirclePanel extends JPanel {

    public CirclePanel(String tag) {
        setLayout(new GridLayout(0, 1));
        add(new JLabel("<html><font color='white'>" + tag + "</font></html>"));
    }

    public CirclePanel(String tag, int width, int height) {
        setLayout(new GridLayout(0, 1));
        add(new JLabel("<html><font color='white' size=5>" + tag + "</font></html>"));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(Color.RED);

        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
    }
}
