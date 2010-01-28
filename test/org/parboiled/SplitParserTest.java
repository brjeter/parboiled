/*
 * Copyright (C) 2009 Mathias Doenitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.parboiled;

import org.testng.annotations.Test;
import org.parboiled.test.AbstractTest;

public class SplitParserTest extends AbstractTest {

    public static class Parser extends BaseParser<Object> {

        final Primitives primitives = Parboiled.createParser(Primitives.class);

        public Rule clause() {
            return sequence(
                    primitives.digit(),
                    primitives.operator(),
                    primitives.digit(),
                    eoi()
            );
        }
    }

    public static class Primitives extends BaseParser<Object> {

        public Rule operator() {
            return firstOf('+', '-');
        }

        public Rule digit() {
            return charRange('0', '9');
        }

    }

    @Test
    public void test() {
        Parser parser = Parboiled.createParser(Parser.class);
        test(parser, parser.clause(), "1+5", "" +
                "[clause] '1+5'\n" +
                "    [digit] '1'\n" +
                "    [operator] '+'\n" +
                "        ['+'] '+'\n" +
                "    [digit] '5'\n" +
                "    [eoi]\n");
    }

}