# BACC Portal Text File Formats

This document describes the on-disk layout of every file in the `txtfiles/`
directory. These files are the persistence layer for the application; they
are read at startup by the file handlers in
`src/main/java/baccportal/model/data/` and rewritten whenever the in-memory
state changes.

## General conventions

All five files share the same conventions, so they are listed here once
instead of repeating them per file:

1. **One field per line.** Each record is a sequence of consecutive lines,
   one value per line, in a fixed order. Parsers read with `Scanner.nextLine()`,
   so trailing whitespace inside a value is preserved.
2. **Record separator.** The string `----------` (ten hyphens) appears on its
   own line at the end of every record. The reader consumes that line as a
   no-op; the writer emits it after the last field of every record.
3. **`NONE` sentinel.** Where a field is a comma-separated list (prerequisites,
   enrolled sections, transaction history, etc.), the literal string `NONE`
   indicates an empty list. An empty line is **not** treated as empty — it
   would be parsed as a list containing one blank ID and likely fail.
4. **Comma-separated lists.** Multi-value fields are written as
   `value1, value2, value3` (with a single space after each comma). The
   reader splits on `,` and trims each element, so the space is optional on
   the way in but always present on the way out.
5. **File location.** Every reader / writer hard-codes the path
   `txtfiles/<name>.txt` relative to the working directory the JVM was
   launched in (typically the project root).
6. **Numeric formats.** All numbers are written with `PrintWriter.println(...)`
   on the corresponding `double` / `int`. Doubles will appear with a decimal
   point (`3.0`, `150.0`) even when they are whole numbers; ints have no
   decimal.

The handlers are:

| File              | Reader / writer class                                                         |
|-------------------|-------------------------------------------------------------------------------|
| `admin.txt`       | `UserFileHandler.readAdminUsersFromFile / writeAdminUsersToFile`              |
| `students.txt`    | `UserFileHandler.readStudentUsersFromFile / writeStudentUserToFile`           |
| `faculty.txt`     | `UserFileHandler.readFacultyUsersFromFile / writeFacultyUsersToFile`          |
| `courses.txt`     | `CourseFileHandler.readCoursesFromFile / writeCoursesToFile`                  |
| `sections.txt`    | `SectionFileHandler.readSectionsFromFile / writeSectionsToFile`               |
| `majors.txt` *    | `MajorFileHandler.readMajorsFromFile / writeMajorsToFile`                     |

\* `majors.txt` is referenced by `MajorFileHandler` and `FileHandlerFacade`
but no such file currently exists on disk and `AppData.load()` does not call
the major handler. The format below documents what the handler expects in
case the file is added later.

## Load order matters

`AppData.load()` reads the files in this order:

1. `courses.txt`  — courses must exist before sections that reference them.
2. `sections.txt` — sections must exist before users that reference them.
3. `admin.txt`
4. `students.txt`
5. `faculty.txt`

Reading them out of order will cause `getSection(...)` / `getCourse(...)`
lookups during user loading to return `null`, silently dropping the
relationship.

---

## `admin.txt`

One admin record. Fields, in order:

| # | Field      | Type   | Notes                                                           |
|---|------------|--------|-----------------------------------------------------------------|
| 1 | email      | String | Login / contact email.                                          |
| 2 | userId     | String | Unique ID. Used as the login identifier.                        |
| 3 | password   | String | Stored as a salted hash in the form `salt:hash` (Base64). The reader passes `isHashed=true` so it is taken verbatim. |
| 4 | fullName   | String | Display name.                                                   |
| 5 | separator  | `----------` |                                                          |

### Example

```
a@test.com
Admin
Q2IvbkiUnvfTw+T+JarLoQ==:Azs04VoHNDovERgb5DKC5ANjQgmeGerzZuRNAK98ABY=
Admin User
----------
```

---

## `students.txt`

One student record per ten-line block (nine data lines plus the separator).
Fields, in order:

| #  | Field                | Type        | Notes                                                                                                  |
|----|----------------------|-------------|--------------------------------------------------------------------------------------------------------|
| 1  | email                | String      |                                                                                                        |
| 2  | userId               | String      | Unique ID.                                                                                             |
| 3  | password             | String      | Salted hash (`salt:hash`). Read with `isHashed=true`.                                                  |
| 4  | fullName             | String      |                                                                                                        |
| 5  | majorName            | String      | The plain name of the major (e.g. `CS`). Combined with field 6 to build a `Major`.                     |
| 6  | department           | Enum        | Must match a value of `Department` (`COMPUTER_SCIENCE`, `DATA_SCIENCE`, `MATHEMATICS`).                 |
| 7  | balanceOwed          | double      | Outstanding balance. Written via `println(double)`, so it always has a decimal point.                  |
| 8  | completedSections    | List<String>| Comma-separated `sectionId` values, or `NONE`. Each id is resolved against `CourseStorage` at load.    |
| 9  | enrolledSections     | List<String>| Same shape as field 8.                                                                                 |
| 10 | transactionHistory   | List<Receipt> | Comma-separated `receiptId-totalPaid-remainingBalance` triples (note: separator is a hyphen, not a comma), or `NONE`. The serialized form is produced by `Receipt.toString()`. |
| 11 | separator            | `----------` |                                                                                                       |

If a section ID in fields 8 or 9 cannot be resolved, the reader silently skips
it; it does not abort the load. Likewise an unknown department in field 6 will
throw `IllegalArgumentException` from `Department.stringToDepartment(...)`.

### Receipt entries

A single receipt is encoded as `receiptId-totalPaid-remainingBalance`.
Multiple receipts in the transaction-history field are joined by `, ` (comma
plus space). The reader splits the field on `,`, then splits each element on
`-`, and finally calls `Receipt(int, double, double)`.

### Example

```
Leon98@gmail.com
012958956
cixImwxWF5iMZFzVjKAYQQ==:kL1XyhW6XPEwcIqsor+i3uXWkvRTvTK2duexrNfmKhw=
Leon
CS
COMPUTER_SCIENCE
320.0
NONE
NONE
NONE
----------
```

A populated record would look like:

```
jane@test.com
S100
saltSalt==:hashHash==
Jane Doe
CS
COMPUTER_SCIENCE
1500.0
01, 02
03
100000-500.0-1500.0, 100001-500.0-1000.0
----------
```

---

## `faculty.txt`

One faculty record per seven-line block. Fields, in order:

| # | Field            | Type            | Notes                                                                  |
|---|------------------|-----------------|------------------------------------------------------------------------|
| 1 | email            | String          |                                                                        |
| 2 | userId           | String          |                                                                        |
| 3 | password         | String          | Salted hash (`salt:hash`).                                             |
| 4 | fullName         | String          | The section roster lookup matches this by name (case-insensitive trim).|
| 5 | department       | Enum            | One of `Department` values.                                            |
| 6 | sectionsTaught   | List<String>    | Comma-separated `sectionId` values, or `NONE`.                         |
| 7 | separator        | `----------`    |                                                                        |

### Example

```
f@test.com
456
8/0nY/yNVQOMg+QglEor5g==:DIbsv4pZrg8bOZVeM3AlQKktIRuxFtCRatXuMbIYIa4=
Faculty User
COMPUTER_SCIENCE
NONE
----------
Lisa98@gmail.com
Faculty
iPwRZqNxYXfMGCVzVJQ42g==:dYayqAcYs6xw/Dwa2SMJ4DORyN4A28NMaBfX9WYzmrg=
Lisa Perez
COMPUTER_SCIENCE
NONE
----------
```

---

## `courses.txt`

One course per six-line block. Fields, in order:

| # | Field             | Type         | Notes                                                                                          |
|---|-------------------|--------------|------------------------------------------------------------------------------------------------|
| 1 | courseId          | String       | Unique catalog ID (e.g. `CS-151`).                                                             |
| 2 | courseName        | String       | Display name.                                                                                  |
| 3 | unitAmount        | double       | Credit hours.                                                                                  |
| 4 | prerequisites     | List<String> | Comma-separated `courseId` values, or `NONE`. **Resolved in a second pass after every course has been loaded** so that forward references work. |
| 5 | separator         | `----------` |                                                                                                |

A prerequisite ID that does not match a known course is logged to stdout
with `Could not find Course Prerequisite with courseId: ...` and skipped.

### Example

```
12345
CS-151
3.0
NONE
----------
123
CS-146
3.0
NONE
----------
```

A course with prerequisites would look like:

```
CS-151
Object-Oriented Design
3.0
CS-146, CS-149
----------
```

---

## `sections.txt`

One section per nine-line block. Fields, in order:

| # | Field             | Type   | Notes                                                                                          |
|---|-------------------|--------|------------------------------------------------------------------------------------------------|
| 1 | courseId          | String | Must already exist in `CourseStorage`. If not, the section is silently skipped during load.    |
| 2 | instructorName    | String | Free-form full name. Faculty assignment is matched by full-name equality (case-insensitive trim). |
| 3 | sectionId         | String | Unique section identifier (e.g. `01`, `02`).                                                   |
| 4 | accessCode        | String | The shared code students must type to enroll.                                                  |
| 5 | price             | double | Section cost added to the student’s balance on enroll.                                         |
| 6 | totalCapacity     | int    | Maximum seats.                                                                                 |
| 7 | currentCapacity   | int    | Currently filled seats. Must be `<= totalCapacity` (validated when added through the UI).      |
| 8 | separator         | `----------` |                                                                                          |

### Example

```
123
Lisa Perez
01
123
150.0
30
1
----------
123
Lisa Perez
02
123
150.0
35
0
----------
```

---

## `majors.txt`  *(planned, not present on disk)*

One major catalog per six-line block. Fields, in order:

| # | Field            | Type         | Notes                                                                                |
|---|------------------|--------------|--------------------------------------------------------------------------------------|
| 1 | majorName        | String       |                                                                                      |
| 2 | department       | Enum         | One of `Department` values.                                                          |
| 3 | requiredCourses  | List<String> | Comma-separated `courseId` values, or `NONE`. Each must already be in `CourseStorage`. |
| 4 | electiveCourses  | List<String> | Same shape as required courses.                                                      |
| 5 | separator        | `----------` |                                                                                      |

If `txtfiles/majors.txt` does not exist, `MajorFileHandler.readMajorsFromFile`
will throw `IOException`. `AppData.load()` does **not** currently call this
handler, so the absence of the file is not fatal in the current build.

### Example

```
Computer Science
COMPUTER_SCIENCE
CS-146, CS-151
CS-160, CS-166
----------
```

---

## Quick reference

| File           | Lines per record | List fields                                       | Sentinel for empty list |
|----------------|------------------|---------------------------------------------------|-------------------------|
| `admin.txt`    | 5                | —                                                 | —                       |
| `students.txt` | 11               | completedSections, enrolledSections, transactions | `NONE`                  |
| `faculty.txt`  | 7                | sectionsTaught                                    | `NONE`                  |
| `courses.txt`  | 5                | prerequisites                                     | `NONE`                  |
| `sections.txt` | 8                | —                                                 | —                       |
| `majors.txt`   | 5                | requiredCourses, electiveCourses                  | `NONE`                  |

“Lines per record” counts every line written, including the trailing
`----------` separator.
