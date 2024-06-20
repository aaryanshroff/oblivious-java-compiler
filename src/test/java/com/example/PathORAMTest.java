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
    assertNotNull(dataRead.get());
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
}