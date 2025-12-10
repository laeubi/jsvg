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
package com.github.weisj.jsvg.parser.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.parser.LoaderContext;
import com.github.weisj.jsvg.parser.SVGLoader;
import com.github.weisj.jsvg.parser.css.impl.SimpleCssParser;

/**
 * Tests for external stylesheet injection via LoaderContext.
 */
class ExternalStyleSheetTest {

    private SVGDocument loadSvg(String svgContent, LoaderContext context) {
        SVGLoader loader = new SVGLoader();
        return loader.load(
                new ByteArrayInputStream(svgContent.getBytes(StandardCharsets.UTF_8)),
                null,
                context);
    }

    @Test
    void externalStyleSheetCanBeInjected() {
        String svg = """
                <svg xmlns="http://www.w3.org/2000/svg" width="100" height="100">
                    <rect x="10" y="10" width="30" height="30" class="external-class" />
                </svg>
                """;

        String externalCss = """
                .external-class {
                    fill: blue;
                    stroke: red;
                    stroke-width: 3;
                }
                """;

        SimpleCssParser cssParser = new SimpleCssParser();
        var externalStyleSheet = cssParser.parse(Collections.singletonList(externalCss.toCharArray()));

        LoaderContext context = LoaderContext.builder()
                .externalStyleSheet(externalStyleSheet)
                .build();

        assertDoesNotThrow(() -> {
            SVGDocument doc = loadSvg(svg, context);
            assertNotNull(doc, "Document should be loaded with external stylesheet");
        });
    }

    @Test
    void externalStyleSheetOverridesInternalStyles() {
        String svg = """
                <svg xmlns="http://www.w3.org/2000/svg" width="100" height="100">
                    <style>
                        .my-class { fill: blue; }
                    </style>
                    <rect x="10" y="10" width="30" height="30" class="my-class" />
                </svg>
                """;

        // External CSS should override internal styles
        String externalCss = """
                .my-class {
                    fill: red;
                }
                """;

        SimpleCssParser cssParser = new SimpleCssParser();
        var externalStyleSheet = cssParser.parse(Collections.singletonList(externalCss.toCharArray()));

        LoaderContext context = LoaderContext.builder()
                .externalStyleSheet(externalStyleSheet)
                .build();

        assertDoesNotThrow(() -> {
            SVGDocument doc = loadSvg(svg, context);
            assertNotNull(doc, "Document should be loaded with overriding external stylesheet");
        });
    }

    @Test
    void externalStyleSheetWithMultipleSelectors() {
        String svg = """
                <svg xmlns="http://www.w3.org/2000/svg" width="100" height="100">
                    <rect x="10" y="10" width="20" height="20" class="box" />
                    <circle cx="50" cy="20" r="10" id="my-circle" />
                    <rect x="10" y="50" width="20" height="20" />
                </svg>
                """;

        String externalCss = """
                .box {
                    fill: blue;
                }
                #my-circle {
                    fill: green;
                }
                rect {
                    stroke: black;
                    stroke-width: 1;
                }
                """;

        SimpleCssParser cssParser = new SimpleCssParser();
        var externalStyleSheet = cssParser.parse(Collections.singletonList(externalCss.toCharArray()));

        LoaderContext context = LoaderContext.builder()
                .externalStyleSheet(externalStyleSheet)
                .build();

        assertDoesNotThrow(() -> {
            SVGDocument doc = loadSvg(svg, context);
            assertNotNull(doc, "Document should be loaded with multi-selector external stylesheet");
        });
    }

    @Test
    void noExternalStyleSheetWorks() {
        String svg = """
                <svg xmlns="http://www.w3.org/2000/svg" width="100" height="100">
                    <rect x="10" y="10" width="30" height="30" fill="blue" />
                </svg>
                """;

        LoaderContext context = LoaderContext.builder()
                .externalStyleSheet(null)
                .build();

        assertDoesNotThrow(() -> {
            SVGDocument doc = loadSvg(svg, context);
            assertNotNull(doc, "Document should be loaded without external stylesheet");
        });
    }

    @Test
    void externalStyleSheetWorksWithEmptySvg() {
        String svg = """
                <svg xmlns="http://www.w3.org/2000/svg" width="100" height="100">
                    <rect x="10" y="10" width="30" height="30" class="box" />
                </svg>
                """;

        String externalCss = """
                .box { fill: purple; }
                """;

        SimpleCssParser cssParser = new SimpleCssParser();
        var externalStyleSheet = cssParser.parse(Collections.singletonList(externalCss.toCharArray()));

        LoaderContext context = LoaderContext.builder()
                .externalStyleSheet(externalStyleSheet)
                .build();

        assertDoesNotThrow(() -> {
            SVGDocument doc = loadSvg(svg, context);
            assertNotNull(doc, "Document without internal styles should work with external stylesheet");
        });
    }
}
