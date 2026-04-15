# Neon Interpreter

A lightweight, register-based interpreter for the **Neon** programming language, written in Java.

---

## Overview

Neon is a simple, low-level scripting language that operates directly on registers. Programs are written in plain `.txt` files as a sequence of instructions that manipulate typed values (integers, floats, strings, booleans) stored in up to 1024 addressable registers.

---

## How It Works

1. The interpreter reads a script file (`.txt`) line by line.
2. **Preprocessing** — scans all lines to register `LABEL` positions, enabling jumps.
3. **Execution** — iterates through instructions sequentially, dispatching each one to its handler. Jump instructions alter the program counter to redirect execution flow.

---

## Running a Script

Place your script in `src/script.txt` and run the `Main` class. The interpreter will read and execute the file automatically.

```
src/
└── script.txt   ← your Neon program goes here
```

---

## Registers

Registers are referenced using the syntax `iN`, where `N` is an index from `0` to `1023`.

```
i0   → register 0
i42  → register 42
```

Registers are untyped containers — they hold whatever value was last assigned to them (Integer, Float, String, or Boolean).

---

## Instruction Set

Instructions are **case-insensitive** (`PUT`, `put`, and `Put` are all valid).

### Data

| Instruction | Syntax | Description |
|---|---|---|
| `PUT` | `PUT iDST value` | Stores a literal value into a register |
| `COPY` | `COPY iDST iSRC` | Copies the value from one register to another |
| `CLEAN` | `CLEAN iREG` | Clears a register (sets it to null) |
| `PRINT` | `PRINT iREG` | Prints the value of a register to stdout |

### Arithmetic

| Instruction | Syntax | Description |
|---|---|---|
| `INC` | `INC iREG [amount]` | Increments a register by 1 (or by `amount`) |
| `DEC` | `DEC iREG [amount]` | Decrements a register by 1 (or by `amount`) |
| `SUM` | `SUM iA iB iR` | `iR = iA + iB` (also concatenates strings) |
| `SUB` | `SUB iA iB iR` | `iR = iA - iB` |
| `MUT` | `MUT iA iB iR` | `iR = iA * iB` |
| `DIV` | `DIV iA iB iR` | `iR = iA / iB` |
| `MOD` | `MOD iA iB iR` | `iR = iA % iB` |
| `RAND` | `RAND vA vB iR` | Stores a random number between `vA` and `vB` into `iR` |

### Comparison

All comparison instructions store a `Boolean` result in the destination register.

| Instruction | Syntax | Description |
|---|---|---|
| `EQ` | `EQ iA iB iR` | `iR = (iA == iB)` |
| `GT` | `GT iA iB iR` | `iR = (iA > iB)` |
| `LT` | `LT iA iB iR` | `iR = (iA < iB)` |
| `GTEQ` | `GTEQ iA iB iR` | `iR = (iA >= iB)` |
| `LTEQ` | `LTEQ iA iB iR` | `iR = (iA <= iB)` |

### Control Flow

| Instruction | Syntax | Description |
|---|---|---|
| `LABEL` | `LABEL name` | Declares a named jump target |
| `JMP` | `JMP name` | Unconditionally jumps to a label |
| `JMPIF` | `JMPIF iCOND name` | Jumps to label if `iCOND` is `true` |
| `JMPIFNOT` | `JMPIFNOT iCOND name` | Jumps to label if `iCOND` is `false` |

---

## Types

| Type | Example literals | Notes |
|---|---|---|
| `Integer` | `0`, `42`, `-7` | Default numeric type |
| `Float` | `3.14`, `-0.5` | Used when a `.` is present |
| `String` | `"hello world"` | Must be enclosed in double quotes |
| `Boolean` | _(result of comparisons only)_ | Produced by `EQ`, `GT`, `LT`, etc. |

---

## Example Programs

### Counting from 1 to 5

```
PUT i0 0
PUT i1 5

LABEL contar
INC i0
PRINT i0

LT i0 i1 i2
JMPIF i2 contar
```

**Output:**
```
Int result: 1
Int result: 2
Int result: 3
Int result: 4
Int result: 5
```

### Sum of two numbers

```
PUT i0 10
PUT i1 32
SUM i0 i1 i2
PRINT i2
```

**Output:**
```
Int result: 42
```

### Random number

```
RAND 1 100 i0
PRINT i0
```

---

## Project Structure

```
src/
├── script.txt                         ← Neon script to be executed
└── main/java/org/neomin/interpreter/
    ├── Main.java         # Entry point: reads script.txt and runs the interpreter
    ├── Tokenizer.java    # Core interpreter: registers, instruction dispatch, execution loop
    └── TokenUtils.java   # Lexer utilities: tokenization, type checking, register parsing
```

---

## License

This project is proprietary. All rights reserved.