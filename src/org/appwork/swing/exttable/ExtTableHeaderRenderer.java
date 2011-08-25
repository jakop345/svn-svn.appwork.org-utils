/**
 * Copyright (c) 2009 - 2011 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * 
 * This file is part of org.appwork.utils.swing.table
 * 
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package org.appwork.swing.exttable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import org.appwork.resources.AWUTheme;
import org.appwork.utils.logging.Log;

import sun.swing.DefaultLookup;

/**
 * @author thomas
 * 
 */
public class ExtTableHeaderRenderer extends DefaultTableCellRenderer implements javax.swing.plaf.UIResource {

    /**
     * 
     */
    private static final long  serialVersionUID = 1L;
    private final ExtColumn<?> column;
    private boolean            paintIcon;

    private Color              focusForeground;
    private Color              focusBackground;
    private final Color        foregroundC;
    private final Color        backgroundC;
    private Border             focusBorder;
    private Border             cellBorder;
    private final ImageIcon    lockedWidth;

    /**
     * @param extColumn
     * @param jTableHeader
     */
    public ExtTableHeaderRenderer(final ExtColumn<?> extColumn, final JTableHeader header) {
        this.column = extColumn;
        // this.setHorizontalTextPosition(10);
        this.lockedWidth = AWUTheme.I().getIcon("exttable/widthLocked", -1);
        try {

            try {
                this.focusForeground = DefaultLookup.getColor(this, this.ui, "TableHeader.focusCellForeground");
                this.focusBackground = DefaultLookup.getColor(this, this.ui, "TableHeader.focusCellBackground");
            } catch (final NoSuchMethodError e) {
                // DefaultLookup is sun.swing, any may not be
                // available
                // e.gh. in 1.6.0_01-b06
                this.focusForeground = (Color) UIManager.get("TableHeader.focusCellForeground", this.getLocale());
                this.focusBackground = (Color) UIManager.get("TableHeader.focusCellBackground", this.getLocale());

            }

        } catch (final Throwable e) {
            Log.exception(e);
        }
        if (this.focusForeground == null) {
            this.focusForeground = header.getForeground();

        }
        if (this.focusBackground == null) {
            this.focusBackground = header.getBackground();
        }
        this.foregroundC = header.getForeground();
        this.backgroundC = header.getBackground();

        try {
            try {

                this.focusBorder = DefaultLookup.getBorder(this, this.ui, "TableHeader.focusCellBorder");

                this.cellBorder = DefaultLookup.getBorder(this, this.ui, "TableHeader.cellBorder");

            } catch (final NoSuchMethodError e) {
                // DefaultLookup is sun.swing, any may not be available
                // e.gh. in 1.6.0_01-b06

                this.focusBorder = (Border) UIManager.get("TableHeader.focusCellBorder", this.getLocale());

                this.cellBorder = (Border) UIManager.get("TableHeader.focusCellBackground", this.getLocale());

            }
        } catch (final Throwable e) {
            Log.exception(e);
            // avoid that the block above kills edt
        }
        if (this.focusBorder == null) {
            this.focusBorder = BorderFactory.createEmptyBorder(0, 10, 0, 10);
        }
        if (this.cellBorder == null) {
            this.cellBorder = BorderFactory.createEmptyBorder(0, 10, 0, 10);
        }
        this.setFont(header.getFont());

    }

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
        this.setForeground(hasFocus ? this.focusForeground : this.foregroundC);
        this.setBackground(hasFocus ? this.focusBackground : this.backgroundC);
        // sort column is no current column
        if (this.column.getModel().getSortColumn() == null || this.column.getModel().getSortColumn() != this.column) {
            this.paintIcon = false;

        } else {
            this.paintIcon = true;

        }

        this.setText(value == null ? "" : value.toString());
        this.setBorder(hasFocus ? this.focusBorder : this.cellBorder);
        // this.setBackground(Color.RED);
        // this.setOpaque(true);
        // System.out.println(this.getPreferredSize());
        return this;
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        if (this.paintIcon) {
            final int left = 2;
            final Icon icon = this.column.getModel().getSortColumn().getSortIcon();
            if (icon != null) {
                final Graphics2D g2 = (Graphics2D) g;
                // final Composite comp = g2.getComposite();
                // g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                // 0.5f));
                icon.paintIcon(this, g2, left, (this.getHeight() - icon.getIconHeight()) / 2);
                // g2.setComposite(comp);
            }
        }
        if (!this.column.isResizable() && this.column.isPaintWidthLockIcon()) {

            // lockedWidth

            final Graphics2D g2 = (Graphics2D) g;
            // final Composite comp = g2.getComposite();
            // g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
            // 0.5f));
            this.lockedWidth.paintIcon(this, g2, this.getWidth() - this.lockedWidth.getIconWidth() - 2, (this.getHeight() - this.lockedWidth.getIconHeight()) / 2);
            // g2.setComposite(comp);
        }

    }

}