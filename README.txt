Let CES_HOME refer to the directory where you unzipped the
contents of the CES-UIS installer zip.

Directory layout
----------------
CES_HOME: is the root of the installation
lib: contains libraries / jars
bin: contains any binaries
scripts: contains any shell scripts
war: contains any web applications

-----------------------
Usage
-----------------------
For validating dirty read of optimistic read lock

./batch-script.sh
------------------
For generating writes of optimistic read lock,
this will generate output files if the operations were performed sequentially

./write-batch-script.sh
------------------

For validating writes of optimistic read lock,
this will use generated output files above
and validate if the output remains same under concurrency

./write-validate-batch-script.sh

------------------

For validating dirty read of pessimistic read lock

./p-batch-script.sh
------------------
For generating writes of pessimistic read lock,
this will generate output files if the operations were performed sequentially

./p-write-batch-script.sh
------------------

For validating writes of pessimistic read lock,
this will use generated output files above
and validate if the output remains same under concurrency

./p-write-validate-batch-script.sh

------------------