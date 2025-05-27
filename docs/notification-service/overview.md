<h1 style="text-align: center">Notification microservice</h1>
<h2>Overview</h2>
<p>This microservice is responsible for real-time communication between microservices as well as 
notifications for different events like  post liked by user, user follow another user etc.<br>
WebSocket protocol is used for real-time interactions. Kafka is used for asynchronous communication 
between microservices which help achieving loose coupling, scalability, resiliency and more.<br>
Chat microservice utilize this microservice service along with WebSocket and Kafka so that
UI(ReactJS) can show chat messages in real-time. User microservice also use it to 
show when people<br> like posts, follow user and other activities.
</p>