/*
 * Copyright 2000-2009 JetBrains s.r.o.
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
package com.intellij.util.xml.impl;

import com.intellij.util.xml.Convert;
import com.intellij.util.xml.Converter;

import java.lang.annotation.Annotation;

/**
 * @author peter
*/
public class ConvertAnnotationImpl implements Convert {
  private final Converter myConverter;
  private final boolean mySoft;

  public ConvertAnnotationImpl(final Converter converter, final boolean soft) {
    myConverter = converter;
    mySoft = soft;
  }

  public Class<? extends Annotation> annotationType() {
    return Convert.class;
  }

  public Converter getConverter() {
    return myConverter;
  }

  public Class<? extends Converter> value() {
    return myConverter.getClass();
  }

  public boolean soft() {
    return mySoft;
  }

}
