
    This program is simple maven project. For building it you have to have the maven installed

    To package project run the command line:

        mvn clean package


    To run the program:

        java -jar target\inchi-1.0-SNAPSHOT.jar


    Default value for N is 10. You can specify any positive number. I.e.

	    java -jar target\inchi-1.0-SNAPSHOT.jar 15


    To see full list of options use -h option

        java -jar target\inchi-1.0-SNAPSHOT.jar -h


	Another part of the README part should discuss the time complexity of the proposed solution.

        complexity is O(m) + O(n * (k..l)) + O(p)

        where:
            m - number of chars in dictionary. Dictionary index is built with 1 loop.
            n - number of compounds in inchi db file (i.e. number of lines)
            k - mininum steps to find the lengthiest word in disctionary
            l - maximum steps to find the lengthiest word in disctionary
            p - number of expected results. There is one final sort


    There should be an acceptance test, that proves that the actual time compexity is the same as discussed.
    The should be an explanation how to run the test and how to interpret the results.

        Such tests weren't implemented.




