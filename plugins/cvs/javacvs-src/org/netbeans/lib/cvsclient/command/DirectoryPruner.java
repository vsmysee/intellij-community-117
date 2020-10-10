/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.lib.cvsclient.command;

import org.netbeans.lib.cvsclient.IClientEnvironment;
import org.netbeans.lib.cvsclient.admin.Entry;
import org.netbeans.lib.cvsclient.admin.IAdminReader;
import org.netbeans.lib.cvsclient.admin.IAdminWriter;
import org.netbeans.lib.cvsclient.event.ICvsListener;
import org.netbeans.lib.cvsclient.event.ICvsListenerRegistry;
import org.netbeans.lib.cvsclient.event.IDirectoryListener;
import org.netbeans.lib.cvsclient.file.DirectoryObject;
import org.netbeans.lib.cvsclient.file.ICvsFileSystem;
import org.netbeans.lib.cvsclient.file.ILocalFileReader;
import org.netbeans.lib.cvsclient.util.BugLog;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author  Thomas Singer
 */
public final class DirectoryPruner
        implements ICvsListener, IDirectoryListener {

	// Fields =================================================================

	private final List directoriesToScan = new ArrayList();
	private final IClientEnvironment clientEnvironment;

	// Setup ==================================================================

	public DirectoryPruner(IClientEnvironment clientEnvironment) {
		BugLog.getInstance().assertNotNull(clientEnvironment);

		this.clientEnvironment = clientEnvironment;
	}

	// Implemented ============================================================

	public void processingDirectory(DirectoryObject directoryObject) {
		if (directoryObject.isRoot()) {
			return;
		}

		if (directoriesToScan.contains(directoryObject)) {
			return;
		}

		directoriesToScan.add(directoryObject);
	}

	public void registerListeners(ICvsListenerRegistry listenerRegistry) {
		listenerRegistry.addDirectoryListener(this);
	}

	public void unregisterListeners(ICvsListenerRegistry listenerRegistry) {
		listenerRegistry.removeDirectoryListener(this);
	}

	// Actions ================================================================

	/**
	 * Remove any directories that don't contain any files
	 */
	public final void pruneEmptyDirectories() throws IOException {
		while (directoriesToScan.size() > 0) {
			final DirectoryObject directoryObject = (DirectoryObject)directoriesToScan.remove(0);
			pruneEmptyDirectory(directoryObject);
		}
	}

	// Utils ==================================================================

	private boolean pruneEmptyDirectory(DirectoryObject directoryObject) throws IOException {
		final ILocalFileReader localFileReader = clientEnvironment.getLocalFileReader();
		final ICvsFileSystem cvsFileSystem = clientEnvironment.getCvsFileSystem();
		final IAdminWriter adminWriter = clientEnvironment.getAdminWriter();
		final IAdminReader adminReader = clientEnvironment.getAdminReader();

		directoriesToScan.remove(directoryObject);

		if (!localFileReader.exists(directoryObject, cvsFileSystem)) {
			return true;
		}

		final List fileNames = new ArrayList();
		final List directoryNames = new ArrayList();
		localFileReader.listFilesAndDirectories(directoryObject, fileNames, directoryNames, cvsFileSystem);

		if (fileNames.size() > 0) {
			return false;
		}

		if (hasFileEntry(directoryObject, cvsFileSystem, adminReader)) {
			return false;
		}

		for (Iterator it = directoryNames.iterator(); it.hasNext();) {
			final String directoryName = (String)it.next();

			if (!pruneEmptyDirectory(DirectoryObject.createInstance(directoryObject, directoryName))) {
				return false;
			}
		}

		if (adminReader.hasCvsDirectory(directoryObject, cvsFileSystem)) {
			try {
				adminWriter.removeEntryForFile(directoryObject, cvsFileSystem);
			}
			catch (FileNotFoundException ex) {
				// ignore
				return false;
			}
			adminWriter.pruneDirectory(directoryObject, cvsFileSystem);
			return true;
		}

		return false;
	}

	private boolean hasFileEntry(DirectoryObject directoryObject, ICvsFileSystem cvsFileSystem, IAdminReader adminReader) throws IOException {
		for (Iterator it = adminReader.getEntries(directoryObject, cvsFileSystem).iterator(); it.hasNext();) {
			final Entry entry = (Entry)it.next();
			if (!entry.isDirectory()) {
				return true;
			}
		}
		return false;
	}
}
