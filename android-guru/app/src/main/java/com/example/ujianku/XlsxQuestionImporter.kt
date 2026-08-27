package com.example.ujianku

import android.content.ContentResolver
import android.net.Uri
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.InputStreamReader
import java.util.zip.ZipInputStream

/**
 * Pembaca XLSX ringan khusus Bank Soal.
 * Tidak membutuhkan Apache POI sehingga APK tetap ringan.
 * Membaca worksheet pertama dan mendukung shared strings + inline strings.
 */
object XlsxQuestionImporter {

    data class ImportRow(
        val rowNumber: Int,
        val question: String,
        val optionA: String,
        val optionB: String,
        val optionC: String,
        val optionD: String,
        val correctAnswer: String
    )

    data class Result(
        val rows: List<ImportRow>,
        val errors: List<String>
    )

    fun read(contentResolver: ContentResolver, uri: Uri): Result {
        val entries = HashMap<String, ByteArray>()
        contentResolver.openInputStream(uri).use { input ->
            requireNotNull(input) { "File Excel tidak dapat dibuka." }
            ZipInputStream(input).use { zip ->
                while (true) {
                    val entry = zip.nextEntry ?: break
                    if (!entry.isDirectory) {
                        entries[entry.name] = zip.readBytes()
                    }
                }
            }
        }

        val workbook = entries["xl/workbook.xml"] ?: error("Workbook Excel tidak ditemukan.")
        val rels = entries["xl/_rels/workbook.xml.rels"] ?: error("Relasi worksheet Excel tidak ditemukan.")
        val sheetPath = findFirstWorksheetPath(workbook, rels)
        val sheetBytes = entries[sheetPath] ?: error("Worksheet pertama tidak ditemukan.")
        val sharedStrings = entries["xl/sharedStrings.xml"]?.let(::parseSharedStrings).orEmpty()

        return parseSheet(sheetBytes, sharedStrings)
    }

    private fun findFirstWorksheetPath(workbook: ByteArray, rels: ByteArray): String {
        val firstRelationshipId = parseFirstSheetRelationshipId(workbook)
        val target = parseRelationshipTarget(rels, firstRelationshipId)
            ?: error("Worksheet pertama Excel tidak memiliki relasi yang valid.")
        return normalizeZipPath("xl/$target")
    }

    private fun parseFirstSheetRelationshipId(bytes: ByteArray): String {
        val parser = Xml.newPullParser()
        parser.setInput(InputStreamReader(bytes.inputStream(), Charsets.UTF_8))
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG && parser.name == "sheet") {
                return parser.getAttributeValue(null, "{http://schemas.openxmlformats.org/officeDocument/2006/relationships}id")
                    ?: parser.getAttributeValue("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "id")
                    ?: error("Relationship worksheet pertama tidak ditemukan.")
            }
        }
        error("Sheet Excel tidak ditemukan.")
    }

    private fun parseRelationshipTarget(bytes: ByteArray, relationshipId: String): String? {
        val parser = Xml.newPullParser()
        parser.setInput(InputStreamReader(bytes.inputStream(), Charsets.UTF_8))
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG && parser.name == "Relationship") {
                val id = parser.getAttributeValue(null, "Id")
                if (id == relationshipId) {
                    return parser.getAttributeValue(null, "Target")
                }
            }
        }
        return null
    }

    private fun normalizeZipPath(path: String): String {
        val clean = path.replace('\\', '/').removePrefix("/")
        if (clean.startsWith("xl/")) return clean
        return "xl/" + clean.removePrefix("../")
    }

    private fun parseSharedStrings(bytes: ByteArray): List<String> {
        val result = mutableListOf<String>()
        val parser = Xml.newPullParser()
        parser.setInput(InputStreamReader(bytes.inputStream(), Charsets.UTF_8))
        var current = StringBuilder()
        var inSi = false
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            when (parser.eventType) {
                XmlPullParser.START_TAG -> when (parser.name) {
                    "si" -> { inSi = true; current = StringBuilder() }
                    "t" -> if (inSi) current.append(parser.nextText())
                }
                XmlPullParser.END_TAG -> if (parser.name == "si" && inSi) {
                    result.add(current.toString())
                    inSi = false
                }
            }
        }
        return result
    }

    private fun parseSheet(bytes: ByteArray, sharedStrings: List<String>): Result {
        val errors = mutableListOf<String>()
        val rows = mutableListOf<ImportRow>()
        val parser = Xml.newPullParser()
        parser.setInput(InputStreamReader(bytes.inputStream(), Charsets.UTF_8))

        var currentRowNumber = 0
        var cells = mutableMapOf<Int, String>()
        var headerValid = false
        var cellColumn = -1
        var cellType: String? = null
        var cellValue = ""
        var inlineValue = StringBuilder()

        fun finishRow() {
            if (currentRowNumber == 0) return
            if (currentRowNumber == 1) {
                val expected = mapOf(
                    1 to setOf("pertanyaan"),
                    2 to setOf("pilihan a", "a"),
                    3 to setOf("pilihan b", "b"),
                    4 to setOf("pilihan c", "c"),
                    5 to setOf("pilihan d", "d"),
                    6 to setOf("jawaban", "jawaban benar", "kunci", "kunci jawaban")
                )
                headerValid = expected.all { (col, names) ->
                    normalizeHeader(cells[col]).let { it in names }
                }
                if (!headerValid) {
                    errors.add("Header Excel tidak sesuai. Gunakan: No, Pertanyaan, Pilihan A, Pilihan B, Pilihan C, Pilihan D, Jawaban.")
                }
                return
            }
            if (!headerValid || cells.values.all { it.isBlank() }) return

            fun value(col: Int) = cells[col]?.trim().orEmpty()
            val question = value(1)
            val a = value(2)
            val b = value(3)
            val c = value(4)
            val d = value(5)
            val answer = value(6).uppercase()

            val missing = mutableListOf<String>()
            if (question.isBlank()) missing.add("Pertanyaan")
            if (a.isBlank()) missing.add("Pilihan A")
            if (b.isBlank()) missing.add("Pilihan B")
            if (c.isBlank()) missing.add("Pilihan C")
            if (d.isBlank()) missing.add("Pilihan D")
            if (answer !in setOf("A", "B", "C", "D")) missing.add("Jawaban (A/B/C/D)")

            if (missing.isNotEmpty()) {
                errors.add("Baris $currentRowNumber: ${missing.joinToString(", ")} tidak valid.")
            } else {
                rows.add(ImportRow(currentRowNumber, question, a, b, c, d, answer))
            }
        }

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            when (parser.eventType) {
                XmlPullParser.START_TAG -> when (parser.name) {
                    "row" -> {
                        cells = mutableMapOf()
                        currentRowNumber = parser.getAttributeValue(null, "r")?.toIntOrNull() ?: (currentRowNumber + 1)
                    }
                    "c" -> {
                        val ref = parser.getAttributeValue(null, "r") ?: ""
                        cellColumn = columnIndex(ref)
                        cellType = parser.getAttributeValue(null, "t")
                        cellValue = ""
                        inlineValue = StringBuilder()
                    }
                    "v" -> cellValue = parser.nextText()
                    "t" -> if (cellType == "inlineStr") {
                        inlineValue.append(parser.nextText())
                    }
                }
                XmlPullParser.END_TAG -> when (parser.name) {
                    "c" -> {
                        val value = when (cellType) {
                            "s" -> sharedStrings.getOrNull(cellValue.toIntOrNull() ?: -1).orEmpty()
                            "inlineStr" -> inlineValue.toString()
                            "str" -> cellValue
                            else -> cellValue
                        }
                        if (cellColumn >= 0) cells[cellColumn] = value
                        cellColumn = -1
                        cellType = null
                    }
                    "row" -> finishRow()
                }
            }
        }

        if (rows.isEmpty() && errors.isEmpty()) {
            errors.add("Tidak ada soal yang dapat diimport. Pastikan format Excel sesuai template.")
        }
        return Result(rows, errors)
    }

    private fun normalizeHeader(value: String?): String =
        value.orEmpty().trim().lowercase().replace(Regex("\\s+"), " ")

    private fun columnIndex(cellRef: String): Int {
        var index = 0
        var found = false
        for (ch in cellRef) {
            if (ch.isLetter()) {
                found = true
                index = index * 26 + (ch.uppercaseChar() - 'A' + 1)
            } else if (found) break
        }
        return if (found) index - 1 else -1
    }
}
