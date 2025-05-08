<h1 style="text-align: center">Post service database</h1>
<h2>Overview</h2>
<p>As main data storage is used PostgreSQL RDMS. There is persisted information about user's posts<br>
and their likes. For communication with the database is used Spring Data JPA using Hibernate ORM.
<br>Redis NoSQL database is used for caching posts data to reduce the load to the main database. 

</p>
<h3>Entity Relationship Diagram</h3>
<img width="600" height="600" src="../../../assets/posts_service_er_diagram.png">