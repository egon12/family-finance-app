package org.egon12.input

import androidx.compose.Composable
import androidx.ui.core.*
import androidx.ui.foundation.Clickable
import androidx.ui.layout.Container
import androidx.ui.layout.EdgeInsets
import androidx.ui.layout.Table
import androidx.ui.material.ripple.Ripple
import androidx.ui.text.ParagraphStyle
import androidx.ui.text.TextStyle
import androidx.ui.text.style.TextAlign
import androidx.ui.tooling.preview.Preview

@Composable
fun InputNumber(id: String, text: String, func: (String) -> Unit) {
    Ripple(bounded = true) {
        Clickable(onClick = { func(id) }) {
            Container(
                height = Dp(
                    64f
                ), expanded = true
            ) {
                Text(
                    text = text,
                    style = TextStyle(
                        fontSize = Sp(
                            24F
                        )
                    ),
                    paragraphStyle = ParagraphStyle(textAlign = TextAlign.Center)
                )
            }
        }
    }
}

val emptyFunc: (String) -> Unit = {}
/**
 * It had minimal height,
 * It had maximum height,
 * It should attach to bottom
 */
@Composable
fun GroupInputNumber(func: (String) -> Unit) {

    Table(
        4,
        { Alignment.Center }) {
        tableRow {
            InputNumber("1", "1", func)
            InputNumber("2", "2", func)
            InputNumber("3", "3", func)
            InputNumber("x", "x", func)
        }
        tableRow {
            InputNumber("4", "4", func)
            InputNumber("5", "5", func)
            InputNumber("6", "6", func)
            InputNumber(
                "",
                "",
                emptyFunc
            )
        }
        tableRow {
            InputNumber("7", "7", func)
            InputNumber("8", "8", func)
            InputNumber("9", "9", func)
            InputNumber(
                "",
                "",
                emptyFunc
            )
        }
        tableRow {
            InputNumber(".", ".", func)
            InputNumber("0", "0", func)
            InputNumber("00", "00", func)
            InputNumber("000", "000", func)
        }
    }
}

@Composable
fun InputValue(text: String) {
    Container(
        expanded = true,
        height = Dp(64f),
        alignment = Alignment.CenterRight,
        padding = EdgeInsets(16.dp, 16.dp, 16.dp, 16.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(fontSize = 24.sp),
            paragraphStyle = ParagraphStyle(textAlign = TextAlign.Right)
        )
    }
}

@Preview
@Composable
fun DefaultPreview() {

}