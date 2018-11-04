package org.typemeta.funcj.codec.xml;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.MapCodecs.*;
import org.typemeta.funcj.codec.xml.io.XmlIO;
import org.typemeta.funcj.codec.xml.io.XmlIO.*;

import java.util.Map;

public abstract class XmlMapCodecs {

    public static class MapCodec<K, V> extends AbstractMapCodec<K, V, Input, Output> {
        private final XmlCodecCoreImpl core;

        public MapCodec(
                XmlCodecCoreImpl core,
                Class<Map<K, V>> type,
                Codec<K, Input, Output> keyCodec,
                Codec<V, Input, Output> valueCodec) {
            super(type, keyCodec, valueCodec);
            this.core = core;
        }

        @Override
        public CodecCoreInternal<Input, Output> core() {
            return core;
        }

        @Override
        public Output encode(Map<K, V> value, Output out) {

            value.forEach((k, v) -> {
                out.startElement(core.config().entryElemName());
                out.startElement(core.config().keyElemName());
                keyCodec.encodeWithCheck(k, out);
                out.endElement();
                out.startElement(core.config().valueElemName());
                valueCodec.encodeWithCheck(v, out);
                out.endElement();
                out.endElement();
            });

            return out;
        }

        @Override
        public Map<K, V> decode(Input in) {
            final Map<K, V> map = core.getNoArgsCtor(type).construct();

            while(in.hasNext()) {
                if (!in.type().equals(XmlIO.Input.Type.START_ELEMENT)) {
                    break;
                }

                in.startElement(core.config().entryElemName());
                in.startElement(core.config().keyElemName());
                final K key = keyCodec.decodeWithCheck(in);
                in.endElement();
                in.startElement(core.config().valueElemName());
                final V val = valueCodec.decodeWithCheck(in);
                in.endElement();
                in.endElement();
                map.put(key, val);
            }

            return map;
        }
    }

    public static class StringMapCodec<V> extends AbstractStringMapCodec<V, Input, Output> {
        private final XmlCodecCoreImpl core;

        public StringMapCodec(
                XmlCodecCoreImpl core,
                Class<Map<String, V>> type,
                Codec<V, Input, Output> valueCodec) {
            super(type, valueCodec);
            this.core = core;
        }

        @Override
        public CodecCoreInternal<Input, Output> core() {
            return core;
        }

        @Override
        public Output encode(Map<String, V> value, Output out) {
            value.forEach((key, val) -> {
                valueCodec.encodeWithCheck(val, out.startElement(key));
                out.endElement();
            });

            return out;
        }

        @Override
        public Map<String, V> decode(Input in) {
            final Map<String, V> map = core.getNoArgsCtor(type).construct();

            while (in.hasNext()) {
                if (!in.type().equals(XmlIO.Input.Type.START_ELEMENT)) {
                    break;
                }

                final String key = in.startElement();
                final V val = valueCodec.decodeWithCheck(in);
                in.endElement();
                map.put(key, val);
            }

            return map;
        }
    }
}
