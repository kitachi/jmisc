In the event play evolution is enabled, and play reports database state is 
inconsistent, the steps to take are:

1. shutdown all play servers pointing to the same data schema.

2. verify sufficient backup of data is in place from the applicable data schema.

3. disable evolution by setting the following in conf/application.conf
   file for the applicable play servers.

4. copy 1.sql to conf/evolutions/default dir in the applicable play servers.

5. run the reset_play_evolutions_table.sql in the applicable data schema.

6. startup the applicable play servers.

7. verify that no database state inconsistency is reported by any of the play servers any more, and all the required ddl exists in the applicable data schema.

8. if issue found in pt7, start data restore from the backup.

TODO: an alert should be sent when ever anyone modifies the application.conf file in any of the production play servers to ensure that play evolution will not be enabled.
