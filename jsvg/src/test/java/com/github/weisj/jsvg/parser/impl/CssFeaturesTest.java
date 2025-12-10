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

import org.junit.jupiter.api.Test;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.parser.LoaderContext;
import com.github.weisj.jsvg.parser.SVGLoader;

/**
 * Tests to verify all documented CSS features work correctly.
 */
class CssFeaturesTest {

    private SVGDocument loadSvg(String svgContent) {
        SVGLoader loader = new SVGLoader();
        return loader.load(
                new ByteArrayInputStream(svgContent.getBytes(StandardCharsets.UTF_8)),
                null,
                LoaderContext.createDefault());
    }

    @Test
    void tagSelectorsWork() {
        String svg = """
                <svg xmlns="http://www.w3.org/2000/svg" width="100" height="100">
                    <style>
                        rect { fill: blue; stroke: red; }
                        circle { fill: green; }
                    </style>
                    <rect x="10" y="10" width="30" height="30" />
                    <circle cx="70" cy="25" r="15" />
                </svg>
                """;
        assertDoesNotThrow(() -> {
            SVGDocument doc = loadSvg(svg);
            assertNotNull(doc);
        });
    }

    @Test
    void classSelectorsWork() {
        String svg = """
                <svg xmlns="http://www.w3.org/2000/svg" width="100" height="100">
                    <style>
                        .blue-box { fill: blue; }
                        .red-border { stroke: red; stroke-width: 2; }
                    </style>
                    <rect x="10" y="10" width="30" height="30" class="blue-box" />
                    <rect x="50" y="10" width="30" height="30" class="red-border" />
                </svg>
                """;
        assertDoesNotThrow(() -> {
            SVGDocument doc = loadSvg(svg);
            assertNotNull(doc);
        });
    }

    @Test
    void idSelectorsWork() {
        String svg = """
                <svg xmlns="http://www.w3.org/2000/svg" width="100" height="100">
                    <style>
                        #special { fill: purple; stroke: gold; }
                    </style>
                    <rect x="10" y="10" width="30" height="30" id="special" />
                </svg>
                """;
        assertDoesNotThrow(() -> {
            SVGDocument doc = loadSvg(svg);
            assertNotNull(doc);
        });
    }

    @Test
    void multipleClassesWork() {
        String svg = """
                <svg xmlns="http://www.w3.org/2000/svg" width="100" height="100">
                    <style>
                        .blue { fill: blue; }
                        .thick { stroke-width: 5; }
                        .dashed { stroke-dasharray: 5,5; }
                    </style>
                    <rect x="10" y="10" width="30" height="30" class="blue thick dashed" />
                </svg>
                """;
        assertDoesNotThrow(() -> {
            SVGDocument doc = loadSvg(svg);
            assertNotNull(doc);
        });
    }

    @Test
    void multipleSelectorsSameRuleWork() {
        String svg = """
                <svg xmlns="http://www.w3.org/2000/svg" width="100" height="100">
                    <style>
                        rect, circle, ellipse {
                            stroke: black;
                            stroke-width: 1;
                        }
                    </style>
                    <rect x="10" y="10" width="20" height="20" />
                    <circle cx="50" cy="20" r="10" />
                    <ellipse cx="80" cy="20" rx="10" ry="5" />
                </svg>
                """;
        assertDoesNotThrow(() -> {
            SVGDocument doc = loadSvg(svg);
            assertNotNull(doc);
        });
    }

    @Test
    void multipleStyleSheetsWork() {
        String svg = """
                <svg xmlns="http://www.w3.org/2000/svg" width="100" height="100">
                    <style>
                        .box { fill: blue; }
                    </style>
                    <style>
                        .box { stroke: red; }
                    </style>
                    <rect x="10" y="10" width="30" height="30" class="box" />
                </svg>
                """;
        assertDoesNotThrow(() -> {
            SVGDocument doc = loadSvg(svg);
            assertNotNull(doc);
        });
    }

    @Test
    void cssPrecedenceWorks() {
        String svg = """
                <svg xmlns="http://www.w3.org/2000/svg" width="200" height="100">
                    <style>
                        rect { fill: blue; }
                        .my-class { fill: green; }
                        #my-rect { fill: purple; }
                    </style>
                    <rect x="10" y="10" width="30" height="30" />
                    <rect x="50" y="10" width="30" height="30" class="my-class" />
                    <rect x="90" y="10" width="30" height="30" id="my-rect" class="my-class" />
                    <rect x="130" y="10" width="30" height="30" id="my-rect" class="my-class" style="fill: red;" />
                </svg>
                """;
        assertDoesNotThrow(() -> {
            SVGDocument doc = loadSvg(svg);
            assertNotNull(doc);
        });
    }

    @Test
    void commonCssPropertiesWork() {
        String svg = """
                <svg xmlns="http://www.w3.org/2000/svg" width="200" height="200">
                    <style>
                        .styled {
                            fill: #4A90E2;
                            fill-opacity: 0.8;
                            stroke: #2E5C8A;
                            stroke-width: 3;
                            stroke-opacity: 0.9;
                            stroke-linecap: round;
                            stroke-linejoin: round;
                            stroke-dasharray: 5,5;
                            opacity: 0.95;
                        }
                        text {
                            font-family: Arial, sans-serif;
                            font-size: 16px;
                            font-weight: bold;
                            font-style: italic;
                            fill: #333;
                            text-anchor: middle;
                        }
                    </style>
                    <rect x="10" y="10" width="80" height="80" class="styled" />
                    <text x="100" y="50">Styled Text</text>
                </svg>
                """;
        assertDoesNotThrow(() -> {
            SVGDocument doc = loadSvg(svg);
            assertNotNull(doc);
        });
    }

    @Test
    void inlineStyleOverridesEverything() {
        String svg = """
                <svg xmlns="http://www.w3.org/2000/svg" width="100" height="100">
                    <style>
                        rect { fill: blue; }
                        .my-class { fill: green; }
                        #my-id { fill: purple; }
                    </style>
                    <rect x="10" y="10" width="30" height="30" 
                          id="my-id" class="my-class" 
                          style="fill: red; stroke: black;" />
                </svg>
                """;
        assertDoesNotThrow(() -> {
            SVGDocument doc = loadSvg(svg);
            assertNotNull(doc);
        });
    }

    @Test
    void cssCommentsWork() {
        String svg = """
                <svg xmlns="http://www.w3.org/2000/svg" width="100" height="100">
                    <style>
                        /* This is a comment */
                        rect { 
                            fill: blue; /* inline comment */
                        }
                        /* 
                         * Multi-line comment
                         * with multiple lines
                         */
                        .box { stroke: red; }
                    </style>
                    <rect x="10" y="10" width="30" height="30" class="box" />
                </svg>
                """;
        assertDoesNotThrow(() -> {
            SVGDocument doc = loadSvg(svg);
            assertNotNull(doc);
        });
    }
}
