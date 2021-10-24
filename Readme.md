# unum - Unique Number Generator

With distributed systems being developed as a part of the cloud age. With focus on performance associated with an application dependent on one DB instance, there is a need to bring down the dependency to a database to generate unique numbers for creating a private key when lots of data needs to be stored in the database tables. This library is intended to provide a way of generating unique numbers on the fly in a JAVA based distributed system.

This is not a new concept but this library helps in keeping you focused on the business logic that you need to develop for your applications rather than spending time on how this is done.

Typically applications powered by RDBMS can leverage the benefit as instant scalability might not be available for these setups when the load to the system spikes.

# Getting Started

add dependency to your project.


`
<dependency>
    <groupId>io.github.kgsnipes</groupId>
    <artifactId>unum</artifactId>
    <version>1.01</version>
</dependency>
`


Create a Number generator and start generating!


  `UniqueLongNumberGenerator generator=new UniqueLongNumberGeneratorImpl(1001,1,-1,1000);`

feel free to reach out with your feedbacks.
