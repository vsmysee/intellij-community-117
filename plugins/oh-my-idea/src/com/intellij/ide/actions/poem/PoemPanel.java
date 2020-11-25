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
import java.util.ArrayList;
import java.util.List;


public class PoemPanel  {

    private List<JPanel> poemLabels = new ArrayList<JPanel>();

    private JComponent poem;

    public PoemPanel(JComponent poem) {
        this.poem = poem;
    }

    public void add(JPanel panel) {
        poemLabels.add(panel);
    }

    public List<JPanel> getPoemLabels() {
        return poemLabels;
    }

    public JComponent getPoem() {
        return poem;
    }
}
