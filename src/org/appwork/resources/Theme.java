/**
 * Copyright (c) 2009 - 2011 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * 
 * This file is part of org.appwork.resources
 * 
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package org.appwork.resources;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.swing.ImageIcon;

import org.appwork.storage.config.MinTimeWeakReference;
import org.appwork.utils.Application;
import org.appwork.utils.IO;
import org.appwork.utils.images.IconIO;
import org.appwork.utils.logging.Log;
import org.appwork.utils.swing.dialog.Dialog;
import org.appwork.utils.swing.dialog.DialogCanceledException;
import org.appwork.utils.swing.dialog.DialogClosedException;

/**
 * 
 * @author thomas
 * 
 */
public abstract class Theme {
    private String                                                 path;

    // private final HashMap<String, MinTimeWeakReference<BufferedImage>>
    // imageCache = new HashMap<String, MinTimeWeakReference<BufferedImage>>();

    private final HashMap<String, MinTimeWeakReference<ImageIcon>> imageIconCache = new HashMap<String, MinTimeWeakReference<ImageIcon>>();

    private long                                                   cacheLifetime  = 20000l;

    private String                                                 theme;

    public Theme() {
        this.setTheme("standard");

    }

    public Theme(final String theme) {
        this.setTheme(theme);

    }

    /**
     * @param relativePath
     * @param size
     * @return
     */
    private String getCacheKey(final Object... objects) {
        if (objects.length == 1) { return objects.toString(); }
        final StringBuilder sb = new StringBuilder();
        for (final Object o : objects) {
            if (sb.length() > 0) {
                sb.append("_");
            }
            sb.append(o);
        }
        return sb.toString();
    }

    public long getCacheLifetime() {
        return this.cacheLifetime;
    }

    private String getDefaultPath(final String pre, final String path, final String ext) {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.path);
        sb.append(pre);
        sb.append(path);
        sb.append(ext);
        return sb.toString();
    }

    public ImageIcon getIcon(final String relativePath, final int size) {
        ImageIcon ret = null;
        final String key = this.getCacheKey(relativePath, size);
        final MinTimeWeakReference<ImageIcon> cache = this.imageIconCache.get(key);
        if (cache != null) {
            ret = cache.get();
        }
        if (ret == null) {
            final URL url = this.getURL("images/", relativePath, ".png");

            ret = IconIO.getImageIcon(url, size);
            if (url == null) {

                try {
                    Dialog.getInstance().showConfirmDialog(0, "Icon Missing", "Please add the\r\n" + this.getPath("images/", relativePath, ".png") + " to the classpath", ret, null, null);
                } catch (final DialogClosedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (final DialogCanceledException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
            this.imageIconCache.put(key, new MinTimeWeakReference<ImageIcon>(ret, this.cacheLifetime, key));
        }
        return ret;

    }

    public Image getImage(final String relativePath, final int size) {
        return this.getIcon(relativePath, size).getImage();
    }

    /**
     * @return
     */
    protected abstract String getNameSpace();

    private String getPath(final String pre, final String path, final String ext) {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.path);
        sb.append(pre);
        sb.append(path);
        sb.append(ext);
        return sb.toString();
    }

    public String getText(final String string) {
        final URL url = this.getURL("", string, "");
        if (url == null) { return null; }
        try {
            return IO.readURLToString(url);
        } catch (final IOException e) {
            Log.exception(e);
        }
        return null;
    }

    public String getTheme() {
        return this.theme;
    }

    /**
     * returns a valid resourceurl or null if no resource is available.
     * 
     * @param pre
     *            subfolder. for exmaple "images/"
     * @param relativePath
     *            relative resourcepath
     * @param ext
     *            resource extension
     * @return
     */
    private URL getURL(final String pre, final String relativePath, final String ext) {
        final String path = this.getPath(pre, relativePath, ext);
        try {

            // first lookup in home dir. .jd_home or installdirectory
            final File file = Application.getResource(path);
            if (file.exists()) { return file.toURI().toURL(); }
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        }
        // afterwards, we lookup in classpath. jar or bin folders
        URL url = Theme.class.getResource(path);
        if (url == null) {
            url = Theme.class.getResource(this.getDefaultPath(pre, relativePath, ext));
        }
        return url;
    }

    public void setCacheLifetime(final long cacheLifetime) {
        this.cacheLifetime = cacheLifetime;
    }

    /**
     * @param theme
     */
    public void setTheme(final String theme) {
        this.theme = theme;
        this.path = "/themes/" + theme + "/" + this.getNameSpace();
    }

}
