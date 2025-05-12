<h1 style="text-align: center">User microservice </h1>

<h2>Overview</h2>
<p>This service is implemented following REST API architecture style using Spring Web.
PostgresSQL RDBMS is used for data persistence. Interaction with database and data manipulation is done via Spring Data using Hibernate ORM.
Redis NoSQL datastore is used as cache for users data so the main database is offloaded.
</p>

<h2>Functional requirements</h2>
<ul>
    <li>User can follow other users.</li>
    <li>User can search for other users.</li>
    <li>User can login with username and password.</li>
    <li>User can view and update their profile details.</li>
</ul>