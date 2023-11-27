package top.topsea.simplediffusion.ui.component

import androidx.annotation.Keep
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import top.topsea.simplediffusion.R
import top.topsea.simplediffusion.util.Constant.addablePattern

@Composable
fun ClickableMessage(
    prompt: MutableState<String>,
    onEditPrompt: MutableState<Boolean>,
    onClickItem: (Int, Int) -> Unit
) {
    val styledMessage = promptFormatter(
        text = prompt.value,
    )

    ClickableText(
        text = styledMessage,
        style = MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current),
        modifier = Modifier.padding(12.dp),
        onClick = {
            styledMessage
                .getStringAnnotations(start = it, end = it)
                .firstOrNull()?.let { annotation ->
                    when (annotation.tag) {
                        SymbolAnnotationType.DEFAULT.name -> onEditPrompt.value = true
                        else -> onClickItem(annotation.start, annotation.end)
                    }
                }
        }
    )
}
@Composable
fun ClickableMessage(
    prompt: String,
    onEditPrompt: MutableState<Boolean>,
    onClickItem: (Int, Int) -> Unit
) {
    val styledMessage = promptFormatter(
        text = prompt,
    )

    ClickableText(
        text = styledMessage,
        style = MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current),
        modifier = Modifier.padding(12.dp),
        onClick = {
            styledMessage
                .getStringAnnotations(start = it, end = it)
                .firstOrNull()?.let { annotation ->
                    when (annotation.tag) {
                        SymbolAnnotationType.DEFAULT.name -> onEditPrompt.value = true
                        else -> onClickItem(annotation.start, annotation.end)
                    }
                }
        }
    )
}

@Keep
enum class SymbolAnnotationType {
    DEFAULT, ADDABLE
}
typealias StringAnnotation = AnnotatedString.Range<String>
// Pair returning styled content and annotation for ClickableText when matching syntax token
typealias SymbolAnnotation = Pair<AnnotatedString, StringAnnotation?>

@Composable
fun promptFormatter(
    text: String,
    isEditing: Boolean = false,
): AnnotatedString {
    val addablePrompts = addablePattern.findAll(text)
    val addPrompt = stringResource(id = R.string.change_default_prompt)

    return buildAnnotatedString {
        var cursorPosition = 0

        for (token in addablePrompts) {
            append(text.slice(cursorPosition until token.range.first))

            val (annotatedString, stringAnnotation) = getAddableAnnotation(
                matchResult = token,
                colorScheme = MaterialTheme.colorScheme
            )
            append(annotatedString)

            if (stringAnnotation != null) {
                val (item, start, end, tag) = stringAnnotation
                addStringAnnotation(tag = tag, start = start, end = end, annotation = item)
            }

            cursorPosition = token.range.last + 1
        }

        if (!addablePrompts.none()) {
            append(text.slice(cursorPosition..text.lastIndex))
        } else {
            append(text)
        }

        // 添加默认提示词
        if (!isEditing) {
            val (annotatedString, stringAnnotation) = getEditAnnotation(
                matchResult = addPrompt,
                colorScheme = MaterialTheme.colorScheme,
                start = text.length,
                end = text.length + addPrompt.length
            )
            append(annotatedString)
            if (stringAnnotation != null) {
                val (item, start, end, tag) = stringAnnotation
                addStringAnnotation(tag = tag, start = start, end = end, annotation = item)
            }
        }
    }
}

private fun getEditAnnotation(
    matchResult: String,
    colorScheme: ColorScheme,
    start: Int,
    end: Int
): SymbolAnnotation {
    return SymbolAnnotation(
        AnnotatedString(
            text = matchResult,
            spanStyle = SpanStyle(
                color = colorScheme.inversePrimary,
                fontStyle = FontStyle.Italic
            )
        ),
        StringAnnotation(
            item = matchResult,
            start = start,
            end = end,
            tag = SymbolAnnotationType.DEFAULT.name
        )
    )
}

private fun getAddableAnnotation(
    matchResult: MatchResult,
    colorScheme: ColorScheme
): SymbolAnnotation {
    return SymbolAnnotation(
        AnnotatedString(
            text = matchResult.value,
            spanStyle = SpanStyle(
                color = colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        ),
        StringAnnotation(
            item = matchResult.value,
            start = matchResult.range.first,
            end = matchResult.range.last,
            tag = SymbolAnnotationType.ADDABLE.name
        )
    )
}