package com.kindeev.swipelauncher.presentation.entities

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

object PhoneNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val mask = "8 (000) 000-00-00"
        val out = when {
            text.length <= 1 -> text.text
            text.length <= 3 -> "${text[0]} (${text.substring(1)}"
            text.length == 4 -> "${text[0]} (${text.substring(1)})"
            text.length <= 7 -> "${text[0]} (${text.substring(1, 4)}) ${text.substring(4)}"
            text.length <= 9 -> "${text[0]} (${text.substring(1, 4)}) ${
                text.substring(4, 7)
            }-${text.substring(7)}"

            text.length <= 11 -> "${text[0]} (${text.substring(1, 4)}) ${
                text.substring(4, 7)
            }-${text.substring(7, 9)}-${text.substring(9)}"

            else -> ""
        }
        val offsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset.returnOffsetTransformed()
                if (offset <= 3) return (offset + 2).returnOffsetTransformed()
                if (offset == 4) return (offset + 3).returnOffsetTransformed()
                if (offset <= 7) return (offset + 4).returnOffsetTransformed()
                if (offset <= 9) return (offset + 5).returnOffsetTransformed()
                if (offset <= 11) return (offset + 6).returnOffsetTransformed()
                return 17.returnOffsetTransformed()
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 1) return offset.returnOffsetOriginal()
                if (offset <= 3) return 1.returnOffsetOriginal()
                if (offset <= 6) return (offset - 3).returnOffsetOriginal()
                if (offset <= 8) return 4.returnOffsetOriginal()
                if (offset <= 11) return (offset - 4).returnOffsetOriginal()
                if (offset == 12) return 7.returnOffsetOriginal()
                if (offset <= 14) return (offset - 5).returnOffsetOriginal()
                if (offset == 15) return 9.returnOffsetOriginal()
                if (offset <= 17) return (offset - 6).returnOffsetOriginal()
                return 17.returnOffsetOriginal()
            }

            fun Int.returnOffsetTransformed() = if (out.length < this) out.length else this
            fun Int.returnOffsetOriginal() = if (text.length < this) text.length else this
        }
        return TransformedText(
            AnnotatedString.Builder().run {
                append(out)
                if (out.length <= 17) {
                    pushStyle(SpanStyle(color = Color.LightGray))
                    append(mask.substring(out.length))
                }
                toAnnotatedString()
            }, offsetTranslator
        )
    }
}