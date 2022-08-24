package com.fly.calulator

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fly.calulator.logic.util.CalculatorResponse
import com.fly.calulator.logic.util.CalculatorUtil
import com.fly.calulator.logic.util.InputStatus
import com.fly.calulator.ui.theme.FlyCalulatorTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlyCalulatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ShowAndKeyboard()
                }
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = !isSystemInDarkTheme()
                val primaryColor = MaterialTheme.colorScheme.primaryContainer
                DisposableEffect(systemUiController, useDarkIcons) {
                    // Update all of the system bar colors to be transparent, and use
                    // dark icons if we're in light theme
                    systemUiController.setSystemBarsColor(
                        color = primaryColor,
                        darkIcons = useDarkIcons
                    )
                    systemUiController.setStatusBarColor(
                        color = primaryColor,
                        darkIcons = useDarkIcons
                    )
                    // setStatusBarColor() and setNavigationBarColor() also exist

                    onDispose {}
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowAndKeyboard(){
    val configuration = LocalConfiguration.current
    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            ShowAndKeyboardLandscape()
        }
        else -> {
            ShowAndKeyboardPortrait()
        }
    }
}

@Composable
fun ShowAndKeyboardLandscape(){
    var nowResult by remember{ mutableStateOf(CalculatorResponse(status = InputStatus.HANDLE_SUCCESS)) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
    ) {
        CalculatorButton(
            modifier = Modifier.weight(0.2f),
            onClick = {
                nowResult = CalculatorUtil.handleInput(
                    nowContent = nowResult.result,
                    newClick = "E"
                )
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(text = "=", fontSize = 25.sp, fontWeight = FontWeight.Bold)
        }
        Box(modifier = Modifier.weight(0.6f), propagateMinConstraints = true){ ShowResult(nowResult = nowResult) }
        Box(modifier = Modifier.weight(0.46f), propagateMinConstraints = true){
            NumberKeyboard(
                clickedString = {
                    nowResult = CalculatorUtil.handleInput(
                        nowContent = nowResult.result,
                        newClick = it
                    )
                    Log.d("MainRes", nowResult.result.toString())
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowAndKeyboardPortrait(){
    var nowResult by remember{ mutableStateOf(CalculatorResponse(status = InputStatus.HANDLE_SUCCESS)) }
    Box(modifier = Modifier.fillMaxSize()){
        SmallTopAppBar(
            title = { Text(text = "计算器") },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Bottom
        ) {
            ShowResult(nowResult = nowResult)
            NumberKeyboard(
                clickedString = {
                    nowResult = CalculatorUtil.handleInput(
                        nowContent = nowResult.result,
                        newClick = it
                    )
                    Log.d("MainRes", nowResult.result.toString())
                }
            )
        }
    }

}

@Composable
fun ShowResult(modifier: Modifier=Modifier,nowResult: CalculatorResponse){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.End
    ) {
       AdjustText(text = nowResult.result.joinToString(separator = ""), fontSize = 54.sp)
    }
}

/**
 * @param clickedString C->归零 B->回退 E->等号
 */
@Composable
fun NumberKeyboard(
    clickedString: (result: String) -> Unit,
){
    val keyBoardTextList = listOf(
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "-"),
        listOf("1", "2", "3", "+"),
        listOf("00", "0", ".", "="),
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            CalculatorButton(onClick = { clickedString("C") }) {
                Text(text = "C", fontSize = 25.sp, fontWeight = FontWeight.Bold)
            }
            CalculatorButton(onClick = { clickedString("B") }) {
                Icon(Icons.Outlined.Backspace, null)
            }
            CalculatorButton(onClick = { clickedString("%") }) {
                Text(text = "%", fontSize = 25.sp, fontWeight = FontWeight.Bold)
            }
            CalculatorButton(onClick = { clickedString("÷") }) {
                Text(text = "÷", fontSize = 25.sp, fontWeight = FontWeight.Bold)
            }

        }
        keyBoardTextList.forEach{ singleRow ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                singleRow.forEach {
                    if (it!="="){
                        CalculatorButton(onClick = { clickedString(it) }) {
                            Text(text = it, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                        }
                    }else{
                        CalculatorButton(
                            onClick = { clickedString("E") },
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(text = it, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RowScope.CalculatorButton(
    modifier: Modifier = Modifier,
    onClick: ()-> Unit,
    containerColor:Color = Color(0x6DDDDDDD),
    content: @Composable RowScope.() -> Unit,
){
    val clicked = remember { mutableStateOf(false) }
    val scale = animateFloatAsState(if (clicked.value) 0.8f else 1f)
    val scope = rememberCoroutineScope()
    TextButton(
        modifier = modifier
            .padding(4.dp)
            .scale(scale.value)
            .weight(1f)
            .aspectRatio(1f)
            .pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        clicked.value = true
                        scope.launch {
                            delay(2000)
                            clicked.value = false
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        clicked.value = false
                        onClick()
                    }
                }
                true
            },
        shape = RoundedCornerShape(10.dp),
        onClick = onClick,
        content = content,
        colors = ButtonDefaults.textButtonColors(containerColor = containerColor),
    )
}

@Composable
fun AdjustText(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit
){
    var fontSizeNow by remember { mutableStateOf(fontSize.value) }
    var readyToDraw by remember { mutableStateOf(false) }
    val fontSizeAnimate = animateFloatAsState(fontSizeNow)
    if (text.isBlank()){ fontSizeNow = fontSize.value }
    Text(
        text = text,
        maxLines = 1,
        softWrap = false,
        fontSize = fontSizeAnimate.value.sp,
        modifier = modifier.drawWithContent {
            if (readyToDraw) drawContent()
        },
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth) {

                fontSizeNow *= 0.9f
            } else {
                readyToDraw = true
            }
        }
    )
}

@Preview(showBackground = true, device = "id:Nexus S")
@Composable
fun DefaultPreview() {
    FlyCalulatorTheme {
        ShowAndKeyboardPortrait()
    }
}