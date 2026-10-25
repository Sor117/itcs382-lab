# Lab Report, Code Quality and Design Quality

Repository URL: https://github.com/Sor117/itcs382-lab

## Tools

| Item | Version or name |
| --- | --- |
| JDK | OpenJDK 23.0.2 (Azul Zulu 23.32+11) |
| Maven | Apache Maven 3.10.0 |
| IDE | Visual Studio Code 1.142.0 (with Extension Pack for Java 0.31.2) |
| SonarQube for IDE | 6.2.0 (SonarSource), default rules, no connected mode |
| AI tool and exact model | Claude Code (Anthropic), model Claude Opus 5.5 (`claude-opus-5-5`) |

Note about SonarQube: I didn't copy the issues from the Problems panel by hand. I ran the language server that comes with the SonarQube for IDE 6.2.0 extension (`sonarlint-ls.jar` with `sonarjava.jar`) from a script. The script opens every Java file in a project and collects the same issues that the Problems panel shows, with the default rules. I left out the warnings from the Java extension, like `Resource leak: 'in' is never closed`.

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

Issue 5 is for the whole file, so SonarQube puts it at line 1, column 1.

When I ran the starter with `store P1 6501 2`, `pickup P1 5` and `staff`, a few things looked wrong. Pickup printed `Fee: 0.0`, but 5 days for a medium parcel should cost 30.0. The staff list still said `P1 6501 STORED` after the pickup and said there were 2 free lockers instead of 3. Also, parcels.csv was empty.

## Task 2. AI review of the starter code

| Item | Value |
| --- | --- |
| Date | 25 October 2026 |
| AI tool and exact model | Claude Code (Anthropic), model Claude Opus 5.5 (`claude-opus-5-5`) |
| Commit reviewed | 190e327 |
| How the AI read the files (attached the files, opened the folder, or pasted the files) | Opened the folder. I started a new Claude Code agent with no earlier context in the repository folder. It read the four Java files by itself and saved its full answer in the reviews folder. I used Prompt A as given and only replaced `<folder>` with `parcelpoint`. |
| Output file in the reviews folder | reviews/parcelpoint-review.md |

Table 2. All findings on the starter code

"AI n" means finding n in reviews/parcelpoint-review.md.

| No. | File:line | Finding | Found by (SonarQube, AI or both) | SonarQube rule or course term | Correct? (yes or no, and why) |
| --- | --- | --- | --- | --- | --- |
| 1 | ParcelService.java:42 | pickUp compares the codes with `==`, so it never finds the parcel | both | java:S4973 (AI 1) | Yes. The run printed `Fee: 0.0` and P1 stayed STORED. After I changed it to `equals()` it prints `Fee: 30.0`. |
| 2 | ParcelService.java:76 | The FileWriter is never closed, so parcels.csv stays empty | both | java:S2095 (AI 2) | Yes. parcels.csv was empty after the C2 run, and after the fix it has `P1,PICKED_UP`. |
| 3 | ParcelService.java:80 | Empty catch block hides a failed save | both | java:S108 (AI 3) | Yes, there is nothing inside the catch block. |
| 4 | ParcelService.java:6 | Unused import `java.util.Date` | both | java:S1128 (AI 8) | Yes, `Date` isn't used anywhere in the file. |
| 5 | ParcelService.java:12 | Unused field `maxParcels` | both | java:S1068 (AI 9) | Yes, nothing reads `maxParcels`, so the limit of 100 parcels is never checked. |
| 6 | ParcelService.java:1 | Uses the old `java.util.Date` API instead of `java.time` | SonarQube | java:S2143 | Partly. `Date` is only imported and never used, so the real problem is row 4. This issue went away after that fix. |
| 7 | Main.java:9, 15, 17, 22 and ParcelService.java:30, 67, 69 | Uses `System.out` instead of a logger | SonarQube | java:S106 | Not for this program. Printing is the program's output here, not logging. |
| 8 | ParcelService.java:49 | pickUp returns 0 for a code that doesn't exist, the same as a free pickup | AI | AI 4 | Yes, `pickup ZZ 9` printed `Fee: 0.0`. |
| 9 | Main.java:13 | The input isn't checked, so a short command or a non-number crashes the program | AI | AI 5 | Yes, `store P1` crashed with `ArrayIndexOutOfBoundsException` at Main.java:13. |
| 10 | Main.java:8 | The Scanner is never closed | AI | AI 6 | No. The Scanner reads `System.in` and the program ends right after the loop, so nothing really leaks. SonarQube's S2095 didn't report it either. |
| 11 | Main.java:12 | No final `else`, so an unknown command is ignored without any message | AI | AI 7 | Yes, typing `hello` printed nothing. |
| 12 | ParcelService.java:53 | Magic numbers: the 3 free days is typed four times, and 10.0, 15.0 and 20.0 have no names | AI | AI 10 | Yes, the 3 is repeated on lines 53, 57, 59 and 61. |
| 13 | ParcelService.java:52 | The fee is money but it uses `double` | AI | AI 11 | No, too general. Every fee here is a whole number (10, 15 or 20 times whole days), so `double` gives exact results anyway. |
| 14 | Parcel.java:4 | Fields that are only set once aren't `final` (Parcel.java:4-6, Locker.java:4-5, ParcelService.java:10-11) | AI | AI 12 | Yes, nothing changes these fields after the constructor. |
| 15 | Main.java:6 | main is long and complex (22 lines, cyclomatic complexity about 8) | AI | AI 13 | Partly. The numbers are right, but it's under SonarQube's limit of 15 for cognitive complexity, which is why SonarQube didn't say anything. |
| 16 | Main.java:6 | main has logical cohesion because the command word picks one of five actions | AI | Logical cohesion (AI 14) | Yes, it's like the getAttribute example from the lecture. The extra part about temporal start-up steps is a stretch though. |
| 17 | ParcelService.java:64 | printParcels has logical cohesion because `forStaff` picks one of two reports | AI | Logical cohesion (AI 15) | Yes, each branch prints a different list. |
| 18 | ParcelService.java:9 | ParcelService allocates lockers, keeps the register, holds the fee rule, sends the SMS, prints and saves the CSV | AI | Weak class cohesion (AI 16) | Yes, that's six jobs in one class. |
| 19 | ParcelService.java:52 | fee doesn't use any attribute of ParcelService | AI | Weak class cohesion (AI 17) | Yes, it only uses its two parameters. |
| 20 | Main.java:19, 21 | Main passes `true` or `false` to choose what printParcels does | AI | Control coupling (AI 18) | Yes, the flag picks the branch inside printParcels, same as Method C in Exercise 3. |
| 21 | ParcelService.java:28, 44 | ParcelService writes Parcel's `status` field directly | AI | Content coupling (AI 19) | Yes. The lecture says content coupling is when one component directly modifies another's data, and that's what happens here. |
| 22 | Main.java:14 | storeParcel returns the whole Locker, but Main only uses its number | AI | Stamp coupling (AI 20) | Yes, Main only calls `getNumber()`. |
| 23 | ParcelService.java:52 | fee gets the whole Parcel but only uses its size | AI | Stamp coupling (AI 21) | Yes, it only calls `getSize()`. |
| 24 | Main.java:22 | Main takes the internal locker list and calls `isFree()` on each Locker to count the free ones | AI | Stamp coupling, Law of Demeter (AI 22) | Yes, Main only needs Locker for this count. |
| 25 | ParcelService.java:41 | Gets the Parcel from a Locker and then calls `getCode()` on it | AI | Law of Demeter (AI 23) | Yes, but it's minor because the chain is just one step. |
| 26 | Locker.java:5 | The size is an int where 1 = small, 2 = medium and 3 = large, and Parcel, ParcelService and Main all have to read it the same way | AI | Connascence of convention (AI 24) | Yes. `store P2 6502 0` was accepted, and the fee rule charges any other number as large. |
| 27 | Parcel.java:13 | The status values "NEW", "STORED" and "PICKED_UP" are plain strings that two classes have to spell the same way | AI | Connascence of convention (AI 25) | Yes, "STORED" is typed in ParcelService on lines 28 and 68. |
| 28 | ParcelService.java:43 | `setParcel(null)` empties a locker only because Locker treats `null` as free | AI | Connascence of convention (AI 26) | Yes, Locker.java:30 checks `parcel == null`. |
| 29 | Main.java:15 | A `null` from storeParcel means no locker is free | AI | Connascence of convention (AI 27) | Yes, ParcelService.java:36 returns `null` in this case. |
| 30 | Parcel.java:9 | Two Strings next to each other in the Parcel constructor, and two ints in the Locker constructor | AI | Connascence of position (AI 28) | Yes, swapping them still compiles. For a constructor with three parameters it's a weak form though. |
| 31 | Parcel.java:7 | The field `status` is public | AI | Field that is not private (AI 29) | Yes. SonarQube's default rules didn't catch this one. |
| 32 | ParcelService.java:21 | getLockers returns the internal list, so callers can change it | AI | Internal collection returned (AI 30) | Yes, Main could call `clear()` on it. |

2.1 Name two problems that only the AI review found.

The first one is content coupling through the public field `status`. ParcelService sets `parcel.status = "STORED"` and `"PICKED_UP"` directly (ParcelService.java:28 and 44, Parcel.java:7). SonarQube's default rules didn't report the public field or the coupling. The second one is connascence of convention in the parcel size. The numbers 1, 2 and 3 mean small, medium and large, so Locker, Parcel, ParcelService.fee and Main all have to read them the same way (Locker.java:5, ParcelService.java:56-61). The AI also caught that pickUp returns 0 when the code doesn't exist (ParcelService.java:49). No SonarQube rule covers that kind of logic problem.

2.2 Name one problem that only SonarQube found, or write none.

java:S2143 at ParcelService.java:1, "Use the "java.time" API for date and time." SonarQube also gave java:S106 (`System.out` instead of a logger) in seven places and the AI didn't mention it. For a command-line program I don't think S106 is a real problem.

2.3 Name one AI finding that was wrong or too vague, and explain why. If every finding was right, explain how you checked them.

AI 6 (row 10 in Table 2) says "The Scanner is never closed". I think this is wrong for this program. The Scanner wraps `System.in`, and closing it would close `System.in` too. The program ends right after the loop anyway, so nothing leaks. SonarQube's S2095 reported the FileWriter but not this Scanner. AI 11, which says to use `BigDecimal` for money, is also too general, since every fee in ParcelPoint is a whole number of baht. To check the findings, I opened each file at the line the AI gave and compared the finding with the lecture definitions. For the claims about what happens when the program runs, I ran the starter again with `store P1`, `pickup ZZ 9`, `hello` and `store P2 6502 0`.

## Task 3. Fix and check

Table 3. What you did with each SonarQube issue in Table 1

| No. in Table 1 | Fixed or kept | Commit, or the reason for keeping it |
| --- | --- | --- |
| 1 | Kept | Main prints the command list, which is normal output for a command-line program, so a logger doesn't make sense here. |
| 2 | Kept | This prints the locker number after store, and the user needs to see it on the console. |
| 3 | Kept | This prints the fee after pickup, and the user needs to see it on the console. |
| 4 | Kept | This prints the number of free lockers for the staff command, which is output for the user. |
| 5 | Fixed | 7312205 (it was caused by the unused `Date` import, so it disappeared when I removed the import) |
| 6 | Fixed | 7312205 |
| 7 | Fixed | 48c951d |
| 8 | Kept | There is no real SMS gateway, so printing the SMS text on the console is how the program shows the message. |
| 9 | Fixed | 684f4a7 |
| 10 | Kept | This is the staff list that the user asks for with the staff command, so it's output and not a log message. |
| 11 | Kept | This is the waiting list that the user asks for with the list command, so it's output and not a log message. |
| 12 | Fixed | 343319c |
| 13 | Fixed | e25ebb8 |

Number of SonarQube issues that remain after your fixes: 7. All of them are java:S106 and I kept them for the reasons above. After the design changes they moved to Main.java:9, 15, 17 and 22 and ParcelService.java:31, 63 and 65. The new file Size.java has no issues.

The two design problems that you fixed

| Design problem (course term) | What you changed | Commit |
| --- | --- | --- |
| Connascence of convention (Table 2 row 26). The size was an int where 1 = small, 2 = medium and 3 = large, so Locker, Parcel, ParcelService.fee and Main all had to read the number the same way. | I added an enum `Size` (SMALL, MEDIUM, LARGE) that also holds the daily fee for each size. Locker and Parcel store a `Size` now. `fee()` uses `getDailyFee()` instead of the if-chain on 1, 2 and 3, `storeParcel` uses `fitsIn()`, and Main turns the typed number into a `Size` once with `Size.fromNumber()`. Now the classes only have to agree on the enum's name and type, and a bad size like 0 gets rejected. | 708a260 |
| Content coupling (Table 2 rows 21 and 31). ParcelService changed Parcel's public `status` field directly and compared the strings itself. | I made `status` private and added `markStored()`, `markPickedUp()`, `isStored()` and `getStatus()` to Parcel. ParcelService only uses these methods now, so the status strings only appear inside Parcel. | 402ef03 |

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
| Prompt (B or C as given, or your changed prompt in full) | Prompt B as given. I only replaced the placeholder line with the seven lines of the BikeShare specification (see the note under the table). | Prompt C as given. I only replaced the placeholder line with the seven lines of the BikeShare specification (see the note under the table). |
| Builds with mvn package (yes or no) | yes, first try, no fixes needed | yes, first try, no fixes needed |
| Number of classes, interfaces, enums and records | 1 class (BikeShare), 0 interfaces, 0 enums, 0 records | 10 in total: 5 classes (Main, ConsoleMenu, BikeShare, Station, CsvRideLog), 1 interface (RideLog), 1 enum (MemberType), 3 records (Bike, Rental, Ride) |
| SonarQube issues, total | 24 | 1 |
| SonarQube issues, by rule | java:S106 15, java:S2386 5, java:S1192 2, java:S3776 1, java:S3358 1 | java:S106 1 |
| AI review findings in Check 1 | 6 | 4 |
| AI review findings in Check 2 | 12 | 6 |
| Review output file in the reviews folder | reviews/bikeshare-poor-review.md | reviews/bikeshare-good-review.md |

Note on the prompts: I ran each prompt in a new Claude Code agent with no earlier context, and I didn't change the prompt text. The agent did get a few extra lines so that it would save its answer to a file and I could copy the files out: "Act as a chat assistant that has been given the prompt below. Do not read any existing file, and do not run builds or other commands. Write your complete answer, exactly as you would reply in a chat, as a Markdown file at `<path>`. That is the only file you may create. Before each code block, put the path of that file relative to the project folder on its own line, for example pom.xml or src/main/java/bikeshare/Main.java." I copied the files from the answers without changing them. The main class is `bikeshare.BikeShare` for bikeshare-poor and `bikeshare.Main` for bikeshare-good.

To check the two programs I did the same thing in both. I rented two bikes at Library and tried to rent a third one there, which was refused because no bike was left. Then I returned the bikes at Dorm and Canteen after 15 and 10 minutes and typed quit. Both printed a receipt with 30 baht for the student's 15 minutes and 30 baht for the staff member's 10 minutes, and both added the two rides to rides.csv. I also tested one claim from the bikeshare-good review. The member ID `a,b` gave the row `a,b,STUDENT,B1,5,10`, which has one more column than the header, so finding 1 in that review is correct.

Severity of the AI findings: bikeshare-poor had 3 Must fix, 10 Should fix and 5 Note. bikeshare-good had 1 Should fix and 9 Note.

For reference, the SonarQube issues in bikeshare-poor are S106 at BikeShare.java:24, 25, 33, 35, 50, 57, 61, 65, 76, 80, 81, 82, 83, 106 and 118, S2386 "Make this member "protected"." at lines 14, 15, 17, 18 and 19, S1192 for "Library" and "Canteen" at line 14, S3776 "Refactor this method to reduce its Cognitive Complexity from 26 to the 15 allowed." at line 42, and S3358 "Extract this nested ternary operation into an independent statement." at line 48. The only issue in bikeshare-good is S106 at Main.java:15, where `System.out` is passed to ConsoleMenu.

4.1 Did SonarQube report any cohesion or coupling problem in bikeshare-poor? Quote the rule and the message if it did.

No. None of the SonarQube rules talk about cohesion, coupling or connascence, but two of them come close. java:S2386 "Make this member "protected"." at BikeShare.java:14, 15, 17, 18 and 19 is about the public static arrays, and those arrays are the shared data that causes the common coupling. SonarQube treats it as a visibility problem though, and it missed the public static `Scanner input` on line 20. java:S3776 "Refactor this method to reduce its Cognitive Complexity from 26 to the 15 allowed." at line 42 is about `useBike`, but it doesn't mention the `renting` flag, which is the reason the method is so complex. SonarQube didn't report the god class, the int member codes or the control coupling at all.

4.2 Did the AI review tell the two versions apart? Give one finding for bikeshare-poor that does not appear for bikeshare-good.

Yes, very clearly. bikeshare-poor got 18 findings and 3 of them were Must fix. They covered logical cohesion, control coupling, common coupling, weak class cohesion, connascence of convention, position and algorithm, and fields that aren't private. bikeshare-good got 10 findings, and apart from one CSV bug they were all Notes. Its review found no control, common or content coupling, no weak class cohesion and no non-private fields. One finding for bikeshare-poor that bikeshare-good doesn't have is finding 9 (Must fix): "This is common coupling. `main`, `useBike` and `ask` share the global `public static` `input` ... and the fleet arrays" (BikeShare.java:17-20).

4.3 Which version would you rather extend with a new member type, such as a visitor who pays 4 baht per minute, and why?

I'd pick bikeshare-good. The rate is stored in the `MemberType` enum, so adding a visitor is just one new line, `VISITOR(4)`. After that `parse`, `feeFor` and the CSV row work without any change, and I'd only have to add the new word to the help text and the error message. In bikeshare-poor the int codes are spread over four places that all have to match: the new constant, the nested ternary on lines 47-48, `fee` on line 93 and the CSV label on line 85. `fee` charges the staff rate for every code that isn't 1, and line 85 writes every code that isn't 1 as "staff". So if I forgot one place, a visitor would pay 3 baht or get saved as staff, and nothing would show an error.

## Task 5. Reflection

5.1 What does SonarQube check, and what does it not check? Answer with examples from this lab.

SonarQube checks the code against a fixed set of rules, looking at one statement or method at a time. In this lab it found likely bugs like `==` on Strings (S4973) and a FileWriter that was never closed (S2095). It also found code smells such as an unused import (S1128), an unused field (S1068), an empty catch block (S108), repeated literals (S1192) and a method that is too complex (S3776). What it doesn't check is design. It said nothing about the public `status` field, the int size codes, the `forStaff` flag or ParcelService doing six jobs. In bikeshare-poor it also missed the god class, the int member codes and the boolean flag, and 15 of its 24 issues there were just S106.

5.2 When should you not trust the AI review? Give an example from this lab.

I wouldn't trust it when it gives general advice that doesn't really fit the code, or when it makes a claim that you can only prove by running the program. For ParcelPoint it said the Scanner on `System.in` has to be closed and that money should use `BigDecimal`, but neither matters in this program (Table 2 rows 10 and 13). It also called part of `main` "temporal", which the code doesn't really support. Its claim that no parcel is ever found was correct, but I only believed it after the run printed `Fee: 0.0`.

5.3 What will you do differently in Project Phase 2 after this lab?

I'll run SonarQube from the beginning and fix the real bugs straight away, and I'll only keep issues that I can explain in one sentence, like S106 in a console program. I'll also run Prompt A on our code, but I'll check every finding against the line it points to, the lecture definitions and a test run before it goes into the report. For the design, I want to use enums instead of int or String codes, keep all fields private, avoid boolean flag parameters and keep the menu separate from the rule classes, like bikeshare-good does.

## AI usage declaration

| AI tool and exact model | Purpose | Files or parts that it produced or changed | How you checked its output |
| --- | --- | --- | --- |
| Claude Code (Anthropic), Claude Opus 5.5 (`claude-opus-5-5`), 25 October 2026 | Task 2: reviewed the starter code with Prompt A in a new agent | reviews/parcelpoint-review.md | Opened each file at every line it gave, compared the course terms with the lecture definitions, and ran the starter again with `store P1`, `pickup ZZ 9`, `hello` and `store P2 6502 0` to test the runtime claims (last column of Table 2) |
| Claude Code (Anthropic), Claude Opus 5.5 (`claude-opus-5-5`), 25 October 2026 | Task 4: wrote the two designs from Prompt B and Prompt C, each in a new agent | all files in bikeshare-poor and bikeshare-good | Built both with `mvn package`, tried rentals and returns, checked the fees (15 minutes for a student and 10 minutes for a staff member both cost 30 baht) and the rows in rides.csv, and counted the classes |
| Claude Code (Anthropic), Claude Opus 5.5 (`claude-opus-5-5`), 25 October 2026 | Task 4: reviewed both designs with Prompt A, each in a new agent | reviews/bikeshare-poor-review.md, reviews/bikeshare-good-review.md | Compared the line numbers with the code, and ran the program to test the CSV comma claim in the bikeshare-good review |
| Claude Code (Anthropic), Claude Opus 5.5 (`claude-opus-5-5`), 25 October 2026 | Did the lab steps: installed Maven and SonarQube for IDE, ran the SonarQube analysis, made the Task 3 fixes and the two design changes, and wrote the draft of this report | parcelpoint fixes (commits 684f4a7 to 402ef03), Size.java, LAB_REPORT.md | After each fix I rebuilt with `mvn package`, ran the C2 input and ran SonarQube again to check that the issue was gone. Checked the fee for all three sizes after the Size change. Checked the numbers and line references in the report against the saved tool output and the code |
