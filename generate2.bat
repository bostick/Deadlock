@echo off
java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 5 2 %1 > transcript-5-2-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 6 0 %1 > transcript-6-0-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 6 1 %1 > transcript-6-1-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 7 0 %1 > transcript-7-0-%1.txt 2>&1
