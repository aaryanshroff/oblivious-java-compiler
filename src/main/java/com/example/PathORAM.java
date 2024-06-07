package com.example;

import java.util.*;

public class PathORAM {
    private static final int BUCKET_SIZE = 4;
    private static final int HEIGHT = 4;

    private static final Map<String, Integer> positionMap = new HashMap<>();
    private static final List<Bucket> storage = new ArrayList<>();
    private static final Random random = new Random();

    static {
        // Initialize the tree with empty buckets
        for (int i = 0; i < (1 << (HEIGHT + 1)) - 1; i++) {
            storage.add(new Bucket());
        }
    }

    private static String generateKey(Object arrayref, int index) {
        return System.identityHashCode(arrayref) + "_" + index;
    }

    public static int readIntArray(int[] arrayref, int arrayidx) {
        return read(arrayref, arrayidx, 0);
    }

    public static void writeIntArray(int[] arrayref, int arrayidx, int value) {
        write(arrayref, arrayidx, value);
    }

    public static float readFloatArray(float[] arrayref, int arrayidx) {
        return read(arrayref, arrayidx, 0.0f);
    }

    public static void writeFloatArray(float[] arrayref, int arrayidx, float value) {
        write(arrayref, arrayidx, value);
    }

    public static Object readObjectArray(Object[] arrayref, int arrayidx) {
        return read(arrayref, arrayidx, null);
    }

    public static void writeObjectArray(Object[] arrayref, int arrayidx, Object value) {
        write(arrayref, arrayidx, value);
    }

    private static <T> T read(Object arrayref, int arrayidx, T defaultValue) {
        String key = generateKey(arrayref, arrayidx);
        System.out.println("Reading " + key);
        Integer leaf = positionMap.get(key);
        if (leaf == null) {
            return defaultValue;
        }

        List<Block> path = accessPath(leaf);
        for (Block block : path) {
            if (block != null && block.key.equals(key)) {
                return (T) block.value;
            }
        }
        return defaultValue;
    }

    private static <T> void write(Object arrayref, int arrayidx, T value) {
        String key = generateKey(arrayref, arrayidx);
        Integer leaf = positionMap.computeIfAbsent(key, k -> random.nextInt(1 << HEIGHT));
        List<Block> path = accessPath(leaf);
        Block newBlock = new Block(key, value);

        for (int i = 0; i < path.size(); i++) {
            if (path.get(i) != null && path.get(i).key.equals(key)) {
                path.set(i, null);
                break;
            }
        }

        path.add(newBlock);
        positionMap.put(key, random.nextInt(1 << HEIGHT));

        for (int i = HEIGHT; i >= 0; i--) {
            int index = (leaf >> i) + (1 << i) - 1;
            storage.get(index).blocks.clear();
            for (int j = 0; j < BUCKET_SIZE && !path.isEmpty(); j++) {
                storage.get(index).blocks.add(path.remove(0));
            }
        }
    }

    private static List<Block> accessPath(int leaf) {
        List<Block> path = new ArrayList<>();
        for (int i = 0; i <= HEIGHT; i++) {
            int index = (leaf >> i) + (1 << i) - 1;
            path.addAll(storage.get(index).blocks);
        }
        return path;
    }

    private static class Bucket {
        List<Block> blocks;

        Bucket() {
            this.blocks = new ArrayList<>(BUCKET_SIZE);
        }
    }

    private static class Block {
        String key;
        Object value;

        Block(String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }
}