package com.github.noamm9.nvgrenderer.nvg

import java.io.FileNotFoundException
import java.nio.file.Files
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ResourceLoaderTest {
    @Test
    fun `normalizeLocation trims whitespace and leading slashes`() {
        assertEquals("testdata/resource-bytes.txt", ResourceLoader.normalizeLocation(" /testdata/resource-bytes.txt "))
    }

    @Test
    fun `read loads classpath resources`() {
        assertEquals("classpath-bytes", ResourceLoader.read("testdata/resource-bytes.txt").decodeToString().trimEnd())
    }

    @Test
    fun `read loads file system resources`() {
        val tempFile = Files.createTempFile("nvg-resource", ".txt")
        tempFile.writeText("filesystem-bytes")

        try {
            assertContentEquals("filesystem-bytes".encodeToByteArray(), ResourceLoader.read(tempFile.toString()))
        }
        finally {
            Files.deleteIfExists(tempFile)
        }
    }

    @Test
    fun `read throws for missing resources`() {
        assertFailsWith<FileNotFoundException> {
            ResourceLoader.read("testdata/does-not-exist.txt")
        }
    }

    @Test
    fun `read from url`() {
        assertContentEquals("Welcome to NoammAddons API!".encodeToByteArray(), ResourceLoader.read("https://api.noamm.org/"))
    }
}
