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

import javax.swing.*;

import org.jetbrains.annotations.NotNull;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.parser.LoaderContext;
import com.github.weisj.jsvg.parser.SVGLoader;

/**
 * Example demonstrating CSS styling support in JSVG.
 * 
 * This example shows how to use:
 * - Tag selectors (rect, circle, text)
 * - Class selectors (.class-name)
 * - ID selectors (#id)
 * - Multiple classes
 * - CSS precedence
 * - Common CSS properties (fill, stroke, stroke-width, font-size, etc.)
 */
public class CssExampleApp {

    private static final String SVG_WITH_CSS = """
            <svg xmlns="http://www.w3.org/2000/svg" width="500" height="400" viewBox="0 0 500 400">
                <style>
                    /* Tag selector - applies to all rectangles */
                    rect {
                        fill: lightblue;
                        stroke: navy;
                        stroke-width: 2;
                    }
                    
                    /* Class selector */
                    .highlighted {
                        fill: yellow;
                        stroke: orange;
                        stroke-width: 3;
                    }
                    
                    /* Another class selector */
                    .dashed {
                        stroke-dasharray: 5,5;
                    }
                    
                    /* ID selector - highest specificity */
                    #special-box {
                        fill: lightgreen;
                        stroke: darkgreen;
                        stroke-width: 4;
                    }
                    
                    /* Circle styling */
                    circle {
                        fill: pink;
                        stroke: red;
                        stroke-width: 2;
                    }
                    
                    /* Text styling */
                    text {
                        font-family: Arial, sans-serif;
                        font-size: 14px;
                        fill: #333;
                        text-anchor: middle;
                    }
                    
                    .title {
                        font-size: 20px;
                        font-weight: bold;
                        fill: #000;
                    }
                </style>
                
                <text x="250" y="30" class="title">CSS Styling Demo</text>
                
                <!-- Tag selector applies -->
                <rect x="50" y="60" width="80" height="60" />
                <text x="90" y="140">Default rect</text>
                
                <!-- Class selector overrides tag selector -->
                <rect x="160" y="60" width="80" height="60" class="highlighted" />
                <text x="200" y="140">Class: highlighted</text>
                
                <!-- Multiple classes -->
                <rect x="270" y="60" width="80" height="60" class="highlighted dashed" />
                <text x="310" y="140">Multiple classes</text>
                
                <!-- ID selector has highest precedence -->
                <rect x="380" y="60" width="80" height="60" id="special-box" class="highlighted" />
                <text x="420" y="140">ID: special-box</text>
                
                <!-- Inline style has ultimate priority -->
                <rect x="50" y="180" width="80" height="60" class="highlighted" style="fill: purple; stroke: black;" />
                <text x="90" y="260">Inline style</text>
                
                <!-- Circle with CSS styling -->
                <circle cx="200" cy="210" r="30" />
                <text x="200" y="270">circle tag</text>
                
                <!-- Circle with class -->
                <circle cx="310" cy="210" r="30" class="highlighted" />
                <text x="310" y="270">circle + class</text>
                
                <!-- Circle with ID -->
                <circle cx="420" cy="210" r="30" id="special-circle" style="fill: cyan; stroke: blue;" />
                <text x="420" y="270">circle + inline</text>
                
                <text x="250" y="320" font-size="12">
                    CSS Precedence (low to high): tag selector &lt; class &lt; id &lt; inline style
                </text>
                
                <text x="250" y="350" font-size="12">
                    All common SVG properties supported: fill, stroke, stroke-width, opacity, etc.
                </text>
            </svg>
            """;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                SVGLoader loader = new SVGLoader();
                SVGDocument document = loader.load(
                        new ByteArrayInputStream(SVG_WITH_CSS.getBytes(StandardCharsets.UTF_8)),
                        null,
                        LoaderContext.createDefault());

                JFrame frame = new JFrame("JSVG CSS Example");
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setContentPane(new SVGPanel(document));
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

    static class SVGPanel extends JPanel {
        private final @NotNull SVGDocument document;

        SVGPanel(@NotNull SVGDocument document) {
            this.document = document;
            setPreferredSize(new Dimension(
                    (int) document.size().width,
                    (int) document.size().height));
            setBackground(Color.WHITE);
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
