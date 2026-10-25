# Lab Report, Code Quality and Design Quality

Repository URL: https://github.com/Sor117/itcs382-lab

## Tools

| Item | Version or name |
| --- | --- |
| JDK | OpenJDK 23.0.2 (Azul Zulu 23.32+11) |
| Maven | Apache Maven 3.10.0 |
| IDE | Visual Studio Code 1.142.0 with Extension Pack for Java 0.31.2 |
| SonarQube for IDE | 6.2.0 (SonarSource), default rules, no connected mode |
| AI tool and exact model | Claude Code (Anthropic), model Claude Opus 5.5 (`claude-opus-5-5`) |

Note on the SonarQube runs: every SonarQube result in this report comes from the language server and Java analyzer of the SonarQube for IDE 6.2.0 extension (`sonarlint-ls.jar` with `sonarjava.jar`). The server was started from a script that opened every Java file of a project and collected the issues that the Problems panel shows, with the default rules. Issues from the Java extension, such as `Resource leak: 'in' is never closed`, are left out.

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

Issue 5 is a file-level issue, so SonarQube shows it at line 1, column 1.

What looked wrong when I ran the starter with `store P1 6501 2`, `pickup P1 5` and `staff`: pickup printed `Fee: 0.0` instead of 30.0 for 5 days of a medium parcel, the staff list still showed `P1 6501 STORED` after the pickup, the program reported 2 free lockers instead of 3, and parcels.csv was empty.

## Task 2. AI review of the starter code

| Item | Value |
| --- | --- |
| Date | 25 October 2026 |
| AI tool and exact model | Claude Code (Anthropic), model Claude Opus 5.5 (`claude-opus-5-5`) |
| Commit reviewed | 190e327 |
| How the AI read the files (attached the files, opened the folder, or pasted the files) | Opened the folder. A new Claude Code agent with no earlier context was started in the repository folder, read the four Java files itself and saved its whole answer to the reviews folder. Prompt A was used as given, with `<folder>` replaced by `parcelpoint`. |
| Output file in the reviews folder | reviews/parcelpoint-review.md |

Table 2. All findings on the starter code

"AI n" refers to finding n in reviews/parcelpoint-review.md.

| No. | File:line | Finding | Found by (SonarQube, AI or both) | SonarQube rule or course term | Correct? (yes or no, and why) |
| --- | --- | --- | --- | --- | --- |
| 1 | ParcelService.java:42 | Codes compared with `==` in pickUp, so no parcel is ever found | both | java:S4973 (AI 1) | yes, the run printed `Fee: 0.0` and P1 stayed STORED; after the `equals()` fix the run prints `Fee: 30.0` |
| 2 | ParcelService.java:76 | FileWriter is never closed, so parcels.csv stays empty | both | java:S2095 (AI 2) | yes, parcels.csv was empty after the run of step C2 and holds `P1,PICKED_UP` after the fix |
| 3 | ParcelService.java:80 | Empty catch block hides a failed save | both | java:S108 (AI 3) | yes, the catch block has no statement |
| 4 | ParcelService.java:6 | Unused import `java.util.Date` | both | java:S1128 (AI 8) | yes, `Date` is not used anywhere in the file |
| 5 | ParcelService.java:12 | Unused field `maxParcels` | both | java:S1068 (AI 9) | yes, nothing reads `maxParcels`, so the limit of 100 is never checked |
| 6 | ParcelService.java:1 | Old `java.util.Date` API instead of `java.time` | SonarQube | java:S2143 | partly, `Date` is only imported and never used, so the real problem is row 4, and the issue went away with that fix |
| 7 | Main.java:9, 15, 17, 22 and ParcelService.java:30, 67, 69 | `System.out` used instead of a logger | SonarQube | java:S106 | no for this program, because printing is the output of a command-line program, not logging |
| 8 | ParcelService.java:49 | pickUp returns 0 for an unknown code, the same value as a free pickup | AI | AI 4 | yes, `pickup ZZ 9` printed `Fee: 0.0` |
| 9 | Main.java:13 | Input is not checked, so a short command or a non-number stops the program | AI | AI 5 | yes, `store P1` stopped the program with `ArrayIndexOutOfBoundsException` at Main.java:13 |
| 10 | Main.java:8 | Scanner is never closed | AI | AI 6 | no, the Scanner reads `System.in` and the program ends right after the loop, so nothing leaks, and SonarQube's resource rule S2095 did not report it either |
| 11 | Main.java:12 | No final `else`, so an unknown command is ignored without a message | AI | AI 7 | yes, typing `hello` printed nothing |
| 12 | ParcelService.java:53 | Magic numbers: 3 free days typed four times and the rates 10.0, 15.0 and 20.0 | AI | AI 10 | yes, lines 53, 57, 59 and 61 all repeat the 3 |
| 13 | ParcelService.java:52 | The fee is money but uses `double` | AI | AI 11 | no, too general, because every fee here is a whole number (10, 15 or 20 times whole days), so `double` gives exact values |
| 14 | Parcel.java:4 | Fields that are set once are not `final` (Parcel.java:4-6, Locker.java:4-5, ParcelService.java:10-11) | AI | AI 12 | yes, no code assigns these fields after the constructor |
| 15 | Main.java:6 | main is long and complex (22 lines, cyclomatic complexity about 8) | AI | AI 13 | partly, the count is right, but it is below SonarQube's limit of 15 for cognitive complexity, which is why SonarQube said nothing |
| 16 | Main.java:6 | main has logical cohesion, because the command word picks one of five actions | AI | Logical cohesion (AI 14) | yes, it matches the getAttribute example of the lecture, but the extra claim of temporal start-up steps is a stretch |
| 17 | ParcelService.java:64 | printParcels has logical cohesion, because `forStaff` picks one of two reports | AI | Logical cohesion (AI 15) | yes, the two branches print different lists |
| 18 | ParcelService.java:9 | ParcelService allocates lockers, keeps the register, holds the fee rule, sends the SMS, prints and saves the CSV | AI | Weak class cohesion (AI 16) | yes, the class does six jobs |
| 19 | ParcelService.java:52 | fee uses no attribute of ParcelService | AI | Weak class cohesion (AI 17) | yes, it uses only its two parameters |
| 20 | Main.java:19, 21 | Main passes `true` or `false` to choose what printParcels does | AI | Control coupling (AI 18) | yes, the flag selects the branch inside printParcels, like Method C of Exercise 3 |
| 21 | ParcelService.java:28, 44 | ParcelService writes Parcel's `status` field directly | AI | Content coupling (AI 19) | yes, the lecture defines content coupling as a component that directly modifies another's data |
| 22 | Main.java:14 | storeParcel returns the whole Locker, but Main uses only its number | AI | Stamp coupling (AI 20) | yes, Main calls only `getNumber()` |
| 23 | ParcelService.java:52 | fee receives the whole Parcel but uses only its size | AI | Stamp coupling (AI 21) | yes, only `getSize()` is called |
| 24 | Main.java:22 | Main takes the internal locker list and calls `isFree()` on each Locker to count free lockers | AI | Stamp coupling, Law of Demeter (AI 22) | yes, Main depends on Locker only for this count |
| 25 | ParcelService.java:41 | Gets the Parcel from a Locker and then calls `getCode()` on it | AI | Law of Demeter (AI 23) | yes, but minor, because the chain is only one step |
| 26 | Locker.java:5 | Size is an int where 1 = small, 2 = medium and 3 = large, and Parcel, ParcelService and Main must read it the same way | AI | Connascence of convention (AI 24) | yes, `store P2 6502 0` was accepted and the fee rule charges any other number as large |
| 27 | Parcel.java:13 | Status values "NEW", "STORED" and "PICKED_UP" are plain strings that two classes must spell the same way | AI | Connascence of convention (AI 25) | yes, "STORED" is typed in ParcelService at lines 28 and 68 |
| 28 | ParcelService.java:43 | `setParcel(null)` empties a locker only because Locker treats `null` as free | AI | Connascence of convention (AI 26) | yes, Locker.java:30 checks `parcel == null` |
| 29 | Main.java:15 | A `null` from storeParcel means that no locker is free | AI | Connascence of convention (AI 27) | yes, ParcelService.java:36 returns `null` for this case |
| 30 | Parcel.java:9 | Two Strings side by side in the Parcel constructor and two ints in the Locker constructor | AI | Connascence of position (AI 28) | yes, a swap still compiles, but it is a weak form for a three-parameter constructor |
| 31 | Parcel.java:7 | Field `status` is public | AI | Field that is not private (AI 29) | yes, and SonarQube's default rules did not report it |
| 32 | ParcelService.java:21 | getLockers returns the internal list, so callers can change it | AI | Internal collection returned (AI 30) | yes, Main could call `clear()` on it |

2.1 Name two problems that only the AI review found.

First, content coupling through the public field `status`. ParcelService sets `parcel.status = "STORED"` and `"PICKED_UP"` directly (ParcelService.java:28 and 44, Parcel.java:7), and SonarQube's default rules reported neither the public field nor the coupling. Second, connascence of convention in the parcel size. The int codes 1, 2 and 3 mean small, medium and large, and Locker, Parcel, ParcelService.fee and Main all had to read them the same way (Locker.java:5, ParcelService.java:56-61). The AI also found the logic problem that pickUp returns 0 for an unknown code (ParcelService.java:49), which no SonarQube rule covers.

2.2 Name one problem that only SonarQube found, or write none.

java:S2143 at ParcelService.java:1, "Use the "java.time" API for date and time." SonarQube also reported java:S106, `System.out` instead of a logger, at seven places, and the AI did not report it. For a command-line program, S106 is not a real problem.

2.3 Name one AI finding that was wrong or too vague, and explain why. If every finding was right, explain how you checked them.

AI 6 (Table 2 row 10), "The Scanner is never closed", is wrong for this program. The Scanner wraps `System.in`, and closing it would also close `System.in`. The program ends right after the loop, so the open Scanner leaks nothing. SonarQube's resource rule S2095, which did report the FileWriter, did not report this Scanner. AI 11, which says to use `BigDecimal` for money, is too general, because every fee in ParcelPoint is a whole number of baht. I checked each finding by opening the file at the line the AI gave and comparing the finding with the lecture definitions. I also re-ran the starter to test the runtime claims, with `store P1`, `pickup ZZ 9`, `hello` and `store P2 6502 0`.

## Task 3. Fix and check

Table 3. What you did with each SonarQube issue in Table 1

| No. in Table 1 | Fixed or kept | Commit, or the reason for keeping it |
| --- | --- | --- |
| 1 | Kept | Main prints the list of commands, which is the output of a command-line program, so a logger is the wrong tool. |
| 2 | Kept | Main prints the locker number as the result of the store command, which the user must see on the console. |
| 3 | Kept | Main prints the fee as the result of the pickup command, which the user must see on the console. |
| 4 | Kept | Main prints the number of free lockers for the staff command, which is console output for the user. |
| 5 | Fixed | 7312205 (the issue came from the unused `Date` import, so it went away when the import was removed) |
| 6 | Fixed | 7312205 |
| 7 | Fixed | 48c951d |
| 8 | Kept | The program has no SMS gateway, so printing the SMS text on the console is how it shows the message to the user. |
| 9 | Fixed | 684f4a7 |
| 10 | Kept | The staff list is the output that the user asks for with the staff command, not a log message. |
| 11 | Kept | The waiting list is the output that the user asks for with the list command, not a log message. |
| 12 | Fixed | 343319c |
| 13 | Fixed | e25ebb8 |

Number of SonarQube issues that remain after your fixes: 7, all java:S106 and all kept with the reasons above. After the design changes they are at Main.java:9, 15, 17 and 22 and at ParcelService.java:31, 63 and 65. The new file Size.java has no issue.

The two design problems that you fixed

| Design problem (course term) | What you changed | Commit |
| --- | --- | --- |
| Connascence of convention (Table 2 row 26): the size was an int where 1 = small, 2 = medium and 3 = large, and Locker, Parcel, ParcelService.fee and Main all had to read the number the same way | Added the enum `Size` (SMALL, MEDIUM, LARGE), which holds the daily fee of each size. Locker and Parcel now store a `Size`, `fee()` uses `getDailyFee()` instead of the if-chain on 1, 2 and 3, `storeParcel` uses `fitsIn()`, and Main turns the typed number into a `Size` once with `Size.fromNumber()`. The classes now agree only on the enum's name and type, and a size such as 0 is rejected. | 708a260 |
| Content coupling (Table 2 rows 21 and 31): ParcelService changed Parcel's public field `status` directly and compared its strings | Made `status` private and added `markStored()`, `markPickedUp()`, `isStored()` and `getStatus()` to Parcel. ParcelService calls only these methods, so the status strings now appear only inside Parcel. | 402ef03 |

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
| AI tool and exact model | Claude Code (Anthropic), model Claude Opus 5.5 (`claude-opus-5-5`) | Claude Code (Anthropic), model Claude Opus 5.5 (`claude-opus-5-5`) |
| Prompt (B or C as given, or your changed prompt in full) | Prompt B as given, with the seven lines of the BikeShare specification in place of the placeholder line (see the note below the table) | Prompt C as given, with the seven lines of the BikeShare specification in place of the placeholder line (see the note below the table) |
| Builds with mvn package (yes or no) | yes, on the first try, with no fix | yes, on the first try, with no fix |
| Number of classes, interfaces, enums and records | 1 class (BikeShare), 0 interfaces, 0 enums, 0 records | 10 in total: 5 classes (Main, ConsoleMenu, BikeShare, Station, CsvRideLog), 1 interface (RideLog), 1 enum (MemberType), 3 records (Bike, Rental, Ride) |
| SonarQube issues, total | 24 | 1 |
| SonarQube issues, by rule | java:S106 15, java:S2386 5, java:S1192 2, java:S3776 1, java:S3358 1 | java:S106 1 |
| AI review findings in Check 1 | 6 | 4 |
| AI review findings in Check 2 | 12 | 6 |
| Review output file in the reviews folder | reviews/bikeshare-poor-review.md | reviews/bikeshare-good-review.md |

Note on the prompts: each prompt ran in a new Claude Code agent with no earlier context. The prompt text was not changed. The agent also got these extra lines, so that its answer could be saved and the files copied out: "Act as a chat assistant that has been given the prompt below. Do not read any existing file, and do not run builds or other commands. Write your complete answer, exactly as you would reply in a chat, as a Markdown file at `<path>`. That is the only file you may create. Before each code block, put the path of that file relative to the project folder on its own line, for example pom.xml or src/main/java/bikeshare/Main.java." The files were copied from the answers unchanged. The main class of bikeshare-poor is `bikeshare.BikeShare`, and the main class of bikeshare-good is `bikeshare.Main`.

How I checked the two programs: in both versions I rented two bikes at Library, tried a third rental there, which was refused because no bike was left, returned the bikes at Dorm and Canteen after 15 and 10 minutes, and typed quit. Both printed a receipt with 30 baht for the student's 15 minutes and 30 baht for the staff member's 10 minutes, and both added the two rides to rides.csv. I also tested a runtime claim from the bikeshare-good review. The member ID `a,b` gave the row `a,b,STUDENT,B1,5,10`, which has one column more than the header, so finding 1 of that review is right.

Severity of the AI findings: bikeshare-poor has 3 Must fix, 10 Should fix and 5 Note, and bikeshare-good has 1 Should fix and 9 Note.

The SonarQube issues of bikeshare-poor are S106 at BikeShare.java:24, 25, 33, 35, 50, 57, 61, 65, 76, 80, 81, 82, 83, 106 and 118, S2386 "Make this member "protected"." at lines 14, 15, 17, 18 and 19, S1192 for "Library" and "Canteen" at line 14, S3776 "Refactor this method to reduce its Cognitive Complexity from 26 to the 15 allowed." at line 42, and S3358 "Extract this nested ternary operation into an independent statement." at line 48. The only issue of bikeshare-good is S106 at Main.java:15, where `System.out` is passed to ConsoleMenu.

4.1 Did SonarQube report any cohesion or coupling problem in bikeshare-poor? Quote the rule and the message if it did.

No. No SonarQube rule names cohesion, coupling or connascence. Two rules come close. java:S2386 "Make this member "protected"." at BikeShare.java:14, 15, 17, 18 and 19 flags the public static arrays, which are the shared data behind the common coupling. But SonarQube treats it as a visibility problem, and it did not flag the public static `Scanner input` at line 20. java:S3776 "Refactor this method to reduce its Cognitive Complexity from 26 to the 15 allowed." at line 42 flags `useBike`, but it says nothing about the `renting` flag that causes the complexity. SonarQube did not report the god class, the int member codes or the control coupling.

4.2 Did the AI review tell the two versions apart? Give one finding for bikeshare-poor that does not appear for bikeshare-good.

Yes. bikeshare-poor got 18 findings, 3 of them Must fix. They cover logical cohesion, control coupling, common coupling, weak class cohesion, connascence of convention, position and algorithm, and non-private fields. bikeshare-good got 10 findings, and all except one CSV bug are Notes. Its review reports no control, common or content coupling, no weak class cohesion and no non-private field. One finding for bikeshare-poor that does not appear for bikeshare-good is finding 9, a Must fix: "This is common coupling. `main`, `useBike` and `ask` share the global `public static` `input` ... and the fleet arrays" (BikeShare.java:17-20).

4.3 Which version would you rather extend with a new member type, such as a visitor who pays 4 baht per minute, and why?

bikeshare-good. The rate is stored in the `MemberType` enum, so a visitor is one new line, `VISITOR(4)`. `parse`, `feeFor` and the CSV row then work without change, and only the help text and the error message need the new word. In bikeshare-poor, the int code convention is spread over four places that must agree: the new constant, the nested ternary at lines 47-48, `fee` at line 93 and the CSV label at line 85. `fee` charges every code other than 1 the staff rate, and line 85 writes every code other than 1 as "staff". If I missed one place, a visitor would pay 3 baht or show up as staff, with no error.

## Task 5. Reflection

5.1 What does SonarQube check, and what does it not check? Answer with examples from this lab.

SonarQube checks code against fixed rules, one statement or method at a time. It found probable bugs, such as `==` on Strings (S4973) and an unclosed FileWriter (S2095), and code smells, such as an unused import (S1128), an unused field (S1068), an empty catch block (S108), repeated literals (S1192) and a method that is too complex (S3776). It does not check design. It said nothing about the public field `status`, the int size codes, the `forStaff` flag or ParcelService doing six jobs. In bikeshare-poor it did not name the god class, the int member codes or the boolean flag either, and 15 of its 24 issues there were S106.

5.2 When should you not trust the AI review? Give an example from this lab.

Do not trust the AI review when it gives general advice that does not fit the code, or a claim that only running the program can prove. In ParcelPoint it said the Scanner on `System.in` must be closed and that money must use `BigDecimal`, but neither matters in this program (Table 2 rows 10 and 13). It also added a "temporal" label to `main` that the code does not support. Its claim that no parcel is ever found was right, but I trusted it only after the run printed `Fee: 0.0`.

5.3 What will you do differently in Project Phase 2 after this lab?

I will run SonarQube from the start and fix real bugs at once. I will keep only the issues that I can justify in one sentence, such as S106 in a console program. I will run Prompt A on our code and check every finding against the line it names, the lecture definitions and a test run before I put it in the report. In the design, I will use enums instead of int or String codes, keep every field private, avoid boolean flag parameters and keep the menu apart from the rule classes, as bikeshare-good does.

## AI usage declaration

| AI tool and exact model | Purpose | Files or parts that it produced or changed | How you checked its output |
| --- | --- | --- | --- |
| Claude Code (Anthropic), Claude Opus 5.5 (`claude-opus-5-5`), 25 October 2026 | Task 2: AI review of the starter code with Prompt A, in a new agent | reviews/parcelpoint-review.md | Opened each file at every line the AI gave, compared the course terms with the lecture definitions, and re-ran the starter with `store P1`, `pickup ZZ 9`, `hello` and `store P2 6502 0` to test the runtime claims (Table 2, last column) |
| Claude Code (Anthropic), Claude Opus 5.5 (`claude-opus-5-5`), 25 October 2026 | Task 4: generated the two designs with Prompt B and Prompt C, each in a new agent | all files in bikeshare-poor and bikeshare-good | Built both with `mvn package`, ran rentals and returns, checked the fees (15 minutes for a student and 10 minutes for a staff member both cost 30 baht) and the rows in rides.csv, and counted the classes |
| Claude Code (Anthropic), Claude Opus 5.5 (`claude-opus-5-5`), 25 October 2026 | Task 4: AI reviews of both designs with Prompt A, each in a new agent | reviews/bikeshare-poor-review.md, reviews/bikeshare-good-review.md | Compared the line numbers with the code, and tested the CSV comma claim of the bikeshare-good review by running the program |
| Claude Code (Anthropic), Claude Opus 5.5 (`claude-opus-5-5`), 25 October 2026 | Carried out the lab steps: installed Maven and SonarQube for IDE, ran the SonarQube analysis, made the Task 3 fixes and the two design changes, and drafted this report | parcelpoint fixes (commits 684f4a7 to 402ef03), Size.java, LAB_REPORT.md | After every fix: rebuilt with `mvn package`, ran the C2 input, and re-ran SonarQube to see that the issue was gone. Checked the fees of all three sizes after the Size change. Checked every number and line in the report against the saved tool output and the code |
