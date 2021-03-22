/**
 * Copyright (c) OpenRS
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.openrs.cache

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.util.*
import kotlin.experimental.and

/**
 * A [ReferenceTable] holds details for all the files with a single type,
 * such as checksums, versions and archive members. There are also optional
 * fields for identifier hashes and whirlpool digests.
 *
 * @author Graham
 * @author `Discardedx2
 * @author Sean
 */
class ReferenceTable {
    /**
     * Represents a child entry within an [Entry] in the
     * [ReferenceTable].
     *
     * @author Graham Edgecombe
     */
    class ChildEntry(
            /**
             * The net.openrs.cache index of this entry
             */
            private val index: Int
    ) {
        /**
         * Gets the identifier of this entry.
         *
         * @return The identifier.
         */
        /**
         * Sets the identifier of this entry.
         *
         * @param identifier
         * The identifier.
         */
        /**
         * This entry's identifier.
         */
        var identifier = -1

        /**
         * Gets the net.openrs.cache index for this child entry
         *
         * @return The net.openrs.cache index
         */
        fun index(): Int {
            return index
        }
    }

    /**
     * Represents a single entry within a [ReferenceTable].
     *
     * @author Graham Edgecombe
     */
    class Entry(
            /**
             * The net.openrs.cache index of this entry
             */
            private val index: Int
    ) {
        /**
         * Gets the identifier of this entry.
         *
         * @return The identifier.
         */
        /**
         * Sets the identifier of this entry.
         *
         * @param identifier
         * The identifier.
         */
        /**
         * The identifier of this entry.
         */
        var identifier = -1
        /**
         * Gets the CRC32 checksum of this entry.
         *
         * @return The CRC32 checksum.
         */
        /**
         * Sets the CRC32 checksum of this entry.
         *
         * @param crc
         * The CRC32 checksum.
         */
        /**
         * The CRC32 checksum of this entry.
         */
        var crc = 0
        /**
         * Gets the compressed size of this entry.
         *
         * @return The compressed size.
         */
        /**
         * The compressed size of this entry.
         */
        var compressed = 0
        /**
         * Gets the uncompressed size of this entry.
         *
         * @return The uncompressed size.
         */
        /**
         * The uncompressed size of this entry.
         */
        var uncompressed = 0
        /**
         * Gets the hash this entry
         *
         * @return The hash
         */
        /**
         * The hash of this entry
         */
        var hash = 0
        /**
         * Gets the whirlpool digest of this entry.
         *
         * @return The whirlpool digest.
         */
        /**
         * Sets the whirlpool digest of this entry.
         *
         * @param whirlpool
         * The whirlpool digest.
         * @throws IllegalArgumentException
         * if the digest is not 64 bytes long.
         */
        /**
         * The whirlpool digest of this entry.
         */
        var whirlpool = ByteArray(64)
            set(whirlpool) {
                require(whirlpool.size == 64)
                System.arraycopy(whirlpool, 0, this.whirlpool, 0, whirlpool.size)
            }
        /**
         * Gets the version of this entry.
         *
         * @return The version.
         */
        /**
         * Sets the version of this entry.
         *
         * @param version
         * The version.
         */
        /**
         * The version of this entry.
         */
        var version = 0

        /**
         * Child identifiers table
         */
        var identifiers: Identifiers? = null

        /**
         * The children in this entry.
         */
        val entries: SortedMap<Int, ChildEntry> = TreeMap()

        /**
         * Gets the net.openrs.cache index for this entry
         *
         * @return The net.openrs.cache index
         */
        fun index(): Int {
            return index
        }

        /**
         * Gets the maximum number of child entries.
         *
         * @return The maximum number of child entries.
         */
        fun capacity(): Int {
            return if (entries.isEmpty()) 0 else entries.lastKey() + 1
        }

        /**
         * Gets the child entry with the specified id.
         *
         * @param id
         * The id.
         * @return The entry, or `null` if it does not exist.
         */
        fun getEntry(id: Int): ChildEntry? {
            return entries[id]
        }

        /**
         * Replaces or inserts the child entry with the specified id.
         *
         * @param id
         * The id.
         * @param entry
         * The entry.
         */
        fun putEntry(id: Int, entry: ChildEntry) {
            entries[id] = entry
        }

        /**
         * Removes the entry with the specified id.
         *
         * @param id
         * The id.
         */
        fun removeEntry(id: Int) {
            entries.remove(id)
        }

        /**
         * Gets the number of actual child entries.
         *
         * @return The number of actual child entries.
         */
        fun size(): Int {
            return entries.size
        }
    }

    /**
     * Puts the data into a certain type by the format id.
     *
     * @param val
     * The value to put into the buffer.
     * @param os
     * The stream.
     * @throws IOException
     * The exception thrown if an i/o error occurs.
     */
    @Throws(IOException::class)
    fun putSmartFormat(`val`: Int, os: DataOutputStream) {
        if (format >= 7) putSmartInt(os, `val`) else os.writeShort(`val`)
    }

    /**
     * The format of this table.
     */
    private var format = 0

    /**
     * The version of this table.
     */
    private var version = 0

    /**
     * The flags of this table.
     */
    private var flags = 0

    /**
     * The entries in this table.
     */
    private val entries: SortedMap<Int, Entry> = TreeMap()
    /**
     * Gets the identifiers table
     *
     * @return The table
     */
    /**
     * Identifier table
     */
    var identifiers: net.openrs.cache.Identifiers? = null
        private set

    /**
     * Gets the maximum number of entries in this table.
     *
     * @return The maximum number of entries.
     */
    fun capacity(): Int {
        return if (entries.isEmpty()) 0 else entries.lastKey() + 1
    }

    /**
     * Encodes this [ReferenceTable] into a [ByteBuffer].
     *
     * @return The [ByteBuffer].
     * @throws IOException
     * if an I/O error occurs.
     */
    @Throws(IOException::class)
    fun encode(): ByteBuffer {
        /*
		 * we can't (easily) predict the size ahead of time, so we write to a
		 * stream and then to the buffer
		 */
        val bout = ByteArrayOutputStream()
        val os = DataOutputStream(bout)
        return os.use { os ->
            /* write the header */
            os.write(format)
            if (format >= 6) {
                os.writeInt(version)
            }
            os.write(flags)

            /* calculate and write the number of non-null entries */putSmartFormat(entries.size, os)

            /* write the ids */
            var last = 0
            for (id in 0 until capacity()) {
                if (entries.containsKey(id)) {
                    val delta = id - last
                    putSmartFormat(delta, os)
                    last = id
                }
            }

            /* write the identifiers if required */if (flags and FLAG_IDENTIFIERS != 0) {
            for (entry in entries.values) {
                os.writeInt(entry.identifier)
            }
        }

            /* write the CRC checksums */for (entry in entries.values) {
            os.writeInt(entry.crc)
        }

            /* write the hashes if required */if (flags and FLAG_HASH != 0) {
            for (entry in entries.values) {
                os.writeInt(entry.hash)
            }
        }

            /* write the whirlpool digests if required */if (flags and FLAG_WHIRLPOOL != 0) {
            for (entry in entries.values) {
                os.write(entry.whirlpool)
            }
        }

            /* write the sizes if required */if (flags and FLAG_SIZES != 0) {
            for (entry in entries.values) {
                os.writeInt(entry.compressed)
                os.writeInt(entry.uncompressed)
            }
        }

            /* write the versions */for (entry in entries.values) {
            os.writeInt(entry.version)
        }

            /* calculate and write the number of non-null child entries */for (entry in entries.values) {
            putSmartFormat(entry.entries.size, os)
        }

            /* write the child ids */for (entry in entries.values) {
            last = 0
            for (id in 0 until entry.capacity()) {
                if (entry.entries.containsKey(id)) {
                    val delta = id - last
                    putSmartFormat(delta, os)
                    last = id
                }
            }
        }

            /* write the child identifiers if required */if (flags and FLAG_IDENTIFIERS != 0) {
            for (entry in entries.values) {
                for (child in entry.entries.values) {
                    os.writeInt(child.identifier)
                }
            }
        }

            /* convert the stream to a byte array and then wrap a buffer */
            val bytes = bout.toByteArray()
            ByteBuffer.wrap(bytes)
        }
    }

    /**
     * Gets the entry with the specified id, or `null` if it does not
     * exist.
     *
     * @param id
     * The id.
     * @return The entry.
     */
    fun getEntry(id: Int): Entry? {
        return entries[id]
    }

    /**
     * Gets the entry with the specified id, or `null` if it does not
     * exist.
     *
     * @param id
     * The id.
     * @return The entry.
     */
    fun getEntry(archive: net.openrs.cache.type.ConfigArchive): Entry? {
        return getEntry(archive.id)
    }

    /**
     * Gets the child entry with the specified id, or `null` if it does
     * not exist.
     *
     * @param id
     * The parent id.
     * @param child
     * The child id.
     * @return The entry.
     */
    fun getEntry(id: Int, child: Int): ChildEntry? {
        val entry = entries[id] ?: return null
        return entry.getEntry(child)
    }

    /**
     * Gets the flags of this table.
     *
     * @return The flags.
     */
    fun getFlags(): Int {
        return flags
    }

    /**
     * Gets the format of this table.
     *
     * @return The format.
     */
    fun getFormat(): Int {
        return format
    }

    /**
     * Gets the version of this table.
     *
     * @return The version of this table.
     */
    fun getVersion(): Int {
        return version
    }

    /**
     * Replaces or inserts the entry with the specified id.
     *
     * @param id
     * The id.
     * @param entry
     * The entry.
     */
    fun putEntry(id: Int, entry: Entry) {
        entries[id] = entry
    }

    /**
     * Removes the entry with the specified id.
     *
     * @param id
     * The id.
     */
    fun removeEntry(id: Int) {
        entries.remove(id)
    }

    /**
     * Sets the flags of this table.
     *
     * @param flags
     * The flags.
     */
    fun setFlags(flags: Int) {
        this.flags = flags
    }

    /**
     * Sets the format of this table.
     *
     * @param format
     * The format.
     */
    fun setFormat(format: Int) {
        this.format = format
    }

    /**
     * Sets the version of this table.
     *
     * @param version
     * The version.
     */
    fun setVersion(version: Int) {
        this.version = version
    }

    /**
     * Gets the number of actual entries.
     *
     * @return The number of actual entries.
     */
    fun size(): Int {
        return entries.size
    }

    /**
     * Gets the uncompressed size of the index
     *
     * @return The size
     */
    val archiveSize: Int
        get() {
            var sum: Long = 0
            for (i in 0 until capacity()) {
                val e = entries[i]
                if (e != null) {
                    sum += e.uncompressed.toLong()
                }
            }
            return sum.toInt()
        }

    companion object {
        /**
         * A flag which indicates this [ReferenceTable] contains [BKDR]
         * hashed identifiers.
         */
        const val FLAG_IDENTIFIERS = 0x01

        /**
         * A flag which indicates this [ReferenceTable] contains whirlpool
         * digests for its entries.
         */
        const val FLAG_WHIRLPOOL = 0x02

        /**
         * A flag which indicates this [ReferenceTable] contains compression
         * sizes.
         */
        const val FLAG_SIZES = 0x04

        /**
         * A flag which indicates this [ReferenceTable] contains a type of
         * hash.
         */
        const val FLAG_HASH = 0x08

        /**
         * Decodes the slave checksum table contained in the specified
         * [ByteBuffer].
         *
         * @param buffer
         * The buffer.
         * @return The slave checksum table.
         */
        @JvmStatic
        fun decode(buffer: ByteBuffer): ReferenceTable {
            /* create a new table */
            val table = ReferenceTable()

            /* read header */table.format = (buffer.get() and 0xFF.toByte()).toInt()
            if (table.format < 5 || table.format > 7) {
                throw RuntimeException()
            }
            if (table.format >= 6) {
                table.version = buffer.int
            }
            table.flags = (buffer.get() and 0xFF.toByte()).toInt()

            /* read the ids */
            val ids = IntArray(if (table.format >= 7) net.openrs.util.ByteBufferUtils.getSmartInt(buffer) else (buffer.short and 0xFFFF.toShort()).toInt())
            var accumulator = 0
            var size = -1
            for (i in ids.indices) {
                val delta = if (table.format >= 7) net.openrs.util.ByteBufferUtils.getSmartInt(buffer) else buffer.short and 0xFFFF.toShort()
                accumulator += delta
                ids[i] = accumulator
                if (ids[i] > size) {
                    size = ids[i]
                }
            }
            size++
            // table.indices = ids;

            /* and allocate specific entries within that array */
            var index = 0
            for (id in ids) {
                table.entries[id] = Entry(index++)
            }

            /* read the identifiers if present */
            val identifiersArray = IntArray(size)
            if (table.flags and FLAG_IDENTIFIERS != 0) {
                for (id in ids) {
                    val identifier = buffer.int
                    identifiersArray[id] = identifier
                    table.entries[id]!!.identifier = identifier
                }
            }
            table.identifiers = net.openrs.cache.Identifiers(identifiersArray)

            /* read the CRC32 checksums */for (id in ids) {
                table.entries[id]!!.crc = buffer.int
            }

            /* read another hash if present */if (table.flags and FLAG_HASH != 0) {
                for (id in ids) {
                    table.entries[id]!!.hash = buffer.int
                }
            }

            /* read the whirlpool digests if present */if (table.flags and FLAG_WHIRLPOOL != 0) {
                for (id in ids) {
                    buffer[table.entries[id]!!.whirlpool]
                }
            }

            /* read the sizes of the archive */if (table.flags and FLAG_SIZES != 0) {
                for (id in ids) {
                    table.entries[id]!!.compressed = buffer.int
                    table.entries[id]!!.uncompressed = buffer.int
                }
            }

            /* read the version numbers */for (id in ids) {
                table.entries[id]!!.version = buffer.int
            }

            /* read the child sizes */
            val members = arrayOfNulls<IntArray>(size)
            for (id in ids) {
                members[id] =
                        IntArray(if (table.format >= 7) net.openrs.util.ByteBufferUtils.getSmartInt(buffer) else (buffer.short and 0xFFFF.toShort()).toInt())
            }

            /* read the child ids */
            val identifiers = arrayOfNulls<IntArray>(members.size)
            for (id in ids) {
                /* reset the accumulator and size */
                accumulator = 0
                size = -1

                /* loop through the array of ids */
                for (i in members[id]!!.indices) {
                    val delta = if (table.format >= 7) net.openrs.util.ByteBufferUtils.getSmartInt(buffer) else buffer.short and 0xFFFF.toShort()
                    accumulator += delta
                    members[id]!![i] = accumulator
                    if (members[id]!![i] > size) {
                        size = members[id]!![i]
                    }
                }
                size++
                identifiers[id] = IntArray(size)

                /* and allocate specific entries within the array */index = 0
                for (child in members[id]!!) {
                    table.entries[id]!!.entries[child] = ChildEntry(index++)
                }
            }

            /* read the child identifiers if present */
            if (table.flags and FLAG_IDENTIFIERS != 0) {
                for (id in ids) {
                    for (child in members[id]!!) {
                        val identifier = buffer.int
                        identifiers[id]!![child] = identifier
                        table.entries[id]!!.entries[child]!!.identifier = identifier
                    }
                    table.entries[id]!!.identifiers = Identifiers(identifiers[id])
                }
            }

            /* return the table we constructed */return table
        }

        /**
         * Puts a smart integer into the stream.
         *
         * @param os
         * The stream.
         * @param value
         * The value.
         * @throws IOException
         * The exception thrown if an i/o error occurs.
         *
         * Credits to Graham for this method.
         */
        @Throws(IOException::class)
        fun putSmartInt(os: DataOutputStream, value: Int) {
            if (value and 0xFFFF < 32768) os.writeShort(value) else os.writeInt(-0x80000000 or value)
        }
    }
}

private operator fun Int.plusAssign(delta: Any) {

}
