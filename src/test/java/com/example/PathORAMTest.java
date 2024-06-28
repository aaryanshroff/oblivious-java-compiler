package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

public class PathORAMTest {

  private PathORAM pathORAM;

  @BeforeEach
  public void setUp() {
    pathORAM = new PathORAM(64);
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

  // TODO: Does size of the block data affect obliviousness?
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

  @Test
  public void testReadWriteMultipleTimes() {
    String blockId = "multipleAccess";
    byte[] data1 = new byte[] { 1, 2, 3 };
    byte[] data2 = new byte[] { 4, 5, 6 };

    // Write data1
    pathORAM.access(blockId, Optional.of(data1), true);

    // Read and verify data1
    Optional<byte[]> readData1 = pathORAM.access(blockId, Optional.empty(), false);
    assertTrue(readData1.isPresent());
    assertArrayEquals(data1, readData1.get());

    // Write data2
    pathORAM.access(blockId, Optional.of(data2), true);

    // Read and verify data2
    Optional<byte[]> readData2 = pathORAM.access(blockId, Optional.empty(), false);
    assertTrue(readData2.isPresent());
    assertArrayEquals(data2, readData2.get());
  }

  @Test
  public void testMultipleBlockInteractions() {
    String blockId1 = "block1";
    String blockId2 = "block2";
    byte[] data1 = new byte[] { 1, 1, 1 };
    byte[] data2 = new byte[] { 2, 2, 2 };

    // Write to both blocks
    pathORAM.access(blockId1, Optional.of(data1), true);
    pathORAM.access(blockId2, Optional.of(data2), true);

    // Read from both blocks
    Optional<byte[]> readData1 = pathORAM.access(blockId1, Optional.empty(), false);
    Optional<byte[]> readData2 = pathORAM.access(blockId2, Optional.empty(), false);

    assertTrue(readData1.isPresent());
    assertTrue(readData2.isPresent());
    assertArrayEquals(data1, readData1.get());
    assertArrayEquals(data2, readData2.get());
  }

  @Test
  public void testOverwriteBlock() {
    String blockId = "overwriteBlock";
    byte[] initialData = new byte[] { 1, 2, 3 };
    byte[] newData = new byte[] { 4, 5, 6 };

    // Write initial data
    pathORAM.access(blockId, Optional.of(initialData), true);

    // Overwrite with new data
    pathORAM.access(blockId, Optional.of(newData), true);

    // Read and verify new data
    Optional<byte[]> readData = pathORAM.access(blockId, Optional.empty(), false);
    assertTrue(readData.isPresent());
    assertArrayEquals(newData, readData.get());
  }

  @Test
  public void testWriteEmptyData() {
    String blockId = "emptyDataBlock";
    byte[] emptyData = new byte[0];

    // Write empty data
    pathORAM.access(blockId, Optional.of(emptyData), true);

    // Read and verify empty data
    Optional<byte[]> readData = pathORAM.access(blockId, Optional.empty(), false);
    assertTrue(readData.isPresent());
    assertEquals(0, readData.get().length);
  }

  @Test
  public void testConsistencyAcrossMultipleAccesses() {
    String blockId = "consistencyBlock";
    byte[] data = new byte[] { 7, 8, 9 };

    // Write data
    pathORAM.access(blockId, Optional.of(data), true);

    // Read multiple times and verify consistency
    for (int i = 0; i < 10; i++) {
      Optional<byte[]> readData = pathORAM.access(blockId, Optional.empty(), false);
      assertTrue(readData.isPresent());
      assertArrayEquals(data, readData.get());
    }
  }
}