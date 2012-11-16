package com.marakana.android.stream.db;

import android.content.ContentValues;


/**
 * ColumnDef
 */
public class ColumnDef {
    /** Column Type */
    public static enum Type {
        /** boolean */ BOOLEAN,
        /** byte */ BYTE,
        /** byte[] */ BYTEARRAY,
        /** double */ DOUBLE,
        /** float */ FLOAT,
        /** int */ INTEGER,
        /** long */ LONG,
        /** short */ SHORT,
        /** String */ STRING
    };

    private final String name;
    private final Type type;

    /**
     * @param name
     * @param type
     */
    public ColumnDef(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    /**
     * @param srcCol
     * @param src
     * @param dst
     */
    public void copy(String srcCol, ContentValues src, ContentValues dst) {
        switch (type) {
            case BOOLEAN:
                dst.put(name, src.getAsBoolean(srcCol));
                break;
            case BYTE:
                dst.put(name, src.getAsByte(srcCol));
                break;
            case BYTEARRAY:
                dst.put(name, src.getAsByteArray(srcCol));
                break;
            case DOUBLE:
                dst.put(name, src.getAsDouble(srcCol));
                break;
            case FLOAT:
                dst.put(name, src.getAsFloat(srcCol));
                break;
            case INTEGER:
                dst.put(name, src.getAsInteger(srcCol));
                break;
            case LONG:
                dst.put(name, src.getAsLong(srcCol));
                break;
            case SHORT:
                dst.put(name, src.getAsShort(srcCol));
                break;
            case STRING:
                dst.put(name, src.getAsString(srcCol));
                break;
        }
    }
}