package com.example.showmemovies

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Test

import kotlin.coroutines.EmptyCoroutineContext

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class CoroutineUnitTest {
    @Test
    fun test1() {
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
    @Test
    fun test2(){
        runBlocking {
            println(Thread.currentThread().name)
            val firstCoroutine = withContext(Dispatchers.IO) {
                println("firstCoroutine: "+Thread.currentThread().name)
                delay(10000)
                "abc"
            }
            println("1")
            val secondCoroutine = withContext(Dispatchers.Default) {
                println("secondCoroutine: "+Thread.currentThread().name)
                "xyz"
            }
            println(firstCoroutine)
            println(secondCoroutine)
        }
    }

    @Test
    fun test3(){
        runBlocking {
            println(Thread.currentThread().name)
            val firstCoroutineDeferred = async(Dispatchers.IO) {
                println("firstCoroutine: "+Thread.currentThread().name)
                delay(10000)
                "abc"
            }
            println("1")
            val secondCoroutineDeferred = async(Dispatchers.Default) {
                println("secondCoroutine: "+Thread.currentThread().name)
                delay(1000)
                "xyz"
            }
            println(secondCoroutineDeferred.await())
            println(firstCoroutineDeferred.await())
        }
    }

    @Test
    fun test4(){
        runBlocking {
            println(Thread.currentThread().name)
            async(Dispatchers.IO) {
                println("firstCoroutine: "+Thread.currentThread().name)
                delay(10000)
                "abc"
            }
            println("1")
            async(Dispatchers.Default) {
                println("secondCoroutine: "+Thread.currentThread().name)
                delay(1000)
                "xyz"
            }
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