package org.typemeta.funcj.json;

import java.util.*;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toMap;

public class JSAPI {

    public static JSNull nul() {
        return JSNull.INSTANCE;
    }

    public static JSBool bool(boolean value) {
        return value ? JSBool.TRUE : JSBool.FALSE;
    }

    public static JSArray arr(JSValue... values) {
        return new JSArray(Arrays.asList(values));
    }

    public static JSArray arr(List<JSValue> values) {
        return new JSArray(values);
    }

    public static JSNumber num(byte value) {
        return new JSNumber(value);
    }

    public static JSNumber num(short value) {
        return new JSNumber(value);
    }

    public static JSNumber num(int value) {
        return new JSNumber(value);
    }

    public static JSNumber num(long value) {
        return new JSNumber(value);
    }

    public static JSNumber num(float value) {
        return new JSNumber(value);
    }

    public static JSNumber num(double value) {
        return new JSNumber(value);
    }

    public static JSNumber num(String value) {
        return new JSNumber(value);
    }

    public static JSString str(String value) {
        return new JSString(value);
    }

    public static JSObject.Field field(String name, JSValue node) {
        return new JSObject.Field(name, node);
    }

    public static JSObject obj(JSObject.Field... fields) {
        final LinkedHashMap<String, JSObject.Field> fieldMap =
                Arrays.stream(fields)
                        .collect(toMap(
                                JSObject.Field::getName,
                                Function.identity(),
                                Utils::duplicateKeyError,
                                LinkedHashMap::new
                        ));
        return obj(fieldMap);
    }

    public static JSObject obj(Iterable<JSObject.Field> iter) {
        final LinkedHashMap<String, JSObject.Field> fieldMap =
                StreamSupport.stream(iter.spliterator(), false)
                        .collect(toMap(
                                JSObject.Field::getName,
                                Function.identity(),
                                Utils::duplicateKeyError,
                                LinkedHashMap::new
                        ));
        return obj(fieldMap);
    }

    public static JSObject obj(LinkedHashMap<String, JSObject.Field> values) {
        return new JSObject(values);
    }
}
