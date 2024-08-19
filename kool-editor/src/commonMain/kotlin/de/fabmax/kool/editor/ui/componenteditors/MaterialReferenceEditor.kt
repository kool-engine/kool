package de.fabmax.kool.editor.ui.componenteditors

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.SetComponentDataAction
import de.fabmax.kool.editor.actions.SetMaterialAction
import de.fabmax.kool.editor.actions.fused
import de.fabmax.kool.editor.components.MaterialComponent
import de.fabmax.kool.editor.components.MaterialReferenceComponent
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.ui.*
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.launchOnMainThread
import kotlin.reflect.KClass

class MaterialReferenceEditor : ComponentEditor<MaterialReferenceComponent>() {

    private val material: MaterialComponent get() = components[0].material!!

    override fun UiScope.compose() {
        val allTheSameMaterial = components.all {
            it.material?.dataState?.use()
            it.dataState.use().materialId == components[0].data.materialId
        }
        componentPanel(
            title = "Material",
            imageIcon = Icons.small.palette,
            onRemove = ::removeComponent,
            titleWidth = sizes.baseSize * 2.3f,
            scopeName = components[0].material?.name ?: "default-material",
            headerContent = {
                val (items, idx) = makeMaterialItemsAndIndex(allTheSameMaterial)
                val dndHandler = rememberMaterialDndHandler(components, uiNode)
                ComboBox {
                    defaultComboBoxStyle()

                    val dndState = getDndHoverState(dndHandler, colors.componentBg)
                    if (dndState.isDndInProgress) {
                        modifier.border(RoundRectBorder(dndState.borderColor, sizes.smallGap, sizes.borderWidth))
                        if (dndState.isHover) {
                            modifier.colors(textBackgroundColor = dndState.bgColor, expanderColor = colors.dndAcceptableBgHovered)
                        } else {
                            modifier.colors(textBackgroundColor = dndState.bgColor, expanderColor = colors.dndAcceptableBg)
                        }
                    }

                    modifier
                        .margin(end = sizes.gap)
                        .width(Grow.Std)
                        .alignY(AlignmentY.Center)
                        .items(items)
                        .selectedIndex(idx)
                        .onItemSelected { index ->
                            if (allTheSameMaterial || index > 0) {
                                launchOnMainThread {
                                    val setMaterial = items[index].getMaterial()
                                    components
                                        .map { SetMaterialAction(it, setMaterial) }
                                        .fused().apply()
                                }
                            }
                        }
                }
            }
        ) {
            modifier.padding(0.dp)

            if (allTheSameMaterial) {
                val checkedMaterial = components[0].material ?: return@componentPanel Unit
                labeledTextField("Name:", checkedMaterial.name, labelWidth = sizes.editorLabelWidthSmall) {
                    val oldData = checkedMaterial.data
                    val newData = oldData.copy(name = it)
                    SetComponentDataAction(checkedMaterial, oldData, newData).apply()
                }

                labeledCombobox("Type:", materialTypes, materialTypes.indexOfFirst { it.matches(checkedMaterial.shaderData) }) {
                    if (!it.matches(checkedMaterial.shaderData)) {
                        val oldData = checkedMaterial.data
                        val newData = oldData.copy(shaderData = it.factory())
                        SetComponentDataAction(checkedMaterial, oldData, newData).apply()
                    }
                }

                menuDivider()
                materialEditor()
            }
        }
    }

    private fun ColumnScope.materialEditor() = Column(width = Grow.Std, scopeName = material.shaderData::class.simpleName) {
        val matEditor: MaterialDataEditor<*> = when (val matData = material.shaderData) {
            is BlinnPhongShaderData -> TODO()
            is PbrShaderData -> {
                remember { PbrMaterialEditor(material, matData, this@MaterialReferenceEditor) }
                    .apply { materialData = matData }
            }
            is UnlitShaderData -> TODO()
            is PbrSplatShaderData -> {
                remember { PbrSplatMaterialEditor(material, matData, this@MaterialReferenceEditor) }
                    .apply { materialData = matData }
            }
        }
        with(matEditor) { materialEditor() }
    }

    private fun UiScope.rememberMaterialDndHandler(
        materialRefs: List<MaterialReferenceComponent>,
        dropTarget: UiNode
    ): MaterialDndHandler {
        val handler = remember { MaterialDndHandler(materialRefs, dropTarget) }
        KoolEditor.instance.ui.dndController.registerHandler(handler, surface)
        return handler
    }

    private class MaterialDndHandler(val materialRefs: List<MaterialReferenceComponent>, dropTarget: UiNode) :
        DndHandler(dropTarget, setOf(DndItemFlavor.DndItemMaterial))
    {
        override fun onMatchingReceive(
            dragItem: EditorDndItem<*>,
            dragPointer: PointerEvent,
            source: DragAndDropHandler<EditorDndItem<*>>?
        ) {
            val dragMaterial = dragItem.get(DndItemFlavor.DndItemMaterial)
            val changeMats = materialRefs.filter { it.materialId != dragMaterial.id }
            if (changeMats.isNotEmpty()) {
                changeMats.map { SetMaterialAction(it, dragMaterial) }.fused().apply()
            }
        }
    }

    private fun UiScope.makeMaterialItemsAndIndex(allTheSameMaterial: Boolean): Pair<List<MaterialItem>, Int> {
        val items = mutableListOf(
            MaterialItem("Default", null),
            MaterialItem("New material", null)
        )

        if (!allTheSameMaterial) {
            items.add(0, MaterialItem("", null))
        }

        var index = 0
        KoolEditor.instance.projectModel.materials.use()
            .filter { it.gameEntity.isVisible }
            .forEachIndexed { i, material ->
                if (allTheSameMaterial && components[0].isHoldingMaterial(material)) {
                    index = i + 2
                }
                items += MaterialItem(material.name, material)
            }
        return items to index
    }

    private class MaterialItem(val itemText: String, val material: MaterialComponent?) {
        override fun toString(): String = itemText

        suspend fun getMaterial(): MaterialComponent? {
            return material ?: if (itemText == "New material") KoolEditor.instance.projectModel.createNewMaterial() else null
        }
    }

    private class MaterialTypeOption<T: MaterialShaderData>(val label: String, val dataType: KClass<T>, val factory: () -> T) {
        fun matches(data: MaterialShaderData): Boolean = dataType.isInstance(data)
        override fun toString() = label
    }

    companion object {
        private val materialTypes = listOf(
            MaterialTypeOption("PBR", PbrShaderData::class) { PbrShaderData() },
            MaterialTypeOption("Splatted PBR", PbrSplatShaderData::class) { PbrSplatShaderData() },
        )
    }
}