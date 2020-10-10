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
package com.intellij.cvsSupport2.cvsExecution;


/**
 * author: lesya
 */
public interface CvsOperationExecutorCallback {

  CvsOperationExecutorCallback EMPTY = new CvsOperationExecutorCallback(){
    public void executionFinished(boolean successfully) {
    }

    public void executionFinishedSuccessfully() {
    }

    public void executeInProgressAfterAction(ModalityContext modaityContext) {
    }
  };

  void executionFinished(boolean successfully);
  void executionFinishedSuccessfully();
  void executeInProgressAfterAction(ModalityContext modaityContext);
}
