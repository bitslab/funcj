package org.funcj.document;

import org.funcj.data.IList;
import org.funcj.util.Functions.F;

import java.io.Writer;

/**
 * A composable model structure for formattable documents.
 */
public interface Document {
    /**
     * A null or empty document.
     */
    enum Nil implements Document {
        INSTANCE {
            @Override
            public <T> T match(
                    F<Nil, T> nil,
                    F<Break, T> brek,
                    F<Text, T> text,
                    F<Group, T> group,
                    F<Nest, T> nest,
                    F<Concat, T> concat) {
                return nil.apply(this);
            }
        }
    }

    /**
     * A line break.
     */
    enum Break implements Document {
        INSTANCE {
            @Override
            public <T> T match(
                    F<Nil, T> nil,
                    F<Break, T> brek,
                    F<Text, T> text,
                    F<Group, T> group,
                    F<Nest, T> nest,
                    F<Concat, T> concat) {
                return brek.apply(this);
            }
        }
    }

    /**
     * A text fragment.
     * <p>
     * A section of text which does not contain newlines, and should not get broken up.
     */
    final class Text implements Document {

        public final String text;

        public Text(String text) {
            this.text = text;
        }

        @Override
        public <T> T match(
                F<Nil, T> nil,
                F<Break, T> brek,
                F<Text, T> text,
                F<Group, T> group,
                F<Nest, T> nest,
                F<Concat, T> concat) {
            return text.apply(this);
        }
    }

    /**
     * A grouped document.
     * <p>
     * Defines document elements that can be grouped onto a single line
     * (if it fits within the current width) by removing line breaks.
     */
    final class Group implements Document {

        public final Document doc;

        public Group(Document doc) {
            this.doc = doc;
        }

        @Override
        public <T> T match(
                F<Nil, T> nil,
                F<Break, T> brek,
                F<Text, T> text,
                F<Group, T> group,
                F<Nest, T> nest,
                F<Concat, T> concat) {
            return group.apply(this);
        }
    }

    /**
     * A nested document.
     * <p>
     * Indicate that the specified doc should formatted with nesting (i.e. indentation)
     */
    final class Nest implements Document {

        public final int indent;
        public final Document doc;

        public Nest(int indent, Document doc) {
            this.indent = indent;
            this.doc = doc;
        }

        @Override
        public <T> T match(
                F<Nil, T> nil,
                F<Break, T> brek,
                F<Text, T> text,
                F<Group, T> group,
                F<Nest, T> nest,
                F<Concat, T> concat) {
            return nest.apply(this);
        }
    }

    /**
     * A concatenation of documents.
     */
    final class Concat implements Document {

        public final IList<Document> children;

        public Concat(IList<Document> children) {
            this.children = children;
        }

        @Override
        public <T> T match(
                F<Nil, T> nil,
                F<Break, T> brek,
                F<Text, T> text,
                F<Group, T> group,
                F<Nest, T> nest,
                F<Concat, T> concat) {
            return concat.apply(this);
        }
    }

    <T> T match(
        F<Nil, T> nil,
        F<Break, T> brek,
        F<Text, T> text,
        F<Group, T> group,
        F<Nest, T> nest,
        F<Concat, T> concat);

    default void format(Writer wtr, int width) {
        DocFormat.format(wtr, width, this);
    }
}
