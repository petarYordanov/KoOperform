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

## Test Plan With One Thread Group and Two Test Cases

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

## Test Plan With More Than One Thread Groups

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

