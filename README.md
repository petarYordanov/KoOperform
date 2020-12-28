# KoOperform Kotlin Performance Testing Tool

**Version 1.0.0**

KoOperform is experimental tool fort REST API Load, Stress, Soak and Scalability Testing. The motivation behind it is to provide friendly DSL for 
creating Performance Test Plan with Thread Groups, Test Cases and Tests Steps within a few lines of code, where the DSL makes it possible to not be
familiar with Kotlin and still be able to use it to it's full potential. The base of the test steps api request is OkHttp client.

---

## Basic Test Plan 

Basic structure of the DSL contains testPlan, threadGroup, testCase and testStep.

```
testPlan {
        threadGroup {
            initiateLoad {
                testCase {
                    testSteps {
                    }
                }
                testCase {
                    testSteps {
                    }
                }
            }
        }
    }
```
---

## Test plan with one thread group and two test cases

---

In the case bellow, we see a Test Plan, that contains one thread group with two test cases under it, which means that the both test cases will be executed
simutaneously, based on the parameters defined in the thread group.

```
 testPlan {
        planName = " Single Thread"
        threadGroup {
            name = "Thread 1"
            threadsCount = 20
            startTestCaseAfter = 1500
            startTestStepAfter = 600
            initiateLoad {
                testCase {
                    val postBody = "{\"name\" : \"John\", \"email\" : \"vv@vv.vv\", \"city\" : \"Pleven\"}"
                    name = "Smoke Test "
                    url = "http://localhost:8080"
                    testSteps {
                        post("/employees", postBody, "application/json")
                        delete("/employees/2")
                        get("/employees")
                    }
                    outputData(kooResponse.toMutableList())
                }
                testCase {
                    val putBody = "{\"id\" : \"3\", \"name\" : \"James\", \"email\" : \"vv@vv.vv\", \"city\" : \"Pleven\"}"
                    name = "Extended Smoke Test "
                    url = "http://localhost:8080"
                    testSteps {
                        put("/employees", putBody, "application/json")
                        delete("/employees/2")
                        get("/employees")
                    }
                    outputData(kooResponse.toMutableList())
                }
            }
        }
    }

```

---

## Test plan with more than one thread groups

---

In the case bellow, there two thread groups containing two test cases each. The test cases in the secound thread group are executed after those in the first
are done.

```
    testPlan {
        planName = "Main Plan"
        threadGroup {
            name = "Thread Group 1"
            threadsCount = 3
            startTestCaseAfter = 1500
            startTestStepAfter = 600
            initiateLoad {
                testCase {
                    name = "Some "
                    url = "http://localhost:8080"
                    testSteps {
                        post("/employees", jsonBody, "application/json")
                        get("/employees")
                    }
                    outputData(kooResponse.toMutableList())
                }
                testCase {
                    name = "Some More"
                    url = "http://localhost:8080"
                    testSteps {
                        put("/employees", jsonBody, "application/json")
                        delete("/employees/1")
                    }
                    outputData(kooResponse.toMutableList())
                }
            }
        }
        threadGroup {
            name = "Thread Group 2"
            threadsCount = 5
            startTestCaseAfter = 1500
            startTestStepAfter = 200
            initiateLoad {
                testCase {
                    name = "Second "
                    url = "http://localhost:8080"
                    testSteps {
                        post("/employees", jsonBody, "application/json")
                        get("/employees")
                        delete("/employees/1")
                    }
                    outputData(kooResponse.toMutableList())
                }
                testCase {
                    name = "Second More"
                    url = "http://localhost:8080"
                    testSteps {
                        put("/employees", jsonBody, "application/json")
                        get("/employees")
                    }
                    outputData(kooResponse.toMutableList())
                }
            }
        }
    }
```
---

## Reports

---
As there is more work to be done on reportiong, there are some mandatory metrics for performance tests available now,
so when you execute your tests you will see logs in the console for every executed test step:

```
----------------------------------------------------------------------------------
-----------------Output Data For Test Case: Smoke Test   -----------------------------------
*******************************************************************************************
*******************************+  Request{method=POST, url=http://localhost:8080/employees}  ****************************************************************
Initial Step : Request{method=POST, url=http://localhost:8080/employees}
Response Time In Milliseconds: 10
CPU Load In Bytes: 0.0
Committed Virtual Memory In Bytes: 10436632576
Free Physical Memory Size In Bytes: 229306368
*******************************+*****************************************************************************************
*******************************************************************************************
*******************************+  Request{method=DELETE, url=http://localhost:8080/employees/2}  ****************************************************************
Initial Step : Request{method=DELETE, url=http://localhost:8080/employees/2}
Response Time In Milliseconds: 1
CPU Load In Bytes: 0.3790378006872852
Committed Virtual Memory In Bytes: 10436632576
Free Physical Memory Size In Bytes: 229306368
*******************************+*****************************************************************************************
*******************************************************************************************
*******************************+  Request{method=GET, url=http://localhost:8080/employees}  ****************************************************************
Initial Step : Request{method=GET, url=http://localhost:8080/employees}
Response Time In Milliseconds: 2
CPU Load In Bytes: 0.32983193277310924
Committed Virtual Memory In Bytes: 10436632576
Free Physical Memory Size In Bytes: 229306368
*******************************+*****************************************************************************************
----------------------------------------------------------------------------------
----------------------------------------------------------------------------------

```
---

## Soon to be improved

---

The methods in the test steps scope, based on OkHttp client are not yet able to carry headers and other http components, so that is soon to be improved.
Still those methods are returning Response OkHttp instance, so you may use any properties and methods for that objects as demonstrated in the snippet
bellow.

```
post("/employees", jsonBody, "application/json")
get("/employees")
delete("/employees/1")
                        
post("/employees", jsonBody, "application/json").message
get("/employees").code
delete("/employees/1").body.string()
                                             

```
---

## Application Under Test

---
Test were perrformed on a Ktor based REST API application:

```
package net.thetechstack

import DAOFacadeDatabase
import Employee
import io.ktor.application.*
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.jetbrains.exposed.sql.Database
import kotlin.text.get

val dao = DAOFacadeDatabase(Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver"))

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    embeddedServer(Netty, port = 8080){
        dao.init()
        install(CallLogging)
        install(ContentNegotiation){
            jackson {}
        }

        routing {
            route("/employees") { // matches the URL which ends with /employees
                routing {
                    route("/employees"){
                        get {
                            call.respond(dao.getAllEmployees())
                        }
                        post {
                            val emp = call.receive<Employee>()
                            dao.createEmployee(emp.name, emp.email, emp.city)
                            call.respond("Employee Created")
                        }
                        put {
                            val emp = call.receive<Employee>()
                            dao.updateEmployee(emp.id, emp.name, emp.email, emp.city)
                            call.respond("Employee Updated")
                        }
                        delete("/{id}") {
                            val id = call.parameters["id"]
                            if(id != null)
                                dao.deleteEmployee(id.toInt())
                            call.respond("Employee Deleted")
                        }
                    }
                }
            }
        }


    }.start(wait = true)
}


```
