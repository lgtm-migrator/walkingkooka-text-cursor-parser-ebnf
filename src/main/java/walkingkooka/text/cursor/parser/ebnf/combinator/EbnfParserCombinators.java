/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.text.cursor.parser.ebnf.combinator;

import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfGrammarParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierName;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfRuleParserToken;

import java.util.Map;
import java.util.Objects;

public final class EbnfParserCombinators implements PublicStaticHelper {

    /**
     * Accepts a {@link EbnfGrammarParserToken} and returns a {@link Map} holding all identifiers(the names) to a parser.
     * The {@link Map} will be used as defaults, any new definitions in the grammar will replace those in the map.
     */
    public static Map<EbnfIdentifierName, Parser<ParserContext>> transform(final EbnfGrammarParserToken grammar,
                                                                           final Map<EbnfIdentifierName, Parser<ParserContext>> identifierToParser,
                                                                           final EbnfParserCombinatorSyntaxTreeTransformer transformer) {
        Objects.requireNonNull(grammar, "grammar");
        Objects.requireNonNull(identifierToParser, "identifierToParser");
        Objects.requireNonNull(transformer, "syntaxTreeTransformer");

        return transform0(EbnfParserCombinatorParserTextCleaningEbnfParserTokenVisitor.clean(grammar),
                identifierToParser,
                transformer);
    }

    private static Map<EbnfIdentifierName, Parser<ParserContext>> transform0(final EbnfGrammarParserToken grammar,
                                                                             final Map<EbnfIdentifierName, Parser<ParserContext>> identifierToParser,
                                                                             final EbnfParserCombinatorSyntaxTreeTransformer transformer) {

        grammar.checkIdentifiers(identifierToParser.keySet());
        preloadProxies(grammar, identifierToParser);
        EbnfParserCombinatorParserCompilingEbnfParserTokenVisitor.compile(identifierToParser,
                transformer,
                grammar);
        return EbnfParserCombinatorsTransformMap.with(identifierToParser);
    }

    /**
     * Fill the {@link Map identifierToParser} with proxies, allowing forward references in the grammar.
     */
    private static void preloadProxies(final EbnfGrammarParserToken grammar, final Map<EbnfIdentifierName, Parser<ParserContext>> identifierToParser) {
        grammar.value()
                .stream()
                .map(EbnfParserCombinators::toEbnfParserToken)
                .filter(EbnfParserToken::isRule)
                .forEach(t -> addProxy(t.cast(EbnfRuleParserToken.class), identifierToParser));
    }

    private static EbnfParserToken toEbnfParserToken(final ParserToken token) {
        return token.cast(EbnfParserToken.class);
    }

    private static void addProxy(final EbnfRuleParserToken rule, final Map<EbnfIdentifierName, Parser<ParserContext>> identifierToParser) {
        final EbnfIdentifierParserToken identifierParserToken = rule.identifier();
        final EbnfIdentifierName identifierName = identifierParserToken.value();

        final Object existing = identifierToParser.get(identifierName);
        if (null != existing) {
            failDuplicateRule(rule, existing);
        }

        identifierToParser.put(identifierName, EbnfParserCombinatorProxyParser.with(identifierParserToken));
    }

    /**
     * Reports an attempt to override a predefined rule to parser or a second rule with the same name.
     */
    private static void failDuplicateRule(final EbnfRuleParserToken duplicate, final Object existing) {
        throw new EbnfParserCombinatorDuplicateRuleException("Rule with identifier " + duplicate.identifier().value() + " already exists=" + existing, duplicate);
    }

    /**
     * Stop creation
     */
    private EbnfParserCombinators() {
        throw new UnsupportedOperationException();
    }
}
