## Learning Spring Batch

This is the demo repository for the Learning Spring Batch videos hosted by O'Reilly.

#### Outline

Spring Batch Video Outline

01. Introduction
    0101 Agenda
        * Overview of batch processing
        * What is Spring Batch
        * Deep dive into implementing complex solutions with Spring Batch
        * Look at related Spring technologies
        	* Spring Batch Admin
        	* Spring for Apache Hadoop
        	* Spring XD
	0102 What is Spring Batch and who uses it?
		* We have a problem
			* Our manager has asked that we design a system to process millions of transactions
			each night.
			* It has a short time window overnight that it can run.
			* We'll be on call if it breaks.
			* And of course, we need to get it done by next week.
		* We need an architecture that can:
			* handle many types of input and output
			* efficiently
			* that can scale
			* that is built on battle tested components so we don't get paged!
		* What is batch processing
			* The original processing model
			* What is old is new again...
				* Data science model training
			* Definition
				* Processing of a finite amount of data without interaction or interruption
				* Finite amount of data
					* Contrast with a stream
				* Without interaction or interruption
					* Contrast with web app
			* Common batch uses cases
				* ETL
				* Reporting
				* Data Science
				* Big Data
				* Other offline processing
		* What is Spring Batch
			* The leading batch framework on the JVM
				* Basis for JSR-352
			* Developed as a partnership between Accenture and SpringSource
			* Released 1.0 in 2008
			* 3.0.4 is the current version
			* Provides the following functionality
				* Job flow state machine
				* Transaction handling
				* Declaritive I/O
				* Robust error handling
				* Extensive scalability options
				* Battle tested in just about every enterprise vertical
				* Built on Spring!
		* Who's used Spring Batch
			* Morgan Stanley
			* Cars.com
			* eHarmony
			* Orbitz
			* Federal Reserve Bank
			* Walgreens
			* MLB
			* NHL
			* HSBC
			* CBOE
	0103 Who is this for and what you'll get out of it
		* Who is this for?
			* Developers looking to learn how to develop batch processes
			* Developers who have background processes that they want to build robustly
	0104 Expectations/pre-reqs
		* Java
		* Spring
		* XML (although we will be focusing on Java Configuration as much as reasonably 
		possible)
		* A Github account and related Git knowledge are helpful but not required

02. Hello World
    0201 Project Setup
    	* Spring Initializer
        * start.spring.io
        * IntelliJ IDEA
	0202 Creating a simple job
		* Create simple Spring Boot based Spring Batch app
			* Hello world tasklet
	0203 Executing our job
		* Main class with Spring Boot
	0204 Reviewing the results
		* Configure MySql
		* Look at db 
		* JobInstance
		* JobExecution
		* StepExecution
		* JobLauncher
		* JobRepository
			* Schema
			* Review the data
			* Map Job Repository - Don't use it
	0205 The Domain of Batch
		* Job
		* Step
		* Tasklet
		* Reader/Processor/Writer
		* Chunk/Item

03. Job Flow
	0301 Transitions
		* Next
		* Fail
		* Stop
		* End
	0302 Flows
		* 
	0303 Splits
	0304 Decisions
	0305 Nested Jobs
	0306 Listeners
	0307 Job Parameters

04. Input
	0401 Interfaces
	0402 Reading from a database
	0403 Reading files
	0404 Reading XML files
	0405 Reading from multiple sources
	0408 State
		* ItemStream
			* ExecutionContext
			* open
			* update
			* close

05. Output
    0501 Interfaces (ItemWriter)
    0502 Writing to a database
    0503 Writing flat files
    0504 Writing XML files
    0505 Writing to multiple sources

06. Processing
    0601 ItemProcessor Interface
    0602 Filtering items
    0603 Validating items
    0604 CompositeItemProcessors

07. Error Handling
    0701 Restart
    0702 Retry
    0703 Skip
    0704 Listeners

08. Scaling jobs
	0801 Ways of scaling Spring Batch jobs
	0802 Multi-Threaded Step
	0803 AsyncItemProcessor/AsyncItemWriter
	0804 Partitioning
	0805 Remote Chunking

09. Job Orchestration
    0901 Starting a job
    0902 Stopping a job
    0903 Scheduling a job using Spring Schedule
    0904 Job Orchestration concerns

10.  Spring Batch With Spring Integration
    1001 Launching jobs via messages
    1002 Informational messages
    
11. Conclusion
    1101 What we learned
    1102 About the author

