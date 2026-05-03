# 📝 Text Editor

A simple text editor application built with Java Swing.
The main focus is demonstrating the effective use of **Design Patterns**.

---

## 🚀 Features

- Open, Save, Save As (`.txt`, `.java`, `.md`)
- Undo / Redo (Ctrl+Z, Ctrl+Y)
- Find text (normal and regex search)
- Text formatting (Bold, Italic, Bold+Italic)
- Status bar showing character count and save status

---

## 🧩 Design Patterns Used

| Pattern | Where | Purpose |
|---|---|---|
| **Singleton** | `EditorApp` | Single application instance, global access point |
| **Command** | `CommandManager`, all file commands | Encapsulates every action, enables Undo/Redo |
| **Observer** | `Document` → `StatusBar` | Automatically updates UI when document changes |
| **Memento** | `EditorMemento`, `EditorCaretaker` | Stores document snapshots for undo operations |
| **Strategy** | `SimpleSearchStrategy`, `RegexSearchStrategy` | Swappable search algorithms at runtime |
| **Decorator** | `BoldDecorator`, `ItalicDecorator` | Dynamically adds formatting to selected text |

---

## 🛠️ Tech Stack

- **Language:** Java 21
- **UI:** Java Swing
- **Build Tool:** Maven
- **Testing:** JUnit 5

---

## ▶️ How to Run

1. Clone the repository:
```bash
git clone https://github.com/KULLANICI_ADIN/TextEditor.git
```
2. Open in IntelliJ IDEA
3. Run `Main.java` inside `editor.app` package

---

## 🧪 Running Tests

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
│   ├── app/       → Main, EditorApp (Singleton)
│   ├── model/     → Document, EditorMemento, EditorCaretaker
│   ├── command/   → Command interface, CommandManager, all commands
│   ├── observer/  → EditorObserver interface
│   ├── strategy/  → SearchStrategy, SimpleSearch, RegexSearch
│   ├── decorator/ → TextFormatter, BoldDecorator, ItalicDecorator
│   └── ui/        → MainFrame, MenuBar, TextArea, FindDialog, StatusBar
└── test/java/editor/
    ├── CommandManagerTest
    └── DocumentTest
```