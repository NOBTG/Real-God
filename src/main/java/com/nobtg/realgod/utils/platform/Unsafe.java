package com.nobtg.realgod.utils.platform;

import java.lang.reflect.Field;

public final class Unsafe {
    public static final int ADDRESS_SIZE = JDKUnsafe.addressSize();
    public static final int INVALID_FIELD_OFFSET = JDKUnsafe.INVALID_FIELD_OFFSET;
    public static final int ARRAY_BOOLEAN_BASE_OFFSET = JDKUnsafe.ARRAY_BOOLEAN_BASE_OFFSET;
    public static final int ARRAY_BYTE_BASE_OFFSET = JDKUnsafe.ARRAY_BYTE_BASE_OFFSET;
    public static final int ARRAY_SHORT_BASE_OFFSET = JDKUnsafe.ARRAY_SHORT_BASE_OFFSET;
    public static final int ARRAY_CHAR_BASE_OFFSET = JDKUnsafe.ARRAY_CHAR_BASE_OFFSET;
    public static final int ARRAY_INT_BASE_OFFSET = JDKUnsafe.ARRAY_INT_BASE_OFFSET;
    public static final int ARRAY_LONG_BASE_OFFSET = JDKUnsafe.ARRAY_LONG_BASE_OFFSET;
    public static final int ARRAY_FLOAT_BASE_OFFSET = JDKUnsafe.ARRAY_FLOAT_BASE_OFFSET;
    public static final int ARRAY_DOUBLE_BASE_OFFSET = JDKUnsafe.ARRAY_DOUBLE_BASE_OFFSET;
    public static final int ARRAY_OBJECT_BASE_OFFSET = JDKUnsafe.ARRAY_OBJECT_BASE_OFFSET;
    public static final int ARRAY_BOOLEAN_INDEX_SCALE = JDKUnsafe.ARRAY_BOOLEAN_INDEX_SCALE;
    public static final int ARRAY_BYTE_INDEX_SCALE = JDKUnsafe.ARRAY_BYTE_INDEX_SCALE;
    public static final int ARRAY_SHORT_INDEX_SCALE = JDKUnsafe.ARRAY_SHORT_INDEX_SCALE;
    public static final int ARRAY_CHAR_INDEX_SCALE = JDKUnsafe.ARRAY_CHAR_INDEX_SCALE;
    public static final int ARRAY_INT_INDEX_SCALE = JDKUnsafe.ARRAY_INT_INDEX_SCALE;
    public static final int ARRAY_LONG_INDEX_SCALE = JDKUnsafe.ARRAY_LONG_INDEX_SCALE;
    public static final int ARRAY_FLOAT_INDEX_SCALE = JDKUnsafe.ARRAY_FLOAT_INDEX_SCALE;
    public static final int ARRAY_DOUBLE_INDEX_SCALE = JDKUnsafe.ARRAY_DOUBLE_INDEX_SCALE;
    public static final int ARRAY_OBJECT_INDEX_SCALE = JDKUnsafe.ARRAY_OBJECT_INDEX_SCALE;

    public static int getInt(Object o, long offset) {
        return JDKUnsafe.getInt(o, offset);
    }

    public static void putInt(Object o, long offset, int x) {
        JDKUnsafe.putInt(o, offset, x);
    }

    public static Object getObject(Object o, long offset) {
        return JDKUnsafe.getReference(o, offset);
    }

    public static void putObject(Object o, long offset, Object x) {
        JDKUnsafe.putReference(o, offset, x);
    }

    public static boolean getBoolean(Object o, long offset) {
        return JDKUnsafe.getBoolean(o, offset);
    }

    public static void putBoolean(Object o, long offset, boolean x) {
        JDKUnsafe.putBoolean(o, offset, x);
    }

    public static byte getByte(Object o, long offset) {
        return JDKUnsafe.getByte(o, offset);
    }

    public static void putByte(Object o, long offset, byte x) {
        JDKUnsafe.putByte(o, offset, x);
    }

    public static short getShort(Object o, long offset) {
        return JDKUnsafe.getShort(o, offset);
    }

    public static void putShort(Object o, long offset, short x) {
        JDKUnsafe.putShort(o, offset, x);
    }

    public static char getChar(Object o, long offset) {
        return JDKUnsafe.getChar(o, offset);
    }

    public static void putChar(Object o, long offset, char x) {
        JDKUnsafe.putChar(o, offset, x);
    }

    public static long getLong(Object o, long offset) {
        return JDKUnsafe.getLong(o, offset);
    }

    public static void putLong(Object o, long offset, long x) {
        JDKUnsafe.putLong(o, offset, x);
    }

    public static float getFloat(Object o, long offset) {
        return JDKUnsafe.getFloat(o, offset);
    }

    public static void putFloat(Object o, long offset, float x) {
        JDKUnsafe.putFloat(o, offset, x);
    }

    public static double getDouble(Object o, long offset) {
        return JDKUnsafe.getDouble(o, offset);
    }

    public static void putDouble(Object o, long offset, double x) {
        JDKUnsafe.putDouble(o, offset, x);
    }

    public static byte getByte(long address) {
        return JDKUnsafe.getByte(address);
    }

    public static void putByte(long address, byte x) {
        JDKUnsafe.putByte(address, x);
    }

    public static short getShort(long address) {
        return JDKUnsafe.getShort(address);
    }

    public static void putShort(long address, short x) {
        JDKUnsafe.putShort(address, x);
    }

    public static char getChar(long address) {
        return JDKUnsafe.getChar(address);
    }

    public static void putChar(long address, char x) {
        JDKUnsafe.putChar(address, x);
    }

    public static int getInt(long address) {
        return JDKUnsafe.getInt(address);
    }

    public static void putInt(long address, int x) {
        JDKUnsafe.putInt(address, x);
    }

    public static long getLong(long address) {
        return JDKUnsafe.getLong(address);
    }

    public static void putLong(long address, long x) {
        JDKUnsafe.putLong(address, x);
    }

    public static float getFloat(long address) {
        return JDKUnsafe.getFloat(address);
    }

    public static void putFloat(long address, float x) {
        JDKUnsafe.putFloat(address, x);
    }

    public static double getDouble(long address) {
        return JDKUnsafe.getDouble(address);
    }

    public static void putDouble(long address, double x) {
        JDKUnsafe.putDouble(address, x);
    }

    public static long getAddress(long address) {
        return JDKUnsafe.getAddress(address);
    }

    public static void putAddress(long address, long x) {
        JDKUnsafe.putAddress(address, x);
    }

    public static long allocateMemory(long bytes) {
        return JDKUnsafe.allocateMemory(bytes);
    }

    public static long reallocateMemory(long address, long bytes) {
        return JDKUnsafe.reallocateMemory(address, bytes);
    }

    public static void setMemory(Object o, long offset, long bytes, byte value) {
        JDKUnsafe.setMemory(o, offset, bytes, value);
    }

    public static void setMemory(long address, long bytes, byte value) {
        JDKUnsafe.setMemory(address, bytes, value);
    }

    public static void copyMemory(Object srcBase, long srcOffset, Object destBase, long destOffset, long bytes) {
        JDKUnsafe.copyMemory(srcBase, srcOffset, destBase, destOffset, bytes);
    }

    public static void copyMemory(long srcAddress, long destAddress, long bytes) {
        JDKUnsafe.copyMemory(srcAddress, destAddress, bytes);
    }

    public static void freeMemory(long address) {
        JDKUnsafe.freeMemory(address);
    }

    public static long objectFieldOffset(Field f) {
        if (f == null) {
            throw new NullPointerException();
        }
        Class<?> declaringClass = f.getDeclaringClass();
        if (declaringClass.isHidden()) {
            throw new UnsupportedOperationException("can't get field offset on a hidden class: " + f);
        }
        if (declaringClass.isRecord()) {
            throw new UnsupportedOperationException("can't get field offset on a record class: " + f);
        }
        return JDKUnsafe.objectFieldOffset(f);
    }

    public static long staticFieldOffset(Field f) {
        if (f == null) {
            throw new NullPointerException();
        }
        Class<?> declaringClass = f.getDeclaringClass();
        if (declaringClass.isHidden()) {
            throw new UnsupportedOperationException("can't get field offset on a hidden class: " + f);
        }
        if (declaringClass.isRecord()) {
            throw new UnsupportedOperationException("can't get field offset on a record class: " + f);
        }
        return JDKUnsafe.staticFieldOffset(f);
    }

    public static Object staticFieldBase(Field f) {
        if (f == null) {
            throw new NullPointerException();
        }
        Class<?> declaringClass = f.getDeclaringClass();
        if (declaringClass.isHidden()) {
            throw new UnsupportedOperationException("can't get base address on a hidden class: " + f);
        }
        if (declaringClass.isRecord()) {
            throw new UnsupportedOperationException("can't get base address on a record class: " + f);
        }
        return JDKUnsafe.staticFieldBase(f);
    }

    @Deprecated(since = "15", forRemoval = true)
    public static boolean shouldBeInitialized(Class<?> c) {
        return JDKUnsafe.shouldBeInitialized(c);
    }

    @Deprecated(since = "15", forRemoval = true)
    public static void ensureClassInitialized(Class<?> c) {
        JDKUnsafe.ensureClassInitialized(c);
    }

    public static int arrayBaseOffset(Class<?> arrayClass) {
        return JDKUnsafe.arrayBaseOffset(arrayClass);
    }

    public static int arrayIndexScale(Class<?> arrayClass) {
        return JDKUnsafe.arrayIndexScale(arrayClass);
    }

    public static int addressSize() {
        return JDKUnsafe.addressSize();
    }

    public static int pageSize() {
        return JDKUnsafe.pageSize();
    }

    public static Object allocateInstance(Class<?> cls) throws InstantiationException {
        return JDKUnsafe.allocateInstance(cls);
    }

    public static void throwException(Throwable ee) {
        JDKUnsafe.throwException(ee);
    }

    public static final boolean compareAndSwapObject(Object o, long offset, Object expected, Object x) {
        return JDKUnsafe.compareAndSetReference(o, offset, expected, x);
    }

    public static final boolean compareAndSwapInt(Object o, long offset, int expected, int x) {
        return JDKUnsafe.compareAndSetInt(o, offset, expected, x);
    }

    public static final boolean compareAndSwapLong(Object o, long offset, long expected, long x) {
        return JDKUnsafe.compareAndSetLong(o, offset, expected, x);
    }

    public static Object getObjectVolatile(Object o, long offset) {
        return JDKUnsafe.getReferenceVolatile(o, offset);
    }

    public static void putObjectVolatile(Object o, long offset, Object x) {
        JDKUnsafe.putReferenceVolatile(o, offset, x);
    }

    public static int getIntVolatile(Object o, long offset) {
        return JDKUnsafe.getIntVolatile(o, offset);
    }

    public static void putIntVolatile(Object o, long offset, int x) {
        JDKUnsafe.putIntVolatile(o, offset, x);
    }

    public static boolean getBooleanVolatile(Object o, long offset) {
        return JDKUnsafe.getBooleanVolatile(o, offset);
    }

    public static void putBooleanVolatile(Object o, long offset, boolean x) {
        JDKUnsafe.putBooleanVolatile(o, offset, x);
    }

    public static byte getByteVolatile(Object o, long offset) {
        return JDKUnsafe.getByteVolatile(o, offset);
    }

    public static void putByteVolatile(Object o, long offset, byte x) {
        JDKUnsafe.putByteVolatile(o, offset, x);
    }

    public static short getShortVolatile(Object o, long offset) {
        return JDKUnsafe.getShortVolatile(o, offset);
    }

    public static void putShortVolatile(Object o, long offset, short x) {
        JDKUnsafe.putShortVolatile(o, offset, x);
    }

    public static char getCharVolatile(Object o, long offset) {
        return JDKUnsafe.getCharVolatile(o, offset);
    }

    public static void putCharVolatile(Object o, long offset, char x) {
        JDKUnsafe.putCharVolatile(o, offset, x);
    }

    public static long getLongVolatile(Object o, long offset) {
        return JDKUnsafe.getLongVolatile(o, offset);
    }

    public static void putLongVolatile(Object o, long offset, long x) {
        JDKUnsafe.putLongVolatile(o, offset, x);
    }

    public static float getFloatVolatile(Object o, long offset) {
        return JDKUnsafe.getFloatVolatile(o, offset);
    }

    public static void putFloatVolatile(Object o, long offset, float x) {
        JDKUnsafe.putFloatVolatile(o, offset, x);
    }

    public static double getDoubleVolatile(Object o, long offset) {
        return JDKUnsafe.getDoubleVolatile(o, offset);
    }

    public static void putDoubleVolatile(Object o, long offset, double x) {
        JDKUnsafe.putDoubleVolatile(o, offset, x);
    }

    public static void putOrderedObject(Object o, long offset, Object x) {
        JDKUnsafe.putReferenceRelease(o, offset, x);
    }

    public static void putOrderedInt(Object o, long offset, int x) {
        JDKUnsafe.putIntRelease(o, offset, x);
    }

    public static void putOrderedLong(Object o, long offset, long x) {
        JDKUnsafe.putLongRelease(o, offset, x);
    }

    public static void unpark(Object thread) {
        JDKUnsafe.unpark(thread);
    }

    public static void park(boolean isAbsolute, long time) {
        JDKUnsafe.park(isAbsolute, time);
    }

    public static int getLoadAverage(double[] loadavg, int nelems) {
        return JDKUnsafe.getLoadAverage(loadavg, nelems);
    }

    public static final int getAndAddInt(Object o, long offset, int delta) {
        return JDKUnsafe.getAndAddInt(o, offset, delta);
    }

    public static final long getAndAddLong(Object o, long offset, long delta) {
        return JDKUnsafe.getAndAddLong(o, offset, delta);
    }

    public static final int getAndSetInt(Object o, long offset, int newValue) {
        return JDKUnsafe.getAndSetInt(o, offset, newValue);
    }

    public static final long getAndSetLong(Object o, long offset, long newValue) {
        return JDKUnsafe.getAndSetLong(o, offset, newValue);
    }

    public static final Object getAndSetObject(Object o, long offset, Object newValue) {
        return JDKUnsafe.getAndSetReference(o, offset, newValue);
    }

    public static void loadFence() {
        JDKUnsafe.loadFence();
    }

    public static void storeFence() {
        JDKUnsafe.storeFence();
    }

    public static void fullFence() {
        JDKUnsafe.fullFence();
    }

    public static void invokeCleaner(java.nio.ByteBuffer directBuffer) {
        if (!directBuffer.isDirect()) throw new IllegalArgumentException("buffer is non-direct");
        JDKUnsafe.invokeCleaner(directBuffer);
    }
}