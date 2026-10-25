# Lab Report, Code Quality and Design Quality

Repository URL: https://github.com/Sor117/itcs382-lab

## Tools

| Item | Version or name |
| --- | --- |
| JDK | OpenJDK 23.0.2 |
| Maven | Apache Maven 3.10.0 |
| IDE | Visual Studio Code 1.142.0 |
| SonarQube for IDE | 6.2.0 |
| AI tool and exact model | Claude Code, model Claude Opus 5.5 (`claude-opus-5-5`) |

## Task 1. SonarQube on the starter code

Commit of the starter, before any change: 190e327

Table 1. SonarQube issues in the starter code

| No. | Rule | File:line | Message |
| --- | --- | --- | --- |
| 1 | java:S106 | Main.java:9 | Replace this use of System.out by a logger. |
| 2 | java:S106 | Main.java:15 | Replace this use of System.out by a logger. |
| 3 | java:S106 | Main.java:17 | Replace this use of System.out by a logger. |
| 4 | java:S106 | Main.java:22 | Replace this use of System.out by a logger. |
| 5 | java:S2143 | ParcelService.java:1 | Use the "java.time" API for date and time. |
| 6 | java:S1128 | ParcelService.java:6 | Remove this unused import 'java.util.Date'. |
| 7 | java:S1068 | ParcelService.java:12 | Remove this unused "maxParcels" private field. |
| 8 | java:S106 | ParcelService.java:30 | Replace this use of System.out by a logger. |
| 9 | java:S4973 | ParcelService.java:42 | Strings and Boxed types should be compared using "equals()". |
| 10 | java:S106 | ParcelService.java:67 | Replace this use of System.out by a logger. |
| 11 | java:S106 | ParcelService.java:69 | Replace this use of System.out by a logger. |
| 12 | java:S2095 | ParcelService.java:76 | Use try-with-resources or close this "FileWriter" in a "finally" clause. |
| 13 | java:S108 | ParcelService.java:80 | Remove this block of code, fill it in, or add a comment explaining why it is empty. |

## Task 2. AI review of the starter code

| Item | Value |
| --- | --- |
| Date | 25 October 2026 |
| AI tool and exact model | Claude Code, model Claude Opus 5.5 (`claude-opus-5-5`) |
| Commit reviewed | 190e327 |
| How the AI read the files (attached the files, opened the folder, or pasted the files) | Opened the folder. A new Claude Code agent read the four Java files by itself. I used Prompt A as given, with `<folder>` replaced by `parcelpoint`. |
| Output file in the reviews folder | reviews/parcelpoint-review.md |

Table 2. All findings on the starter code

| No. | File:line | Finding | Found by (SonarQube, AI or both) | SonarQube rule or course term | Correct? (yes or no, and why) |
| --- | --- | --- | --- | --- | --- |
| 1 | ParcelService.java:42 | pickUp compares the codes with `==`, so it never finds the parcel | both | java:S4973 | Yes. The run printed `Fee: 0.0` and P1 stayed STORED. After I changed it to `equals()` it prints `Fee: 30.0`. |
| 2 | ParcelService.java:76 | The FileWriter is never closed, so parcels.csv stays empty | both | java:S2095 | Yes. parcels.csv was empty after the C2 run, and after the fix it has `P1,PICKED_UP`. |
| 3 | ParcelService.java:80 | Empty catch block hides a failed save | both | java:S108 | Yes, there is nothing inside the catch block. |
| 4 | ParcelService.java:6 | Unused import `java.util.Date` | both | java:S1128 | Yes, `Date` isn't used anywhere in the file. |
| 5 | ParcelService.java:12 | Unused field `maxParcels` | both | java:S1068 | Yes, nothing reads `maxParcels`. |
| 6 | ParcelService.java:1 | Uses the old `java.util.Date` API instead of `java.time` | SonarQube | java:S2143 | Partly. `Date` is only imported and never used, so the real problem is row 4. |
| 7 | Main.java:9, 15, 17, 22 and ParcelService.java:30, 67, 69 | Uses `System.out` instead of a logger | SonarQube | java:S106 | Not for this program. Printing is the program's output here, not logging. |
| 8 | ParcelService.java:49 | pickUp returns 0 for a code that doesn't exist, the same as a free pickup | AI | Probable bug | Yes, `pickup ZZ 9` printed `Fee: 0.0`. |
| 9 | Main.java:13 | The input isn't checked, so a short command or a non-number crashes the program | AI | Probable bug | Yes, `store P1` crashed with `ArrayIndexOutOfBoundsException`. |
| 10 | Main.java:8 | The Scanner is never closed | AI | Probable bug (resource not closed) | No. The Scanner reads `System.in` and the program ends right after the loop, so nothing really leaks. |
| 11 | Main.java:12 | No final `else`, so an unknown command is ignored without any message | AI | Code smell | Yes, typing `hello` printed nothing. |
| 12 | ParcelService.java:53 | Magic numbers: the 3 free days is typed four times, and 10.0, 15.0 and 20.0 have no names | AI | Code smell | Yes, the 3 is repeated on lines 53, 57, 59 and 61. |
| 13 | ParcelService.java:52 | The fee is money but it uses `double` | AI | Code smell | No, too general. Every fee here is a whole number, so `double` gives exact results anyway. |
| 14 | Parcel.java:4 | Fields that are only set once aren't `final` (Parcel.java:4-6, Locker.java:4-5, ParcelService.java:10-11) | AI | Code smell | Yes, nothing changes these fields after the constructor. |
| 15 | Main.java:6 | main is long and complex (22 lines, cyclomatic complexity about 8) | AI | Method too long or complex | Partly. The numbers are right, but it's under SonarQube's limit of 15 for cognitive complexity. |
| 16 | Main.java:6 | main has logical cohesion because the command word picks one of five actions | AI | Logical cohesion | Yes, it's like the getAttribute example from the lecture. |
| 17 | ParcelService.java:64 | printParcels has logical cohesion because `forStaff` picks one of two reports | AI | Logical cohesion | Yes, each branch prints a different list. |
| 18 | ParcelService.java:9 | ParcelService allocates lockers, keeps the register, holds the fee rule, sends the SMS, prints and saves the CSV | AI | Weak class cohesion | Yes, that's six jobs in one class. |
| 19 | ParcelService.java:52 | fee doesn't use any attribute of ParcelService | AI | Weak class cohesion | Yes, it only uses its two parameters. |
| 20 | Main.java:19, 21 | Main passes `true` or `false` to choose what printParcels does | AI | Control coupling | Yes, the flag picks the branch inside printParcels. |
| 21 | ParcelService.java:28, 44 | ParcelService writes Parcel's `status` field directly | AI | Content coupling | Yes, one class directly modifies another class's data. |
| 22 | Main.java:14 | storeParcel returns the whole Locker, but Main only uses its number | AI | Stamp coupling | Yes, Main only calls `getNumber()`. |
| 23 | ParcelService.java:52 | fee gets the whole Parcel but only uses its size | AI | Stamp coupling | Yes, it only calls `getSize()`. |
| 24 | Main.java:22 | Main takes the internal locker list and calls `isFree()` on each Locker to count the free ones | AI | Stamp coupling, Law of Demeter | Yes, Main only needs Locker for this count. |
| 25 | ParcelService.java:41 | Gets the Parcel from a Locker and then calls `getCode()` on it | AI | Law of Demeter | Yes, but it's minor because the chain is just one step. |
| 26 | Locker.java:5 | The size is an int where 1 = small, 2 = medium and 3 = large, and Parcel, ParcelService and Main all have to read it the same way | AI | Connascence of convention | Yes. `store P2 6502 0` was accepted and charged as large. |
| 27 | Parcel.java:13 | The status values "NEW", "STORED" and "PICKED_UP" are plain strings that two classes have to spell the same way | AI | Connascence of convention | Yes, "STORED" is typed in ParcelService on lines 28 and 68. |
| 28 | ParcelService.java:43 | `setParcel(null)` empties a locker only because Locker treats `null` as free | AI | Connascence of convention | Yes, Locker.java:30 checks `parcel == null`. |
| 29 | Main.java:15 | A `null` from storeParcel means no locker is free | AI | Connascence of convention | Yes, ParcelService.java:36 returns `null` in this case. |
| 30 | Parcel.java:9 | Two Strings next to each other in the Parcel constructor, and two ints in the Locker constructor | AI | Connascence of position | Yes, swapping them still compiles. |
| 31 | Parcel.java:7 | The field `status` is public | AI | Field that is not private | Yes, it's declared `public`. |
| 32 | ParcelService.java:21 | getLockers returns the internal list, so callers can change it | AI | Internal collection returned | Yes, Main could call `clear()` on it. |

2.1 Name two problems that only the AI review found.

The first one is content coupling through the public field `status`. ParcelService sets `parcel.status` directly (ParcelService.java:28 and 44, Parcel.java:7). The second one is connascence of convention in the parcel size. The numbers 1, 2 and 3 mean small, medium and large, so Locker, Parcel, ParcelService.fee and Main all have to read them the same way (Locker.java:5).

2.2 Name one problem that only SonarQube found, or write none.

java:S2143 at ParcelService.java:1, "Use the "java.time" API for date and time."

2.3 Name one AI finding that was wrong or too vague, and explain why. If every finding was right, explain how you checked them.

Row 10 in Table 2 says "The Scanner is never closed". I think this is wrong for this program. The Scanner wraps `System.in`, and closing it would close `System.in` too. The program ends right after the loop anyway, so nothing leaks, and SonarQube didn't report it either.

## Task 3. Fix and check

Table 3. What you did with each SonarQube issue in Table 1

| No. in Table 1 | Fixed or kept | Commit, or the reason for keeping it |
| --- | --- | --- |
| 1 | Kept | Main prints the command list, which is normal output for a command-line program, so a logger doesn't make sense here. |
| 2 | Kept | This prints the locker number after store, and the user needs to see it on the console. |
| 3 | Kept | This prints the fee after pickup, and the user needs to see it on the console. |
| 4 | Kept | This prints the number of free lockers for the staff command, which is output for the user. |
| 5 | Fixed | 7312205 |
| 6 | Fixed | 7312205 |
| 7 | Fixed | 48c951d |
| 8 | Kept | There is no real SMS gateway, so printing the SMS text on the console is how the program shows the message. |
| 9 | Fixed | 684f4a7 |
| 10 | Kept | This is the staff list that the user asks for with the staff command, so it's output and not a log message. |
| 11 | Kept | This is the waiting list that the user asks for with the list command, so it's output and not a log message. |
| 12 | Fixed | 343319c |
| 13 | Fixed | e25ebb8 |

Number of SonarQube issues that remain after your fixes: 7 (all java:S106, kept for the reasons above)

The two design problems that you fixed

| Design problem (course term) | What you changed | Commit |
| --- | --- | --- |
| Connascence of convention: the size was an int where 1 = small, 2 = medium and 3 = large, so Locker, Parcel, ParcelService and Main all had to read the number the same way | Added an enum `Size` (SMALL, MEDIUM, LARGE) that holds the daily fee for each size, and used it in Locker, Parcel, ParcelService and Main instead of the int | 708a260 |
| Content coupling: ParcelService changed Parcel's public `status` field directly | Made `status` private and added `markStored()`, `markPickedUp()`, `isStored()` and `getStatus()` to Parcel, so ParcelService only uses these methods | 402ef03 |

Output of the program after your fixes, for the commands `store P1 6501 2`, `pickup P1 5` and `staff`, and the content of parcels.csv:

```text
Commands: store <code> <studentId> <size 1-3>, pickup <code> <days>, list, staff, quit
SMS to 6501: parcel P1 is in locker 2
Locker 2
Fee: 30.0
P1 6501 PICKED_UP
Free lockers: 3

parcels.csv:
P1,PICKED_UP
```

## Task 4. AI-generated poor and good designs

| Item | bikeshare-poor | bikeshare-good |
| --- | --- | --- |
| Date | 25 October 2026 | 25 October 2026 |
| AI tool and exact model | Claude Code, model Claude Opus 5.5 (`claude-opus-5-5`) | Claude Code, model Claude Opus 5.5 (`claude-opus-5-5`) |
| Prompt (B or C as given, or your changed prompt in full) | Prompt B as given | Prompt C as given |
| Builds with mvn package (yes or no) | yes | yes |
| Number of classes, interfaces, enums and records | 1 class | 5 classes, 1 interface, 1 enum, 3 records |
| SonarQube issues, total | 24 | 1 |
| SonarQube issues, by rule | java:S106 15, java:S2386 5, java:S1192 2, java:S3776 1, java:S3358 1 | java:S106 1 |
| AI review findings in Check 1 | 6 | 4 |
| AI review findings in Check 2 | 12 | 6 |
| Review output file in the reviews folder | reviews/bikeshare-poor-review.md | reviews/bikeshare-good-review.md |

4.1 Did SonarQube report any cohesion or coupling problem in bikeshare-poor? Quote the rule and the message if it did.

No. None of the rules mention cohesion or coupling. The closest one is java:S2386 "Make this member "protected"." at BikeShare.java:14, 15, 17, 18 and 19. It is about the public static arrays that cause the common coupling, but SonarQube treats it as a visibility problem. java:S3776 "Refactor this method to reduce its Cognitive Complexity from 26 to the 15 allowed." at line 42 flags `useBike`, but it doesn't mention the `renting` flag that makes the method so complex.

4.2 Did the AI review tell the two versions apart? Give one finding for bikeshare-poor that does not appear for bikeshare-good.

Yes. bikeshare-poor got 18 findings (3 of them Must fix) and bikeshare-good got 10, and apart from one CSV bug all of the good version's findings were Notes. One finding for bikeshare-poor that doesn't appear for bikeshare-good is finding 9 (Must fix): "This is common coupling. `main`, `useBike` and `ask` share the global `public static` `input` ... and the fleet arrays" (BikeShare.java:17-20).

4.3 Which version would you rather extend with a new member type, such as a visitor who pays 4 baht per minute, and why?

I'd pick bikeshare-good. The rate is stored in the `MemberType` enum, so adding a visitor is just one new line, `VISITOR(4)`, and `parse`, `feeFor` and the CSV row work without any change. In bikeshare-poor the int codes are spread over four places that all have to match: the new constant, the nested ternary on lines 47-48, `fee` on line 93 and the CSV label on line 85. If I forgot one of them, a visitor would pay the staff rate or get saved as staff, and nothing would show an error.

## Task 5. Reflection

5.1 What does SonarQube check, and what does it not check? Answer with examples from this lab.

SonarQube checks the code against a fixed set of rules, one statement or method at a time. In this lab it found likely bugs like `==` on Strings (S4973) and an unclosed FileWriter (S2095), and code smells like an unused import (S1128), an empty catch block (S108) and a method that is too complex (S3776). It doesn't check design, so it said nothing about the public `status` field, the int size codes, the `forStaff` flag or ParcelService doing six jobs. In bikeshare-poor it also missed the god class, the int member codes and the boolean flag.

5.2 When should you not trust the AI review? Give an example from this lab.

I wouldn't trust it when it gives general advice that doesn't really fit the code, or when it makes a claim that you can only prove by running the program. For ParcelPoint it said the Scanner on `System.in` has to be closed and that money should use `BigDecimal`, but neither matters in this program (Table 2 rows 10 and 13). Its claim that no parcel is ever found was correct, but I only believed it after the run printed `Fee: 0.0`.

5.3 What will you do differently in Project Phase 2 after this lab?

I'll run SonarQube from the beginning and only keep issues that I can explain in one sentence, like S106 in a console program. I'll run Prompt A on our code, but I'll check every finding against the code and a test run before it goes into the report. For the design, I want to use enums instead of int or String codes, keep all fields private and avoid boolean flag parameters, like bikeshare-good does.

## AI usage declaration

| AI tool and exact model | Purpose | Files or parts that it produced or changed | How you checked its output |
| --- | --- | --- | --- |
| Claude Code, Claude Opus 5.5 (`claude-opus-5-5`), 25 October 2026 | Task 2: reviewed the starter code with Prompt A | reviews/parcelpoint-review.md | Opened each file at the line it gave, compared the course terms with the lecture, and ran the starter to test the claims |
| Claude Code, Claude Opus 5.5 (`claude-opus-5-5`), 25 October 2026 | Task 4: wrote the two designs from Prompt B and Prompt C | bikeshare-poor, bikeshare-good | Built both with `mvn package`, tried rentals and returns, and checked the fees and rides.csv |
| Claude Code, Claude Opus 5.5 (`claude-opus-5-5`), 25 October 2026 | Task 4: reviewed both designs with Prompt A | reviews/bikeshare-poor-review.md, reviews/bikeshare-good-review.md | Compared the line numbers with the code |
| Claude Code, Claude Opus 5.5 (`claude-opus-5-5`), 25 October 2026 | Did the other lab steps: installed the tools, ran SonarQube, made the Task 3 fixes and the two design changes, and drafted this report | parcelpoint fixes, Size.java, LAB_REPORT.md | After each fix I rebuilt, ran the C2 input and ran SonarQube again to check that the issue was gone |
