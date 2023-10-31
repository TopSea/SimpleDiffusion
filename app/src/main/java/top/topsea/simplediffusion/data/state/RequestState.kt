package top.topsea.simplediffusion.data.state

import top.topsea.simplediffusion.data.param.BasicParam
import top.topsea.simplediffusion.data.param.ImgParam
import top.topsea.simplediffusion.data.param.TxtParam

data class ParamLocalState (
    val tParams: List<TxtParam> = emptyList(),
    val iParams: List<ImgParam> = emptyList(),
    val image: String = "",
    val currParam: BasicParam? = null,
    val editingCurr: Boolean = false,
    val editing: Boolean = false,
    val editingParam: BasicParam? = null,
)
