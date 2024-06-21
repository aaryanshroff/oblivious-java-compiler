package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

public class PathORAMTest {

  private PathORAM pathORAM;

  @BeforeEach
  public void setUp() {
    pathORAM = new PathORAM();
  }

  @Test
  public void testAccessRead() {
    // Write some data to the PathORAM
    String blockId = "block1";
    byte[] dataToWrite = new byte[] { 1, 2, 3, 4 };
    pathORAM.access(blockId, Optional.of(dataToWrite), true);

    // Read the data back
    Optional<byte[]> dataRead = pathORAM.access(blockId, Optional.empty(), false);

    // Ensure that the data is not null and the correct data was read
    assertTrue(dataRead.isPresent());
    assertArrayEquals(dataToWrite, dataRead.get());
  }

  @Test
  public void testAccessWrite() {
    // Initialize a block
    String blockId = "block2";
    byte[] initialData = new byte[] { 5, 6, 7, 8 };
    pathORAM.access(blockId, Optional.of(initialData), true);

    // Write new data to the block
    byte[] newData = new byte[] { 9, 10, 11, 12 };
    pathORAM.access(blockId, Optional.of(newData), true);

    // Access the block for reading to verify the write
    Optional<byte[]> readData = pathORAM.access(blockId, Optional.empty(), false);

    // Assert that the data read is the same as the new data written
    assertTrue(readData.isPresent()); // Ensure that the readData is present
    assertArrayEquals(newData, readData.get());
  }

  @Test
  public void testReadUninitializedBlock() {
    String blockId = "uninitializedBlock";
    Optional<byte[]> dataRead = pathORAM.access(blockId, Optional.empty(), false);

    // Ensure that the data read is empty
    assertFalse(dataRead.isPresent());
  }

  @Test
  public void testWriteNullData() {
    String blockId = "block3";

    // Attempt to write null data
    assertThrows(NullPointerException.class, () -> {
      pathORAM.access(blockId, Optional.of(null), true);
    });
  }

  @Test
  public void testLargeDataBlock() {
    String blockId = "largeBlock";
    byte[] largeData = new byte[1024 * 1024]; // 1 MB of data
    for (int i = 0; i < largeData.length; i++) {
      largeData[i] = (byte) (i % 256);
    }

    pathORAM.access(blockId, Optional.of(largeData), true);
    Optional<byte[]> dataRead = pathORAM.access(blockId, Optional.empty(), false);

    assertTrue(dataRead.isPresent());
    assertArrayEquals(largeData, dataRead.get());
  }

  @Test
  public void testMultipleBlocks() {
    String blockId1 = "block5";
    String blockId2 = "block6";

    byte[] data1 = new byte[] { 1, 1, 1, 1 };
    byte[] data2 = new byte[] { 2, 2, 2, 2 };

    pathORAM.access(blockId1, Optional.of(data1), true);
    pathORAM.access(blockId2, Optional.of(data2), true);

    Optional<byte[]> dataRead1 = pathORAM.access(blockId1, Optional.empty(), false);
    Optional<byte[]> dataRead2 = pathORAM.access(blockId2, Optional.empty(), false);

    assertTrue(dataRead1.isPresent());
    assertArrayEquals(data1, dataRead1.get());

    assertTrue(dataRead2.isPresent());
    assertArrayEquals(data2, dataRead2.get());
  }

  @Test
  public void testInvalidBlockId() {
    String invalidBlockId = "invalidBlock";

    // Ensure that accessing an invalid block ID doesn't throw an exception
    assertDoesNotThrow(() -> {
      pathORAM.access(invalidBlockId, Optional.empty(), false);
    });
  }
}