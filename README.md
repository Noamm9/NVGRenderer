# NvgRenderer

NanoVG helpers for Fabric `1.21.11`.

The library is aimed at client-side HUD, menu, and overlay rendering. It wraps the NanoVG frame setup used by
Minecraft's picture-in-picture GUI path and adds a small set of drawing, text, image, and coordinate helpers.

## Requirements

- Minecraft `1.21.11`
- Fabric Loader `0.18.4+`
- Fabric API `0.140.2+1.21.11`
- Fabric Language Kotlin `1.13.9+kotlin.2.3.10`
- Java `21`

## Main API

[`NVG`](src/main/kotlin/com/github/noamm9/nvgrenderer/nvg/NVG.kt) is the drawing entry point.

```kotlin
NVG.rect(20, 20, 120, 40, Color(20, 20, 20, 180), 8f)
NVG.hollowRect(20, 20, 120, 40, 1f, Color.WHITE, 8f)
NVG.line(20, 70, 180, 70, 2f, Color(255, 255, 255, 180))
NVG.circle(200, 70, 10f, Color(80, 200, 255))
NVG.gradientRect(
    20, 90, 160, 42,
    Color(73, 140, 255),
    Color(88, 235, 180),
    Gradient.LEFT_RIGHT,
    10f
)
NVG.dropShadow(20, 150, 180, 60, 20f, 6f, 12f)
```

Transforms and clipping:

```kotlin
NVG.push()
NVG.translate(40, 30)
NVG.scale(1.25f, 1.25f)

NVG.pushScissor(0, 0, 120, 60)
NVG.rect(0, 0, 120, 60, Color(0, 0, 0, 120), 8f)
NVG.popScissor()

NVG.pop()
```

## Text

[`NvgText`](src/main/kotlin/com/github/noamm9/nvgrenderer/helpers/NvgText.kt) wraps common text drawing and strips
Minecraft formatting codes before measuring or rendering.

```kotlin
NvgText.draw("Hello", 24f, 24f, Color.WHITE, 16f)
NvgText.draw("Centered", 120f, 50f, Color.WHITE, 14f, align = NvgText.Align.CENTER)
NvgText.draw("Shadow", 24f, 72f, Color.WHITE, 14f, shadow = true)
NvgText.drawGradient(
    "Gradient",
    24f,
    96f,
    Color.WHITE,
    Color(255, 220, 120),
    18f
)
```

## Images

[`Image`](src/main/kotlin/com/github/noamm9/nvgrenderer/nvg/Image.kt) and `NVG.createImage(...)` support:

- classpath resources
- absolute or relative file paths
- `http://` and `https://` URLs
- SVG input

```kotlin
val icon = NVG.createImage("assets/nvgrenderer/icon.png")
val svg = NVG.createImage("https://example.com/logo.svg")

NVG.image(icon, 20f, 20f, 32f, 32f, 8f)
NVG.image(svg, 60f, 20f, 32f, 32f)

NVG.deleteImage(icon)
NVG.deleteImage(svg)
```

Notes:

- `createImage(...)` is reference-counted. If you keep the returned handle, call `deleteImage(...)` when you are done.
- `image(path, ...)` is fine for stable assets, but it caches by path. Don't feed it a stream of unique URLs every
  frame.
- Loading images from disk or the network inside a render loop is a bad idea. Preload them.

You can also wrap an existing GL texture with `createNVGImage(...)`.

## GuiGraphics Integration

[`NVGPIP`](src/main/kotlin/com/github/noamm9/nvgrenderer/nvg/NVGPIP.kt) exposes a `GuiGraphics.drawNVG { ... }`
extension.
It respects the current `GuiGraphics` transform and scissor state, and it draws in normal Minecraft GUI coordinates.

```kotlin
override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
    super.render(guiGraphics, mouseX, mouseY, partialTick)

    guiGraphics.drawNVG {
        NVG.rect(0, 0, 220, 120, Color(15, 18, 24, 210), 14f)
        NvgText.draw("NVG inside GuiGraphics", 16f, 18f, Color.WHITE, 16f)
    }
}
```

## Demo Screen

Press `F8` in-game to open the built-in demo screen. It exercises:

- primitive shapes
- gradients
- text helpers
- scissor clipping
- translated and scaled hover math
- PNG and SVG image loading
- world-to-screen projection against your current crosshair target

## Mouse Coordinates

[`MouseStack`](src/main/kotlin/com/github/noamm9/nvgrenderer/helpers/MouseStack.kt) mirrors simple 2D GUI transforms so
hover checks can use local coordinates.

```kotlin
val mouse = MouseStack()

mouse.push()
mouse.translate(panelX, panelY)
mouse.scale(1.5f)

val local = mouse.toLocal(screenMouseX, screenMouseY)
val hovered = local.first in 0.0 .. 120.0 && local.second in 0.0 .. 40.0

mouse.pop()
```

## World To Screen

[`WorldToScreen`](src/main/kotlin/com/github/noamm9/nvgrenderer/helpers/WorldToScreen.kt) projects a world position into
GUI-space coordinates.

```kotlin
val point = WorldToScreen.project(entity.position())
if (point != null && point.onScreen) {
    NVG.circle(point.x, point.y, 4f, Color.RED)
}
```

## License

Unlicense. See [`LICENSE.txt`](LICENSE.txt).

## Credits

Parts of the rendering approach were inspired by [Odin (Fabric)](https://github.com/odtheking/Odin).
