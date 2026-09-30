package com.example.helloapp.features.counter

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.helloapp.ui.components.CardText
import com.example.helloapp.ui.components.MyCard

@Composable
fun CounterCard(
    index: Int,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    MyCard(
        onClick = onClick,
        modifier = modifier
    ) {
        CardText(text = "Clicks: $index: ")

        AnimatedContent(
            targetState = count,
            modifier = Modifier.widthIn(min = 40.dp),
            transitionSpec = {
                (slideInVertically { height -> -height } + fadeIn()) togetherWith
                        slideOutVertically { height -> height } + fadeOut()
            },
            label = "CounterAnimation"
        ) { animatedCount ->
            CardText( text = "$animatedCount" )
        }
    }
}
