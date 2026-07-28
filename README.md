# Drawing Studio Pro

A desktop Java Swing application for composing scenes from a media library and
freehand drawing, side by side. One canvas ("Composition") lets you drag
animal, flower, and image assets onto a workspace and transform them; the
other ("Drawing Pad") is a simple paint canvas with a pen and eraser.

## Features

### Composition Canvas (left / "Composition")

- **Drag and drop** — drag any thumbnail from the media library (animal,
  flower, or custom image) straight onto the canvas to place it.
- **Move** — click and drag a placed item anywhere on the canvas.
- **Rotate** — drag the rotate handle that appears above a selected item to
  spin it freely; **Rotate** in the toolbar rotates every item on the canvas
  90° at once.
- **Resize** — drag any of the four corner handles on a selected item to
  scale it up or down.
- **Flip** — flip the selected item horizontally or vertically from the
  toolbar.
  
  Move, rotate, resize, and flip all work the same way for animals, flowers,
  and custom images — there's no longer any per-type restriction.
- **Compose** — flattens every item currently on the canvas into a single
  background image, so you can keep layering new items on top without losing
  earlier work. The flattened layer can't be deleted (only replaced by
  clearing the canvas), but it can still be moved, resized, and flipped like
  anything else.
- **Delete** — removes the selected item from the canvas.
- **Clear Composition** — wipes the canvas after a confirmation prompt.
- **Save** — save the composition either into the media library (it shows up
  immediately in the Images tab) or as a PNG file anywhere on disk.

### Drawing Pad (right / "Drawing Pad")

- **Pen** — freehand drawing tool.
- **Eraser** — erases in the same freehand style as the pen.
- **Color picker** — pick from a palette of preset colors.
- **Pen size** — adjustable stroke width (1–30px).
- **Save** — save the drawing either into the media library or as a PNG file
  anywhere on disk.
- **Clear Drawing** — wipes the canvas after a confirmation prompt.

### Media Library (left sidebar)

- **Animals / Flowers / Images tabs** — each tab shows draggable thumbnails
  loaded from disk (`assets/animals/`, `assets/flowers/`, `assets/images/`).
- **Upload New Image** — pick any image file from disk and add it to the
  Images tab, where it becomes a draggable asset like everything else.
- Anything you save from either canvas (a composition or a drawing) is
  automatically added back into the Images tab, so your own creations become
  reusable assets too.

## Architecture

The UI is plain Swing with no external dependencies or build tool.

- **`DrawingStudioPro`** — entry point; builds the main `JFrame` and lays out
  the media library, composition canvas, and drawing pad in a split pane.
- **`CreationItem`** (abstract) / **`AnimalItem`**, **`FlowerItem`**,
  **`CustomImageItem`** — the objects placed on the composition canvas. Each
  subtype declares its own transform capabilities (`canFlip`, `canScale`,
  `canTranspose`); currently every item type supports all three.
- **`CreationFactory`** — Factory Method that builds the correct
  `CreationItem` subtype from a `"Type:path"` string, used for drag-and-drop
  and when composing a flattened layer.
- **`LeftCanvas`** — the composition canvas: hit-testing, drag/rotate/resize
  handling, compose, and save.
- **`RightCanvas`** — the drawing pad: a `BufferedImage` painted with
  `Graphics2D`, with strokes tracked as `DrawingPath` objects so the canvas
  can be redrawn cleanly when the window resizes.
- **`CollectionPanel`** / **`ImageThumbnail`** / **`WrapLayout`** — the tabbed
  media library. Each thumbnail is a drag source (`TransferHandler` +
  `StringSelection`) carrying its type and file path; `WrapLayout` is a
  `FlowLayout` variant that wraps thumbnails onto new rows as the panel
  resizes.
- **`LeftCanvasControls`** / **`RightCanvasControls`** — the icon toolbars and
  their save/clear dialogs for each canvas.
- **`ImageUploader`** — file-picker based import into `assets/images/`.

## Project structure

```
DrawingStudioPro.java       Entry point / main window
LeftCanvas.java              Composition canvas
LeftCanvasControls.java      Toolbar for the composition canvas
RightCanvas.java             Drawing pad
RightCanvasControls.java     Toolbar for the drawing pad
CollectionPanel.java         Tabbed media library
CreationFactory.java         Factory for CreationItem subtypes
CreationItem.java            Abstract base for placeable items
AnimalItem.java / FlowerItem.java / CustomImageItem.java
ImageThumbnail.java          Draggable thumbnail component
ImageUploader.java           File-picker image import
WrapLayout.java              Wrapping FlowLayout for the thumbnail grid
Icons/                       Toolbar icon assets
assets/animals/              Bundled animal assets
assets/flowers/              Bundled flower assets
assets/images/                Uploaded images + anything saved from the app
assets/temp/                  Scratch space used briefly while composing
```

## Requirements

- JDK 14 or newer (the code uses switch expressions); JDK 17+ is recommended
- No external libraries or build tool — everything is plain `javax.swing`

## Running

From the project root:

```bash
javac *.java
java DrawingStudioPro
```

Or open the folder in any IDE that supports plain JDK projects (IntelliJ
IDEA, Eclipse, VS Code + Java extensions) and run `DrawingStudioPro.main`.

Run it from the project root (not a subfolder) — icon and asset paths are
resolved relative to the working directory (`icons/...`, `assets/...`).

## Known issues

- Icon loading (`icons/Pen.png`, etc.) uses a lowercase `icons/` path, while
  the tracked folder is `Icons/`. This resolves fine on Windows and macOS
  (case-insensitive filesystems by default) but will fail to find icons on a
  case-sensitive filesystem (e.g. Linux) unless you rename or duplicate the
  folder to lowercase.
