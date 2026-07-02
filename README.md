# APITOR: An API Analytics Engine (Backend Core)

The core processing engine of **APITOR**—a high-performance, robust backend designed to act as an analytical layer for APIs. Instead of just dumping raw log files, APITOR intercept requests, tracks critical performance metrics in real-time, and aggregates data to help developers isolate infrastructure bottlenecks instantly.

* **Frontend Dashboard Repository:** [🔗 Click here to view the Frontend Repository](https://github.com/alokprajapat386/APITOR-Frontend)
  
## 🚀 Key Analytical Metrics Tracked

The engine intercepts, extracts, and aggregates data across the following dimensions:
* **Traffic Volume:** Total Request Hits over configurable time windows.
* **User Reach:** Unique IP address tracking per endpoint.
* **Protocol Insights:** Breakdown of HTTP Methods (`GET`, `POST`, `PUT`, `DELETE`, etc.) utilized.
* **System Health:** Distribution of Response Status Codes (`2xx`, `4xx`, `5xx`).
* **Performance Profiling:** Deep route-based latency checks and hit analysis to locate slow database queries or blocking middleware.


## ⚙️ Core Architecture & Flow

The backend logic is structured into a clean, secure pipeline to manage configurations, endpoints, and analytic ingestion:

1.  An isolated configuration management for environment instead of hardcoing the vlues.
2.  Seperated the users and project management from metrics provider and tracker to make them maintainable.
3.  Secured the application with stateless jwt and cryptographic authenticaiton- JWtAuthFilter for normal User logins and TrackerFilter for metrics ingestion.
4.  Declared immutable DTOs for error-less data retrieval and mapping from HTTP requests.
5.  Exception handling with a Gloabl Exception Handler and using standard Exceptions to ensure standard http status codes in response.
6.  Complete user isolation so, no one can see any details of any other user.


## 🛠️ Tech Stack

* **Language/Framework:** Java / Spring Boot 
* **Database:** PostgreSQL (Optimized for time-series aggregation query patterns)
* **Security:** Spring Security & JWT Architecture
* **Communication Protocols:** RESTful APIs with strict CORS filters for seamless frontend handshake

Only this for now but I will try to improve it in future
