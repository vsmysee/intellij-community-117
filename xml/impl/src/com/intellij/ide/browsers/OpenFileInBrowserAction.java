package com.intellij.ide.browsers;

import com.intellij.ide.BrowserUtil;
import com.intellij.ide.browsers.impl.WebBrowserServiceImpl;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import com.intellij.xml.XmlBundle;
import com.intellij.xml.util.HtmlUtil;

import java.awt.event.InputEvent;

public class OpenFileInBrowserAction extends DumbAwareAction {
  private static final Logger LOG = Logger.getInstance("#com.intellij.ide.browsers.OpenFileInBrowserAction");

  public void update(AnActionEvent e) {
    final DataContext dataContext = e.getDataContext();
    final PsiFile file = LangDataKeys.PSI_FILE.getData(dataContext);
    final Presentation presentation = e.getPresentation();

    if (file != null && file.getVirtualFile() != null) {
      presentation.setVisible(true);

      final WebBrowserUrlProvider browserUrlProvider = WebBrowserServiceImpl.getProvider(file);
      final boolean isHtmlFile = HtmlUtil.isHtmlFile(file);
      presentation.setEnabled(browserUrlProvider != null || isHtmlFile);
      String text = getTemplatePresentation().getText();
      String description = getTemplatePresentation().getDescription();

      if (browserUrlProvider != null) {
        final String customText = browserUrlProvider.getOpenInBrowserActionText(file);
        if (customText != null) {
          text = customText;
        }
        final String customDescription = browserUrlProvider.getOpenInBrowserActionDescription(file);
        if (customDescription != null) {
          description = customDescription;
        }
        if (isHtmlFile) {
          description += " (hold Shift to open URL of local file)";
        }
      }

      presentation.setText(text);
      presentation.setDescription(description);
      if (ActionPlaces.isPopupPlace(e.getPlace())) {
        presentation.setVisible(presentation.isEnabled());
      }
    } else {
      presentation.setVisible(false);
      presentation.setEnabled(false);
    }
  }

  public void actionPerformed(AnActionEvent e) {
    final DataContext dataContext = e.getDataContext();
    final PsiFile psiFile = LangDataKeys.PSI_FILE.getData(dataContext);
    LOG.assertTrue(psiFile != null);
    try {
      final InputEvent event = e.getInputEvent();
      final String url = WebBrowserService.getInstance().getUrlToOpen(psiFile, event != null && event.isShiftDown());
      if (url != null) {
        ApplicationManager.getApplication().saveAll();
        BrowserUtil.launchBrowser(url);
      }
    }
    catch (WebBrowserUrlProvider.BrowserException e1) {
      Messages.showErrorDialog(e1.getMessage(), XmlBundle.message("browser.error"));
    }
    catch (Exception e1) {
      LOG.error(e1);
    }
  }
}
