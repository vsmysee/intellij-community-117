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

package com.intellij.openapi.progress;

import com.intellij.openapi.application.ModalityState;
import org.jetbrains.annotations.NotNull;

public class EmptyProgressIndicator implements ProgressIndicator {
  private boolean myIsRunning = false;
  private volatile boolean myIsCanceled = false;
  private boolean myFinished;

  public void start() {
    myIsRunning = true;
    myIsCanceled = false;
    myFinished = false;
  }

  public void stop() {
    myIsRunning = false;
  }

  public boolean isRunning() {
    return myIsRunning;
  }

  public void cancel() {
    myIsCanceled = true;
  }

  public boolean isCanceled() {
    return myIsCanceled;
  }

  public void setText(String text) {
  }

  public String getText() {
    return "";
  }

  public void setText2(String text) {
  }

  public String getText2() {
    return "";
  }

  public double getFraction() {
    return 1;
  }

  public void setFraction(double fraction) {
  }

  public void pushState() {
  }

  public void popState() {
  }

  public void startNonCancelableSection() {
  }

  public void finishNonCancelableSection() {
  }

  public boolean isModal() {
    return false;
  }

  @NotNull
  public ModalityState getModalityState() {
    return ModalityState.NON_MODAL;
  }

  public void setModalityProgress(ProgressIndicator modalityProgress) {
  }

  public boolean isIndeterminate() {
    return false;
  }


  public void finish(final Task task) {
    myFinished = true;
  }

  public boolean isFinished(final Task task) {
    return myFinished;
  }

  public void setIndeterminate(boolean indeterminate) {
  }

  public void checkCanceled() {
    if (myIsCanceled) {
      throw new ProcessCanceledException();
    }
  }

  @Override
  public boolean isPopupWasShown() {
    return false;
  }

  @Override
  public boolean isShowing() {
    return false;
  }
}
