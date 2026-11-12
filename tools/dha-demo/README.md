# DHA portfolio demo

The desktop edition preserves the supplied JavaFX screens and replaces MySQL and LAN synchronization with an H2 in-memory sample database. Original archive contents are kept in the ignored original/ directory. No live records, database credentials or database dumps are distributed.

## Rebuild

Requires JDK 21, H2 2.3.232, and the JavaFX 21 / JFoenix 8.0.5 / FontAwesomeFX 8.9 JARs from the supplied archive under original/target/libs/. The script accepts -Jdk and -H2Jar overrides.

From the repository root in PowerShell:

~~~powershell
& tools/dha-demo/build.ps1 -Package
~~~

Output: downloads/dha-inventory-demo-windows.zip. Extract the entire folder and run DHAInventoryDemo.exe. Login: demo / demo123. Close every demo window and restart to reset data.

## Verification

- DemoSmokeTest.java exercises real JDBC repositories: login, receive, issue, partial return, rejection and rollback of over-allocation/over-return, auction and user inventory.
- DemoRenderTest.java loads the JavaFX screens and forms, exercises store tabs, and renders the screenshots used on the project page. RenderLauncher.java starts the JavaFX test from a classpath launcher.
- web-demo.test.mjs tests browser inventory conservation, validation, separate categories, employees, limits and reset. Run with node --test tools/dha-demo/web-demo.test.mjs.

Compile the Java test files to build/test-classes with build/app-classes and build/input/* on the classpath. Run with those same entries plus build/test-classes on the classpath.

## Demo adaptations

- Only an in-memory H2 connection; production configuration removed and synchronization disabled.
- Fictional seed records and sample credentials.
- Derived balance triggers compatible with H2, with over-issue and over-return rejection.
- Employee IDs kept as strings through the user inventory model and UI.
- Dashboard loaded on entry and center panels anchored when resizing.
- Corrected temporary issue display and Pending.UserID queries.
- Dedicated return repository for the demo schema.

The browser edition in projects/dha-demo is a recreation of the primary workflows, not a Java runtime embedded in the browser. State lasts only for the current page session. It supports receipt, allocation, temporary return, three inventory views, employee creation, thresholds, search, date-filtered reports and CSV export.
