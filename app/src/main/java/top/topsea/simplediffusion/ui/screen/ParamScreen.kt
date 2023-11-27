package top.topsea.simplediffusion.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import top.topsea.simplediffusion.R
import top.topsea.simplediffusion.data.param.CNParam
import top.topsea.simplediffusion.data.param.ImgParam
import top.topsea.simplediffusion.data.param.TxtParam
import top.topsea.simplediffusion.data.state.ControlNetState
import top.topsea.simplediffusion.data.state.ParamLocalState
import top.topsea.simplediffusion.data.state.UIEvent
import top.topsea.simplediffusion.data.viewmodel.UIViewModel
import top.topsea.simplediffusion.event.ControlNetEvent
import top.topsea.simplediffusion.event.ParamEvent
import top.topsea.simplediffusion.ui.tab.CNParamTab
import top.topsea.simplediffusion.ui.tab.ParamTab


@Composable
fun ParamScreen(
    navController: NavController,
    paramState: ParamLocalState,
    uiViewModel: UIViewModel,
    cnState: ControlNetState,
    paramEvent: (ParamEvent) -> Unit,
    cnEvent: (ControlNetEvent) -> Unit,
) {
    val titles = listOf(stringResource(id = R.string.r_request_card_a1), stringResource(id = R.string.r_request_card_a2), stringResource(id = R.string.r_request_card_a3))

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = uiViewModel.paramTab, modifier = Modifier.fillMaxWidth()) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = uiViewModel.paramTab == index,
                    onClick = { uiViewModel.onEvent(UIEvent.ChangeParamTab(index)) },
                    text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) }
                )
            }
        }
        when (uiViewModel.paramTab) {
            0 -> {
                ParamTab(
                    navController = navController,
                    params = paramState.tParams,
                    uiViewModel = uiViewModel,
                    paramEvent = paramEvent,
                    cnEvent = cnEvent,
                ) {
                    paramEvent(
                        ParamEvent.AddParam(
                            TxtParam(
                                priority_order = uiViewModel.tDisplayPri,
                                baseModel = uiViewModel.tSDModel,
                                refinerModel = uiViewModel.tRefineModel,
                                refinerAt = uiViewModel.tRefineAt,
                                defaultPrompt = uiViewModel.tPrompt,
                                defaultNegPrompt = uiViewModel.tNPrompt,
                                width = uiViewModel.tImgWidth,
                                height = uiViewModel.tImgHeight,
                                steps = uiViewModel.tSteps,
                                cfgScale = uiViewModel.tCFG,
                                sampler_index = uiViewModel.tSampler,
                                batch_size = uiViewModel.tBatchSize,
                            )
                        )
                    )
                }
            }
            1 -> {
                ParamTab(
                    navController = navController,
                    isi2i = true,
                    params = paramState.iParams,
                    uiViewModel = uiViewModel,
                    paramEvent = paramEvent,
                    cnEvent = cnEvent,
                ){
                    paramEvent(ParamEvent.AddParam(ImgParam()))
                }
            }
            2 -> {
                CNParamTab(
                    navController = navController,
                    cnModels = cnState.cnParams,
                    uiViewModel = uiViewModel,
                    paramState = paramState,
                    paramEvent = paramEvent,
                    cnEvent = cnEvent
                ){
                    cnEvent(ControlNetEvent.AddCNParam(CNParam()))
                }
            }
        }
    }
}