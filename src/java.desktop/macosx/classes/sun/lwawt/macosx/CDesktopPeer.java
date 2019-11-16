/*
 * Copyright (c) 2011, 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package sun.lwawt.macosx;

import com.apple.eawt.AppEventListener;
import com.apple.eawt.AppReOpenedListener;
import com.apple.eawt.Application;

import javax.swing.*;
import java.awt.Desktop.Action;
import java.awt.desktop.*;
import java.awt.peer.DesktopPeer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.function.Consumer;


/**
 * Concrete implementation of the interface {@code DesktopPeer} for MacOS X
 *
 * @see DesktopPeer
 */
final public class CDesktopPeer implements DesktopPeer {

    @Override
    public boolean isSupported(Action action) {
        return true;
    }

    @Override
    public void open(File file) throws IOException {
        this.lsOpenFile(file, false);
    }

    @Override
    public void edit(File file) throws IOException {
        this.lsOpenFile(file, false);
    }

    @Override
    public void print(File file) throws IOException {
        this.lsOpenFile(file, true);
    }

    @Override
    public void mail(URI uri) throws IOException {
        this.lsOpen(uri);
    }

    @Override
    public void browse(URI uri) throws IOException {
        this.lsOpen(uri);
    }

    private static abstract class AppEventListenerDelegate<AWTListener extends SystemEventListener> implements AppEventListener {
        protected final AWTListener myDelegate;

        protected AppEventListenerDelegate(AWTListener delegate) {
            this.myDelegate = delegate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            AppEventListenerDelegate<?> that = (AppEventListenerDelegate<?>) o;
            return Objects.equals(myDelegate, that.myDelegate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(myDelegate);
        }
    }

    private static class _AppForegroundListener extends AppEventListenerDelegate<AppForegroundListener> implements com.apple.eawt.AppForegroundListener {
        protected _AppForegroundListener(AppForegroundListener myDelegate) {
            super(myDelegate);
        }

        @Override
        public void appRaisedToForeground(com.apple.eawt.AppEvent.AppForegroundEvent e) {
            myDelegate.appRaisedToForeground(new AppForegroundEvent());
        }

        @Override
        public void appMovedToBackground(com.apple.eawt.AppEvent.AppForegroundEvent e) {
            myDelegate.appMovedToBackground(new AppForegroundEvent());
        }
    }

    private static class _AppHiddenListener extends AppEventListenerDelegate<AppHiddenListener> implements com.apple.eawt.AppHiddenListener {
        protected _AppHiddenListener(AppHiddenListener delegate) {
            super(delegate);
        }

        @Override
        public void appHidden(com.apple.eawt.AppEvent.AppHiddenEvent e) {
            myDelegate.appHidden(new AppHiddenEvent());
        }

        @Override
        public void appUnhidden(com.apple.eawt.AppEvent.AppHiddenEvent e) {
            myDelegate.appUnhidden(new AppHiddenEvent());
        }
    }

    private static class _AppReOpenedListener extends AppEventListenerDelegate<AppReopenedListener> implements AppReOpenedListener {

        _AppReOpenedListener(AppReopenedListener delegate) {
            super(delegate);
        }

        @Override
        public void appReOpened(com.apple.eawt.AppEvent.AppReOpenedEvent e) {
            myDelegate.appReopened(new AppReopenedEvent());
        }
    }

    private static class _ScreenSleepListener extends AppEventListenerDelegate<ScreenSleepListener> implements com.apple.eawt.ScreenSleepListener {

        _ScreenSleepListener(ScreenSleepListener delegate) {
            super(delegate);
        }

        @Override
        public void screenAboutToSleep(com.apple.eawt.AppEvent.ScreenSleepEvent e) {
            myDelegate.screenAboutToSleep(new ScreenSleepEvent());
        }

        @Override
        public void screenAwoke(com.apple.eawt.AppEvent.ScreenSleepEvent e) {
            myDelegate.screenAwoke(new ScreenSleepEvent());
        }
    }

    private static class _SystemSleepListener extends AppEventListenerDelegate<SystemSleepListener> implements com.apple.eawt.SystemSleepListener {

        _SystemSleepListener(SystemSleepListener delegate) {
            super(delegate);
        }

        @Override
        public void systemAboutToSleep(com.apple.eawt.AppEvent.SystemSleepEvent e) {
            myDelegate.systemAboutToSleep(new SystemSleepEvent());
        }

        @Override
        public void systemAwoke(com.apple.eawt.AppEvent.SystemSleepEvent e) {
            myDelegate.systemAwoke(new SystemSleepEvent());
        }
    }

    private static class _UserSessionListener extends AppEventListenerDelegate<UserSessionListener> implements com.apple.eawt.UserSessionListener {

        _UserSessionListener(UserSessionListener delegate) {
            super(delegate);
        }

        @Override
        public void userSessionDeactivated(com.apple.eawt.AppEvent.UserSessionEvent e) {
            myDelegate.userSessionDeactivated(new UserSessionEvent(UserSessionEvent.Reason.UNSPECIFIED));
        }

        @Override
        public void userSessionActivated(com.apple.eawt.AppEvent.UserSessionEvent e) {
            myDelegate.userSessionActivated(new UserSessionEvent(UserSessionEvent.Reason.UNSPECIFIED));
        }
    }

    @Override
    public void addAppEventListener(SystemEventListener listener) {
        wrapListener(listener, Application.getApplication()::addAppEventListener);
    }

    @Override
    public void removeAppEventListener(SystemEventListener listener) {
        wrapListener(listener, Application.getApplication()::removeAppEventListener);
    }

    private <AWTEventListener extends SystemEventListener> void wrapListener(AWTEventListener listener, Consumer<AppEventListener> consumer) {
        if (listener instanceof AppForegroundListener) {
            consumer.accept(new _AppForegroundListener((AppForegroundListener) listener));
        }
        else if (listener instanceof AppHiddenListener) {
            consumer.accept(new _AppHiddenListener((AppHiddenListener) listener));
        }
        else if (listener instanceof AppReopenedListener) {
            consumer.accept(new _AppReOpenedListener((AppReopenedListener) listener));
        }
        else if (listener instanceof ScreenSleepListener) {
            consumer.accept(new _ScreenSleepListener((ScreenSleepListener) listener));
        }
        else if (listener instanceof SystemSleepListener) {
            consumer.accept(new _SystemSleepListener((SystemSleepListener) listener));
        }
        else if (listener instanceof UserSessionListener) {
            consumer.accept(new _UserSessionListener((UserSessionListener) listener));
        }
    }

    @Override
    public void setAboutHandler(AboutHandler aboutHandler) {
        Application.getApplication().setAboutHandler(e -> aboutHandler.handleAbout(new AboutEvent()));
    }

    @Override
    public void setPreferencesHandler(PreferencesHandler preferencesHandler) {
        Application.getApplication().setPreferencesHandler(e -> preferencesHandler.handlePreferences(new PreferencesEvent()));
    }

    @Override
    public void setOpenFileHandler(OpenFilesHandler openFileHandler) {
        Application.getApplication().setOpenFileHandler(e -> openFileHandler.openFiles(new OpenFilesEvent(e.getFiles(), e.getSearchTerm())));
    }

    @Override
    public void setPrintFileHandler(PrintFilesHandler printFileHandler) {
        Application.getApplication().setPrintFileHandler(e -> printFileHandler.printFiles(new PrintFilesEvent(e.getFiles())));
    }

    @Override
    public void setOpenURIHandler(OpenURIHandler openURIHandler) {
        Application.getApplication().setOpenURIHandler(e -> openURIHandler.openURI(new OpenURIEvent(e.getURI())));
    }

    @Override
    public void setQuitHandler(QuitHandler quitHandler) {
        Application.getApplication().setQuitHandler((e, response) -> quitHandler.handleQuitRequestWith(new QuitEvent(), new QuitResponse() {
            @Override
            public void performQuit() {
                response.performQuit();
            }

            @Override
            public void cancelQuit() {
                response.cancelQuit();
            }
        }));
    }

    @Override
    public void setQuitStrategy(QuitStrategy strategy) {
        com.apple.eawt.QuitStrategy eawtStrategy;

        switch (strategy) {
            case NORMAL_EXIT:
                eawtStrategy = com.apple.eawt.QuitStrategy.SYSTEM_EXIT_0;
                break;
            case CLOSE_ALL_WINDOWS:
                eawtStrategy = com.apple.eawt.QuitStrategy.CLOSE_ALL_WINDOWS;
                break;
            default:
                throw new UnsupportedOperationException("unsupported value " + strategy);
        }
        Application.getApplication().setQuitStrategy(eawtStrategy);
    }

    @Override
    public void enableSuddenTermination() {
        Application.getApplication().enableSuddenTermination();
    }

    @Override
    public void disableSuddenTermination() {
        Application.getApplication().disableSuddenTermination();
    }

    @Override
    public void requestForeground(boolean allWindows) {
        Application.getApplication().requestForeground(allWindows);
    }

    @Override
    public void openHelpViewer() {
        Application.getApplication().openHelpViewer();
    }

    @Override
    public void setDefaultMenuBar(JMenuBar menuBar) {
        Application.getApplication().setDefaultMenuBar(menuBar);
    }

    @Override
    public boolean browseFileDirectory(File file) {
        try {
            return com.apple.eio.FileManager.revealInFinder(file);
        }
        catch (FileNotFoundException ex) {
            return false; //handled in java.awt.Desktop
        }
    }

    @Override
    public boolean moveToTrash(File file) {
        try {
            return com.apple.eio.FileManager.moveToTrash(file);
        }
        catch (FileNotFoundException ex) {
            return false; //handled in java.awt.Desktop
        }
    }

    private void lsOpen(URI uri) throws IOException {
        int status = _lsOpenURI(uri.toString());

        if (status != 0 /* noErr */) {
            throw new IOException("Failed to mail or browse " + uri + ". Error code: " + status);
        }
    }

    private void lsOpenFile(File file, boolean print) throws IOException {
        int status = _lsOpenFile(file.getCanonicalPath(), print);

        if (status != 0 /* noErr */) {
            throw new IOException("Failed to open, edit or print " + file + ". Error code: " + status);
        }
    }

    private static native int _lsOpenURI(String uri);

    private static native int _lsOpenFile(String path, boolean print);

}
