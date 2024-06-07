package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

    private static String generateKey(int[] arrayref, int index) {
        return System.identityHashCode(arrayref) + "_" + index;
    }

    public static int read(int[] arrayref, int arrayidx) {
        String key = generateKey(arrayref, arrayidx);
        System.out.println("Reading " + key);
        Integer leaf = positionMap.get(key);
        if (leaf == null) {
            return 0; // TODO: Handle not in tree
        }

        List<Block> path = accessPath(leaf);
        for (Block block : path) {
            System.out.println("Block " + block.key);
            if (block != null && block.key.equals(key)) {
                return block.value;
            }
        }
        return 0; // TODO: Handle not in tree
    }

    public static void write(int[] arrayref, int arrayidx, int value) {
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
        int value;

        Block(String key, int value) {
            this.key = key;
            this.value = value;
        }
    }
}