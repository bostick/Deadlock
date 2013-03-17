@echo off
java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 0 4 %1 > transcript-0-4-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 0 5 %1 > transcript-0-5-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 0 6 %1 > transcript-0-6-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 0 7 %1 > transcript-0-7-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 1 3 %1 > transcript-1-3-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 1 4 %1 > transcript-1-4-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 1 5 %1 > transcript-1-5-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 1 6 %1 > transcript-1-6-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 2 2 %1 > transcript-2-2-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 2 3 %1 > transcript-2-3-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 2 4 %1 > transcript-2-4-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 2 5 %1 > transcript-2-5-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 3 1 %1 > transcript-3-1-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 3 2 %1 > transcript-3-2-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 3 3 %1 > transcript-3-3-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 3 4 %1 > transcript-3-4-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 4 0 %1 > transcript-4-0-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 4 1 %1 > transcript-4-1-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 4 2 %1 > transcript-4-2-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 4 3 %1 > transcript-4-3-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 5 0 %1 > transcript-5-0-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 5 1 %1 > transcript-5-1-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 5 2 %1 > transcript-5-2-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 6 0 %1 > transcript-6-0-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 6 1 %1 > transcript-6-1-%1.txt 2>&1

java -da -Xmx4096M -cp Deadlock\bin;DeadlockTests\bin;Deadlock\lib\trove-3.0.3.jar solver.Generator 7 0 %1 > transcript-7-0-%1.txt 2>&1
