# 📝 Text Editor

A simple yet feature-rich text editor built with Java Swing, inspired by Windows Notepad.

---

## 🚀 Features

- **File Operations** — Open, Save, Save As with format selection
- **Export Formats** — `.txt`, `.java`, `.md`, `.html`, `.xml`, `.csv`, `.log`, `.pdf`
- **Undo / Redo** — Full undo/redo support (Ctrl+Z, Ctrl+Y)
- **Find** — Normal and Regex search (Ctrl+F)
- **Text Formatting** — Bold, Italic, Underline toolbar with toggle support
- **Font Size** — Selectable and typeable font size
- **Status Bar** — Live character count and save status
- **Unsaved Changes** — Title shows `*` indicator and exit prompts to save

---

## 🧩 Design Patterns Used

| Pattern | Class(es) | Purpose |
|---|---|---|
| **Singleton** | `EditorApp` | Single application instance, global access point for Document and CommandManager |
| **Command** | `CommandManager`, `OpenFileCommand`, `SaveFileCommand`, `SaveAsFileCommand` | Encapsulates every action as an object, enables Undo/Redo |
| **Observer** | `EditorObserver`, `Document`, `StatusBar` | Automatically updates status bar and title when document changes |
| **Strategy** | `SearchStrategy`, `SimpleSearchStrategy`, `RegexSearchStrategy` | Swappable search algorithms (normal vs regex) at runtime |
| **Decorator** | `TextFormatter`, `BaseFormatter`, `BoldDecorator`, `ItalicDecorator` | Dynamically applies text formatting without modifying the base class |

---

## 🛠️ Tech Stack

- **Language:** Java 21
- **UI:** Java Swing
- **Build Tool:** Maven
- **Testing:** JUnit 5
- **PDF Export:** iText 5

---

## ▶️ How to Run

1. Clone the repository:
```bash
git clone https://github.com/lyn4c4ry/TextEditor.git
```

2. Open the project in IntelliJ IDEA

3. Run `Main.java` inside `editor.app` package

---

## 🧪 Running Tests

In IntelliJ, right-click on `src/test/java` → **Run All Tests**

Or via Maven:
```bash
mvn test
```

---

## ⌨️ Keyboard Shortcuts

| Shortcut | Action |
|---|---|
| Ctrl+O | Open file |
| Ctrl+S | Save file |
| Ctrl+Shift+S | Save As |
| Ctrl+Z | Undo |
| Ctrl+Y | Redo |
| Ctrl+F | Find |
| Ctrl+B | Bold |
| Ctrl+I | Italic |

---

## 📁 Project Structure

```
src/
├── main/java/editor/
│   ├── app/          → Main, EditorApp (Singleton)
│   ├── model/        → Document
│   ├── command/      → Command interface, CommandManager, all file commands
│   ├── observer/     → EditorObserver interface
│   ├── strategy/     → SearchStrategy, SimpleSearchStrategy, RegexSearchStrategy
│   ├── decorator/    → TextFormatter, BaseFormatter, BoldDecorator, ItalicDecorator
│   └── ui/           → MainFrame, EditorMenuBar, EditorTextArea, EditorToolBar, FindDialog, StatusBar
└── test/java/editor/ → CommandManagerTest, DocumentTest, SearchStrategyTest
```