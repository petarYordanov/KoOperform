import KooPerformTestCase.Companion.kooResponse
import KooPerformTestCase.Companion.url

fun main(){
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
}