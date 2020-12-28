import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import com.sun.management.OperatingSystemMXBean
import java.lang.management.ManagementFactory
import javax.management.MBeanServerConnection

data class KooPerformTestPlan(var planName: String? = null)

data class KooPerformThreadGroup(
    var name: String? = null, var threadsCount: Int? = null,
    var startTestStepAfter: Long? = null, var startTestCaseAfter: Long? = null,
    val test: KooPerformTestCase? = null)

data class KooPerformTestCase(var name: String? = null, var address: KooPerformHttp? = null) {
    companion object {
        lateinit var url: String
        lateinit var client: OkHttpClient
        var kooResponse: MutableList<Response?> = mutableListOf(null)
    }
}

data class KooPerformHttp(val response: Response? = null)

fun testPlan(initializer: KooPerformTestPlan.() -> Unit): KooPerformTestPlan {
    return KooPerformTestPlan().apply(initializer)
}


fun testSteps(initializer: KooPerformHttp.() -> Unit): KooPerformHttp {
    try {
        KooPerformTestCase.kooResponse.removeAt(0)
    }catch (e: IndexOutOfBoundsException){
        e.message
    }
    return KooPerformHttp().apply(initializer)
}


fun threadGroup(initializer: KooPerformThreadGroup.() -> Unit): KooPerformThreadGroup {
    return KooPerformThreadGroup().apply(initializer)
}

fun testCase(initializer: KooPerformTestCase.() -> Unit): KooPerformTestCase {
    KooPerformTestCase.client = OkHttpClient()
    return KooPerformTestCase().apply(initializer)
}

fun KooPerformHttp.post(endpoint: String, body: String?, mediaType: String): Response {
    val requestPost = Request.Builder()
        .url(KooPerformTestCase.url + endpoint)
        .post(body!!.toRequestBody(mediaType.toMediaType()))
        .build()
    val responseCase = KooPerformTestCase.client!!.newCall(requestPost!!).execute()
    KooPerformTestCase.kooResponse.add(responseCase)
    return responseCase
}

fun KooPerformHttp.put(endpoint: String, body: String?, mediaType: String) : Response {
    val request = Request.Builder()
        .url(KooPerformTestCase.url + endpoint)
        .put(body!!.toRequestBody(mediaType.toMediaType()))
        .build()
    val responseCase = KooPerformTestCase.client!!.newCall(request!!).execute()
    KooPerformTestCase.kooResponse.add(responseCase)
    return responseCase
}

fun KooPerformHttp.get(endpoint: String) : Response {
    val request = Request.Builder()
        .url(KooPerformTestCase.url + endpoint)
        .build()
    val responseCase = KooPerformTestCase.client!!.newCall(request!!).execute()
    KooPerformTestCase.kooResponse.add(responseCase)
    return responseCase
}

fun KooPerformHttp.delete(endpoint: String): Response {
    val request = Request.Builder()
        .url(KooPerformTestCase.url + endpoint)
        .delete()
        .build()
    val responseCase = KooPerformTestCase.client!!.newCall(request!!).execute()
    KooPerformTestCase.kooResponse.add(responseCase)
    return responseCase
}

fun KooPerformTestCase.outputData(responseList: MutableList<Response?>) {
    val mbsc: MBeanServerConnection = ManagementFactory.getPlatformMBeanServer()

    val osMBean: OperatingSystemMXBean = ManagementFactory.newPlatformMXBeanProxy(
        mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean::class.java
    )
    println("----------------------------------------------------------------------------------")
    println("-----------------Output Data For Test Case: ${this.name}  -----------------------------------")
    for(response in responseList) {
        println("*******************************************************************************************")
        println("*******************************+  ${response?.request}  ****************************************************************")
        println("Initial Step : " + response?.request)
        println("Response Time In Milliseconds: " + (response?.receivedResponseAtMillis?.minus(response?.sentRequestAtMillis!!)))
        println("CPU Load In Bytes: " + osMBean.processCpuLoad)
        println("Committed Virtual Memory In Bytes: " + osMBean.committedVirtualMemorySize)
        println("Free Physical Memory Size In Bytes: " + osMBean.freePhysicalMemorySize)
        println("*******************************+*****************************************************************************************")
    }
    println("----------------------------------------------------------------------------------")

}

fun KooPerformThreadGroup.initiateLoad(block: () -> Unit) {
    val lambda =  Thread {
        Thread.sleep(startTestCaseAfter!!)
        for (i in 0 until threadsCount!!) {
                Thread.sleep(startTestStepAfter!!)
                block()
            }
        }
    lambda.start()
    lambda.join()
}



