package top.topsea.simplediffusion.ui.scripts

import android.util.Log
import androidx.annotation.Keep
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import top.topsea.simplediffusion.R
import top.topsea.simplediffusion.ui.component.FitScreen
import top.topsea.simplediffusion.ui.component.ParamTitle
import top.topsea.simplediffusion.ui.theme.Pink80
import top.topsea.simplediffusion.util.TextUtil


@Keep
enum class XYZPlot {
    Nothing {
        override val type: Int = 0
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_1
        override val onlyImg = false
    },
    XYZ1 {
        override val type: Int = 1
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_2
        override val onlyImg = false
    },
    XYZ2 {
        override val type: Int = 2
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_3
        override val onlyImg = false
    },
    XYZ3 {
        override val type: Int = 3
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_4
        override val onlyImg = false
    },
    XYZ4 {
        override val type: Int = 4
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_5
        override val onlyImg = false
    },
    XYZ5 {
        override val type: Int = 5
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_6
        override val onlyImg = false
    },
    XYZ6 {
        override val type: Int = 6
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_7
        override val onlyImg = false
    },
    XYZ7 {
        override val type: Int = 7
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_8
        override val onlyImg = false
    },
    XYZ8 {
        override val type: Int = 8
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_9
        override val onlyImg = false
    },
    XYZ9 {
        override val type: Int = 9
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_10
        override val onlyImg = false
    },
    XYZ10 {
        override val type: Int = 10
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_11
        override val onlyImg = false
    },
    XYZ11 {
        override val type: Int = 11
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_12
        override val onlyImg = false
    },
    XYZ12 {
        override val type: Int = 12
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_13
        override val onlyImg = false
    },
    XYZ13 {
        override val type: Int = 13
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_14
        override val onlyImg = false
    },
    XYZ14 {
        override val type: Int = 14
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_15
        override val onlyImg = false
    },
    XYZ15 {
        override val type: Int = 15
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_16
        override val onlyImg = false
    },
    XYZ16 {
        override val type: Int = 16
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_17
        override val onlyImg = false
    },
    XYZ17 {
        override val type: Int = 17
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_18
        override val onlyImg = false
    },
    XYZ18 {
        override val type: Int = 18
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_19
        override val onlyImg = false
    },
    XYZ19 {
        override val type: Int = 19
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_20
        override val onlyImg = false
    },
    XYZ20 {
        override val type: Int = 10
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_21
        override val onlyImg = false
    },
    XYZ21 {
        override val type: Int = 21
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_22
        override val onlyImg = false
    },
    XYZ22 {
        override val type: Int = 22
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_23
        override val onlyImg = false
    },
    XYZ23 {
        override val type: Int = 23
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_24
        override val onlyImg = false
    },
    XYZ24 {
        override val type: Int = 24
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_25
        override val onlyImg = false
    },
    XYZ25 {
        override val type: Int = 25
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_26
        override val onlyImg = false
    },
    XYZ26 {
        override val type: Int = 26
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_27
        override val onlyImg = false
    },
    XYZ27 {
        override val type: Int = 27
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_28
        override val onlyImg = false
    },
    XYZ28 {
        override val type: Int = 28
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_29
        override val onlyImg = false
    },
    XYZ29 {
        override val type: Int = 29
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_30
        override val onlyImg = false
    },
    XYZ30 {
        override val type: Int = 30
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_31
        override val onlyImg = false
    },
    XYZ31 {
        override val type: Int = 31
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_32
        override val onlyImg = false
    },
    XYZ32 {
        override val type: Int = 32
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_33
        override val onlyImg = false
    },
    XYZ33 {
        override val type: Int = 33
        override var value: String = ""
        override val display: Int
            get() = R.string.xyz_34
        override val onlyImg = false
    };

    abstract val type: Int
    abstract val value: String
    abstract val display: Int
    abstract val onlyImg: Boolean
}

data class XYZ(
    val xType: Int = 0,
    val yType: Int = 0,
    val zType: Int = 0,
    val xValue: String = "",
    val yValue: String = "",
    val zValue: String = "",
    val value1: Boolean = true,
    val value2: Boolean = false,
    val value3: Boolean = false,
    val value4: Boolean = false,

    val margin: Int = 0,
) : Script()

@Composable
fun XYZPlotScript(
    script_args: MutableState<Script?>
) {
    if (script_args.value !is XYZ)
        script_args.value = XYZ()

    val xType = remember { mutableStateOf((script_args.value as XYZ).xType) }
    val yType = remember { mutableStateOf((script_args.value as XYZ).yType) }
    val zType = remember { mutableStateOf((script_args.value as XYZ).zType) }

    val xValue = remember { mutableStateOf((script_args.value as XYZ).xValue) }
    val yValue = remember { mutableStateOf((script_args.value as XYZ).yValue) }
    val zValue = remember { mutableStateOf((script_args.value as XYZ).zValue) }

    val value1 = remember { mutableStateOf((script_args.value as XYZ).value1) }     // 画注释
    val value2 = remember { mutableStateOf((script_args.value as XYZ).value2) }     // 保持随机值
    val value3 = remember { mutableStateOf((script_args.value as XYZ).value3) }     // 包含次级图像
    val value4 = remember { mutableStateOf((script_args.value as XYZ).value4) }     // 包含次级网格

    LaunchedEffect(xType.value, yType.value, zType.value, xValue.value, yValue.value, zValue.value, value1.value, value2.value, value3.value, value4.value) {
        TextUtil.topsea("changing script...", Log.ERROR)
        val xyz = XYZ(
            xType = xType.value, yType = yType.value, zType = zType.value,
            xValue = xValue.value, yValue = yValue.value, zValue = zValue.value,
            value1 = value1.value, value2 = value2.value, value3 = value3.value, value4 = value4.value,
        )
        script_args.value = xyz
    }

    XYZPlotChooseRow(
        title1 = stringResource(id = R.string.rs_xtype),
        title2 = stringResource(id = R.string.rs_xvalues),
        currType = xType,
        currValue = xValue,
    )
    XYZPlotChooseRow(
        title1 = stringResource(id = R.string.rs_ytype),
        title2 = stringResource(id = R.string.rs_yvalues),
        currType = yType,
        currValue = yValue,
    )
    XYZPlotChooseRow(
        title1 = stringResource(id = R.string.rs_ztype),
        title2 = stringResource(id = R.string.rs_zvalues),
        currType = zType,
        currValue = zValue,
    )
    XYZPlotCheckRow(
        title1 = stringResource(id = R.string.rs_xyz_check1),
        title2 = stringResource(id = R.string.rs_xyz_check2),
        title3 = stringResource(id = R.string.rs_xyz_check3),
        title4 = stringResource(id = R.string.rs_xyz_check4),
        value1 = value1,
        value2 = value2,
        value3 = value3,
        value4 = value4,
    )
    // 相互交换值
    XYZPlotChanges(
        {   // X 和 Y 的值交换
            val temp = xValue.value
            xValue.value = yValue.value
            yValue.value = temp
        },
        {
            // Y 和 Z 的值交换
            val temp = yValue.value
            yValue.value = zValue.value
            zValue.value = temp
        },
        {
            // X 和 Z 的值交换
            val temp = xValue.value
            xValue.value = zValue.value
            zValue.value = temp
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun XYZPlotChooseRow(
    title1: String,
    title2: String,
    currType: MutableState<Int>,
    currValue: MutableState<String>,
) {
    val types = XYZPlot.values()

    var menuExpanded by remember { mutableStateOf(false) }
    val onEditPrompt = remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() } //焦点
    val softKeyboard = LocalSoftwareKeyboardController.current //软键盘

    LaunchedEffect(key1 = onEditPrompt.value) {
        if (onEditPrompt.value) {
            delay(100) //延迟操作(关键点)
            focusRequester.requestFocus()
            softKeyboard?.show()
        }
    }

    val editingColor = if (onEditPrompt.value) {
        Pink80
    } else {
        MaterialTheme.colorScheme.primary
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        FitScreen(
            titleComp = {
                ParamTitle(
                    boldTitle = false,title = title1, isPad = it) }
        ) { modifier ->

            Box(
                modifier = modifier
                    .padding(start = 8.dp, top = 8.dp, end = 48.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.param_drop_menu))
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                        .clickable { menuExpanded = true },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(types[currType.value].display),
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        fontSize = 18.sp
                    )
                    Icon(
                        imageVector = if (menuExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Open menu.",
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .align(Alignment.CenterVertically),
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded, onDismissRequest = { menuExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth(.7f)
                ) {
                    types.forEach {
                        DropdownMenuItem(text = {
                            Text(
                                text = stringResource(id = it.display),
                                modifier = Modifier
                                    .widthIn(min = 72.dp),
                                maxLines = 1,
                            )
                        }, onClick = {
                            // TODO
                            currType.value = it.type
                            menuExpanded = false
                        })
                    }
                }
            }
        }
        FitScreen(
            modifier = Modifier,
            titleComp = {
                ParamTitle(
                    boldTitle = false,title = title2, isPad = it) }
        ) { modifier ->
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {

                LazyColumn(
                    modifier = Modifier
                        .padding(start = 8.dp, top = 8.dp)
                        .weight(1F)
                        .height(dimensionResource(id = R.dimen.script_value_height))
                        .border(2.dp, editingColor, RoundedCornerShape(8.dp))
                ) {
                    item {
                        BasicTextField(
                            value = currValue.value,
                            modifier = Modifier
                                .focusRequester(focusRequester)
                                .padding(8.dp)
                                .fillMaxSize(),
                            onValueChange = { currValue.value = it },
                            textStyle = TextStyle(
                                fontSize = TextUnit(
                                    value = 16F,
                                    type = TextUnitType.Sp
                                )
                            )
                        )
                    }
                }

                Button(
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
                    onClick = {
                        onEditPrompt.value = false
                    },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = stringResource(id = R.string.r_btn_confirm))
                }
            }
        }
    }
}

@Composable
fun XYZPlotCheckRow(
    title1: String,
    title2: String,
    title3: String,
    title4: String,
    value1: MutableState<Boolean>,
    value2: MutableState<Boolean>,
    value3: MutableState<Boolean>,
    value4: MutableState<Boolean>,
) {
    Column(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.param_drop_menu)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(.5f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ParamTitle(boldTitle = false, title = title1, isPad = false)
                Checkbox(checked = value1.value, onCheckedChange = { value1.value = it })
            }
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(.5f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ParamTitle(boldTitle = false, title = title2, isPad = false)
                Checkbox(checked = value2.value, onCheckedChange = { value2.value = it })
            }
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.param_drop_menu)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(.5f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ParamTitle(boldTitle = false, title = title3, isPad = false)
                Checkbox(checked = value3.value, onCheckedChange = { value3.value = it })
            }
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(.5f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ParamTitle(boldTitle = false, title = title4, isPad = false)
                Checkbox(checked = value4.value, onCheckedChange = { value4.value = it })
            }
        }
    }
}

@Composable
fun XYZPlotChanges(
    onChangeXY: () -> Unit,
    onChangeYZ: () -> Unit,
    onChangeXZ: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Button(onClick = { onChangeXY() }) {
            PlotExchange(stringResource(id = R.string.rs_xyz_changexy))
        }
        Button(onClick = { onChangeYZ() }) {
            PlotExchange(stringResource(id = R.string.rs_xyz_changeyz))
        }
        Button(onClick = { onChangeXZ() }) {
            PlotExchange(stringResource(id = R.string.rs_xyz_changexz))
        }
    }
}

@Composable
fun PlotExchange(
    title: String,
) {
    val witch = title.split("-")
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = witch[0],
            modifier = Modifier,
            fontSize = 15.sp,
        )
        Icon(
            painter = painterResource(id = R.drawable.arrows_exchange),
            contentDescription = "",
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .size(24.dp),
        )
        Text(
            text = witch[1],
            modifier = Modifier,
            fontSize = 15.sp,
        )
    }
}