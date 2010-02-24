/**
 * Copyright (c) 2009 - 2010 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * 
 * This file is part of org.appwork.utils.ImageProvider
 * 
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package org.appwork.utils.ImageProvider;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.appwork.utils.Application;
import org.appwork.utils.logging.Log;

import sun.awt.image.ToolkitImage;

/**
 * This class grants easy access to images stored in APPROOT/images/**.png
 * 
 * @author $Author: unknown$
 * 
 */
public class ImageProvider {
    /**
     * Hashcashmap to cache images.
     */
    private static HashMap<String, BufferedImage> IMAGE_CACHE = new HashMap<String, BufferedImage>();
    private static HashMap<String, ImageIcon> IMAGEICON_CACHE = new HashMap<String, ImageIcon>();

    private static Object LOCK = new Object();
    // stringbuilder die concat strings fast
    private static StringBuilder SB = new StringBuilder();

    /**
     * 
     * @param name
     *            to the png file
     * @return
     * @throws IOException
     */
    public static Image getBufferedImage(String name) throws IOException {
        synchronized (LOCK) {
            if (IMAGE_CACHE.containsKey(name)) { return IMAGE_CACHE.get(name); }

            File absolutePath = Application.getRessource("images/" + name + ".png");
            try {
                BufferedImage image = ImageIO.read(absolutePath);

                IMAGE_CACHE.put(name, image);
                return image;
            } catch (IOException e) {
                Log.L.severe("Could not Init Image: " + absolutePath.getAbsolutePath());
                throw e;
            }
        }
    }

    /**
     * Loads the image, scales it to the desired size and returns it as an
     * imageicon
     * 
     * @param name
     * @param width
     * @param height
     * @return
     * @throws IOException
     */
    public static ImageIcon getImageIcon(String name, int width, int height) throws IOException {
        synchronized (LOCK) {
            SB.delete(0, SB.capacity());
            SB.append(name);
            SB.append('_');
            SB.append(width);
            SB.append('_');
            SB.append(height);
            String key;
            if (IMAGEICON_CACHE.containsKey(key = SB.toString())) { return IMAGEICON_CACHE.get(key); }
            Image image = getBufferedImage(name);
            double faktor = Math.min((double) image.getWidth(null) / width, (double) image.getHeight(null) / height);
            width = (int) (image.getWidth(null) / faktor);
            height = (int) (image.getHeight(null) / faktor);
            ImageIcon imageicon = new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
            IMAGEICON_CACHE.put(key, imageicon);
            return imageicon;
        }
    }

    /**
     * Converts an Icon to an Imageicon.
     * 
     * @param icon
     * @return
     */
    public static ImageIcon toImageIcon(Icon icon) {

        if (icon == null) return null;
        if (icon instanceof ImageIcon) {
            return ((ImageIcon) icon);
        } else {
            int w = icon.getIconWidth();
            int h = icon.getIconHeight();
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gd.getDefaultConfiguration();
            BufferedImage image = gc.createCompatibleImage(w, h, Transparency.BITMASK);
            Graphics2D g = image.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();
            return new ImageIcon(image);

        }

    }

    /**
     * Scales a buffered Image to the given size. This method is NOT cached. so
     * take care to cache it externally if you use it frequently
     * 
     * @param img
     * @param width
     * @param height
     * @return
     */
    public static Image scaleBufferedImage(BufferedImage img, int width, int height) {
        if (img == null) return null;
        final double faktor = Math.min((double) img.getWidth() / width, (double) img.getHeight() / height);
        width = (int) (img.getWidth() / faktor);
        height = (int) (img.getHeight() / faktor);
        if (faktor == 1.0) return img;
        return img.getScaledInstance(width, height, Image.SCALE_SMOOTH);

    }

    /**
     * Scales an imageicon to w x h.<br>
     * like {@link #scaleBufferedImage(BufferedImage, int, int)}, this Function
     * is NOT cached. USe an external cache if you use it frequently
     * 
     * @param img
     * @param w
     * @param h
     * @return
     */
    public static ImageIcon scaleImageIcon(ImageIcon img, int w, int h) {
        // already has the desired size?
        if (img.getIconHeight() == h && img.getIconWidth() == w) return img;

        BufferedImage dest;

        if (img.getImage() instanceof ToolkitImage) {
            dest = new BufferedImage(w, h, BufferedImage.TRANSLUCENT);
            Graphics2D g2 = dest.createGraphics();
            g2.drawImage(img.getImage(), 0, 0, null);
            g2.dispose();
        } else {
            dest = (BufferedImage) img.getImage();
        }

        return new ImageIcon(scaleBufferedImage(dest, w, h));
    }

    /**
     * @param bufferedImage
     * @return
     */
    public static BufferedImage convertToGrayScale(BufferedImage bufferedImage) {
        BufferedImage dest = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2 = dest.createGraphics();
        g2.drawImage(bufferedImage, 0, 0, null);
        g2.dispose();
        return dest;

    }

    /**
     * @param image
     * @param imageIcon
     * @param i
     * @param j
     */
    public static BufferedImage merge(Image image, Image b, int xoffset, int yoffset) {
        int width = Math.max(image.getWidth(null), xoffset + b.getWidth(null));
        int height = Math.max(image.getHeight(null), yoffset + b.getHeight(null));
        BufferedImage dest = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g2 = dest.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.drawImage(b, xoffset, yoffset, null);
        g2.dispose();
        return dest;
    }
}
