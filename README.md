# Optimistic Locking
Instead of locking at the database level to prevent conflicts, an optimistic lock detects conflicts and the application must recover.

This is done by attempting an update on the version of the row that was fetched.  
For example:

1. `select * from table where id = 1; -- returns a row with version column of, say, 0`
2. `update table set column=value, version=1 where id=1 and version=0; -- set to version + 1`
3. An affected row count of 1 means there were no updates between the fetch and the update.
4. An affected row count of 0 means the row was updated since fetched. A re-fetch is required before updating.
5. `select * from table where id = 1; -- returns a row with version column of now 1`
6. `update table set column=value, version=2 where id=1 and version=1;`
7. The same check is repeated on affected row count.

Pros:
- Spans transactions.
- High throughput.

Cons:
- Poor performance if data contention is high (high concurrent writes).

### Hibernate
Hibernate has built-in support for optimistic locking.

1. Add a `version` column to your table.
2. Annotate the `Entity` column with `@Version`.
3. Catch `OptimisticLockingFailureException` when conflicts occur.
4. Re-fetch the row to get the latest state and try the update again.

### Database
Hibernate logging is turned on. Review the logs to see how the version column is being automatically added to update queries.

H2 is used and can be viewed at `localhost:8080/h2-console`.

The table `MY_TABLE` starts with the following row in `data.sql`:

| ID | VERSION | PRODUCT_NAME | QUANTITY |
|----|---------|--------------|----------|
| 1  | 0       | Keyboard     | 50       |

### Force an `OptimisticLockingFailureException` in the app
This exception is thrown when the optimistic lock detects a conflict.

1. Run the app in debug mode.
2. Add a break point in the Controller *after* the fetch statement is executed.
3. Make 2 requests that fetch the same state of the row.
   1. Open a browser tab and hit the endpoint: `http://localhost:8080`
   2. Open another browser tab and hit the same endpoint: `http://localhost:8080`
4. Resume both breakpoints.
5. The second will trigger the exception.
   1. The stack trace is not printed since a `RetryListener` is being used. I am logging a warn message.

### Metrics
1. Add `org.hibernate:hibernate-micrometer` dependency.
2. Add `spring.jpa.properties[hibernate.generate_statistics]=true` property.
3. Set `logging.level.org.hibernate.engine.internal=WARN` property to avoid excessive metrics logging.
4. Go to: `http://localhost:8080/actuator/metrics/hibernate.optimistic.failures`.

### `RetryListener`
A `RetryListener` can be used to log retry attempts via a bean.

### TODO
1. What is the best way to retry on conflict?
   1. Recursion? https://stackoverflow.com/a/12253130/807183
   2. Spring Retry? https://stackoverflow.com/a/45543257/807183
      1. I tried this one.
   3. AOP + `Ordered`? https://stackoverflow.com/a/62872042/807183
2. How to test?