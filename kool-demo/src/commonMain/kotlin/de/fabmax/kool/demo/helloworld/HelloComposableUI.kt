package de.fabmax.kool.demo.helloworld

import androidx.compose.runtime.*
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.compose.ExperimentalKoolComposeAPI
import de.fabmax.kool.modules.compose.addComposableSurface
import de.fabmax.kool.modules.compose.composables.layout.Column
import de.fabmax.kool.modules.compose.composables.layout.Row
import de.fabmax.kool.modules.compose.composables.rendering.Text
import de.fabmax.kool.modules.compose.composables.toolkit.*
import de.fabmax.kool.modules.compose.modifiers.fillMaxSize
import de.fabmax.kool.modules.compose.modifiers.fillMaxWidth
import de.fabmax.kool.modules.compose.modifiers.size
import de.fabmax.kool.modules.ui2.dp
import de.fabmax.kool.modules.ui2.setupUiScene
import de.fabmax.kool.pipeline.ClearColorFill
import de.fabmax.kool.scene.Scene
import me.dvyy.compose.mini.modifier.Modifier

class HelloComposableUI : DemoScene("Composable UI") {
    override fun Scene.setupMainScene(ctx: KoolContext) {
        setupUiScene(ClearColorFill(Scene.DEFAULT_CLEAR_COLOR))

        @OptIn(ExperimentalKoolComposeAPI::class)
        addComposableSurface {
            FloatingWindow("Sample UI Elements", Vec2f(50f, 50f), layer = 300) {
                var buttonText by remember { mutableStateOf(1) }
                Button(onClick = { buttonText += 1 }) {
                    Text("Counter: $buttonText")
                }
                var checked by remember { mutableStateOf(false) }
                Row {
                    Switch(checked, onCheckedChange = { checked = it })
                    Checkbox(checked, onCheckedChange = { checked = it })
                    RadioButton(checked, onCheckedChange = { checked = it })
                }
                var value by remember { mutableStateOf(1f) }
                Slider(value, onValueChange = { value = it }, range = 0f..10f)

                var expanded by remember { mutableStateOf(false) }
                val items = listOf("Item 1", "Item 2", "Item 3", "Item with longer title")
                var selected by remember { mutableStateOf(0) }
                DropdownButton(onClick = { expanded = !expanded }) {
                    Text(items[selected])
                }
                DropdownMenu(expanded, onDismissRequest = { expanded = false }) {
                    items.forEachIndexed { index, item ->
                        key(item) {
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = { selected = index; expanded = false }
                            )
                        }
                    }
                }

                var text by remember { mutableStateOf("Hello World") }
                TextField(text, onValueChange = { text = it }, modifier = Modifier.fillMaxWidth())
            }

            FloatingWindow("Scroll container", Vec2f(250f, 50f), layer = 100) {
                ScrollArea(modifier = Modifier.size(200.dp, 300.dp)) {
                    Column(Modifier.fillMaxSize()) {
                        repeat(100) {
                            Text("Hello World $it")
                        }
                        Text("Some item with really long text as an example.")
                    }
                }
            }
        }
    }
}
