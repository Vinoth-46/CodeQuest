package com.codequest.learn.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codequest.learn.model.ProgrammingLanguage
import com.codequest.learn.ui.theme.*

@Composable
fun CodeBlock(
    code: String,
    language: ProgrammingLanguage,
    modifier: Modifier = Modifier,
    highlightBlanks: Boolean = false,
    showLineNumbers: Boolean = true
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val lines = code.lines()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Neutral800, RoundedCornerShape(12.dp)),
        color = Neutral950
    ) {
        Column {
            // Header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Neutral900)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Terminal mock window dots
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFFFF5F56), RoundedCornerShape(5.dp)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFFFFBD2E), RoundedCornerShape(5.dp)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFF27C93F), RoundedCornerShape(5.dp)))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = language.displayName.lowercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Neutral400,
                        fontFamily = FontFamily.Monospace
                    )
                }

                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("code", code)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Code copied", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy code",
                        tint = Neutral400,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Code content with line numbers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .horizontalScroll(scrollState)
            ) {
                if (showLineNumbers) {
                    Column(
                        modifier = Modifier
                            .padding(start = 12.dp, end = 12.dp)
                    ) {
                        lines.indices.forEach { idx ->
                            Text(
                                text = "${idx + 1}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    lineHeight = 20.sp
                                ),
                                color = Neutral600
                            )
                        }
                    }

                    // Vertical divider between line numbers and code
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height((lines.size * 20).dp)
                            .background(Neutral800)
                    )
                }

                Column(
                    modifier = Modifier.padding(start = 12.dp, end = 16.dp)
                ) {
                    lines.forEach { line ->
                        val annotated = highlightCodeLine(line, language, highlightBlanks)
                        Text(
                            text = annotated,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Lightweight, high-performance syntax highlighter for Kotlin/Java/Python in Compose
 */
private fun highlightCodeLine(
    line: String,
    language: ProgrammingLanguage,
    highlightBlanks: Boolean
): AnnotatedString {
    return buildAnnotatedString {
        val keywords = when (language) {
            ProgrammingLanguage.PYTHON -> setOf(
                "def", "class", "return", "if", "elif", "else", "for", "while",
                "in", "not", "and", "or", "import", "from", "as", "try", "except",
                "finally", "with", "lambda", "pass", "None", "True", "False", "is",
                "yield", "raise", "break", "continue"
            )
            ProgrammingLanguage.JAVA -> setOf(
                "public", "private", "protected", "class", "interface", "extends",
                "implements", "static", "final", "void", "int", "double", "float",
                "boolean", "char", "long", "return", "if", "else", "for", "while",
                "new", "null", "true", "false", "this", "super", "try", "catch",
                "finally", "throw", "throws", "import", "package"
            )
        }

        val builtins = setOf("print", "len", "range", "str", "int", "list", "dict", "set", "System", "out", "println")

        var i = 0
        while (i < line.length) {
            // Check for comment
            if (line[i] == '#' || (i + 1 < line.length && line[i] == '/' && line[i + 1] == '/')) {
                val comment = line.substring(i)
                pushStyle(SpanStyle(color = Neutral500))
                append(comment)
                pop()
                break
            }

            // Check for string literal
            if (line[i] == '"' || line[i] == '\'') {
                val quote = line[i]
                var end = i + 1
                while (end < line.length && line[end] != quote) {
                    if (line[end] == '\\' && end + 1 < line.length) {
                        end += 2
                    } else {
                        end++
                    }
                }
                if (end < line.length) end++ // Include closing quote
                val str = line.substring(i, end)
                pushStyle(SpanStyle(color = Green400))
                append(str)
                pop()
                i = end
                continue
            }

            // Check for blank placeholder ___X___
            if (highlightBlanks && line.startsWith("___", i)) {
                val end = line.indexOf("___", i + 3)
                if (end != -1) {
                    val blank = line.substring(i, end + 3)
                    pushStyle(
                        SpanStyle(
                            color = Yellow400,
                            fontWeight = FontWeight.Bold,
                            background = Yellow900
                        )
                    )
                    append(" [ $blank ] ")
                    pop()
                    i = end + 3
                    continue
                }
            }

            // Check for numbers
            if (line[i].isDigit()) {
                var end = i
                while (end < line.length && (line[end].isDigit() || line[end] == '.')) {
                    end++
                }
                val num = line.substring(i, end)
                pushStyle(SpanStyle(color = Purple400))
                append(num)
                pop()
                i = end
                continue
            }

            // Check for identifier/word
            if (line[i].isLetter() || line[i] == '_') {
                var end = i
                while (end < line.length && (line[end].isLetterOrDigit() || line[end] == '_')) {
                    end++
                }
                val word = line.substring(i, end)
                when {
                    keywords.contains(word) -> {
                        pushStyle(SpanStyle(color = Blue400, fontWeight = FontWeight.SemiBold))
                        append(word)
                        pop()
                    }
                    builtins.contains(word) -> {
                        pushStyle(SpanStyle(color = Yellow400))
                        append(word)
                        pop()
                    }
                    else -> {
                        pushStyle(SpanStyle(color = Neutral100))
                        append(word)
                        pop()
                    }
                }
                i = end
                continue
            }

            // Default character
            pushStyle(SpanStyle(color = Neutral300))
            append(line[i].toString())
            pop()
            i++
        }
    }
}
