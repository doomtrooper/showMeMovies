package com.example.showmemovies

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

import kotlin.coroutines.EmptyCoroutineContext

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class CoroutineUnitTest {
    @Test
    fun addition_isCorrect() {
        runBlocking {
            val scope = CoroutineScope(EmptyCoroutineContext)
            scope.printCoroutineScopeInfo()
            val job = scope.launch {
                delay(100)
                println("coroutine done")
            }
            job.join()
            println("test done")
        }
    }
}

fun CoroutineScope.printCoroutineScopeInfo(){
    println()
    println("CoroutineScope: $this")
    println("CoroutineContext: ${this.coroutineContext}")
    println("Job: ${this.coroutineContext[Job]}")
    println()
}