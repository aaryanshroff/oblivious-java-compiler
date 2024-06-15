package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class PathORAM {
  private final static int BUCKET_SIZE = 4;
  private final List<Bucket> tree = new ArrayList<>();
  private final HashMap<String, Integer> positionMap = new HashMap<>();
  private final List<Block> stash = new ArrayList<>();
  private final Random random = new Random();

  private final int treeHeight = 4;

  public byte[] access(String blockId, byte[] newData, boolean isWrite) {
    // Step 1: Remap block
    int prevBlockPos = positionMap.get(blockId);
    positionMap.put(blockId, random.nextInt(1 << (treeHeight - 1)));

    // Step 2: Read path
    List<Block> blocksOnPath = readPath(prevBlockPos);

    // Step 3: Update block
    if (isWrite) {
      // TODO
    }

    // TODO
    writePath(prevBlockPos);

    return null;
  }

  private List<Block> readPath(int leafPos) {
    List<Block> blocksOnPath = new ArrayList<>();
    for (int level = 0; level < treeHeight; level++) {
      int bucketIdx = computeIdxOfBucketOnThisLevelOnPathToLeaf(level, leafPos);
      blocksOnPath.addAll(tree.get(bucketIdx).popAllBlocks());
    }
    return blocksOnPath;
  }

  private void writePath(int leafPos) {
    for (int level = treeHeight - 1; level >= 0; level--) {
      int idxOfBucketOnThisLevelOnPathToLeaf = computeIdxOfBucketOnThisLevelOnPathToLeaf(level, leafPos)
      for (Block stashBlock : stash) {
        // stashBlock can be added to the bucket at this level (on the
        // path to leafPos) only if the bucket at this level on the path to the block's
        // leaf is the same
        if (idxOfBucketOnThisLevelOnPathToLeaf == computeIdxOfBucketOnThisLevelOnPathToLeaf(level, stashBlock.)) {

        }
      }
    }
  }

  private int computeIdxOfBucketOnThisLevelOnPathToLeaf(int level, int leafPos) {
    return (leafPos + (1 << (treeHeight - 1))) >> (treeHeight - 1 - level);
  }

  private static class Block {
    private String id;
    private byte[] data;

    Block(String id, byte[] data) {
      this.id = id;
      this.data = data;
    }
  }

  private static class Bucket {
    List<Block> blocks;

    Bucket() {
      this.blocks = new ArrayList<>(BUCKET_SIZE);
    }

    public List<Block> popAllBlocks() {
      List<Block> blocks = this.blocks;
      this.blocks.clear();
      return blocks;
    }
  }
}