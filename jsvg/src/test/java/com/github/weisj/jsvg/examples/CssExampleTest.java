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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.parser.LoaderContext;
import com.github.weisj.jsvg.parser.SVGLoader;

class CssExampleTest {

    @Test
    void cssExampleLoadsCorrectly() {
        String svgWithCss = """
                <svg xmlns="http://www.w3.org/2000/svg" width="200" height="200">
                    <style>
                        rect { fill: blue; }
                        .red { fill: red; }
                        #green { fill: green; }
                    </style>
                    <rect x="10" y="10" width="50" height="50" />
                    <rect x="70" y="10" width="50" height="50" class="red" />
                    <rect x="130" y="10" width="50" height="50" id="green" />
                </svg>
                """;

        SVGLoader loader = new SVGLoader();
        SVGDocument document = loader.load(
                new ByteArrayInputStream(svgWithCss.getBytes(StandardCharsets.UTF_8)),
                null,
                LoaderContext.createDefault());

        assertNotNull(document, "Document should be loaded");
        assertNotNull(document.size(), "Document should have a size");
    }
}
