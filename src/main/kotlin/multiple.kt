
import KooPerformTestCase.Companion.kooResponse
import KooPerformTestCase.Companion.url

fun main() {

    val jsonBody = "{\"name\" : \"Dzilio\", \"email\" : \"vv@vv.vv\", \"city\" : \"Pleven\"}"

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
}



