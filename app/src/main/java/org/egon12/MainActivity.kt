package org.egon12

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.Model
import androidx.ui.core.*
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.text.TextStyle
import androidx.ui.tooling.preview.Preview
import org.egon12.input.GroupInputNumber
import org.egon12.input.InputValue

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val model = MainViewModel("0")

        setContent {
            MaterialTheme { App(model) }
        }
    }
}


@Composable
fun App(model: MainViewModel) {

    Stack {
        aligned(alignment = Alignment.TopCenter) {
            Text(
                text = "Expenses",
                style = TextStyle(fontSize = 24.sp)
            )
        }

        expanded {
            HeightSpacer(height = 0.dp)
        }

        aligned(alignment = Alignment.BottomCenter) {
            Column {
                InputValue(text = model.typedText)
                GroupInputNumber(model::insertText)
            }
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MaterialTheme {
        App(MainViewModel("1,000,000"))
    }
}

@Model
class MainViewModel(
    var typedText: String = ""
) {
    fun insertText(input: String) {
        if (input == "x") {
            if (typedText.isNotEmpty())
                typedText = typedText.substring(0, typedText.length - 1)
        } else {
            typedText += input
        }
    }
}