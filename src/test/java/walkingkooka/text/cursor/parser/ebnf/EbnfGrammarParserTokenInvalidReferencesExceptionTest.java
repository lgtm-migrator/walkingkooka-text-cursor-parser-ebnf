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

package walkingkooka.text.cursor.parser.ebnf;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.ThrowableTesting;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class EbnfGrammarParserTokenInvalidReferencesExceptionTest implements ClassTesting2<EbnfGrammarParserTokenInvalidReferencesException>,
        ThrowableTesting,
        ToStringTesting<EbnfGrammarParserTokenInvalidReferencesException> {

    @SuppressWarnings("ThrowableNotThrown")
    @Test
    public void testWithNullReferencesFails() {
        assertThrows(NullPointerException.class, () -> new EbnfGrammarParserTokenInvalidReferencesException("message", null));
    }

    @SuppressWarnings("ThrowableNotThrown")
    @Test
    public void testWithEmptyReferencesFails() {
        assertThrows(IllegalArgumentException.class, () -> new EbnfGrammarParserTokenInvalidReferencesException("message", Sets.empty()));
    }

    @Test
    public void testWith() {
        final String message = "abc";
        final Set<EbnfIdentifierName> references = this.references();
        final EbnfGrammarParserTokenInvalidReferencesException exception = new EbnfGrammarParserTokenInvalidReferencesException(message, references);
        checkMessage(exception, message);
        assertEquals(references, exception.references(), "references");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(new EbnfGrammarParserTokenInvalidReferencesException("message 123", this.references()), "Unknown references=[abc]");
    }

    private Set<EbnfIdentifierName> references() {
        return Sets.of(EbnfIdentifierName.with("abc"));
    }

    @Override
    public Class<EbnfGrammarParserTokenInvalidReferencesException> type() {
        return EbnfGrammarParserTokenInvalidReferencesException.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
