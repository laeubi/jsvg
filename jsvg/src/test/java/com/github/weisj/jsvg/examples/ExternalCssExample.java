/*
 * MIT License
 *
 * Copyright (c) 2025 Jannis Weis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package com.github.weisj.jsvg.examples;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import javax.swing.*;

import org.jetbrains.annotations.NotNull;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.parser.LoaderContext;
import com.github.weisj.jsvg.parser.SVGLoader;
import com.github.weisj.jsvg.parser.css.impl.SimpleCssParser;

/**
 * Example demonstrating external CSS injection via LoaderContext.
 * 
 * This shows how to apply custom CSS styles to SVG documents at load time
 * without modifying the SVG files themselves. This is useful when:
 * - Multiple SVGs share the same style classes
 * - You want to allow user customization of SVG styling
 * - You need to apply theme-specific styles to SVGs
 */
public class ExternalCssExample {

    // Sample SVG with class names but no internal styles
    private static final String SVG_CONTENT = """
            <svg xmlns="http://www.w3.org/2000/svg" width="400" height="300" viewBox="0 0 400 300">
                <text x="200" y="30" text-anchor="middle" class="title">External CSS Demo</text>
                
                <rect x="50" y="60" width="80" height="60" class="box primary" />
                <text x="90" y="140" text-anchor="middle" class="label">Box 1</text>
                
                <rect x="160" y="60" width="80" height="60" class="box secondary" />
                <text x="200" y="140" text-anchor="middle" class="label">Box 2</text>
                
                <rect x="270" y="60" width="80" height="60" class="box accent" />
                <text x="310" y="140" text-anchor="middle" class="label">Box 3</text>
                
                <circle cx="90" cy="200" r="30" class="shape primary" />
                <circle cx="200" cy="200" r="30" class="shape secondary" />
                <circle cx="310" cy="200" r="30" class="shape accent" />
                
                <text x="200" y="270" text-anchor="middle" font-size="12">
                    Styles applied via external CSS
                </text>
            </svg>
            """;

    // User-defined external CSS that will be applied to the SVG
    private static final String EXTERNAL_CSS = """
            .title {
                font-size: 24px;
                font-weight: bold;
                fill: #2C3E50;
            }
            
            .box {
                stroke: #34495E;
                stroke-width: 2;
            }
            
            .shape {
                stroke: #34495E;
                stroke-width: 2;
            }
            
            .primary {
                fill: #3498DB;
            }
            
            .secondary {
                fill: #2ECC71;
            }
            
            .accent {
                fill: #E74C3C;
            }
            
            .label {
                font-size: 14px;
                fill: #7F8C8D;
            }
            """;

    // Alternative theme CSS
    private static final String DARK_THEME_CSS = """
            .title {
                font-size: 24px;
                font-weight: bold;
                fill: #ECF0F1;
            }
            
            .box {
                stroke: #BDC3C7;
                stroke-width: 2;
            }
            
            .shape {
                stroke: #BDC3C7;
                stroke-width: 2;
            }
            
            .primary {
                fill: #9B59B6;
            }
            
            .secondary {
                fill: #E67E22;
            }
            
            .accent {
                fill: #F39C12;
            }
            
            .label {
                font-size: 14px;
                fill: #95A5A6;
            }
            """;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                JFrame frame = new JFrame("External CSS Example");
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());

                // Create two panels with different themes
                SVGDocument lightDoc = loadSvgWithCss(EXTERNAL_CSS);
                SVGDocument darkDoc = loadSvgWithCss(DARK_THEME_CSS);

                JPanel container = new JPanel(new GridLayout(1, 2, 10, 10));
                container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                JPanel lightPanel = new JPanel(new BorderLayout());
                lightPanel.setBackground(Color.WHITE);
                lightPanel.add(new JLabel("Light Theme", SwingConstants.CENTER), BorderLayout.NORTH);
                lightPanel.add(new SVGPanel(lightDoc), BorderLayout.CENTER);

                JPanel darkPanel = new JPanel(new BorderLayout());
                darkPanel.setBackground(new Color(44, 62, 80));
                darkPanel.add(new JLabel("Dark Theme", SwingConstants.CENTER), BorderLayout.NORTH);
                darkPanel.add(new SVGPanel(darkDoc), BorderLayout.CENTER);

                container.add(lightPanel);
                container.add(darkPanel);

                frame.add(container, BorderLayout.CENTER);

                JLabel info = new JLabel(
                        "<html><center>Same SVG content with different external CSS styles applied<br>" +
                                "No modification to SVG files needed!</center></html>",
                        SwingConstants.CENTER);
                info.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                frame.add(info, BorderLayout.SOUTH);

                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error loading SVG: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static SVGDocument loadSvgWithCss(String cssContent) {
        SVGLoader loader = new SVGLoader();
        SimpleCssParser cssParser = new SimpleCssParser();
        var externalStyleSheet = cssParser.parse(Collections.singletonList(cssContent.toCharArray()));

        LoaderContext context = LoaderContext.builder()
                .externalStyleSheet(externalStyleSheet)
                .build();

        return loader.load(
                new ByteArrayInputStream(SVG_CONTENT.getBytes(StandardCharsets.UTF_8)),
                null,
                context);
    }

    static class SVGPanel extends JPanel {
        private final @NotNull SVGDocument document;

        SVGPanel(@NotNull SVGDocument document) {
            this.document = document;
            setPreferredSize(new Dimension(
                    (int) document.size().width,
                    (int) document.size().height));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            document.render(this, g2d);
        }
    }
}
