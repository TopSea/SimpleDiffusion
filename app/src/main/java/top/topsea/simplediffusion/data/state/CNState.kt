package top.topsea.simplediffusion.data.state

import top.topsea.simplediffusion.api.dto.ControlTypes
import top.topsea.simplediffusion.data.param.CNParam


data class ControlNetState (
    val cnParams: List<CNParam> = emptyList(),
    val controlTypes: List<Pair<String, ControlTypes>> = emptyList(),
    val currControlType: ControlTypes = ControlTypes(emptyList(), emptyList(), "", ""),
    val currType: String = "All",
    val image: String = "",
    val version: Int = -1,
    val maxModelsNum: Int = -1,
    val editParam: CNParam? = null,
    val editing: Boolean = false,
)
