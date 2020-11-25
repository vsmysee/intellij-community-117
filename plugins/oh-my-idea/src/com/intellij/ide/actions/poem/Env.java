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

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Env {


    public static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    private static final String OS_NAME = System.getProperty("os.name");
    private static final String OS_VERSION = System.getProperty("os.version");


    public static boolean isWindows() {
        return OS_NAME.indexOf("Windows") > -1;
    }

    public static boolean isLinux() {
        return OS_NAME.indexOf("Linux") > -1;
    }

    public static boolean isMacOs() {
        return OS_NAME.indexOf("Mac OS") > -1;
    }

    public static List<String> FONTS = new ArrayList<String>();

    public static void fontList() {

        String fonts[] =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        for (int i = 0; i < fonts.length; i++) {

            if (fonts[i].matches("[\\u4E00-\\u9FA5]+")) {
                FONTS.add(fonts[i]);
            }
        }

    }


    public static int getHeight() {
        return screenSize.height;
    }

    public static int getWidth() {
        return screenSize.width;
    }

}
